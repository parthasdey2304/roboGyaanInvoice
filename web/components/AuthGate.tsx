'use client';

import React, { useState, useEffect } from 'react';
import { hashPasswordWithArgon2 } from '../lib/crypto';
import {
  Shield,
  Lock,
  Mail,
  ArrowRight,
  Loader2,
  KeyRound,
  Sun,
  Moon,
  Fingerprint,
  AlertTriangle,
  Sparkles,
  CheckCircle2
} from 'lucide-react';
import { InteractiveGridBackground } from './InteractiveGridBackground';
import {
  isPlatformBiometricAvailable,
  getBiometricLockoutStatus,
  recordFailedBiometricAttempt,
  resetBiometricAttempts,
  getSavedCredentialId,
  saveCredentialId,
  registerPlatformCredential,
  verifyPlatformCredential,
  MAX_BIOMETRIC_ATTEMPTS,
} from '../lib/webauthn';

interface AuthGateProps {
  children: (user: { email: string }, onLogout: () => void) => React.ReactNode;
}

export const AuthGate: React.FC<AuthGateProps> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);
  const [userEmail, setUserEmail] = useState<string>('invoiceadmin@robogyaan.in');
  const [emailInput, setEmailInput] = useState<string>('invoiceadmin@robogyaan.in');
  const [passwordInput, setPasswordInput] = useState<string>('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [hashingProgress, setHashingProgress] = useState<boolean>(false);
  const [theme, setTheme] = useState<'light' | 'dark'>('light');

  // Biometric state
  const [authMode, setAuthMode] = useState<'fingerprint' | 'password'>('fingerprint');
  const [biometricAvailable, setBiometricAvailable] = useState<boolean>(false);
  const [isBiometricLocked, setIsBiometricLocked] = useState<boolean>(false);
  const [failedAttempts, setFailedAttempts] = useState<number>(0);
  const [hasRegisteredCred, setHasRegisteredCred] = useState<boolean>(false);
  const [setupPassword, setSetupPassword] = useState<string>('');
  const [biometricFeedback, setBiometricFeedback] = useState<string | null>(null);

  // Load theme, check existing session, and check biometric support
  useEffect(() => {
    const savedTheme = localStorage.getItem('robogyaan_theme') as 'light' | 'dark' | null;
    if (savedTheme) {
      setTheme(savedTheme);
      if (savedTheme === 'dark') {
        document.documentElement.classList.add('dark');
      } else {
        document.documentElement.classList.remove('dark');
      }
    } else {
      document.documentElement.classList.remove('dark');
    }

    // Check biometric lockout status
    const lockout = getBiometricLockoutStatus();
    setIsBiometricLocked(lockout.isLocked);
    setFailedAttempts(lockout.failedAttempts);

    // Check if biometric credential exists
    const credId = getSavedCredentialId();
    setHasRegisteredCred(!!credId);

    // Initial default auth mode
    if (lockout.isLocked) {
      setAuthMode('password');
    } else {
      setAuthMode('fingerprint');
    }

    // Detect hardware support (Windows Hello / Apple Touch ID / Android biometrics)
    isPlatformBiometricAvailable().then((supported) => {
      setBiometricAvailable(supported);
      if (!supported && !credId) {
        setAuthMode('password');
      }
    });

    const checkSession = async () => {
      try {
        const res = await fetch('/api/auth/check');
        const data = await res.json();
        if (data.authenticated && data.user?.email) {
          setIsAuthenticated(true);
          setUserEmail(data.user.email);
          return;
        }
      } catch (e) {
        console.error('Session check error:', e);
      }

      // Check local fallback
      const savedAuth = localStorage.getItem('robogyaan_authenticated_admin');
      if (savedAuth === 'true') {
        setIsAuthenticated(true);
      } else {
        setIsAuthenticated(false);
      }
    };

    checkSession();
  }, []);

  const toggleTheme = () => {
    const nextTheme = theme === 'dark' ? 'light' : 'dark';
    setTheme(nextTheme);
    localStorage.setItem('robogyaan_theme', nextTheme);
    if (nextTheme === 'dark') {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  };

  // Handle Admin Password Login
  const handlePasswordLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!emailInput.trim() || !passwordInput) {
      setError('Please provide both email and password.');
      return;
    }

    try {
      setLoading(true);
      setHashingProgress(true);

      // Derive Argon2id 32-byte hex hash client-side (plaintext password is NEVER sent)
      const passwordHash = await hashPasswordWithArgon2(passwordInput);
      setHashingProgress(false);

      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: emailInput.trim(),
          passwordHash: passwordHash,
        }),
      });

      const data = await res.json();

      if (res.ok && data.success) {
        localStorage.setItem('robogyaan_authenticated_admin', 'true');
        // Reset any prior biometric lockout on successful password login
        resetBiometricAttempts();
        setIsBiometricLocked(false);
        setFailedAttempts(0);

        setUserEmail(emailInput.trim());
        setIsAuthenticated(true);
      } else {
        setError(data.error || 'Invalid credentials. Please verify your email and password.');
      }
    } catch (err: any) {
      console.error('Login error:', err);
      setError(err?.message || 'Authentication failed. Please check your credentials.');
    } finally {
      setLoading(false);
      setHashingProgress(false);
    }
  };

  // Handle Fingerprint / Biometric Authentication
  const handleBiometricAuth = async () => {
    if (isBiometricLocked) {
      setAuthMode('password');
      setError('Fingerprint login locked after 5 failed attempts. Please use your Admin Password.');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      setBiometricFeedback(null);

      // Step 1: Get challenge from server
      const chalRes = await fetch('/api/auth/biometric', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ action: 'challenge' }),
      });
      const chalData = await chalRes.json();
      if (!chalRes.ok || !chalData.challenge) {
        throw new Error('Unable to acquire authentication challenge.');
      }

      // Step 2: Trigger platform biometric verification prompt
      const credId = getSavedCredentialId() || undefined;
      const verifyResult = await verifyPlatformCredential(chalData.challenge, credId);

      // Step 3: Verify with backend
      const loginRes = await fetch('/api/auth/biometric', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          action: 'verify',
          credentialId: verifyResult.credentialId,
        }),
      });
      const loginData = await loginRes.json();

      if (loginRes.ok && loginData.success) {
        localStorage.setItem('robogyaan_authenticated_admin', 'true');
        resetBiometricAttempts();
        setIsBiometricLocked(false);
        setFailedAttempts(0);
        setUserEmail(loginData.user?.email || 'invoiceadmin@robogyaan.in');
        setIsAuthenticated(true);
      } else {
        throw new Error(loginData.error || 'Biometric verification failed.');
      }
    } catch (err: any) {
      console.error('Biometric authentication failed:', err);
      const { isLocked, count } = recordFailedBiometricAttempt();
      setFailedAttempts(count);

      if (isLocked) {
        setIsBiometricLocked(true);
        setAuthMode('password');
        setError(
          `Maximum fingerprint attempts reached (${MAX_BIOMETRIC_ATTEMPTS}/${MAX_BIOMETRIC_ATTEMPTS}). Fingerprint login is locked. Please enter your Admin Password.`
        );
      } else {
        setBiometricFeedback(
          `Fingerprint not recognized or cancelled. Attempt ${count} of ${MAX_BIOMETRIC_ATTEMPTS}.`
        );
      }
    } finally {
      setLoading(false);
    }
  };

  // Handle Initial Device Biometric Registration
  const handleRegisterDeviceBiometrics = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!setupPassword) {
      setError('Please enter your Admin Password to pair device biometrics.');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      setHashingProgress(true);

      const passwordHash = await hashPasswordWithArgon2(setupPassword);
      setHashingProgress(false);

      // Get challenge
      const chalRes = await fetch('/api/auth/biometric', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ action: 'challenge' }),
      });
      const chalData = await chalRes.json();

      // Register platform credential (Touch ID / Windows Hello / Android biometric)
      const cred = await registerPlatformCredential(chalData.challenge, emailInput.trim());

      // Save on server
      const regRes = await fetch('/api/auth/biometric', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          action: 'register',
          passwordHash: passwordHash,
          credentialId: cred.rawId,
        }),
      });
      const regData = await regRes.json();

      if (regRes.ok && regData.success) {
        saveCredentialId(cred.rawId);
        setHasRegisteredCred(true);
        resetBiometricAttempts();
        setIsBiometricLocked(false);
        setFailedAttempts(0);
        localStorage.setItem('robogyaan_authenticated_admin', 'true');
        setUserEmail(emailInput.trim());
        setIsAuthenticated(true);
      } else {
        setError(regData.error || 'Failed to register biometric sensor.');
      }
    } catch (err: any) {
      console.error('Registration error:', err);
      setError(err?.message || 'Could not register biometric hardware on this device.');
    } finally {
      setLoading(false);
      setHashingProgress(false);
    }
  };

  const handleLogout = async () => {
    try {
      await fetch('/api/auth/logout', { method: 'POST' });
    } catch (e) {
      console.error('Logout error:', e);
    }
    localStorage.removeItem('robogyaan_authenticated_admin');
    setIsAuthenticated(false);
    setPasswordInput('');
    setSetupPassword('');
  };

  // Initial loading spinner
  if (isAuthenticated === null) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-transparent relative">
        <InteractiveGridBackground theme={theme} enabled={true} />
        <div className="flex flex-col items-center gap-3 relative z-10">
          <div className="w-12 h-12 bg-[#FFE600] border-2 border-black rounded-lg shadow-[3px_3px_0px_#000] flex items-center justify-center animate-bounce">
            <Shield className="w-6 h-6 text-black" />
          </div>
          <p
            className={`text-xs font-black font-virgil uppercase tracking-wider ${
              theme === 'dark' ? 'text-white' : 'text-black'
            }`}
          >
            Verifying Admin Session...
          </p>
        </div>
      </div>
    );
  }

  // If already authenticated, render app children
  if (isAuthenticated) {
    return <>{children({ email: userEmail }, handleLogout)}</>;
  }

  const isDark = theme === 'dark';

  return (
    <div
      className={`min-h-screen flex flex-col justify-center items-center p-4 sm:p-6 relative select-none ${
        isDark ? 'text-white' : 'text-black'
      }`}
    >
      {/* 3D Interactive Grid Background with Cursor Swelling Effect */}
      <InteractiveGridBackground theme={theme} enabled={true} />

      {/* Top Banner & Theme Switcher */}
      <div className="max-w-md w-full mb-4 flex items-center justify-between gap-2 relative z-10">
        <div className="inline-flex items-center gap-2 bg-[#FFE600] text-black border-2 border-black px-3 py-1 rounded-full shadow-[2px_2px_0px_#000]">
          <Shield className="w-4 h-4 text-black" />
          <span className="text-[11px] font-black uppercase tracking-wider">
            Biometric &bull; Argon2id &bull; Cloud Firestore
          </span>
        </div>

        {/* Sun/Moon Toggle in same span */}
        <button
          type="button"
          onClick={toggleTheme}
          className="p-1.5 bg-black text-[#FFE600] border-2 border-black rounded-lg shadow-[2px_2px_0px_#000] hover:scale-105 active:translate-x-[1px] active:translate-y-[1px] transition cursor-pointer"
          title={isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
          aria-label={isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
        >
          <span className="flex items-center justify-center">
            {isDark ? <Sun className="w-4 h-4 text-[#FFE600]" /> : <Moon className="w-4 h-4 text-[#FFE600]" />}
          </span>
        </button>
      </div>

      <div
        className={`max-w-md w-full border-[3.5px] border-black rounded-xl p-6 sm:p-8 shadow-[8px_8px_0px_#000000] relative z-10 ${
          isDark ? 'bg-[#18181b] border-white/80' : 'bg-white'
        }`}
      >
        {/* Portal Header */}
        <div className="flex items-center gap-3 mb-5 pb-4 border-b-2 border-black dark:border-neutral-700">
          <div className="w-12 h-12 bg-black text-[#FFE600] font-black rounded-lg flex items-center justify-center text-2xl border-2 border-black shadow-[3px_3px_0px_#FFE600] shrink-0">
            RG
          </div>
          <div>
            <h1 className="text-xl sm:text-2xl font-black tracking-tight leading-none uppercase">
              RoboGyaan Invoice
            </h1>
            <p className="text-[11px] font-bold text-neutral-500 dark:text-neutral-300 uppercase tracking-wider mt-1">
              Admin Authentication Portal
            </p>
          </div>
        </div>

        {/* Two Options for Login: Fingerprint and Admin Password */}
        <div className="grid grid-cols-2 gap-2 mb-5">
          <button
            type="button"
            disabled={isBiometricLocked}
            onClick={() => {
              if (!isBiometricLocked) {
                setAuthMode('fingerprint');
                setError(null);
              }
            }}
            className={`py-2 px-3 border-2 border-black rounded-lg font-black text-xs uppercase tracking-wider flex items-center justify-center gap-2 transition cursor-pointer shadow-[2px_2px_0px_#000] active:translate-x-[1px] active:translate-y-[1px] ${
              isBiometricLocked
                ? 'opacity-50 cursor-not-allowed bg-neutral-200 dark:bg-neutral-800 text-neutral-500'
                : authMode === 'fingerprint'
                ? 'bg-[#FFE600] text-black shadow-[3px_3px_0px_#000]'
                : isDark
                ? 'bg-[#27272a] text-white hover:bg-[#323238]'
                : 'bg-neutral-100 text-black hover:bg-neutral-200'
            }`}
          >
            {isBiometricLocked ? (
              <Lock className="w-4 h-4 text-red-500" />
            ) : (
              <Fingerprint className="w-4 h-4" />
            )}
            <span>{isBiometricLocked ? 'Locked' : 'Fingerprint'}</span>
          </button>

          <button
            type="button"
            onClick={() => {
              setAuthMode('password');
              setError(null);
            }}
            className={`py-2 px-3 border-2 border-black rounded-lg font-black text-xs uppercase tracking-wider flex items-center justify-center gap-2 transition cursor-pointer shadow-[2px_2px_0px_#000] active:translate-x-[1px] active:translate-y-[1px] ${
              authMode === 'password' || isBiometricLocked
                ? 'bg-[#FFE600] text-black shadow-[3px_3px_0px_#000]'
                : isDark
                ? 'bg-[#27272a] text-white hover:bg-[#323238]'
                : 'bg-neutral-100 text-black hover:bg-neutral-200'
            }`}
          >
            <Lock className="w-4 h-4" />
            <span>Admin Password</span>
          </button>
        </div>

        {/* Lockout Warning Banner when 5 attempts are exceeded */}
        {isBiometricLocked && (
          <div className="mb-4 bg-red-100 dark:bg-red-950/80 border-2 border-red-600 rounded-lg p-3 text-red-900 dark:text-red-200 text-xs font-bold shadow-[2px_2px_0px_#dc2626] flex items-start gap-2">
            <AlertTriangle className="w-5 h-5 text-red-600 shrink-0 mt-0.5" />
            <div>
              <p className="font-black uppercase tracking-wide text-red-700 dark:text-red-300">
                Fingerprint Option Locked (5/5 Failed)
              </p>
              <p className="text-[11px] leading-tight mt-0.5">
                Maximum biometric attempts exceeded. For your security, fingerprint access is locked. Please authenticate using your Admin Password below to unlock.
              </p>
            </div>
          </div>
        )}

        {/* Informational Security Notice */}
        {!isBiometricLocked && (
          <div
            className={`mb-5 border-2 border-black rounded-lg p-3 shadow-[2px_2px_0px_#000] ${
              isDark ? 'bg-[#27272a]' : 'bg-[#FFFDE6]'
            }`}
          >
            <div className="flex items-start gap-2">
              <KeyRound className="w-4 h-4 text-[#FFE600] shrink-0 mt-0.5" />
              <p className="text-[11px] font-bold leading-tight">
                {authMode === 'fingerprint' ? (
                  <>
                    Compatible with <span className="underline font-black text-[#FFE600]">Windows Hello</span>,{' '}
                    <span className="underline font-black text-[#FFE600]">MacBook Touch ID</span>, and{' '}
                    <span className="underline font-black text-[#FFE600]">Android fingerprint sensors</span> (in-display, power button, rear).
                  </>
                ) : (
                  <>
                    Password is encrypted client-side using <span className="underline font-black text-[#FFE600]">Argon2id</span>. Plaintext is never transmitted.
                  </>
                )}
              </p>
            </div>
          </div>
        )}

        {/* Error message banner */}
        {error && (
          <div className="mb-4 bg-red-100 dark:bg-red-950/60 border-2 border-red-500 rounded-lg p-3 text-red-900 dark:text-red-200 text-xs font-bold shadow-[2px_2px_0px_#dc2626]">
            {error}
          </div>
        )}

        {/* ----------------------------------------------------------------- */}
        {/* OPTION 1: FINGERPRINT LOGIN VIEW                                  */}
        {/* ----------------------------------------------------------------- */}
        {authMode === 'fingerprint' && !isBiometricLocked && (
          <div className="space-y-4">
            {hasRegisteredCred ? (
              <div className="text-center py-3">
                <div
                  onClick={handleBiometricAuth}
                  className="mx-auto w-24 h-24 rounded-full border-4 border-black bg-[#FFE600] flex items-center justify-center cursor-pointer shadow-[4px_4px_0px_#000] hover:scale-105 active:scale-95 transition"
                  title="Touch sensor to authenticate"
                >
                  <Fingerprint className="w-14 h-14 text-black animate-pulse" />
                </div>

                <h3 className="text-sm font-black uppercase tracking-wider mt-4">
                  Touch Fingerprint Sensor
                </h3>
                <p className="text-[11px] font-bold text-neutral-500 dark:text-neutral-400 mt-1">
                  Windows Hello &bull; Touch ID &bull; Android Biometrics
                </p>

                {/* Remaining Attempts Pill */}
                <div className="mt-3 inline-flex items-center gap-1.5 px-3 py-1 bg-neutral-100 dark:bg-neutral-800 border border-neutral-300 dark:border-neutral-700 rounded-full text-[10px] font-bold">
                  <span
                    className={
                      failedAttempts >= 3
                        ? 'text-red-600 dark:text-red-400 font-black'
                        : 'text-neutral-600 dark:text-neutral-300'
                    }
                  >
                    {MAX_BIOMETRIC_ATTEMPTS - failedAttempts} of {MAX_BIOMETRIC_ATTEMPTS} attempts remaining
                  </span>
                </div>

                {biometricFeedback && (
                  <p className="text-xs font-bold text-red-600 dark:text-red-400 mt-2">
                    {biometricFeedback}
                  </p>
                )}

                <button
                  type="button"
                  disabled={loading}
                  onClick={handleBiometricAuth}
                  className="w-full mt-5 py-3 bg-[#FFE600] text-black font-black text-xs uppercase tracking-wider border-2 border-black rounded-lg shadow-[4px_4px_0px_#000] hover:bg-[#FFD700] flex items-center justify-center gap-2 transition active:translate-x-[2px] active:translate-y-[2px] disabled:opacity-60 cursor-pointer"
                >
                  {loading ? (
                    <>
                      <Loader2 className="w-4 h-4 animate-spin text-black" />
                      <span className="text-black">Listening for Fingerprint Sensor...</span>
                    </>
                  ) : (
                    <>
                      <Fingerprint className="w-4 h-4 text-black" />
                      <span className="text-black">Scan Fingerprint to Sign In</span>
                    </>
                  )}
                </button>
              </div>
            ) : (
              /* One-time setup on this browser to pair Touch ID / Windows Hello */
              <form onSubmit={handleRegisterDeviceBiometrics} className="space-y-3">
                <div className="border-2 border-black rounded-lg p-3 bg-neutral-50 dark:bg-neutral-800/80">
                  <div className="flex items-center gap-2 text-xs font-black uppercase text-[#FFE600]">
                    <Sparkles className="w-4 h-4 text-black dark:text-[#FFE600]" />
                    <span>Setup Fingerprint on this Device</span>
                  </div>
                  <p className="text-[11px] font-medium text-neutral-600 dark:text-neutral-300 mt-1 leading-snug">
                    Enter your admin password once to register your laptop or phone&apos;s fingerprint sensor (Touch ID, Windows Hello, or Android fingerprint).
                  </p>
                </div>

                <div>
                  <label className="block text-xs font-black uppercase tracking-wider mb-1.5 flex items-center gap-1.5">
                    <Lock className="w-3.5 h-3.5" />
                    Admin Password (For Device Verification)
                  </label>
                  <input
                    type="password"
                    value={setupPassword}
                    onChange={(e) => setSetupPassword(e.target.value)}
                    placeholder="Enter password to link sensor"
                    required
                    className={`neo-input w-full text-xs font-semibold py-2.5 px-3 border-2 border-black rounded-lg shadow-[2.5px_2.5px_0px_#000] focus:shadow-[4px_4px_0px_#FFE600] outline-none ${
                      isDark ? 'bg-[#27272a] text-white border-neutral-600' : 'bg-white text-black'
                    }`}
                  />
                </div>

                <button
                  type="submit"
                  disabled={loading}
                  className="w-full mt-2 py-3 bg-[#FFE600] text-black font-black text-xs uppercase tracking-wider border-2 border-black rounded-lg shadow-[4px_4px_0px_#000] hover:bg-[#FFD700] flex items-center justify-center gap-2 transition active:translate-x-[2px] active:translate-y-[2px] disabled:opacity-60 cursor-pointer"
                >
                  {loading ? (
                    <>
                      <Loader2 className="w-4 h-4 animate-spin text-black" />
                      <span className="text-black">
                        {hashingProgress ? 'Computing Argon2 Hash...' : 'Connecting to Sensor...'}
                      </span>
                    </>
                  ) : (
                    <>
                      <Fingerprint className="w-4 h-4 text-black" />
                      <span className="text-black">Pair Sensor &amp; Authenticate</span>
                    </>
                  )}
                </button>
              </form>
            )}

            <div className="text-center pt-2">
              <button
                type="button"
                onClick={() => setAuthMode('password')}
                className="text-[11px] font-bold underline hover:text-[#FFE600] cursor-pointer"
              >
                Or use standard Admin Password login &rarr;
              </button>
            </div>
          </div>
        )}

        {/* ----------------------------------------------------------------- */}
        {/* OPTION 2: ADMIN PASSWORD LOGIN VIEW                               */}
        {/* ----------------------------------------------------------------- */}
        {(authMode === 'password' || isBiometricLocked) && (
          <form onSubmit={handlePasswordLogin} className="space-y-4">
            <div>
              <label className="block text-xs font-black uppercase tracking-wider mb-1.5 flex items-center gap-1.5">
                <Mail className="w-3.5 h-3.5" />
                Admin Email Address
              </label>
              <input
                type="email"
                value={emailInput}
                onChange={(e) => setEmailInput(e.target.value)}
                placeholder="invoiceadmin@robogyaan.in"
                required
                className={`neo-input w-full text-xs font-semibold py-2.5 px-3 border-2 border-black rounded-lg shadow-[2.5px_2.5px_0px_#000] focus:shadow-[4px_4px_0px_#FFE600] outline-none ${
                  isDark ? 'bg-[#27272a] text-white border-neutral-600' : 'bg-white text-black'
                }`}
              />
            </div>

            <div>
              <label className="block text-xs font-black uppercase tracking-wider mb-1.5 flex items-center gap-1.5">
                <Lock className="w-3.5 h-3.5" />
                Admin Password
              </label>
              <input
                type="password"
                value={passwordInput}
                onChange={(e) => setPasswordInput(e.target.value)}
                placeholder="Enter admin password"
                required
                className={`neo-input w-full text-xs font-semibold py-2.5 px-3 border-2 border-black rounded-lg shadow-[2.5px_2.5px_0px_#000] focus:shadow-[4px_4px_0px_#FFE600] outline-none ${
                  isDark ? 'bg-[#27272a] text-white border-neutral-600' : 'bg-white text-black'
                }`}
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full mt-2 py-3 bg-[#FFE600] text-black font-black text-xs uppercase tracking-wider border-2 border-black rounded-lg shadow-[4px_4px_0px_#000] hover:bg-[#FFD700] flex items-center justify-center gap-2 transition active:translate-x-[2px] active:translate-y-[2px] disabled:opacity-60 cursor-pointer"
            >
              {loading ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin text-black" />
                  <span className="text-black">
                    {hashingProgress ? 'Computing Argon2 Hash...' : 'Authenticating...'}
                  </span>
                </>
              ) : (
                <>
                  <span className="text-black">Sign In with Password</span>
                  <ArrowRight className="w-4 h-4 text-black" />
                </>
              )}
            </button>

            {!isBiometricLocked && (
              <div className="text-center pt-2">
                <button
                  type="button"
                  onClick={() => setAuthMode('fingerprint')}
                  className="text-[11px] font-bold underline hover:text-[#FFE600] cursor-pointer"
                >
                  &larr; Switch to Fingerprint Login
                </button>
              </div>
            )}
          </form>
        )}

        {/* Footer info */}
        <div className="mt-6 pt-4 border-t border-neutral-200 dark:border-neutral-800 text-center">
          <p className="text-[10px] font-bold text-neutral-500 dark:text-neutral-400">
            RoboGyaan Invoice Suite &bull; Official Management System
          </p>
        </div>
      </div>
    </div>
  );
};
