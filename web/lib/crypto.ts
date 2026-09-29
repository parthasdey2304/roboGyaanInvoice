import { argon2id } from 'hash-wasm';

export const AUTH_SALT = process.env.NEXT_PUBLIC_AUTH_SALT || 'robogyaan_invoice_auth_salt_2026';

/**
 * Derives an Argon2id 32-byte hexadecimal hash client-side before network transmission.
 * Ensures the plaintext password is NEVER transmitted over the wire or exposed in plain form.
 */
export async function hashPasswordWithArgon2(password: string): Promise<string> {
  try {
    const hash = await argon2id({
      password,
      salt: AUTH_SALT,
      iterations: 3,
      memorySize: 4096,
      parallelism: 1,
      hashLength: 32,
      outputType: 'hex',
    });
    return hash;
  } catch (error) {
    console.error('Argon2 computation error:', error);
    throw new Error('Failed to securely hash password using Argon2id.');
  }
}
