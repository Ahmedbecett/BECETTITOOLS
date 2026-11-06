/**
 * The 15 tools of v1.0.0, grouped exactly as the in-app Categories screen groups them
 * (app/src/main/java/com/becetti/tools/features/home/HomeScreen.kt → CategoriesScreen).
 * Descriptions are limited to behaviour that exists in the v1.0.0 source.
 */

export const categories = [
  { id: "calculators", icon: "calculator", name: { en: "Calculator & Converters", fr: "Calculs & Convertisseurs", ar: "الحاسبة والتحويلات" } },
  { id: "security", icon: "shield", name: { en: "QR & Security", fr: "QR & Sécurité", ar: "الرموز والأمان" } },
  { id: "documents", icon: "document", name: { en: "Documents & Files", fr: "Documents & Fichiers", ar: "المستندات والملفات" } },
  { id: "images", icon: "image", name: { en: "Image Utilities", fr: "Outils Image", ar: "أدوات الصور" } },
  { id: "text", icon: "text", name: { en: "Text Utilities", fr: "Outils Texte", ar: "أدوات النصوص" } },
  { id: "datetime", icon: "calendar", name: { en: "Date & Time", fr: "Date & Heure", ar: "التاريخ والوقت" } },
];

export const tools = [
  {
    id: "scientific-calculator",
    route: "calc_scientific",
    category: "calculators",
    icon: "calculator",
    name: { en: "Scientific Calculator", fr: "Calculatrice scientifique", ar: "الحاسبة العلمية" },
    desc: {
      en: "Arithmetic, powers, square root, percentages and trigonometry (sin/cos/tan in degrees) plus log and ln. Every result can be kept in a local history.",
      fr: "Opérations, puissances, racine carrée, pourcentages et trigonométrie (sin/cos/tan en degrés), plus log et ln. Chaque résultat peut être conservé dans un historique local.",
      ar: "العمليات الحسابية والقوى والجذر التربيعي والنسب المئوية والمثلثات (sin/cos/tan بالدرجات) مع log و ln. يمكن حفظ كل نتيجة في سجل محلي.",
    },
  },
  {
    id: "unit-converter",
    route: "unit_converter",
    category: "calculators",
    icon: "ruler",
    name: { en: "Unit Converter", fr: "Convertisseur d’unités", ar: "محوّل الوحدات" },
    desc: {
      en: "Six categories — length, weight, temperature, area, volume and speed — with instant two-way conversion.",
      fr: "Six catégories — longueur, poids, température, surface, volume et vitesse — avec conversion instantanée dans les deux sens.",
      ar: "ست فئات — الطول والوزن والحرارة والمساحة والحجم والسرعة — بتحويل فوري في الاتجاهين.",
    },
  },
  {
    id: "currency-converter",
    route: "currency",
    category: "calculators",
    icon: "exchange",
    name: { en: "Currency Converter", fr: "Convertisseur de devises", ar: "محوّل العملات" },
    desc: {
      en: "Reference exchange rates from the Frankfurter API (European Central Bank data) over HTTPS. Rates are cached on the device, with a last-updated timestamp and an offline indicator.",
      fr: "Taux de référence via l’API Frankfurter (données de la Banque centrale européenne) en HTTPS. Les taux sont mis en cache sur l’appareil, avec horodatage et indicateur hors-ligne.",
      ar: "أسعار مرجعية عبر واجهة Frankfurter (بيانات البنك المركزي الأوروبي) باستخدام HTTPS. تُحفظ الأسعار على الجهاز مع وقت آخر تحديث ومؤشر لعدم الاتصال.",
    },
  },
  {
    id: "percentage-calculator",
    route: "percentage",
    category: "calculators",
    icon: "percent",
    name: { en: "Percentage Calculator", fr: "Calcul de pourcentage", ar: "حاسبة النسبة المئوية" },
    desc: {
      en: "Three modes: percentage of a number, percentage increase and percentage decrease.",
      fr: "Trois modes : pourcentage d’un nombre, augmentation et réduction en pourcentage.",
      ar: "ثلاثة أوضاع: نسبة من رقم، وزيادة بنسبة، ونقصان بنسبة.",
    },
  },
  {
    id: "age-calculator",
    route: "age",
    category: "datetime",
    icon: "calendar",
    name: { en: "Age Calculator", fr: "Calculateur d’âge", ar: "حاسبة العمر" },
    desc: {
      en: "Exact age in years, months and days, the next birthday, and the difference between two dates.",
      fr: "Âge exact en années, mois et jours, le prochain anniversaire et la différence entre deux dates.",
      ar: "العمر بدقة بالسنوات والأشهر والأيام، وعيد الميلاد القادم، والفرق بين تاريخين.",
    },
  },
  {
    id: "qr-generator",
    route: "qr_generator",
    category: "security",
    icon: "qr",
    name: { en: "QR Code Generator", fr: "Générateur de QR codes", ar: "مُنشئ رموز QR" },
    desc: {
      en: "Create QR codes for plain text, a URL, a Wi-Fi network (WIFI: SSID/password) or a contact (vCard), then export a PNG to Pictures/Becetti.",
      fr: "Créez des QR codes pour du texte, une URL, un réseau Wi-Fi (WIFI : SSID/mot de passe) ou un contact (vCard), puis exportez un PNG dans Pictures/Becetti.",
      ar: "أنشئ رموز QR لنص عادي أو رابط أو شبكة Wi-Fi (WIFI: الاسم/كلمة المرور) أو جهة اتصال (vCard)، ثم صدّرها كصورة PNG إلى Pictures/Becetti.",
    },
  },
  {
    id: "qr-scanner",
    route: "qr_scanner",
    category: "security",
    icon: "scan",
    name: { en: "QR Code Scanner", fr: "Scanner de QR codes", ar: "ماسح رموز QR" },
    desc: {
      en: "CameraX preview with on-device ML Kit barcode decoding. Camera permission is requested only when you open the scanner, and links open only after you confirm.",
      fr: "Aperçu CameraX et décodage des codes-barres par ML Kit sur l’appareil. L’autorisation caméra n’est demandée qu’à l’ouverture du scanner, et un lien ne s’ouvre qu’après confirmation.",
      ar: "معاينة بالكاميرا (CameraX) وقراءة الرموز بواسطة ML Kit على الجهاز. يُطلب إذن الكاميرا فقط عند فتح الماسح، ولا يُفتح أي رابط إلا بعد تأكيدك.",
    },
  },
  {
    id: "password-generator",
    route: "password_gen",
    category: "security",
    icon: "key",
    name: { en: "Password Generator", fr: "Générateur de mots de passe", ar: "مولّد كلمات المرور" },
    desc: {
      en: "Passwords of 4 to 64 characters from SecureRandom, with independent toggles for upper case, lower case, digits and symbols. Nothing is stored or uploaded.",
      fr: "Mots de passe de 4 à 64 caractères via SecureRandom, avec options indépendantes majuscules, minuscules, chiffres et symboles. Rien n’est stocké ni envoyé.",
      ar: "كلمات مرور من 4 إلى 64 حرفًا باستخدام SecureRandom مع مفاتيح مستقلة للأحرف الكبيرة والصغيرة والأرقام والرموز. لا يتم تخزين أو إرسال أي شيء.",
    },
  },
  {
    id: "password-strength",
    route: "password_strength",
    category: "security",
    icon: "gauge",
    name: { en: "Password Strength Checker", fr: "Test de robustesse", ar: "فاحص قوة كلمة المرور" },
    desc: {
      en: "A local 0–5 analysis of length and character variety. The password never leaves the device and is never written to the database.",
      fr: "Analyse locale de 0 à 5 selon la longueur et la variété des caractères. Le mot de passe ne quitte jamais l’appareil et n’est jamais écrit en base.",
      ar: "تحليل محلي من 0 إلى 5 بناءً على الطول وتنوع المحارف. لا تغادر كلمة المرور الجهاز ولا تُكتب في قاعدة البيانات أبدًا.",
    },
  },
  {
    id: "image-to-pdf",
    route: "image_to_pdf",
    category: "documents",
    icon: "imagePdf",
    name: { en: "Image to PDF", fr: "Image en PDF", ar: "تحويل الصور إلى PDF" },
    desc: {
      en: "Pick several images with the Android file picker, keep them in selection order and export a PDF (A4, A3 or Letter) to Documents/Becetti. Your originals are untouched.",
      fr: "Sélectionnez plusieurs images avec le sélecteur Android, gardez l’ordre choisi et exportez un PDF (A4, A3 ou Letter) dans Documents/Becetti. Les originaux restent intacts.",
      ar: "اختر عدة صور عبر منتقي ملفات أندرويد واحفظ ترتيب الاختيار ثم صدّر ملف PDF (A4 أو A3 أو Letter) إلى Documents/Becetti، مع الحفاظ على الصور الأصلية.",
    },
  },
  {
    id: "pdf-merger",
    route: "pdf_merger",
    category: "documents",
    icon: "pdf",
    name: { en: "PDF Merger", fr: "Fusion de PDF", ar: "دمج ملفات PDF" },
    desc: {
      en: "Combine several PDFs in selection order using PDFBox-Android and save the merged file to Documents/Becetti.",
      fr: "Combinez plusieurs PDF dans l’ordre sélectionné avec PDFBox-Android et enregistrez le fichier fusionné dans Documents/Becetti.",
      ar: "ادمج عدة ملفات PDF بترتيب الاختيار باستخدام PDFBox-Android واحفظ الملف المدموج في Documents/Becetti.",
    },
  },
  {
    id: "ocr",
    route: "ocr",
    category: "documents",
    icon: "ocr",
    name: { en: "OCR Text Extractor", fr: "Extraction de texte (OCR)", ar: "استخراج النص (OCR)" },
    desc: {
      en: "Read text out of a photo or screenshot with on-device ML Kit text recognition (Latin script: French, English and similar), then copy it out.",
      fr: "Extrayez le texte d’une photo ou d’une capture avec la reconnaissance de texte ML Kit sur l’appareil (écriture latine : français, anglais et proches), puis copiez-le.",
      ar: "استخرج النص من صورة أو لقطة شاشة عبر التعرف الضوئي من ML Kit على الجهاز (النص اللاتيني: الفرنسية والإنجليزية وما شابهها)، ثم انسخه.",
    },
  },
  {
    id: "image-compressor",
    route: "image_compressor",
    category: "images",
    icon: "compress",
    name: { en: "Image Compressor", fr: "Compresseur d’images", ar: "ضاغط الصور" },
    desc: {
      en: "A quality slider from 10 to 100 with a live preview, saving the result as JPEG to Pictures/Becetti.",
      fr: "Un curseur de qualité de 10 à 100 avec aperçu, enregistrant le résultat en JPEG dans Pictures/Becetti.",
      ar: "شريط جودة من 10 إلى 100 مع معاينة، ويُحفظ الناتج بصيغة JPEG في Pictures/Becetti.",
    },
  },
  {
    id: "text-toolkit",
    route: "text_toolkit",
    category: "text",
    icon: "text",
    name: { en: "Text Toolkit", fr: "Boîte à outils texte", ar: "صندوق أدوات النص" },
    desc: {
      en: "Word and character counts (with and without spaces), UPPER/lower case, duplicate-space cleanup, empty-line removal and reverse.",
      fr: "Comptage des mots et caractères (avec et sans espaces), MAJUSCULES/minuscules, nettoyage des espaces doubles, suppression des lignes vides et inversion.",
      ar: "عدّ الكلمات والمحارف (مع وبدون مسافات)، وتحويل إلى أحرف كبيرة/صغيرة، وتنظيف المسافات المكررة، وحذف الأسطر الفارغة، وعكس النص.",
    },
  },
  {
    id: "notes",
    route: "notes",
    category: "text",
    icon: "note",
    name: { en: "Notes Manager", fr: "Gestionnaire de notes", ar: "مدير الملاحظات" },
    desc: {
      en: "Create, edit, search and delete notes in a local Room database. Notes never leave the device and there is no account.",
      fr: "Créez, modifiez, recherchez et supprimez des notes dans une base Room locale. Les notes ne quittent jamais l’appareil et aucun compte n’est requis.",
      ar: "أنشئ الملاحظات وحرّرها وابحث فيها واحذفها في قاعدة بيانات Room محلية. لا تغادر الملاحظات الجهاز ولا حاجة إلى حساب.",
    },
  },
];
