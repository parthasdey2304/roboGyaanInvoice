# Graph Report - roboGyaanInvoice  (2026-09-30)

## Corpus Check
- Corpus is ~37,430 words - fits in a single context window. You may not need a graph.

## Summary
- 305 nodes · 543 edges · 25 communities (17 shown, 8 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 17 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Invoice PDF & Preview
- Android Compose UI
- Web Next.js Pages
- Invoice Data Models
- Authentication & Login
- AI Prompt & Gemini
- Firestore History
- Settings & Updates
- Theme & Styling
- PDF Generation
- Firebase Services
- Build Configuration
- Editor Components
- Neo-Brutal Design
- Utility Functions
- Navigation & Routing
- Version Management
- Web API Routes
- Box Grid Background
- Kotlin Compose Core

## God Nodes (most connected - your core abstractions)
1. `InvoiceViewModel` - 23 edges
2. `compilerOptions` - 16 edges
3. `FirebaseFirestoreService` - 14 edges
4. `MainScreen()` - 12 edges
5. `neoBrutal()` - 12 edges
6. `InvoiceData` - 11 edges
7. `InvoiceEditorScreen()` - 10 edges
8. `AuthPreferences` - 10 edges
9. `InvoiceHistoryItem` - 9 edges
10. `NeoBrutalButton()` - 9 edges

## Surprising Connections (you probably didn't know these)
- `MainActivity` --references--> `InvoiceViewModel`  [EXTRACTED]
  android/app/src/main/java/com/robogyaan/invoice/MainActivity.kt → android/app/src/main/java/com/robogyaan/invoice/ui/InvoiceViewModel.kt
- `MainScreen()` --references--> `InvoiceViewModel`  [EXTRACTED]
  android/app/src/main/java/com/robogyaan/invoice/MainActivity.kt → android/app/src/main/java/com/robogyaan/invoice/ui/InvoiceViewModel.kt
- `MainScreen()` --calls--> `InvoicePreviewScreen()`  [EXTRACTED]
  android/app/src/main/java/com/robogyaan/invoice/MainActivity.kt → android/app/src/main/java/com/robogyaan/invoice/ui/preview/InvoicePreviewScreen.kt
- `InvoiceEditorScreen()` --references--> `InvoiceViewModel`  [EXTRACTED]
  android/app/src/main/java/com/robogyaan/invoice/ui/components/InvoiceEditorScreen.kt → android/app/src/main/java/com/robogyaan/invoice/ui/InvoiceViewModel.kt
- `LoginScreen()` --calls--> `BoxGridBackground()`  [INFERRED]
  android/app/src/main/java/com/robogyaan/invoice/ui/components/LoginScreen.kt → android/app/src/main/java/com/robogyaan/invoice/ui/components/BoxGridBackground.kt

## Import Cycles
- None detected.

## Communities (25 total, 8 thin omitted)

### Community 0 - "Invoice PDF & Preview"
Cohesion: 0.08
Nodes (39): InvoicePage(), AuthGate(), AuthGateProps, HistorySidebar(), HistorySidebarProps, InteractiveGridBackground(), InteractiveGridBackgroundProps, InvoiceEditor() (+31 more)

### Community 1 - "Android Compose UI"
Cohesion: 0.07
Nodes (23): InvoiceData, InvoiceItem, InvoicePageSlice, paginateInvoiceItems(), PaymentMethod, BANK_DRAFT, CASH, CHEQUE (+15 more)

### Community 2 - "Web Next.js Pages"
Cohesion: 0.13
Nodes (26): HandDrawnAsteriskIcon(), Color, Modifier, MainActivity, MainScreen(), BoxGridBackground(), Modifier, HistorySidebarSheet() (+18 more)

### Community 3 - "Invoice Data Models"
Cohesion: 0.07
Nodes (28): dom, dom.iterable, esnext, **/*.mts, .next/dev/types/**/*.ts, next-env.d.ts, .next/types/**/*.ts, node_modules (+20 more)

### Community 4 - "Authentication & Login"
Cohesion: 0.08
Nodes (25): eslint, eslint-config-next, tailwindcss, @tailwindcss/postcss, @types/node, @types/react, @types/react-dom, typescript (+17 more)

### Community 5 - "AI Prompt & Gemini"
Cohesion: 0.36
Nodes (7): FirebaseFirestoreService, InvoiceHistoryItem, Context, InvoiceData, BilledParty, SenderParty, JSONObject

### Community 6 - "Firestore History"
Cohesion: 0.11
Nodes (19): firebase, hash-wasm, html2canvas, html-to-image, jspdf, lucide-react, next, react (+11 more)

### Community 7 - "Settings & Updates"
Cohesion: 0.18
Nodes (13): InvoicePreview, RobogyaanLogo(), RobogyaanLogoProps, numberToWordsIndian(), convertThreeDigits(), convertTwoDigits(), CONTINUATION_PAGE_MAX_ITEMS, FINAL_PAGE_WITH_FOOTER_MAX (+5 more)

### Community 9 - "PDF Generation"
Cohesion: 0.38
Nodes (3): AppUpdateManager, Context, UpdateInfo

### Community 10 - "Firebase Services"
Cohesion: 0.80
Nodes (5): Color, Dp, Modifier, neoBrutal(), neoBrutalClickable()

### Community 12 - "Editor Components"
Cohesion: 0.40
Nodes (3): metadata, poppins, virgil

### Community 13 - "Neo-Brutal Design"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **80 isolated node(s):** `CASH`, `UPI`, `CHEQUE`, `BANK_DRAFT`, `NEFT` (+75 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **8 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `InvoiceViewModel` connect `Android Compose UI` to `Web Next.js Pages`?**
  _High betweenness centrality (0.048) - this node is a cross-community bridge._
- **Why does `MainScreen()` connect `Web Next.js Pages` to `Android Compose UI`, `AI Prompt & Gemini`?**
  _High betweenness centrality (0.041) - this node is a cross-community bridge._
- **Why does `AuthPreferences` connect `Theme & Styling` to `Web Next.js Pages`?**
  _High betweenness centrality (0.021) - this node is a cross-community bridge._
- **What connects `CASH`, `UPI`, `CHEQUE` to the rest of the system?**
  _80 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Invoice PDF & Preview` be split into smaller, more focused modules?**
  _Cohesion score 0.07896575821104122 - nodes in this community are weakly interconnected._
- **Should `Android Compose UI` be split into smaller, more focused modules?**
  _Cohesion score 0.06852497096399536 - nodes in this community are weakly interconnected._
- **Should `Web Next.js Pages` be split into smaller, more focused modules?**
  _Cohesion score 0.1319073083778966 - nodes in this community are weakly interconnected._