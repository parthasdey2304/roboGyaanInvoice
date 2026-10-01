# Graph Report - roboGyaanInvoice  (2026-10-01)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 379 nodes · 650 edges · 40 communities (23 shown, 17 thin omitted)
- Extraction: 95% EXTRACTED · 5% INFERRED · 0% AMBIGUOUS · INFERRED: 35 edges (avg confidence: 0.88)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `af992212`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Web Invoice Editor & History
- InvoiceViewModel
- MainActivity.kt
- webauthn.ts
- compilerOptions
- Web Styling & Tooling Dependencies
- FirebaseFirestoreService
- AuthPreferences
- Web External Libraries & Bundles
- InvoicePreviewScreen.kt
- AppUpdateManager
- Standard Git Pull Request, Build & Release Workflow
- biometric/route.ts
- neoBrutal
- layout.tsx
- gradlew
- login/route.ts
- RoboGyaan Official Logo Branding Asset
- Suman Mondal Official Authorised Signatory Signature Asset
- check/route.ts
- version/route.ts
- Next.js App Router Agent Development Rules
- eslint.config.mjs
- next.config.ts
- postcss.config.mjs
- Dp
- Android Drawable RoboGyaan Symbol Asset
- RoboGyaan Connected Circular Nodes Symbol Asset
- Web Public File SVG Asset
- Web Public Globe SVG Asset
- Web Public Next.js Logo SVG Asset
- Web Public Directory RoboGyaan Symbol Asset
- Web Public Vercel Logo SVG Asset
- Web Public Window Icon SVG Asset
- Next.js Web Portal Bootstrap Guide

## God Nodes (most connected - your core abstractions)
1. `InvoiceViewModel` - 20 edges
2. `compilerOptions` - 16 edges
3. `FirebaseFirestoreService` - 15 edges
4. `AuthPreferences` - 15 edges
5. `AuthGate()` - 12 edges
6. `InvoiceData` - 10 edges
7. `MainScreen()` - 10 edges
8. `InvoiceHistoryItem` - 9 edges
9. `neoBrutal()` - 9 edges
10. `InvoiceEditorScreen()` - 9 edges

## Surprising Connections (you probably didn't know these)
- `RoboGyaan Official Logo Branding Asset` --semantically_similar_to--> `Android Drawable RoboGyaan Logo Asset`  [INFERRED] [semantically similar]
  robogyaan_logo.png → android/app/src/main/res/drawable/ic_robogyaan_logo.png
- `RoboGyaan Official Logo Branding Asset` --semantically_similar_to--> `Web Public Directory RoboGyaan Logo Asset`  [INFERRED] [semantically similar]
  robogyaan_logo.png → web/public/robogyaan_logo.png
- `Suman Mondal Official Authorised Signatory Signature Asset` --semantically_similar_to--> `Android Drawable Signatory Signature Asset`  [INFERRED] [semantically similar]
  signature.png → android/app/src/main/res/drawable/ic_signature.png
- `Suman Mondal Official Authorised Signatory Signature Asset` --semantically_similar_to--> `Web Public Directory Signatory Signature Asset`  [INFERRED] [semantically similar]
  signature.png → web/public/signature.png
- `InvoicePreviewScreen()` --calls--> `paginateInvoiceItems()`  [INFERRED]
  android/app/src/main/java/com/robogyaan/invoice/ui/preview/InvoicePreviewScreen.kt → android/app/src/main/java/com/robogyaan/invoice/data/InvoiceModels.kt

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Cross-Platform Realtime Autosave & Cloud Synchronization** — _agents_syntax_style_firestore_autosave_standard, web_lib_firebase, android_app_src_main_java_com_robogyaan_invoice_data_firebasefirestoreservice [INFERRED 0.85]
- **Neo-Brutalist Visual Identity & Component System** — _agents_syntax_style_neobrutalist_identity, readme_robogyaan_invoice_generation_suite, web_components_neobrutalbutton [INFERRED 0.85]

## Communities (40 total, 17 thin omitted)

### Community 0 - "Web Invoice Editor & History"
Cohesion: 0.07
Nodes (46): InvoicePage(), HistorySidebar(), HistorySidebarProps, InvoiceEditor(), InvoiceEditorProps, InvoicePreview, InvoicePreviewProps, NeoBrutalButton() (+38 more)

### Community 1 - "InvoiceViewModel"
Cohesion: 0.07
Nodes (16): InvoiceData, InvoiceItem, InvoicePageSlice, paginateInvoiceItems(), PaymentMethod, BANK_DRAFT, CASH, CHEQUE (+8 more)

### Community 2 - "MainActivity.kt"
Cohesion: 0.13
Nodes (26): HandDrawnAsteriskIcon(), Color, Modifier, MainActivity, MainScreen(), BoxGridBackground(), Modifier, HistorySidebarSheet() (+18 more)

### Community 3 - "webauthn.ts"
Cohesion: 0.15
Nodes (28): BiometricsManagementPage(), CloudBiometricCred, AuthGate(), AuthGateProps, InteractiveGridBackground(), InteractiveGridBackgroundProps, AUTH_SALT, hashPasswordWithArgon2() (+20 more)

### Community 4 - "compilerOptions"
Cohesion: 0.07
Nodes (28): dom, dom.iterable, esnext, **/*.mts, .next/dev/types/**/*.ts, next-env.d.ts, .next/types/**/*.ts, node_modules (+20 more)

### Community 5 - "Web Styling & Tooling Dependencies"
Cohesion: 0.08
Nodes (25): eslint, eslint-config-next, tailwindcss, @tailwindcss/postcss, @types/node, @types/react, @types/react-dom, typescript (+17 more)

### Community 6 - "FirebaseFirestoreService"
Cohesion: 0.33
Nodes (7): FirebaseFirestoreService, InvoiceHistoryItem, Context, InvoiceData, BilledParty, SenderParty, JSONObject

### Community 7 - "AuthPreferences"
Cohesion: 0.17
Nodes (6): AuthPreferences, Context, LoginAuthMode, FINGERPRINT, PASSWORD, LoginScreen()

### Community 8 - "Web External Libraries & Bundles"
Cohesion: 0.11
Nodes (19): firebase, hash-wasm, html2canvas, html-to-image, jspdf, lucide-react, next, react (+11 more)

### Community 9 - "InvoicePreviewScreen.kt"
Cohesion: 0.19
Nodes (13): InvoicePreviewScreen(), InvoiceSheetCard(), InvoiceData, Modifier, Context, InvoiceData, PdfGenerator, Apple Face ID TrueDepth Biometric Architecture (+5 more)

### Community 10 - "AppUpdateManager"
Cohesion: 0.38
Nodes (3): AppUpdateManager, Context, UpdateInfo

### Community 11 - "Standard Git Pull Request, Build & Release Workflow"
Cohesion: 0.22
Nodes (9): Android APK Compilation with JDK 17 Pipeline, GitHub Release Publishing with Tagged APK Asset, PR Squash-Merge and Branch Cleanup Protocol, Standard Git Pull Request, Build & Release Workflow, RoboGyaan Invoice Suite - Agent Instructions & Architecture Guide, Argon2id Client-Side Password Hashing Security Standard, Firebase Firestore Realtime Autosave & Offline Fallback, RoboGyaan Syntax & Design Style Guidelines (+1 more)

### Community 12 - "biometric/route.ts"
Cohesion: 0.43
Nodes (6): EXPECTED_EMAIL, EXPECTED_HASH, GET(), getCloudBiometricCredentials(), POST(), sanitizeDocId()

### Community 13 - "neoBrutal"
Cohesion: 0.80
Nodes (5): Color, Dp, Modifier, neoBrutal(), neoBrutalClickable()

### Community 14 - "layout.tsx"
Cohesion: 0.40
Nodes (3): metadata, poppins, virgil

### Community 15 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 17 - "RoboGyaan Official Logo Branding Asset"
Cohesion: 0.67
Nodes (3): Android Drawable RoboGyaan Logo Asset, RoboGyaan Official Logo Branding Asset, Web Public Directory RoboGyaan Logo Asset

### Community 18 - "Suman Mondal Official Authorised Signatory Signature Asset"
Cohesion: 0.67
Nodes (3): Android Drawable Signatory Signature Asset, Suman Mondal Official Authorised Signatory Signature Asset, Web Public Directory Signatory Signature Asset

## Knowledge Gaps
- **110 isolated node(s):** `NeoBrutalButtonProps`, `NeoBrutalCardProps`, `NeoBrutalModalProps`, `BilledParty`, `SenderParty` (+105 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **17 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `PDF & Invoice Preview Table Dual-Box Footer Architecture (v1.6.2)` connect `InvoicePreviewScreen.kt` to `Web Invoice Editor & History`?**
  _High betweenness centrality (0.138) - this node is a cross-community bridge._
- **Why does `Firebase Firestore Realtime Autosave & Offline Fallback` connect `Standard Git Pull Request, Build & Release Workflow` to `Web Invoice Editor & History`, `FirebaseFirestoreService`?**
  _High betweenness centrality (0.081) - this node is a cross-community bridge._
- **Why does `InvoiceViewModel` connect `InvoiceViewModel` to `MainActivity.kt`?**
  _High betweenness centrality (0.054) - this node is a cross-community bridge._
- **What connects `NeoBrutalButtonProps`, `NeoBrutalCardProps`, `NeoBrutalModalProps` to the rest of the system?**
  _110 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Web Invoice Editor & History` be split into smaller, more focused modules?**
  _Cohesion score 0.0715846994535519 - nodes in this community are weakly interconnected._
- **Should `InvoiceViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.07057057057057058 - nodes in this community are weakly interconnected._
- **Should `MainActivity.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.13012477718360071 - nodes in this community are weakly interconnected._