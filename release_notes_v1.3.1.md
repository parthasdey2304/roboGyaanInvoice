# 🤖 RoboGyaan Invoice Suite v1.3.1 - Android & Web Release

We are proud to release **RoboGyaan Invoice v1.3.1**, introducing repository-wide persistent knowledge graphing via **Graphify**, dedicated AI agent design and syntax standards (`.agents/`), and automated release workflow runbooks.

---

## 🚀 What's New in v1.3.1

### 1. 🕸️ Repository Knowledge Graph (Graphify Engine)
- **Comprehensive Code & Architecture Graph:** Mapped 259 nodes and 427 edges across 23 distinct architectural communities covering web components, Jetpack Compose screens, Firebase Firestore cloud sync, and Argon2id security.
- **Interactive Visualizer:** High-performance browser visualization exported to [`graphify-out/graph.html`](graphify-out/graph.html).
- **Architectural Audit:** In-depth community detection, god node analysis, and cross-boundary bridge tracing documented in [`graphify-out/GRAPH_REPORT.md`](graphify-out/GRAPH_REPORT.md).
- **GraphRAG Ready:** Persistent entity and relationship metadata exported to [`graphify-out/graph.json`](graphify-out/graph.json).

### 2. 🤖 Official Agent Architecture & Syntax Standards (`.agents/`)
- **Syntax & Design System (`.agents/syntax-style.md`):** Formalized Neo-Brutalist design tokens (`#FFE600`, `#FDFBF7`, solid black borders, hard drop-shadows), Virgil ultra-bold typography rules, responsive header icon-toggles, and client-side Argon2id encryption specs.
- **Git PR & Release Workflow (`.agents/git-pr-release.md`):** Documented standardized feature branching, GitHub PR generation, squash-merging with branch deletion, Android APK compilation with JDK 17, and Vercel production deployment.
- **Agent Overview (`.agents/README.md`):** High-level architectural orientation connecting source code, styling rules, and the knowledge graph.

### 3. 📱 Android App v1.3.1 (`versionCode = 5`)
- Version bump with updated build configuration and full parity across Android and Web platforms.

---

## 📦 Download & Installation

Download the attached APK below and install on any Android device running **Android 7.0 (Nougat, API 24) or higher**:
- **APK Asset:** [`robogyaan-invoice-v1.3.1.apk`](https://github.com/parthasdey2304/roboGyaanInvoice/releases/download/v1.3.1/robogyaan-invoice-v1.3.1.apk)
- **SHA-256 / File Size:** ~16.5 MB
- **Package Name:** `com.robogyaan.invoice`
- **Target SDK:** Android 14 (API 34)

---

## 🌐 Web Application Live Deployment
- **Live URL:** [https://invoice.robogyaan.in](https://invoice.robogyaan.in)
- **Engine:** Next.js 16 (App Router), TypeScript, Tailwind CSS v4, Argon2id, and Firebase Firestore.

---

## 🛠️ Verification & Build Status
- **Knowledge Graph:** Verified 259 nodes, 427 edges, 23 communities in `graphify-out/`.
- **Next.js Web Engine:** Turbopack production build verified with zero errors.
- **Android Engine:** Compiled via Gradle 8.7 & JDK 17 (`./gradlew assembleDebug`).
