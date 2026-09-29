'use client';

import React, { useState, useEffect } from 'react';
import { InvoiceData } from '../lib/types';
import { 
  InvoiceHistoryEntry, 
  fetchInvoiceHistory, 
  saveInvoicePrompt, 
  updateInvoiceHistoryEntry,
  deleteInvoiceHistoryEntry 
} from '../lib/firebase';
import { formatINR } from '../lib/numberToWordsIndian';
import { NeoBrutalModal } from './NeoBrutalModal';
import { 
  X, 
  Cloud, 
  Trash2, 
  Download, 
  Clock, 
  RefreshCw,
  PlusCircle,
  FileText,
  Edit2,
  Check,
  Save,
  CheckCircle2
} from 'lucide-react';

interface HistorySidebarProps {
  isOpen: boolean;
  onClose: () => void;
  currentInvoiceData: InvoiceData;
  onLoadInvoice: (data: InvoiceData, id?: string) => void;
  activeInvoiceId?: string | null;
}

export const HistorySidebar: React.FC<HistorySidebarProps> = ({
  isOpen,
  onClose,
  currentInvoiceData,
  onLoadInvoice,
  activeInvoiceId,
}) => {
  const [history, setHistory] = useState<InvoiceHistoryEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [updatingId, setUpdatingId] = useState<string | null>(null);
  const [promptText, setPromptText] = useState('');
  const [showSavePrompt, setShowSavePrompt] = useState(false);
  const [saveSuccessMessage, setSaveSuccessMessage] = useState<string | null>(null);
  const [deleteCandidateId, setDeleteCandidateId] = useState<string | null>(null);

  // In-line editing state for a history item
  const [editingId, setEditingId] = useState<string | null>(null);
  const [editingText, setEditingText] = useState('');

  const loadHistory = async () => {
    try {
      setLoading(true);
      const items = await fetchInvoiceHistory();
      setHistory(items);
    } catch (err) {
      console.error('Failed to load history:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isOpen) {
      loadHistory();
    }
  }, [isOpen]);

  const showToast = (msg: string) => {
    setSaveSuccessMessage(msg);
    setTimeout(() => setSaveSuccessMessage(null), 3000);
  };

  const handleSaveNew = async () => {
    try {
      setSaving(true);
      const desc = promptText.trim() || `Invoice #${currentInvoiceData.invoiceNo} for ${currentInvoiceData.billedTo.name || 'Client'}`;
      const newId = await saveInvoicePrompt(currentInvoiceData, desc);
      setPromptText('');
      setShowSavePrompt(false);
      showToast('✓ Saved new invoice prompt to Firestore!');
      await loadHistory();
    } catch (err) {
      console.error('Failed to save prompt:', err);
    } finally {
      setSaving(false);
    }
  };

  const handleUpdateExisting = async (targetId: string, customDesc?: string) => {
    try {
      setUpdatingId(targetId);
      const desc = customDesc !== undefined ? customDesc : promptText.trim();
      await updateInvoiceHistoryEntry(targetId, currentInvoiceData, desc || undefined);
      showToast('✓ Updated existing invoice in Firestore!');
      setEditingId(null);
      await loadHistory();
    } catch (err) {
      console.error('Failed to update invoice in Firestore:', err);
    } finally {
      setUpdatingId(null);
    }
  };

  const handleSaveInlineRename = async (id: string) => {
    if (!editingText.trim()) {
      setEditingId(null);
      return;
    }
    const item = history.find((h) => h.id === id);
    if (!item) return;

    try {
      setUpdatingId(id);
      await updateInvoiceHistoryEntry(id, item.invoiceData, editingText.trim());
      showToast('✓ Note updated in Firestore!');
      setEditingId(null);
      await loadHistory();
    } catch (err) {
      console.error('Failed to rename prompt:', err);
    } finally {
      setUpdatingId(null);
    }
  };

  const handleDeleteClick = (id?: string) => {
    if (id) setDeleteCandidateId(id);
  };

  const confirmDeletePrompt = async () => {
    if (!deleteCandidateId) return;
    try {
      await deleteInvoiceHistoryEntry(deleteCandidateId);
      setHistory((prev) => prev.filter((item) => item.id !== deleteCandidateId));
      showToast('Deleted invoice from Firestore');
    } catch (err) {
      console.error('Delete error:', err);
    } finally {
      setDeleteCandidateId(null);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-hidden no-print">
      {/* Backdrop */}
      <div
        className="fixed inset-0 bg-black/60 backdrop-blur-xs transition-opacity"
        onClick={onClose}
      />

      <div className="fixed inset-y-0 left-0 max-w-full flex pr-10">
        <div className="w-screen max-w-md bg-[#FFFDF7] border-r-[3.5px] border-black shadow-[6px_0px_0px_#000000] flex flex-col z-50">
          {/* Header */}
          <div className="bg-[#FFE600] border-b-[3px] border-black p-4 flex items-center justify-between shadow-[0_2px_0px_#000]">
            <div className="flex items-center gap-2.5">
              <div className="w-8 h-8 bg-black text-[#FFE600] font-black rounded flex items-center justify-center border border-black shadow-[1px_1px_0px_#000]">
                <Cloud className="w-4 h-4" />
              </div>
              <div>
                <div className="flex items-center gap-1.5">
                  <h2 className="text-base font-black text-black tracking-tight leading-none uppercase">
                    Prompt History
                  </h2>
                  <span className="text-[9px] font-black uppercase bg-black text-white px-1.5 py-0.5 rounded">
                    Firestore
                  </span>
                </div>
                <p className="text-[10px] font-bold text-black/80 mt-0.5">
                  Firebase Cloud Storage &bull; Live Sync
                </p>
              </div>
            </div>

            <button
              onClick={onClose}
              className="p-1.5 bg-white text-black font-black border-2 border-black rounded shadow-[2px_2px_0px_#000] hover:bg-neutral-100 transition active:translate-x-[1px] active:translate-y-[1px]"
              title="Close Sidebar"
            >
              <X className="w-4 h-4" />
            </button>
          </div>

          {/* Save / Update Current Invoice Section */}
          <div className="p-4 border-b-2 border-black bg-white space-y-2">
            {!showSavePrompt ? (
              <div className="space-y-2">
                {activeInvoiceId && (
                  <button
                    onClick={() => handleUpdateExisting(activeInvoiceId)}
                    disabled={updatingId === activeInvoiceId}
                    className="w-full py-2 px-3 bg-black text-[#FFE600] font-black text-xs uppercase tracking-wider border-2 border-black rounded shadow-[3px_3px_0px_#FFE600] hover:bg-neutral-800 flex items-center justify-center gap-2 transition active:translate-x-[1px] active:translate-y-[1px] disabled:opacity-50"
                  >
                    <Save className="w-4 h-4" />
                    <span>{updatingId === activeInvoiceId ? 'Updating Firestore...' : 'Update Active Invoice in Firestore'}</span>
                  </button>
                )}

                <button
                  onClick={() => setShowSavePrompt(true)}
                  className="w-full py-2 px-3 bg-[#FFE600] text-black font-black text-xs uppercase tracking-wider border-2 border-black rounded shadow-[3px_3px_0px_#000] hover:bg-[#FFD700] flex items-center justify-center gap-2 transition active:translate-x-[1px] active:translate-y-[1px]"
                >
                  <PlusCircle className="w-4 h-4" />
                  <span>Save Current as New Prompt</span>
                </button>
              </div>
            ) : (
              <div className="space-y-2 border-2 border-black p-3 bg-neutral-50 rounded shadow-[2px_2px_0px_#000]">
                <label className="block text-[11px] font-black uppercase tracking-wider text-black">
                  Prompt Description / Note:
                </label>
                <input
                  type="text"
                  value={promptText}
                  onChange={(e) => setPromptText(e.target.value)}
                  placeholder={`e.g. ECA Invoice for ${currentInvoiceData.billedTo.name || 'School'}`}
                  className="neo-input text-xs font-medium w-full"
                  autoFocus
                />
                <div className="flex items-center gap-2 pt-1">
                  <button
                    onClick={handleSaveNew}
                    disabled={saving}
                    className="flex-1 py-1.5 bg-black text-white text-xs font-black uppercase rounded border border-black shadow-[2px_2px_0px_#FFE600] hover:bg-neutral-800 disabled:opacity-50"
                  >
                    {saving ? 'Saving...' : 'Confirm Save'}
                  </button>
                  <button
                    onClick={() => setShowSavePrompt(false)}
                    className="py-1.5 px-3 bg-white text-black text-xs font-bold rounded border border-black hover:bg-neutral-100"
                  >
                    Cancel
                  </button>
                </div>
              </div>
            )}

            {saveSuccessMessage && (
              <p className="text-[11px] font-black text-emerald-800 bg-emerald-100 border border-emerald-500 rounded p-1.5 text-center flex items-center justify-center gap-1">
                <CheckCircle2 className="w-3.5 h-3.5" />
                {saveSuccessMessage}
              </p>
            )}
          </div>

          {/* History List Content */}
          <div className="flex-1 overflow-y-auto p-4 space-y-3">
            <div className="flex items-center justify-between mb-1">
              <span className="text-xs font-black uppercase tracking-wider text-neutral-800 flex items-center gap-1">
                <Clock className="w-3.5 h-3.5" />
                Saved Invoices ({history.length})
              </span>
              <button
                onClick={loadHistory}
                disabled={loading}
                className="text-[10px] font-black text-neutral-600 hover:text-black flex items-center gap-1"
                title="Refresh history"
              >
                <RefreshCw className={`w-3 h-3 ${loading ? 'animate-spin' : ''}`} />
                Refresh
              </button>
            </div>

            {loading && history.length === 0 ? (
              <div className="py-12 text-center text-xs font-bold text-neutral-500">
                <RefreshCw className="w-6 h-6 animate-spin mx-auto mb-2 text-black" />
                Loading prompts from Firestore...
              </div>
            ) : history.length === 0 ? (
              <div className="py-10 px-4 text-center border-2 border-dashed border-neutral-300 rounded-lg">
                <FileText className="w-8 h-8 mx-auto mb-2 text-neutral-400" />
                <p className="text-xs font-bold text-neutral-700">No saved invoice prompts yet</p>
                <p className="text-[11px] text-neutral-500 mt-1">
                  Save invoices above to store and modify them in Firebase Cloud Firestore.
                </p>
              </div>
            ) : (
              history.map((entry) => {
                const isActive = activeInvoiceId === entry.id;
                const isEditingThis = editingId === entry.id;

                return (
                  <div
                    key={entry.id}
                    className={`border-2 border-black rounded-lg p-3 shadow-[3px_3px_0px_#000] space-y-2 transition ${
                      isActive ? 'bg-[#FFFDE6] ring-2 ring-black' : 'bg-white hover:bg-neutral-50'
                    }`}
                  >
                    <div className="flex items-start justify-between gap-2">
                      <div className="flex-1">
                        <div className="flex items-center gap-1.5 flex-wrap">
                          <span className="text-[10px] font-black bg-black text-[#FFE600] px-1.5 py-0.5 rounded">
                            {entry.invoiceNo}
                          </span>
                          {isActive && (
                            <span className="text-[9px] font-black uppercase bg-emerald-600 text-white px-1.5 py-0.5 rounded">
                              Active Editor
                            </span>
                          )}
                        </div>

                        {/* Note Description / Inline Edit */}
                        {isEditingThis ? (
                          <div className="flex items-center gap-1.5 mt-1.5">
                            <input
                              type="text"
                              value={editingText}
                              onChange={(e) => setEditingText(e.target.value)}
                              className="neo-input text-xs font-medium w-full py-0.5 px-1.5"
                              autoFocus
                            />
                            <button
                              onClick={() => handleSaveInlineRename(entry.id!)}
                              className="p-1 bg-black text-[#FFE600] rounded hover:bg-neutral-800"
                              title="Save description"
                            >
                              <Check className="w-3.5 h-3.5" />
                            </button>
                            <button
                              onClick={() => setEditingId(null)}
                              className="p-1 bg-neutral-200 text-black rounded hover:bg-neutral-300"
                              title="Cancel"
                            >
                              <X className="w-3.5 h-3.5" />
                            </button>
                          </div>
                        ) : (
                          <div className="flex items-center gap-1 mt-1 group">
                            <h4 className="text-xs font-black text-black line-clamp-1">
                              {entry.promptDescription || entry.clientName}
                            </h4>
                            <button
                              onClick={() => {
                                setEditingId(entry.id || null);
                                setEditingText(entry.promptDescription || '');
                              }}
                              className="opacity-60 group-hover:opacity-100 hover:text-black p-0.5 transition"
                              title="Rename Prompt Description"
                            >
                              <Edit2 className="w-3 h-3 text-neutral-600" />
                            </button>
                          </div>
                        )}

                        <p className="text-[11px] font-semibold text-neutral-700 mt-0.5">
                          {entry.clientName}
                        </p>
                      </div>

                      <div className="text-right shrink-0">
                        <span className="text-xs font-black text-black block">
                          ₹{formatINR(entry.totalAmount)}
                        </span>
                        <span className="text-[9px] font-bold text-neutral-500">
                          {entry.createdAt ? new Date(entry.createdAt).toLocaleDateString() : 'Cloud'}
                        </span>
                      </div>
                    </div>

                    <div className="flex items-center gap-2 pt-1 border-t border-neutral-200">
                      {/* Load Button */}
                      <button
                        onClick={() => {
                          onLoadInvoice(entry.invoiceData, entry.id);
                          showToast(`Loaded invoice ${entry.invoiceNo}`);
                          onClose();
                        }}
                        className="flex-1 py-1 px-2 bg-[#FFE600] text-black text-[11px] font-black uppercase rounded border border-black shadow-[1px_1px_0px_#000] hover:bg-[#FFD700] flex items-center justify-center gap-1 active:translate-x-[1px] active:translate-y-[1px]"
                      >
                        <Download className="w-3 h-3" />
                        Load
                      </button>

                      {/* Modify / Update with Editor values button */}
                      <button
                        onClick={() => handleUpdateExisting(entry.id!)}
                        disabled={updatingId === entry.id}
                        className="py-1 px-2 bg-white text-black text-[11px] font-black uppercase rounded border border-black hover:bg-neutral-100 flex items-center gap-1 transition"
                        title="Update this saved invoice with the current values in the editor"
                      >
                        <Save className="w-3 h-3 text-neutral-700" />
                        Modify
                      </button>

                      {/* Delete button */}
                      <button
                        onClick={() => handleDeleteClick(entry.id)}
                        className="p-1 text-red-600 hover:text-red-800 hover:bg-red-50 rounded border border-transparent hover:border-red-400 transition"
                        title="Delete from Firestore"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  </div>
                );
              })
            )}
          </div>

          {/* Footer Info */}
          <div className="p-3 bg-neutral-100 border-t-2 border-black text-[10px] font-bold text-neutral-600 text-center">
            Connected to Firebase: <span className="font-mono text-black font-bold">robogyaan-invoice</span>
          </div>
        </div>
      </div>

      {/* NEO-BRUTALIST DELETE CONFIRMATION MODAL */}
      <NeoBrutalModal
        isOpen={!!deleteCandidateId}
        onClose={() => setDeleteCandidateId(null)}
        onConfirm={confirmDeletePrompt}
        title="Delete Prompt"
        message="Are you sure you want to permanently delete this invoice prompt from Firebase Firestore? This action cannot be undone."
        confirmText="Delete Forever"
        cancelText="Cancel"
        variant="danger"
        type="confirm"
      />
    </div>
  );
};
