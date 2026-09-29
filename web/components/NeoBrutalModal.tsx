'use client';

import React from 'react';
import { NeoBrutalButton } from './NeoBrutalButton';
import { AlertTriangle, AlertCircle, Info, X } from 'lucide-react';

export interface NeoBrutalModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm?: () => void;
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  variant?: 'danger' | 'warning' | 'info';
  type?: 'confirm' | 'alert';
}

export const NeoBrutalModal: React.FC<NeoBrutalModalProps> = ({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
  confirmText = 'Confirm',
  cancelText = 'Cancel',
  variant = 'warning',
  type = 'confirm',
}) => {
  if (!isOpen) return null;

  const headerBg =
    variant === 'danger'
      ? 'bg-[#FF4D4D]'
      : variant === 'info'
      ? 'bg-[#4D96FF]'
      : 'bg-[#FFE600]';

  const icon =
    variant === 'danger' ? (
      <AlertCircle className="w-5 h-5 text-black stroke-[2.5]" />
    ) : variant === 'info' ? (
      <Info className="w-5 h-5 text-black stroke-[2.5]" />
    ) : (
      <AlertTriangle className="w-5 h-5 text-black stroke-[2.5]" />
    );

  return (
    <div
      className="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-white/75 backdrop-blur-[3px] transition-all animate-in fade-in duration-150"
      onClick={onClose}
      role="dialog"
      aria-modal="true"
    >
      <div
        className="w-full max-w-md bg-[#FDFBF7] border-[3px] border-black rounded-xl shadow-[8px_8px_0px_0px_#000000] overflow-hidden transform scale-100 transition-all select-none"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div
          className={`${headerBg} border-b-[3px] border-black px-4 py-3 flex items-center justify-between`}
        >
          <div className="flex items-center gap-2">
            {icon}
            <h3 className="font-virgil font-black text-black text-base sm:text-lg tracking-tight uppercase">
              {title}
            </h3>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="w-7 h-7 flex items-center justify-center bg-white text-black border-2 border-black rounded-md shadow-[2px_2px_0px_#000000] hover:bg-neutral-100 active:translate-x-[1px] active:translate-y-[1px] transition cursor-pointer"
            aria-label="Close dialog"
          >
            <X className="w-4 h-4 stroke-[3]" />
          </button>
        </div>

        {/* Message Body */}
        <div className="p-6">
          <p className="font-virgil font-bold text-black text-base sm:text-lg leading-relaxed">
            {message}
          </p>
        </div>

        {/* Action Buttons */}
        <div className="px-5 py-3.5 bg-neutral-100/90 border-t-[3px] border-black flex items-center justify-end gap-3">
          {type === 'confirm' && (
            <NeoBrutalButton
              type="button"
              variant="white"
              size="sm"
              onClick={onClose}
              className="font-virgil font-black uppercase tracking-wider text-xs"
            >
              {cancelText}
            </NeoBrutalButton>
          )}
          <NeoBrutalButton
            type="button"
            variant={variant === 'danger' ? 'danger' : 'yellow'}
            size="sm"
            onClick={() => {
              if (onConfirm) onConfirm();
              onClose();
            }}
            className="font-virgil font-black uppercase tracking-wider text-xs"
          >
            {confirmText}
          </NeoBrutalButton>
        </div>
      </div>
    </div>
  );
};
