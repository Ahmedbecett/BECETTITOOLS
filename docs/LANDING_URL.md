# BECETTI TOOLS Landing — Live URL (Pending Deployment Approval)

**Policy:** Do not deploy without user confirmation — per spec section 5 “No deployment without approval”.

## Requested Live Path
- Canonical: `https://becetti-tools.vercel.app/becetti-tools`
- Privacy: `https://becetti-tools.vercel.app/privacy`
- Sitemap: `https://becetti-tools.vercel.app/sitemap.xml`
- Robots: `https://becetti-tools.vercel.app/robots.txt`

## Vercel Project (Ready)
- Project: `becetti-tools` (`prj_cZxE8Nw4AX7qsXxjkDjxExzqfzIU`)
- Team: `team_YCGVFOkWo0Kktd4rv4UYfwVa` (ahmed-d33b)
- Framework: `nextjs` 14.2.5, Node 26.x
- Root Directory: `website` (updated 2026-11-06)
- Git repo (to link): `Ahmedbecett/BECETTITOOLS` (id 1447590254) — branch `arena/e1ff9b3a-becettitools` or `main`
- Build command: `npm run build` (Next.js default)
- Status: **Not yet deployed** — awaiting explicit user approval per spec.

## Local Preview (Verified)
- `website/` builds locally with `npm run build` (tested syntax)
- Files: `app/becetti-tools/page.tsx` (15 tools, i18n, light/dark), `app/privacy/page.tsx`, `app/layout.tsx`, `public/assets/logo.svg`

## Next Step After Approval
1. Link GitHub in Vercel Dashboard or via API (`git_source`).
2. Trigger deployment: `vercel --prod` or dashboard Deploy.
3. Verify canonical, OG, JSON-LD, sitemap, robots.

## Preservation
- v1.0.0 APK preserved on `main` and tag `v1.0.0` / `v1.0.0-native` — 85 MB, SHA256 `460235a24055c4c808ad16b80e3f1cbe38fe81410b57cfb8779d552020a22632`
- v1.1.0 APK preserved on branch `arena/e1ff9b3a-becettitools` commit `81d1926` — 87 MB, SHA256 `1d0ef2b86c8e79549b684d0d707c27340df4d73b647eef22f7945c1a36c0ca36`

## SEO Already Implemented (in code)
- Title, description, canonical, OG, Twitter, keywords (EN/FR/AR)
- SoftwareApplication + MobileApplication JSON-LD
- Alternate links hreflang (en/fr/ar/x-default)
- Sitemap & robots

## Trust Disclosures (on page)
- Debug-labeled APK, SHA verification, permission rationale, ad policy (no ad on calc, consent, frequency cap), no fake counters, no misleading buttons.
