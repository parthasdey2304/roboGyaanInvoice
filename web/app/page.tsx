'use client';

import React, { useState, useRef, useEffect } from 'react';
import { InvoiceData } from '../lib/types';
import { defaultInvoiceData } from '../lib/defaultInvoice';
import { InvoiceEditor } from '../components/InvoiceEditor';
import { InvoicePreview } from '../components/InvoicePreview';
import { NeoBrutalButton } from '../components/NeoBrutalButton';
import { NeoBrutalModal } from '../components/NeoBrutalModal';
import { HistorySidebar } from '../components/HistorySidebar';
import { AuthGate } from '../components/AuthGate';
import { InteractiveGridBackground } from '../components/InteractiveGridBackground';
import { SettingsModal, HandDrawnAsteriskSticker } from '../components/SettingsModal';
import { autoSaveInvoice, getAutosavedDraft, AUTOSAVE_DRAFT_ID } from '../lib/firebase';
import {
  Download,
  Printer,
  RotateCcw,
  Eye,
  Edit3,
  Columns,
  CheckCircle2,
  LogOut,
  ShieldCheck,
  Sun,
  Moon,
  Settings,
} from 'lucide-react';
import { toPng } from 'html-to-image';
import jsPDF from 'jspdf';

export default function InvoicePage() {
  const [invoiceData, setInvoiceData] = useState<InvoiceData>(defaultInvoiceData);
  const [activeInvoiceId, setActiveInvoiceId] = useState<string | null>(null);
  const [autosaveStatus, setAutosaveStatus] = useState<'idle' | 'saving' | 'saved'>('idle');
  const [modalConfig, setModalConfig] = useState<{
    isOpen: boolean;
    title: string;
    message: string;
    confirmText?: string;
    cancelText?: string;
    variant?: 'danger' | 'warning' | 'info';
    type?: 'confirm' | 'alert';
    onConfirm?: () => void;
  }>({
    isOpen: false,
    title: '',
    message: '',
  });
  const [activeTab, setActiveTab] = useState<'split' | 'editor' | 'preview'>('split');
  const [isExporting, setIsExporting] = useState(false);
  const [exportSuccess, setExportSuccess] = useState(false);
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [isSettingsOpen, setIsSettingsOpen] = useState(false);
  const [theme, setTheme] = useState<'light' | 'dark'>('light');
  const [backgroundStyle, setBackgroundStyle] = useState<'default' | 'grid'>('grid');
  const [previewPage, setPreviewPage] = useState<number | 'all'>('all');
  const [totalPages, setTotalPages] = useState<number>(1);
  const [isPdfExporting, setIsPdfExporting] = useState<boolean>(false);
  const previewRef = useRef<HTMLDivElement>(null);

  const isFirstRender = useRef(true);
  const saveTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  const invoiceDataRef = useRef(invoiceData);
  const activeInvoiceIdRef = useRef(activeInvoiceId);

  invoiceDataRef.current = invoiceData;
  activeInvoiceIdRef.current = activeInvoiceId;

  // Restore theme, background style & autosaved draft on mount
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

    const savedBg = localStorage.getItem('robogyaan_bg_style') as 'default' | 'grid' | null;
    if (savedBg) {
      setBackgroundStyle(savedBg);
    }

    const draft = getAutosavedDraft();
    if (draft && !activeInvoiceId) {
      setInvoiceData(draft);
      setActiveInvoiceId(AUTOSAVE_DRAFT_ID);
    }
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

  const handleBackgroundStyleChange = (style: 'default' | 'grid') => {
    setBackgroundStyle(style);
    localStorage.setItem('robogyaan_bg_style', style);
  };

  // Debounced Autosave: any change is saved to Firestore without duplicate copies
  useEffect(() => {
    if (isFirstRender.current) {
      isFirstRender.current = false;
      return;
    }

    setAutosaveStatus('saving');

    if (saveTimeoutRef.current) {
      clearTimeout(saveTimeoutRef.current);
    }

    saveTimeoutRef.current = setTimeout(async () => {
      try {
        const savedId = await autoSaveInvoice(invoiceData, activeInvoiceId);
        if (!activeInvoiceId) {
          setActiveInvoiceId(savedId);
        }
        setAutosaveStatus('saved');
        setTimeout(() => {
          setAutosaveStatus((current) => (current === 'saved' ? 'idle' : current));
        }, 2000);
      } catch (err) {
        console.error('Autosave error:', err);
        setAutosaveStatus('idle');
      }
    }, 1000);

    return () => {
      if (saveTimeoutRef.current) {
        clearTimeout(saveTimeoutRef.current);
      }
    };
  }, [invoiceData, activeInvoiceId]);

  // Flush pending changes before the tab closes or navigates away
  useEffect(() => {
    const handleBeforeUnload = () => {
      autoSaveInvoice(invoiceDataRef.current, activeInvoiceIdRef.current);
    };
    window.addEventListener('beforeunload', handleBeforeUnload);
    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
    };
  }, []);

  const handleReset = () => {
    setModalConfig({
      isOpen: true,
      title: 'Reset Template',
      message: 'Reset all fields to the default Robogyaan template values? Any unsaved edits will be cleared.',
      confirmText: 'Yes, Reset',
      cancelText: 'Cancel',
      variant: 'warning',
      type: 'confirm',
      onConfirm: () => {
        setInvoiceData(defaultInvoiceData);
        setActiveInvoiceId(null);
        if (typeof window !== 'undefined') {
          localStorage.removeItem('robogyaan_invoice_autosave_draft');
        }
        autoSaveInvoice(defaultInvoiceData, AUTOSAVE_DRAFT_ID);
      },
    });
  };

  const generatePdfInstance = async (): Promise<jsPDF | null> => {
    if (!previewRef.current) return null;
    setIsExporting(true);
    setIsPdfExporting(true);

    // Give React time to ensure all pages are in DOM for capture
    await new Promise((resolve) => setTimeout(resolve, 150));

    const container = previewRef.current;
    const pageElements = container.querySelectorAll<HTMLElement>('.print-invoice-page');

    if (!pageElements || pageElements.length === 0) {
      throw new Error('No invoice pages found to export');
    }

    const pdf = new jsPDF({
      orientation: 'portrait',
      unit: 'mm',
      format: 'a4',
      compress: true,
    });

    const pdfWidth = pdf.internal.pageSize.getWidth();
    const pdfHeight = pdf.internal.pageSize.getHeight();

    for (let i = 0; i < pageElements.length; i++) {
      const pageEl = pageElements[i];

      const originalClasses = pageEl.className;
      pageEl.classList.remove('hidden');
      pageEl.classList.add('flex');

      const dataUrl = await toPng(pageEl, {
        pixelRatio: 2.5,
        cacheBust: true,
        backgroundColor: '#FFFFFF',
        filter: (node) => {
          if (node instanceof HTMLElement && node.classList.contains('no-print')) {
            return false;
          }
          return true;
        },
      });

      pageEl.className = originalClasses;

      if (i > 0) {
        pdf.addPage('a4', 'portrait');
      }

      pdf.addImage(dataUrl, 'PNG', 0, 0, pdfWidth, pdfHeight, undefined, 'FAST');
    }

    return pdf;
  };

  const handlePrint = () => {
    window.print();
  };

  const handleDownloadPdf = async () => {
    try {
      const pdf = await generatePdfInstance();
      if (!pdf) {
        window.print();
        return;
      }
      const filename = `RoboGyaan_Invoice_${invoiceData.invoiceNo.replace(/[^a-zA-Z0-9_-]/g, '_')}.pdf`;
      pdf.save(filename);
      setExportSuccess(true);
      setTimeout(() => setExportSuccess(false), 3000);
    } catch (err) {
      console.error('PDF generation error, opening print dialog:', err);
      window.print();
    } finally {
      setIsPdfExporting(false);
      setIsExporting(false);
    }
  };

  const isDark = theme === 'dark';

  return (
    <AuthGate>
      {(user, onLogout) => (
        <main className={`min-h-screen flex flex-col relative bg-transparent ${isDark ? 'dark text-white' : 'text-black'}`}>
          {/* INTERACTIVE BACKGROUND CANVAS WITH MOUSE SWELL ANIMATION */}
          <InteractiveGridBackground
            theme={theme}
            enabled={backgroundStyle === 'grid'}
          />

          {/* TOP HEADER & ACTION BAR */}
          <header
            className={`no-print sticky top-0 z-40 border-b-[3px] px-4 sm:px-6 py-2.5 transition-colors duration-200 ${
              isDark
                ? 'bg-black text-white border-[#27272a] shadow-[0_4px_0px_0px_#18181b]'
                : 'bg-[#FFE600] text-black border-black shadow-[0_4px_0px_0px_#000000]'
            }`}
          >
            <div className="max-w-7xl mx-auto flex flex-col md:flex-row md:items-center justify-between gap-2.5 sm:gap-3">
              {/* Top Left Row on Mobile / Left Section on Desktop */}
              <div className="w-full md:w-auto flex items-center justify-between gap-3">
                {/* Top Left: Logo & Heading */}
                <div className="flex items-center gap-2.5 sm:gap-3 shrink-0">
                  <div className="w-9 h-9 sm:w-10 sm:h-10 bg-black text-[#FFE600] font-black rounded-lg flex items-center justify-center text-lg sm:text-xl border-2 border-black shadow-[2px_2px_0px_#000] shrink-0">
                    RG
                  </div>
                  <div className="text-left">
                    <h1 className="text-lg sm:text-2xl font-black tracking-tight leading-none flex items-center gap-1.5 sm:gap-2">
                      ROBOGYAAN{' '}
                      <span
                        className={`text-[10px] sm:text-xs px-1.5 sm:px-2 py-0.5 rounded font-black tracking-wider ${
                          isDark ? 'bg-[#FFE600] text-black' : 'bg-black text-white'
                        }`}
                      >
                        INVOICE
                      </span>
                    </h1>
                    <p
                      className={`text-[10px] sm:text-[11px] font-bold uppercase tracking-wider mt-0.5 ${
                        isDark ? 'text-neutral-400' : 'text-black/80'
                      }`}
                    >
                      Neo-Brutalist Live Engine
                    </p>
                  </div>
                </div>

                {/* Mobile View Toggles on Top Right (< md): ONLY ICONS VISIBLE ON PHONE SCREEN */}
                <div className="flex md:hidden items-center gap-1 bg-white dark:bg-[#18181b] p-1 rounded-lg border-2 border-black dark:border-neutral-700 shadow-[2px_2px_0px_#000] shrink-0">
                  <button
                    type="button"
                    onClick={() => setActiveTab('editor')}
                    className={`p-1.5 rounded transition-all ${
                      activeTab === 'editor'
                        ? 'bg-black text-[#FFE600] shadow-[1px_1px_0px_#000]'
                        : 'text-black dark:text-white hover:bg-neutral-100 dark:hover:bg-neutral-800'
                    }`}
                    title="Editor Mode"
                    aria-label="Editor Mode"
                  >
                    <Edit3 className="w-4 h-4" />
                  </button>
                  <button
                    type="button"
                    onClick={() => setActiveTab('split')}
                    className={`p-1.5 rounded transition-all ${
                      activeTab === 'split'
                        ? 'bg-black text-[#FFE600] shadow-[1px_1px_0px_#000]'
                        : 'text-black dark:text-white hover:bg-neutral-100 dark:hover:bg-neutral-800'
                    }`}
                    title="Split Mode"
                    aria-label="Split Mode"
                  >
                    <Columns className="w-4 h-4" />
                  </button>
                  <button
                    type="button"
                    onClick={() => setActiveTab('preview')}
                    className={`p-1.5 rounded transition-all ${
                      activeTab === 'preview'
                        ? 'bg-black text-[#FFE600] shadow-[1px_1px_0px_#000]'
                        : 'text-black dark:text-white hover:bg-neutral-100 dark:hover:bg-neutral-800'
                    }`}
                    title="Preview Mode"
                    aria-label="Preview Mode"
                  >
                    <Eye className="w-4 h-4" />
                  </button>

                  {/* Mobile Theme Toggle Button */}
                  <button
                    type="button"
                    onClick={toggleTheme}
                    className="p-1.5 rounded text-black dark:text-white hover:bg-neutral-100 dark:hover:bg-neutral-800"
                    title={isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
                  >
                    <span className="flex items-center justify-center">
                      {isDark ? (
                        <Sun className="w-4 h-4 text-[#FFE600]" />
                      ) : (
                        <Moon className="w-4 h-4 text-black" />
                      )}
                    </span>
                  </button>
                </div>
              </div>

              {/* Desktop Right Side: Tabs + Action Buttons + User Logout */}
              <div className="flex items-center justify-end gap-2.5 flex-wrap w-full md:w-auto">
                {/* Desktop Tabs on Top Right: BOTH ICONS AND TEXT VISIBLE */}
                <div className="hidden md:flex items-center gap-1 bg-white dark:bg-[#18181b] p-1 rounded-lg border-2 border-black dark:border-neutral-700 shadow-[2px_2px_0px_#000]">
                  <button
                    type="button"
                    onClick={() => setActiveTab('editor')}
                    className={`px-3 py-1.5 text-xs font-black uppercase tracking-wider rounded flex items-center gap-1.5 transition-all ${
                      activeTab === 'editor'
                        ? 'bg-black text-[#FFE600] shadow-[1px_1px_0px_#000]'
                        : 'text-black dark:text-white hover:bg-neutral-100 dark:hover:bg-neutral-800'
                    }`}
                  >
                    <Edit3 className="w-3.5 h-3.5" />
                    <span className="font-virgil font-black">EDITOR</span>
                  </button>
                  <button
                    type="button"
                    onClick={() => setActiveTab('split')}
                    className={`px-3 py-1.5 text-xs font-black uppercase tracking-wider rounded flex items-center gap-1.5 transition-all ${
                      activeTab === 'split'
                        ? 'bg-black text-[#FFE600] shadow-[1px_1px_0px_#000]'
                        : 'text-black dark:text-white hover:bg-neutral-100 dark:hover:bg-neutral-800'
                    }`}
                  >
                    <Columns className="w-3.5 h-3.5" />
                    <span className="font-virgil font-black">SPLIT</span>
                  </button>
                  <button
                    type="button"
                    onClick={() => setActiveTab('preview')}
                    className={`px-3 py-1.5 text-xs font-black uppercase tracking-wider rounded flex items-center gap-1.5 transition-all ${
                      activeTab === 'preview'
                        ? 'bg-black text-[#FFE600] shadow-[1px_1px_0px_#000]'
                        : 'text-black dark:text-white hover:bg-neutral-100 dark:hover:bg-neutral-800'
                    }`}
                  >
                    <Eye className="w-3.5 h-3.5" />
                    <span className="font-virgil font-black">PREVIEW</span>
                  </button>
                </div>

                {/* Action Buttons */}
                <div className="flex items-center gap-2 justify-end w-full sm:w-auto">
                  {/* NAVBAR LIGHT / DARK MODE TOGGLE (SAME SPAN REQUIREMENT) */}
                  <button
                    type="button"
                    onClick={toggleTheme}
                    className={`p-2 rounded-lg border-2 font-black transition active:translate-x-[1px] active:translate-y-[1px] flex items-center justify-center cursor-pointer ${
                      isDark
                        ? 'bg-[#18181b] text-[#FFE600] border-neutral-700 shadow-[2px_2px_0px_#FFE600]'
                        : 'bg-white text-black border-black shadow-[2px_2px_0px_#000]'
                    }`}
                    title={isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
                    aria-label={isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
                  >
                    <span className="flex items-center justify-center">
                      {isDark ? (
                        <Sun className="w-4 h-4 text-[#FFE600]" />
                      ) : (
                        <Moon className="w-4 h-4 text-black" />
                      )}
                    </span>
                  </button>

                  {/* Settings Button */}
                  <button
                    type="button"
                    onClick={() => setIsSettingsOpen(true)}
                    className={`p-2 rounded-lg border-2 font-black transition active:translate-x-[1px] active:translate-y-[1px] flex items-center justify-center cursor-pointer ${
                      isDark
                        ? 'bg-[#18181b] text-white border-neutral-700 shadow-[2px_2px_0px_#ffffff]'
                        : 'bg-white text-black border-black shadow-[2px_2px_0px_#000]'
                    }`}
                    title="App Settings & Updates"
                    aria-label="App Settings"
                  >
                    <Settings className="w-4 h-4" />
                  </button>

                  <NeoBrutalButton
                    variant="white"
                    size="sm"
                    onClick={handleReset}
                    icon={<RotateCcw className="w-4 h-4" />}
                    title="Reset fields to original Robogyaan invoice values"
                  >
                    Reset
                  </NeoBrutalButton>

                  <NeoBrutalButton
                    variant="white"
                    size="sm"
                    onClick={handlePrint}
                    icon={<Printer className="w-4 h-4" />}
                  >
                    Print
                  </NeoBrutalButton>

                  <NeoBrutalButton
                    variant="dark"
                    size="sm"
                    onClick={handleDownloadPdf}
                    disabled={isExporting}
                    icon={
                      exportSuccess ? (
                        <CheckCircle2 className="w-4 h-4 text-[#FFE600]" />
                      ) : (
                        <Download className="w-4 h-4" />
                      )
                    }
                  >
                    {isExporting ? 'Generating...' : exportSuccess ? 'Downloaded!' : 'Export PDF'}
                  </NeoBrutalButton>

                  {/* Admin Logout Button */}
                  <button
                    type="button"
                    onClick={() => {
                      setModalConfig({
                        isOpen: true,
                        title: 'Log Out Session',
                        message: `Are you sure you want to log out of the admin session (${user.email})?`,
                        confirmText: 'Yes, Log Out',
                        cancelText: 'Stay Logged In',
                        variant: 'danger',
                        type: 'confirm',
                        onConfirm: onLogout,
                      });
                    }}
                    title={`Logged in as ${user.email}. Click to Logout`}
                    className="p-2 bg-white text-black font-black border-2 border-black rounded-lg shadow-[2px_2px_0px_#000] hover:bg-red-50 hover:text-red-700 transition active:translate-x-[1px] active:translate-y-[1px] flex items-center gap-1 text-xs cursor-pointer"
                  >
                    <LogOut className="w-3.5 h-3.5" />
                    <span className="hidden sm:inline font-bold">Logout</span>
                  </button>
                </div>
              </div>
            </div>
          </header>

          {/* 3-DASH SIDEBAR TOGGLE ON THE LEFT TOP BELOW NAVBAR & SETTINGS STICKER */}
          <div className="no-print max-w-7xl mx-auto w-full px-4 sm:px-6 pt-3 pb-0 flex items-center justify-between flex-wrap gap-2 relative z-10">
            <div className="flex items-center gap-2.5 flex-wrap">
              <button
                type="button"
                onClick={() => setIsSidebarOpen(true)}
                className={`flex items-center gap-2 px-3 py-1.5 font-black text-xs uppercase tracking-wider border-2 border-black rounded-lg shadow-[3px_3px_0px_#000000] hover:bg-[#FFE600] hover:text-black transition active:translate-x-[1px] active:translate-y-[1px] cursor-pointer ${
                  isDark ? 'bg-[#18181b] text-white border-neutral-700' : 'bg-white text-black'
                }`}
                title="Open Prompt History Sidebar (Firebase Firestore)"
                aria-label="Open Prompt History"
              >
                {/* 3 horizontal dashes icon */}
                <div className="flex flex-col gap-1 w-4 justify-center">
                  <span className="block h-0.5 w-full bg-current rounded" />
                  <span className="block h-0.5 w-full bg-current rounded" />
                  <span className="block h-0.5 w-full bg-current rounded" />
                </div>
                <span className="font-virgil font-black">History</span>
              </button>

              {activeInvoiceId && (
                <div className="flex items-center gap-1.5 bg-[#FFFDE6] dark:bg-[#27272a] text-black dark:text-white border-2 border-black dark:border-neutral-700 px-2.5 py-1 rounded-lg text-xs font-bold shadow-[2px_2px_0px_#000]">
                  <span className="w-2 h-2 rounded-full bg-emerald-500 animate-ping" />
                  <span>Editing Loaded Invoice:</span>
                  <span className="font-black underline">{invoiceData.invoiceNo}</span>
                </div>
              )}
            </div>

            {/* GREEN SAVING INDICATOR AS ANNOTATED BY USER IN MEDIA */}
            <div className="flex items-center justify-center min-h-[28px] px-2">
              {autosaveStatus === 'saving' && (
                <span className="font-virgil font-black text-[#16a34a] text-base sm:text-lg tracking-wider animate-pulse flex items-center gap-1">
                  Saving....
                </span>
              )}
              {autosaveStatus === 'saved' && (
                <span className="font-virgil font-black text-[#16a34a]/85 text-xs sm:text-sm tracking-wider flex items-center gap-1">
                  ✓ Saved
                </span>
              )}
            </div>

            {/* User status & Hand-Drawn Asterisk/Splat sticker matching Image 2 */}
            <div className="hidden sm:flex items-center gap-2 text-[11px] font-bold text-neutral-500 dark:text-neutral-400">
              <div className="flex items-center gap-1">
                <ShieldCheck className="w-3.5 h-3.5 text-emerald-600" />
                <span>{user.email}</span>
              </div>

              {/* Hand-Drawn Asterisk Sticker from Image 2 */}
              <HandDrawnAsteriskSticker
                className="w-5 h-5 ml-1"
                onClick={() => setIsSettingsOpen(true)}
                title="App Settings & Updates (Click to open)"
              />
            </div>
          </div>

          {/* MAIN CONTENT AREA */}
          <div className="flex-1 max-w-7xl mx-auto w-full p-4 sm:p-6 print:p-0 print:m-0 print:max-w-none relative z-10">
            <div
              className={`grid gap-8 print:block print:p-0 print:m-0 ${
                activeTab === 'split'
                  ? 'grid-cols-1 lg:grid-cols-12'
                  : 'grid-cols-1'
              }`}
            >
              {/* LEFT PANEL: FORM INPUTS & CONTROLS */}
              {(activeTab === 'split' || activeTab === 'editor') && (
                <div
                  className={`no-print ${
                    activeTab === 'split' ? 'lg:col-span-5 xl:col-span-5' : 'max-w-3xl mx-auto w-full'
                  }`}
                >
                  <div className="mb-4">
                    <h2 className="text-xl font-black">{isDark ? 'Invoice Editor' : 'Invoice Editor'}</h2>
                    <p className="text-xs text-neutral-500 dark:text-neutral-400 font-medium">
                      Update quantities, rates, and client details with instant real-time calculation.
                    </p>
                  </div>

                  <InvoiceEditor
                    data={invoiceData}
                    onChange={setInvoiceData}
                    onReset={handleReset}
                  />
                </div>
              )}

              {/* RIGHT PANEL: STICKY LIVE A4 PREVIEW (ROUNDED BORDERS AS PER IMAGE 3) */}
              {(activeTab === 'split' || activeTab === 'preview') && (
                <div
                  className={`${
                    activeTab === 'split'
                      ? 'lg:col-span-7 xl:col-span-7'
                      : 'max-w-4xl mx-auto w-full'
                  }`}
                >
                  <div className="no-print flex items-center justify-between mb-4 flex-wrap gap-3">
                    <div>
                      <h2 className="text-xl font-black flex items-center gap-2">
                        Live A4 Preview
                        <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse inline-block" />
                      </h2>
                      <p className="text-xs text-neutral-500 dark:text-neutral-400 font-medium">
                        Multi-page A4 layout &bull; Dynamic page breaks with authentic Robogyaan fidelity.
                      </p>
                    </div>

                    {/* Page Selector Dropdown */}
                    <div className="flex items-center gap-2 bg-white dark:bg-[#18181b] border-2 border-black dark:border-neutral-700 rounded-lg px-3 py-1 shadow-[2px_2px_0px_#000000]">
                      <label htmlFor="preview-page-select" className="text-xs font-black uppercase text-black dark:text-white shrink-0">
                        Page:
                      </label>
                      <select
                        id="preview-page-select"
                        value={previewPage}
                        onChange={(e) => {
                          const val = e.target.value;
                          setPreviewPage(val === 'all' ? 'all' : parseInt(val, 10));
                        }}
                        className="bg-transparent text-xs font-black text-black dark:text-white py-0.5 pr-2 focus:outline-none cursor-pointer"
                      >
                        <option value="all" className="bg-white dark:bg-[#18181b] text-black dark:text-white">
                          All Pages ({totalPages})
                        </option>
                        {Array.from({ length: totalPages }, (_, i) => (
                          <option key={i} value={i} className="bg-white dark:bg-[#18181b] text-black dark:text-white">
                            Page {i + 1} of {totalPages}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>

                  <div className="lg:sticky lg:top-24 overflow-x-auto pb-8 print:p-0 print:m-0 print:overflow-visible print:static">
                    <InvoicePreview
                      ref={previewRef}
                      data={invoiceData}
                      selectedPage={isPdfExporting ? 'all' : previewPage}
                      onTotalPagesChange={(count) => {
                        setTotalPages(count);
                        if (typeof previewPage === 'number' && previewPage >= count) {
                          setPreviewPage('all');
                        }
                      }}
                      forceShowAllForExport={isPdfExporting}
                    />
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* FOOTER */}
          <footer className="no-print mt-auto border-t-2 border-black dark:border-neutral-800 bg-white dark:bg-[#121212] py-4 px-6 text-center text-xs font-bold text-neutral-600 dark:text-neutral-400 relative z-10">
            Robogyaan Dual-Platform Invoice Suite &bull; Neo-Brutalist Edition &bull; IGNITING CURIOSITY, BUILDING FUTURE
          </footer>

          {/* FIRESTORE PROMPT HISTORY SIDEBAR */}
          <HistorySidebar
            isOpen={isSidebarOpen}
            onClose={() => setIsSidebarOpen(false)}
            currentInvoiceData={invoiceData}
            onLoadInvoice={(loadedData, loadedId) => {
              setInvoiceData(loadedData);
              setActiveInvoiceId(loadedId || null);
            }}
            activeInvoiceId={activeInvoiceId}
          />

          {/* APP SETTINGS & IN-APP UPDATER MODAL */}
          <SettingsModal
            isOpen={isSettingsOpen}
            onClose={() => setIsSettingsOpen(false)}
            theme={theme}
            onThemeChange={(nextTheme) => {
              setTheme(nextTheme);
              localStorage.setItem('robogyaan_theme', nextTheme);
              if (nextTheme === 'dark') {
                document.documentElement.classList.add('dark');
              } else {
                document.documentElement.classList.remove('dark');
              }
            }}
            backgroundStyle={backgroundStyle}
            onBackgroundStyleChange={handleBackgroundStyleChange}
          />

          {/* NEO-BRUTALIST MODAL / ALERT DIALOG */}
          <NeoBrutalModal
            isOpen={modalConfig.isOpen}
            onClose={() => setModalConfig((prev) => ({ ...prev, isOpen: false }))}
            onConfirm={modalConfig.onConfirm}
            title={modalConfig.title}
            message={modalConfig.message}
            confirmText={modalConfig.confirmText}
            cancelText={modalConfig.cancelText}
            variant={modalConfig.variant}
            type={modalConfig.type}
          />
        </main>
      )}
    </AuthGate>
  );
}
