import { NextResponse } from 'next/server';
import { cookies } from 'next/headers';

const EXPECTED_EMAIL = (process.env.ADMIN_EMAIL || 'invoiceadmin@robogyaan.in').toLowerCase().trim();

export async function GET() {
  try {
    const cookieStore = await cookies();
    const session = cookieStore.get('robogyaan_auth_session');

    if (session && session.value === 'authenticated_admin') {
      return NextResponse.json({
        authenticated: true,
        user: { email: EXPECTED_EMAIL }
      });
    }

    return NextResponse.json({ authenticated: false });
  } catch (err) {
    return NextResponse.json({ authenticated: false });
  }
}
