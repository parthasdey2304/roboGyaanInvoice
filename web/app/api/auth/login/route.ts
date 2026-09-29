import { NextResponse } from 'next/server';

const EXPECTED_EMAIL = (process.env.ADMIN_EMAIL || 'invoiceadmin@robogyaan.in').toLowerCase().trim();
const EXPECTED_HASH = (process.env.ADMIN_PASSWORD_HASH || '854c56fc1d15552fc0148da2e41404444dd358cc2f0f495e3d668a7913aba1d5').trim();

export async function POST(request: Request) {
  try {
    const body = await request.json();
    const { email, passwordHash } = body;

    if (!email || !passwordHash) {
      return NextResponse.json(
        { success: false, error: 'Email and password are required' },
        { status: 400 }
      );
    }

    const cleanEmail = String(email).toLowerCase().trim();
    const cleanHash = String(passwordHash).trim();

    if (cleanEmail === EXPECTED_EMAIL && cleanHash === EXPECTED_HASH) {
      const response = NextResponse.json({
        success: true,
        user: { email: cleanEmail }
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

    return NextResponse.json(
      { success: false, error: 'Invalid email or password' },
      { status: 401 }
    );
  } catch (error) {
    console.error('Login error:', error);
    return NextResponse.json(
      { success: false, error: 'An unexpected authentication error occurred' },
      { status: 500 }
    );
  }
}
