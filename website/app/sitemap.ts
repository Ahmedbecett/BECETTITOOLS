import { MetadataRoute } from 'next'
export default function sitemap(): MetadataRoute.Sitemap {
  const base = 'https://becetti-tools.vercel.app'
  return [
    { url: `${base}/becetti-tools`, lastModified: new Date(), alternates: { languages: { en: `${base}/becetti-tools?lang=en`, fr: `${base}/becetti-tools?lang=fr`, ar: `${base}/becetti-tools?lang=ar` } } },
    { url: `${base}/privacy`, lastModified: new Date() },
  ]
}
