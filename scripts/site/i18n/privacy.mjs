/**
 * Public privacy policy, published at /privacy/ (+ /privacy/fr/, /privacy/ar/).
 * Statements are derived from the v1.0.0 source: Room DAOs, DataStore keys,
 * AndroidManifest permissions, SAF usage, api.frankfurter.app, AdsManager/AdPolicy.
 */

export const privacy = {
  en: {
    title: "Privacy Policy — BECETTI TOOLS",
    metaTitle: "Privacy Policy — BECETTI TOOLS (com.becetti.tools)",
    metaDescription:
      "How BECETTI TOOLS v1.0.0 handles your data: local-only notes and history, on-device QR and OCR processing, Storage Access Framework file access, the currency API, permissions and advertising consent.",
    heading: "Privacy Policy",
    appLine: "Applies to BECETTI TOOLS 1.0.0 for Android (package com.becetti.tools)",
    effective: "Effective 6 November 2026",
    updated: "Last updated 6 November 2026",
    summaryTitle: "The short version",
    summary:
      "BECETTI TOOLS is an offline-first utility app with no account, no analytics and no server of its own. The things you write, scan or convert stay on your device. The only network requests the app can make are for reference currency rates, and — if you tap it yourself — an optional sponsor link.",
    toc: "On this page",
    sections: [
      {
        id: "scope",
        heading: "1. Scope and controller",
        paragraphs: [
          "This policy covers the Android application BECETTI TOOLS, published by Ahmed Becetti, distributed as an APK from the GitHub repository Ahmedbecett/BECETTITOOLS. It describes what the v1.0.0 build does, not what a future version might do; if a later version changes behaviour, this page and the in-app text will be updated first.",
          "The source code is public and MIT-licensed, so every statement below can be checked against the implementation.",
        ],
      },
      {
        id: "on-device",
        heading: "2. Data stored on your device (and nowhere else)",
        paragraphs: [
          "The app keeps a small amount of information locally so it works well without a connection. It is stored in an on-device database (Room/SQLite) and Android DataStore preferences. Nothing in this list is uploaded to us or to a third party:",
        ],
        list: [
          "Notes you create, in a local database.",
          "Favourites, recently used tools and calculator history, so lists and history survive a restart.",
          "Your chosen interface language, theme and advertising-consent flag.",
          "Cached currency rates with the date and time they were fetched, so the converter can work offline.",
        ],
        after: [
          "You can clear calculator history from the History screen and manage favourites in the app. Uninstalling the app deletes the database and preferences with it, because no copy exists anywhere else.",
        ],
      },
      {
        id: "no-collection",
        heading: "3. What we do not collect",
        list: [
          "No account, name, email address or phone number is required or requested.",
          "No analytics, crash-reporting or attribution SDK is included in v1.0.0.",
          "No advertising identifier is read or transmitted.",
          "Passwords you generate or test are never stored, logged or uploaded — generation uses the platform's cryptographically secure random number generator, and the strength check is a local calculation.",
          "Text you extract with OCR, images you convert and codes you scan are processed on the device and are not transmitted.",
        ],
      },
      {
        id: "permissions",
        heading: "4. Permissions and when they are used",
        list: [
          "Camera (android.permission.CAMERA): requested only when you open the QR scanner, with an explanation shown first. Frames are decoded on the device and are not saved. If you deny it, the rest of the app continues to work.",
          "Internet and network state: used for the currency converter's rate update and, only if you tap the sponsor row, to open an external page in your browser.",
          "Photos/media access (READ_MEDIA_IMAGES and READ_MEDIA_VISUAL_USER_SELECTED on Android 13+): declared so image tools can open the system picker and so saved images can be written through MediaStore. The app does not scan or index your photo library.",
          "No location, contacts, microphone, SMS, phone-state, calendar or background-location permission is requested. No permission is requested at first launch.",
        ],
      },
      {
        id: "files",
        heading: "5. Files and storage",
        paragraphs: [
          "Files are chosen through the Android Storage Access Framework — the system file picker. The app receives access only to the specific documents you pick, and cannot browse the rest of your storage.",
          "Images and PDFs you select are processed locally. Created files are written to public collections: PDFs to Documents/Becetti and images to Pictures/Becetti. Your original files are never modified or deleted.",
        ],
      },
      {
        id: "ocr",
        heading: "6. OCR and barcode recognition",
        paragraphs: [
          "Text recognition and barcode decoding use Google ML Kit running on the device, with recognition models bundled inside the APK. The images and camera frames are processed in memory on the phone; they are not uploaded to a recognition service and are not retained by the app.",
          "The text that results from OCR stays in the app until you copy it or leave the screen. Extracted text is not added to your notes automatically.",
        ],
      },
      {
        id: "currency",
        heading: "7. The currency exchange API",
        paragraphs: [
          "When you open the currency converter with a connection available, the app requests reference rates from api.frankfurter.app over HTTPS. The request contains the currency codes being converted — for example EUR and USD — and nothing about you: no name, account, device identifier or precise location.",
          "Responses are cached on the device with a timestamp. With no connection the converter shows the cached rates with their date and an offline indicator. Frankfurter publishes European Central Bank reference rates; its own terms apply to those requests.",
        ],
      },
      {
        id: "ads",
        heading: "8. Advertising and consent",
        paragraphs: [
          "v1.0.0 ships no active advertising SDK. The monetisation layer is an abstraction: the Monetag adapter is initialised in a no-SDK state and reports itself as unavailable, and the Adsterra integration is limited to an optional, clearly labelled “Sponsored Offers” row that opens an external link only after you tap it. There is no automatic redirect, no pop-under and no interstitial that opens by itself.",
          "The app also enforces its own limits in code: no advertising is shown on the calculator screen, no interstitial is forced before a tool, and a frequency cap of 90 seconds and three interstitial impressions per session applies. A consent flag is stored locally and can be changed in Settings.",
          "If an advertising SDK is enabled in a future version, this policy will be updated before release, consent will be requested where the law requires it, and the change will be noted in the release notes.",
        ],
      },
      {
        id: "security",
        heading: "9. Security",
        paragraphs: [
          "Local data is protected by Android's application sandbox and by your device's own lock screen and encryption. The app communicates over HTTPS only. No API keys, publisher secrets or credentials are embedded in the APK.",
          "Keep in mind that the v1.0.0 APK is a debug build: it is debuggable, which is one more reason to install it for evaluation rather than for sensitive work.",
        ],
      },
      {
        id: "rights",
        heading: "10. Your controls",
        list: [
          "Clear calculator history from the History screen.",
          "Change or withdraw advertising consent in Settings.",
          "Revoke camera or media permissions at any time in Android Settings.",
          "Delete everything by uninstalling the app — there is no server-side copy to request or erase.",
        ],
      },
      {
        id: "children",
        heading: "11. Children",
        paragraphs: [
          "The app is a general-purpose utility and is not directed at children. It collects no personal data from anyone, including children.",
        ],
      },
      {
        id: "changes",
        heading: "12. Changes to this policy",
        paragraphs: [
          "When the app's behaviour changes, this policy is updated before the release is published, with a new effective date. Substantial changes will also be described in the release notes on GitHub.",
        ],
      },
      {
        id: "contact",
        heading: "13. Contact",
        paragraphs: [
          "Questions about this policy or about data handling can be raised in the project's public issue tracker, or by email to the address shown below. Please do not post personal notes or password screenshots in public issues.",
        ],
      },
      {
        id: "site-ads",
        heading: "14. Advertising on this website",
        paragraphs: [
          "This page is not the app. The website shows sponsored messages served by Monetag (zone 12206566, script loaded from nap5k.com). The script is never part of the HTML: it is fetched only after you press \u201cAccept ads\u201d in the consent bar, and choosing Decline leaves the page loading nothing from that host.",
          "Your choice is stored in your browser under the local key becetti-ads-consent and is never transmitted to us \u2014 there is no server that receives it. Once the tag is allowed to load, Monetag may set its own cookies or identifiers and may display in-page sponsored messages; that processing is covered by Monetag's own privacy policy, not by this one.",
          "You can change your choice at any time with the \u201cAd settings\u201d link in the footer. Advertisements are never disguised as download buttons, never open without a click, never appear on this privacy page, and never replace or delay the APK download. One unlinked page, /ads-verify/, exists only so the advertising network can confirm the tag; it is excluded from the sitemap and from search results.",
        ],
      },
    ],
    backToLanding: "Back to the download page",
    relatedTitle: "Related",
    relatedLinks: [
      { label: "Download and installation", href: "landing" },
      { label: "Project repository", href: "repo" },
      { label: "Report an issue", href: "issues" },
    ],
    disclaimer:
      "This page describes the published v1.0.0 build. It is a good-faith technical disclosure, not legal advice; if you distribute the app commercially, have it reviewed by counsel.",
  },

  fr: {
    title: "Politique de confidentialité — BECETTI TOOLS",
    metaTitle: "Politique de confidentialité — BECETTI TOOLS (com.becetti.tools)",
    metaDescription:
      "Comment BECETTI TOOLS v1.0.0 traite vos données : notes et historique locaux, traitement QR et OCR sur l’appareil, accès aux fichiers via le Storage Access Framework, API de devises, autorisations et consentement publicitaire.",
    heading: "Politique de confidentialité",
    appLine: "S’applique à BECETTI TOOLS 1.0.0 pour Android (paquet com.becetti.tools)",
    effective: "Entrée en vigueur le 6 novembre 2026",
    updated: "Dernière mise à jour le 6 novembre 2026",
    summaryTitle: "En bref",
    summary:
      "BECETTI TOOLS est une application utilitaire prioritairement hors ligne, sans compte, sans analytique et sans serveur propre. Ce que vous écrivez, scannez ou convertissez reste sur votre appareil. Les seules requêtes réseau sont celles des taux de change de référence et, si vous le touchez vous-même, un lien sponsor optionnel.",
    toc: "Sur cette page",
    sections: [
      {
        id: "scope",
        heading: "1. Portée et responsable",
        paragraphs: [
          "Cette politique couvre l’application Android BECETTI TOOLS, publiée par Ahmed Becetti et distribuée sous forme d’APK depuis le dépôt GitHub Ahmedbecett/BECETTITOOLS. Elle décrit le comportement de la version 1.0.0, non celui d’une version future ; si une version ultérieure change ce comportement, cette page et les textes intégrés à l’application seront mis à jour au préalable.",
          "Le code source est public et sous licence MIT : chaque affirmation ci-dessous peut être vérifiée dans l’implémentation.",
        ],
      },
      {
        id: "on-device",
        heading: "2. Données stockées sur votre appareil (et nulle part ailleurs)",
        paragraphs: [
          "L’application conserve localement un peu d’informations pour bien fonctionner sans connexion. Elles résident dans une base de données sur l’appareil (Room/SQLite) et dans les préférences DataStore d’Android. Rien de cette liste n’est transmis à nous ou à un tiers :",
        ],
        list: [
          "Les notes que vous créez, dans une base locale.",
          "Les favoris, les outils récemment utilisés et l’historique de la calculatrice, pour qu’ils survivent à un redémarrage.",
          "La langue d’interface choisie, le thème et l’indicateur de consentement publicitaire.",
          "Les taux de change mis en cache, avec la date et l’heure de leur récupération, pour que le convertisseur fonctionne hors ligne.",
        ],
        after: [
          "Vous pouvez effacer l’historique de calcul depuis l’écran Historique et gérer vos favoris dans l’application. La désinstallation supprime la base et les préférences, car aucune copie n’existe ailleurs.",
        ],
      },
      {
        id: "no-collection",
        heading: "3. Ce que nous ne collectons pas",
        list: [
          "Aucun compte, nom, e-mail ou numéro de téléphone n’est demandé.",
          "Aucun SDK d’analytique, de rapport de plantage ou d’attribution n’est inclus dans la v1.0.0.",
          "Aucun identifiant publicitaire n’est lu ni transmis.",
          "Les mots de passe générés ou testés ne sont jamais stockés, journalisés ou envoyés — la génération utilise le générateur aléatoire cryptographique de la plateforme et le test de robustesse est un calcul local.",
          "Le texte extrait par OCR, les images converties et les codes scannés sont traités sur l’appareil et ne sont pas transmis.",
        ],
      },
      {
        id: "permissions",
        heading: "4. Autorisations et moment de leur usage",
        list: [
          "Caméra (android.permission.CAMERA) : demandée uniquement à l’ouverture du scanner QR, après une explication. Les images sont décodées sur l’appareil et ne sont pas enregistrées. En cas de refus, le reste de l’application continue de fonctionner.",
          "Internet et état du réseau : utilisés pour la mise à jour des taux du convertisseur de devises et, seulement si vous touchez la ligne sponsor, pour ouvrir une page externe dans votre navigateur.",
          "Accès aux photos/médias (READ_MEDIA_IMAGES et READ_MEDIA_VISUAL_USER_SELECTED sur Android 13+) : déclarés pour que les outils d’image ouvrent le sélecteur système et pour écrire les images via MediaStore. L’application ne parcourt ni n’indexe votre bibliothèque.",
          "Aucune autorisation de localisation, contacts, micro, SMS, état du téléphone, calendrier ou localisation en arrière-plan n’est demandée. Aucune autorisation n’est demandée au premier lancement.",
        ],
      },
      {
        id: "files",
        heading: "5. Fichiers et stockage",
        paragraphs: [
          "Les fichiers sont choisis via le Storage Access Framework d’Android — le sélecteur de fichiers système. L’application n’obtient l’accès qu’aux documents que vous désignez et ne peut pas parcourir le reste de votre stockage.",
          "Les images et PDF sélectionnés sont traités localement. Les fichiers créés sont écrits dans des collections publiques : les PDF dans Documents/Becetti et les images dans Pictures/Becetti. Vos fichiers d’origine ne sont jamais modifiés ni supprimés.",
        ],
      },
      {
        id: "ocr",
        heading: "6. OCR et reconnaissance de codes",
        paragraphs: [
          "La reconnaissance de texte et le décodage de codes-barres utilisent Google ML Kit exécuté sur l’appareil, avec les modèles de reconnaissance embarqués dans l’APK. Les images et les images de caméra sont traitées en mémoire sur le téléphone : elles ne sont pas envoyées à un service de reconnaissance et ne sont pas conservées par l’application.",
          "Le texte issu de l’OCR reste dans l’application jusqu’à ce que vous le copiiez ou quittiez l’écran. Il n’est pas ajouté automatiquement à vos notes.",
        ],
      },
      {
        id: "currency",
        heading: "7. L’API de taux de change",
        paragraphs: [
          "Lorsque vous ouvrez le convertisseur de devises avec une connexion disponible, l’application demande les taux de référence à api.frankfurter.app en HTTPS. La requête contient les codes de devises convertis — par exemple EUR et USD — et rien sur vous : ni nom, ni compte, ni identifiant d’appareil, ni localisation précise.",
          "Les réponses sont mises en cache sur l’appareil avec un horodatage. Sans connexion, le convertisseur affiche les taux en cache avec leur date et un indicateur hors-ligne. Frankfurter publie les taux de référence de la Banque centrale européenne ; ses propres conditions s’appliquent à ces requêtes.",
        ],
      },
      {
        id: "ads",
        heading: "8. Publicité et consentement",
        paragraphs: [
          "La v1.0.0 n’embarque aucune régie publicitaire active. La couche de monétisation est une abstraction : l’adaptateur Monetag s’initialise sans SDK et se déclare indisponible, et l’intégration Adsterra se limite à une ligne « Offres sponsorisées » facultative et clairement étiquetée, qui n’ouvre un lien externe qu’après un appui. Aucune redirection automatique, aucun pop-under, aucun interstitiel ne s’ouvre seul.",
          "L’application applique aussi ses propres règles dans le code : aucune publicité sur l’écran calculatrice, aucun interstitiel imposé avant un outil, et un plafond de 90 secondes et de trois interstitiels par session. Un indicateur de consentement est stocké localement et modifiable dans les réglages.",
          "Si une régie publicitaire est activée dans une version future, cette politique sera mise à jour avant la publication, le consentement sera demandé lorsque la loi l’exige, et le changement sera indiqué dans les notes de version.",
        ],
      },
      {
        id: "security",
        heading: "9. Sécurité",
        paragraphs: [
          "Les données locales sont protégées par le bac à sable applicatif d’Android ainsi que par le verrouillage et le chiffrement de votre appareil. L’application ne communique qu’en HTTPS. Aucune clé d’API, aucun secret d’éditeur et aucun identifiant ne sont intégrés à l’APK.",
          "Rappel : l’APK v1.0.0 est une version debug, donc débuggable — une raison supplémentaire de l’installer pour évaluation plutôt que pour des usages sensibles.",
        ],
      },
      {
        id: "rights",
        heading: "10. Vos moyens d’action",
        list: [
          "Effacer l’historique de calcul depuis l’écran Historique.",
          "Modifier ou retirer le consentement publicitaire dans les réglages.",
          "Révoquer à tout moment les autorisations caméra ou médias dans les réglages Android.",
          "Tout supprimer en désinstallant l’application — aucune copie serveur à demander ou à effacer.",
        ],
      },
      {
        id: "children",
        heading: "11. Enfants",
        paragraphs: [
          "L’application est un utilitaire généraliste qui ne s’adresse pas spécifiquement aux enfants. Elle ne collecte aucune donnée personnelle, y compris auprès des enfants.",
        ],
      },
      {
        id: "changes",
        heading: "12. Modifications de cette politique",
        paragraphs: [
          "Lorsque le comportement de l’application change, cette politique est mise à jour avant la publication de la version, avec une nouvelle date d’entrée en vigueur. Les changements importants sont également décrits dans les notes de version sur GitHub.",
        ],
      },
      {
        id: "contact",
        heading: "13. Contact",
        paragraphs: [
          "Les questions relatives à cette politique ou au traitement des données peuvent être posées dans le suivi public des tickets du projet, ou par e-mail à l’adresse ci-dessous. Merci de ne pas publier de notes personnelles ni de captures de mots de passe dans les tickets.",
        ],
      },
      {
        id: "site-ads",
        heading: "14. Publicité sur ce site",
        paragraphs: [
          "Cette page n’est pas l’application. Le site affiche des messages sponsorisés servis par Monetag (zone 12206566, script chargé depuis nap5k.com). Le script ne fait jamais partie du HTML : il n’est récupéré qu’après un clic sur « Accepter » dans la barre de consentement, et un refus laisse la page sans aucune requête vers cet hôte.",
          "Votre choix est stocké dans votre navigateur sous la clé locale becetti-ads-consent et ne nous est jamais transmis — aucun serveur ne le reçoit. Une fois le tag autorisé, Monetag peut déposer ses propres cookies ou identifiants et afficher des messages sponsorisés dans la page ; ce traitement relève de la politique de confidentialité de Monetag, pas de celle-ci.",
          "Vous pouvez changer d’avis à tout moment via le lien « Réglages des annonces » en pied de page. Une publicité n’est jamais déguisée en bouton de téléchargement, ne s’ouvre jamais sans clic, n’apparaît jamais sur cette page et ne remplace ni ne retarde le téléchargement de l’APK. Une page non liée, /ads-verify/, existe uniquement pour que la régie publicitaire puisse confirmer le tag ; elle est exclue du sitemap et des résultats de recherche.",
        ],
      },
    ],
    backToLanding: "Retour à la page de téléchargement",
    relatedTitle: "Voir aussi",
    relatedLinks: [
      { label: "Téléchargement et installation", href: "landing" },
      { label: "Dépôt du projet", href: "repo" },
      { label: "Signaler un problème", href: "issues" },
    ],
    disclaimer:
      "Cette page décrit la version 1.0.0 publiée. C’est une divulgation technique de bonne foi, pas un avis juridique ; en cas de distribution commerciale, faites-la relire par un juriste.",
  },

  ar: {
    title: "سياسة الخصوصية — أدوات بي سيتي",
    metaTitle: "سياسة الخصوصية — أدوات بي سيتي (com.becetti.tools)",
    metaDescription:
      "كيف يتعامل تطبيق أدوات بي سيتي 1.0.0 مع بياناتك: ملاحظات وسجل محلي فقط، معالجة QR واستخراج النص على الجهاز، الوصول للملفات عبر إطار التخزين، واجهة أسعار العملات، الأذونات وموافقة الإعلانات.",
    heading: "سياسة الخصوصية",
    appLine: "تنطبق على أدوات بي سيتي 1.0.0 لأندرويد (الحزمة com.becetti.tools)",
    effective: "سارية من 6 نوفمبر 2026",
    updated: "آخر تحديث 6 نوفمبر 2026",
    summaryTitle: "الخلاصة",
    summary:
      "أدوات بي سيتي تطبيق أدوات يعمل دون اتصال أولًا، بلا حساب وبلا تحليلات وبلا خادم خاص به. ما تكتبه أو تمسحه أو تحوّله يبقى على جهازك. الطلبات الشبكية الوحيدة هي أسعار العملات المرجعية، ورابط راعٍ اختياري إن لمسته بنفسك.",
    toc: "في هذه الصفحة",
    sections: [
      {
        id: "scope",
        heading: "1. النطاق والمسؤول",
        paragraphs: [
          "تشمل هذه السياسة تطبيق أندرويد «أدوات بي سيتي» المنشور من أحمد بيسيتي والموزَّع كملف APK عبر مستودع GitHub باسم Ahmedbecett/BECETTITOOLS. وهي تصف سلوك الإصدار 1.0.0 لا ما قد يفعله إصدار لاحق؛ وإذا تغيّر السلوك في نسخة قادمة فسيُحدَّث هذا النص ونصوص التطبيق قبل النشر.",
          "الشيفرة المصدرية علنية وبرخصة MIT، لذا يمكن التحقق من كل عبارة أدناه مقابل التنفيذ الفعلي.",
        ],
      },
      {
        id: "on-device",
        heading: "2. بيانات تُخزَّن على جهازك (ولا مكان آخر)",
        paragraphs: [
          "يحتفظ التطبيق بقدر بسيط من المعلومات محليًا ليعمل جيدًا دون اتصال، وتُخزَّن في قاعدة بيانات على الجهاز (Room/SQLite) وفي تفضيلات DataStore في أندرويد. لا يُرفع أي عنصر من هذه القائمة إلينا أو إلى طرف ثالث:",
        ],
        list: [
          "الملاحظات التي تنشئها، في قاعدة بيانات محلية.",
          "المفضلة والأدوات المستخدمة حديثًا وسجل الحاسبة، لتبقى بعد إعادة التشغيل.",
          "لغة الواجهة والمظهر وعلامة موافقة الإعلانات التي اخترتها.",
          "أسعار العملات المحفوظة مع تاريخ ووقت جلبها، ليعمل المحوّل دون اتصال.",
        ],
        after: [
          "يمكنك محو سجل الحاسبة من شاشة السجل وإدارة المفضلة داخل التطبيق. إزالة التطبيق تحذف قاعدة البيانات والتفضيلات معه، لأنه لا توجد نسخة في أي مكان آخر.",
        ],
      },
      {
        id: "no-collection",
        heading: "3. ما لا نجمعه",
        list: [
          "لا يُطلب حساب أو اسم أو بريد إلكتروني أو رقم هاتف.",
          "لا تتضمن النسخة 1.0.0 أي حزمة تحليلات أو تقارير أعطال أو إحالة.",
          "لا يُقرأ أي معرّف إعلاني ولا يُرسَل.",
          "كلمات المرور التي تولّدها أو تفحصها لا تُخزَّن ولا تُسجَّل ولا تُرسَل — التوليد يستخدم مولّد الأرقام العشوائية الآمن في النظام، وفحص القوة حساب محلي.",
          "النص المستخرج بالـ OCR والصور المحوَّلة والرموز الممسوحة تُعالج على الجهاز ولا تُرسَل.",
        ],
      },
      {
        id: "permissions",
        heading: "4. الأذونات ووقت استخدامها",
        list: [
          "الكاميرا (android.permission.CAMERA): تُطلب فقط عند فتح ماسح الرموز، مع شرح مسبق. تُقرأ الإطارات على الجهاز ولا تُحفظ. وإذا رفضت، يستمر بقية التطبيق في العمل.",
          "الإنترنت وحالة الشبكة: لتحديث أسعار العملات، ولأن يفتح المتصفح صفحة خارجية فقط إذا لمست سطر الراعي.",
          "الوصول إلى الصور والوسائط (READ_MEDIA_IMAGES و READ_MEDIA_VISUAL_USER_SELECTED في أندرويد 13+): مُعلَن ليتمكن منتقي النظام من العمل وليُكتب ما تحفظه عبر MediaStore. لا يتصفّح التطبيق مكتبة صورك ولا يفهرسها.",
          "لا يُطلب أي إذن للموقع أو جهات الاتصال أو الميكروفون أو الرسائل أو حالة الهاتف أو التقويم أو الموقع في الخلفية. ولا يُطلب أي إذن عند أول تشغيل.",
        ],
      },
      {
        id: "files",
        heading: "5. الملفات والتخزين",
        paragraphs: [
          "تُختار الملفات عبر إطار الوصول إلى التخزين في أندرويد — منتقي الملفات في النظام. يحصل التطبيق على وصول إلى المستندات التي تختارها فقط، ولا يمكنه تصفّح بقية تخزينك.",
          "الصور وملفات PDF التي تختارها تُعالج محليًا. تُكتب الملفات الناتجة في مجلدات عامة: ملفات PDF في Documents/Becetti والصور في Pictures/Becetti. لا تُعدَّل ملفاتك الأصلية ولا تُحذف.",
        ],
      },
      {
        id: "ocr",
        heading: "6. استخراج النص وقراءة الرموز",
        paragraphs: [
          "يستخدم التعرف على النص وقراءة الرموز مكتبة Google ML Kit على الجهاز، مع نماذج التعرف المضمّنة داخل ملف APK. تُعالج الصور وإطارات الكاميرا في الذاكرة على الهاتف، ولا تُرسَل إلى خدمة تعرّف ولا يحتفظ بها التطبيق.",
          "يبقى النص المستخرج داخل التطبيق حتى تنسخه أو تخرج من الشاشة، ولا يُضاف تلقائيًا إلى ملاحظاتك.",
        ],
      },
      {
        id: "currency",
        heading: "7. واجهة أسعار العملات",
        paragraphs: [
          "عند فتح محوّل العملات مع توفر اتصال، يطلب التطبيق الأسعار المرجعية من api.frankfurter.app عبر HTTPS. يحتوي الطلب على رموز العملات المحوَّلة — مثل EUR و USD — ولا شيء عنك: لا اسم ولا حساب ولا معرّف جهاز ولا موقع دقيق.",
          "تُحفظ الردود على الجهاز مع وقتها. ودون اتصال يعرض المحوّل الأسعار المحفوظة مع تاريخها ومؤشر عدم الاتصال. تنشر Frankfurter أسعار البنك المركزي الأوروبي المرجعية، وتسري شروطها هي على تلك الطلبات.",
        ],
      },
      {
        id: "ads",
        heading: "8. الإعلانات والموافقة",
        paragraphs: [
          "لا تتضمن النسخة 1.0.0 أي حزمة إعلانية نشطة. طبقة الربح عبارة عن تجريد: يبدأ محوّل Monetag دون حزمة ويعلن عدم توفّره، ويقتصر تكامل Adsterra على سطر «عروض برعاية» اختياري وموسوم بوضوح لا يفتح رابطًا خارجيًا إلا بعد لمسك. لا إعادة توجيه تلقائية ولا نوافذ منبثقة ولا إعلان بيني يفتح وحده.",
          "كما يفرض التطبيق حدوده في الشيفرة: لا إعلانات في شاشة الحاسبة، ولا إعلان بيني قبل أي أداة، مع حدّ فاصل 90 ثانية وثلاث مرات في الجلسة. تُخزَّن موافقة الإعلانات محليًا ويمكن تغييرها من الإعدادات.",
          "إذا فُعِّلت حزمة إعلانية في نسخة قادمة، فستُحدَّث هذه السياسة قبل النشر، وستُطلب الموافقة حيث يوجب القانون، وسيُذكر التغيير في ملاحظات الإصدار.",
        ],
      },
      {
        id: "security",
        heading: "9. الأمان",
        paragraphs: [
          "تُحمى البيانات المحلية بصندوق تطبيقات أندرويد وبقفل الشاشة والتشفير في جهازك. ويتصل التطبيق عبر HTTPS فقط. لا توجد مفاتيح API ولا أسرار ناشر ولا بيانات اعتماد مضمّنة في ملف APK.",
          "تذكّر أن ملف الإصدار 1.0.0 نسخة تجريبية قابلة للتصحيح، وهذا سبب إضافي لتثبيتها للتجربة لا للاستخدامات الحساسة.",
        ],
      },
      {
        id: "rights",
        heading: "10. أدوات التحكم لديك",
        list: [
          "محو سجل الحاسبة من شاشة السجل.",
          "تعديل موافقة الإعلانات أو سحبها من الإعدادات.",
          "سحب أذونات الكاميرا أو الوسائط في أي وقت من إعدادات أندرويد.",
          "حذف كل شيء بإزالة التطبيق — لا توجد نسخة على خادم لطلبها أو محوها.",
        ],
      },
      {
        id: "children",
        heading: "11. الأطفال",
        paragraphs: [
          "التطبيق أداة عامة غير موجَّهة للأطفال، ولا يجمع أي بيانات شخصية من أي شخص، بما في ذلك الأطفال.",
        ],
      },
      {
        id: "changes",
        heading: "12. تغييرات هذه السياسة",
        paragraphs: [
          "عند تغيّر سلوك التطبيق تُحدَّث هذه السياسة قبل نشر الإصدار مع تاريخ سريان جديد، وتُوصف التغييرات الجوهرية أيضًا في ملاحظات الإصدار على GitHub.",
        ],
      },
      {
        id: "contact",
        heading: "13. التواصل",
        paragraphs: [
          "يمكن طرح الأسئلة حول هذه السياسة أو معالجة البيانات في متتبّع البلاغات العام للمشروع، أو عبر البريد الإلكتروني أدناه. يُرجى عدم نشر ملاحظات شخصية أو صور لكلمات مرور في البلاغات العامة.",
        ],
      },
      {
        id: "site-ads",
        heading: "14. الإعلانات على هذا الموقع",
        paragraphs: [
          "هذه الصفحة ليست التطبيق. يعرض الموقع رسائل مدعومة من Monetag (المنطقة 12206566، والسكربت يُحمَّل من nap5k.com). السكربت ليس جزءًا من ملف HTML أبدًا: لا يُجلب إلا بعد ضغطك على «أوافق» في شريط الموافقة، ورفضك يُبقي الصفحة دون أي طلب إلى ذلك النطاق.",
          "يُخزَّن اختيارك في متصفحك تحت المفتاح المحلي becetti-ads-consent ولا يُرسل إلينا إطلاقًا — لا يوجد خادم يستقبله. وبعد السماح بتحميل الوسم، قد تضع Monetag ملفات تعريف أو معرّفات خاصة بها وتعرض رسائل مدعومة داخل الصفحة، وهذا المعالجة تخضع لسياسة خصوصية Monetag لا لهذه الصفحة.",
          "يمكنك تغيير اختيارك في أي وقت عبر رابط «إعدادات الإعلانات» في تذييل الصفحة. لا يُقنَّع أي إعلان كزر تحميل، ولا يُفتح دون نقر، ولا يظهر في صفحة الخصوصية هذه، ولا يستبدل تحميل ملف APK أو يؤخّره. وتوجد صفحة غير مرتبطة بالموقع هي ‎/ads-verify/‎ هدفها الوحيد أن تتحقق شبكة الإعلانات من الوسم، وهي مستبعدة من خريطة الموقع ومن نتائج البحث.",
        ],
      },
    ],
    backToLanding: "العودة إلى صفحة التحميل",
    relatedTitle: "روابط ذات صلة",
    relatedLinks: [
      { label: "التحميل والتثبيت", href: "landing" },
      { label: "مستودع المشروع", href: "repo" },
      { label: "الإبلاغ عن مشكلة", href: "issues" },
    ],
    disclaimer:
      "تصف هذه الصفحة الإصدار 1.0.0 المنشور. وهي إفصاح تقني بحُسن نية وليست استشارة قانونية؛ وإذا وُزّع التطبيق تجاريًا فليُراجَع من مختص.",
  },
};
