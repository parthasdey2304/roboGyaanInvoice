# Graph Report - roboGyaanInvoice  (2026-10-01)

## Corpus Check
- 32 files · ~44,859 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 345 nodes · 599 edges · 28 communities (18 shown, 10 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 8 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Web Invoice Editor & History
- Android Main Activity & Navigation
- Invoice Domain Models & State
- TypeScript Compiler Options
- Android Compose Editor UI
- Web Styling & Tooling Dependencies
- Authentication & Biometrics UI
- Android Firebase Sync Service
- Web External Libraries & Bundles
- Android Invoice ViewModel
- In-App Updater & Release Checker
- Web Root Layout & Font Setup
- Gradle Wrapper Build Scripts
- WebAuthn Biometric API
- Admin Password Login API
- Session Check API
- Release Version API
- ESLint Configuration
- Next.js Build Configuration
- PostCSS Configuration
- Compose Color Definitions
- Neo-Brutal Modifiers
- Compose Typography System

## God Nodes (most connected - your core abstractions)
1. `InvoiceViewModel` - 20 edges
2. `compilerOptions` - 16 edges
3. `AuthPreferences` - 16 edges
4. `FirebaseFirestoreService` - 15 edges
5. `InvoiceData` - 11 edges
6. `neoBrutal()` - 10 edges
7. `AuthGate()` - 10 edges
8. `InvoiceHistoryItem` - 9 edges
9. `PaymentMethod` - 8 edges
10. `NumberToWordsIndian` - 8 edges

## Surprising Connections (you probably didn't know these)
- `HistorySidebarProps` --references--> `InvoiceData`  [EXTRACTED]
  web/components/HistorySidebar.tsx → web/lib/types.ts
- `InvoiceEditorProps` --references--> `InvoiceData`  [EXTRACTED]
  web/components/InvoiceEditor.tsx → web/lib/types.ts
- `InvoicePreviewProps` --references--> `InvoiceData`  [EXTRACTED]
  web/components/InvoicePreview.tsx → web/lib/types.ts
- `InvoiceEditorScreen()` --references--> `InvoiceViewModel`  [EXTRACTED]
  android/app/src/main/java/com/robogyaan/invoice/ui/components/InvoiceEditorScreen.kt → android/app/src/main/java/com/robogyaan/invoice/ui/InvoiceViewModel.kt
- `HistorySidebarSheet()` --calls--> `NeoBrutalAlertDialog()`  [INFERRED]
  android/app/src/main/java/com/robogyaan/invoice/ui/components/HistorySidebarSheet.kt → android/app/src/main/java/com/robogyaan/invoice/ui/components/NeoBrutalComponents.kt

## Import Cycles
- None detected.

## Communities (28 total, 10 thin omitted)

### Community 0 - "Web Invoice Editor & History"
Cohesion: 0.07
Nodes (46): InvoicePage(), HistorySidebar(), HistorySidebarProps, InvoiceEditor(), InvoiceEditorProps, InvoicePreview, InvoicePreviewProps, NeoBrutalButton() (+38 more)

### Community 1 - "Android Main Activity & Navigation"
Cohesion: 0.07
Nodes (25): BilledParty, InvoiceData, InvoiceItem, InvoicePageSlice, paginateInvoiceItems(), PaymentMethod, BANK_DRAFT, CASH (+17 more)

### Community 2 - "Invoice Domain Models & State"
Cohesion: 0.10
Nodes (17): HandDrawnAsteriskIcon(), MainActivity, MainScreen(), BoxGridBackground(), Modifier, AuthPreferences, Context, LoginAuthMode (+9 more)

### Community 3 - "TypeScript Compiler Options"
Cohesion: 0.07
Nodes (28): dom, dom.iterable, esnext, **/*.mts, .next/dev/types/**/*.ts, next-env.d.ts, .next/types/**/*.ts, node_modules (+20 more)

### Community 4 - "Android Compose Editor UI"
Cohesion: 0.17
Nodes (22): BiometricsManagementPage(), CloudBiometricCred, AuthGate(), AuthGateProps, InteractiveGridBackground(), InteractiveGridBackgroundProps, AUTH_SALT, hashPasswordWithArgon2() (+14 more)

### Community 5 - "Web Styling & Tooling Dependencies"
Cohesion: 0.08
Nodes (25): eslint, eslint-config-next, tailwindcss, @tailwindcss/postcss, @types/node, @types/react, @types/react-dom, typescript (+17 more)

### Community 6 - "Authentication & Biometrics UI"
Cohesion: 0.18
Nodes (19): HistorySidebarSheet(), InvoiceData, InvoiceEditorScreen(), InvoiceData, Modifier, Color, Dp, Modifier (+11 more)

### Community 7 - "Android Firebase Sync Service"
Cohesion: 0.37
Nodes (5): FirebaseFirestoreService, InvoiceHistoryItem, Context, InvoiceData, JSONObject

### Community 8 - "Web External Libraries & Bundles"
Cohesion: 0.11
Nodes (19): firebase, hash-wasm, html2canvas, html-to-image, jspdf, lucide-react, next, react (+11 more)

### Community 9 - "Android Invoice ViewModel"
Cohesion: 0.38
Nodes (3): AppUpdateManager, Context, UpdateInfo

### Community 10 - "In-App Updater & Release Checker"
Cohesion: 0.43
Nodes (6): EXPECTED_EMAIL, EXPECTED_HASH, GET(), getCloudBiometricCredentials(), POST(), sanitizeDocId()

### Community 12 - "Gradle Wrapper Build Scripts"
Cohesion: 0.40
Nodes (3): metadata, poppins, virgil

### Community 13 - "WebAuthn Biometric API"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **88 isolated node(s):** `NeoBrutalButtonProps`, `NeoBrutalCardProps`, `NeoBrutalModalProps`, `RobogyaanLogoProps`, `BilledParty` (+83 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **10 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `InvoiceViewModel` connect `Android Main Activity & Navigation` to `Authentication & Biometrics UI`?**
  _High betweenness centrality (0.038) - this node is a cross-community bridge._
- **What connects `NeoBrutalButtonProps`, `NeoBrutalCardProps`, `NeoBrutalModalProps` to the rest of the system?**
  _88 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Web Invoice Editor & History` be split into smaller, more focused modules?**
  _Cohesion score 0.07213114754098361 - nodes in this community are weakly interconnected._
- **Should `Android Main Activity & Navigation` be split into smaller, more focused modules?**
  _Cohesion score 0.06871035940803383 - nodes in this community are weakly interconnected._
- **Should `Invoice Domain Models & State` be split into smaller, more focused modules?**
  _Cohesion score 0.09759759759759759 - nodes in this community are weakly interconnected._
- **Should `TypeScript Compiler Options` be split into smaller, more focused modules?**
  _Cohesion score 0.06896551724137931 - nodes in this community are weakly interconnected._
- **Should `Web Styling & Tooling Dependencies` be split into smaller, more focused modules?**
  _Cohesion score 0.07692307692307693 - nodes in this community are weakly interconnected._