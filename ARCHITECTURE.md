# 🏛️ RoboGyaan Invoice Suite - System Architecture & Security Specification

This architecture document details the dual-mode biometric and cryptographic authentication architecture implemented in **RoboGyaan Invoice Suite v1.5.8** across Web, Android, macOS, and Windows.

---

## 1. High-Level Authentication Architecture

```mermaid
flowchart TD
    User([User arrives at Login Screen]) --> Choice{Choose Login Mode}

    subgraph BiometricFlow["Option 1: Biometric Authentication"]
        Choice -->|Fingerprint Mode| CheckLock{Check Lockout Status}
        CheckLock -->|Failed >= 5 Attempts| LockedOut[Biometric Locked]
        LockedOut --> AutoSwitch[Auto-switch to Admin Password Tab]
        AutoSwitch --> PasswordFlow

        CheckLock -->|Attempts < 5| SensorType{Detect Platform / Hardware}
        SensorType -->|Android Phone| AndroidBio[BiometricPrompt API\nOptical / Ultrasonic In-Display\nPower Button / Rear Sensor]
        SensorType -->|macOS / MacBook| MacBio[WebAuthn Platform Authenticator\nTouch ID Sensor]
        SensorType -->|Windows Laptop| WinBio[WebAuthn Platform Authenticator\nWindows Hello Fingerprint]
        SensorType -->|iOS / Safari| iOSBio[WebAuthn / LocalAuthentication\nTouch ID / Face ID]

        AndroidBio --> VerifyBio[Evaluate Fingerprint Match]
        MacBio --> VerifyBio
        WinBio --> VerifyBio
        iOSBio --> VerifyBio

        VerifyBio -->|Success| GrantAccess[Reset Fail Counter\nIssue Session / Token\nEnter Invoice Suite]
        VerifyBio -->|Failed Match| IncFail[Increment Fail Counter: Count + 1]
        IncFail --> CheckCount{Count >= 5?}
        CheckCount -->|Yes| LockoutBiometrics[Lockout Biometrics\nShow Security Alert\nForce Switch to Password]
        CheckCount -->|No| PromptRetry[Show Remaining Attempts\nAllow Retry]
    end

    subgraph PasswordFlow["Option 2: Admin Password Authentication"]
        Choice -->|Password Mode| EnterCreds[Enter Admin Email & Password]
        EnterCreds --> ArgonHash[Client-Side Argon2id Hash\n32-Byte Hex Digest]
        ArgonHash --> SubmitAuth[Verify Hash with Backend]
        SubmitAuth -->|Valid| Unlocked[Reset Biometric Lockout\nEstablish 30-Day Session\nEnter Invoice Suite]
        SubmitAuth -->|Invalid| PwError[Display Invalid Credentials Error]
    end
```

---

## 2. Hardware Sensor Compatibility Matrix

The architecture provides universal hardware abstraction, ensuring fingerprint authentication works on every consumer hardware configuration:

| Platform / Form Factor | Sensor Placement / Mechanism | Underlying Technology | API Integration |
| :--- | :--- | :--- | :--- |
| **Android (Modern Flagship)** | In-display screen (Ultrasonic) | Sound wave acoustic 3D mapping | `androidx.biometric.BiometricPrompt` (`BIOMETRIC_STRONG`) |
| **Android (Mid-Range/AMOLED)** | In-display screen (Optical) | High-resolution optical illumination | `androidx.biometric.BiometricPrompt` (`BIOMETRIC_STRONG`) |
| **Android (Side / Ergonomic)** | Power button (Capacitive) | Semiconductor electrostatic capacitance | `androidx.biometric.BiometricPrompt` (`BIOMETRIC_WEAK / STRONG`) |
| **Android (Classic / Legacy)** | Rear chassis next to camera | Capacitive swipe / touch array | `androidx.biometric.BiometricPrompt` + `USE_FINGERPRINT` |
| **macOS (MacBook Air / Pro)** | Keyboard top-right Touch ID key | Secure Enclave capacitive sensor | WebAuthn `navigator.credentials` (`platform`) |
| **Windows Laptops** | Power button / wrist rest sensor | Windows Hello biometric driver | WebAuthn `navigator.credentials` (`platform`) |
| **iOS (iPhone / iPad)** | Home button / Face ID | Secure Enclave biometrics | WebKit WebAuthn platform authenticator |

---

## 3. Finite State Machine: Biometric Lockout Policy

To prevent unauthorized shoulder-surfing, repeated guessing, or spoofing, the app enforces a strict **5-Attempt Lockout Policy**:

```mermaid
stateDiagram-v2
    [*] --> Idle: App Launches
    Idle --> BiometricPrompting: User taps Fingerprint
    BiometricPrompting --> Authenticated: Sensor Match (Success)
    Authenticated --> [*]: Open Invoice Suite

    BiometricPrompting --> AttemptFailed: Sensor Mismatch (Attempt 1-4)
    AttemptFailed --> BiometricPrompting: User Retries

    BiometricPrompting --> LockedOut: Sensor Mismatch (Attempt 5)
    LockedOut --> PasswordForced: Biometric Tab Disabled
    PasswordForced --> VerifyingPassword: User inputs Admin Password
    VerifyingPassword --> Authenticated: Password Correct & Reset Lockout
    VerifyingPassword --> PasswordForced: Password Incorrect
```

---

## 5. Central Cloud Storage & Browser Enrollment Architecture (`/biometrics`)

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Admin User (Browser / Device)
    participant Route as /biometrics Page
    participant WebAuthn as Platform Sensor (Windows Hello / Touch ID / Android)
    participant API as /api/auth/biometric
    participant Cloud as Cloud Firestore

    Admin->>Route: Enter Fingerprint Label & Admin Password
    Route->>API: POST { action: "challenge" }
    API-->>Route: Return Cryptographic Challenge
    Route->>WebAuthn: navigator.credentials.create() (Phase 1: Place Finger)
    WebAuthn-->>Route: First Contact Captured (35%)
    Route->>Admin: UI: "Lift your finger..."
    Route->>Admin: UI: "Place same finger again at different angle..." (Phase 2: 85%)
    Route->>API: POST { action: "register", credentialId, name, platform, passwordHash }
    API->>Cloud: PATCH /invoice_prompts/biometric_cred_{id} (enabled: true)
    Cloud-->>API: 200 OK
    API-->>Route: 200 Success & Stored
    Route->>Admin: Display in Authorized Fingerprints List with Power Toggle Switch

    Note over Admin, Cloud: Real-Time Deactivation Flow
    Admin->>Route: Click "Turn OFF" on Fingerprint
    Route->>API: POST { action: "toggle", credentialId, enabled: false }
    API->>Cloud: Update enabled=false
    Cloud-->>API: 200 OK
    Note over Admin, Cloud: Subsequent Logins with that Finger are Immediately Blocked on Web & Android
```

---

## 6. Apple Face ID TrueDepth Biometric Architecture (v1.6.0)

When accessed on Apple iOS devices (iPhone, iPad) or Apple Safari browsers, the suite dynamically adapts to dedicated **Apple Face ID** biometric authentication:

```mermaid
sequenceDiagram
    autonumber
    actor User as iOS User (iPhone / Safari)
    participant UI as AuthGate / Biometrics Page
    participant TrueDepth as Apple TrueDepth Camera Array
    participant Enclave as Apple Secure Enclave
    participant API as /api/auth/biometric
    participant Cloud as Cloud Firestore

    User->>UI: Select "Face ID" Login / Setup
    UI->>API: Request WebAuthn Challenge
    API-->>UI: Return 32-byte Cryptographic Challenge
    UI->>TrueDepth: Activate Sensor Array (Infrared Camera + 30k Dot Projector)
    TrueDepth-->>UI: Capture 3D Depth Mesh Contours (Head Roll Verification)
    TrueDepth->>Enclave: Evaluate 3D Facial Key against Mathematical Enclave Model
    Enclave-->>UI: Cryptographic Assertion (Hardware Verified)
    UI->>API: POST { action: "verify", credentialId }
    API->>Cloud: Query biometric_cred_{id} (Verify isEnabled == true)
    Cloud-->>API: Status Active
    API-->>UI: Session Authorized (30-day Cookie Issued)
    UI->>User: Access Granted to Invoice Suite
```

### Apple TrueDepth Hardware Subsystem & Security:
1. **Dynamic Island / Sensor Notch Visualizer:** Emulates Apple's infrared camera emitter, speaker bar, and 30,000 IR dot matrix projection.
2. **Circular Head Roll Setup Wizard:** Guides user through two full 3D head rolls with 24 radial green segments that light up as depth contour coverage reaches 100%.
3. **Platform Separation:** Face ID is strictly scoped to Apple devices (iOS and Safari), while Android and Windows devices maintain dedicated fingerprint sensor flows.
4. **5-Attempt Lockout Parity:** Enforces strict lockout upon 5 consecutive failed face recognition attempts with automatic fallback to Admin Password.

---

## 7. High-Contrast Dark Mode Visual Polish & Accessibility (v1.6.1)

In v1.6.1, the visual accessibility across dark mode was strengthened:

```mermaid
flowchart LR
    A["Dark Mode Active"] --> B["SettingsDialog Theme Toggle"]
    B --> C["Light Option: Icon & Text Color.White (Contrast 14:1)"]
    B --> D["Container: #18181B background + #3F3F46 1.5dp border"]
    B --> E["Dark Option: #27272A active pill + NeoYellow Icon"]
```

- **Theme Toggle Contrast Guarantee:** In `SettingsDialog.kt`, the unselected "Light" option now renders both the `WbSunny` icon and the Virgil text in `Color.White` when in dark mode (instead of `Color.Black`), ensuring high readability against dark surfaces.
- **Container Differentiation:** Toggle background uses `#18181B` with subtle `#3F3F46` border in dark mode to clearly distinguish interactive buttons from the dialog background.
- **Version Parity:** Web API (`/api/version`), Web Settings Modal, Android Native `AppUpdateManager`, and Gradle configuration synchronized to `v1.6.1` (versionCode 20).

---

## 8. PDF & Invoice Preview Table Dual-Box Footer Architecture (v1.6.2)

In v1.6.2, both Web and Android native PDF/Preview engines implement structured 2-box summary alignment across the invoice table:

```mermaid
flowchart TD
    subgraph TableColumns["Invoice Table Columns"]
        Col1["Col 1: Item (5 spans / 44%)"]
        Col2["Col 2: Amount / Student head (3 spans / 20%)"]
        Col3["Col 3: No. of Students (2 spans / 16%)"]
        Col4["Col 4: Total Amount (2 spans / 20%)"]
    end

    subgraph TableFooter["Dual-Box Table Footer"]
        EmptyLeft["Col 1-2: Open Span (8 cols / 64%)"]
        Box1["Box 1 (Col 3): Total Amount : (Right-Aligned)"]
        Box2["Box 2 (Col 4): ₹27,200.00 (Right-Aligned)"]
    end

    Col1 -.-> EmptyLeft
    Col2 -.-> EmptyLeft
    Col3 ==>|Direct Vertical Line c2X & c3X| Box1
    Col4 ==>|Direct Vertical Line c3X & Right Border| Box2
```

### Architecture Specifications:
1. **Column Alignment Guarantee:**
   - **Box 1:** Positioned strictly within the horizontal bounds of the **No. of Students** column (`col-span-2` in CSS Grid / `c2X` to `c3X` in Android PDF). Displays `Total Amount :` right-aligned at the end of the cell.
   - **Box 2:** Positioned strictly within the horizontal bounds of the **Total Amount** column (`col-span-2` in CSS Grid / `c3X` to `rightX` in Android PDF). Displays the formatted currency value (e.g. `₹27,200.00`) right-aligned to match the numbers in the table rows above.
2. **Vertical Divider Continuity:**
   - The vertical line separating Column 3 and Column 4 (`c3X`) continues uninterrupted through the footer to the bottom border, physically separating Box 1 from Box 2.
   - The vertical line separating Column 2 and Column 3 (`c2X`) extends to the bottom border, forming the left wall of Box 1.
   - Column 1 divider (`c1X`) terminates cleanly at `totalRowTop`, keeping the left span open and uncluttered.
3. **Cross-Platform Uniformity:**
   - **Web HTML/Canvas/PDF (`InvoicePreview.tsx`):** 12-column CSS Grid with `col-span-8` blank, `col-span-2` with `border-l-[2px] border-r-[2px]`, and `col-span-2` for currency.
   - **Android Native Canvas PDF (`PdfGenerator.kt`):** Precise coordinate lines `c2X` and `c3X` drawn to `tableBottom` with segmented text drawing.
   - **Android Compose Preview (`InvoicePreviewScreen.kt`):** Intrinsic row height with proportional weight boxes (`1.1f` and `1.4f`) separated by 1.5dp black dividers.

---

## 9. Official Brand Assets, Single-Line Footer & Dark Mode Alert Architecture (v1.6.3)

```mermaid
flowchart TD
    subgraph BrandIdentity["Official RoboGyaan Brand System"]
        Logo["Logo (robogyaan_logo.png)\nEmblem + 'ROBOGYAAN' + 'IGNITING CURIOSITY, BUILDING FUTURE'"]
        Symbol["Symbol (robogyaan_symbol.png)\nAuthentic connected dumbbell-nodes"]
        LauncherIcon["Android Launcher Icon (@mipmap/ic_launcher)\nHigh-DPI 512x512 with 18% safe padding"]
        Logo --> HeaderWeb["Web & PDF Headers"]
        Symbol --> Watermark["Invoice Table Watermark (-20° rotation, 10% opacity)"]
        Symbol --> LauncherIcon
    end

    subgraph TableFooter["Expanded 1-Line Table Footer"]
        LeftSpan["Left Span: 7 columns (58.3%)"]
        Box1Expanded["Box 1: 3 columns (25%)\nHorizontally expanded with padding\n'Total Amount' strictly on 1 line (NO colon)"]
        Box2Val["Box 2: 2 columns (16.7%)\n'₹XX,XXX.00' Right-aligned currency"]
        LeftSpan --- Box1Expanded --- Box2Val
    end

    subgraph DialogDarkTheme["NeoBrutalAlertDialog Dark Mode Engine"]
        ThemeDetect{"isDarkMode?"}
        ThemeDetect -->|Light| LightDialog["#FDFBF7 Card + Black Border\nWhite Translucent Backdrop (78%)\nBlack Message Text\nWhite Close & Cancel Buttons"]
        ThemeDetect -->|Dark| DarkDialog["#18181B Dark Card + #3F3F46 Border\nDark Translucent Backdrop (75%)\nWhite Message Text\nPreserved #FF4D4D Header Bar\n#27272A Dark Action Buttons"]
    end
```

### 1. Authentic RoboGyaan Brand & Icon Overhaul:
- **Elimination of Legacy Pentagon SVG:** All obsolete 5-dot pentagon geometric SVGs previously used in headers and watermarks were eliminated.
- **Clean Alpha Transparency:** Python-processed alpha masks convert white backgrounds to smooth alpha transparency without fringing or halo artifacts, ensuring perfect rendering on both light paper previews and dark app backgrounds.
- **Adaptive Launcher Icon Density:** Android manifest points to `@mipmap/ic_launcher` and `@mipmap/ic_launcher_round`, generated from high-resolution 512x512 vector-equivalent art across `mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, and `xxxhdpi` folders.

### 2. Single-Line Table Footer Typography:
- **Width Expansion:** Box 1 was expanded horizontally (from 2 columns to 3 columns in Web CSS Grid, and with `box1Left = c2X - 26f` in Android native PDF canvas) to prevent any two-line word wrap.
- **Punctuation Cleanliness:** Colons (`:`) were eliminated from the total amount label (`"Total Amount"`), with balanced padding providing a clean look.

### 3. NeoBrutalAlertDialog Dark Mode Polish:
- **Full Palette Adaptation:** `NeoBrutalAlertDialog` accepts `isDarkMode: Boolean`, toggling the card surface from `#FDFBF7` to `#18181B`, the fullscreen backdrop to 75% dark translucent black, and the message text from `Color.Black` to `Color.White`.
- **Preserved Alert Intent:** The bright red header bar (`#FF4D4D`) and danger confirm button (`#FF4D4D`) are preserved for instant visual recognition of session termination and destructive actions.



