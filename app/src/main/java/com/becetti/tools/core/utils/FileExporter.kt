package com.becetti.tools.core.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * File + sharing plumbing shared by every tool.
 *
 * Saving is scoped-storage aware: on Android 10+ files land in the public Documents/Downloads/Pictures
 * collections through MediaStore (no storage permission needed); on Android 7–9 they go to the app's
 * external files directory with the same folder name so the user can still reach them from their
 * file browser. Nothing is ever written outside these two locations.
 */
object FileExporter {

    private fun subDirFor(mime: String): String = when {
        mime.startsWith("image/") -> Environment.DIRECTORY_PICTURES + "/Becetti"
        mime.startsWith("audio/") -> Environment.DIRECTORY_MUSIC + "/Becetti"
        mime.startsWith("video/") -> Environment.DIRECTORY_MOVIES + "/Becetti"
        mime == "application/pdf" || mime.startsWith("text/") -> Environment.DIRECTORY_DOCUMENTS + "/Becetti"
        else -> Environment.DIRECTORY_DOWNLOADS + "/Becetti"
    }

    /** Writes bytes and returns a human-readable location (or throws with a real message). */
    fun saveBytes(ctx: Context, fileName: String, mime: String, bytes: ByteArray): String {
        val safeName = fileName.replace(Regex("[\\\\/:*?\"<>|]"), "_")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val collection = when {
                mime.startsWith("image/") -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                mime.startsWith("audio/") -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                mime.startsWith("video/") -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                else -> MediaStore.Files.getContentUri("external")
            }
            val values = android.content.ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, safeName)
                put(MediaStore.MediaColumns.MIME_TYPE, mime)
                put(MediaStore.MediaColumns.RELATIVE_PATH, subDirFor(mime))
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
            val uri = ctx.contentResolver.insert(collection, values)
                ?: throw IllegalStateException("Could not create a MediaStore entry")
            ctx.contentResolver.openOutputStream(uri)?.use { it.write(bytes) }
                ?: throw IllegalStateException("Could not open the output stream")
            values.clear()
            values.put(MediaStore.MediaColumns.IS_PENDING, 0)
            ctx.contentResolver.update(uri, values, null, null)
            return "${subDirFor(mime)}/$safeName"
        }
        val dir = File(ctx.getExternalFilesDir(null), subDirFor(mime)).apply { mkdirs() }
        val out = File(dir, safeName)
        out.writeBytes(bytes)
        return out.absolutePath
    }

    fun saveText(ctx: Context, fileName: String, mime: String, text: String): String =
        saveBytes(ctx, fileName, mime, text.toByteArray(Charsets.UTF_8))

    fun copyToClipboard(ctx: Context, text: String, label: String = "BECETTI TOOLS") {
        val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
        cm.setPrimaryClip(ClipData.newPlainText(label, text))
    }

    fun shareText(ctx: Context, text: String, subject: String = "BECETTI TOOLS") {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        ctx.startActivity(Intent.createChooser(send, subject).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Shares a previously exported file through the app's FileProvider. */
    fun shareExported(ctx: Context, path: String, mime: String = "application/octet-stream") {
        val file = File(path)
        if (!file.exists()) return
        val uri: Uri = runCatching {
            FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", file)
        }.getOrElse { return }
        val send = Intent(Intent.ACTION_SEND).apply {
            type = mime
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        ctx.startActivity(Intent.createChooser(send, file.name).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun openFile(ctx: Context, path: String, mime: String) {
        val file = File(path)
        if (!file.exists()) return
        val uri = runCatching {
            FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", file)
        }.getOrNull() ?: return
        val view = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mime)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { ctx.startActivity(view) }
    }

    fun readBytes(ctx: Context, uri: Uri): ByteArray? = runCatching {
        ctx.contentResolver.openInputStream(uri)?.use { it.readBytes() }
    }.getOrNull()

    fun readText(ctx: Context, uri: Uri): String? = runCatching {
        ctx.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }
    }.getOrNull()

    fun displayName(ctx: Context, uri: Uri): String {
        val fromProvider = runCatching {
            ctx.contentResolver.query(uri, null, null, null, null)?.use { c ->
                val idx = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (idx >= 0 && c.moveToFirst()) c.getString(idx) else null
            }
        }.getOrNull()
        return fromProvider
            ?: uri.lastPathSegment?.substringAfterLast('/')
            ?: "file"
    }

    fun sizeOf(ctx: Context, uri: Uri): Long = runCatching {
        ctx.contentResolver.query(uri, null, null, null, null)?.use { c ->
            val idx = c.getColumnIndex(android.provider.OpenableColumns.SIZE)
            if (idx >= 0 && c.moveToFirst()) c.getLong(idx) else 0L
        }
    }.getOrNull() ?: 0L

    fun mimeFor(name: String): String =
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(name.substringAfterLast('.', "").lowercase())
            ?: "application/octet-stream"

    /** Human readable byte size (KiB/MiB/GiB, binary units). */
    fun humanSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val units = arrayOf("KB", "MB", "GB", "TB")
        var value = bytes.toDouble() / 1024.0
        var i = 0
        while (value >= 1024.0 && i < units.size - 1) {
            value /= 1024.0
            i++
        }
        return String.format(Locale.US, if (value >= 100) "%.0f %s" else "%.1f %s", value, units[i])
    }

    fun humanDate(millis: Long, withTime: Boolean = true): String {
        val pattern = if (withTime) "dd MMM yyyy · HH:mm" else "dd MMM yyyy"
        return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(millis))
    }

    fun humanShortDate(millis: Long): String =
        DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(millis))

    /** Deletes app-internal caches/temp exports older than [days]. */
    fun clearTempFiles(ctx: Context): Long {
        var freed = 0L
        runCatching { ctx.cacheDir.listFiles()?.forEach { freed += it.sizeSafely(); it.deleteRecursively() } }
        val tmp = File(ctx.filesDir, "exports")
        runCatching { tmp.listFiles()?.forEach { freed += it.sizeSafely(); it.deleteRecursively() } }
        return freed
    }

    private fun File.sizeSafely(): Long = runCatching {
        if (isDirectory) walkBottomUp().filter { it.isFile }.sumOf { it.length() } else length()
    }.getOrDefault(0L)
}
