# Graph Report - roboGyaanInvoice  (2026-09-30)

## Corpus Check
- 17 files · ~36,058 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 335 nodes · 524 edges · 48 communities (14 shown, 27 thin omitted)
- Extraction: 95% EXTRACTED · 5% INFERRED · 0% AMBIGUOUS · INFERRED: 24 edges (avg confidence: 0.88)
- Token cost: 0 input · 0 output

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
- Community 20
- Community 21
- Community 22
- Community 23
- Community 24
- Community 27
- Community 28
- Community 29
- Community 30
- Community 31
- Community 32
- Community 33
- Community 36
- Community 38
- Community 40
- Community 41
- Community 42
- Community 43
- Community 44
- Community 45
- Community 46
- Community 47

## God Nodes (most connected - your core abstractions)
1. `InvoiceViewModel` - 18 edges
2. `InvoiceData` - 18 edges
3. `compilerOptions` - 16 edges
4. `FirebaseFirestoreService` - 14 edges
5. `AuthPreferences` - 10 edges
6. `InvoiceHistoryItem` - 9 edges
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

## Communities (48 total, 27 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.06
Nodes (45): HistorySidebar(), HistorySidebarProps, InvoiceEditor(), InvoiceEditorProps, InvoicePreview, InvoicePreviewProps, NeoBrutalButton(), NeoBrutalButtonProps (+37 more)

### Community 1 - "Community 1"
Cohesion: 0.13
Nodes (30): HandDrawnAsteriskIcon(), Modifier, MainActivity, MainScreen(), BoxGridBackground(), Modifier, HistorySidebarSheet(), InvoiceEditorScreen() (+22 more)

### Community 2 - "Community 2"
Cohesion: 0.23
Nodes (12): FirebaseFirestoreService, InvoiceHistoryItem, Context, BilledParty, InvoiceData, InvoiceItem, InvoicePageSlice, paginateInvoiceItems() (+4 more)

### Community 3 - "Community 3"
Cohesion: 0.07
Nodes (28): dom, dom.iterable, esnext, **/*.mts, .next/dev/types/**/*.ts, next-env.d.ts, .next/types/**/*.ts, node_modules (+20 more)

### Community 4 - "Community 4"
Cohesion: 0.08
Nodes (25): eslint, eslint-config-next, tailwindcss, @tailwindcss/postcss, @types/node, @types/react, @types/react-dom, typescript (+17 more)

### Community 5 - "Community 5"
Cohesion: 0.09
Nodes (11): PaymentMethod, BANK_DRAFT, CASH, CHEQUE, NEFT, UPI, InvoiceViewModel, InvoiceData (+3 more)

### Community 6 - "Community 6"
Cohesion: 0.11
Nodes (19): firebase, hash-wasm, html2canvas, html-to-image, jspdf, lucide-react, next, react (+11 more)

### Community 7 - "Community 7"
Cohesion: 0.12
Nodes (15): Android APK Compilation with JDK 17 Pipeline, GitHub Release Publishing with Tagged APK Asset, PR Squash-Merge and Branch Cleanup Protocol, Standard Git Pull Request, Build & Release Workflow, RoboGyaan Invoice Suite - Agent Instructions & Architecture Guide, Argon2id Client-Side Password Hashing Security Standard, Firebase Firestore Realtime Autosave & Offline Fallback, RoboGyaan Syntax & Design Style Guidelines (+7 more)

### Community 9 - "Community 9"
Cohesion: 0.39
Nodes (3): AppUpdateManager, Context, UpdateInfo

### Community 10 - "Community 10"
Cohesion: 0.80
Nodes (5): Color, Dp, Modifier, neoBrutal(), neoBrutalClickable()

### Community 12 - "Community 12"
Cohesion: 0.40
Nodes (3): metadata, poppins, virgil

### Community 13 - "Community 13"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 15 - "Community 15"
Cohesion: 0.67
Nodes (3): Android Drawable RoboGyaan Logo Asset, RoboGyaan Official Logo Branding Asset, Web Public Directory RoboGyaan Logo Asset

### Community 16 - "Community 16"
Cohesion: 0.67
Nodes (3): Android Drawable Signatory Signature Asset, Suman Mondal Official Authorised Signatory Signature Asset, Web Public Directory Signatory Signature Asset

## Knowledge Gaps
- **103 isolated node(s):** `NeoBrutalModalProps`, `RobogyaanLogoProps`, `BilledParty`, `SenderParty`, `app` (+98 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 139 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **27 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Firebase Firestore Realtime Autosave & Offline Fallback` connect `Community 7` to `Community 0`, `Community 2`?**
  _High betweenness centrality (0.173) - this node is a cross-community bridge._
- **Why does `InvoiceData` connect `Community 2` to `Community 1`, `Community 5`?**
  _High betweenness centrality (0.085) - this node is a cross-community bridge._
- **Why does `FirebaseFirestoreService` connect `Community 2` to `Community 1`?**
  _High betweenness centrality (0.070) - this node is a cross-community bridge._
- **What connects `NeoBrutalModalProps`, `RobogyaanLogoProps`, `BilledParty` to the rest of the system?**
  _103 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.0633879781420765 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.12564102564102564 - nodes in this community are weakly interconnected._
- **Should `Community 3` be split into smaller, more focused modules?**
  _Cohesion score 0.06896551724137931 - nodes in this community are weakly interconnected._