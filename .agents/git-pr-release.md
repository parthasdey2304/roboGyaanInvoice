# 🚀 Standard Git Pull Request, Build & Release Workflow

This workflow describes the mandatory process for shipping updates, making pull requests, squash-merging, compiling Android APKs, and publishing GitHub releases.

---

## 1. Feature Branch & Commit Workflow
1. **Create a fresh feature branch:**
   ```powershell
   git checkout -b feat/<feature-name>
   ```
2. **Stage all changes:**
   ```powershell
   git add .
   ```
3. **Commit with descriptive conventional message:**
   ```powershell
   git commit -m "feat: <detailed description of feature>"
   ```
4. **Push branch to remote origin:**
   ```powershell
   git push -u origin feat/<feature-name>
   ```

---

## 2. Pull Request & Squash-Merge Workflow
1. **Open Pull Request via GitHub CLI:**
   ```powershell
   gh pr create --title "<Title>" --body "<Detailed markdown changelog>" --base main
   ```
2. **Squash-Merge and Delete Remote Branch:**
   ```powershell
   gh pr merge <PR_NUMBER> --squash --delete-branch
   ```
3. **Checkout `main` and Pull Latest:**
   ```powershell
   git checkout main
   git pull origin main
   ```
4. **Delete Local Feature Branch (if still present):**
   ```powershell
   git branch -D feat/<feature-name>
   ```

---

## 3. Android APK Compilation
1. **Bump Version in `android/app/build.gradle.kts`:**
   - Increment `versionCode` (e.g. `4` ➔ `5`).
   - Increment `versionName` (e.g. `"1.3.0"` ➔ `"1.3.1"`).
2. **Compile with JDK 17:**
   ```powershell
   $env:JAVA_HOME="C:\jdk17"
   cd android
   .\gradlew.bat assembleDebug
   cd ..
   ```
3. **Copy APK to Root Directory with Version Tag:**
   ```powershell
   Copy-Item "android\app\build\outputs\apk\debug\app-debug.apk" -Destination "robogyaan-invoice-vX.Y.Z.apk" -Force
   ```

---

## 4. Release Notes & GitHub Release
1. **Draft Release Notes:**
   - Create `release_notes_vX.Y.Z.md` following the strict format of `release_notes.md`.
   - Maintain sections: Key Features / What's New, Download & Installation with direct asset link, Live Deployment link, and Build Verification.
2. **Publish Release on GitHub:**
   ```powershell
   gh release create vX.Y.Z "robogyaan-invoice-vX.Y.Z.apk#RoboGyaan Invoice vX.Y.Z APK" --title "RoboGyaan Invoice Suite vX.Y.Z (<Release Highlights>)" --notes-file "release_notes_vX.Y.Z.md"
   ```

---

## 5. Web Deployment to Vercel
1. **Deploy Production Build:**
   ```powershell
   npx vercel@latest --prod --yes
   ```
2. **Verify Live URL:**
   - [https://invoice.robogyaan.in](https://invoice.robogyaan.in)
