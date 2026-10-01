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

