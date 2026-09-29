# 🎨 RoboGyaan Syntax & Design Style Guidelines

## 1. Neo-Brutalist Visual Identity
Both Web and Android implementations strictly follow the Neo-Brutalist design language:
- **Primary Yellow:** `#FFE600` (accent bars, primary buttons, logo badge, and badges).
- **Background Cream:** `#FDFBF7` / `#FFFDF7` (tactile paper feel).
- **Deep Black:** `#000000` / `#1E1E1E` (headers, borders, high-contrast badges).
- **Borders & Shadows:** Solid black borders (`2px` to `3.5px`), offset hard drop-shadows (`3px 3px 0px #000` or `4px 4px 0px #000`). No blur filters.
- **Tactile States:** Buttons translate down-right on press (`active:translate-x-[1px] active:translate-y-[1px]` or `neoBrutalClickable` in Compose).

---

## 2. Typography Rules
- **Headings & Badges:** Virgil handwritten font (`font-virgil` in Web, `VirgilFontFamily` in Android) in ultra-bold or black weight (`font-black` / `FontWeight.Black`).
- **Body & Tabular Data:** Google Poppins (`font-sans` / `PoppinsFontFamily`) for high readability, rates, and input fields.
- **Bold Headings Standard:** All card headers, table headers, and modal titles must be bold/black weight.

---

## 3. Responsive Layout Hierarchy
- **Header Segmented Control (Editor / Split / Preview):**
  - **Phone screens:** Icons only (no text) to maintain compact navigation.
  - **Desktop screens:** Icon + uppercase Virgil text (`EDITOR`, `SPLIT`, `PREVIEW`).
- **Line Item Form Grid:**
  - `Description`, `Amount / Head`, and `No. of Students` must render on the **same horizontal line** across platforms.
- **Badge Positioning:**
  - `RECIPIENT` and `SENDER` pill badges sit elevated on the top-right of their respective cards.
- **Prompt History Button:**
  - 3-horizontal-dash hamburger toggle must reside on the **left top below the navbar**.

---

## 4. Security & Authentication Standards
- **Admin Email:** `invoiceadmin@robogyaan.in`
- **Admin Password:** `InvoiceManagerRoboGyaan_Suman_Bhaiya`
- **Zero Plaintext Transmission:** Passwords MUST be hashed client-side before network transmission using **Argon2id** (`salt: robogyaan_invoice_auth_salt_2026`, iterations: 3, memory: 4096KB, parallelism: 1, 32-byte hex hash).
- **Session Management:** Secure HTTP-only cookies on Web (`robogyaan_auth_session`) and persistent encrypted preferences on Android.

---

## 5. Firebase Firestore Integration
- **Project ID:** `robogyaan-invoice`
- **Collection Name:** `invoice_prompts`
- **Operations:**
  - Create: `saveInvoicePrompt` / `savePrompt`
  - Read: `fetchInvoiceHistory` / `fetchHistory`
  - Update / Modify: `updateInvoiceHistoryEntry` / `updatePrompt`
  - Delete: `deleteInvoiceHistoryEntry` / `deletePrompt`
- **Offline Resilience:** All operations must update local device storage (`localStorage` on Web, `SharedPreferences` on Android) so data remains instantly accessible.
