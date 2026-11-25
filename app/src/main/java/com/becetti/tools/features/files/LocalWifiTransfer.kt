package com.becetti.tools.features.files

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.Inet4Address
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val PROTOCOL_MAGIC = 0x42435431 // BCT1
private const val CHUNK_SIZE = 64 * 1024
private const val MAX_RECORD_SIZE = CHUNK_SIZE + 16
private const val MAX_FILE_SIZE = 512L * 1024L * 1024L
private const val SOCKET_TIMEOUT_MS = 120_000

const val WIFI_PAIRING_RESULT_KEY = "becetti_wifi_pairing_result"

/** A short-lived pairing secret is carried in the QR and used for AES-GCM authenticated encryption. */
data class TransferPairing(val host: String, val port: Int, val secret: ByteArray) {
    fun encode(): String = listOf(
        "BCT1",
        host,
        port.toString(),
        Base64.encodeToString(secret, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    ).joinToString("|")

    companion object {
        fun create(context: Context, port: Int): TransferPairing {
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = manager.activeNetwork
                ?: throw IllegalStateException("No active Wi-Fi network")
            val capabilities = manager.getNetworkCapabilities(network)
            if (capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) != true) {
                throw IllegalStateException("Connect to Wi-Fi first")
            }
            val address = manager.getLinkProperties(network)?.linkAddresses
                ?.mapNotNull { it.address as? Inet4Address }
                ?.firstOrNull { it.isSiteLocalAddress && !it.isLoopbackAddress }
                ?: throw IllegalStateException("No local Wi-Fi IPv4 address")
            val key = ByteArray(32).also(SecureRandom()::nextBytes)
            return TransferPairing(address.hostAddress ?: throw IllegalStateException("No Wi-Fi address"), port, key)
        }

        fun parse(payload: String): TransferPairing? = runCatching {
            val fields = payload.trim().split('|')
            require(fields.size == 4 && fields[0] == "BCT1")
            val octets = fields[1].split('.').map { it.toInt().also { part -> require(part in 0..255) } }
            require(octets.size == 4)
            val address = InetAddress.getByAddress(ByteArray(4) { octets[it].toByte() }) as? Inet4Address
                ?: error("IPv4 required")
            require(address.isSiteLocalAddress && !address.isLoopbackAddress)
            val port = fields[2].toInt().also { require(it in 1..65535) }
            val secret = Base64.decode(fields[3], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            require(secret.size == 32)
            TransferPairing(address.hostAddress ?: error("Invalid address"), port, secret)
        }.getOrNull()
    }
}

class WifiTransferServer(context: Context) : AutoCloseable {
    private val receiveDirectory = File(context.filesDir, "wifi_received").apply { mkdirs() }
    private val serverSocket: ServerSocket
    private val pairing: TransferPairing
    @Volatile private var activeSocket: Socket? = null

    init {
        val addressAndKey = TransferPairing.create(context, 1)
        serverSocket = ServerSocket(0, 8, InetAddress.getByName(addressAndKey.host))
        pairing = addressAndKey.copy(port = serverSocket.localPort)
    }

    val pairingCode: String get() = pairing.encode()
    val receivedFiles: List<File>
        get() = receiveDirectory.listFiles()?.filter { it.isFile && !it.name.endsWith(".part") }
            ?.sortedByDescending { it.lastModified() }.orEmpty()

    suspend fun listen(onReceived: suspend (File) -> Unit, onFailure: suspend (String) -> Unit) =
        withContext(Dispatchers.IO) {
            while (!serverSocket.isClosed) {
                try {
                    val socket = serverSocket.accept()
                    activeSocket = socket
                    socket.use {
                        it.soTimeout = SOCKET_TIMEOUT_MS
                        val file = receiveOne(it, pairing.secret, receiveDirectory)
                        withContext(Dispatchers.Main.immediate) { onReceived(file) }
                    }
                } catch (_: SocketException) {
                    if (!serverSocket.isClosed) {
                        withContext(Dispatchers.Main.immediate) { onFailure("Connection closed") }
                    }
                } catch (error: Throwable) {
                    if (!serverSocket.isClosed) {
                        withContext(Dispatchers.Main.immediate) {
                            onFailure(error.message ?: "Transfer failed")
                        }
                    }
                } finally {
                    activeSocket = null
                }
            }
        }

    override fun close() {
        runCatching { activeSocket?.close() }
        runCatching { serverSocket.close() }
        pairing.secret.fill(0)
    }
}

fun sendSelectedFile(context: Context, uri: Uri, pairing: TransferPairing) {
    val resolver = context.contentResolver
    val displayName = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }
        ?.take(160)
        ?.ifBlank { null }
        ?: "shared-file"
    val mimeType = resolver.getType(uri)?.take(160) ?: "application/octet-stream"

    Socket().use { socket ->
        socket.connect(InetAddress.getByName(pairing.host).let { java.net.InetSocketAddress(it, pairing.port) }, 15_000)
        socket.soTimeout = SOCKET_TIMEOUT_MS
        val output = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
        output.writeInt(PROTOCOL_MAGIC)
        var sequence = 0L
        writeRecord(output, pairing.secret, sequence++, encodeMetadata(displayName, mimeType))

        var total = 0L
        resolver.openInputStream(uri)?.use { input ->
            val buffer = ByteArray(CHUNK_SIZE)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                if (read == 0) continue
                total += read
                require(total <= MAX_FILE_SIZE) { "File is larger than the 512 MiB transfer limit" }
                writeRecord(output, pairing.secret, sequence++, buffer.copyOf(read))
            }
        } ?: throw IllegalStateException("Unable to read the selected file")

        writeRecord(output, pairing.secret, sequence, ByteArray(0)) // authenticated end marker
        output.flush()
        socket.shutdownOutput()

        val acknowledgement = DataInputStream(BufferedInputStream(socket.getInputStream()))
        val response = decryptRecord(acknowledgement, pairing.secret, sequence + 1L)
        require(response?.contentEquals("OK".toByteArray(Charsets.UTF_8)) == true) {
            "Receiver did not confirm the saved file"
        }
    }
}

private fun receiveOne(socket: Socket, secret: ByteArray, directory: File): File {
    val input = DataInputStream(BufferedInputStream(socket.getInputStream()))
    require(input.readInt() == PROTOCOL_MAGIC) { "Not a BECETTI transfer" }
    var sequence = 0L
    val metadata = decryptRecord(input, secret, sequence++) ?: error("Missing file metadata")
    val (fileName, mimeType) = decodeMetadata(metadata)
    require(mimeType.length <= 160)

    val target = uniqueFile(directory, sanitizeFileName(fileName))
    val partial = File(directory, ".${target.name}.${System.nanoTime()}.part")
    var total = 0L
    try {
        FileOutputStream(partial).use { rawOutput ->
            val output = BufferedOutputStream(rawOutput)
            while (true) {
                val record = decryptRecord(input, secret, sequence++) ?: error("Incomplete transfer")
                if (record.isEmpty()) break
                total += record.size
                require(total <= MAX_FILE_SIZE) { "File is larger than the 512 MiB transfer limit" }
                output.write(record)
                record.fill(0)
            }
            output.flush()
            rawOutput.fd.sync()
        }
        require(input.read() == -1) { "Unexpected data after transfer end" }
        check(partial.renameTo(target)) { "Unable to save received file" }
        val acknowledgement = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
        writeRecord(acknowledgement, secret, sequence, "OK".toByteArray(Charsets.UTF_8))
        acknowledgement.flush()
        return target
    } catch (error: Throwable) {
        partial.delete()
        throw error
    }
}

private fun writeRecord(output: DataOutputStream, secret: ByteArray, sequence: Long, plaintext: ByteArray) {
    require(plaintext.size <= CHUNK_SIZE || sequence == 0L)
    val nonce = ByteArray(12).also(SecureRandom()::nextBytes)
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(secret, "AES"), GCMParameterSpec(128, nonce))
    cipher.updateAAD(ByteBuffer.allocate(12).putInt(PROTOCOL_MAGIC).putLong(sequence).array())
    val ciphertext = cipher.doFinal(plaintext)
    output.writeInt(ciphertext.size)
    output.write(nonce)
    output.write(ciphertext)
}

private fun decryptRecord(input: DataInputStream, secret: ByteArray, sequence: Long): ByteArray? {
    val ciphertextSize = try {
        input.readInt()
    } catch (error: java.io.EOFException) {
        return null
    }
    require(ciphertextSize in 16..MAX_RECORD_SIZE) { "Invalid transfer record" }
    val nonce = ByteArray(12)
    input.readFully(nonce)
    val ciphertext = ByteArray(ciphertextSize)
    input.readFully(ciphertext)
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(secret, "AES"), GCMParameterSpec(128, nonce))
    cipher.updateAAD(ByteBuffer.allocate(12).putInt(PROTOCOL_MAGIC).putLong(sequence).array())
    return cipher.doFinal(ciphertext)
}

private fun encodeMetadata(name: String, mimeType: String): ByteArray = ByteArrayOutputStream().use { bytes ->
    DataOutputStream(bytes).use { output ->
        output.writeUTF(name.take(160))
        output.writeUTF(mimeType.take(160))
    }
    bytes.toByteArray()
}

private fun decodeMetadata(bytes: ByteArray): Pair<String, String> =
    DataInputStream(ByteArrayInputStream(bytes)).use { input ->
        val name = input.readUTF()
        val type = input.readUTF()
        require(input.read() == -1) { "Invalid file metadata" }
        name to type
    }

private fun sanitizeFileName(value: String): String {
    val cleaned = value.replace('/', '_').replace('\\', '_').replace('\u0000', '_')
        .trim().take(120).takeUnless { it.isBlank() || it == "." || it == ".." }
        ?: "received-file"
    return cleaned
}

private fun uniqueFile(directory: File, name: String): File {
    val base = name.substringBeforeLast('.', name)
    val extension = name.substringAfterLast('.', "").takeIf { it != name }.orEmpty()
    var candidate = File(directory, name)
    var index = 1
    while (candidate.exists()) {
        val suffix = if (extension.isEmpty()) "" else ".$extension"
        candidate = File(directory, "$base ($index)$suffix")
        index++
    }
    return candidate
}
