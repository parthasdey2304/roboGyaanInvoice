# Graph Report - roboGyaanInvoice  (2026-09-30)

## Corpus Check
- Corpus is ~23,895 words - fits in a single context window. You may not need a graph.

## Summary
- 259 nodes · 427 edges · 23 communities (12 shown, 6 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 15 edges (avg confidence: 0.85)
- Token cost: 1,200 input · 600 output

## Community Hubs (Navigation)
- Next.js Invoice Web Portal & Editor
- Android Compose MainActivity & UI
- TypeScript DOM & Compiler Config
- Domain Models & Payment Enums
- Web Styling & Linting Pipeline
- Android Cloud Firestore Service
- Web Dependencies & Export Engines
- Architecture Releases & Specs
- Indian Currency Words Converter
- Web Client-Side Argon2 Auth Gate
- Android Native A4 PDF Generator
- Web Layout & Poppins Font Engine
- Gradle Build Wrapper Tooling
- Login API Authentication Route
- Session Check API Route
- ESLint Configuration
- Next.js Engine Configuration
- PostCSS Tailwind Configuration

## God Nodes (most connected - your core abstractions)
1. `InvoiceViewModel` - 23 edges
2. `compilerOptions` - 16 edges
3. `FirebaseFirestoreService` - 13 edges
4. `neoBrutal()` - 11 edges
5. `InvoiceData` - 11 edges
6. `InvoiceEditorScreen()` - 9 edges
7. `NeoBrutalButton()` - 9 edges
8. `InvoiceHistoryItem` - 8 edges
9. `PaymentMethod` - 8 edges
10. `NumberToWordsIndian` - 8 edges

## Surprising Connections (you probably didn't know these)
- `RoboGyaan Invoice Suite v1.3.0` --implements--> `Argon2id Client-Side Authentication Gate`  [EXTRACTED]
  release_notes_v1.3.0.md → web/lib/crypto.ts
- `RoboGyaan Invoice Suite v1.3.0` --implements--> `Firebase Firestore Cloud Sync Engine`  [EXTRACTED]
  release_notes_v1.3.0.md → web/lib/firebase.ts
- `RoboGyaan Invoice Suite Documentation` --documents--> `RoboGyaan Invoice Suite v1.1.0`  [EXTRACTED]
  release_notes.md → release_notes_v1.1.0.md
- `RoboGyaan Invoice Suite Documentation` --documents--> `RoboGyaan Invoice Suite v1.2.0`  [EXTRACTED]
  release_notes.md → release_notes_v1.2.0.md
- `RoboGyaan Invoice Suite Documentation` --documents--> `RoboGyaan Invoice Suite v1.3.0`  [EXTRACTED]
  release_notes.md → release_notes_v1.3.0.md

## Import Cycles
- None detected.

## Communities (23 total, 6 thin omitted)

### Community 0 - "Next.js Invoice Web Portal & Editor"
Cohesion: 0.10
Nodes (31): HistorySidebar(), HistorySidebarProps, InvoiceEditor(), InvoiceEditorProps, InvoicePreview, InvoicePreviewProps, NeoBrutalButton(), NeoBrutalButtonProps (+23 more)

### Community 1 - "Android Compose MainActivity & UI"
Cohesion: 0.11
Nodes (30): InvoiceData, MainActivity, MainScreen(), HistorySidebarSheet(), InvoiceData, InvoiceEditorScreen(), InvoiceData, Modifier (+22 more)

### Community 2 - "TypeScript DOM & Compiler Config"
Cohesion: 0.07
Nodes (28): dom, dom.iterable, esnext, **/*.mts, .next/dev/types/**/*.ts, next-env.d.ts, .next/types/**/*.ts, node_modules (+20 more)

### Community 3 - "Domain Models & Payment Enums"
Cohesion: 0.09
Nodes (11): PaymentMethod, BANK_DRAFT, CASH, CHEQUE, NEFT, UPI, InvoiceViewModel, InvoiceData (+3 more)

### Community 4 - "Web Styling & Linting Pipeline"
Cohesion: 0.08
Nodes (25): eslint, eslint-config-next, tailwindcss, @tailwindcss/postcss, @types/node, @types/react, @types/react-dom, typescript (+17 more)

### Community 5 - "Android Cloud Firestore Service"
Cohesion: 0.33
Nodes (8): FirebaseFirestoreService, InvoiceHistoryItem, Context, InvoiceData, BilledParty, InvoiceItem, SenderParty, JSONObject

### Community 6 - "Web Dependencies & Export Engines"
Cohesion: 0.11
Nodes (19): firebase, hash-wasm, html2canvas, html-to-image, jspdf, lucide-react, next, react (+11 more)

### Community 7 - "Architecture Releases & Specs"
Cohesion: 0.29
Nodes (7): Argon2id Client-Side Authentication Gate, RoboGyaan Invoice Suite Documentation, Firebase Firestore Cloud Sync Engine, RoboGyaan Invoice Suite v1.0.0, RoboGyaan Invoice Suite v1.1.0, RoboGyaan Invoice Suite v1.2.0, RoboGyaan Invoice Suite v1.3.0

### Community 9 - "Web Client-Side Argon2 Auth Gate"
Cohesion: 0.47
Nodes (4): AuthGate(), AuthGateProps, AUTH_SALT, hashPasswordWithArgon2()

### Community 10 - "Android Native A4 PDF Generator"
Cohesion: 0.50
Nodes (3): Context, InvoiceData, PdfGenerator

### Community 11 - "Web Layout & Poppins Font Engine"
Cohesion: 0.40
Nodes (3): metadata, poppins, virgil

### Community 12 - "Gradle Build Wrapper Tooling"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **77 isolated node(s):** `CASH`, `UPI`, `CHEQUE`, `BANK_DRAFT`, `NEFT` (+72 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 107 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **6 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `InvoiceViewModel` connect `Domain Models & Payment Enums` to `Android Compose MainActivity & UI`?**
  _High betweenness centrality (0.051) - this node is a cross-community bridge._
- **Why does `dependencies` connect `Web Dependencies & Export Engines` to `Web Styling & Linting Pipeline`?**
  _High betweenness centrality (0.018) - this node is a cross-community bridge._
- **What connects `CASH`, `UPI`, `CHEQUE` to the rest of the system?**
  _77 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Next.js Invoice Web Portal & Editor` be split into smaller, more focused modules?**
  _Cohesion score 0.09830866807610994 - nodes in this community are weakly interconnected._
- **Should `Android Compose MainActivity & UI` be split into smaller, more focused modules?**
  _Cohesion score 0.11149825783972125 - nodes in this community are weakly interconnected._
- **Should `TypeScript DOM & Compiler Config` be split into smaller, more focused modules?**
  _Cohesion score 0.06896551724137931 - nodes in this community are weakly interconnected._
- **Should `Domain Models & Payment Enums` be split into smaller, more focused modules?**
  _Cohesion score 0.08615384615384615 - nodes in this community are weakly interconnected._