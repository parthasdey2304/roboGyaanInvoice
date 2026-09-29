# Graph Report - roboGyaanInvoice  (2026-09-30)

## Corpus Check
- 37 files · ~27,747 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 308 nodes · 491 edges · 44 communities (14 shown, 25 thin omitted)
- Extraction: 95% EXTRACTED · 5% INFERRED · 0% AMBIGUOUS · INFERRED: 26 edges (avg confidence: 0.88)
- Token cost: 2,400 input · 1,200 output

## Community Hubs (Navigation)
- Web Invoice Editor & History UI
- Android Jetpack Compose UI & ViewModel
- TypeScript & Compiler Environment Config
- Invoice Payment Methods & Domain Models
- Web Build & Linting Tooling
- Android Firestore Service & Offline Cache
- Web Client Libraries & Utilities
- Multi-Page Invoice Pagination & Preview Engine
- Agent Guidelines & Release Automation
- Android Neo-Brutalist Layout Modifiers
- Indian Currency Number-to-Words Converter
- Web Root Layout & Typography
- Gradle Wrapper Build Scripts
- Web Auth Login Endpoint
- RoboGyaan Logo Branding Assets
- Authorised Signatory Signature Assets
- Web Auth Check Endpoint
- Next.js Agent Guidelines & Context
- ESLint Configuration
- Next.js Configuration
- PostCSS Configuration
- Client-Side Argon2id Auth Gate
- Cloud Firestore Sync Engine
- Invoice Data Structures
- Invoice Item Schema
- Invoice Recipient Details
- Neo-Brutal Color Palette
- UI Spacing Dimensions
- Invoice Sender Details
- Invoice Payment Details
- Android Logo Symbol Asset
- RoboGyaan Circular Symbol Asset
- Web Public File Icon Asset
- Web Public Globe Icon Asset
- Web Public Next.js Icon Asset
- Web Public Symbol Asset
- Web Public Vercel Icon Asset
- Web Public Window Icon Asset
- Next.js Web App Bootstrap Guide

## God Nodes (most connected - your core abstractions)
1. `InvoiceData` - 21 edges
2. `InvoiceViewModel` - 18 edges
3. `compilerOptions` - 16 edges
4. `FirebaseFirestoreService` - 14 edges
5. `MainScreen()` - 10 edges
6. `InvoiceEditorScreen()` - 10 edges
7. `NeoBrutalButton()` - 10 edges
8. `InvoiceHistoryItem` - 9 edges
9. `InvoiceData` - 8 edges
10. `PaymentMethod` - 8 edges

## Surprising Connections (you probably didn't know these)
- `RoboGyaan Official Logo Branding Asset` --semantically_similar_to--> `Android Drawable RoboGyaan Logo Asset`  [INFERRED] [semantically similar]
  robogyaan_logo.png → android/app/src/main/res/drawable/ic_robogyaan_logo.png
- `RoboGyaan Official Logo Branding Asset` --semantically_similar_to--> `Web Public Directory RoboGyaan Logo Asset`  [INFERRED] [semantically similar]
  robogyaan_logo.png → web/public/robogyaan_logo.png
- `Suman Mondal Official Authorised Signatory Signature Asset` --semantically_similar_to--> `Android Drawable Signatory Signature Asset`  [INFERRED] [semantically similar]
  signature.png → android/app/src/main/res/drawable/ic_signature.png
- `Suman Mondal Official Authorised Signatory Signature Asset` --semantically_similar_to--> `Web Public Directory Signatory Signature Asset`  [INFERRED] [semantically similar]
  signature.png → web/public/signature.png
- `InvoiceEditorProps` --references--> `InvoiceData`  [EXTRACTED]
  web/components/InvoiceEditor.tsx → web/lib/types.ts

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Neo-Brutalist Visual Identity & Component System** — _agents_syntax_style_neobrutalist_identity, readme_robogyaan_invoice_generation_suite, web_components_neobrutalbutton [INFERRED 0.85]
- **Cross-Platform Realtime Autosave & Cloud Synchronization** — _agents_syntax_style_firestore_autosave_standard, web_lib_firebase, android_app_src_main_java_com_robogyaan_invoice_data_firebasefirestoreservice [INFERRED 0.85]
- **Multi-Page A4 Layout & High-Fidelity PDF Export Pipeline** — readme_multipage_pagination_engine, web_components_invoicepreview, android_app_src_main_java_com_robogyaan_invoice_util_pdfgenerator [INFERRED 0.85]

## Communities (44 total, 25 thin omitted)

### Community 0 - "Web Invoice Editor & History UI"
Cohesion: 0.07
Nodes (43): InvoicePage(), HistorySidebar(), HistorySidebarProps, InvoiceEditor(), InvoiceEditorProps, InvoicePreview, InvoicePreviewProps, NeoBrutalButton() (+35 more)

### Community 1 - "Android Jetpack Compose UI & ViewModel"
Cohesion: 0.14
Nodes (21): InvoiceViewModel, MainActivity, MainScreen(), HistorySidebarSheet(), InvoiceEditorScreen(), InvoiceViewModel, Modifier, AuthPreferences (+13 more)

### Community 2 - "TypeScript & Compiler Environment Config"
Cohesion: 0.07
Nodes (28): dom, dom.iterable, esnext, **/*.mts, .next/dev/types/**/*.ts, next-env.d.ts, .next/types/**/*.ts, node_modules (+20 more)

### Community 3 - "Invoice Payment Methods & Domain Models"
Cohesion: 0.09
Nodes (11): PaymentMethod, BANK_DRAFT, CASH, CHEQUE, NEFT, UPI, InvoiceViewModel, InvoiceData (+3 more)

### Community 4 - "Web Build & Linting Tooling"
Cohesion: 0.08
Nodes (25): eslint, eslint-config-next, tailwindcss, @tailwindcss/postcss, @types/node, @types/react, @types/react-dom, typescript (+17 more)

### Community 5 - "Android Firestore Service & Offline Cache"
Cohesion: 0.38
Nodes (7): FirebaseFirestoreService, InvoiceHistoryItem, Context, BilledParty, InvoiceData, SenderParty, JSONObject

### Community 6 - "Web Client Libraries & Utilities"
Cohesion: 0.11
Nodes (19): firebase, hash-wasm, html2canvas, html-to-image, jspdf, lucide-react, next, react (+11 more)

### Community 7 - "Multi-Page Invoice Pagination & Preview Engine"
Cohesion: 0.20
Nodes (12): InvoiceItem, InvoicePageSlice, paginateInvoiceItems(), InvoicePreviewScreen(), InvoiceSheetCard(), Modifier, Context, PdfGenerator (+4 more)

### Community 8 - "Agent Guidelines & Release Automation"
Cohesion: 0.15
Nodes (13): Android APK Compilation with JDK 17 Pipeline, GitHub Release Publishing with Tagged APK Asset, PR Squash-Merge and Branch Cleanup Protocol, Standard Git Pull Request, Build & Release Workflow, RoboGyaan Invoice Suite - Agent Instructions & Architecture Guide, Argon2id Client-Side Password Hashing Security Standard, Firebase Firestore Realtime Autosave & Offline Fallback, RoboGyaan Syntax & Design Style Guidelines (+5 more)

### Community 9 - "Android Neo-Brutalist Layout Modifiers"
Cohesion: 0.80
Nodes (5): Color, Dp, Modifier, neoBrutal(), neoBrutalClickable()

### Community 11 - "Web Root Layout & Typography"
Cohesion: 0.40
Nodes (3): metadata, poppins, virgil

### Community 12 - "Gradle Wrapper Build Scripts"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 14 - "RoboGyaan Logo Branding Assets"
Cohesion: 0.67
Nodes (3): Android Drawable RoboGyaan Logo Asset, RoboGyaan Official Logo Branding Asset, Web Public Directory RoboGyaan Logo Asset

### Community 15 - "Authorised Signatory Signature Assets"
Cohesion: 0.67
Nodes (3): Android Drawable Signatory Signature Asset, Suman Mondal Official Authorised Signatory Signature Asset, Web Public Directory Signatory Signature Asset

## Knowledge Gaps
- **101 isolated node(s):** `NeoBrutalButtonProps`, `NeoBrutalCardProps`, `RobogyaanLogoProps`, `BilledParty`, `SenderParty` (+96 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 136 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **25 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `InvoiceData` connect `Android Firestore Service & Offline Cache` to `Android Jetpack Compose UI & ViewModel`, `Invoice Payment Methods & Domain Models`, `Multi-Page Invoice Pagination & Preview Engine`?**
  _High betweenness centrality (0.122) - this node is a cross-community bridge._
- **Why does `Multi-Page Invoice Pagination & Continuation Header Engine` connect `Multi-Page Invoice Pagination & Preview Engine` to `Web Invoice Editor & History UI`?**
  _High betweenness centrality (0.103) - this node is a cross-community bridge._
- **Why does `Firebase Firestore Realtime Autosave & Offline Fallback` connect `Agent Guidelines & Release Automation` to `Web Invoice Editor & History UI`, `Android Firestore Service & Offline Cache`?**
  _High betweenness centrality (0.062) - this node is a cross-community bridge._
- **What connects `NeoBrutalButtonProps`, `NeoBrutalCardProps`, `RobogyaanLogoProps` to the rest of the system?**
  _101 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Web Invoice Editor & History UI` be split into smaller, more focused modules?**
  _Cohesion score 0.07330827067669173 - nodes in this community are weakly interconnected._
- **Should `Android Jetpack Compose UI & ViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.14260249554367202 - nodes in this community are weakly interconnected._
- **Should `TypeScript & Compiler Environment Config` be split into smaller, more focused modules?**
  _Cohesion score 0.06896551724137931 - nodes in this community are weakly interconnected._