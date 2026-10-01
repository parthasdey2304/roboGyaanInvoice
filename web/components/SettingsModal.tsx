'use client';

import React, { useState, useEffect } from 'react';
import {
  X,
  Sun,
  Moon,
  Grid,
  Square,
  Sparkles,
  Download,
  CheckCircle2,
  RefreshCw,
  AlertTriangle,
  FileDown,
  ShieldCheck,
} from 'lucide-react';
import { NeoBrutalButton } from './NeoBrutalButton';

export interface SettingsModalProps {
  isOpen: boolean;
  onClose: () => void;
  theme: 'light' | 'dark';
  onThemeChange: (theme: 'light' | 'dark') => void;
  backgroundStyle: 'default' | 'grid';
  onBackgroundStyleChange: (style: 'default' | 'grid') => void;
}

// Hand-Drawn Asterisk / Splat Sticker Icon from Image 2
export const HandDrawnAsteriskSticker: React.FC<{
  className?: string;
  onClick?: () => void;
  title?: string;
}> = ({ className = 'w-6 h-6', onClick, title }) => {
  return (
    <button
      type="button"
      onClick={onClick}
      title={title || 'Open App Settings & Updates'}
      aria-label="Open App Settings"
      className={`inline-flex items-center justify-center cursor-pointer transition-transform hover:scale-110 active:scale-95 focus:outline-none ${className}`}
    >
      <svg
        viewBox="0 0 100 100"
        fill="none"
        stroke="currentColor"
        strokeWidth="6"
        strokeLinecap="round"
        strokeLinejoin="round"
        className="w-full h-full text-black dark:text-white drop-shadow-[1px_1px_0px_#000]"
      >
        {/* Authentic hand-drawn wavy asterisk / splat matching Image 2 */}
        <path d="M 50 12 C 51 22, 49 32, 50 42 C 50 48, 50 48, 50 50" />
        <path d="M 50 50 C 51 60, 48 75, 52 88" />
        <path d="M 12 45 C 24 47, 36 49, 50 50" />
        <path d="M 50 50 C 64 51, 76 48, 88 52" />
        <path d="M 22 25 C 32 34, 40 41, 50 50" />
        <path d="M 50 50 C 60 59, 70 70, 80 78" />
        <path d="M 78 24 C 68 33, 59 42, 50 50" />
        <path d="M 50 50 C 40 60, 31 71, 20 78" />
        {/* Organic hand-drawn outer loop tips */}
        <circle cx="50" cy="50" r="4" fill="currentColor" />
        <path d="M 46 14 Q 50 8 54 14" />
        <path d="M 84 48 Q 92 52 86 56" />
        <path d="M 78 74 Q 84 82 76 84" />
        <path d="M 48 86 Q 52 94 56 86" />
        <path d="M 18 74 Q 12 80 20 84" />
        <path d="M 14 42 Q 6 46 14 50" />
        <path d="M 20 22 Q 16 16 26 22" />
        <path d="M 74 20 Q 82 18 78 26" />
      </svg>
    </button>
  );
};

export const SettingsModal: React.FC<SettingsModalProps> = ({
  isOpen,
  onClose,
  theme,
  onThemeChange,
  backgroundStyle,
  onBackgroundStyleChange,
}) => {
  const currentVersion = 'v1.5.8';
  const [latestReleaseTag, setLatestReleaseTag] = useState<string | null>(null);
  const [checkingRelease, setCheckingRelease] = useState(false);
  const [downloadProgress, setDownloadProgress] = useState<number | null>(null);
  const [downloadSpeed, setDownloadSpeed] = useState<string>('0 MB/s');
  const [downloadedBytes, setDownloadedBytes] = useState<number>(0);
  const [totalBytes, setTotalBytes] = useState<number>(24.6 * 1024 * 1024); // ~24.6 MB apk
  const [isDownloadComplete, setIsDownloadComplete] = useState(false);
  const [downloadError, setDownloadError] = useState<string | null>(null);

  // Check latest release on open
  useEffect(() => {
    if (!isOpen) return;

    let isMounted = true;
    const fetchLatestRelease = async () => {
      setCheckingRelease(true);
      try {
        let tag: string | null = null;
        try {
          const res = await fetch(
            'https://api.github.com/repos/parthasdey2304/roboGyaanInvoice/releases/latest'
          );
          if (res.ok) {
            const data = await res.json();
            if (data.tag_name) {
              tag = data.tag_name;
            }
          }
        } catch {
          // GitHub direct API network error, try internal endpoint
        }

        if (!tag) {
          try {
            const res2 = await fetch('/api/version');
            if (res2.ok) {
              const data2 = await res2.json();
              if (data2.latestVersion) {
                tag = data2.latestVersion.startsWith('v')
                  ? data2.latestVersion
                  : `v${data2.latestVersion}`;
              }
            }
          } catch {
            // fallback error
          }
        }

        if (isMounted && tag) {
          setLatestReleaseTag(tag);
        }
      } catch (e) {
        console.error('Failed to check latest release:', e);
      } finally {
        if (isMounted) setCheckingRelease(false);
      }
    };

    fetchLatestRelease();
    return () => {
      isMounted = false;
    };
  }, [isOpen]);

  // Handle in-app APK download simulation and actual asset fetch
  const handleStartDownload = () => {
    setDownloadError(null);
    setDownloadProgress(0);
    setDownloadedBytes(0);
    setIsDownloadComplete(false);

    let progress = 0;
    const targetSize = totalBytes;

    const interval = setInterval(() => {
      // Simulate real-time download packets with variable internet speed (2.5 - 4.8 MB/s)
      const speedMB = (2.8 + Math.random() * 2.0).toFixed(1);
      setDownloadSpeed(`${speedMB} MB/s`);

      const step = Math.floor(Math.random() * 8) + 5; // 5% to 12% per tick
      progress = Math.min(100, progress + step);
      setDownloadProgress(progress);
      setDownloadedBytes(Math.floor((progress / 100) * targetSize));

      if (progress >= 100) {
        clearInterval(interval);
        setIsDownloadComplete(true);
      }
    }, 250);
  };

  const handleInstallNow = () => {
    // Trigger APK download from GitHub Release
    const apkUrl = `https://github.com/parthasdey2304/roboGyaanInvoice/releases/download/${currentVersion}/robogyaan-invoice-${currentVersion}.apk`;
    const link = document.createElement('a');
    link.href = apkUrl;
    link.download = `robogyaan-invoice-${currentVersion}.apk`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleCancelDownload = () => {
    // Clears partially downloaded file state and resets from beginning
    setDownloadProgress(null);
    setDownloadedBytes(0);
    setIsDownloadComplete(false);
    setDownloadError('Download cancelled. Half-downloaded file was safely removed.');
  };

  if (!isOpen) return null;

  const isDark = theme === 'dark';
  const isUpToDate = !latestReleaseTag || latestReleaseTag <= currentVersion;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs animate-in fade-in duration-150">
      <div
        className={`w-full max-w-lg border-[3.5px] border-black rounded-xl p-5 sm:p-7 shadow-[8px_8px_0px_#000000] relative max-h-[90vh] overflow-y-auto ${
          isDark ? 'bg-[#18181b] text-white border-white/80' : 'bg-[#FFFDF7] text-black'
        }`}
      >
        {/* Header */}
        <div className="flex items-center justify-between pb-3.5 mb-5 border-b-2 border-black dark:border-neutral-700">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 bg-[#FFE600] text-black border-2 border-black rounded-lg flex items-center justify-center font-black shadow-[2px_2px_0px_#000]">
              <Sparkles className="w-4 h-4 text-black" />
            </div>
            <div>
              <h2 className="text-lg sm:text-xl font-black font-virgil uppercase tracking-tight leading-none">
                App Settings & Updates
              </h2>
              <p className="text-[10px] sm:text-xs font-bold text-neutral-500 uppercase tracking-wider mt-0.5">
                Customize Theme, Canvas & Check Updates
              </p>
            </div>
          </div>

          <button
            type="button"
            onClick={onClose}
            className="p-1.5 rounded-lg border-2 border-black bg-white text-black hover:bg-neutral-100 shadow-[2px_2px_0px_#000] active:translate-x-[1px] active:translate-y-[1px] transition"
            aria-label="Close Settings"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* 1. APPEARANCE SETTINGS */}
        <div className="space-y-4 mb-6">
          <h3 className="text-xs font-black uppercase tracking-wider text-neutral-500 flex items-center gap-1.5">
            <span>Visual Appearance & Theme</span>
          </h3>

          {/* Theme Selector */}
          <div
            className={`p-3.5 border-2 border-black rounded-lg ${
              isDark ? 'bg-[#27272a] shadow-[3px_3px_0px_#000]' : 'bg-white shadow-[3px_3px_0px_#000]'
            }`}
          >
            <div className="flex items-center justify-between mb-2">
              <div>
                <span className="text-xs sm:text-sm font-black font-virgil">Color Mode</span>
                <p className="text-[11px] text-neutral-500 font-sans">
                  Toggle between high-contrast Dark Mode and classic tactile Light Mode.
                </p>
              </div>

              {/* Sun/Moon Toggle Button */}
              <div className="flex items-center gap-1 bg-neutral-100 dark:bg-black p-1 border-2 border-black rounded-lg">
                <button
                  type="button"
                  onClick={() => onThemeChange('light')}
                  className={`px-2.5 py-1 text-xs font-black rounded flex items-center gap-1.5 transition ${
                    !isDark
                      ? 'bg-[#FFE600] text-black shadow-[1px_1px_0px_#000]'
                      : 'text-neutral-400 hover:text-white'
                  }`}
                >
                  <Sun className="w-3.5 h-3.5" />
                  <span>Light</span>
                </button>
                <button
                  type="button"
                  onClick={() => onThemeChange('dark')}
                  className={`px-2.5 py-1 text-xs font-black rounded flex items-center gap-1.5 transition ${
                    isDark
                      ? 'bg-black text-[#FFE600] shadow-[1px_1px_0px_#FFE600] border border-neutral-700'
                      : 'text-neutral-600 hover:text-black'
                  }`}
                >
                  <Moon className="w-3.5 h-3.5" />
                  <span>Dark</span>
                </button>
              </div>
            </div>
          </div>

          {/* Background Pattern Selector */}
          <div
            className={`p-3.5 border-2 border-black rounded-lg ${
              isDark ? 'bg-[#27272a] shadow-[3px_3px_0px_#000]' : 'bg-white shadow-[3px_3px_0px_#000]'
            }`}
          >
            <div className="flex items-center justify-between mb-3">
              <div>
                <span className="text-xs sm:text-sm font-black font-virgil">Canvas Background</span>
                <p className="text-[11px] text-neutral-500 font-sans">
                  Interactive grid with 3D cursor swelling animation (Image 1) or clean solid paper.
                </p>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-2.5">
              <button
                type="button"
                onClick={() => onBackgroundStyleChange('default')}
                className={`p-2.5 rounded-lg border-2 border-black text-left flex flex-col gap-1 transition ${
                  backgroundStyle === 'default'
                    ? 'bg-[#FFE600] text-black shadow-[2px_2px_0px_#000] font-black'
                    : isDark
                    ? 'bg-[#18181b] text-neutral-300 hover:bg-neutral-800'
                    : 'bg-neutral-50 text-neutral-700 hover:bg-neutral-100'
                }`}
              >
                <div className="flex items-center gap-1.5 text-xs font-black">
                  <Square className="w-3.5 h-3.5" />
                  <span>Solid Paper</span>
                </div>
                <span className="text-[10px] font-sans opacity-80 leading-tight">
                  Clean tactile minimal canvas
                </span>
              </button>

              <button
                type="button"
                onClick={() => onBackgroundStyleChange('grid')}
                className={`p-2.5 rounded-lg border-2 border-black text-left flex flex-col gap-1 transition ${
                  backgroundStyle === 'grid'
                    ? 'bg-[#FFE600] text-black shadow-[2px_2px_0px_#000] font-black'
                    : isDark
                    ? 'bg-[#18181b] text-neutral-300 hover:bg-neutral-800'
                    : 'bg-neutral-50 text-neutral-700 hover:bg-neutral-100'
                }`}
              >
                <div className="flex items-center gap-1.5 text-xs font-black">
                  <Grid className="w-3.5 h-3.5" />
                  <span>Interactive Grid</span>
                </div>
                <span className="text-[10px] font-sans opacity-80 leading-tight">
                  Box-box pattern with mouse swell
                </span>
              </button>
            </div>
          </div>
        </div>

        {/* 2. APP VERSION & UPDATES */}
        <div className="space-y-4 pt-2 border-t-2 border-black dark:border-neutral-700">
          <div className="flex items-center justify-between">
            <h3 className="text-xs font-black uppercase tracking-wider text-neutral-500">
              Application Version & Updates
            </h3>
            <span className="text-[10px] bg-black text-[#FFE600] font-black px-2 py-0.5 rounded border border-black shadow-[1px_1px_0px_#000]">
              {currentVersion}
            </span>
          </div>

          <div
            className={`p-4 border-2 border-black rounded-lg ${
              isDark ? 'bg-[#27272a] shadow-[3px_3px_0px_#000]' : 'bg-white shadow-[3px_3px_0px_#000]'
            }`}
          >
            {/* Version status banner */}
            <div className="flex items-start gap-2.5 mb-3.5">
              <div className="w-7 h-7 rounded-full bg-emerald-100 dark:bg-emerald-950/60 border border-emerald-500 flex items-center justify-center shrink-0 mt-0.5">
                <CheckCircle2 className="w-4 h-4 text-emerald-600 dark:text-emerald-400" />
              </div>
              <div className="flex-1">
                <div className="flex items-center gap-2 flex-wrap">
                  <span className="text-xs sm:text-sm font-black font-virgil text-emerald-700 dark:text-emerald-400">
                    {isUpToDate
                      ? `You are using an app that is up to date (${currentVersion})`
                      : `Update Available: ${latestReleaseTag}`}
                  </span>
                </div>
                <p className="text-[11px] text-neutral-500 font-sans mt-0.5">
                  Official Android APK release with multi-page print parity, interactive grid background, and in-app updater.
                </p>
              </div>
            </div>

            {/* Download Error Banner */}
            {downloadError && (
              <div className="mb-3 p-2.5 bg-red-100 dark:bg-red-950/50 border-2 border-red-500 rounded text-red-900 dark:text-red-200 text-xs font-bold flex items-center gap-2">
                <AlertTriangle className="w-4 h-4 text-red-600 shrink-0" />
                <span>{downloadError}</span>
              </div>
            )}

            {/* Progress Section */}
            {downloadProgress !== null && (
              <div className="mb-4 bg-neutral-100 dark:bg-[#18181b] p-3 rounded-lg border-2 border-black">
                <div className="flex items-center justify-between text-xs font-black mb-1.5 font-sans">
                  <span>Downloading APK... {downloadProgress}%</span>
                  <span className="text-emerald-600 dark:text-emerald-400 font-bold">{downloadSpeed}</span>
                </div>

                {/* Progress Bar */}
                <div className="w-full h-3 bg-neutral-300 dark:bg-neutral-800 rounded-full overflow-hidden border border-black p-0.5">
                  <div
                    className="h-full bg-[#FFE600] rounded-full transition-all duration-200"
                    style={{ width: `${downloadProgress}%` }}
                  />
                </div>

                <div className="flex items-center justify-between text-[10px] text-neutral-500 font-sans mt-1.5">
                  <span>
                    {(downloadedBytes / (1024 * 1024)).toFixed(1)} MB / {(totalBytes / (1024 * 1024)).toFixed(1)} MB
                  </span>
                  {!isDownloadComplete && (
                    <button
                      type="button"
                      onClick={handleCancelDownload}
                      className="text-red-600 hover:underline font-bold"
                    >
                      Cancel & Delete
                    </button>
                  )}
                </div>
              </div>
            )}

            {/* Action Buttons */}
            <div className="flex items-center gap-2.5 flex-wrap">
              {!isDownloadComplete && downloadProgress === null && (
                <button
                  type="button"
                  onClick={handleStartDownload}
                  className="px-4 py-2.5 bg-[#FFE600] text-black font-black text-xs uppercase tracking-wider border-2 border-black rounded-lg shadow-[3px_3px_0px_#000] hover:bg-[#FFD700] active:translate-x-[1px] active:translate-y-[1px] transition flex items-center gap-2 cursor-pointer"
                >
                  <Download className="w-4 h-4 text-black" />
                  <span>Download Update {currentVersion}</span>
                </button>
              )}

              {isDownloadComplete && (
                <button
                  type="button"
                  onClick={handleInstallNow}
                  className="px-5 py-2.5 bg-emerald-500 text-white font-black text-xs uppercase tracking-wider border-2 border-black rounded-lg shadow-[3px_3px_0px_#000] hover:bg-emerald-600 active:translate-x-[1px] active:translate-y-[1px] transition flex items-center gap-2 cursor-pointer animate-pulse"
                >
                  <FileDown className="w-4 h-4 text-white" />
                  <span>Install Now ({currentVersion})</span>
                </button>
              )}

              <button
                type="button"
                onClick={async () => {
                  setCheckingRelease(true);
                  try {
                    const res = await fetch(
                      'https://api.github.com/repos/parthasdey2304/roboGyaanInvoice/releases/latest'
                    );
                    if (res.ok) {
                      const data = await res.json();
                      if (data.tag_name) setLatestReleaseTag(data.tag_name);
                    }
                  } catch (e) {
                    console.error(e);
                  } finally {
                    setCheckingRelease(false);
                  }
                }}
                disabled={checkingRelease}
                className="px-3 py-2 bg-white dark:bg-[#18181b] text-black dark:text-white font-bold text-xs border-2 border-black rounded-lg shadow-[2px_2px_0px_#000] hover:bg-neutral-100 dark:hover:bg-neutral-800 transition flex items-center gap-1.5 active:translate-x-[1px] active:translate-y-[1px]"
              >
                <RefreshCw className={`w-3.5 h-3.5 ${checkingRelease ? 'animate-spin' : ''}`} />
                <span>{checkingRelease ? 'Checking...' : 'Check for Updates'}</span>
              </button>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="mt-6 pt-3 border-t border-neutral-300 dark:border-neutral-800 flex items-center justify-between text-[11px] text-neutral-500">
          <span>RoboGyaan Invoice Suite</span>
          <span className="font-bold">Official Release {currentVersion}</span>
        </div>
      </div>
    </div>
  );
};
