# 🤖 RoboGyaan Invoice Suite v1.1.0 - Android & Web Release

We are proud to release **RoboGyaan Invoice v1.1.0**, featuring significant typography enhancements with Google Poppins, a refined responsive header layout, improved Neo-Brutalist aesthetics, and bug fixes across both Android and Next.js platforms.

---

## 🚀 What's New in v1.1.0

### 1. 🔤 Google Poppins Typography Integration
- **A4 Live Preview & PDF Engine:** Switched invoice document typography from Virgil to crisp Google Poppins across both the Next.js web application and the Android native Jetpack Compose engine.
- **Form Placeholders & Input Text:** Input fields and placeholders across the Editor now render in modern Google Poppins font for enhanced readability and clarity.
- **Semi-Bold Virgil UI:** Virgil handwritten font weights tuned to semi-bold (`font-weight: 600`) for headers, badges, and action buttons.

### 2. 📱 Header & Responsive Layout Refinements
- **Top Bar Alignment:** Heading (RoboGyaan branding & emblem) is pinned to the **Top Left**.
- **Segmented View Switcher:** **`EDITOR`**, **`SPLIT`**, and **`PREVIEW`** buttons are positioned on the **Top Right** with always-visible labels and tactile Neo-Brutalist styling.
- **Android Split Mode:** Added 3-way layout switcher (`Editor`, `Split`, `Preview`) to the Android app, enabling simultaneous side-by-side editing and previewing.
- **Sender & Recipient Cards:** Moved `RECIPIENT` and `SENDER` badges to the top-right of cards while keeping titles (`Billed To (Client)`, `From (Robogyaan)`) left-aligned normally.

### 3. 🛠️ Critical Bug Fixes & A4 Sheet Containment
- **Phone Screen Sheet Overflow Fixed:** Resolved issue where signature boxes (`Customer Signature` and `Authorised Signatory`) spilled outside the bottom and right borders on mobile screens. The invoice sheet now wraps dynamically on mobile with responsive column scaling.
- **RoboGyaan Watermark Integrity:** Watermark text and connected-nodes emblem are fully visible without truncation or clipping.
- **Continuous Table Dividers:** Column dividers now run unbroken through table data and filler space down to the footer.
- **Vector PDF Engine on Web:** Replaced `html2canvas` with `html-to-image` (`toPng`), eliminating CSS parsing alerts (`oklch`) and enabling clean 1-click PDF downloads.

---

## 📦 Download & Installation

Download the attached APK below and install on any Android device running **Android 7.0 (Nougat, API 24) or higher**:
- **APK Asset:** [`robogyaan-invoice-v1.1.0.apk`](https://github.com/parthasdey2304/roboGyaanInvoice/releases/download/v1.1.0/robogyaan-invoice-v1.1.0.apk)
- **SHA-256 / File Size:** ~16.0 MB
- **Package Name:** `com.robogyaan.invoice`
- **Target SDK:** Android 14 (API 34)

---

## 🌐 Web Application Live Deployment
- **Live URL:** [https://robogyaan-invoice.vercel.app](https://robogyaan-invoice.vercel.app)
- **Engine:** Next.js 16 (App Router), TypeScript, Tailwind CSS v4, html-to-image & jsPDF.

---

## 🛠️ Verification & Build Status
- **Next.js Web Engine:** Production Turbopack build verified with zero errors.
- **Android Engine:** Compiled via Gradle 8.7 & JDK 17 (`./gradlew assembleDebug`).
