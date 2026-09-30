# Graph Report - roboGyaanInvoice  (2026-09-30)

## Corpus Check
- 4 files · ~36,887 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 344 nodes · 526 edges · 52 communities (16 shown, 30 thin omitted)
- Extraction: 95% EXTRACTED · 5% INFERRED · 0% AMBIGUOUS · INFERRED: 24 edges (avg confidence: 0.88)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Web Application & History Sidebar
- Web Application & History Sidebar
- TypeScript Compiler Configuration
- Next.js Build & Linting Tooling
- Android Invoice ViewModel State
- Firebase Firestore Cloud Synchronization
- TypeScript Compiler Configuration
- Firebase Firestore Cloud Synchronization
- Invoice Data Models & Pagination Engine
- Authentication & User Preferences
- In-App Software Updater System
- Design System & Suite Architecture
- Neo-Brutalist Compose Modifiers
- Web Auth Login Endpoint
- Android Gradle Build Scripts
- Authorised Signatory Signature Assets
- Web Auth Check Endpoint
- Next.js Agent Guidelines & Context
- Web Auth Logout Endpoint
- ESLint Configuration
- Next.js Configuration
- Client-Side Argon2id Auth Gate
- Cloud Firestore Sync Engine
- Android App Gradle Configuration
- Firebase Firestore Cloud Synchronization
- Android Gradle Build Scripts
- Android Invoice ViewModel State
- UI Spacing Dimensions
- Invoice Sender Details
- Compose Typography System
- Invoice Payment Details
- Android Root Gradle Configuration
- Android Logo Symbol Asset
- Android Gradle Settings
- RoboGyaan Circular Symbol Asset
- Android Gradle Build Scripts
- Web Public Symbol Asset
- Web Public Window Icon Asset
- Web Public Directory RoboGyaan Symbol Asset
- Web Public Vercel Logo SVG Asset
- Web Public Window Icon SVG Asset
- Next.js Web Portal Bootstrap Guide
- Community 48
- Community 49
- Community 50
- Community 51

## God Nodes (most connected - your core abstractions)
1. `InvoiceViewModel` - 18 edges
2. `InvoiceData` - 18 edges
3. `compilerOptions` - 16 edges
4. `FirebaseFirestoreService` - 14 edges
5. `InvoiceHistoryItem` - 9 edges
6. `AuthPreferences` - 9 edges
7. `PaymentMethod` - 8 edges
8. `InvoiceEditorScreen()` - 8 edges
9. `InvoiceData` - 7 edges
10. `BilledParty` - 7 edges

## Surprising Connections (you probably didn't know these)
- `RoboGyaan Official Logo Branding Asset` --semantically_similar_to--> `Android Drawable RoboGyaan Logo Asset`  [INFERRED] [semantically similar]
  robogyaan_logo.png → android/app/src/main/res/drawable/ic_robogyaan_logo.png
- `RoboGyaan Official Logo Branding Asset` --semantically_similar_to--> `Web Public Directory RoboGyaan Logo Asset`  [INFERRED] [semantically similar]
  robogyaan_logo.png → web/public/robogyaan_logo.png
- `Suman Mondal Official Authorised Signatory Signature Asset` --semantically_similar_to--> `Android Drawable Signatory Signature Asset`  [INFERRED] [semantically similar]
  signature.png → android/app/src/main/res/drawable/ic_signature.png
- `Suman Mondal Official Authorised Signatory Signature Asset` --semantically_similar_to--> `Web Public Directory Signatory Signature Asset`  [INFERRED] [semantically similar]
  signature.png → web/public/signature.png
- `HistorySidebarSheet()` --references--> `InvoiceData`  [EXTRACTED]
  android/app/src/main/java/com/robogyaan/invoice/ui/components/HistorySidebarSheet.kt → android/app/src/main/java/com/robogyaan/invoice/data/InvoiceModels.kt

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Cross-Platform Realtime Autosave & Cloud Synchronization** — _agents_syntax_style_firestore_autosave_standard, web_lib_firebase, android_app_src_main_java_com_robogyaan_invoice_data_firebasefirestoreservice [INFERRED 0.85]
- **Neo-Brutalist Visual Identity & Component System** — _agents_syntax_style_neobrutalist_identity, web_components_neobrutalbutton [INFERRED 0.85]

## Communities (52 total, 30 thin omitted)

### Community 0 - "Web Application & History Sidebar"
Cohesion: 0.06
Nodes (45): HistorySidebar(), HistorySidebarProps, InvoiceEditor(), InvoiceEditorProps, InvoicePreview, InvoicePreviewProps, NeoBrutalButton(), NeoBrutalButtonProps (+37 more)

### Community 1 - "Web Application & History Sidebar"
Cohesion: 0.15
Nodes (22): HandDrawnAsteriskIcon(), MainActivity, MainScreen(), BoxGridBackground(), HistorySidebarSheet(), InvoiceEditorScreen(), InvoiceViewModel, Modifier (+14 more)

### Community 2 - "TypeScript Compiler Configuration"
Cohesion: 0.08
Nodes (12): PaymentMethod, BANK_DRAFT, CASH, CHEQUE, NEFT, UPI, InvoiceViewModel, InvoiceData (+4 more)

### Community 3 - "Next.js Build & Linting Tooling"
Cohesion: 0.23
Nodes (12): FirebaseFirestoreService, InvoiceHistoryItem, Context, BilledParty, InvoiceData, InvoiceItem, InvoicePageSlice, paginateInvoiceItems() (+4 more)

### Community 4 - "Android Invoice ViewModel State"
Cohesion: 0.07
Nodes (28): dom, dom.iterable, esnext, **/*.mts, .next/dev/types/**/*.ts, next-env.d.ts, .next/types/**/*.ts, node_modules (+20 more)

### Community 5 - "Firebase Firestore Cloud Synchronization"
Cohesion: 0.08
Nodes (25): eslint, eslint-config-next, tailwindcss, @tailwindcss/postcss, @types/node, @types/react, @types/react-dom, typescript (+17 more)

### Community 6 - "TypeScript Compiler Configuration"
Cohesion: 0.11
Nodes (19): firebase, hash-wasm, html2canvas, html-to-image, jspdf, lucide-react, next, react (+11 more)

### Community 7 - "Firebase Firestore Cloud Synchronization"
Cohesion: 0.12
Nodes (15): Android APK Compilation with JDK 17 Pipeline, GitHub Release Publishing with Tagged APK Asset, PR Squash-Merge and Branch Cleanup Protocol, Standard Git Pull Request, Build & Release Workflow, RoboGyaan Invoice Suite - Agent Instructions & Architecture Guide, Argon2id Client-Side Password Hashing Security Standard, Firebase Firestore Realtime Autosave & Offline Fallback, RoboGyaan Syntax & Design Style Guidelines (+7 more)

### Community 8 - "Invoice Data Models & Pagination Engine"
Cohesion: 0.38
Nodes (3): AppUpdateManager, UpdateInfo, Context

### Community 10 - "In-App Software Updater System"
Cohesion: 0.80
Nodes (5): Color, Dp, Modifier, neoBrutal(), neoBrutalClickable()

### Community 11 - "Design System & Suite Architecture"
Cohesion: 0.67
Nodes (5): InvoicePreviewScreen(), InvoiceSheetCard(), Modifier, InvoiceData, InvoicePageSlice

### Community 12 - "Neo-Brutalist Compose Modifiers"
Cohesion: 0.40
Nodes (3): metadata, poppins, virgil

### Community 13 - "Web Auth Login Endpoint"
Cohesion: 0.50
Nodes (4): Android Payment Method Draft Optimization, Deep Obsidian Dark Mode Palette, Responsive Split Page Resizer Specification, RoboGyaan Invoice Suite Documentation

### Community 14 - "Android Gradle Build Scripts"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 16 - "Web Auth Check Endpoint"
Cohesion: 0.67
Nodes (3): Android Drawable RoboGyaan Logo Asset, RoboGyaan Official Logo Branding Asset, Web Public Directory RoboGyaan Logo Asset

### Community 17 - "Next.js Agent Guidelines & Context"
Cohesion: 0.67
Nodes (3): Android Drawable Signatory Signature Asset, Suman Mondal Official Authorised Signatory Signature Asset, Web Public Directory Signatory Signature Asset

## Knowledge Gaps
- **107 isolated node(s):** `HistorySidebarProps`, `InvoiceEditorProps`, `InvoicePreviewProps`, `NeoBrutalButtonProps`, `NeoBrutalCardProps` (+102 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 148 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **30 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Firebase Firestore Realtime Autosave & Offline Fallback` connect `Firebase Firestore Cloud Synchronization` to `Web Application & History Sidebar`, `Next.js Build & Linting Tooling`?**
  _High betweenness centrality (0.164) - this node is a cross-community bridge._
- **Why does `InvoiceData` connect `Next.js Build & Linting Tooling` to `Web Application & History Sidebar`, `TypeScript Compiler Configuration`?**
  _High betweenness centrality (0.080) - this node is a cross-community bridge._
- **Why does `FirebaseFirestoreService` connect `Next.js Build & Linting Tooling` to `Web Application & History Sidebar`?**
  _High betweenness centrality (0.066) - this node is a cross-community bridge._
- **What connects `HistorySidebarProps`, `InvoiceEditorProps`, `InvoicePreviewProps` to the rest of the system?**
  _107 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Web Application & History Sidebar` be split into smaller, more focused modules?**
  _Cohesion score 0.0633879781420765 - nodes in this community are weakly interconnected._
- **Should `TypeScript Compiler Configuration` be split into smaller, more focused modules?**
  _Cohesion score 0.07741935483870968 - nodes in this community are weakly interconnected._
- **Should `Android Invoice ViewModel State` be split into smaller, more focused modules?**
  _Cohesion score 0.06896551724137931 - nodes in this community are weakly interconnected._