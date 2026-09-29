# 🤖 RoboGyaan Invoice Suite - Agent Instructions & Architecture Guide

Welcome to the **RoboGyaan Invoice Suite** agent guide. This folder specifies coding standards, design conventions, build commands, and the pull request and release lifecycle.

---

## 📚 Table of Contents
1. [Syntax & Design Style](syntax-style.md)
2. [Git PR, Build & Release Workflow](git-pr-release.md)
3. [Knowledge Graph & Architecture Map](../graphify-out/GRAPH_REPORT.md)

---

## ⚡ Quick Architecture Overview
- **Web Portal:** Next.js 16 (App Router), React 19, TypeScript, Tailwind CSS v4, `html-to-image`, `jsPDF`, `hash-wasm` (Argon2id).
- **Android App:** Kotlin 1.9, Jetpack Compose, Material 3, Android SDK 34 (minSdk 24), Android `PdfDocument`.
- **Backend / Database:** Firebase Cloud Firestore (`invoice_prompts` collection in project `robogyaan-invoice`) with client-side Argon2id authentication.
- **Visual Graph:** Interactive knowledge graph available at `graphify-out/graph.html` and `graphify-out/graph.json`.
