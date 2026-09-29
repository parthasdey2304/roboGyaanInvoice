# RoboGyaan Invoice Generation Suite

A dual-platform invoice generation system for **RoboGyaan**:
1. **Next.js 14+ (App Router)** web application with real-time live preview, responsive split-screen layout, and high-fidelity PDF export / print engine.
2. **Native Android Application** written in Kotlin using **Jetpack Compose (Material 3)**, reactive `StateFlow` ViewModel, and native vector A4 PDF generation via `android.graphics.pdf.PdfDocument`.

Both platforms strictly adhere to a **Neo-Brutalist** design language with the hand-drawn **Virgil / Excalidraw** aesthetic, high-contrast black borders with asymmetric hard drop shadows, a vibrant yellow primary palette, and an exact replica of the official RoboGyaan invoice structure.

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
- Next.js 14+ (App Router), TypeScript, Tailwind CSS v4, Lucide React.
- Font: `next/font/local` importing `Virgil.woff2`.
- Export: Client-side PDF generation via `html2canvas` + `jspdf` at 2.5x high-res scale, plus `@media print` single-page A4 CSS.

### Project Structure
```
web/
├── app/
│   ├── globals.css         # Tailwind v4 styles, Neo-Brutalist utility classes & A4 print CSS
│   ├── layout.tsx          # Root layout importing Virgil local font
│   └── page.tsx            # Main page with split screen, tab switcher, PDF download & print
├── components/
│   ├── InvoiceEditor.tsx   # Neo-Brutalist input forms with item add/delete & payment chips
│   ├── InvoicePreview.tsx  # Live dynamic A4 invoice sheet matching reference
│   ├── NeoBrutalButton.tsx # Tactile button component with micro-interaction translation
│   ├── NeoBrutalCard.tsx   # Neo-brutalist card container with shadows and borders
│   └── RobogyaanLogo.tsx   # Scalable vector logo and table watermark
├── lib/
│   ├── defaultInvoice.ts   # Reference invoice initial state
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
