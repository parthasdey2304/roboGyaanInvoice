# Graph Report - roboGyaanInvoice  (2026-10-01)

## Corpus Check
- Corpus is ~47,773 words - fits in a single context window. You may not need a graph.

## Summary
- 377 nodes · 662 edges · 38 communities (22 shown, 16 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 27 edges (avg confidence: 0.89)
- Token cost: 1,400 input · 700 output

## Community Hubs (Navigation)
- Web Invoice Editor & History
- Android Invoice Models & State
- Android Main Activity & Layout
- Browser Biometric Vault & Face ID
- TypeScript Compiler Options
- Web Styling & Tooling Dependencies
- Android Firebase Firestore Sync
- Android Biometric Login & Security
- Web External Libraries & Bundles
- In-App APK Updater & Release Checker
- Agent Protocols & Release Pipelines
- WebAuthn Biometric API Endpoints
- Number to Words Indian Currency Formatter
- Web Root Layout & Font Setup
- Gradle Wrapper Build Scripts
- Admin Password Login API
- Session Check API
- Release Version API
- Session Logout API
- ESLint Configuration
- Next.js Build Configuration
- Android App Gradle Module
- Compose Color Definitions
- Neo-Brutal Modifiers & Effects
- Module: Android Ic Robogyaan Symbol Im
- Module: Robogyaan Symbol Image
- Module: Web Public File Svg Image
- Module: Web Public Globe Svg Image
- Module: Web Public Next Svg Image
- Module: Web Public Robogyaan Symbol Im
- Module: Web Public Vercel Svg Image
- Module: Web Public Window Svg Image
- Module: Web Readme Nextjs App Bootstra

## God Nodes (most connected - your core abstractions)
1. `InvoiceViewModel` - 23 edges
2. `AuthPreferences` - 16 edges
3. `compilerOptions` - 16 edges
4. `FirebaseFirestoreService` - 15 edges
5. `MainScreen()` - 12 edges
6. `neoBrutal()` - 12 edges
7. `AuthGate()` - 12 edges
8. `InvoiceData` - 11 edges
9. `InvoiceEditorScreen()` - 10 edges
10. `InvoiceHistoryItem` - 9 edges

## Surprising Connections (you probably didn't know these)
- `RoboGyaan Official Logo Branding Asset` --semantically_similar_to--> `Android Drawable RoboGyaan Logo Asset`  [INFERRED] [semantically similar]
  robogyaan_logo.png → android/app/src/main/res/drawable/ic_robogyaan_logo.png
- `Suman Mondal Official Authorised Signatory Signature Asset` --semantically_similar_to--> `Android Drawable Signatory Signature Asset`  [INFERRED] [semantically similar]
  signature.png → android/app/src/main/res/drawable/ic_signature.png
- `RoboGyaan Official Logo Branding Asset` --semantically_similar_to--> `Web Public Directory RoboGyaan Logo Asset`  [INFERRED] [semantically similar]
  robogyaan_logo.png → web/public/robogyaan_logo.png
- `Suman Mondal Official Authorised Signatory Signature Asset` --semantically_similar_to--> `Web Public Directory Signatory Signature Asset`  [INFERRED] [semantically similar]
  signature.png → web/public/signature.png
- `LoginScreen()` --calls--> `BoxGridBackground()`  [INFERRED]
  android/app/src/main/java/com/robogyaan/invoice/ui/components/LoginScreen.kt → android/app/src/main/java/com/robogyaan/invoice/ui/components/BoxGridBackground.kt

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Neo-Brutalist Visual Identity & Component System** — _agents_syntax_style_neobrutalist_identity, readme_robogyaan_invoice_generation_suite, web_components_neobrutalbutton [INFERRED 0.85]
- **Cross-Platform Realtime Autosave & Cloud Synchronization** — _agents_syntax_style_firestore_autosave_standard, web_lib_firebase, android_app_src_main_java_com_robogyaan_invoice_data_firebasefirestoreservice [INFERRED 0.85]

## Communities (38 total, 16 thin omitted)

### Community 0 - "Web Invoice Editor & History"
Cohesion: 0.07
Nodes (46): InvoicePage(), HistorySidebar(), HistorySidebarProps, InvoiceEditor(), InvoiceEditorProps, InvoicePreview, InvoicePreviewProps, NeoBrutalButton() (+38 more)

### Community 1 - "Android Invoice Models & State"
Cohesion: 0.12
Nodes (30): HandDrawnAsteriskIcon(), Color, Modifier, MainActivity, MainScreen(), BoxGridBackground(), Modifier, HistorySidebarSheet() (+22 more)

### Community 2 - "Android Main Activity & Layout"
Cohesion: 0.15
Nodes (28): BiometricsManagementPage(), CloudBiometricCred, AuthGate(), AuthGateProps, InteractiveGridBackground(), InteractiveGridBackgroundProps, AUTH_SALT, hashPasswordWithArgon2() (+20 more)

### Community 3 - "Browser Biometric Vault & Face ID"
Cohesion: 0.08
Nodes (12): PaymentMethod, BANK_DRAFT, CASH, CHEQUE, NEFT, UPI, InvoiceViewModel, InvoiceData (+4 more)

### Community 4 - "TypeScript Compiler Options"
Cohesion: 0.07
Nodes (28): dom, dom.iterable, esnext, **/*.mts, .next/dev/types/**/*.ts, next-env.d.ts, .next/types/**/*.ts, node_modules (+20 more)

### Community 5 - "Web Styling & Tooling Dependencies"
Cohesion: 0.08
Nodes (25): eslint, eslint-config-next, tailwindcss, @tailwindcss/postcss, @types/node, @types/react, @types/react-dom, typescript (+17 more)

### Community 6 - "Android Firebase Firestore Sync"
Cohesion: 0.33
Nodes (7): FirebaseFirestoreService, InvoiceHistoryItem, Context, InvoiceData, BilledParty, SenderParty, JSONObject

### Community 7 - "Android Biometric Login & Security"
Cohesion: 0.15
Nodes (17): InvoiceData, InvoiceItem, InvoicePageSlice, paginateInvoiceItems(), InvoicePreviewScreen(), InvoiceSheetCard(), InvoiceData, Modifier (+9 more)

### Community 8 - "Web External Libraries & Bundles"
Cohesion: 0.17
Nodes (6): AuthPreferences, Context, LoginAuthMode, FINGERPRINT, PASSWORD, LoginScreen()

### Community 9 - "In-App APK Updater & Release Checker"
Cohesion: 0.11
Nodes (19): firebase, hash-wasm, html2canvas, html-to-image, jspdf, lucide-react, next, react (+11 more)

### Community 10 - "Agent Protocols & Release Pipelines"
Cohesion: 0.38
Nodes (3): AppUpdateManager, Context, UpdateInfo

### Community 11 - "WebAuthn Biometric API Endpoints"
Cohesion: 0.22
Nodes (9): Android APK Compilation with JDK 17 Pipeline, GitHub Release Publishing with Tagged APK Asset, PR Squash-Merge and Branch Cleanup Protocol, Standard Git Pull Request, Build & Release Workflow, RoboGyaan Invoice Suite - Agent Instructions & Architecture Guide, Argon2id Client-Side Password Hashing Security Standard, Firebase Firestore Realtime Autosave & Offline Fallback, RoboGyaan Syntax & Design Style Guidelines (+1 more)

### Community 12 - "Number to Words Indian Currency Formatter"
Cohesion: 0.43
Nodes (6): EXPECTED_EMAIL, EXPECTED_HASH, GET(), getCloudBiometricCredentials(), POST(), sanitizeDocId()

### Community 13 - "Web Root Layout & Font Setup"
Cohesion: 0.40
Nodes (3): metadata, poppins, virgil

### Community 14 - "Gradle Wrapper Build Scripts"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 16 - "Session Check API"
Cohesion: 0.67
Nodes (3): Android Drawable RoboGyaan Logo Asset, RoboGyaan Official Logo Branding Asset, Web Public Directory RoboGyaan Logo Asset

### Community 17 - "Release Version API"
Cohesion: 0.67
Nodes (3): Android Drawable Signatory Signature Asset, Suman Mondal Official Authorised Signatory Signature Asset, Web Public Directory Signatory Signature Asset

## Knowledge Gaps
- **109 isolated node(s):** `CASH`, `UPI`, `CHEQUE`, `BANK_DRAFT`, `NEFT` (+104 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **16 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `PDF & Invoice Preview Table Dual-Box Footer Architecture (v1.6.2)` connect `Android Biometric Login & Security` to `Web Invoice Editor & History`?**
  _High betweenness centrality (0.150) - this node is a cross-community bridge._
- **Why does `Firebase Firestore Realtime Autosave & Offline Fallback` connect `WebAuthn Biometric API Endpoints` to `Web Invoice Editor & History`, `Android Firebase Firestore Sync`?**
  _High betweenness centrality (0.070) - this node is a cross-community bridge._
- **Why does `InvoiceViewModel` connect `Browser Biometric Vault & Face ID` to `Android Invoice Models & State`, `Android Biometric Login & Security`?**
  _High betweenness centrality (0.055) - this node is a cross-community bridge._
- **What connects `CASH`, `UPI`, `CHEQUE` to the rest of the system?**
  _109 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Web Invoice Editor & History` be split into smaller, more focused modules?**
  _Cohesion score 0.07213114754098361 - nodes in this community are weakly interconnected._
- **Should `Android Invoice Models & State` be split into smaller, more focused modules?**
  _Cohesion score 0.11875843454790823 - nodes in this community are weakly interconnected._
- **Should `Android Main Activity & Layout` be split into smaller, more focused modules?**
  _Cohesion score 0.14583333333333334 - nodes in this community are weakly interconnected._