/**
 * WebAuthn & Platform Biometric Authentication Utilities for RoboGyaan Invoice Suite
 * Supports Windows Hello (Fingerprint/PIN/Face), Apple Touch ID (macOS/iOS),
 * and Android Biometric Authentication (in-display, power button, and rear sensors).
 */

export const ROBOGYAAN_WEBAUTHN_CRED_KEY = 'robogyaan_webauthn_credential_id';
export const ROBOGYAAN_BIOMETRIC_FAIL_COUNT = 'robogyaan_biometric_failed_attempts';
export const ROBOGYAAN_BIOMETRIC_LOCKED = 'robogyaan_biometric_locked';
export const MAX_BIOMETRIC_ATTEMPTS = 5;

export type BiometricAuthType = 'faceid' | 'fingerprint';

/**
 * Detects if the current client is an Apple hardware device (iPhone, iPad, iPod, Mac).
 */
export function isAppleDevice(): boolean {
  if (typeof window === 'undefined' || !navigator) return false;
  const ua = navigator.userAgent || '';
  const platform = (navigator as any).userAgentData?.platform || navigator.platform || '';
  return (
    /iPhone|iPad|iPod/.test(ua) ||
    /Macintosh|Mac OS X|MacIntel/.test(ua) ||
    (platform === 'MacIntel' && navigator.maxTouchPoints > 1)
  );
}

/**
 * Detects iOS devices (iPhone, iPad, iPod, including iPadOS pretending to be Mac).
 */
export function isIOS(): boolean {
  if (typeof window === 'undefined' || !navigator) return false;
  const ua = navigator.userAgent || '';
  const platform = (navigator as any).userAgentData?.platform || navigator.platform || '';
  return (
    /iPhone|iPad|iPod/.test(ua) ||
    (platform === 'MacIntel' && navigator.maxTouchPoints > 1)
  );
}

/**
 * Detects whether the current browser is Apple Safari (and not Chrome/Edge/Firefox on iOS/Mac).
 */
export function isSafariBrowser(): boolean {
  if (typeof window === 'undefined' || !navigator) return false;
  const ua = navigator.userAgent || '';
  return (
    /Safari/.test(ua) &&
    !/Chrome|CriOS|FxiOS|EdgiOS|OPiOS|mercury/i.test(ua)
  );
}

/**
 * Determines whether the user should be presented with Apple Face ID or Fingerprint authentication.
 * Face ID applies to:
 * - iPhone & iPad on ANY browser (Safari, Chrome iOS, Firefox iOS, etc.)
 * - Apple Safari browsers on macOS / iOS
 * Android and Windows devices are explicitly kept on Fingerprint sensors.
 */
export function isFaceIdPlatform(): boolean {
  if (typeof window === 'undefined' || !navigator) return false;
  const ua = navigator.userAgent || '';

  // Explicit exclusion: Android and Windows are always Fingerprint
  if (/Android/i.test(ua) || /Windows/i.test(ua)) {
    return false;
  }

  // 1. iPhone or iPad (any browser)
  if (isIOS()) {
    return true;
  }

  // 2. Safari on an Apple platform (macOS or iOS)
  if (isAppleDevice() && isSafariBrowser()) {
    return true;
  }

  return false;
}

export function getBiometricType(): BiometricAuthType {
  return isFaceIdPlatform() ? 'faceid' : 'fingerprint';
}

export function bufferToBase64Url(buffer: ArrayBuffer): string {
  const bytes = new Uint8Array(buffer);
  let str = '';
  for (let i = 0; i < bytes.length; i++) {
    str += String.fromCharCode(bytes[i]);
  }
  return btoa(str).replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '');
}

export function base64UrlToBuffer(base64url: string): Uint8Array {
  const base64 = base64url.replace(/-/g, '+').replace(/_/g, '/');
  const pad = base64.length % 4;
  const padded = pad ? base64 + '='.repeat(4 - pad) : base64;
  const binary = atob(padded);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) {
    bytes[i] = binary.charCodeAt(i);
  }
  return bytes;
}

export async function isPlatformBiometricAvailable(): Promise<boolean> {
  if (typeof window === 'undefined' || !window.PublicKeyCredential) {
    return false;
  }
  try {
    if (typeof PublicKeyCredential.isUserVerifyingPlatformAuthenticatorAvailable === 'function') {
      return await PublicKeyCredential.isUserVerifyingPlatformAuthenticatorAvailable();
    }
    return true;
  } catch {
    return false;
  }
}

export function getBiometricLockoutStatus(): { isLocked: boolean; failedAttempts: number } {
  if (typeof window === 'undefined') return { isLocked: false, failedAttempts: 0 };
  const count = parseInt(localStorage.getItem(ROBOGYAAN_BIOMETRIC_FAIL_COUNT) || '0', 10);
  const locked = localStorage.getItem(ROBOGYAAN_BIOMETRIC_LOCKED) === 'true' || count >= MAX_BIOMETRIC_ATTEMPTS;
  return { isLocked: locked, failedAttempts: count };
}

export function recordFailedBiometricAttempt(): { isLocked: boolean; count: number } {
  if (typeof window === 'undefined') return { isLocked: false, count: 0 };
  const current = parseInt(localStorage.getItem(ROBOGYAAN_BIOMETRIC_FAIL_COUNT) || '0', 10) + 1;
  const locked = current >= MAX_BIOMETRIC_ATTEMPTS;
  localStorage.setItem(ROBOGYAAN_BIOMETRIC_FAIL_COUNT, String(current));
  if (locked) {
    localStorage.setItem(ROBOGYAAN_BIOMETRIC_LOCKED, 'true');
  }
  return { isLocked: locked, count: current };
}

export function resetBiometricAttempts(): void {
  if (typeof window === 'undefined') return;
  localStorage.setItem(ROBOGYAAN_BIOMETRIC_FAIL_COUNT, '0');
  localStorage.removeItem(ROBOGYAAN_BIOMETRIC_LOCKED);
}

export function getSavedCredentialId(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem(ROBOGYAAN_WEBAUTHN_CRED_KEY);
}

export function saveCredentialId(credId: string): void {
  if (typeof window === 'undefined') return;
  localStorage.setItem(ROBOGYAAN_WEBAUTHN_CRED_KEY, credId);
}

export async function registerPlatformCredential(
  challenge: string,
  email: string
): Promise<{ credentialId: string; rawId: string }> {
  const challengeBuffer = base64UrlToBuffer(challenge);
  const userId = new TextEncoder().encode(email);

  const credential = (await navigator.credentials.create({
    publicKey: {
      challenge: challengeBuffer as unknown as BufferSource,
      rp: {
        name: 'RoboGyaan Invoice Suite',
        id: window.location.hostname === 'localhost' ? 'localhost' : window.location.hostname,
      },
      user: {
        id: userId as unknown as BufferSource,
        name: email,
        displayName: 'RoboGyaan Invoice Admin',
      },
      pubKeyCredParams: [
        { alg: -7, type: 'public-key' },  // ES256
        { alg: -257, type: 'public-key' }, // RS256
      ],
      authenticatorSelection: {
        authenticatorAttachment: 'platform', // Enforce Windows Hello / Touch ID / Android biometrics
        userVerification: 'required',
        residentKey: 'preferred',
      },
      timeout: 60000,
      attestation: 'none',
    },
  })) as PublicKeyCredential;

  if (!credential) {
    throw new Error('Biometric credential creation cancelled or failed.');
  }

  const credIdBase64 = bufferToBase64Url(credential.rawId);
  saveCredentialId(credIdBase64);

  return {
    credentialId: credential.id,
    rawId: credIdBase64,
  };
}

export async function verifyPlatformCredential(
  challenge: string,
  credentialIdBase64?: string
): Promise<{ success: boolean; credentialId: string }> {
  const challengeBuffer = base64UrlToBuffer(challenge);
  const allowCredentials = credentialIdBase64
    ? [
        {
          id: base64UrlToBuffer(credentialIdBase64) as unknown as BufferSource,
          type: 'public-key' as const,
          transports: ['internal' as const],
        },
      ]
    : undefined;

  const assertion = (await navigator.credentials.get({
    publicKey: {
      challenge: challengeBuffer as unknown as BufferSource,
      rpId: window.location.hostname === 'localhost' ? 'localhost' : window.location.hostname,
      allowCredentials: allowCredentials,
      userVerification: 'required',
      timeout: 60000,
    },
  })) as PublicKeyCredential;

  if (!assertion) {
    throw new Error('Biometric verification cancelled or failed.');
  }

  const rawIdBase64 = bufferToBase64Url(assertion.rawId);
  return {
    success: true,
    credentialId: rawIdBase64,
  };
}
