# Graph Report - roboGyaanInvoice  (2026-10-01)

## Corpus Check
- Corpus is ~47,051 words - fits in a single context window. You may not need a graph.

## Summary
- 346 nodes · 638 edges · 25 communities (19 shown, 6 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 17 edges (avg confidence: 0.85)
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
- Community 18
- Community 19
- Community 20

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
- `LoginScreen()` --calls--> `BoxGridBackground()`  [INFERRED]
  android/app/src/main/java/com/robogyaan/invoice/ui/components/LoginScreen.kt → android/app/src/main/java/com/robogyaan/invoice/ui/components/BoxGridBackground.kt
- `MainActivity` --references--> `InvoiceViewModel`  [EXTRACTED]
  android/app/src/main/java/com/robogyaan/invoice/MainActivity.kt → android/app/src/main/java/com/robogyaan/invoice/ui/InvoiceViewModel.kt
- `MainScreen()` --references--> `InvoiceViewModel`  [EXTRACTED]
  android/app/src/main/java/com/robogyaan/invoice/MainActivity.kt → android/app/src/main/java/com/robogyaan/invoice/ui/InvoiceViewModel.kt
- `MainScreen()` --calls--> `InvoicePreviewScreen()`  [EXTRACTED]
  android/app/src/main/java/com/robogyaan/invoice/MainActivity.kt → android/app/src/main/java/com/robogyaan/invoice/ui/preview/InvoicePreviewScreen.kt
- `InvoiceEditorScreen()` --references--> `InvoiceViewModel`  [EXTRACTED]
  android/app/src/main/java/com/robogyaan/invoice/ui/components/InvoiceEditorScreen.kt → android/app/src/main/java/com/robogyaan/invoice/ui/InvoiceViewModel.kt

## Import Cycles
- None detected.

## Communities (25 total, 6 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.07
Nodes (46): InvoicePage(), HistorySidebar(), HistorySidebarProps, InvoiceEditor(), InvoiceEditorProps, InvoicePreview, InvoicePreviewProps, NeoBrutalButton() (+38 more)

### Community 1 - "Community 1"
Cohesion: 0.12
Nodes (30): HandDrawnAsteriskIcon(), Color, Modifier, MainActivity, MainScreen(), BoxGridBackground(), Modifier, HistorySidebarSheet() (+22 more)

### Community 2 - "Community 2"
Cohesion: 0.15
Nodes (28): BiometricsManagementPage(), CloudBiometricCred, AuthGate(), AuthGateProps, InteractiveGridBackground(), InteractiveGridBackgroundProps, AUTH_SALT, hashPasswordWithArgon2() (+20 more)

### Community 3 - "Community 3"
Cohesion: 0.07
Nodes (28): dom, dom.iterable, esnext, **/*.mts, .next/dev/types/**/*.ts, next-env.d.ts, .next/types/**/*.ts, node_modules (+20 more)

### Community 4 - "Community 4"
Cohesion: 0.08
Nodes (25): eslint, eslint-config-next, tailwindcss, @tailwindcss/postcss, @types/node, @types/react, @types/react-dom, typescript (+17 more)

### Community 5 - "Community 5"
Cohesion: 0.10
Nodes (6): InvoiceViewModel, InvoiceData, NumberToWordsIndian, PaymentMethod, StateFlow, ViewModel

### Community 6 - "Community 6"
Cohesion: 0.13
Nodes (18): InvoiceData, InvoiceItem, InvoicePageSlice, paginateInvoiceItems(), PaymentMethod, BANK_DRAFT, CASH, CHEQUE (+10 more)

### Community 7 - "Community 7"
Cohesion: 0.33
Nodes (7): FirebaseFirestoreService, InvoiceHistoryItem, Context, InvoiceData, BilledParty, SenderParty, JSONObject

### Community 8 - "Community 8"
Cohesion: 0.17
Nodes (6): AuthPreferences, Context, LoginAuthMode, FINGERPRINT, PASSWORD, LoginScreen()

### Community 9 - "Community 9"
Cohesion: 0.11
Nodes (19): firebase, hash-wasm, html2canvas, html-to-image, jspdf, lucide-react, next, react (+11 more)

### Community 10 - "Community 10"
Cohesion: 0.38
Nodes (3): AppUpdateManager, Context, UpdateInfo

### Community 11 - "Community 11"
Cohesion: 0.43
Nodes (6): EXPECTED_EMAIL, EXPECTED_HASH, GET(), getCloudBiometricCredentials(), POST(), sanitizeDocId()

### Community 12 - "Community 12"
Cohesion: 0.40
Nodes (3): metadata, poppins, virgil

### Community 13 - "Community 13"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **88 isolated node(s):** `CASH`, `UPI`, `CHEQUE`, `BANK_DRAFT`, `NEFT` (+83 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **6 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `InvoiceViewModel` connect `Community 5` to `Community 1`, `Community 6`?**
  _High betweenness centrality (0.041) - this node is a cross-community bridge._
- **Why does `MainScreen()` connect `Community 1` to `Community 5`, `Community 6`, `Community 7`?**
  _High betweenness centrality (0.035) - this node is a cross-community bridge._
- **Why does `AuthPreferences` connect `Community 8` to `Community 1`?**
  _High betweenness centrality (0.026) - this node is a cross-community bridge._
- **What connects `CASH`, `UPI`, `CHEQUE` to the rest of the system?**
  _88 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.07213114754098361 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.11875843454790823 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.14583333333333334 - nodes in this community are weakly interconnected._