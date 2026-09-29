# 🤖 RoboGyaan Invoice Suite v1.2.0 - Android & Web Release

We are proud to release **RoboGyaan Invoice v1.2.0**, introducing responsive segmented view controls, inline line item editing, elevated badge positioning, and bold neo-brutalist typography across both Android and Next.js platforms.

---

## 🚀 What's New in v1.2.0

### 1. 📱 Responsive Segmented Header Controls
- **Phone Screen Optimization:** The `Editor`, `Split`, and `Preview` view switcher displays strictly icons on phone screens for a clean, compact, non-crowded top bar.
- **Desktop Typography:** On computer screens, uppercase bold Virgil typography (`EDITOR`, `SPLIT`, `PREVIEW`) accompanies each tactile icon matching Neo-Brutalist design language.
- **Android Native Sync:** Android segmented tab bar mirrors this icon-first approach on mobile devices.

### 2. 🏷️ Elevated RECIPIENT & SENDER Badges
- **Upper Alignment:** Positioned `RECIPIENT` and `SENDER` badges higher on the top-right of cards, aligned cleanly above the multiline titles `Billed To (Client)` and `From (Robogyaan)`.
- **Visual Hierarchy:** Preserves clear distinction between client and company data with improved spacing.

### 3. 📊 Inline Line Item Editing (Same-Line Grid)
- **Single Horizontal Line:** All three billing fields—`Description / Program Name`, `Amount / Student Head (₹)`, and `No. of Students`—now render on the **same horizontal line** in both the Web application and Android Jetpack Compose editor.
- **Improved Workflow:** Eliminates vertical field stacking for rapid, seamless invoice entry.

### 4. 🔤 Bold Typography System-Wide
- **High-Contrast Headings:** Upgraded card headings, table column headers, meta summary titles, and signature labels to ultra-bold / black weights (`font-black` on Web, `FontWeight.Black` in Android Compose).

---

## 📦 Download & Installation

Download the attached APK below and install on any Android device running **Android 7.0 (Nougat, API 24) or higher**:
- **APK Asset:** [`robogyaan-invoice-v1.2.0.apk`](https://github.com/parthasdey2304/roboGyaanInvoice/releases/download/v1.2.0/robogyaan-invoice-v1.2.0.apk)
- **SHA-256 / File Size:** ~16.0 MB
- **Package Name:** `com.robogyaan.invoice`
- **Target SDK:** Android 14 (API 34)

---

## 🌐 Web Application Live Deployment
- **Live URL:** [https://invoice.robogyaan.in](https://invoice.robogyaan.in)
- **Engine:** Next.js 16 (App Router), TypeScript, Tailwind CSS v4, html-to-image & jsPDF.

---

## 🛠️ Verification & Build Status
- **Next.js Web Engine:** Production Turbopack build verified with zero errors.
- **Android Engine:** Compiled via Gradle 8.7 & JDK 17 (`./gradlew assembleDebug`).
