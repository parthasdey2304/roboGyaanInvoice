'use client';

import React, { useState, useEffect } from 'react';
import { hashPasswordWithArgon2 } from '../lib/crypto';
import { Shield, Lock, Mail, ArrowRight, Loader2, KeyRound, Sun, Moon } from 'lucide-react';
import { InteractiveGridBackground } from './InteractiveGridBackground';

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

  // Load theme and check existing session on load
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

  const handleLogin = async (e: React.FormEvent) => {
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

  const handleLogout = async () => {
    try {
      await fetch('/api/auth/logout', { method: 'POST' });
    } catch (e) {
      console.error('Logout error:', e);
    }
    localStorage.removeItem('robogyaan_authenticated_admin');
    setIsAuthenticated(false);
    setPasswordInput('');
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
          <p className={`text-xs font-black font-virgil uppercase tracking-wider ${theme === 'dark' ? 'text-white' : 'text-black'}`}>
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

  // Otherwise, render Neo-Brutal Login Portal with Interactive Grid Background (as per Image 1)
  return (
    <div className={`min-h-screen flex flex-col justify-center items-center p-4 sm:p-6 relative select-none ${isDark ? 'text-white' : 'text-black'}`}>
      {/* 3D Interactive Grid Background with Cursor Swelling Effect */}
      <InteractiveGridBackground theme={theme} enabled={true} />

      {/* Top Banner & Theme Switcher */}
      <div className="max-w-md w-full mb-4 flex items-center justify-between gap-2 relative z-10">
        <div className="inline-flex items-center gap-2 bg-[#FFE600] text-black border-2 border-black px-3 py-1 rounded-full shadow-[2px_2px_0px_#000]">
          <Shield className="w-4 h-4 text-black" />
          <span className="text-[11px] font-black uppercase tracking-wider">
            Secured by Argon2id &bull; Firebase Cloud
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
            {isDark ? (
              <Sun className="w-4 h-4 text-[#FFE600]" />
            ) : (
              <Moon className="w-4 h-4 text-[#FFE600]" />
            )}
          </span>
        </button>
      </div>

      <div
        className={`max-w-md w-full border-[3.5px] border-black rounded-xl p-6 sm:p-8 shadow-[8px_8px_0px_#000000] relative z-10 ${
          isDark ? 'bg-[#18181b] border-white/80' : 'bg-white'
        }`}
      >
        {/* Portal Header */}
        <div className="flex items-center gap-3 mb-6 pb-4 border-b-2 border-black dark:border-neutral-700">
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

        {/* Security Notice Banner */}
        <div
          className={`mb-5 border-2 border-black rounded-lg p-3 shadow-[2px_2px_0px_#000] ${
            isDark ? 'bg-[#27272a]' : 'bg-[#FFFDE6]'
          }`}
        >
          <div className="flex items-start gap-2">
            <KeyRound className="w-4 h-4 text-[#FFE600] shrink-0 mt-0.5" />
            <p className="text-[11px] font-bold leading-tight">
              Password is automatically encrypted client-side using <span className="underline font-black text-[#FFE600]">Argon2id</span> before sending over the wire. Plaintext is never transmitted.
            </p>
          </div>
        </div>

        {/* Error message banner */}
        {error && (
          <div className="mb-4 bg-red-100 dark:bg-red-950/60 border-2 border-red-500 rounded-lg p-3 text-red-900 dark:text-red-200 text-xs font-bold shadow-[2px_2px_0px_#dc2626]">
            {error}
          </div>
        )}

        {/* Login Form */}
        <form onSubmit={handleLogin} className="space-y-4">
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
                <span className="text-black">{hashingProgress ? 'Computing Argon2 Hash...' : 'Authenticating...'}</span>
              </>
            ) : (
              <>
                <span className="text-black">Sign In to Invoice Suite</span>
                <ArrowRight className="w-4 h-4 text-black" />
              </>
            )}
          </button>
        </form>

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
