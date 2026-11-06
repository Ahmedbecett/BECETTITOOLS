/**
 * Landing-page copy for the three published locales: English (default), French, Arabic.
 * No marketing claims that cannot be verified from the repository or the APK:
 * no download counters, no star ratings, no user numbers.
 */

const en = {
  dir: "ltr",
  htmlLang: "en",
  locale: "en_US",
  route: { landing: "becetti-tools/", privacy: "privacy/" },
  meta: {
    title: "BECETTI TOOLS — Free Android Utility App (Calculator, QR, PDF, OCR)",
    description:
      "BECETTI TOOLS v1.0.0: 15 free Android tools — calculator, unit and currency converters, QR scanner, image to PDF, PDF merger, OCR and notes. Offline-first, no account.",
    keywords: [
      "BECETTI TOOLS",
      "Free Android Utility App",
      "Android Calculator and Converter",
      "QR Code Generator and Scanner",
      "Image to PDF Android",
      "PDF Merger Android",
      "Free Android Tools",
      "open source Android toolbox",
      "offline Android tools",
      "OCR Android app",
    ],
    ogImageAlt: "BECETTI TOOLS — Everything you need. One smart toolbox.",
  },
  nav: {
    label: "Main navigation",
    tools: "Tools",
    previews: "Preview",
    install: "Install",
    verify: "Verify",
    privacy: "Privacy",
    faq: "FAQ",
    roadmap: "Roadmap",
    contact: "Contact",
    download: "Download APK",
    language: "Language",
    theme: "Theme",
    themeLight: "Light",
    themeDark: "Dark",
    themeSystem: "System",
    skip: "Skip to main content",
  },
  hero: {
    badge: "v1.0.0 · Native Android · Free · No account",
    title: "BECETTI TOOLS",
    tagline: "Everything you need. One smart toolbox.",
    lead:
      "Fifteen focused tools for everyday work — calculations, conversions, QR codes, PDFs, images and text — built natively with Kotlin and Jetpack Compose. No account, no subscription, and the core tools keep working with the network switched off.",
    ctaPrimary: "Download APK",
    ctaSecondary: "Installation guide",
    ctaTertiary: "View source on GitHub",
    sizeMeta: "88.9 MB",
    facts: [
      { value: "15", label: "tools in v1.0.0" },
      { value: "88.9 MB", label: "debug APK size (84.8 MiB)" },
      { value: "Android 7.0+", label: "minSdk 24" },
      { value: "AR · FR · EN", label: "interface languages" },
    ],
    buildNoticeTitle: "This APK is a debug build",
    buildNotice:
      "It is debuggable and signed with a debug key, not with a production/Play signing key. Install it to evaluate the app; it is not a store-ready release. The exact file published here is the one committed in the repository.",
  },
  trust: {
    title: "What this app does with your data",
    items: [
      {
        title: "No account, no tracking",
        text: "There is no sign-in, no analytics SDK and no advertising identifier in v1.0.0. Notes, favourites and history stay in a local database.",
      },
      {
        title: "On-device processing",
        text: "QR decoding, OCR, PDF creation and password generation all run on the phone. Passwords are never stored or uploaded.",
      },
      {
        title: "Permissions on demand",
        text: "Camera is requested only when you open the QR scanner. Files are chosen through the Android system picker, not by scanning your storage.",
      },
      {
        title: "Open source",
        text: "The full Kotlin source, including every tool listed below, is public under the MIT licence. You can read, rebuild and audit it.",
      },
    ],
  },
  toolsSection: {
    id: "tools",
    title: "All 15 tools in v1.0.0",
    lead:
      "Grouped the same way as the in-app Categories screen. Each description below reflects what the published source actually does.",
    countOne: "tool",
    countMany: "tools",
    directLink: "Direct link to this tool",
  },
  previews: {
    id: "preview",
    title: "Interface preview",
    lead:
      "These panels are rebuilt from the app’s own Compose layout and colour palette (#F47B20 orange, #172235 navy).",
    disclaimer:
      "Illustrative UI previews, not device screenshots. Real captures can replace them without changing any code — see screenshots/README.md.",
    items: [
      { id: "home", title: "Home", caption: "Search, categories and the full tool list." },
      { id: "calculator", title: "Scientific calculator", caption: "Expression entry with local history." },
      { id: "qr", title: "QR generator", caption: "Text, URL, Wi-Fi and contact payloads." },
      { id: "pdf", title: "Image to PDF", caption: "Multi-image selection, page size and export." },
      { id: "converter", title: "Unit converter", caption: "Six categories with two-way conversion." },
      { id: "settings-rtl", title: "Settings (Arabic RTL)", caption: "Right-to-left layout in the Arabic interface." },
    ],
  },
  compatibility: {
    id: "compatibility",
    title: "Package and compatibility",
    lead: "Read directly from the published AndroidManifest.xml of the APK on this page.",
    rows: [
      { label: "Application ID", value: "com.becetti.tools" },
      { label: "Install ID of this debug APK", value: "com.becetti.tools.debug" },
      { label: "Version name", value: "1.0.0" },
      { label: "Version code", value: "1" },
      { label: "Build type", value: "debug (debuggable = true)" },
      { label: "Signature", value: "APK Signature Scheme v2, debug key" },
      { label: "Minimum Android", value: "7.0 (API 24)" },
      { label: "Target / compile SDK", value: "34 (Android 14)" },
      { label: "ABIs bundled", value: "arm64-v8a, armeabi-v7a, x86, x86_64" },
      { label: "Permissions declared", value: "CAMERA · INTERNET · ACCESS_NETWORK_STATE · READ_MEDIA_IMAGES · READ_MEDIA_VISUAL_USER_SELECTED" },
      { label: "Interface languages", value: "English · French · Arabic (RTL)" },
      { label: "Licence", value: "MIT" },
    ],
  },
  download: {
    id: "verify",
    title: "Download and verify",
    lead:
      "This page links only to the newest published release on GitHub. GitHub resolves that link to whichever release is marked Latest, so it always opens the current version — today v1.0.0 — and the file is linked from the release notes there. The SHA-256 below is the checksum of that exact file.",
    fileLabel: "File",
    sizeLabel: "Size",
    shaLabel: "SHA-256",
    copy: "Copy checksum",
    copied: "Copied",
    releaseLink: "Open the latest release on GitHub",
    verifyTitle: "Check the checksum before installing",
    verifyHint: "The value must match exactly, character for character.",
    commands: [
      { os: "Linux", cmd: "sha256sum BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk" },
      { os: "macOS", cmd: "shasum -a 256 BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk" },
      { os: "Windows", cmd: "certutil -hashfile BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk SHA256" },
    ],
    onPhone: "Verifying on the phone itself",
    onPhoneText:
      "On Android you can check the digest with any file manager that shows SHA-256, or from a terminal emulator with: sha256sum /sdcard/Download/BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk",
  },
  install: {
    id: "install",
    title: "Installation instructions",
    lead: "Five steps, about a minute, on any phone running Android 7.0 or newer.",
    steps: [
      {
        title: "Download the APK",
        text: "Use the download button above on the phone itself, so the file lands in your Downloads folder.",
      },
      {
        title: "Allow installing from this source",
        text: "When Android asks, open Settings and enable “Install unknown apps” for your browser or file manager. This is a per-app Android permission; the app itself cannot change it.",
      },
      {
        title: "Open the file and install",
        text: "Tap the downloaded file, review the permissions Android lists, then choose Install.",
      },
      {
        title: "Expect a Play Protect notice",
        text: "An APK that did not come from Google Play can raise a “scan for harmful apps” prompt. Choose to install anyway only if you verified the checksum in the previous step.",
      },
      {
        title: "Grant permissions as you use the app",
        text: "Camera is only requested when you open the QR scanner, and file access only when you pick images or PDFs. Everything else works without granting anything.",
      },
    ],
    troubleshootTitle: "If installation fails",
    troubleshoot: [
      "“App not installed” usually means an older debug build is already installed. Uninstall it first — the debug install ID is com.becetti.tools.debug.",
      "“There was a problem parsing the package” usually means the download was incomplete. Re-download and compare the checksum.",
      "If you need to keep data from an older build, do not uninstall blindly: export your notes first.",
      "This APK contains arm64-v8a, armeabi-v7a, x86 and x86_64 libraries, so it installs on phones and on most emulators.",
    ],
    uninstallNote: "Uninstalling removes the local database along with the app. Notes and history are not backed up anywhere.",
  },
  privacy: {
    id: "privacy",
    title: "Privacy in plain language",
    lead:
      "The full policy is published on its own page in all three languages. The short version: nothing you type, scan or convert is uploaded.",
    points: [
      "Notes, favourites, recent tools and calculator history are stored in a local Room database on the device.",
      "Camera frames from the QR scanner are decoded on the device by ML Kit and are not saved.",
      "Images and PDFs are read through the Android Storage Access Framework and processed locally; outputs are written to Pictures/Becetti and Documents/Becetti.",
      "The currency converter sends only currency codes to api.frankfurter.app over HTTPS, and caches the result with a timestamp.",
      "Generated and checked passwords never leave the device and are never written to storage.",
      "Advertising consent is stored as a local flag. In v1.0.0 no advertising SDK is initialised and the optional “Sponsored Offers” row opens only when you tap it.",
    ],
    cta: "Read the full privacy policy",
  },
  faq: {
    id: "faq",
    title: "Frequently asked questions",
    items: [
      {
        q: "Is BECETTI TOOLS free?",
        a: "Yes. v1.0.0 is free to download and has no subscription, no account and no paywalled tool. The source is MIT-licensed.",
      },
      {
        q: "Does it work without internet?",
        a: "Every tool except the live currency update works offline. The currency converter shows cached rates with their last-updated time and an offline indicator.",
      },
      {
        q: "Why is this APK labelled a debug build?",
        a: "It was built with the Android Gradle plugin’s debug variant, so it is debuggable and signed with a debug key. That is stated openly instead of being presented as a store release. A signed release build is on the roadmap.",
      },
      {
        q: "Which Android versions are supported?",
        a: "Android 7.0 (API 24) and newer. It is compiled against API 34 (Android 14) and uses the Android 13+ media permissions. Newer Android versions are expected to work but are not part of the tested baseline.",
      },
      {
        q: "Why is the app about 89 MB?",
        a: "The file is 88,869,105 bytes — 88.9 MB in decimal, which is what your phone's file manager shows, or 84.8 MiB in binary. Most of it is the bundled on-device machine-learning libraries for barcode scanning and text recognition, plus four CPU architectures. That is the price of working offline without sending your camera images to a server.",
      },
      {
        q: "Are my passwords or notes uploaded anywhere?",
        a: "No. Passwords are generated with SecureRandom and are never stored. Notes live in the local Room database. There is no sync service in v1.0.0.",
      },
      {
        q: "Does OCR read Arabic?",
        a: "The text recognition model bundled in v1.0.0 covers Latin script, so French and English work. Arabic-script OCR needs an additional model and is listed for v1.1.",
      },
      {
        q: "Will I see ads?",
        a: "In v1.0.0 no advertising SDK is initialised. A labelled “Sponsored Offers” row can appear on the home screen; it opens an external page only when you tap it, never automatically. The calculator screen never shows ads.",
      },
      {
        q: "Is there an iOS or web version?",
        a: "No. The app is native Android only (Kotlin, Jetpack Compose, Material 3).",
      },
      {
        q: "How do I uninstall and remove my data?",
        a: "Uninstall the app from Android settings. The local database is deleted with it. There is no server-side copy to delete.",
      },
    ],
  },
  roadmap: {
    id: "roadmap",
    title: "Proposed for future versions",
    lead:
      "These are proposals, not part of v1.0.0. The approved 1.0.0 baseline stays unchanged; each item below would ship as a separate, separately tested addition.",
    note: "Dates are not promised, and nothing on this list changes the behaviour of the current download.",
    groups: [
      {
        version: "v1.1",
        items: [
          "Additional scientific calculator functions",
          "More unit conversion categories",
          "Improved OCR language support, including Arabic script",
          "Additional image processing options",
          "Improved PDF management",
        ],
      },
      {
        version: "v1.2",
        items: [
          "Home screen widgets",
          "A customisable dashboard",
          "More favourites and shortcuts",
          "Deeper application personalisation",
        ],
      },
      {
        version: "v2.0",
        items: [
          "Optional encrypted cloud backup",
          "Optional cross-device synchronisation",
          "Export and import of user settings",
          "Advanced document utilities",
        ],
      },
    ],
  },
  contact: {
    id: "contact",
    title: "Contact",
    lead: "Bug reports, translation corrections and feature requests are welcome.",
    emailLabel: "Email",
    githubLabel: "Developer on GitHub",
    issuesLabel: "Report an issue",
    repoLabel: "Repository",
    responseNote: "Please do not attach personal notes or password screenshots to public issues.",
  },
  footer: {
    madeBy: "Built by Ahmed Becetti",
    license: "Source released under the MIT licence",
    version: "Version 1.0.0 (versionCode 1) · debug build",
    updated: "Page updated",
    factNote:
      "No download counters, star ratings or user numbers are published on this page, because none could be verified.",
    links: "Site",
    legal: "Legal",
    project: "Project",
  },
};

const fr = {
  dir: "ltr",
  htmlLang: "fr",
  locale: "fr_FR",
  route: { landing: "becetti-tools/fr/", privacy: "privacy/fr/" },
  meta: {
    title: "BECETTI TOOLS — Outils Android gratuits (calculatrice, QR, PDF, OCR)",
    description:
      "BECETTI TOOLS v1.0.0 : 15 outils Android gratuits — calculatrice, convertisseurs, QR, image en PDF, fusion PDF, OCR et notes. Natif, hors-ligne, sans compte.",
    keywords: [
      "BECETTI TOOLS Android",
      "Outils Android gratuits",
      "Convertisseur PDF Android",
      "Générateur QR Android",
      "calculatrice Android gratuite",
      "application Android hors ligne",
      "convertisseur d’unités Android",
      "fusion PDF Android",
    ],
    ogImageAlt: "BECETTI TOOLS — Tout ce dont vous avez besoin. Une boîte à outils intelligente.",
  },
  nav: {
    label: "Navigation principale",
    tools: "Outils",
    previews: "Aperçu",
    install: "Installer",
    verify: "Vérifier",
    privacy: "Confidentialité",
    faq: "FAQ",
    roadmap: "Feuille de route",
    contact: "Contact",
    download: "Télécharger l’APK",
    language: "Langue",
    theme: "Thème",
    themeLight: "Clair",
    themeDark: "Sombre",
    themeSystem: "Système",
    skip: "Aller au contenu principal",
  },
  hero: {
    badge: "v1.0.0 · Android natif · Gratuit · Sans compte",
    title: "BECETTI TOOLS",
    tagline: "Tout ce dont vous avez besoin. Une boîte à outils intelligente.",
    lead:
      "Quinze outils ciblés pour le quotidien — calculs, conversions, QR codes, PDF, images et texte — développés en Kotlin avec Jetpack Compose. Sans compte, sans abonnement, et les outils essentiels continuent de fonctionner sans réseau.",
    ctaPrimary: "Télécharger l’APK",
    ctaSecondary: "Guide d’installation",
    ctaTertiary: "Voir le code sur GitHub",
    sizeMeta: "88,9 Mo",
    facts: [
      { value: "15", label: "outils dans la v1.0.0" },
      { value: "88,9 Mo", label: "taille de l’APK debug (84,8 Mio)" },
      { value: "Android 7.0+", label: "minSdk 24" },
      { value: "AR · FR · EN", label: "langues de l’interface" },
    ],
    buildNoticeTitle: "Cet APK est une version debug",
    buildNotice:
      "Il est débuggable et signé avec une clé de debug, pas avec une clé de production/Play. Installez-le pour évaluer l’application : ce n’est pas une version prête pour un store. Le fichier publié ici est exactement celui archivé dans le dépôt.",
  },
  trust: {
    title: "Ce que l’application fait de vos données",
    items: [
      {
        title: "Sans compte, sans pistage",
        text: "Aucune connexion, aucun SDK d’analytique et aucun identifiant publicitaire dans la v1.0.0. Notes, favoris et historique restent dans une base locale.",
      },
      {
        title: "Traitement sur l’appareil",
        text: "Lecture des QR, OCR, création de PDF et génération de mots de passe se font sur le téléphone. Les mots de passe ne sont ni stockés ni envoyés.",
      },
      {
        title: "Autorisations à la demande",
        text: "La caméra n’est demandée qu’à l’ouverture du scanner QR. Les fichiers sont choisis via le sélecteur Android, sans parcourir votre stockage.",
      },
      {
        title: "Open source",
        text: "Le code Kotlin complet, y compris chaque outil ci-dessous, est public sous licence MIT. Vous pouvez le lire, le recompiler et l’auditer.",
      },
    ],
  },
  toolsSection: {
    id: "tools",
    title: "Les 15 outils de la v1.0.0",
    lead:
      "Regroupés comme dans l’écran Catégories de l’application. Chaque description correspond au comportement réel du code publié.",
    countOne: "outil",
    countMany: "outils",
    directLink: "Lien direct vers cet outil",
  },
  previews: {
    id: "preview",
    title: "Aperçu de l’interface",
    lead:
      "Ces panneaux sont reconstruits à partir de la mise en page Compose et de la palette de l’application (#F47B20 orange, #172235 bleu nuit).",
    disclaimer:
      "Aperçus illustratifs, pas des captures d’écran réelles. De vraies captures peuvent les remplacer sans modifier le code — voir screenshots/README.md.",
    items: [
      { id: "home", title: "Accueil", caption: "Recherche, catégories et liste complète des outils." },
      { id: "calculator", title: "Calculatrice scientifique", caption: "Saisie d’expression avec historique local." },
      { id: "qr", title: "Générateur QR", caption: "Textes, URL, Wi-Fi et contacts." },
      { id: "pdf", title: "Image en PDF", caption: "Sélection multi-images, format de page et export." },
      { id: "converter", title: "Convertisseur d’unités", caption: "Six catégories, conversion dans les deux sens." },
      { id: "settings-rtl", title: "Réglages (RTL arabe)", caption: "Mise en page de droite à gauche en arabe." },
    ],
  },
  compatibility: {
    id: "compatibility",
    title: "Paquet et compatibilité",
    lead: "Lu directement dans l’AndroidManifest.xml de l’APK publié sur cette page.",
    rows: [
      { label: "Identifiant d’application", value: "com.becetti.tools" },
      { label: "Identifiant d’installation (debug)", value: "com.becetti.tools.debug" },
      { label: "Nom de version", value: "1.0.0" },
      { label: "Code de version", value: "1" },
      { label: "Type de build", value: "debug (debuggable = true)" },
      { label: "Signature", value: "APK Signature Scheme v2, clé de debug" },
      { label: "Android minimum", value: "7.0 (API 24)" },
      { label: "SDK cible / compilation", value: "34 (Android 14)" },
      { label: "ABI inclus", value: "arm64-v8a, armeabi-v7a, x86, x86_64" },
      { label: "Autorisations déclarées", value: "CAMERA · INTERNET · ACCESS_NETWORK_STATE · READ_MEDIA_IMAGES · READ_MEDIA_VISUAL_USER_SELECTED" },
      { label: "Langues de l’interface", value: "anglais · français · arabe (RTL)" },
      { label: "Licence", value: "MIT" },
    ],
  },
  download: {
    id: "verify",
    title: "Téléchargement et vérification",
    lead:
      "Cette page ne renvoie qu’à la dernière release publiée sur GitHub. GitHub résout ce lien vers la release marquée « Latest », qui ouvre donc toujours la version actuelle — aujourd’hui la v1.0.0 — et le fichier y est lié dans les notes de version. Le SHA-256 ci-dessous est l’empreinte de ce fichier exact.",
    fileLabel: "Fichier",
    sizeLabel: "Taille",
    shaLabel: "SHA-256",
    copy: "Copier l’empreinte",
    copied: "Copié",
    releaseLink: "Ouvrir la dernière release sur GitHub",
    verifyTitle: "Vérifiez l’empreinte avant d’installer",
    verifyHint: "La valeur doit correspondre exactement, caractère par caractère.",
    commands: [
      { os: "Linux", cmd: "sha256sum BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk" },
      { os: "macOS", cmd: "shasum -a 256 BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk" },
      { os: "Windows", cmd: "certutil -hashfile BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk SHA256" },
    ],
    onPhone: "Vérifier directement sur le téléphone",
    onPhoneText:
      "Sur Android, utilisez un gestionnaire de fichiers affichant le SHA-256, ou un terminal : sha256sum /sdcard/Download/BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk",
  },
  install: {
    id: "install",
    title: "Instructions d’installation",
    lead: "Cinq étapes, une minute environ, sur tout téléphone sous Android 7.0 ou plus récent.",
    steps: [
      {
        title: "Télécharger l’APK",
        text: "Utilisez le bouton de téléchargement ci-dessus depuis le téléphone, pour que le fichier arrive dans Téléchargements.",
      },
      {
        title: "Autoriser l’installation depuis cette source",
        text: "Quand Android le demande, ouvrez les réglages et activez « Installer des applications inconnues » pour votre navigateur ou gestionnaire de fichiers. C’est une autorisation Android par application.",
      },
      {
        title: "Ouvrir le fichier et installer",
        text: "Touchez le fichier téléchargé, vérifiez les autorisations listées par Android, puis choisissez Installer.",
      },
      {
        title: "Un avertissement Play Protect est normal",
        text: "Un APK ne venant pas de Google Play peut déclencher une analyse de sécurité. Ne poursuivez que si vous avez vérifié l’empreinte à l’étape précédente.",
      },
      {
        title: "Accorder les autorisations à l’usage",
        text: "La caméra n’est demandée qu’à l’ouverture du scanner QR, et l’accès aux fichiers seulement quand vous choisissez des images ou des PDF.",
      },
    ],
    troubleshootTitle: "Si l’installation échoue",
    troubleshoot: [
      "« Application non installée » signifie souvent qu’une ancienne version debug est présente. Désinstallez-la d’abord — l’identifiant debug est com.becetti.tools.debug.",
      "« Problème d’analyse du paquet » indique généralement un téléchargement incomplet. Retéléchargez et comparez l’empreinte.",
      "Si vous devez conserver des données d’une ancienne version, ne désinstallez pas à l’aveugle : exportez d’abord vos notes.",
      "Cet APK contient les bibliothèques arm64-v8a, armeabi-v7a, x86 et x86_64 : il s’installe sur téléphone comme sur la plupart des émulateurs.",
    ],
    uninstallNote: "La désinstallation supprime la base locale avec l’application. Notes et historique ne sont sauvegardés nulle part.",
  },
  privacy: {
    id: "privacy",
    title: "La confidentialité, simplement",
    lead:
      "La politique complète est publiée sur une page dédiée dans les trois langues. En bref : rien de ce que vous saisissez, scannez ou convertissez n’est envoyé.",
    points: [
      "Notes, favoris, outils récents et historique de calcul sont stockés dans une base Room locale, sur l’appareil.",
      "Les images de la caméra du scanner QR sont décodées sur l’appareil par ML Kit et ne sont pas enregistrées.",
      "Images et PDF sont lus via le Storage Access Framework d’Android et traités localement ; les résultats vont dans Pictures/Becetti et Documents/Becetti.",
      "Le convertisseur de devises n’envoie que des codes de devises à api.frankfurter.app en HTTPS, et met le résultat en cache avec un horodatage.",
      "Les mots de passe générés ou testés ne quittent jamais l’appareil et ne sont jamais écrits sur le stockage.",
      "Le consentement publicitaire est un simple indicateur local. En v1.0.0 aucun SDK publicitaire n’est initialisé et la ligne « Offres sponsorisées » ne s’ouvre qu’au toucher.",
    ],
    cta: "Lire la politique de confidentialité complète",
  },
  faq: {
    id: "faq",
    title: "Questions fréquentes",
    items: [
      {
        q: "BECETTI TOOLS est-il gratuit ?",
        a: "Oui. La v1.0.0 est gratuite, sans abonnement, sans compte et sans outil verrouillé. Le code est sous licence MIT.",
      },
      {
        q: "Fonctionne-t-il sans internet ?",
        a: "Tous les outils sauf la mise à jour des devises fonctionnent hors ligne. Le convertisseur affiche des taux en cache avec leur date et un indicateur hors-ligne.",
      },
      {
        q: "Pourquoi cet APK est-il marqué debug ?",
        a: "Il a été produit par la variante debug du plugin Android Gradle : il est débuggable et signé avec une clé de debug. Nous le disons clairement plutôt que de le présenter comme une version store. Une version release signée figure dans la feuille de route.",
      },
      {
        q: "Quelles versions d’Android sont prises en charge ?",
        a: "Android 7.0 (API 24) et plus récent. La compilation cible l’API 34 (Android 14) et utilise les autorisations média d’Android 13+. Les versions plus récentes devraient fonctionner, sans faire partie de la base testée.",
      },
      {
        q: "Pourquoi l’application fait-elle environ 89 Mo ?",
        a: "Le fichier fait 88 869 105 octets, soit 88,9 Mo en décimal (ce qu’affiche le gestionnaire de fichiers du téléphone) ou 84,8 Mio en binaire. L’essentiel du poids vient des bibliothèques d’apprentissage automatique embarquées pour les codes-barres et l’OCR, et de quatre architectures processeur. C’est le prix du fonctionnement hors ligne sans envoyer vos images à un serveur.",
      },
      {
        q: "Mes mots de passe ou mes notes sont-ils envoyés quelque part ?",
        a: "Non. Les mots de passe sont générés avec SecureRandom et ne sont jamais stockés. Les notes restent dans la base Room locale. Aucun service de synchronisation en v1.0.0.",
      },
      {
        q: "L’OCR lit-il l’arabe ?",
        a: "Le modèle embarqué en v1.0.0 couvre l’écriture latine : le français et l’anglais fonctionnent. L’OCR arabe nécessite un modèle supplémentaire et figure en v1.1.",
      },
      {
        q: "Y aura-t-il de la publicité ?",
        a: "En v1.0.0, aucun SDK publicitaire n’est initialisé. Une ligne « Offres sponsorisées » peut apparaître sur l’accueil ; elle n’ouvre une page externe qu’au toucher, jamais automatiquement. L’écran calculatrice n’affiche jamais de publicité.",
      },
      {
        q: "Existe-t-il une version iOS ou web ?",
        a: "Non. L’application est uniquement Android natif (Kotlin, Jetpack Compose, Material 3).",
      },
      {
        q: "Comment désinstaller et supprimer mes données ?",
        a: "Désinstallez l’application depuis les réglages Android. La base locale est supprimée avec elle. Aucune copie n’existe sur un serveur.",
      },
    ],
  },
  roadmap: {
    id: "roadmap",
    title: "Propositions pour les versions futures",
    lead:
      "Ce sont des propositions, pas des éléments de la v1.0.0. La base approuvée reste inchangée ; chaque point serait livré comme ajout séparé et testé séparément.",
    note: "Aucune date n’est promise et rien de cette liste ne modifie le téléchargement actuel.",
    groups: [
      {
        version: "v1.1",
        items: [
          "Fonctions scientifiques supplémentaires",
          "Davantage de catégories de conversion",
          "Meilleure prise en charge OCR, y compris l’arabe",
          "Options de traitement d’image supplémentaires",
          "Gestion des PDF améliorée",
        ],
      },
      {
        version: "v1.2",
        items: [
          "Widgets d’écran d’accueil",
          "Tableau de bord personnalisable",
          "Favoris et raccourcis supplémentaires",
          "Personnalisation plus poussée",
        ],
      },
      {
        version: "v2.0",
        items: [
          "Sauvegarde cloud chiffrée, facultative",
          "Synchronisation entre appareils, facultative",
          "Export et import des réglages",
          "Utilitaires de documents avancés",
        ],
      },
    ],
  },
  contact: {
    id: "contact",
    title: "Contact",
    lead: "Les rapports de bugs, corrections de traduction et demandes de fonctionnalités sont bienvenus.",
    emailLabel: "E-mail",
    githubLabel: "Développeur sur GitHub",
    issuesLabel: "Signaler un problème",
    repoLabel: "Dépôt",
    responseNote: "Merci de ne pas joindre de notes personnelles ni de captures de mots de passe aux tickets publics.",
  },
  footer: {
    madeBy: "Développé par Ahmed Becetti",
    license: "Code publié sous licence MIT",
    version: "Version 1.0.0 (versionCode 1) · build debug",
    updated: "Page mise à jour",
    factNote:
      "Aucun compteur de téléchargements, note par étoiles ou nombre d’utilisateurs n’est publié ici : aucun ne pourrait être vérifié.",
    links: "Site",
    legal: "Légal",
    project: "Projet",
  },
};

const ar = {
  dir: "rtl",
  htmlLang: "ar",
  locale: "ar_DZ",
  route: { landing: "becetti-tools/ar/", privacy: "privacy/ar/" },
  meta: {
    title: "أدوات بي سيتي — تطبيق أدوات أندرويد مجاني (حاسبة، QR، PDF، OCR)",
    description:
      "أدوات بي سيتي 1.0.0: 15 أداة أندرويد مجانية — حاسبة ومحوّلات وحدات وعملات ورموز QR وتحويل الصور إلى PDF ودمج PDF واستخراج النص والملاحظات. أصلية وتعمل دون اتصال وبدون حساب.",
    keywords: [
      "أدوات أندرويد مجانية",
      "تطبيق أدوات بي سيتي",
      "تطبيق تحويل الصور إلى PDF",
      "ماسح QR للأندرويد",
      "حاسبة أندرويد مجانية",
      "دمج ملفات PDF أندرويد",
      "محوّل وحدات أندرويد",
      "تطبيق يعمل بدون إنترنت",
    ],
    ogImageAlt: "أدوات بي سيتي — كل ما تحتاجه. صندوق أدوات ذكي واحد.",
  },
  nav: {
    label: "التنقل الرئيسي",
    tools: "الأدوات",
    previews: "معاينة",
    install: "التثبيت",
    verify: "التحقق",
    privacy: "الخصوصية",
    faq: "الأسئلة",
    roadmap: "خطة التطوير",
    contact: "اتصل بنا",
    download: "تحميل APK",
    language: "اللغة",
    theme: "المظهر",
    themeLight: "فاتح",
    themeDark: "داكن",
    themeSystem: "النظام",
    skip: "الانتقال إلى المحتوى الرئيسي",
  },
  hero: {
    badge: "الإصدار 1.0.0 · أندرويد أصلي · مجاني · بدون حساب",
    title: "أدوات بي سيتي",
    tagline: "كل ما تحتاجه. صندوق أدوات ذكي واحد.",
    lead:
      "خمس عشرة أداة مركّزة للاستخدام اليومي — حسابات وتحويلات ورموز QR وملفات PDF وصور ونصوص — مبنية بـ Kotlin و Jetpack Compose. بدون حساب وبدون اشتراك، والأدوات الأساسية تعمل حتى مع إيقاف الشبكة.",
    ctaPrimary: "تحميل ملف APK",
    ctaSecondary: "دليل التثبيت",
    ctaTertiary: "الشيفرة على GitHub",
    sizeMeta: "88.9 م.ب",
    facts: [
      { value: "15", label: "أداة في الإصدار 1.0.0" },
      { value: "88.9 م.ب", label: "حجم ملف APK التجريبي (84.8 مبيبايت)" },
      { value: "أندرويد 7.0+", label: "minSdk 24" },
      { value: "عربي · فرنسي · إنجليزي", label: "لغات الواجهة" },
    ],
    buildNoticeTitle: "هذا الملف إصدار تجريبي (debug)",
    buildNotice:
      "الملف قابل للتصحيح وموقّع بمفتاح تجريبي، وليس بمفتاح إنتاج أو مفتاح متجر Play. ثبّته لتجربة التطبيق، فهو ليس إصدارًا جاهزًا للمتجر. الملف المنشور هنا هو نفسه المحفوظ في المستودع.",
  },
  trust: {
    title: "ما يفعله التطبيق ببياناتك",
    items: [
      {
        title: "بدون حساب وبدون تتبّع",
        text: "لا تسجيل دخول ولا حزمة تحليلات ولا معرّف إعلاني في الإصدار 1.0.0. الملاحظات والمفضلة والسجل تبقى في قاعدة بيانات محلية.",
      },
      {
        title: "المعالجة على الجهاز",
        text: "قراءة رموز QR واستخراج النص وإنشاء PDF وتوليد كلمات المرور كلها تجري على الهاتف. كلمات المرور لا تُخزَّن ولا تُرسَل.",
      },
      {
        title: "أذونات عند الحاجة فقط",
        text: "الكاميرا تُطلب فقط عند فتح ماسح الرموز، والملفات تُختار عبر منتقي أندرويد بدل تصفّح مساحة التخزين.",
      },
      {
        title: "مفتوح المصدر",
        text: "شيفرة Kotlin كاملة، مع كل أداة مذكورة أدناه، منشورة برخصة MIT. يمكنك قراءتها وإعادة بنائها ومراجعتها.",
      },
    ],
  },
  toolsSection: {
    id: "tools",
    title: "الأدوات الخمس عشرة في الإصدار 1.0.0",
    lead: "مرتّبة كما في شاشة الفئات داخل التطبيق. كل وصف أدناه يعكس ما تفعله الشيفرة المنشورة فعليًا.",
    countOne: "أداة",
    countMany: "أداة",
    directLink: "رابط مباشر لهذه الأداة",
  },
  previews: {
    id: "preview",
    title: "معاينة الواجهة",
    lead: "أُعيد بناء هذه اللوحات من تخطيط Compose ومن ألوان التطبيق (#F47B20 برتقالي، #172235 كحلي).",
    disclaimer:
      "معاينات توضيحية وليست لقطات شاشة حقيقية. يمكن استبدالها بلقطات فعلية دون تغيير أي شيفرة — راجع screenshots/README.md.",
    items: [
      { id: "home", title: "الرئيسية", caption: "البحث والفئات وقائمة الأدوات الكاملة." },
      { id: "calculator", title: "الحاسبة العلمية", caption: "إدخال التعبير مع سجل محلي." },
      { id: "qr", title: "مُنشئ QR", caption: "نص أو رابط أو شبكة Wi-Fi أو جهة اتصال." },
      { id: "pdf", title: "تحويل الصور إلى PDF", caption: "اختيار عدة صور وحجم الصفحة والتصدير." },
      { id: "converter", title: "محوّل الوحدات", caption: "ست فئات مع تحويل في الاتجاهين." },
      { id: "settings-rtl", title: "الإعدادات (عربي RTL)", caption: "تخطيط من اليمين إلى اليسار في الواجهة العربية." },
    ],
  },
  compatibility: {
    id: "compatibility",
    title: "الحزمة والتوافق",
    lead: "مقروءة مباشرة من ملف AndroidManifest.xml داخل الملف المنشور في هذه الصفحة.",
    rows: [
      { label: "معرّف التطبيق", value: "com.becetti.tools" },
      { label: "معرّف التثبيت لهذا الملف التجريبي", value: "com.becetti.tools.debug" },
      { label: "اسم الإصدار", value: "1.0.0" },
      { label: "رمز الإصدار", value: "1" },
      { label: "نوع البناء", value: "تجريبي (debuggable = true)" },
      { label: "التوقيع", value: "APK Signature Scheme v2 بمفتاح تجريبي" },
      { label: "أقل إصدار أندرويد", value: "7.0 (API 24)" },
      { label: "هدف البناء / الـ SDK", value: "34 (أندرويد 14)" },
      { label: "معماريات المعالجات", value: "arm64-v8a, armeabi-v7a, x86, x86_64" },
      { label: "الأذونات المعلنة", value: "CAMERA · INTERNET · ACCESS_NETWORK_STATE · READ_MEDIA_IMAGES · READ_MEDIA_VISUAL_USER_SELECTED" },
      { label: "لغات الواجهة", value: "الإنجليزية · الفرنسية · العربية (RTL)" },
      { label: "الرخصة", value: "MIT" },
    ],
  },
  download: {
    id: "verify",
    title: "التحميل والتحقق",
    lead:
      "هذه الصفحة تشير إلى أحدث إصدار منشور على GitHub فقط. يحوّل GitHub هذا الرابط إلى الإصدار المعلَّم كأحدث (Latest)، فيفتح دائمًا النسخة الحالية — حاليًا 1.0.0 — ورابط الملف مذكور في ملاحظات الإصدار هناك. البصمة SHA-256 أدناه هي بصمة الملف نفسه.",
    fileLabel: "الملف",
    sizeLabel: "الحجم",
    shaLabel: "SHA-256",
    copy: "نسخ البصمة",
    copied: "تم النسخ",
    releaseLink: "فتح أحدث إصدار على GitHub",
    verifyTitle: "تحقّق من البصمة قبل التثبيت",
    verifyHint: "يجب أن تتطابق القيمة تمامًا، حرفًا بحرف.",
    commands: [
      { os: "Linux", cmd: "sha256sum BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk" },
      { os: "macOS", cmd: "shasum -a 256 BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk" },
      { os: "Windows", cmd: "certutil -hashfile BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk SHA256" },
    ],
    onPhone: "التحقق على الهاتف نفسه",
    onPhoneText:
      "على أندرويد يمكنك فحص البصمة عبر أي مدير ملفات يعرض SHA-256، أو من تطبيق طرفية بالأمر: sha256sum /sdcard/Download/BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk",
  },
  install: {
    id: "install",
    title: "خطوات التثبيت",
    lead: "خمس خطوات، نحو دقيقة، على أي هاتف يعمل بأندرويد 7.0 أو أحدث.",
    steps: [
      {
        title: "تحميل ملف APK",
        text: "استخدم زر التحميل أعلاه من الهاتف مباشرة، ليصل الملف إلى مجلد التنزيلات.",
      },
      {
        title: "السماح بالتثبيت من هذا المصدر",
        text: "عندما يطلب أندرويد ذلك، افتح الإعدادات وفعّل «تثبيت تطبيقات غير معروفة» للمتصفح أو مدير الملفات. هذا إذن خاص بكل تطبيق ولا يمكن للتطبيق تغييره.",
      },
      {
        title: "فتح الملف والتثبيت",
        text: "اضغط الملف الذي حمّلته، راجع الأذونات التي يعرضها أندرويد، ثم اختر تثبيت.",
      },
      {
        title: "ظهور تنبيه Play Protect أمر طبيعي",
        text: "قد يُطلب فحص أمني لأن الملف ليس من Google Play. تابع فقط إذا كنت قد تحققت من البصمة في الخطوة السابقة.",
      },
      {
        title: "امنح الأذونات عند الاستخدام",
        text: "الكاميرا تُطلب فقط عند فتح ماسح الرموز، والوصول للملفات فقط عند اختيار صور أو ملفات PDF. بقية الأدوات تعمل دون منح أي إذن.",
      },
    ],
    troubleshootTitle: "إذا فشل التثبيت",
    troubleshoot: [
      "رسالة «لم يتم تثبيت التطبيق» تعني عادة وجود نسخة تجريبية أقدم. أزلها أولًا — معرّف النسخة التجريبية هو com.becetti.tools.debug.",
      "رسالة «حدثت مشكلة أثناء تحليل الحزمة» تعني عادة تحميلًا غير مكتمل. أعد التحميل وقارن البصمة.",
      "إذا كنت بحاجة للاحتفاظ ببيانات نسخة قديمة، لا تُزل التطبيق مباشرة: صدّر ملاحظاتك أولًا.",
      "يحتوي الملف على مكتبات arm64-v8a و armeabi-v7a و x86 و x86_64، لذا يعمل على الهواتف وعلى معظم المحاكيات.",
    ],
    uninstallNote: "إزالة التطبيق تحذف قاعدة البيانات المحلية معه. الملاحظات والسجل لا تُنسخ إلى أي مكان.",
  },
  privacy: {
    id: "privacy",
    title: "الخصوصية ببساطة",
    lead: "السياسة الكاملة منشورة في صفحة مستقلة باللغات الثلاث. الخلاصة: لا يُرفع أي شيء تكتبه أو تمسحه أو تحوّله.",
    points: [
      "الملاحظات والمفضلة والأدوات الأخيرة وسجل الحاسبة تُخزَّن في قاعدة Room محلية على الجهاز.",
      "إطارات كاميرا ماسح الرموز تُقرأ على الجهاز بواسطة ML Kit ولا تُحفظ.",
      "الصور وملفات PDF تُقرأ عبر إطار الوصول إلى التخزين في أندرويد وتُعالج محليًا؛ وتُكتب النتائج في Pictures/Becetti و Documents/Becetti.",
      "محوّل العملات يرسل رموز العملات فقط إلى api.frankfurter.app عبر HTTPS ويحفظ النتيجة مع وقت التحديث.",
      "كلمات المرور المولَّدة أو المفحوصة لا تغادر الجهاز ولا تُكتب في التخزين.",
      "موافقة الإعلانات تُحفظ كعلامة محلية. في الإصدار 1.0.0 لا تُهيَّأ أي حزمة إعلانية، وسطر «عروض برعاية» لا يُفتح إلا بلمسك.",
    ],
    cta: "قراءة سياسة الخصوصية الكاملة",
  },
  faq: {
    id: "faq",
    title: "الأسئلة الشائعة",
    items: [
      {
        q: "هل تطبيق أدوات بي سيتي مجاني؟",
        a: "نعم. الإصدار 1.0.0 مجاني، بلا اشتراك وبلا حساب وبلا أداة مدفوعة. الشيفرة منشورة برخصة MIT.",
      },
      {
        q: "هل يعمل بدون إنترنت؟",
        a: "كل الأدوات تعمل دون اتصال باستثناء تحديث أسعار العملات المباشر. يعرض محوّل العملات الأسعار المحفوظة مع وقت آخر تحديث ومؤشر عدم الاتصال.",
      },
      {
        q: "لماذا الملف موسوم بأنه إصدار تجريبي؟",
        a: "لأنه بُني بالنسخة التجريبية من إضافة Android Gradle، فهو قابل للتصحيح وموقّع بمفتاح تجريبي. نذكر ذلك بوضوح بدل تقديمه كإصدار متجر. النسخة الموقّعة للإنتاج ضمن خطة التطوير.",
      },
      {
        q: "ما إصدارات أندرويد المدعومة؟",
        a: "أندرويد 7.0 (API 24) وأحدث. البناء يستهدف API 34 (أندرويد 14) ويستخدم أذونات الوسائط في أندرويد 13+. الإصدارات الأحدث يُتوقع أن تعمل لكنها ليست ضمن خط الأساس المُختبَر.",
      },
      {
        q: "لماذا حجم التطبيق نحو 89 ميجابايت؟",
        a: "حجم الملف 88,869,105 بايت — أي 88.9 ميجابايت بالحساب العشري (وهو ما يعرضه مدير الملفات في هاتفك)، أو 84.8 مبيبايت بالحساب الثنائي. معظم الحجم من مكتبات التعلّم الآلي المضمّنة لقراءة الرموز واستخراج النص، ومن أربع معماريات معالجات؛ وهذا ثمن العمل دون اتصال ودون إرسال صور الكاميرا إلى خادم.",
      },
      {
        q: "هل تُرسل كلمات المرور أو الملاحظات إلى أي مكان؟",
        a: "لا. كلمات المرور تُولَّد بـ SecureRandom ولا تُخزَّن. الملاحظات تبقى في قاعدة Room المحلية، ولا توجد خدمة مزامنة في الإصدار 1.0.0.",
      },
      {
        q: "هل يقرأ استخراج النص اللغة العربية؟",
        a: "نموذج استخراج النص المضمّن في الإصدار 1.0.0 يغطي الكتابة اللاتينية: الفرنسية والإنجليزية تعمل. أما العربية فتحتاج نموذجًا إضافيًا وهي مدرجة في الإصدار 1.1.",
      },
      {
        q: "هل ستظهر إعلانات؟",
        a: "في الإصدار 1.0.0 لا تُهيَّأ أي حزمة إعلانية. قد يظهر سطر موسوم «عروض برعاية» في الشاشة الرئيسية، ولا يفتح صفحة خارجية إلا بلمسك، وليس تلقائيًا. شاشة الحاسبة لا تعرض إعلانات أبدًا.",
      },
      {
        q: "هل توجد نسخة iOS أو ويب؟",
        a: "لا. التطبيق أندرويد أصلي فقط (Kotlin و Jetpack Compose و Material 3).",
      },
      {
        q: "كيف أحذف التطبيق وبياناتي؟",
        a: "أزل التطبيق من إعدادات أندرويد. تُحذف قاعدة البيانات المحلية معه، ولا توجد نسخة على أي خادم.",
      },
    ],
  },
  roadmap: {
    id: "roadmap",
    title: "مقترحات للإصدارات القادمة",
    lead:
      "هذه مقترحات وليست جزءًا من الإصدار 1.0.0. خط الأساس المعتمد يبقى كما هو، وكل بند سيُنفَّذ كإضافة منفصلة ومُختبرة بشكل مستقل.",
    note: "لا تواريخ موعودة، ولا شيء في هذه القائمة يغيّر سلوك الملف الحالي.",
    groups: [
      {
        version: "1.1",
        items: [
          "دوال حسابية علمية إضافية",
          "فئات تحويل وحدات أكثر",
          "دعم أفضل للغات في استخراج النص، بما فيها العربية",
          "خيارات إضافية لمعالجة الصور",
          "إدارة أفضل لملفات PDF",
        ],
      },
      {
        version: "1.2",
        items: [
          "ودجات للشاشة الرئيسية",
          "لوحة رئيسية قابلة للتخصيص",
          "مفضلة واختصارات أكثر",
          "تخصيص أعمق للتطبيق",
        ],
      },
      {
        version: "2.0",
        items: [
          "نسخ احتياطي سحابي مشفّر واختياري",
          "مزامنة اختيارية بين الأجهزة",
          "تصدير واستيراد إعدادات المستخدم",
          "أدوات متقدمة للمستندات",
        ],
      },
    ],
  },
  contact: {
    id: "contact",
    title: "التواصل",
    lead: "نرحّب ببلاغات الأخطاء وتصحيحات الترجمة وطلبات الميزات.",
    emailLabel: "البريد الإلكتروني",
    githubLabel: "المطوّر على GitHub",
    issuesLabel: "الإبلاغ عن مشكلة",
    repoLabel: "المستودع",
    responseNote: "يُرجى عدم إرفاق ملاحظات شخصية أو صور لكلمات مرور في البلاغات العامة.",
  },
  footer: {
    madeBy: "من تطوير أحمد بيسيتي",
    license: "الشيفرة منشورة برخصة MIT",
    version: "الإصدار 1.0.0 (versionCode 1) · بناء تجريبي",
    updated: "آخر تحديث للصفحة",
    factNote: "لا تُنشر في هذه الصفحة عدّادات تحميل أو تقييمات بالنجوم أو أعداد مستخدمين، لعدم إمكانية التحقق منها.",
    links: "الموقع",
    legal: "قانوني",
    project: "المشروع",
  },
};

export const pages = { en, fr, ar };
export const locales = ["en", "fr", "ar"];
