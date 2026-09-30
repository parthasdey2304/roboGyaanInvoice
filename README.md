# RoboGyaan Invoice Generation Suite

[![Vercel Deployment](https://img.shields.io/badge/Vercel-Live_Web_App-000000?style=for-the-badge&logo=vercel)](https://invoice.robogyaan.in)
[![Android APK Release](https://img.shields.io/badge/Android_APK-v1.5.0_Download-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/parthasdey2304/roboGyaanInvoice/releases/download/v1.5.0/robogyaan-invoice-v1.5.0.apk)
[![GitHub Release](https://img.shields.io/badge/GitHub-v1.5.0_Release-181717?style=for-the-badge&logo=github)](https://github.com/parthasdey2304/roboGyaanInvoice/releases/tag/v1.5.0)

A dual-platform invoice generation system for **RoboGyaan**:
1. **Next.js 16+ (App Router)** web application with real-time live preview, responsive split-screen layout, high-fidelity multi-page PDF export / print engine, real-time Firebase Firestore autosave, interactive 60fps canvas grid background with mouse swelling distortion, light/dark mode with single-span Sun/Moon toggle, and in-app APK updater.
2. **Native Android Application** written in Kotlin using **Jetpack Compose (Material 3)**, reactive `StateFlow` ViewModel, multi-page pagination engine with page dropdown selector, Firestore cloud synchronization with real-time autosave, dark/light theme engine, rounded PDF preview cards, and integrated in-app GitHub releases updater with progress indicator and MB/s download speed meter.

- 🌐 **Live Web Application:** [https://invoice.robogyaan.in](https://invoice.robogyaan.in)
- 📱 **Android APK Download:** [robogyaan-invoice-v1.5.0.apk](https://github.com/parthasdey2304/roboGyaanInvoice/releases/download/v1.5.0/robogyaan-invoice-v1.5.0.apk)
- 📦 **GitHub Release Notes:** [v1.5.0 Release](https://github.com/parthasdey2304/roboGyaanInvoice/releases/tag/v1.5.0)

Both platforms strictly adhere to a **Neo-Brutalist** design language with the hand-drawn **Virgil / Excalidraw** aesthetic, high-contrast black borders with asymmetric hard drop shadows, a vibrant yellow primary palette, and an exact replica of the official RoboGyaan invoice structure.

---

## 🚀 What's New in v1.5.0

- 🌓 **Navbar Light & Dark Mode:** Interactive Sun/Moon toggle in the exact same container span. Smooth transition between Neo-Brutalist yellow/white theme and high-contrast dark mode with black `#000000` header and white typography.
- 📄 **PDF Authentic White Paper Immunity:** The invoice preview and printed/exported sheets strictly maintain authentic pure white background (`#FFFFFF`) with genuine black typography and amber headers in both light and dark modes.
- 🔲 **Interactive Box-Box Grid Background:** HTML5 Canvas (Web) and Jetpack Compose Canvas (Android) grid background matching the login and main screens, complete with an interactive cursor swelling lens deformation effect on hover.
- ⭕ **Rounded Borders for PDF Sheets:** Clean rounded borders (`rounded-xl` / `16.dp`) for invoice preview sheets in split and preview modes without harsh drop shadows.
- ⚙️ **App Settings & Hand-Drawn Asterisk Sticker:** Hand-drawn asterisk/splat sticker next to admin email opening comprehensive app settings.
- 📲 **In-App Software Updater:** Direct GitHub Releases API integration checking for updates, real-time 0%–100% progress bar with live download speed in MB/s, automatic corrupted/partial file deletion, and direct package installer launch.

---

## 🎨 Design Specification & Neo-Brutalist System

- **Primary Yellow:** `#FFE600` / `#FFDD00`
- **Deep Neutral / Accent:** `#1E1E1E` / `#000000`
- **Surface Background:** `#FFFFFF` (Invoice Sheet) & `#FDFBF7` (Canvas Surface)
- **Secondary Badge Accent:** `#FFA500` / `#E58B00` (Registration Badge)
- **Soft Neutral Fill:** `#E5E7EB` / `#F3F4F6` (BILL To / From pills)
- **Neo-Brutalist Borders & Shadows:**
  - Solid black borders (`2px` to `2.5px`).
  - Asymmetric hard drop shadows (`4px 4px 0px #000000`).
  - Tactile micro-interactions (`translate(2px, 2px)` on press with shadow reduction).
- **Typography:** Virgil / Excalidraw handwriting font (`Virgil.woff2` for web and `virgil.ttf` in `res/font` for Android).

---

## 📑 RoboGyaan Invoice Layout & Fields

- **Header Section:**
  - **RoboGyaan Logo:** Connected circular network nodes emblem + "ROBOGYAAN" + "IGNITING CURIOSITY, BUILDING FUTURE".
  - **Top-Right Polygon:** Solid black angled polygon banner displaying dynamic `Invoice No. : RG-JPS-2608-001`.
  - **Registration Badge:** Angled orange/gold badge displaying `Registration No. - 273744`.
- **Sender & Recipient Blocks:**
  - **BILL To (Left):** Grey pill badge + School / Client Name, Address, Pin & State.
  - **From (Right):** Grey pill badge + RoboGyaan, Sonarpur Address, Pin & State.
- **Meta Summary Bar (4 Columns):**
  - `Invoice No.` (Amber/Yellow)
  - `Issue Date:` (Amber/Yellow)
  - `Due Date:` (Amber/Yellow)
  - `Total Due :` (Dark Charcoal `#505050` with white text `₹ 27,200/-`)
- **Itemized Billing Table:**
  - Columns: `Item`, `Amount / Student head`, `No. of Students`, `Total Amount`.
  - Background Watermark: Semi-transparent RoboGyaan logo rotated at -25° behind table rows.
  - Footer Subtotal: `Total Amount : ₹ [CALCULATED_TOTAL]`.
- **Payment & Legal Details:**
  - **Amount (In words):** Automatically generated using the Indian currency numbering algorithm (e.g. `Twenty Seven Thousand Two Hundred Only`).
  - **Payment Method:** `CASH`, `UPI`, `CHEQUE`, `BANK DRAFT`, `NEFT`.
- **Signatures Footer:**
  - **Customer Signature (Left):** Physical client signature box.
  - **Authorised Signatory (Right):** Pre-embedded cursive signature of **Suman Mondal**.

---

## 🧮 Indian Number-to-Words Algorithm

Pure utility function in both TypeScript (`web/lib/numberToWordsIndian.ts`) and Kotlin (`android/.../util/NumberToWordsIndian.kt`) converting values up to 99,99,99,999:
- `27200` ➔ `Twenty Seven Thousand Two Hundred Only`
- `150500` ➔ `One Lakh Fifty Thousand Five Hundred Only`

---

## 💻 Part 1: Next.js Web Application (`web/`)

### Tech Stack
- Next.js 16+ (App Router), TypeScript, Tailwind CSS v4, Lucide React, Firebase Firestore, Argon2id.
- Font: `next/font/local` importing `Virgil.woff2`.
- Export: Client-side PDF generation via `html2canvas` + `jspdf` at 2.5x high-res scale, plus `@media print` single-page A4 CSS.
- Autosave: Debounced single-copy sync with Firebase Firestore and local offline fallback.

### Project Structure
```
web/
├── app/
│   ├── globals.css         # Tailwind v4 styles, Neo-Brutalist utility classes & A4 print CSS
│   ├── layout.tsx          # Root layout importing Virgil local font
│   └── page.tsx            # Main page with split screen, tab switcher, autosave indicator, PDF download & print
├── components/
│   ├── AuthGate.tsx        # Argon2id client-side password authentication screen
│   ├── HistorySidebar.tsx  # Firestore prompt history panel with edit/delete/load actions
│   ├── InvoiceEditor.tsx   # Neo-Brutalist input forms with item add/delete & payment chips
│   ├── InvoicePreview.tsx  # Live dynamic A4 invoice sheet matching reference
│   ├── NeoBrutalButton.tsx # Tactile button component with micro-interaction translation
│   ├── NeoBrutalCard.tsx   # Neo-brutalist card container with shadows and borders
│   └── RobogyaanLogo.tsx   # Scalable vector logo and table watermark
├── lib/
│   ├── defaultInvoice.ts   # Reference invoice initial state
│   ├── firebase.ts         # Firebase Firestore initialization, queries, and autosave
│   ├── numberToWordsIndian.ts # Indian currency number-to-words algorithm
│   └── types.ts            # TypeScript data models
└── public/
    ├── fonts/Virgil.woff2  # Virgil handwriting font
    ├── robogyaan_logo.png   # Transparent logo asset
    └── signature.png       # Suman Mondal signature asset
```

### Running the Web App
```bash
cd web
npm install
npm run dev
```
Open [http://localhost:3000](http://localhost:3000) in your browser.

---

## 📱 Part 2: Native Android Application (`android/`)

### Tech Stack
- Kotlin, Jetpack Compose (Material 3), AndroidX Lifecycle ViewModel, StateFlow.
- Firebase Firestore REST cloud synchronization with debounced autosave draft engine.
- Native `android.graphics.pdf.PdfDocument` API for vector A4 PDF generation.
- Custom Neo-Brutalist Modifier in Compose:
```kotlin
fun Modifier.neoBrutal(
    backgroundColor: Color = Color(0xFFFFE600),
    borderColor: Color = Color.Black,
    borderWidth: Dp = 2.dp,
    shadowOffset: Dp = 4.dp,
    cornerRadius: Dp = 8.dp
): Modifier
```

### Project Structure
```
android/app/src/main/
├── AndroidManifest.xml
├── java/com/robogyaan/invoice/
│   ├── MainActivity.kt                 # Screen orchestration, tabs & PDF share intent
│   ├── data/InvoiceModels.kt           # Data classes & PaymentMethod enum
│   ├── ui/
│   │   ├── InvoiceViewModel.kt         # Reactive StateFlow ViewModel
│   │   ├── NeoBrutalModifiers.kt       # Modifier.neoBrutal & neoBrutalClickable
│   │   ├── components/
│   │   │   ├── InvoiceEditorScreen.kt  # Compose editor screen
│   │   │   └── NeoBrutalComponents.kt  # NeoBrutalCard, NeoBrutalButton, TextFields
│   │   ├── preview/
│   │   │   └── InvoicePreviewScreen.kt # Compose A4 sheet live preview
│   │   └── theme/
│   │       ├── Color.kt, Theme.kt, Type.kt # Virgil font & palette
│   └── util/
│       ├── NumberToWordsIndian.kt      # Kotlin Indian words converter
│       └── PdfGenerator.kt             # Native Android A4 PdfDocument renderer
└── res/
    ├── drawable/
    │   ├── ic_robogyaan_logo.png
    │   ├── ic_robogyaan_symbol.png
    │   └── ic_signature.png
    ├── font/virgil.ttf
    ├── values/
    └── xml/file_paths.xml
```

### Running the Android App
Open the `android/` directory in **Android Studio** (Hedgehog / Iguana / Koala / Ladybug or newer) and run on an Android device or emulator (Android 7.0+ / API 24+).
