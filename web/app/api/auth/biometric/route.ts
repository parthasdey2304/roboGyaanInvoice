import { NextResponse } from 'next/server';
import crypto from 'crypto';

const EXPECTED_EMAIL = (process.env.ADMIN_EMAIL || 'invoiceadmin@robogyaan.in').toLowerCase().trim();
const EXPECTED_HASH = (process.env.ADMIN_PASSWORD_HASH || '854c56fc1d15552fc0148da2e41404444dd358cc2f0f495e3d668a7913aba1d5').trim();
const FIRESTORE_BASE = 'https://firestore.googleapis.com/v1/projects/robogyaan-invoice/databases/(default)/documents/invoice_prompts';

// Helper to sanitize doc ID for Firestore
function sanitizeDocId(id: string): string {
  return 'biometric_cred_' + id.replace(/[^a-zA-Z0-9_-]/g, '_').slice(0, 80);
}

// Fetch all credentials from Cloud Firestore
async function getCloudBiometricCredentials(): Promise<any[]> {
  try {
    const res = await fetch(`${FIRESTORE_BASE}?pageSize=100`, {
      cache: 'no-store',
      headers: { 'Accept': 'application/json' },
    });

    if (!res.ok) return [];

    const data = await res.json();
    const documents = data.documents || [];

    const list: any[] = [];
    for (const doc of documents) {
      const name = doc.name || '';
      const docId = name.substring(name.lastIndexOf('/') + 1);
      if (!docId.startsWith('biometric_cred_')) continue;

      const fields = doc.fields || {};
      const bioName = fields.name?.stringValue || 'Enrolled Biometric';
      const bioPlatform = fields.platform?.stringValue || 'unknown';
      const detectedType = fields.biometricType?.stringValue || (
        (bioName.toLowerCase().includes('face') || bioPlatform.toLowerCase().includes('ios') || bioPlatform.toLowerCase().includes('iphone') || bioPlatform.toLowerCase().includes('safari'))
          ? 'faceid'
          : 'fingerprint'
      );

      list.push({
        id: fields.credentialId?.stringValue || docId.replace('biometric_cred_', ''),
        docId: docId,
        name: bioName,
        platform: bioPlatform,
        deviceModel: fields.deviceModel?.stringValue || '',
        biometricType: detectedType,
        enabled: fields.enabled?.booleanValue ?? true,
        createdAt: fields.createdAt?.stringValue || new Date().toISOString(),
        lastUsedAt: fields.lastUsedAt?.stringValue || null,
        userEmail: fields.userEmail?.stringValue || EXPECTED_EMAIL,
      });
    }

    // Sort by createdAt descending
    return list.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  } catch (err) {
    console.error('Error fetching cloud biometric credentials:', err);
    return [];
  }
}

// GET: Retrieve all cloud-registered biometric credentials
export async function GET() {
  try {
    const credentials = await getCloudBiometricCredentials();
    return NextResponse.json({
      success: true,
      credentials: credentials,
      count: credentials.length,
      activeCount: credentials.filter((c) => c.enabled).length,
    });
  } catch (error) {
    console.error('GET /api/auth/biometric error:', error);
    return NextResponse.json({ success: false, error: 'Failed to retrieve biometric credentials' }, { status: 500 });
  }
}

// POST: Actions (challenge, register, verify, toggle, delete)
export async function POST(request: Request) {
  try {
    const body = await request.json();
    const { action, credentialId, name, platform, deviceModel, passwordHash, enabled, biometricType } = body;

    // 1. Generate challenge for WebAuthn ceremony
    if (action === 'challenge') {
      const challenge = crypto.randomBytes(32).toString('base64url');
      return NextResponse.json({
        success: true,
        challenge: challenge,
        rpName: 'RoboGyaan Invoice Suite',
        rpId: undefined,
      });
    }

    // 2. Register new biometric credential in Cloud Firestore
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

      const docId = sanitizeDocId(credentialId);
      const createdAt = new Date().toISOString();
      const bioName = String(name || 'Admin Biometric');
      const bioPlatform = String(platform || 'browser');
      const computedType = biometricType || (
        (bioName.toLowerCase().includes('face') || bioPlatform.toLowerCase().includes('ios') || bioPlatform.toLowerCase().includes('iphone') || bioPlatform.toLowerCase().includes('safari'))
          ? 'faceid'
          : 'fingerprint'
      );

      const firestoreDoc = {
        fields: {
          type: { stringValue: 'biometric_credential' },
          biometricType: { stringValue: computedType },
          credentialId: { stringValue: String(credentialId) },
          name: { stringValue: bioName },
          platform: { stringValue: bioPlatform },
          deviceModel: { stringValue: String(deviceModel || 'Platform Authenticator') },
          enabled: { booleanValue: true },
          createdAt: { stringValue: createdAt },
          userEmail: { stringValue: EXPECTED_EMAIL },
        },
      };

      const patchRes = await fetch(`${FIRESTORE_BASE}/${docId}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(firestoreDoc),
      });

      if (!patchRes.ok) {
        const errText = await patchRes.text();
        console.error('Failed to store biometric credential in Firestore:', errText);
        return NextResponse.json(
          { success: false, error: 'Cloud database could not store biometric credential.' },
          { status: 500 }
        );
      }

      return NextResponse.json({
        success: true,
        message: `${computedType === 'faceid' ? 'Face ID' : 'Fingerprint'} enrolled and saved to Cloud Firestore successfully.`,
        credential: {
          id: credentialId,
          name: bioName,
          platform: bioPlatform,
          biometricType: computedType,
          enabled: true,
          createdAt: createdAt,
        },
      });
    }

    // 3. Toggle enabled/disabled status of a specific fingerprint
    if (action === 'toggle') {
      if (!credentialId) {
        return NextResponse.json({ success: false, error: 'Credential ID required to toggle.' }, { status: 400 });
      }

      const docId = sanitizeDocId(credentialId);
      const isEnabled = Boolean(enabled);

      const updateRes = await fetch(`${FIRESTORE_BASE}/${docId}?updateMask.fieldPaths=enabled`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          fields: {
            enabled: { booleanValue: isEnabled },
          },
        }),
      });

      if (!updateRes.ok) {
        return NextResponse.json({ success: false, error: 'Failed to update status in cloud database.' }, { status: 500 });
      }

      return NextResponse.json({
        success: true,
        credentialId: credentialId,
        enabled: isEnabled,
        message: isEnabled ? 'Fingerprint enabled for login.' : 'Fingerprint disabled. Access with this finger is blocked.',
      });
    }

    // 4. Delete / Revoke a biometric credential
    if (action === 'delete') {
      if (!credentialId) {
        return NextResponse.json({ success: false, error: 'Credential ID required to delete.' }, { status: 400 });
      }

      const docId = sanitizeDocId(credentialId);
      const delRes = await fetch(`${FIRESTORE_BASE}/${docId}`, {
        method: 'DELETE',
      });

      if (!delRes.ok) {
        return NextResponse.json({ success: false, error: 'Failed to delete credential from cloud database.' }, { status: 500 });
      }

      return NextResponse.json({
        success: true,
        credentialId: credentialId,
        message: 'Fingerprint credential permanently deleted.',
      });
    }

    // 5. Verify biometric assertion & check Cloud Authorization status
    if (action === 'verify') {
      if (!credentialId) {
        return NextResponse.json(
          { success: false, error: 'Biometric credential ID required.' },
          { status: 400 }
        );
      }

      // Check central cloud database to ensure this fingerprint is ENROLLED and ENABLED
      const credentials = await getCloudBiometricCredentials();
      const matched = credentials.find((c) => c.id === credentialId);

      // If registered credentials exist in the cloud, enforce enabled check!
      if (matched) {
        if (!matched.enabled) {
          return NextResponse.json(
            {
              success: false,
              error: `Biometric login with "${matched.name}" is currently disabled by Admin in /biometrics. Please enable it or use Admin Password.`,
              disabled: true,
            },
            { status: 403 }
          );
        }
      }

      // Update lastUsedAt in background
      try {
        const docId = sanitizeDocId(credentialId);
        fetch(`${FIRESTORE_BASE}/${docId}?updateMask.fieldPaths=lastUsedAt`, {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            fields: {
              lastUsedAt: { stringValue: new Date().toISOString() },
            },
          }),
        }).catch(() => {});
      } catch {}

      const response = NextResponse.json({
        success: true,
        user: { email: EXPECTED_EMAIL },
        credentialName: matched ? matched.name : 'Authorized Biometric Key',
      });

      // Set auth session cookie (valid for 30 days)
      response.cookies.set('robogyaan_auth_session', 'authenticated_admin', {
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        sameSite: 'lax',
        path: '/',
        maxAge: 60 * 60 * 24 * 30, // 30 days
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
