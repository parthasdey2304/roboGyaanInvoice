'use client';

import React, { useState, useRef } from 'react';
import { InvoiceData } from '../lib/types';
import { defaultInvoiceData } from '../lib/defaultInvoice';
import { InvoiceEditor } from '../components/InvoiceEditor';
import { InvoicePreview } from '../components/InvoicePreview';
import { NeoBrutalButton } from '../components/NeoBrutalButton';
import { Download, Printer, RotateCcw, Eye, Edit3, Columns, CheckCircle2 } from 'lucide-react';
import { toPng } from 'html-to-image';
import jsPDF from 'jspdf';

export default function InvoicePage() {
  const [invoiceData, setInvoiceData] = useState<InvoiceData>(defaultInvoiceData);
  const [activeTab, setActiveTab] = useState<'split' | 'editor' | 'preview'>('split');
  const [isExporting, setIsExporting] = useState(false);
  const [exportSuccess, setExportSuccess] = useState(false);
  const previewRef = useRef<HTMLDivElement>(null);

  const handleReset = () => {
    if (confirm('Reset all fields to the default Robogyaan template values?')) {
      setInvoiceData(defaultInvoiceData);
    }
  };

  const handlePrint = () => {
    window.print();
  };

  const handleDownloadPdf = async () => {
    if (!previewRef.current) return;
    try {
      setIsExporting(true);
      const invoiceElement = previewRef.current;

      // Render high-res image at 2.5x scale using native browser vector engine
      const dataUrl = await toPng(invoiceElement, {
        quality: 0.98,
        pixelRatio: 2.5,
        backgroundColor: '#FFFFFF',
        cacheBust: true,
      });

      // A4 dimensions in mm: 210 x 297
      const pdf = new jsPDF({
        orientation: 'portrait',
        unit: 'mm',
        format: 'a4',
      });

      const pdfWidth = pdf.internal.pageSize.getWidth();
      const pdfHeight = (invoiceElement.offsetHeight * pdfWidth) / invoiceElement.offsetWidth;

      pdf.addImage(dataUrl, 'PNG', 0, 0, pdfWidth, Math.min(pdfHeight, 297));
      pdf.save(`Invoice_${invoiceData.invoiceNo.replace(/[^a-zA-Z0-9_-]/g, '_')}.pdf`);

      setExportSuccess(true);
      setTimeout(() => setExportSuccess(false), 3000);
    } catch (err) {
      console.error('PDF generation error, opening print dialog:', err);
      window.print();
    } finally {
      setIsExporting(false);
    }
  };

  return (
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

            {/* Mobile View Toggles on Top Right (< md) */}
            <div className="flex md:hidden items-center gap-1 bg-white p-1 rounded-lg border-2 border-black shadow-[2px_2px_0px_#000] shrink-0">
              <button
                type="button"
                onClick={() => setActiveTab('editor')}
                className={`px-2 py-1 text-[11px] font-black uppercase tracking-wider rounded flex items-center gap-1 transition-all ${
                  activeTab === 'editor'
                    ? 'bg-black text-[#FFE600] shadow-[1px_1px_0px_#000]'
                    : 'text-black hover:bg-neutral-100'
                }`}
              >
                <Edit3 className="w-3 h-3" />
                <span>Editor</span>
              </button>
              <button
                type="button"
                onClick={() => setActiveTab('split')}
                className={`px-2 py-1 text-[11px] font-black uppercase tracking-wider rounded flex items-center gap-1 transition-all ${
                  activeTab === 'split'
                    ? 'bg-black text-[#FFE600] shadow-[1px_1px_0px_#000]'
                    : 'text-black hover:bg-neutral-100'
                }`}
              >
                <Columns className="w-3 h-3" />
                <span>Split</span>
              </button>
              <button
                type="button"
                onClick={() => setActiveTab('preview')}
                className={`px-2 py-1 text-[11px] font-black uppercase tracking-wider rounded flex items-center gap-1 transition-all ${
                  activeTab === 'preview'
                    ? 'bg-black text-[#FFE600] shadow-[1px_1px_0px_#000]'
                    : 'text-black hover:bg-neutral-100'
                }`}
              >
                <Eye className="w-3 h-3" />
                <span>Preview</span>
              </button>
            </div>
          </div>

          {/* Desktop Right Side: Tabs + Action Buttons */}
          <div className="flex items-center justify-end gap-2.5 flex-wrap w-full md:w-auto">
            {/* Desktop Tabs on Top Right */}
            <div className="hidden md:flex items-center gap-1 bg-white p-1 rounded-lg border-2 border-black shadow-[2px_2px_0px_#000]">
              <button
                type="button"
                onClick={() => setActiveTab('editor')}
                className={`px-2.5 py-1 text-xs font-black uppercase tracking-wider rounded flex items-center gap-1.5 transition-all ${
                  activeTab === 'editor'
                    ? 'bg-black text-[#FFE600] shadow-[1px_1px_0px_#000]'
                    : 'text-black hover:bg-neutral-100'
                }`}
              >
                <Edit3 className="w-3.5 h-3.5" />
                <span>Editor</span>
              </button>
              <button
                type="button"
                onClick={() => setActiveTab('split')}
                className={`px-2.5 py-1 text-xs font-black uppercase tracking-wider rounded flex items-center gap-1.5 transition-all ${
                  activeTab === 'split'
                    ? 'bg-black text-[#FFE600] shadow-[1px_1px_0px_#000]'
                    : 'text-black hover:bg-neutral-100'
                }`}
              >
                <Columns className="w-3.5 h-3.5" />
                <span>Split</span>
              </button>
              <button
                type="button"
                onClick={() => setActiveTab('preview')}
                className={`px-2.5 py-1 text-xs font-black uppercase tracking-wider rounded flex items-center gap-1.5 transition-all ${
                  activeTab === 'preview'
                    ? 'bg-black text-[#FFE600] shadow-[1px_1px_0px_#000]'
                    : 'text-black hover:bg-neutral-100'
                }`}
              >
                <Eye className="w-3.5 h-3.5" />
                <span>Preview</span>
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
            </div>
          </div>
        </div>
      </header>

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
              <div className="no-print flex items-center justify-between mb-4">
                <div>
                  <h2 className="text-xl font-black text-black flex items-center gap-2">
                    Live A4 Preview
                    <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse inline-block" />
                  </h2>
                  <p className="text-xs text-neutral-600 font-medium">
                    Strict Robogyaan template with Virgil font and high-res vector rendering.
                  </p>
                </div>
              </div>

              <div className="lg:sticky lg:top-24 overflow-x-auto pb-8">
                <InvoicePreview ref={previewRef} data={invoiceData} />
              </div>
            </div>
          )}
        </div>
      </div>

      {/* FOOTER */}
      <footer className="no-print mt-auto border-t-2 border-black bg-white py-4 px-6 text-center text-xs font-bold text-neutral-600">
        Robogyaan Dual-Platform Invoice Suite &bull; Neo-Brutalist Edition &bull; IGNITING CURIOSITY, BUILDING FUTURE
      </footer>
    </main>
  );
}
