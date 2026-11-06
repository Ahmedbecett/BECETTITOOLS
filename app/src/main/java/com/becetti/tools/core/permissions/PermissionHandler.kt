package com.becetti.tools.core.permissions
// Permission handling uses ActivityResultContracts.RequestPermission in each screen requiring it (e.g., QrScanner).
// This avoids unnecessary permissions and follows spec §6 & §10.
object PermissionHandler {
    const val CAMERA_RATIONALE = "Camera access is needed for QR scanning and is requested only when you open the scanner."
}
