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
import { autoSaveInvoice, getAutosavedDraft, AUTOSAVE_DRAFT_ID } from '../lib/firebase';
import { Download, Printer, RotateCcw, Eye, Edit3, Columns, CheckCircle2, LogOut, ShieldCheck } from 'lucide-react';
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

  // Restore autosaved draft on mount if available
  useEffect(() => {
    const draft = getAutosavedDraft();
    if (draft && !activeInvoiceId) {
      setInvoiceData(draft);
      setActiveInvoiceId(AUTOSAVE_DRAFT_ID);
    }
  }, []);

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

  const handlePrint = () => {
    window.print();
  };

  const handleDownloadPdf = async () => {
    if (!previewRef.current) return;
    try {
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
      });

      const pdfWidth = pdf.internal.pageSize.getWidth();
      const pdfHeight = pdf.internal.pageSize.getHeight();

      for (let i = 0; i < pageElements.length; i++) {
        const pageEl = pageElements[i];

        // Temporarily ensure element is displayed during capture
        const prevDisplay = pageEl.style.display;
        pageEl.style.display = 'flex';

        const dataUrl = await toPng(pageEl, {
          quality: 0.98,
          pixelRatio: 2.5,
          backgroundColor: '#FFFFFF',
          cacheBust: true,
        });

        pageEl.style.display = prevDisplay;

        if (i > 0) {
          pdf.addPage('a4', 'portrait');
        }

        pdf.addImage(dataUrl, 'PNG', 0, 0, pdfWidth, pdfHeight);
      }

      pdf.save(`Invoice_${invoiceData.invoiceNo.replace(/[^a-zA-Z0-9_-]/g, '_')}.pdf`);

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

  return (
    <AuthGate>
      {(user, onLogout) => (
        <main className="min-h-screen flex flex-col bg-[#FDFBF7]">
          {/* TOP HEADER & ACTION BAR */}
          <header className="no-print sticky top-0 z-50 bg-[#FFE600] border-b-[3px] border-black px-4 sm:px-6 py-2.5 shadow-[0_4px_0px_0px_#000000]">
            <div className="max-w-7xl mx-auto flex flex-col md:flex-row md:items-center justify-between gap-2.5 sm:gap-3">
              {/* Top Left Row on Mobile / Left Section on Desktop */}
              <div className="w-full md:w-auto flex items-center justify-between gap-3">
                {/* Top Left: Logo & Heading */}
                <div className="flex items-center gap-2.5 sm:gap-3 shrink-0">
                  <div className="w-9 h-9 sm:w-10 sm:h-10 bg-black text-[#FFE600] font-black rounded-lg flex items-center justify-center text-lg sm:text-xl border-2 border-black shadow-[2px_2px_0px_#000] shrink-0">
                    RG
                  </div>
                  <div className="text-left">
                    <h1 className="text-lg sm:text-2xl font-black tracking-tight leading-none text-black flex items-center gap-1.5 sm:gap-2">
                      ROBOGYAAN <span className="text-[10px] sm:text-xs bg-black text-white px-1.5 sm:px-2 py-0.5 rounded font-black tracking-wider">INVOICE</span>
                    </h1>
                    <p className="text-[10px] sm:text-[11px] font-bold text-black/80 uppercase tracking-wider mt-0.5">
                      Neo-Brutalist Live Engine
                    </p>
                  </div>
                </div>

                {/* Mobile View Toggles on Top Right (< md): ONLY ICONS VISIBLE ON PHONE SCREEN */}
                <div className="flex md:hidden items-center gap-1 bg-white p-1 rounded-lg border-2 border-black shadow-[2px_2px_0px_#000] shrink-0">
                  <button
                    type="button"
                    onClick={() => setActiveTab('editor')}
                    className={`p-1.5 rounded transition-all ${
                      activeTab === 'editor'
                        ? 'bg-black text-[#FFE600] shadow-[1px_1px_0px_#000]'
                        : 'text-black hover:bg-neutral-100'
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
                        : 'text-black hover:bg-neutral-100'
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
                        : 'text-black hover:bg-neutral-100'
                    }`}
                    title="Preview Mode"
                    aria-label="Preview Mode"
                  >
                    <Eye className="w-4 h-4" />
                  </button>
                </div>
              </div>

              {/* Desktop Right Side: Tabs + Action Buttons + User Logout */}
              <div className="flex items-center justify-end gap-2.5 flex-wrap w-full md:w-auto">
                {/* Desktop Tabs on Top Right: BOTH ICONS AND TEXT VISIBLE */}
                <div className="hidden md:flex items-center gap-1 bg-white p-1 rounded-lg border-2 border-black shadow-[2px_2px_0px_#000]">
                  <button
                    type="button"
                    onClick={() => setActiveTab('editor')}
                    className={`px-3 py-1.5 text-xs font-black uppercase tracking-wider rounded flex items-center gap-1.5 transition-all ${
                      activeTab === 'editor'
                        ? 'bg-black text-[#FFE600] shadow-[1px_1px_0px_#000]'
                        : 'text-black hover:bg-neutral-100'
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
                        : 'text-black hover:bg-neutral-100'
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
                        : 'text-black hover:bg-neutral-100'
                    }`}
                  >
                    <Eye className="w-3.5 h-3.5" />
                    <span className="font-virgil font-black">PREVIEW</span>
                  </button>
                </div>

                {/* Action Buttons */}
                <div className="flex items-center gap-2 justify-end w-full sm:w-auto">
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
                    className="p-2 bg-white text-black font-black border-2 border-black rounded-lg shadow-[2px_2px_0px_#000] hover:bg-red-50 hover:text-red-700 transition active:translate-x-[1px] active:translate-y-[1px] flex items-center gap-1 text-xs"
                  >
                    <LogOut className="w-3.5 h-3.5" />
                    <span className="hidden sm:inline font-bold">Logout</span>
                  </button>
                </div>
              </div>
            </div>
          </header>

          {/* 3-DASH SIDEBAR TOGGLE ON THE LEFT TOP BELOW NAVBAR */}
          <div className="no-print max-w-7xl mx-auto w-full px-4 sm:px-6 pt-3 pb-0 flex items-center justify-between flex-wrap gap-2">
            <div className="flex items-center gap-2.5 flex-wrap">
              <button
                type="button"
                onClick={() => setIsSidebarOpen(true)}
                className="flex items-center gap-2 px-3 py-1.5 bg-white text-black font-black text-xs uppercase tracking-wider border-2 border-black rounded-lg shadow-[3px_3px_0px_#000000] hover:bg-[#FFE600] transition active:translate-x-[1px] active:translate-y-[1px]"
                title="Open Prompt History Sidebar (Firebase Firestore)"
                aria-label="Open Prompt History"
              >
                {/* 3 horizontal dashes icon */}
                <div className="flex flex-col gap-1 w-4 justify-center">
                  <span className="block h-0.5 w-full bg-black rounded" />
                  <span className="block h-0.5 w-full bg-black rounded" />
                  <span className="block h-0.5 w-full bg-black rounded" />
                </div>
                <span className="font-virgil font-black">Prompt History</span>
                <span className="text-[9px] bg-black text-[#FFE600] px-1.5 py-0.5 rounded font-black">
                  Firestore
                </span>
              </button>

              {activeInvoiceId && (
                <div className="flex items-center gap-1.5 bg-[#FFFDE6] border-2 border-black px-2.5 py-1 rounded-lg text-xs font-bold shadow-[2px_2px_0px_#000]">
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

            <div className="hidden sm:flex items-center gap-1 text-[11px] font-bold text-neutral-500">
              <ShieldCheck className="w-3.5 h-3.5 text-emerald-600" />
              <span>{user.email}</span>
            </div>
          </div>

          {/* MAIN CONTENT AREA */}
          <div className="flex-1 max-w-7xl mx-auto w-full p-4 sm:p-6">
            <div
              className={`grid gap-8 ${
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
                    <h2 className="text-xl font-black text-black">Invoice Editor</h2>
                    <p className="text-xs text-neutral-600 font-medium">
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

              {/* RIGHT PANEL: STICKY LIVE A4 PREVIEW */}
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
                      <h2 className="text-xl font-black text-black flex items-center gap-2">
                        Live A4 Preview
                        <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse inline-block" />
                      </h2>
                      <p className="text-xs text-neutral-600 font-medium">
                        Multi-page A4 layout &bull; Dynamic page breaks with authentic Robogyaan fidelity.
                      </p>
                    </div>

                    {/* Page Selector Dropdown */}
                    <div className="flex items-center gap-2 bg-white border-2 border-black rounded-lg px-3 py-1 shadow-[2px_2px_0px_#000000]">
                      <label htmlFor="preview-page-select" className="text-xs font-black uppercase text-black shrink-0">
                        Page:
                      </label>
                      <select
                        id="preview-page-select"
                        value={previewPage}
                        onChange={(e) => {
                          const val = e.target.value;
                          setPreviewPage(val === 'all' ? 'all' : parseInt(val, 10));
                        }}
                        className="bg-transparent text-xs font-black text-black py-0.5 pr-2 focus:outline-none cursor-pointer"
                      >
                        <option value="all">All Pages ({totalPages})</option>
                        {Array.from({ length: totalPages }, (_, i) => (
                          <option key={i} value={i}>
                            Page {i + 1} of {totalPages}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>

                  <div className="lg:sticky lg:top-24 overflow-x-auto pb-8">
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
          <footer className="no-print mt-auto border-t-2 border-black bg-white py-4 px-6 text-center text-xs font-bold text-neutral-600">
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
