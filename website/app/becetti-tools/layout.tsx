import type { Metadata } from "next";
export const metadata: Metadata = {
  title: "BECETTI TOOLS v1.0.0 — Free Android Utility Toolbox | Calculator, QR, PDF, OCR & More",
  description: "BECETTI TOOLS v1.0.0 — native Android (Kotlin + Jetpack Compose, 15 tools). Scientific calculator, unit & currency converters, QR generator & scanner, image-to-PDF, PDF merger, compressor, OCR (AR/FR/EN) & notes. Offline-first, no account, lightweight (85 MB). Download APK for Android 7.0+.",
  keywords: [
    "BECETTI TOOLS","Free Android Utility App","Android Calculator and Converter","QR Code Generator and Scanner","Image to PDF Android","PDF Merger Android","Free Android Tools",
    "BECETTI TOOLS Android","Outils Android gratuits","Convertisseur PDF Android","Générateur QR Android",
    "أدوات أندرويد مجانية","تطبيق أدوات بي سيتي","تطبيق تحويل الصور إلى PDF","ماسح QR للأندرويد"
  ],
  alternates: {
    canonical: "https://becetti-tools.vercel.app/becetti-tools",
    languages: {
      en: "https://becetti-tools.vercel.app/becetti-tools?lang=en",
      fr: "https://becetti-tools.vercel.app/becetti-tools?lang=fr",
      ar: "https://becetti-tools.vercel.app/becetti-tools?lang=ar",
      "x-default": "https://becetti-tools.vercel.app/becetti-tools",
    },
  },
  openGraph: {
    title: "BECETTI TOOLS v1.0.0 — Everything you need. One smart toolbox.",
    description: "15 tools: calculators, converters, QR, PDFs, OCR, notes. Native Kotlin • 85 MB • Android 7.0+ • Offline-first • No paid libs",
    url: "https://becetti-tools.vercel.app/becetti-tools",
    siteName: "BECETTI TOOLS",
    locale: "en_US",
    alternateLocale: ["fr_FR","ar_DZ"],
    type: "website",
    images: [{ url: "/assets/logo.svg", width: 512, height: 512, alt: "BECETTI TOOLS — B logo on deep navy, orange glow" }],
  },
  twitter: {
    card: "summary_large_image",
    title: "BECETTI TOOLS v1.0.0 — Free Android Toolbox (15 Tools)",
    description: "Calculator, converters, QR, PDF & more. Native, offline, no account. Download APK 85 MB.",
    images: ["/assets/logo.svg"],
  },
  robots: { index: true, follow: true },
};
const jsonLd = {
  "@context": "https://schema.org",
  "@type": "SoftwareApplication",
  "name": "BECETTI TOOLS",
  "alternateName": ["أدوات بي سيتي","Outils Becetti"],
  "applicationCategory": "UtilitiesApplication",
  "operatingSystem": "Android 7.0+",
  "offers": { "@type": "Offer", "price": "0", "priceCurrency": "USD" },
  "softwareVersion": "1.0.0",
  "version": "1.0.0",
  "fileSize": "85MB",
  "downloadUrl": "https://raw.githubusercontent.com/Ahmedbecett/BECETTITOOLS/main/BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk",
  "installUrl": "https://becetti-tools.vercel.app/becetti-tools#install",
  "screenshot": "https://becetti-tools.vercel.app/assets/logo.svg",
  "description": "Native Android utility toolbox: scientific calculator, unit & currency converters, percentage & age calculators, QR generator & scanner, password tools, image-to-PDF, PDF merger, image compressor, OCR (AR/FR/EN), text toolkit & notes. Offline-first, Material 3, Kotlin + Compose.",
  "author": { "@type": "Person", "name": "Ahmed Becetti", "url": "https://github.com/Ahmedbecett" },
  "publisher": { "@type": "Organization", "name": "BECETTI TOOLS", "url": "https://becetti-tools.vercel.app/becetti-tools" },
  "inLanguage": ["en","fr","ar"],
  "isAccessibleForFree": true,
  "license": "https://github.com/Ahmedbecett/BECETTITOOLS/blob/main/LICENSE",
  "releaseNotes": "https://github.com/Ahmedbecett/BECETTITOOLS/releases/tag/v1.0.0",
  "featureList": "Scientific Calculator, Unit Converter, Currency Converter, Percentage Calculator, Age Calculator, QR Generator, QR Scanner, Password Generator, Password Strength, Image to PDF, PDF Merger, Image Compressor, OCR, Text Toolkit, Notes Manager"
};
const mobileAppLd = {
  "@context": "https://schema.org",
  "@type": "MobileApplication",
  "name": "BECETTI TOOLS",
  "operatingSystem": "Android",
  "applicationCategory": "UtilitiesApplication",
  "aggregateRating": { "@type": "AggregateRating", "ratingValue": "5", "reviewCount": "1" }
};
export default function Layout({children}:{children:React.ReactNode}){
  return <>
    <script type="application/ld+json" dangerouslySetInnerHTML={{__html: JSON.stringify(jsonLd)}} />
    <script type="application/ld+json" dangerouslySetInnerHTML={{__html: JSON.stringify(mobileAppLd)}} />
    {children}
  </>;
}
