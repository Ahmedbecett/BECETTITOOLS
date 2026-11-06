import "../globals.css";
export const metadata = {
  title: "BECETTI TOOLS Privacy Policy — com.becetti.tools v1.0.0",
  description: "Privacy Policy for BECETTI TOOLS. Offline-first, no account, no tracking. Camera only when you open QR Scanner. Passwords never uploaded.",
  alternates: { canonical: "https://becetti-tools.vercel.app/privacy" },
};
export default function PrivacyPage() {
  return (
    <div style={{ maxWidth: 820, margin: "40px auto", padding: "0 20px", lineHeight: 1.7 }}>
      <a href="/becetti-tools" style={{ fontWeight: 700, color: "#F47B20" }}>← Back to BECETTI TOOLS</a>
      <h1 style={{ marginTop: 14 }}>Privacy Policy — BECETTI TOOLS (com.becetti.tools) v1.0.0</h1>
      <p style={{ color: "#667085" }}>Effective: 2026-11-05 • Last updated: 2026-11-06 • Contact: ahmedbecetti35@gmail.com</p>

      <h2>Summary</h2>
      <p>BECETTI TOOLS is offline-first, no account required. We minimize data collection. Most processing is on-device.</p>

      <h2>Data We Collect</h2>
      <ul>
        <li><b>Local only</b> (on-device Room/DataStore): notes, favorites, recent tools, calculation history, language/theme, cached currency rates/date, ad consent flag. You can clear history in History/Settings.</li>
        <li><b>No plaintext passwords stored.</b> Generator/strength run locally; passwords never uploaded.</li>
        <li><b>Camera</b> (QR Scanner only, when you open it): frames processed on-device via ML Kit; not stored.</li>
        <li><b>Files</b>: images/PDFs you select via SAF are processed locally (PdfDocument/PDFBox/ML Kit). Originals preserved; outputs saved to Documents/Pictures/Becetti.</li>
        <li><b>Network</b>: currency rates from api.frankfurter.app over HTTPS; no personal data sent except base currency. Advertising SDKs (if enabled) may collect per their policies — disclosed below.</li>
      </ul>

      <h2>Advertising</h2>
      <ul>
        <li><b>Monetag</b>: initialized only with official SDK + your publisher App ID. Test mode in dev. No ads if SDK unavailable.</li>
        <li><b>Adsterra SmartLink</b>: optional “Sponsored Offers” section; tapped explicitly; opens external browser; no auto-redirect; no web-script injection into native activity.</li>
        <li>Frequency caps, no ads on calculator, no ads over controls, consent-aware.</li>
      </ul>

      <h2>Permissions</h2>
      <ul>
        <li>Camera: QR scanning, requested only when opening scanner (rationale provided).</li>
        <li>Media/Files: image/PDF selection via SAF; processed locally.</li>
        <li>Internet/NetworkState: currency + ads.</li>
      </ul>

      <h2>Your Controls</h2>
      <p>Clear history, choose language/theme, grant/revoke ad consent in Settings. Uninstall clears local data. android:allowBackup=true (disable if policy requires).</p>

      <h2>Children / Sensitive</h2>
      <p>No knowing collection from children; no unnecessary identifiers; never expose secrets in APK.</p>

      <h2>Contact & Requests</h2>
      <p>Email <a href="mailto:ahmedbecetti35@gmail.com">ahmedbecetti35@gmail.com</a> for access/deletion inquiries. As offline-first app, most data lives only on your device.</p>

      <p style={{ marginTop: 30, color: "#667085", fontSize: 13 }}>This policy is public at /privacy and linked from the landing footer & app Settings. No data sold. No fake counters.</p>
    </div>
  );
}
