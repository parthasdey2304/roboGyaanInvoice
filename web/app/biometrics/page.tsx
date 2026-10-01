'use client';

import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import {
  Fingerprint,
  Shield,
  CheckCircle2,
  AlertTriangle,
  ArrowLeft,
  Trash2,
  Power,
  Sparkles,
  Lock,
  Sun,
  Moon,
  Laptop,
  Smartphone,
  Loader2,
  RefreshCw,
  Plus,
  ScanFace
} from 'lucide-react';
import { hashPasswordWithArgon2 } from '../../lib/crypto';
import {
  isPlatformBiometricAvailable,
  registerPlatformCredential,
  saveCredentialId,
  isFaceIdPlatform,
} from '../../lib/webauthn';
import { InteractiveGridBackground } from '../../components/InteractiveGridBackground';

interface CloudBiometricCred {
  id: string;
  docId: string;
  name: string;
  platform: string;
  deviceModel: string;
  biometricType?: string;
  enabled: boolean;
  createdAt: string;
  lastUsedAt: string | null;
}

export default function BiometricsManagementPage() {
  const [theme, setTheme] = useState<'light' | 'dark'>('light');
  const [credentials, setCredentials] = useState<CloudBiometricCred[]>([]);
  const [loadingList, setLoadingList] = useState<boolean>(true);
  const [platformSupported, setPlatformSupported] = useState<boolean>(false);
  const [detectedOS, setDetectedOS] = useState<string>('Detected Hardware');
  const [isFaceIdUser, setIsFaceIdUser] = useState<boolean>(false);

  // Enrollment Wizard state
  const [isWizardOpen, setIsWizardOpen] = useState<boolean>(false);
  const [fingerprintName, setFingerprintName] = useState<string>('');
  const [adminPassword, setAdminPassword] = useState<string>('');
  const [enrollmentStep, setEnrollmentStep] = useState<
    | 'idle'
    | 'place_first'
    | 'scanning_first'
    | 'lift_finger'
    | 'place_second'
    | 'scanning_second'
    | 'position_face'
    | 'scanning_face'
    | 'head_roll_1'
    | 'head_roll_2'
    | 'success'
    | 'error'
  >('idle');
  const [progressPercent, setProgressPercent] = useState<number>(0);
  const [wizardError, setWizardError] = useState<string | null>(null);
  const [actionLoading, setActionLoading] = useState<boolean>(false);
  const [statusNotification, setStatusNotification] = useState<string | null>(null);

  // Load theme and detect OS/platform on mount
  useEffect(() => {
    const savedTheme = localStorage.getItem('robogyaan_theme') as 'light' | 'dark' | null;
    if (savedTheme) {
      setTheme(savedTheme);
      if (savedTheme === 'dark') document.documentElement.classList.add('dark');
      else document.documentElement.classList.remove('dark');
    }

    // Detect if client is an Apple Face ID device (iPhone/iPad/Safari) or Fingerprint device (Android/Windows)
    const faceIdActive = isFaceIdPlatform();
    setIsFaceIdUser(faceIdActive);

    const ua = navigator.userAgent.toLowerCase();
    if (faceIdActive) {
      if (ua.includes('iphone')) {
        setDetectedOS('Apple iPhone (iOS TrueDepth Face ID & Secure Enclave)');
        setFingerprintName('iPhone Face ID');
      } else if (ua.includes('ipad')) {
        setDetectedOS('Apple iPad (iPadOS TrueDepth Face ID & Secure Enclave)');
        setFingerprintName('iPad Face ID');
      } else {
        setDetectedOS('Apple Safari (TrueDepth Face ID & Secure Enclave)');
        setFingerprintName('Apple Face ID');
      }
    } else if (ua.includes('android')) {
      setDetectedOS('Android Smartphone (Screen / Power / Rear Sensor)');
      setFingerprintName('Android Fingerprint');
    } else if (ua.includes('win')) {
      setDetectedOS('Microsoft Windows (Windows Hello Fingerprint)');
      setFingerprintName('Windows Hello Fingerprint');
    } else if (ua.includes('mac')) {
      setDetectedOS('Apple macOS (Touch ID Fingerprint)');
      setFingerprintName('MacBook Touch ID');
    } else {
      setDetectedOS('Universal Platform Authenticator');
      setFingerprintName('Primary Fingerprint');
    }

    isPlatformBiometricAvailable().then((avail) => setPlatformSupported(avail));
    fetchCredentials();
  }, []);

  const toggleTheme = () => {
    const next = theme === 'dark' ? 'light' : 'dark';
    setTheme(next);
    localStorage.setItem('robogyaan_theme', next);
    if (next === 'dark') document.documentElement.classList.add('dark');
    else document.documentElement.classList.remove('dark');
  };

  const fetchCredentials = async () => {
    try {
      setLoadingList(true);
      const res = await fetch('/api/auth/biometric', { cache: 'no-store' });
      const data = await res.json();
      if (data.success && Array.isArray(data.credentials)) {
        setCredentials(data.credentials);
      }
    } catch (err) {
      console.error('Failed to load credentials:', err);
    } finally {
      setLoadingList(false);
    }
  };

  // Toggle credential ON / OFF
  const handleToggle = async (credentialId: string, currentStatus: boolean, credType?: string) => {
    try {
      setActionLoading(true);
      const newStatus = !currentStatus;
      const res = await fetch('/api/auth/biometric', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          action: 'toggle',
          credentialId: credentialId,
          enabled: newStatus,
        }),
      });

      const data = await res.json();
      if (res.ok && data.success) {
        setCredentials((prev) =>
          prev.map((c) => (c.id === credentialId ? { ...c, enabled: newStatus } : c))
        );
        const typeLabel = credType === 'faceid' ? 'Face ID' : 'Fingerprint';
        setStatusNotification(
          newStatus
            ? `${typeLabel} enabled: access is now authorized.`
            : `${typeLabel} turned OFF: access with this credential is now blocked.`
        );
        setTimeout(() => setStatusNotification(null), 4000);
      }
    } catch (err) {
      console.error('Toggle error:', err);
    } finally {
      setActionLoading(false);
    }
  };

  // Delete credential
  const handleDelete = async (credentialId: string, name: string) => {
    if (!confirm(`Are you sure you want to delete the biometric credential for "${name}"?`)) return;

    try {
      setActionLoading(true);
      const res = await fetch('/api/auth/biometric', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          action: 'delete',
          credentialId: credentialId,
        }),
      });

      const data = await res.json();
      if (res.ok && data.success) {
        setCredentials((prev) => prev.filter((c) => c.id !== credentialId));
        setStatusNotification(`Deleted "${name}" permanently.`);
        setTimeout(() => setStatusNotification(null), 4000);
      }
    } catch (err) {
      console.error('Delete error:', err);
    } finally {
      setActionLoading(false);
    }
  };

  // Start Enrollment Ceremony (Adapts between Apple Face ID TrueDepth and Android/Windows Fingerprint)
  const startEnrollment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!adminPassword) {
      setWizardError('Admin password is required to authorize hardware registration.');
      return;
    }

    setWizardError(null);

    try {
      // Step 1: Compute Argon2id hash for admin verification
      const passwordHash = await hashPasswordWithArgon2(adminPassword);

      // Step 2: Request WebAuthn Challenge from Vercel
      const chalRes = await fetch('/api/auth/biometric', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ action: 'challenge' }),
      });
      const chalData = await chalRes.json();
      if (!chalRes.ok || !chalData.challenge) {
        throw new Error('Could not acquire biometric registration challenge.');
      }

      let cred;

      if (isFaceIdUser) {
        // --- APPLE FACE ID ENROLLMENT CEREMONY ---
        setEnrollmentStep('position_face');
        setProgressPercent(20);
        await new Promise((resolve) => setTimeout(resolve, 800));

        setEnrollmentStep('scanning_face');
        setProgressPercent(45);

        // Hardware Platform WebAuthn Prompt (iOS triggers native Face ID dialog)
        cred = await registerPlatformCredential(chalData.challenge, 'invoiceadmin@robogyaan.in');

        // Apple TrueDepth Circular Head Roll Simulation
        setEnrollmentStep('head_roll_1');
        setProgressPercent(70);
        await new Promise((resolve) => setTimeout(resolve, 1200));

        setEnrollmentStep('head_roll_2');
        setProgressPercent(95);
        await new Promise((resolve) => setTimeout(resolve, 800));

      } else {
        // --- UNIVERSAL FINGERPRINT CEREMONY ---
        setEnrollmentStep('place_first');
        setProgressPercent(20);

        setEnrollmentStep('scanning_first');
        setProgressPercent(40);

        cred = await registerPlatformCredential(chalData.challenge, 'invoiceadmin@robogyaan.in');

        setEnrollmentStep('lift_finger');
        setProgressPercent(65);
        await new Promise((resolve) => setTimeout(resolve, 1400));

        setEnrollmentStep('place_second');
        setProgressPercent(85);
        await new Promise((resolve) => setTimeout(resolve, 900));

        setEnrollmentStep('scanning_second');
        setProgressPercent(95);
      }

      setProgressPercent(100);

      // Save credential to Cloud Firestore & Vercel
      const regRes = await fetch('/api/auth/biometric', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          action: 'register',
          passwordHash: passwordHash,
          credentialId: cred.rawId,
          name: fingerprintName.trim() || (isFaceIdUser ? 'Apple Face ID' : 'Admin Fingerprint'),
          platform: detectedOS,
          deviceModel: navigator.userAgent.slice(0, 60),
          biometricType: isFaceIdUser ? 'faceid' : 'fingerprint',
        }),
      });

      const regData = await regRes.json();
      if (!regRes.ok || !regData.success) {
        throw new Error(regData.error || 'Failed to save biometric credential to cloud.');
      }

      saveCredentialId(cred.rawId);
      setEnrollmentStep('success');
      await fetchCredentials();

      setTimeout(() => {
        setIsWizardOpen(false);
        setEnrollmentStep('idle');
        setProgressPercent(0);
        setAdminPassword('');
      }, 2200);
    } catch (err: any) {
      console.error('Enrollment error:', err);
      setEnrollmentStep('error');
      setWizardError(
        err?.message ||
          `${isFaceIdUser ? 'Apple Face ID' : 'Biometric sensor'} registration failed or was cancelled.`
      );
    }
  };

  const isDark = theme === 'dark';

  return (
    <div
      className={`min-h-screen flex flex-col justify-start items-center p-4 sm:p-8 relative select-none ${
        isDark ? 'text-white' : 'text-black'
      }`}
    >
      <InteractiveGridBackground theme={theme} enabled={true} />

      {/* Top Navbar */}
      <header className="max-w-3xl w-full flex items-center justify-between mb-6 relative z-10">
        <Link
          href="/"
          className="inline-flex items-center gap-2 bg-[#FFE600] text-black border-2 border-black px-3.5 py-1.5 rounded-lg font-black text-xs uppercase tracking-wider shadow-[3px_3px_0px_#000] hover:scale-105 active:translate-x-[1px] active:translate-y-[1px] transition"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Return to Suite</span>
        </Link>

        <div className="flex items-center gap-3">
          <div className="hidden sm:inline-flex items-center gap-1.5 px-3 py-1 bg-black text-[#FFE600] border-2 border-black rounded-full font-virgil text-xs font-black uppercase">
            <Shield className="w-3.5 h-3.5" />
            <span>Cloud Firestore Vault</span>
          </div>

          <button
            type="button"
            onClick={toggleTheme}
            className="p-2 bg-black text-[#FFE600] border-2 border-black rounded-lg shadow-[2px_2px_0px_#000] hover:scale-105 transition cursor-pointer"
            title={isDark ? 'Light Mode' : 'Dark Mode'}
          >
            {isDark ? <Sun className="w-4 h-4" /> : <Moon className="w-4 h-4" />}
          </button>
        </div>
      </header>

      {/* Main Container */}
      <main
        className={`max-w-3xl w-full border-[3.5px] border-black rounded-xl p-6 sm:p-8 shadow-[8px_8px_0px_#000000] relative z-10 ${
          isDark ? 'bg-[#18181b] border-white/80' : 'bg-white'
        }`}
      >
        {/* Header - Adapts dynamically for Apple Face ID vs Fingerprint */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b-2 border-black dark:border-neutral-700">
          <div className="flex items-center gap-3">
            <div className="w-14 h-14 bg-black text-[#FFE600] rounded-xl flex items-center justify-center border-2 border-black shadow-[3px_3px_0px_#FFE600] shrink-0">
              {isFaceIdUser ? (
                <ScanFace className="w-8 h-8 text-[#FFE600]" />
              ) : (
                <Fingerprint className="w-8 h-8 text-[#FFE600]" />
              )}
            </div>
            <div>
              <h1 className="text-xl sm:text-2xl font-black uppercase tracking-tight font-virgil">
                {isFaceIdUser ? 'Apple Face ID & Biometric Vault' : 'Biometric Hardware Vault'}
              </h1>
              <p className="text-xs font-bold text-neutral-500 dark:text-neutral-400 uppercase tracking-wider mt-0.5">
                {isFaceIdUser
                  ? 'Centralized Apple Face ID & TrueDepth Authorization for iOS & Safari'
                  : 'Centralized Fingerprint Authorization for Web & Android'}
              </p>
            </div>
          </div>

          <button
            type="button"
            onClick={() => {
              setIsWizardOpen(true);
              setEnrollmentStep('idle');
              setWizardError(null);
            }}
            className="self-start sm:self-auto py-2.5 px-4 bg-[#FFE600] text-black border-2 border-black rounded-lg font-black text-xs uppercase tracking-wider shadow-[3px_3px_0px_#000] hover:bg-[#FFD700] active:translate-x-[1px] active:translate-y-[1px] flex items-center gap-2 cursor-pointer transition"
          >
            <Plus className="w-4 h-4 text-black stroke-[3]" />
            {isFaceIdUser ? (
              <>
                <ScanFace className="w-4 h-4 text-black" />
                <span>Enroll Face ID</span>
              </>
            ) : (
              <>
                <Fingerprint className="w-4 h-4 text-black" />
                <span>Enroll Fingerprint</span>
              </>
            )}
          </button>
        </div>

        {/* Status Notification */}
        {statusNotification && (
          <div className="mt-4 p-3 bg-[#FFFDE6] dark:bg-neutral-800 border-2 border-black rounded-lg text-xs font-black shadow-[2px_2px_0px_#000] flex items-center gap-2 animate-fade-in text-black dark:text-[#FFE600]">
            <CheckCircle2 className="w-4 h-4 text-black dark:text-[#FFE600]" />
            <span>{statusNotification}</span>
          </div>
        )}

        {/* Hardware Status Banner */}
        <div
          className={`mt-6 border-2 border-black rounded-lg p-3.5 shadow-[2px_2px_0px_#000] ${
            isDark ? 'bg-[#27272a]' : 'bg-[#FFFDE6]'
          }`}
        >
          <div className="flex items-start gap-2.5">
            <Sparkles className="w-4 h-4 text-[#FFE600] shrink-0 mt-0.5" />
            <div>
              <p className="text-xs font-black uppercase tracking-wider">
                Current Device: {detectedOS}
              </p>
              <p className="text-[11px] font-medium text-neutral-600 dark:text-neutral-300 mt-0.5 leading-snug">
                {isFaceIdUser
                  ? 'Apple Face ID credentials registered via TrueDepth camera and Secure Enclave are synchronized in Cloud Firestore. Only active Face ID profiles can unlock the suite. You can toggle any profile ON/OFF at any time.'
                  : 'Fingerprint credentials registered here are synced into Cloud Firestore. Only active fingers can unlock the suite on Web or Android. You can toggle any finger ON/OFF at any time.'}
              </p>
            </div>
          </div>
        </div>

        {/* Credentials List Section */}
        <section className="mt-6">
          <div className="flex items-center justify-between mb-3">
            <h2 className="text-xs font-black uppercase tracking-wider font-virgil flex items-center gap-2">
              <span>Authorized Biometrics</span>
              <span className="px-2 py-0.5 bg-black text-[#FFE600] rounded-full text-[10px]">
                {credentials.length} Registered
              </span>
            </h2>

            <button
              type="button"
              onClick={fetchCredentials}
              disabled={loadingList}
              className="text-[11px] font-bold underline flex items-center gap-1 hover:text-[#FFE600] cursor-pointer"
            >
              <RefreshCw className={`w-3 h-3 ${loadingList ? 'animate-spin' : ''}`} />
              <span>Refresh Cloud</span>
            </button>
          </div>

          {loadingList ? (
            <div className="py-12 flex flex-col items-center justify-center gap-2">
              <Loader2 className="w-6 h-6 animate-spin text-[#FFE600]" />
              <p className="text-xs font-black uppercase font-virgil">Fetching Cloud Credentials...</p>
            </div>
          ) : credentials.length === 0 ? (
            <div className="py-10 border-2 border-dashed border-neutral-300 dark:border-neutral-700 rounded-lg text-center p-6">
              {isFaceIdUser ? (
                <ScanFace className="w-12 h-12 mx-auto text-neutral-400 mb-2" />
              ) : (
                <Fingerprint className="w-12 h-12 mx-auto text-neutral-400 mb-2" />
              )}
              <p className="text-sm font-black uppercase font-virgil">
                No Biometrics Registered in Cloud
              </p>
              <p className="text-xs text-neutral-500 mt-1">
                Click &quot;{isFaceIdUser ? 'Enroll Face ID' : 'Enroll Fingerprint'}&quot; above to capture and store your first biometric profile.
              </p>
            </div>
          ) : (
            <div className="space-y-3">
              {credentials.map((cred) => {
                const isCredFaceId =
                  cred.biometricType === 'faceid' ||
                  cred.name?.toLowerCase().includes('face') ||
                  cred.platform?.toLowerCase().includes('ios') ||
                  cred.platform?.toLowerCase().includes('iphone');

                return (
                  <div
                    key={cred.id}
                    className={`border-2 border-black rounded-xl p-4 shadow-[3px_3px_0px_#000] flex flex-col sm:flex-row sm:items-center justify-between gap-4 transition ${
                      cred.enabled
                        ? isDark
                          ? 'bg-[#27272a]'
                          : 'bg-white'
                        : 'opacity-60 bg-neutral-100 dark:bg-neutral-900 border-neutral-400'
                    }`}
                  >
                    <div className="flex items-start gap-3">
                      <div
                        className={`w-10 h-10 rounded-lg border-2 border-black flex items-center justify-center shrink-0 ${
                          cred.enabled ? 'bg-[#FFE600] text-black' : 'bg-neutral-300 text-neutral-600'
                        }`}
                      >
                        {isCredFaceId ? (
                          <ScanFace className="w-6 h-6" />
                        ) : (
                          <Fingerprint className="w-6 h-6" />
                        )}
                      </div>

                      <div>
                        <div className="flex items-center gap-2 flex-wrap">
                          <h3 className="text-sm font-black uppercase tracking-tight">{cred.name}</h3>
                          <span
                            className={`text-[9px] font-black uppercase px-2 py-0.5 rounded-full border border-black ${
                              isCredFaceId
                                ? 'bg-cyan-200 text-cyan-950 dark:bg-cyan-900 dark:text-cyan-200'
                                : 'bg-neutral-200 text-neutral-950 dark:bg-neutral-800 dark:text-neutral-200'
                            }`}
                          >
                            {isCredFaceId ? 'APPLE FACE ID' : 'FINGERPRINT'}
                          </span>
                          <span
                            className={`text-[9px] font-black uppercase px-2 py-0.5 rounded-full border border-black ${
                              cred.enabled
                                ? 'bg-green-300 text-green-950 dark:bg-green-900 dark:text-green-200'
                                : 'bg-red-200 text-red-900 dark:bg-red-950 dark:text-red-200'
                            }`}
                          >
                            {cred.enabled ? 'ACTIVE (ON)' : 'DISABLED (OFF)'}
                          </span>
                        </div>

                        <div className="flex flex-wrap items-center gap-x-3 gap-y-1 mt-1 text-[10px] text-neutral-500 dark:text-neutral-400 font-bold">
                          <span>Platform: {cred.platform}</span>
                          <span>&bull;</span>
                          <span>
                            Security:{' '}
                            {isCredFaceId ? 'TrueDepth / Secure Enclave' : 'Biometric Hardware'}
                          </span>
                          <span>&bull;</span>
                          <span>Enrolled: {new Date(cred.createdAt).toLocaleDateString()}</span>
                          {cred.lastUsedAt && (
                            <>
                              <span>&bull;</span>
                              <span>Last Used: {new Date(cred.lastUsedAt).toLocaleDateString()}</span>
                            </>
                          )}
                        </div>
                      </div>
                    </div>

                    {/* Actions: Toggle Switch & Delete Button */}
                    <div className="flex items-center gap-3 self-end sm:self-center">
                      <button
                        type="button"
                        disabled={actionLoading}
                        onClick={() => handleToggle(cred.id, cred.enabled, isCredFaceId ? 'faceid' : 'fingerprint')}
                        className={`py-1.5 px-3 rounded-lg border-2 border-black font-black text-xs uppercase tracking-wider flex items-center gap-1.5 shadow-[2px_2px_0px_#000] cursor-pointer transition active:translate-x-[1px] active:translate-y-[1px] ${
                          cred.enabled
                            ? 'bg-neutral-100 dark:bg-neutral-800 text-red-600 hover:bg-red-50'
                            : 'bg-[#FFE600] text-black hover:bg-[#FFD700]'
                        }`}
                        title={
                          cred.enabled
                            ? `Turn OFF ${isCredFaceId ? 'Face ID' : 'Fingerprint'}`
                            : `Turn ON ${isCredFaceId ? 'Face ID' : 'Fingerprint'}`
                        }
                      >
                        <Power className="w-3.5 h-3.5" />
                        <span>{cred.enabled ? 'Turn OFF' : 'Turn ON'}</span>
                      </button>

                      <button
                        type="button"
                        disabled={actionLoading}
                        onClick={() => handleDelete(cred.id, cred.name)}
                        className="p-1.5 text-neutral-400 hover:text-red-600 rounded border border-neutral-300 dark:border-neutral-700 hover:border-red-500 transition cursor-pointer"
                        title="Delete Credential"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </section>
      </main>

      {/* ------------------------------------------------------------- */}
      {/* ADAPTIVE BIOMETRIC ENROLLMENT MODAL (FACE ID OR FINGERPRINT)   */}
      {/* ------------------------------------------------------------- */}
      {isWizardOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fade-in">
          <div
            className={`max-w-md w-full border-[3.5px] border-black rounded-xl p-6 shadow-[8px_8px_0px_#000000] relative ${
              isDark ? 'bg-[#18181b] text-white border-white/80' : 'bg-white text-black'
            }`}
          >
            {/* Header */}
            <div className="flex items-center justify-between pb-3 border-b-2 border-black dark:border-neutral-700 mb-4">
              <div className="flex items-center gap-2">
                {isFaceIdUser ? (
                  <ScanFace className="w-5 h-5 text-[#FFE600]" />
                ) : (
                  <Smartphone className="w-5 h-5 text-[#FFE600]" />
                )}
                <h3 className="text-base font-black uppercase font-virgil">
                  {isFaceIdUser ? 'Apple Face ID Setup (TrueDepth)' : 'Phone-Style Fingerprint Setup'}
                </h3>
              </div>
              <button
                type="button"
                onClick={() => setIsWizardOpen(false)}
                className="w-7 h-7 bg-neutral-200 dark:bg-neutral-800 border-2 border-black rounded-md flex items-center justify-center font-black text-xs hover:bg-red-400 transition"
              >
                &times;
              </button>
            </div>

            {/* Error banner */}
            {wizardError && (
              <div className="mb-4 bg-red-100 dark:bg-red-950/70 border-2 border-red-500 rounded-lg p-3 text-red-900 dark:text-red-200 text-xs font-bold shadow-[2px_2px_0px_#dc2626]">
                {wizardError}
              </div>
            )}

            {/* Step: Initial Details & Admin Authorization Form */}
            {enrollmentStep === 'idle' && (
              <form onSubmit={startEnrollment} className="space-y-4">
                <div>
                  <label className="block text-xs font-black uppercase tracking-wider mb-1">
                    {isFaceIdUser ? 'Face ID Profile Name' : 'Fingerprint Label'}
                  </label>
                  <input
                    type="text"
                    value={fingerprintName}
                    onChange={(e) => setFingerprintName(e.target.value)}
                    placeholder={isFaceIdUser ? 'e.g. Parth - iPhone Face ID' : 'e.g. Parth - Right Thumb'}
                    required
                    className={`neo-input w-full text-xs font-semibold py-2.5 px-3 border-2 border-black rounded-lg shadow-[2.5px_2.5px_0px_#000] outline-none ${
                      isDark ? 'bg-[#27272a] text-white border-neutral-600' : 'bg-white text-black'
                    }`}
                  />
                </div>

                <div>
                  <label className="block text-xs font-black uppercase tracking-wider mb-1 flex items-center gap-1.5">
                    <Lock className="w-3.5 h-3.5" />
                    Admin Password (Authorization)
                  </label>
                  <input
                    type="password"
                    value={adminPassword}
                    onChange={(e) => setAdminPassword(e.target.value)}
                    placeholder="Enter admin password to authorize"
                    required
                    className={`neo-input w-full text-xs font-semibold py-2.5 px-3 border-2 border-black rounded-lg shadow-[2.5px_2.5px_0px_#000] outline-none ${
                      isDark ? 'bg-[#27272a] text-white border-neutral-600' : 'bg-white text-black'
                    }`}
                  />
                </div>

                <div className="p-3 bg-neutral-100 dark:bg-neutral-800 rounded-lg border border-neutral-300 dark:border-neutral-700 text-[11px] leading-tight text-neutral-600 dark:text-neutral-300">
                  {isFaceIdUser ? (
                    <>
                      The TrueDepth sensor array will project 30,000 infrared dots to construct a 3D depth map of your facial geometry, verified and protected inside the Apple Secure Enclave.
                    </>
                  ) : (
                    <>
                      Like on smartphones, you will be prompted to place and lift your finger on the sensor to capture complete ridge coverage.
                    </>
                  )}
                </div>

                <button
                  type="submit"
                  className="w-full py-3 bg-[#FFE600] text-black font-black text-xs uppercase tracking-wider border-2 border-black rounded-lg shadow-[4px_4px_0px_#000] hover:bg-[#FFD700] active:translate-x-[2px] active:translate-y-[2px] flex items-center justify-center gap-2 cursor-pointer"
                >
                  {isFaceIdUser ? (
                    <>
                      <ScanFace className="w-4 h-4 text-black" />
                      <span>Begin Apple Face ID Scan</span>
                    </>
                  ) : (
                    <>
                      <Fingerprint className="w-4 h-4 text-black" />
                      <span>Begin Fingerprint Scan</span>
                    </>
                  )}
                </button>
              </form>
            )}

            {/* Step: Multi-Sample Interactive Scanner Ceremony */}
            {enrollmentStep !== 'idle' && (
              <div className="py-6 flex flex-col items-center justify-center text-center">
                {isFaceIdUser ? (
                  /* -------------------------------------------------- */
                  /* APPLE FACE ID TRUEDEPTH INTERACTIVE SCANNER        */
                  /* -------------------------------------------------- */
                  <div>
                    {/* iPhone TrueDepth Dynamic Island Notch cutout */}
                    <div className="mx-auto mb-4 w-44 h-7 bg-black rounded-full border border-neutral-700 flex items-center justify-between px-3 shadow-md">
                      {/* Infrared Camera */}
                      <div className="flex items-center gap-1.5" title="TrueDepth Infrared Camera">
                        <div className="w-2.5 h-2.5 rounded-full bg-neutral-900 border border-neutral-700 flex items-center justify-center">
                          <div className="w-1.5 h-1.5 rounded-full bg-red-600 animate-ping opacity-80" />
                        </div>
                        <span className="text-[8px] font-mono text-neutral-400">IR</span>
                      </div>
                      {/* Speaker Slit */}
                      <div className="w-12 h-1 bg-neutral-800 rounded-full" />
                      {/* 30k IR Dot Projector */}
                      <div className="flex items-center gap-1" title="30,000 IR Dot Projector">
                        <span className="text-[8px] font-mono text-cyan-400">30K</span>
                        <div className="w-2.5 h-2.5 rounded-full bg-cyan-950 border border-cyan-500/50 flex items-center justify-center">
                          <div className="w-1 h-1 rounded-full bg-cyan-400" />
                        </div>
                      </div>
                    </div>

                    {/* Circular Apple Face ID Head Roll Ring with 24 Radial Ticks */}
                    <div className="relative w-40 h-40 mx-auto flex items-center justify-center mb-4">
                      {/* Circular Progress SVG */}
                      <svg className="absolute inset-0 w-full h-full -rotate-90" viewBox="0 0 100 100">
                        <circle
                          cx="50"
                          cy="50"
                          r="44"
                          className="text-neutral-200 dark:text-neutral-800"
                          strokeWidth="6"
                          stroke="currentColor"
                          fill="transparent"
                        />
                        <circle
                          cx="50"
                          cy="50"
                          r="44"
                          className="text-[#FFE600] transition-all duration-500 ease-out"
                          strokeWidth="6"
                          strokeDasharray={276.4}
                          strokeDashoffset={276.4 - (276.4 * progressPercent) / 100}
                          strokeLinecap="round"
                          stroke="currentColor"
                          fill="transparent"
                        />
                      </svg>

                      {/* Face ID Viewfinder Framing Brackets */}
                      <div className="absolute inset-4 flex items-center justify-center">
                        <div className="absolute top-0 left-0 w-4 h-4 border-t-3 border-l-3 border-[#FFE600] rounded-tl-md" />
                        <div className="absolute top-0 right-0 w-4 h-4 border-t-3 border-r-3 border-[#FFE600] rounded-tr-md" />
                        <div className="absolute bottom-0 left-0 w-4 h-4 border-b-3 border-l-3 border-[#FFE600] rounded-bl-md" />
                        <div className="absolute bottom-0 right-0 w-4 h-4 border-b-3 border-r-3 border-[#FFE600] rounded-br-md" />

                        {/* Center Face Glyph */}
                        <div
                          className={`w-20 h-20 rounded-2xl border-2 border-black flex items-center justify-center shadow-[3px_3px_0px_#000] relative overflow-hidden transition ${
                            enrollmentStep === 'success'
                              ? 'bg-green-400 text-black'
                              : 'bg-[#FFE600] text-black animate-pulse'
                          }`}
                        >
                          {enrollmentStep === 'success' ? (
                            <CheckCircle2 className="w-12 h-12 stroke-[2.5]" />
                          ) : (
                            <ScanFace className="w-12 h-12 text-black" />
                          )}

                          {/* Sweeping 3D laser line during scanning */}
                          {(enrollmentStep === 'scanning_face' ||
                            enrollmentStep === 'head_roll_1' ||
                            enrollmentStep === 'head_roll_2') && (
                            <div className="absolute inset-x-0 h-1 bg-white shadow-[0_0_8px_#ffffff] animate-bounce" />
                          )}
                        </div>
                      </div>
                    </div>

                    {/* Progress Percent */}
                    <span className="text-2xl font-black font-virgil mb-1 block">
                      {progressPercent}% Complete
                    </span>

                    {/* Dynamic Face ID Instructions */}
                    {enrollmentStep === 'position_face' && (
                      <p className="text-xs font-black uppercase text-neutral-800 dark:text-neutral-200">
                        Position your face in the camera frame...
                      </p>
                    )}

                    {enrollmentStep === 'scanning_face' && (
                      <div className="bg-[#FFE600] text-black px-4 py-2 border-2 border-black rounded-lg shadow-[2px_2px_0px_#000]">
                        <p className="text-xs font-black uppercase tracking-wider">
                          ✨ PROJECTING 30,000 INFRARED DOTS
                        </p>
                        <p className="text-[10px] font-bold mt-0.5">Capturing initial 3D mesh contour</p>
                      </div>
                    )}

                    {enrollmentStep === 'head_roll_1' && (
                      <div className="bg-[#FFE600] text-black px-4 py-2 border-2 border-black rounded-lg shadow-[2px_2px_0px_#000]">
                        <p className="text-xs font-black uppercase tracking-wider">
                          🔄 SLOWLY MOVE YOUR HEAD IN A CIRCLE
                        </p>
                        <p className="text-[10px] font-bold mt-0.5">Show all facial angles to TrueDepth camera</p>
                      </div>
                    )}

                    {enrollmentStep === 'head_roll_2' && (
                      <div className="bg-[#FFE600] text-black px-4 py-2 border-2 border-black rounded-lg shadow-[2px_2px_0px_#000]">
                        <p className="text-xs font-black uppercase tracking-wider">
                          📐 SECOND 3D DEPTH MESH VERIFICATION
                        </p>
                        <p className="text-[10px] font-bold mt-0.5">Finalizing mathematical key in Secure Enclave</p>
                      </div>
                    )}

                    {enrollmentStep === 'success' && (
                      <div className="text-green-600 dark:text-green-400">
                        <p className="text-sm font-black uppercase font-virgil">
                          🎉 Apple Face ID Enrolled Successfully!
                        </p>
                        <p className="text-xs mt-1">
                          Encrypted by Secure Enclave &amp; stored in Cloud Firestore.
                        </p>
                      </div>
                    )}
                  </div>
                ) : (
                  /* -------------------------------------------------- */
                  /* UNIVERSAL FINGERPRINT INTERACTIVE SCANNER          */
                  /* -------------------------------------------------- */
                  <div>
                    <div className="relative w-36 h-36 flex items-center justify-center mb-5 mx-auto">
                      <svg className="absolute inset-0 w-full h-full -rotate-90" viewBox="0 0 100 100">
                        <circle
                          cx="50"
                          cy="50"
                          r="44"
                          className="text-neutral-200 dark:text-neutral-800"
                          strokeWidth="8"
                          stroke="currentColor"
                          fill="transparent"
                        />
                        <circle
                          cx="50"
                          cy="50"
                          r="44"
                          className="text-[#FFE600] transition-all duration-500 ease-out"
                          strokeWidth="8"
                          strokeDasharray={276.4}
                          strokeDashoffset={276.4 - (276.4 * progressPercent) / 100}
                          strokeLinecap="round"
                          stroke="currentColor"
                          fill="transparent"
                        />
                      </svg>

                      <div
                        className={`w-24 h-24 rounded-full border-3 border-black flex items-center justify-center shadow-[3px_3px_0px_#000] transition ${
                          enrollmentStep === 'lift_finger'
                            ? 'bg-neutral-300 text-neutral-700 animate-bounce'
                            : enrollmentStep === 'success'
                            ? 'bg-green-400 text-black'
                            : 'bg-[#FFE600] text-black animate-pulse'
                        }`}
                      >
                        {enrollmentStep === 'success' ? (
                          <CheckCircle2 className="w-12 h-12 stroke-[2.5]" />
                        ) : (
                          <Fingerprint className="w-12 h-12 stroke-[2]" />
                        )}
                      </div>
                    </div>

                    <span className="text-2xl font-black font-virgil mb-1 block">
                      {progressPercent}% Complete
                    </span>

                    {enrollmentStep === 'place_first' && (
                      <p className="text-xs font-black uppercase text-neutral-800 dark:text-neutral-200">
                        Place finger firmly on the sensor...
                      </p>
                    )}

                    {enrollmentStep === 'scanning_first' && (
                      <p className="text-xs font-black uppercase text-neutral-800 dark:text-neutral-200">
                        Reading fingerprint ridges (Center)...
                      </p>
                    )}

                    {enrollmentStep === 'lift_finger' && (
                      <div className="bg-[#FFE600] text-black px-4 py-2 border-2 border-black rounded-lg shadow-[2px_2px_0px_#000]">
                        <p className="text-xs font-black uppercase tracking-wider">
                          👆 LIFT YOUR FINGER FROM SENSOR
                        </p>
                      </div>
                    )}

                    {enrollmentStep === 'place_second' && (
                      <div className="bg-[#FFE600] text-black px-4 py-2 border-2 border-black rounded-lg shadow-[2px_2px_0px_#000]">
                        <p className="text-xs font-black uppercase tracking-wider">
                          👇 PLACE THE SAME FINGER AGAIN
                        </p>
                        <p className="text-[10px] font-bold mt-0.5">Tilt slightly to capture edge contours</p>
                      </div>
                    )}

                    {enrollmentStep === 'scanning_second' && (
                      <p className="text-xs font-black uppercase text-neutral-800 dark:text-neutral-200">
                        Finalizing enrollment &amp; securing in Cloud Firestore...
                      </p>
                    )}

                    {enrollmentStep === 'success' && (
                      <div className="text-green-600 dark:text-green-400">
                        <p className="text-sm font-black uppercase font-virgil">
                          🎉 Fingerprint Successfully Enrolled!
                        </p>
                        <p className="text-xs mt-1">Saved to Cloud Firestore &amp; authorized for login.</p>
                      </div>
                    )}
                  </div>
                )}

                {enrollmentStep === 'error' && (
                  <button
                    type="button"
                    onClick={() => setEnrollmentStep('idle')}
                    className="mt-3 py-2 px-4 bg-neutral-200 dark:bg-neutral-800 border-2 border-black rounded-lg font-black text-xs uppercase cursor-pointer"
                  >
                    Try Again
                  </button>
                )}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
