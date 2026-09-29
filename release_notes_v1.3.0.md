# 🤖 RoboGyaan Invoice Suite v1.3.0 - Android & Web Release

We are proud to release **RoboGyaan Invoice v1.3.0**, introducing Firebase Firestore cloud prompt history, full invoice modification & deletion capabilities, and client-side Argon2id authentication security across both Android and Next.js platforms.

---

## 🚀 What's New in v1.3.0

### 1. 🔐 Admin Authentication & Client-Side Argon2id Security
- **Zero Plaintext Transmission:** The invoice engine is gated behind admin authentication requiring:
  - **Email:** `invoiceadmin@robogyaan.in`
  - **Password:** `InvoiceManagerRoboGyaan_Suman_Bhaiya`
- **Client-Side Argon2id Encryption:** Passwords are mathematically hashed in the browser / client using WebAssembly-powered Argon2id before transmission (`salt: robogyaan_invoice_auth_salt_2026`, 32-byte hex). Plaintext credentials never travel over the network.
- **Session Management:** Secure HTTP-only cookies on Web and encrypted local persistence on Android keep administrators securely signed in with a dedicated one-tap Logout action.
- **Cross-Platform Gate:** Complete Neo-Brutalist authentication portal on both the Next.js Web engine and the native Android app.

### 2. ☁️ Firebase Firestore Cloud Prompt History
- **3-Dash Menu Below Navbar:** Prominently positioned 3-horizontal-dash button on the **left top below the navbar / topbar** on both Web and Android.
- **Cloud Database Integration:** Connected directly to Firebase Firestore collection `invoice_prompts` in project `robogyaan-invoice`.
- **Live Sync & Offline Resilience:** Prompts and full invoice states are stored in Cloud Firestore and mirrored in local device cache for instantaneous access.
- **Rich Prompt Metadata:** Displays custom prompt notes, invoice numbers, recipient school/client, calculated totals in ₹, and timestamps.

### 3. ✏️ Modify, Update & Delete Existing Invoices
- **Update Active Invoices:** Load any previously saved invoice into the live editor, tweak quantities, items, or rates, and update the existing Firestore document with a single tap.
- **Inline Prompt Renaming:** Quick-edit prompt descriptions and notes without leaving the history sidebar.
- **Delete from Cloud:** Instant deletion of outdated invoices from both Firebase Firestore and local storage.

### 4. ⚙️ Vercel Environment Configuration
- Complete `.env.example` and `.env.local` with Firebase client parameters, admin credentials, and Argon2id hash for instant Vercel production deployment.

---

## 📦 Download & Installation

Download the attached APK below and install on any Android device running **Android 7.0 (Nougat, API 24) or higher**:
- **APK Asset:** [`robogyaan-invoice-v1.3.0.apk`](https://github.com/parthasdey2304/roboGyaanInvoice/releases/download/v1.3.0/robogyaan-invoice-v1.3.0.apk)
- **SHA-256 / File Size:** ~16.5 MB
- **Package Name:** `com.robogyaan.invoice`
- **Target SDK:** Android 14 (API 34)

---

## 🌐 Web Application Live Deployment
- **Live URL:** [https://invoice.robogyaan.in](https://invoice.robogyaan.in)
- **Engine:** Next.js 16 (App Router), TypeScript, Tailwind CSS v4, Argon2id, and Firebase Firestore.

---

## 🛠️ Verification & Build Status
- **Next.js Web Engine:** Turbopack production build verified with zero errors.
- **Android Engine:** Compiled via Gradle 8.7 & JDK 17 (`./gradlew assembleDebug`).
