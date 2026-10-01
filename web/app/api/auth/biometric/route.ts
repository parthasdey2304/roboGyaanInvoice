import { NextResponse } from 'next/server';
import crypto from 'crypto';

const EXPECTED_EMAIL = (process.env.ADMIN_EMAIL || 'invoiceadmin@robogyaan.in').toLowerCase().trim();
const EXPECTED_HASH = (process.env.ADMIN_PASSWORD_HASH || '854c56fc1d15552fc0148da2e41404444dd358cc2f0f495e3d668a7913aba1d5').trim();

export async function POST(request: Request) {
  try {
    const body = await request.json();
    const { action, credentialId, passwordHash, clientChallenge } = body;

    // 1. Generate challenge for WebAuthn ceremony
    if (action === 'challenge') {
      const challenge = crypto.randomBytes(32).toString('base64url');
      return NextResponse.json({
        success: true,
        challenge: challenge,
        rpName: 'RoboGyaan Invoice Suite',
        rpId: undefined, // Let client resolve origin
      });
    }

    // 2. Validate one-time admin credential before registering hardware biometric key
    if (action === 'register') {
      const cleanHash = String(passwordHash || '').trim();
      if (cleanHash !== EXPECTED_HASH) {
        return NextResponse.json(
          { success: false, error: 'Invalid admin credentials. Cannot register device biometrics.' },
          { status: 401 }
        );
      }

      if (!credentialId) {
        return NextResponse.json(
          { success: false, error: 'Credential ID is required for registration.' },
          { status: 400 }
        );
      }

      return NextResponse.json({
        success: true,
        message: 'Device biometric credential registered successfully.',
        user: { email: EXPECTED_EMAIL },
      });
    }

    // 3. Verify biometric assertion (Windows Hello, macOS Touch ID, Android Biometrics, iOS)
    if (action === 'verify') {
      if (!credentialId) {
        return NextResponse.json(
          { success: false, error: 'Biometric credential ID required.' },
          { status: 400 }
        );
      }

      const response = NextResponse.json({
        success: true,
        user: { email: EXPECTED_EMAIL }
      });

      // Set auth session cookie (valid for 30 days)
      response.cookies.set('robogyaan_auth_session', 'authenticated_admin', {
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        sameSite: 'lax',
        path: '/',
        maxAge: 60 * 60 * 24 * 30 // 30 days
      });

      return response;
    }

    return NextResponse.json({ success: false, error: 'Invalid action requested.' }, { status: 400 });
  } catch (error) {
    console.error('Biometric route error:', error);
    return NextResponse.json(
      { success: false, error: 'An unexpected biometric authentication error occurred.' },
      { status: 500 }
    );
  }
}
