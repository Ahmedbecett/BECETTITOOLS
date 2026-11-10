# Files and local Wi-Fi transfer

- Send only files the user selects with Android's document picker. Receive into the app's private `files/wifi_received` directory; the user chooses whether to export or delete each received file.
- Both devices must be connected to the same Wi-Fi network. The receiver binds to its active Wi-Fi IPv4 address and displays a QR pairing code containing a temporary random 256-bit key.
- Transfer records and metadata are encrypted/authenticated with AES-GCM. The protocol uses unique random nonces, ordered record authentication, a final authenticated marker, and a receiver acknowledgement. Transfers are limited to 512 MiB per file; interruptions or invalid authentication discard the partial file.
- The pairing code is a bearer secret: share it only with the intended device and stop the receiver after use. This implementation is intended for a trusted local network, not internet exposure.
- APKs are transferred as ordinary files and are never installed automatically. Installation, if desired, remains a separate user/system action.
- The temporary-file cleanup action deletes only this app's cache directory; it cannot and does not attempt to remove files belonging to other apps or the Android system.
- Virus scanning is deferred. Android's application sandbox prevents this app from scanning or cleaning other apps' private data. No antivirus engine or malware-detection claim is included.
