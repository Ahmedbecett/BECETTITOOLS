package com.becetti.tools.core.localization

/**
 * Localisation for the professional toolkit expansion.
 *
 * The v1.0 [AppStrings] data class covers the original 18 tools. Rather than triple the size of that
 * data class, new strings are exposed as extension properties that resolve against [AppStrings.langCode].
 * Every string is authored in English, Arabic and French in one place, so no screen can drift into a
 * partially-translated state.
 */
private fun AppStrings.t3(en: String, ar: String, fr: String): String = when (langCode) {
    "ar" -> ar
    "fr" -> fr
    else -> en
}

/* --------------------------------------- Navigation --------------------------------------- */

val AppStrings.navHomeTab: String get() = t3("Home", "الرئيسية", "Accueil")
val AppStrings.navDevelopers: String get() = t3("Developers", "المطورون", "Développeurs")
val AppStrings.navProjects: String get() = t3("Projects", "المشاريع", "Projets")
val AppStrings.navTools: String get() = t3("Tools", "الأدوات", "Outils")
val AppStrings.navProfile: String get() = t3("Profile", "الملف الشخصي", "Profil")

/* ------------------------------------ Groups / categories --------------------------------- */

fun AppStrings.groupName(id: String): String = when (id) {
    "developer" -> t3("Developer Studio", "استوديو المطوّر", "Studio Développeur")
    "projects" -> t3("Project Manager", "مدير المشاريع", "Gestion de Projets")
    "files" -> t3("File Master", "مدير الملفات", "Maître des Fichiers")
    "media" -> t3("Image & Media Studio", "استوديو الصور والوسائط", "Studio Image & Média")
    "network" -> t3("Web & Network", "الويب والشبكات", "Web & Réseau")
    "business" -> t3("Business Toolkit", "حزمة الأعمال", "Boîte à Outils Pro")
    "everyday" -> t3("Everyday Tools", "أدوات يومية", "Outils Quotidiens")
    else -> t3("Tools", "أدوات", "Outils")
}

fun AppStrings.groupDescription(id: String): String = when (id) {
    "developer" -> t3("Format, encode, hash, test and call APIs", "تنسيق وترميز وتجزئة واختبار واستدعاء واجهات API", "Formater, encoder, hacher, tester et appeler des API")
    "projects" -> t3("Boards, tasks, deadlines, budgets and reports", "لوحات ومهام ومواعيد وميزانيات وتقارير", "Tableaux, tâches, échéances, budgets et rapports")
    "files" -> t3("Browse, archive, split, analyse and compare files", "استعراض وأرشفة وتقسيم وتحليل ومقارنة الملفات", "Parcourir, archiver, diviser, analyser et comparer")
    "media" -> t3("Resize, crop, record, trim and convert media", "تغيير الحجم والقص والتسجيل والاقتطاع والتحويل", "Redimensionner, rogner, enregistrer, découper, convertir")
    "network" -> t3("DNS, IP, headers, diagnostics and link checks", "DNS وعناوين IP والترويسات والتشخيص وفحص الروابط", "DNS, IP, en-têtes, diagnostics et vérification de liens")
    "business" -> t3("Invoices, quotes, customers, stock and sales", "فواتير وعروض أسعار وعملاء ومخزون ومبيعات", "Factures, devis, clients, stock et ventes")
    "everyday" -> t3("Calculators, converters, timers, QR and dates", "حاسبات ومحولات ومؤقتات ورموز QR وتواريخ", "Calculatrices, convertisseurs, minuteurs, QR et dates")
    else -> ""
}

/* ----------------------------------------- Tools ------------------------------------------ */

fun AppStrings.toolName(id: String): String = when (id) {
    // Developer
    "dev_json" -> t3("JSON & XML", "JSON و XML", "JSON & XML")
    "dev_base64" -> t3("Base64 Codec", "ترميز Base64", "Codec Base64")
    "dev_hash" -> t3("SHA Hashing", "تجزئة SHA", "Hachage SHA")
    "dev_uuid" -> t3("UUID Generator", "مولّد UUID", "Générateur UUID")
    "dev_regex" -> t3("Regex Tester", "اختبار التعبيرات النمطية", "Testeur Regex")
    "dev_api" -> t3("REST API Client", "عميل REST API", "Client REST API")
    "dev_diff" -> t3("Code Comparison", "مقارنة الأكواد", "Comparaison de Code")
    "dev_url" -> t3("URL Encoder", "ترميز الروابط", "Encodeur d'URL")
    "dev_github" -> t3("GitHub Utilities", "أدوات GitHub", "Utilitaires GitHub")
    // Files
    "file_manager" -> t3("File Manager", "مدير الملفات", "Gestionnaire de Fichiers")
    "file_zip" -> t3("ZIP Archives", "أرشيف ZIP", "Archives ZIP")
    "file_pdf" -> t3("PDF Split & Inspect", "تقسيم وفحص PDF", "Division PDF")
    "file_compare" -> t3("File Comparison", "مقارنة الملفات", "Comparaison de Fichiers")
    "file_storage" -> t3("Storage Analyzer", "محلّل التخزين", "Analyseur de Stockage")
    "file_duplicates" -> t3("Duplicate Finder", "كشف الملفات المكررة", "Doublons")
    "file_convert" -> t3("Document Conversion", "تحويل المستندات", "Conversion de Documents")
    // Media
    "media_image" -> t3("Image Studio", "استوديو الصور", "Studio Image")
    "media_audio" -> t3("Audio Studio", "استوديو الصوت", "Studio Audio")
    "media_video" -> t3("Video Studio", "استوديو الفيديو", "Studio Vidéo")
    // Network
    "net_dns" -> t3("DNS Lookup", "فحص DNS", "Recherche DNS")
    "net_ip" -> t3("IP Information", "معلومات IP", "Informations IP")
    "net_headers" -> t3("HTTP Headers", "ترويسات HTTP", "En-têtes HTTP")
    "net_diagnostics" -> t3("Connection Diagnostics", "تشخيص الاتصال", "Diagnostic Connexion")
    "net_link" -> t3("Link Inspector", "فاحص الروابط", "Inspecteur de Liens")
    "net_color" -> t3("Color Converter", "محوّل الألوان", "Convertisseur Couleur")
    // Business
    "biz_invoice" -> t3("Invoices", "الفواتير", "Factures")
    "biz_quote" -> t3("Quotations", "عروض الأسعار", "Devis")
    "biz_customers" -> t3("Customers", "العملاء", "Clients")
    "biz_products" -> t3("Product Catalog", "كتالوج المنتجات", "Catalogue Produits")
    "biz_inventory" -> t3("Inventory", "المخزون", "Stock")
    "biz_barcode" -> t3("Barcode Studio", "استوديو الباركود", "Studio Code-barres")
    "biz_report" -> t3("Sales Reports", "تقارير المبيعات", "Rapports de Ventes")
    "biz_expenses" -> t3("Expenses & Profit", "المصاريف والأرباح", "Dépenses & Bénéfice")
    // Everyday
    "date_time" -> t3("Date & Time", "التاريخ والوقت", "Date & Heure")
    "calendar" -> t3("Calendar & Notes", "التقويم والملاحظات", "Calendrier & Notes")
    // Projects
    "projects" -> t3("Project Manager", "مدير المشاريع", "Gestion de Projets")
    // Legacy 18
    "calc_scientific" -> tools["calc_scientific"]?.name ?: "Scientific Calculator"
    "unit_converter" -> tools["unit_converter"]?.name ?: "Unit Converter"
    "currency" -> tools["currency"]?.name ?: "Currency Converter"
    "percentage" -> tools["percentage"]?.name ?: "Percentage Calculator"
    "age" -> tools["age"]?.name ?: "Age Calculator"
    "qr_generator" -> tools["qr_generator"]?.name ?: "QR Generator"
    "qr_scanner" -> tools["qr_scanner"]?.name ?: "QR Scanner"
    "password_gen" -> tools["password_gen"]?.name ?: "Password Generator"
    "password_strength" -> tools["password_strength"]?.name ?: "Password Strength"
    "image_to_pdf" -> tools["image_to_pdf"]?.name ?: "Image to PDF"
    "pdf_merger" -> tools["pdf_merger"]?.name ?: "PDF Merger"
    "image_compressor" -> tools["image_compressor"]?.name ?: "Image Compressor"
    "ocr" -> tools["ocr"]?.name ?: "OCR Text Extractor"
    "text_toolkit" -> tools["text_toolkit"]?.name ?: "Text Toolkit"
    "notes" -> tools["notes"]?.name ?: "Notes Manager"
    "bmi_calculator" -> tools["bmi_calculator"]?.name ?: "BMI Calculator"
    "stopwatch_timer" -> tools["stopwatch_timer"]?.name ?: "Stopwatch & Timer"
    "device_info" -> tools["device_info"]?.name ?: "Device Info"
    else -> id
}

fun AppStrings.toolDescription(id: String): String = when (id) {
    "dev_json" -> t3("Pretty-print, minify, validate and convert", "تنسيق وضغط والتحقق والتحويل", "Formater, minifier, valider et convertir")
    "dev_base64" -> t3("Encode or decode text and files", "ترميز وفك ترميز النصوص والملفات", "Encoder/décoder texte et fichiers")
    "dev_hash" -> t3("SHA-1, SHA-256, SHA-512 and MD5 digests", "بصمات SHA و MD5", "Empreintes SHA et MD5")
    "dev_uuid" -> t3("Generate and inspect unique identifiers", "إنشاء وفحص المعرفات الفريدة", "Générer et inspecter des identifiants")
    "dev_regex" -> t3("Live matches, groups and replacement", "مطابقات ومجموعات واستبدال مباشر", "Correspondances, groupes et remplacement")
    "dev_api" -> t3("Send HTTP requests and inspect responses", "إرسال طلبات HTTP وفحص الاستجابات", "Envoyer des requêtes et lire les réponses")
    "dev_diff" -> t3("Line-by-line comparison of two texts", "مقارنة نصين سطراً بسطر", "Comparaison ligne par ligne")
    "dev_url" -> t3("Percent-encoding and query parsing", "ترميز النسبة وتحليل الاستعلامات", "Encodage et analyse des requêtes")
    "dev_github" -> t3("Profiles, repositories and releases", "الحسابات والمستودعات والإصدارات", "Profils, dépôts et versions")
    "file_manager" -> t3("Browse, open, rename, copy and delete", "استعراض وفتح وإعادة تسمية ونسخ وحذف", "Parcourir, ouvrir, renommer, copier, supprimer")
    "file_zip" -> t3("Create archives and extract safely", "إنشاء أرشيفات واستخراج آمن", "Créer et extraire des archives")
    "file_pdf" -> t3("Split ranges and inspect page counts", "تقسيم النطاقات وفحص الصفحات", "Diviser et inspecter les pages")
    "file_compare" -> t3("Byte and text comparison of two files", "مقارنة بايت ونص لملفين", "Comparer deux fichiers")
    "file_storage" -> t3("Largest folders and storage breakdown", "أكبر المجلدات وتوزيع المساحة", "Dossiers les plus lourds")
    "file_duplicates" -> t3("Detect identical files by content hash", "كشف الملفات المتطابقة بالبصمة", "Détecter les fichiers identiques")
    "file_convert" -> t3("Text, Markdown and CSV to PDF", "النص وMarkdown وCSV إلى PDF", "Texte, Markdown et CSV en PDF")
    "media_image" -> t3("Resize, crop, convert and watermark", "تغيير الحجم والقص والتحويل والعلامة", "Redimensionner, rogner, convertir")
    "media_audio" -> t3("Record, inspect and trim audio", "تسجيل وفحص واقتطاع الصوت", "Enregistrer, inspecter, découper")
    "media_video" -> t3("Trim, extract thumbnails and metadata", "اقتطاع وصور مصغرة وبيانات", "Découper, vignettes et métadonnées")
    "net_dns" -> t3("Resolve hostnames to addresses", "تحويل الأسماء إلى عناوين", "Résoudre les noms d'hôtes")
    "net_ip" -> t3("Public and local address details", "تفاصيل العنوان العام والمحلي", "Détails de l'adresse publique/locale")
    "net_headers" -> t3("Status codes, headers and redirects", "الترويسات والحالة وإعادة التوجيه", "Statut, en-têtes et redirections")
    "net_diagnostics" -> t3("Latency, DNS timing and reachability", "زمن الاستجابة وDNS والوصول", "Latence, DNS et accessibilité")
    "net_link" -> t3("Break down links, params and safety", "تفكيك الروابط والمعاملات والأمان", "Analyser liens, paramètres et sécurité")
    "net_color" -> t3("HEX, RGB, HSL and CMYK conversion", "تحويل HEX وRGB وHSL وCMYK", "HEX, RGB, HSL et CMYK")
    "biz_invoice" -> t3("Create, store and export invoices", "إنشاء وحفظ وتصدير الفواتير", "Créer, stocker et exporter")
    "biz_quote" -> t3("Price offers with validity and totals", "عروض أسعار بالصلاحية والمجاميع", "Offres avec validité et totaux")
    "biz_customers" -> t3("Contacts, addresses and history", "جهات الاتصال والعناوين والسجل", "Contacts, adresses et historique")
    "biz_products" -> t3("Products, prices, SKUs and tax", "منتجات وأسعار ورموز وضرائب", "Produits, prix, SKU et taxes")
    "biz_inventory" -> t3("Stock levels, movements and alerts", "المخزون والحركات والتنبيهات", "Stock, mouvements et alertes")
    "biz_barcode" -> t3("Code128, EAN-13 and QR labels", "ملصقات Code128 وEAN-13 وQR", "Code128, EAN-13 et QR")
    "biz_report" -> t3("Revenue, top items and CSV export", "الإيرادات والأصناف وتصدير CSV", "Revenus, top articles et CSV")
    "biz_expenses" -> t3("Costs, margins and net profit", "التكاليف والهوامش وصافي الربح", "Coûts, marges et bénéfice net")
    "date_time" -> t3("Durations, timestamps and week numbers", "المدد والطوابع الزمنية وأرقام الأسابيع", "Durées, horodatages et semaines")
    "calendar" -> t3("Month view with day notes", "عرض شهري مع ملاحظات يومية", "Vue mensuelle avec notes")
    "projects" -> t3("Boards, tasks, budgets and reports", "لوحات ومهام وميزانيات وتقارير", "Tableaux, tâches, budgets et rapports")
    else -> tools[id]?.description ?: ""
}

/* -------------------------------------- Home dashboard ------------------------------------- */

val AppStrings.homeGreetingMorning: String get() = t3("Good morning", "صباح الخير", "Bonjour")
val AppStrings.homeGreetingAfternoon: String get() = t3("Good afternoon", "مساء الخير", "Bon après-midi")
val AppStrings.homeGreetingEvening: String get() = t3("Good evening", "مساء الخير", "Bonsoir")
val AppStrings.homeWelcomeBack: String get() = t3("Welcome back", "مرحباً بعودتك", "Bon retour")
val AppStrings.homeSubtitle: String get() = t3("Your complete professional toolkit", "صندوق أدواتك الاحترافي الكامل", "Votre boîte à outils professionnelle")
val AppStrings.homeFeatured: String get() = t3("Featured tools", "أدوات مميزة", "Outils en vedette")
val AppStrings.homeCategories: String get() = t3("Categories", "الفئات", "Catégories")
val AppStrings.homeRecent: String get() = t3("Recently used", "المستخدمة مؤخراً", "Récemment utilisés")
val AppStrings.homeFavorites: String get() = t3("Favorites", "المفضلة", "Favoris")
val AppStrings.homeQuickActions: String get() = t3("Quick actions", "إجراءات سريعة", "Actions rapides")
val AppStrings.homeActivity: String get() = t3("Project activity", "نشاط المشاريع", "Activité des projets")
val AppStrings.homeSeeAll: String get() = t3("See all", "عرض الكل", "Voir tout")
val AppStrings.homeToolsCount: String get() = t3("tools", "أداة", "outils")
val AppStrings.homeProjectsCount: String get() = t3("projects", "مشروع", "projets")
val AppStrings.homeNotesCount: String get() = t3("notes", "ملاحظة", "notes")
val AppStrings.homeOffline: String get() = t3("Works offline", "يعمل دون اتصال", "Fonctionne hors ligne")
val AppStrings.homeNoFavorites: String get() = t3("Tap the star on any tool to pin it here", "اضغط النجمة على أي أداة لتثبيتها هنا", "Touchez l'étoile d'un outil pour l'épingler")
val AppStrings.homeNoRecent: String get() = t3("Tools you open will appear here", "ستظهر الأدوات التي تفتحها هنا", "Les outils ouverts apparaîtront ici")
val AppStrings.homeNoProjects: String get() = t3("No active projects yet", "لا توجد مشاريع نشطة بعد", "Aucun projet actif")
val AppStrings.homeOpenProjects: String get() = t3("Open projects", "المشاريع المفتوحة", "Projets ouverts")
val AppStrings.homeQuickNewProject: String get() = t3("New project", "مشروع جديد", "Nouveau projet")
val AppStrings.homeQuickNewNote: String get() = t3("New note", "ملاحظة جديدة", "Nouvelle note")
val AppStrings.homeQuickQr: String get() = t3("Scan QR", "مسح QR", "Scanner QR")
val AppStrings.homeQuickPassword: String get() = t3("Password", "كلمة مرور", "Mot de passe")
val AppStrings.searchInCategory: String get() = t3("Search in this category…", "ابحث في هذه الفئة…", "Rechercher dans cette catégorie…")
val AppStrings.searchAllTools: String get() = t3("Search every tool…", "ابحث في كل الأدوات…", "Rechercher tous les outils…")
val AppStrings.searchNoResults: String get() = t3("No tools match your search", "لا توجد أدوات مطابقة", "Aucun outil ne correspond")
val AppStrings.searchNoResultsHint: String get() = t3("Try a different keyword or browse by category", "جرّب كلمة أخرى أو تصفح الفئات", "Essayez un autre mot-clé")
val AppStrings.categoryToolsIn: String get() = t3("Tools in this category", "أدوات هذه الفئة", "Outils de cette catégorie")

/* ------------------------------------------ Generic ---------------------------------------- */

val AppStrings.gRun: String get() = t3("Run", "تنفيذ", "Exécuter")
val AppStrings.gCalculate: String get() = t3("Calculate", "احسب", "Calculer")
val AppStrings.gGenerate: String get() = t3("Generate", "إنشاء", "Générer")
val AppStrings.gEncode: String get() = t3("Encode", "ترميز", "Encoder")
val AppStrings.gDecode: String get() = t3("Decode", "فك الترميز", "Décoder")
val AppStrings.gValidate: String get() = t3("Validate", "تحقق", "Valider")
val AppStrings.gFormat: String get() = t3("Format", "تنسيق", "Formater")
val AppStrings.gMinify: String get() = t3("Minify", "ضغط", "Minifier")
val AppStrings.gConvert: String get() = t3("Convert", "تحويل", "Convertir")
val AppStrings.gInput: String get() = t3("Input", "المدخلات", "Entrée")
val AppStrings.gOutput: String get() = t3("Output", "المخرجات", "Sortie")
val AppStrings.gResult: String get() = t3("Result", "النتيجة", "Résultat")
val AppStrings.gDetails: String get() = t3("Details", "التفاصيل", "Détails")
val AppStrings.gStatus: String get() = t3("Status", "الحالة", "Statut")
val AppStrings.gReady: String get() = t3("Ready", "جاهز", "Prêt")
val AppStrings.gProcessing: String get() = t3("Working…", "جارٍ التنفيذ…", "Traitement…")
val AppStrings.gLoading: String get() = t3("Loading…", "جارٍ التحميل…", "Chargement…")
val AppStrings.gError: String get() = t3("Something went wrong", "حدث خطأ", "Une erreur est survenue")
val AppStrings.gRetry: String get() = t3("Retry", "إعادة المحاولة", "Réessayer")
val AppStrings.gAdd: String get() = t3("Add", "إضافة", "Ajouter")
val AppStrings.gEdit: String get() = t3("Edit", "تعديل", "Modifier")
val AppStrings.gClose: String get() = t3("Close", "إغلاق", "Fermer")
val AppStrings.gConfirm: String get() = t3("Confirm", "تأكيد", "Confirmer")
val AppStrings.gOpen: String get() = t3("Open", "فتح", "Ouvrir")
val AppStrings.gRefresh: String get() = t3("Refresh", "تحديث", "Actualiser")
val AppStrings.gSearch: String get() = t3("Search", "بحث", "Rechercher")
val AppStrings.gEmpty: String get() = t3("Nothing here yet", "لا شيء هنا بعد", "Rien pour le moment")
val AppStrings.gPickFile: String get() = t3("Choose a file", "اختر ملفاً", "Choisir un fichier")
val AppStrings.gPickImage: String get() = t3("Choose an image", "اختر صورة", "Choisir une image")
val AppStrings.gNoFileSelected: String get() = t3("No file selected", "لم يتم اختيار ملف", "Aucun fichier sélectionné")
val AppStrings.gFileName: String get() = t3("File name", "اسم الملف", "Nom du fichier")
val AppStrings.gSize: String get() = t3("Size", "الحجم", "Taille")
val AppStrings.gSaved: String get() = t3("Saved", "تم الحفظ", "Enregistré")
val AppStrings.gCopiedToClipboard: String get() = t3("Copied to clipboard", "تم النسخ", "Copié")
val AppStrings.gShare: String get() = t3("Share", "مشاركة", "Partager")
val AppStrings.gSaveToDevice: String get() = t3("Save to device", "حفظ في الجهاز", "Enregistrer")
val AppStrings.gInvalidInput: String get() = t3("Please check the input", "يرجى التحقق من المدخلات", "Vérifiez la saisie")
val AppStrings.gNoNetwork: String get() = t3("No internet connection", "لا يوجد اتصال بالإنترنت", "Pas de connexion Internet")
val AppStrings.gOptional: String get() = t3("optional", "اختياري", "facultatif")
val AppStrings.gAll: String get() = t3("All", "الكل", "Tous")
val AppStrings.gFavoriteAdd: String get() = t3("Add to favorites", "أضف للمفضلة", "Ajouter aux favoris")
val AppStrings.gFavoriteRemove: String get() = t3("Remove from favorites", "إزالة من المفضلة", "Retirer des favoris")
val AppStrings.gClearDone: String get() = t3("Cleared", "تم المسح", "Effacé")
val AppStrings.gDeleted: String get() = t3("Deleted", "تم الحذف", "Supprimé")
val AppStrings.gAllDataDeleted: String get() = t3("All local data deleted", "تم حذف كل البيانات المحلية", "Toutes les données locales ont été supprimées")
val AppStrings.gLocalOnly: String get() = t3("Processed on your device only", "تتم المعالجة على جهازك فقط", "Traité sur votre appareil uniquement")

/* ------------------------------------------ Profile ---------------------------------------- */

val AppStrings.profileTitle: String get() = t3("Profile & Preferences", "الملف الشخصي والتفضيلات", "Profil & Préférences")
val AppStrings.profileGuest: String get() = t3("Local profile", "ملف محلي", "Profil local")
val AppStrings.profileGuestHint: String get() = t3("No account needed — your data stays on this device", "لا حاجة لحساب — بياناتك تبقى على جهازك", "Aucun compte : vos données restent ici")
val AppStrings.profileAppearance: String get() = t3("Appearance", "المظهر", "Apparence")
val AppStrings.profileTheme: String get() = t3("Theme", "السمة", "Thème")
val AppStrings.profileThemeSystem: String get() = t3("System", "النظام", "Système")
val AppStrings.profileThemeDark: String get() = t3("Dark", "داكن", "Sombre")
val AppStrings.profileThemeLight: String get() = t3("Light", "فاتح", "Clair")
val AppStrings.profileLanguage: String get() = t3("Language", "اللغة", "Langue")
val AppStrings.profileReduceMotion: String get() = t3("Reduce motion", "تقليل الحركة", "Réduire les animations")
val AppStrings.profileReduceMotionDesc: String get() = t3("Shorter transitions and no entrance animations", "انتقالات أقصر بدون حركات دخول", "Transitions courtes, sans animations")
val AppStrings.profileData: String get() = t3("Data & storage", "البيانات والتخزين", "Données & stockage")
val AppStrings.profileStorageUsed: String get() = t3("App data on this device", "بيانات التطبيق على الجهاز", "Données de l'app")
val AppStrings.profileClearCache: String get() = t3("Clear temporary files", "مسح الملفات المؤقتة", "Vider les fichiers temporaires")
val AppStrings.profileCacheCleared: String get() = t3("Temporary files cleared", "تم مسح الملفات المؤقتة", "Fichiers temporaires supprimés")
val AppStrings.profileClearAllData: String get() = t3("Delete all app data", "حذف كل بيانات التطبيق", "Supprimer toutes les données")
val AppStrings.profileClearAllConfirm: String get() = t3("This removes your projects, notes, invoices and activity from this device. This cannot be undone.", "سيؤدي هذا إلى حذف مشاريعك وملاحظاتك وفواتيرك ونشاطك من هذا الجهاز، ولا يمكن التراجع.", "Cela supprime vos projets, notes, factures et activité de cet appareil, sans retour possible.")
val AppStrings.profileActivity: String get() = t3("Recent activity", "النشاط الأخير", "Activité récente")
val AppStrings.profileNoActivity: String get() = t3("No activity recorded yet", "لا يوجد نشاط مسجل بعد", "Aucune activité enregistrée")
val AppStrings.profilePrivacy: String get() = t3("Privacy", "الخصوصية", "Confidentialité")
val AppStrings.profileAbout: String get() = t3("About", "حول التطبيق", "À propos")
val AppStrings.profileVersion: String get() = t3("Version", "الإصدار", "Version")
val AppStrings.profileToolsAvailable: String get() = t3("Tools available", "الأدوات المتاحة", "Outils disponibles")
val AppStrings.profileFavoritesCount: String get() = t3("Favorites", "المفضلة", "Favoris")
val AppStrings.profileForceDark: String get() = t3("Always follow my selection", "استخدام اختياري دائماً", "Toujours utiliser mon choix")

/* ----------------------------------------- Projects --------------------------------------- */

val AppStrings.projNew: String get() = t3("New project", "مشروع جديد", "Nouveau projet")
val AppStrings.projName: String get() = t3("Project name", "اسم المشروع", "Nom du projet")
val AppStrings.projNameHint: String get() = t3("e.g. Mobile app launch", "مثلاً: إطلاق تطبيق", "ex. Lancement d'application")
val AppStrings.projDescription: String get() = t3("Description", "الوصف", "Description")
val AppStrings.projDeadline: String get() = t3("Deadline (YYYY-MM-DD)", "الموعد النهائي (YYYY-MM-DD)", "Échéance (AAAA-MM-JJ)")
val AppStrings.projDateHint: String get() = t3("Leave empty for no deadline", "اتركه فارغاً بدون موعد", "Vide si aucune échéance")
val AppStrings.projCreated: String get() = t3("Project created", "تم إنشاء المشروع", "Projet créé")
val AppStrings.projTasks: String get() = t3("tasks", "مهمة", "tâches")
val AppStrings.projFiles: String get() = t3("files", "ملف", "fichiers")
val AppStrings.projOverdue: String get() = t3("Overdue", "متأخر", "En retard")
val AppStrings.projLate: String get() = t3("late", "تأخير", "retard")
val AppStrings.projBalance: String get() = t3("Balance", "الرصيد", "Solde")
val AppStrings.projActiveFilter: String get() = t3("Active", "نشط", "Actifs")
val AppStrings.projArchived: String get() = t3("Archived", "مؤرشف", "Archivés")
val AppStrings.projNoArchived: String get() = t3("No archived projects", "لا توجد مشاريع مؤرشفة", "Aucun projet archivé")
val AppStrings.projEmptyHint: String get() = t3("Create a project to plan tasks, budgets and deadlines", "أنشئ مشروعاً لتخطيط المهام والميزانيات والمواعيد", "Créez un projet pour planifier tâches, budgets et échéances")
val AppStrings.projTabOverview: String get() = t3("Overview", "نظرة عامة", "Aperçu")
val AppStrings.projTabBoard: String get() = t3("Board", "اللوحة", "Tableau")
val AppStrings.projTabBudget: String get() = t3("Budget", "الميزانية", "Budget")
val AppStrings.projTabNotes: String get() = t3("Notes & files", "ملاحظات وملفات", "Notes & fichiers")
val AppStrings.projTodo: String get() = t3("To do", "قائمة الانتظار", "À faire")
val AppStrings.projDoing: String get() = t3("In progress", "قيد التنفيذ", "En cours")
val AppStrings.projDone: String get() = t3("Done", "منجز", "Terminé")
val AppStrings.projNewTask: String get() = t3("New task", "مهمة جديدة", "Nouvelle tâche")
val AppStrings.projTaskHint: String get() = t3("What needs to be done?", "ما الذي يجب إنجازه؟", "Que faut-il faire ?")
val AppStrings.projMoveNext: String get() = t3("Move to next stage", "نقل للمرحلة التالية", "Étape suivante")
val AppStrings.projAddBudget: String get() = t3("Add budget line", "إضافة بند ميزانية", "Ajouter une ligne")
val AppStrings.projAmount: String get() = t3("Amount", "المبلغ", "Montant")
val AppStrings.projIncome: String get() = t3("Income", "إيراد", "Revenu")
val AppStrings.projExpense: String get() = t3("Expense", "مصروف", "Dépense")
val AppStrings.projSpent: String get() = t3("Spent", "المصروف", "Dépensé")
val AppStrings.projEarned: String get() = t3("Earned", "المكتسب", "Encaissé")
val AppStrings.projExportCsv: String get() = t3("Export CSV", "تصدير CSV", "Exporter CSV")
val AppStrings.projExportPdf: String get() = t3("Export PDF report", "تصدير تقرير PDF", "Exporter rapport PDF")
val AppStrings.projDeleteConfirm: String get() = t3("Delete this project and all its tasks?", "حذف هذا المشروع وكل مهامه؟", "Supprimer ce projet et ses tâches ?")
val AppStrings.projArchive: String get() = t3("Archive project", "أرشفة المشروع", "Archiver le projet")
val AppStrings.projUnarchive: String get() = t3("Restore project", "استعادة المشروع", "Restaurer le projet")
val AppStrings.projSaveChanges: String get() = t3("Save changes", "حفظ التغييرات", "Enregistrer")
val AppStrings.projAttach: String get() = t3("Attach a file", "إرفاق ملف", "Joindre un fichier")
val AppStrings.projNotesHint: String get() = t3("Meeting notes, decisions, links…", "ملاحظات الاجتماعات والقرارات والروابط…", "Notes de réunion, décisions, liens…")
val AppStrings.projReportTitle: String get() = t3("Project report", "تقرير المشروع", "Rapport de projet")
val AppStrings.projNotFound: String get() = t3("Project not found", "المشروع غير موجود", "Projet introuvable")
val AppStrings.gMove: String get() = t3("Move", "نقل", "Déplacer")
val AppStrings.gExport: String get() = t3("Export", "تصدير", "Exporter")
val AppStrings.savedPathPrefix: String get() = t3("Saved to", "تم الحفظ في", "Enregistré dans")
