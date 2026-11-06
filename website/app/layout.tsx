import "./globals.css";
export const metadata = {
  metadataBase: new URL("https://becetti-tools.vercel.app"),
};
export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body>{children}</body>
    </html>
  );
}
