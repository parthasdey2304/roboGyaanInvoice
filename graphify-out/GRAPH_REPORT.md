# Graph Report - roboGyaanInvoice  (2026-09-30)

## Corpus Check
- 12 files · ~36,279 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 339 nodes · 520 edges · 52 communities (17 shown, 29 thin omitted)
- Extraction: 95% EXTRACTED · 5% INFERRED · 0% AMBIGUOUS · INFERRED: 24 edges (avg confidence: 0.88)
- Token cost: 120 input · 240 output

## Community Hubs (Navigation)
- Community 0
- Community 1
- Community 2
- Community 3
- Community 4
- Community 5
- Community 6
- Community 7
- Community 8
- Community 9
- Community 10
- Community 11
- Community 12
- Community 13
- Community 14
- Community 15
- Community 16
- Community 17
- Community 18
- Community 19
- Community 20
- Community 22
- Community 23
- Community 24
- Community 25
- Community 26
- Community 28
- Community 29
- Community 30
- Community 31
- Community 32
- Community 33
- Community 34
- Community 35
- Community 36
- Community 39
- Community 41
- Community 43
- Community 44
- Community 45
- Community 46
- Community 47
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

## Communities (52 total, 29 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.06
Nodes (45): HistorySidebar(), HistorySidebarProps, InvoiceEditor(), InvoiceEditorProps, InvoicePreview, InvoicePreviewProps, NeoBrutalButton(), NeoBrutalButtonProps (+37 more)

### Community 1 - "Community 1"
Cohesion: 0.15
Nodes (22): HandDrawnAsteriskIcon(), MainActivity, MainScreen(), BoxGridBackground(), HistorySidebarSheet(), InvoiceEditorScreen(), InvoiceViewModel, Modifier (+14 more)

### Community 2 - "Community 2"
Cohesion: 0.07
Nodes (28): dom, dom.iterable, esnext, **/*.mts, .next/dev/types/**/*.ts, next-env.d.ts, .next/types/**/*.ts, node_modules (+20 more)

### Community 3 - "Community 3"
Cohesion: 0.08
Nodes (25): eslint, eslint-config-next, tailwindcss, @tailwindcss/postcss, @types/node, @types/react, @types/react-dom, typescript (+17 more)

### Community 4 - "Community 4"
Cohesion: 0.10
Nodes (6): InvoiceViewModel, InvoiceData, NumberToWordsIndian, PaymentMethod, StateFlow, ViewModel

### Community 5 - "Community 5"
Cohesion: 0.38
Nodes (7): FirebaseFirestoreService, InvoiceHistoryItem, Context, BilledParty, InvoiceData, SenderParty, JSONObject

### Community 6 - "Community 6"
Cohesion: 0.11
Nodes (19): firebase, hash-wasm, html2canvas, html-to-image, jspdf, lucide-react, next, react (+11 more)

### Community 7 - "Community 7"
Cohesion: 0.12
Nodes (15): Android APK Compilation with JDK 17 Pipeline, GitHub Release Publishing with Tagged APK Asset, PR Squash-Merge and Branch Cleanup Protocol, Standard Git Pull Request, Build & Release Workflow, RoboGyaan Invoice Suite - Agent Instructions & Architecture Guide, Argon2id Client-Side Password Hashing Security Standard, Firebase Firestore Realtime Autosave & Offline Fallback, RoboGyaan Syntax & Design Style Guidelines (+7 more)

### Community 8 - "Community 8"
Cohesion: 0.18
Nodes (11): InvoiceItem, InvoicePageSlice, paginateInvoiceItems(), PaymentMethod, BANK_DRAFT, CASH, CHEQUE, NEFT (+3 more)

### Community 10 - "Community 10"
Cohesion: 0.39
Nodes (3): AppUpdateManager, Context, UpdateInfo

### Community 11 - "Community 11"
Cohesion: 0.80
Nodes (5): Color, Dp, Modifier, neoBrutal(), neoBrutalClickable()

### Community 12 - "Community 12"
Cohesion: 0.67
Nodes (5): InvoicePreviewScreen(), InvoiceSheetCard(), Modifier, InvoiceData, InvoicePageSlice

### Community 13 - "Community 13"
Cohesion: 0.40
Nodes (3): metadata, poppins, virgil

### Community 14 - "Community 14"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 16 - "Community 16"
Cohesion: 0.67
Nodes (3): Deep Obsidian Dark Mode Palette, Responsive Split Page Resizer Specification, RoboGyaan Invoice Suite Documentation

### Community 17 - "Community 17"
Cohesion: 0.67
Nodes (3): Android Drawable RoboGyaan Logo Asset, RoboGyaan Official Logo Branding Asset, Web Public Directory RoboGyaan Logo Asset

### Community 18 - "Community 18"
Cohesion: 0.67
Nodes (3): Android Drawable Signatory Signature Asset, Suman Mondal Official Authorised Signatory Signature Asset, Web Public Directory Signatory Signature Asset

## Knowledge Gaps
- **105 isolated node(s):** `NeoBrutalModalProps`, `RobogyaanLogoProps`, `BilledParty`, `SenderParty`, `InvoiceEditorProps` (+100 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 145 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **29 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Firebase Firestore Realtime Autosave & Offline Fallback` connect `Community 7` to `Community 0`, `Community 5`?**
  _High betweenness centrality (0.168) - this node is a cross-community bridge._
- **Why does `InvoiceData` connect `Community 5` to `Community 8`, `Community 1`, `Community 4`?**
  _High betweenness centrality (0.082) - this node is a cross-community bridge._
- **Why does `FirebaseFirestoreService` connect `Community 5` to `Community 1`?**
  _High betweenness centrality (0.068) - this node is a cross-community bridge._
- **What connects `NeoBrutalModalProps`, `RobogyaanLogoProps`, `BilledParty` to the rest of the system?**
  _105 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.0633879781420765 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.06896551724137931 - nodes in this community are weakly interconnected._
- **Should `Community 3` be split into smaller, more focused modules?**
  _Cohesion score 0.07692307692307693 - nodes in this community are weakly interconnected._