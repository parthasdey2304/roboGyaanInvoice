# Graph Report - roboGyaanInvoice  (2026-10-01)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 307 nodes · 536 edges · 25 communities (18 shown, 7 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 23 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `67238bca`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

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
- Signing & Release
- Web API Routes
- Box Grid Background
- Web Components

## God Nodes (most connected - your core abstractions)
1. `InvoiceViewModel` - 20 edges
2. `compilerOptions` - 16 edges
3. `FirebaseFirestoreService` - 14 edges
4. `InvoiceData` - 11 edges
5. `AuthPreferences` - 10 edges
6. `neoBrutal()` - 10 edges
7. `MainScreen()` - 10 edges
8. `InvoiceHistoryItem` - 9 edges
9. `InvoiceEditorScreen()` - 9 edges
10. `NumberToWordsIndian` - 8 edges

## Surprising Connections (you probably didn't know these)
- `MainScreen()` --calls--> `SettingsDialog()`  [INFERRED]
  android/app/src/main/java/com/robogyaan/invoice/MainActivity.kt → android/app/src/main/java/com/robogyaan/invoice/ui/components/SettingsDialog.kt
- `MainScreen()` --calls--> `InvoicePreviewScreen()`  [INFERRED]
  android/app/src/main/java/com/robogyaan/invoice/MainActivity.kt → android/app/src/main/java/com/robogyaan/invoice/ui/preview/InvoicePreviewScreen.kt
- `HistorySidebarProps` --references--> `InvoiceData`  [EXTRACTED]
  web/components/HistorySidebar.tsx → web/lib/types.ts
- `InvoiceEditorProps` --references--> `InvoiceData`  [EXTRACTED]
  web/components/InvoiceEditor.tsx → web/lib/types.ts
- `InvoicePreviewProps` --references--> `InvoiceData`  [EXTRACTED]
  web/components/InvoicePreview.tsx → web/lib/types.ts

## Import Cycles
- None detected.

## Communities (25 total, 7 thin omitted)

### Community 0 - "Invoice PDF & Preview"
Cohesion: 0.07
Nodes (46): InvoicePage(), HistorySidebar(), HistorySidebarProps, InvoiceEditor(), InvoiceEditorProps, InvoicePreview, InvoicePreviewProps, NeoBrutalButton() (+38 more)

### Community 1 - "Android Compose UI"
Cohesion: 0.07
Nodes (28): dom, dom.iterable, esnext, **/*.mts, .next/dev/types/**/*.ts, next-env.d.ts, .next/types/**/*.ts, node_modules (+20 more)

### Community 2 - "Web Next.js Pages"
Cohesion: 0.14
Nodes (13): HandDrawnAsteriskIcon(), Modifier, MainActivity, BoxGridBackground(), Modifier, AuthPreferences, Context, LoginScreen() (+5 more)

### Community 3 - "Invoice Data Models"
Cohesion: 0.18
Nodes (21): MainScreen(), HistorySidebarSheet(), InvoiceData, InvoiceEditorScreen(), InvoiceData, Modifier, Color, Dp (+13 more)

### Community 4 - "Authentication & Login"
Cohesion: 0.10
Nodes (6): InvoiceViewModel, InvoiceData, NumberToWordsIndian, PaymentMethod, StateFlow, ViewModel

### Community 5 - "AI Prompt & Gemini"
Cohesion: 0.08
Nodes (25): eslint, eslint-config-next, tailwindcss, @tailwindcss/postcss, @types/node, @types/react, @types/react-dom, typescript (+17 more)

### Community 6 - "Firestore History"
Cohesion: 0.14
Nodes (18): InvoiceData, InvoiceItem, InvoicePageSlice, paginateInvoiceItems(), PaymentMethod, BANK_DRAFT, CASH, CHEQUE (+10 more)

### Community 7 - "Settings & Updates"
Cohesion: 0.36
Nodes (7): FirebaseFirestoreService, InvoiceHistoryItem, Context, InvoiceData, BilledParty, SenderParty, JSONObject

### Community 8 - "Theme & Styling"
Cohesion: 0.11
Nodes (19): firebase, hash-wasm, html2canvas, html-to-image, jspdf, lucide-react, next, react (+11 more)

### Community 9 - "PDF Generation"
Cohesion: 0.38
Nodes (3): AppUpdateManager, Context, UpdateInfo

### Community 10 - "Firebase Services"
Cohesion: 0.31
Nodes (6): AuthGate(), AuthGateProps, InteractiveGridBackground(), InteractiveGridBackgroundProps, AUTH_SALT, hashPasswordWithArgon2()

### Community 11 - "Build Configuration"
Cohesion: 0.40
Nodes (3): metadata, poppins, virgil

### Community 12 - "Editor Components"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **80 isolated node(s):** `NeoBrutalButtonProps`, `NeoBrutalCardProps`, `NeoBrutalModalProps`, `BilledParty`, `SenderParty` (+75 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **7 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `InvoiceViewModel` connect `Authentication & Login` to `Invoice Data Models`?**
  _High betweenness centrality (0.043) - this node is a cross-community bridge._
- **What connects `NeoBrutalButtonProps`, `NeoBrutalCardProps`, `NeoBrutalModalProps` to the rest of the system?**
  _80 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Invoice PDF & Preview` be split into smaller, more focused modules?**
  _Cohesion score 0.07213114754098361 - nodes in this community are weakly interconnected._
- **Should `Android Compose UI` be split into smaller, more focused modules?**
  _Cohesion score 0.06896551724137931 - nodes in this community are weakly interconnected._
- **Should `Web Next.js Pages` be split into smaller, more focused modules?**
  _Cohesion score 0.13846153846153847 - nodes in this community are weakly interconnected._
- **Should `Authentication & Login` be split into smaller, more focused modules?**
  _Cohesion score 0.09538461538461539 - nodes in this community are weakly interconnected._
- **Should `AI Prompt & Gemini` be split into smaller, more focused modules?**
  _Cohesion score 0.07692307692307693 - nodes in this community are weakly interconnected._