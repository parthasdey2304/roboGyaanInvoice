# 🤖 RoboGyaan Invoice Suite v1.0.0 - Android & Web Release

We are proud to release **RoboGyaan Invoice v1.0.0**, featuring a complete native Android application built with Kotlin and Jetpack Compose alongside our Next.js web application.

---

## 🚀 Key Features

### 1. 🎨 Neo-Brutalist Design System & Virgil Aesthetics
- **Distinctive Palette:** Vibrant Primary Yellow (`#FFE600`), Jet Black (`#1E1E1E`), Orange registration badge (`#FFA500`), and clean white invoice sheets.
- **Asymmetric Borders & Shadows:** Solid black borders with `4dp` hard drop shadow offset.
- **Tactile Micro-interactions:** Animated touch translations (`translate(2dp, 2dp)`) and shadow reductions on press.
- **Handwritten Typography:** High-contrast Virgil / Excalidraw font loaded from `res/font/virgil.ttf`.

### 2. 📑 Authentic RoboGyaan Document Layout
- **Header:** RoboGyaan connected nodes emblem, subtitle (*"IGNITING CURIOSITY, BUILDING FUTURE"*), black polygon invoice header, and orange registration badge (`Registration No. - 273744`).
- **Sender & Client Blocks:** `BILL To :` and `From :` grey rounded pill badges.
- **Meta Bar:** 4-column summary (Invoice No., Issue Date, Due Date, Total Due).
- **Itemized Billing Table:** Dynamic line items with subtotal auto-calculation, center semi-transparent watermark, and total amount.
- **Indian Number-to-Words:** Automatic conversion (e.g., `27200` ➔ *Twenty Seven Thousand Two Hundred Only*).
- **Payment Method Selector:** `CASH`, `UPI`, `CHEQUE`, `BANK DRAFT`, `NEFT`.
- **Signatures:** Customer signature box and Authorised Signatory with official cursive signature of **Suman Mondal**.

### 3. 📄 Native A4 Vector PDF Generation & Sharing
- Vector-grade rendering directly through Android's native `android.graphics.pdf.PdfDocument` API.
- Instant export and sharing via standard Android `FileProvider` and system share sheet.

---

## 📦 Download & Installation

Download the attached APK below and install on any Android device running **Android 7.0 (Nougat, API 24) or higher**:
- **APK Asset:** [`robogyaan-invoice-v1.0.0.apk`](https://github.com/parthasdey2304/roboGyaanInvoice/releases/download/v1.0.0/robogyaan-invoice-v1.0.0.apk)
- **SHA-256 / File Size:** ~15.0 MB
- **Package Name:** `com.robogyaan.invoice`
- **Target SDK:** Android 14 (API 34)

---

## 🛠️ Verification & Build Status
- **Next.js Web Engine:** Production build verified with zero errors.
- **Android Engine:** Compiled via Gradle 8.7 & JDK 17 (`./gradlew assembleDebug`).
