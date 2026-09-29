import type { Metadata } from 'next';
import localFont from 'next/font/local';
import './globals.css';

const virgil = localFont({
  src: '../public/fonts/Virgil.woff2',
  variable: '--font-virgil',
  display: 'swap',
});

export const metadata: Metadata = {
  title: 'RoboGyaan Invoice Generator | Neo-Brutalist Edition',
  description: 'Dual-platform invoice generation system for Robogyaan with real-time live preview and high-fidelity PDF export.',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className={virgil.variable}>
      <body className="min-h-screen bg-[#FDFBF7] text-[#1E1E1E] font-sans antialiased selection:bg-[#FFE600] selection:text-black">
        {children}
      </body>
    </html>
  );
}
