import React, { ChangeEvent } from 'react';
import { InvoiceData, InvoiceItem, PaymentMethod } from '../lib/types';
import { NeoBrutalCard } from './NeoBrutalCard';
import { NeoBrutalButton } from './NeoBrutalButton';
import { Plus, Trash2, Upload, RotateCcw, Check, Sparkles } from 'lucide-react';
import { formatINR } from '../lib/numberToWordsIndian';

interface InvoiceEditorProps {
  data: InvoiceData;
  onChange: (updated: InvoiceData) => void;
  onReset: () => void;
}

export const InvoiceEditor: React.FC<InvoiceEditorProps> = ({
  data,
  onChange,
  onReset,
}) => {
  const updateField = <K extends keyof InvoiceData>(key: K, value: InvoiceData[K]) => {
    onChange({
      ...data,
      [key]: value,
    });
  };

  const updateBilledTo = (field: keyof InvoiceData['billedTo'], value: string) => {
    onChange({
      ...data,
      billedTo: {
        ...data.billedTo,
        [field]: value,
      },
    });
  };

  const updateFrom = (field: keyof InvoiceData['from'], value: string) => {
    onChange({
      ...data,
      from: {
        ...data.from,
        [field]: value,
      },
    });
  };

  const handleItemChange = (index: number, field: keyof InvoiceItem, value: any) => {
    const newItems = [...data.items];
    newItems[index] = {
      ...newItems[index],
      [field]: value,
    };
    onChange({
      ...data,
      items: newItems,
    });
  };

  const addItem = () => {
    const newItem: InvoiceItem = {
      id: `item-${Date.now()}`,
      description: 'Robogyaan Robotics & AI Workshop',
      amountPerHead: 250,
      studentCount: 100,
    };
    onChange({
      ...data,
      items: [...data.items, newItem],
    });
  };

  const removeItem = (index: number) => {
    if (data.items.length <= 1) return;
    const newItems = data.items.filter((_, i) => i !== index);
    onChange({
      ...data,
      items: newItems,
    });
  };

  const handleSignatureUpload = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (event) => {
        if (event.target?.result) {
          updateField('signatureImage', event.target.result as string);
        }
      };
      reader.readAsDataURL(file);
    }
  };

  const paymentMethods: PaymentMethod[] = ['CASH', 'UPI', 'CHEQUE', 'BANK DRAFT', 'NEFT'];

  return (
    <div className="space-y-6">
      {/* 1. DOCUMENT IDENTIFIERS & DATES */}
      <NeoBrutalCard
        title="Invoice Details"
        badge="Header Meta"
        variant="yellow"
      >
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-black uppercase tracking-wider mb-1">
              Invoice Number
            </label>
            <input
              type="text"
              value={data.invoiceNo}
              onChange={(e) => updateField('invoiceNo', e.target.value)}
              className="neo-input font-bold"
              placeholder="e.g. RG-JPS-2608-001"
            />
          </div>

          <div>
            <label className="block text-xs font-black uppercase tracking-wider mb-1">
              Registration Number
            </label>
            <input
              type="text"
              value={data.registrationNo}
              onChange={(e) => updateField('registrationNo', e.target.value)}
              className="neo-input font-bold"
              placeholder="e.g. 273744"
            />
          </div>

          <div>
            <label className="block text-xs font-black uppercase tracking-wider mb-1">
              Issue Date
            </label>
            <input
              type="text"
              value={data.issueDate}
              onChange={(e) => updateField('issueDate', e.target.value)}
              className="neo-input font-bold"
              placeholder="DD/MM/YYYY"
            />
          </div>

          <div>
            <label className="block text-xs font-black uppercase tracking-wider mb-1">
              Due Date
            </label>
            <input
              type="text"
              value={data.dueDate}
              onChange={(e) => updateField('dueDate', e.target.value)}
              className="neo-input font-bold"
              placeholder="DD/MM/YYYY"
            />
          </div>
        </div>
      </NeoBrutalCard>

      {/* 2. RECIPIENT & SENDER */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Recipient */}
        <NeoBrutalCard
          title={`Billed To\n(Client)`}
          badge="Recipient"
          variant="white"
        >
          <div className="space-y-3">
            <div>
              <label className="block text-xs font-bold uppercase mb-1 text-black dark:text-white">
                Client / School Name
              </label>
              <input
                type="text"
                value={data.billedTo.name}
                onChange={(e) => updateBilledTo('name', e.target.value)}
                className="neo-input"
                placeholder="JYOTIRMOY PUBLIC SCHOOL"
              />
            </div>
            <div>
              <label className="block text-xs font-bold uppercase mb-1 text-black dark:text-white">
                Address Line
              </label>
              <input
                type="text"
                value={data.billedTo.address}
                onChange={(e) => updateBilledTo('address', e.target.value)}
                className="neo-input"
                placeholder="Tematha , Sonarpur"
              />
            </div>
            <div>
              <label className="block text-xs font-bold uppercase mb-1 text-black dark:text-white">
                Pin & State
              </label>
              <input
                type="text"
                value={data.billedTo.pinState}
                onChange={(e) => updateBilledTo('pinState', e.target.value)}
                className="neo-input"
                placeholder="Pin:743330 , West Bengal"
              />
            </div>
          </div>
        </NeoBrutalCard>

        {/* Sender */}
        <NeoBrutalCard
          title={`From\n(Robogyaan)`}
          badge="Sender"
          variant="white"
        >
          <div className="space-y-3">
            <div>
              <label className="block text-xs font-bold uppercase mb-1 text-black dark:text-white">
                Organization / Company
              </label>
              <input
                type="text"
                value={data.from.company}
                onChange={(e) => updateFrom('company', e.target.value)}
                className="neo-input font-bold"
                placeholder="ROBOGYAAN"
              />
            </div>
            <div>
              <label className="block text-xs font-bold uppercase mb-1 text-black dark:text-white">
                Address
              </label>
              <input
                type="text"
                value={data.from.address}
                onChange={(e) => updateFrom('address', e.target.value)}
                className="neo-input"
                placeholder="Dakshin Gobindopur , Sonarpur"
              />
            </div>
            <div>
              <label className="block text-xs font-bold uppercase mb-1 text-black dark:text-white">
                City, Pin & State
              </label>
              <input
                type="text"
                value={data.from.cityPinState}
                onChange={(e) => updateFrom('cityPinState', e.target.value)}
                className="neo-input"
                placeholder="Kolkata - 700145 , West Bengal"
              />
            </div>
          </div>
        </NeoBrutalCard>
      </div>

      {/* 3. ITEMIZED BILLING TABLE */}
      <NeoBrutalCard
        title="Itemized Billing Table"
        badge="Services"
        variant="white"
        headerAction={
          <NeoBrutalButton
            size="sm"
            variant="yellow"
            onClick={addItem}
            icon={<Plus className="w-4 h-4" />}
          >
            Add Item
          </NeoBrutalButton>
        }
      >
        <div className="space-y-4">
          {data.items.map((item, index) => {
            const rowTotal =
              (Number(item.amountPerHead) || 0) * (Number(item.studentCount) || 0);

            return (
              <div
                key={item.id || index}
                className="p-3 sm:p-4 rounded-lg border-2 border-black dark:border-neutral-700 bg-neutral-50 dark:bg-[#202024] text-black dark:text-white shadow-[3px_3px_0px_0px_#000000] relative space-y-3"
              >
                <div className="flex items-center justify-between border-b border-black/20 dark:border-neutral-700 pb-2">
                  <span className="font-extrabold text-sm flex items-center gap-1.5 text-black dark:text-white">
                    <span className="w-5 h-5 bg-[#FFE600] text-black border border-black rounded-full inline-flex items-center justify-center text-xs font-bold">
                      {index + 1}
                    </span>
                    Line Item #{index + 1}
                  </span>
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-black bg-black text-[#FFE600] px-2 py-0.5 rounded border border-black dark:border-neutral-600">
                      Subtotal: ₹{formatINR(rowTotal)}
                    </span>
                    {data.items.length > 1 && (
                      <button
                        onClick={() => removeItem(index)}
                        className="p-1 text-red-600 hover:text-red-800 dark:text-red-400 dark:hover:text-red-300 hover:bg-red-50 dark:hover:bg-red-950/30 rounded border border-transparent hover:border-red-400 transition"
                        title="Delete Item"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    )}
                  </div>
                </div>

                <div className="grid grid-cols-12 gap-2 sm:gap-3 items-end">
                  <div className="col-span-6">
                    <label className="block text-[11px] sm:text-xs font-bold mb-1 truncate text-black dark:text-white" title="Description / Program Name">
                      Description / Program Name
                    </label>
                    <input
                      type="text"
                      value={item.description}
                      onChange={(e) =>
                        handleItemChange(index, 'description', e.target.value)
                      }
                      className="neo-input"
                      placeholder="e.g. Robogyaan ECA Program"
                    />
                  </div>

                  <div className="col-span-3">
                    <label className="block text-[11px] sm:text-xs font-bold mb-1 truncate text-black dark:text-white" title="Amount / Student Head (₹)">
                      Amount / Student Head (₹)
                    </label>
                    <input
                      type="number"
                      min="0"
                      step="any"
                      value={item.amountPerHead}
                      onChange={(e) =>
                        handleItemChange(
                          index,
                          'amountPerHead',
                          parseFloat(e.target.value) || 0
                        )
                      }
                      className="neo-input font-semibold"
                    />
                  </div>

                  <div className="col-span-3">
                    <label className="block text-[11px] sm:text-xs font-bold mb-1 truncate text-black dark:text-white" title="No. of Students">
                      No. of Students
                    </label>
                    <input
                      type="number"
                      min="1"
                      step="1"
                      value={item.studentCount}
                      onChange={(e) =>
                        handleItemChange(
                          index,
                          'studentCount',
                          parseInt(e.target.value, 10) || 0
                        )
                      }
                      className="neo-input font-semibold"
                    />
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </NeoBrutalCard>

      {/* 4. PAYMENT METHOD & SETTINGS */}
      <NeoBrutalCard
        title="Payment & Document Settings"
        badge="Finalize"
        variant="white"
      >
        <div className="space-y-5">
          {/* Payment Method Chips */}
          <div>
            <label className="block text-xs font-black uppercase tracking-wider mb-2 text-black dark:text-white">
              Payment Method
            </label>
            <div className="flex flex-wrap gap-2">
              {paymentMethods.map((method) => {
                const isSelected = data.paymentMethod === method;
                return (
                  <button
                    key={method}
                    type="button"
                    onClick={() => updateField('paymentMethod', method)}
                    className={`px-3 py-1.5 rounded-lg border-2 font-bold text-xs sm:text-sm tracking-wide transition-all ${
                      isSelected
                        ? 'bg-[#FFE600] text-black border-black shadow-[3px_3px_0px_0px_#000000] -translate-y-0.5'
                        : 'bg-white text-black border-black hover:bg-neutral-100 dark:bg-[#27272a] dark:text-white dark:border-neutral-600 dark:hover:bg-[#323238] shadow-[2px_2px_0px_0px_#000000]'
                    }`}
                  >
                    {isSelected && <Check className="w-3.5 h-3.5 inline mr-1 stroke-[3]" />}
                    {method}
                  </button>
                );
              })}
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2 border-t border-black/20 dark:border-neutral-700">
            {/* Signature Upload / Reset */}
            <div>
              <label className="block text-xs font-bold uppercase mb-1 text-black dark:text-white">
                Authorised Signature (PNG)
              </label>
              <div className="flex items-center gap-2">
                <label className="neo-btn bg-white text-black border-black hover:bg-neutral-50 dark:bg-[#27272a] dark:text-white dark:border-neutral-600 dark:hover:bg-[#323238] text-xs py-1.5 px-3 cursor-pointer">
                  <Upload className="w-3.5 h-3.5 mr-1.5 text-black dark:text-white" />
                  Upload Custom Signature
                  <input
                    type="file"
                    accept="image/*"
                    onChange={handleSignatureUpload}
                    className="hidden"
                  />
                </label>
                {data.signatureImage !== '/signature.png' && (
                  <button
                    onClick={() => updateField('signatureImage', '/signature.png')}
                    className="p-1.5 text-neutral-700 hover:text-black dark:text-neutral-300 dark:hover:text-white border border-black dark:border-neutral-600 rounded bg-white dark:bg-[#27272a]"
                    title="Reset to default Suman Mondal signature"
                  >
                    <RotateCcw className="w-3.5 h-3.5" />
                  </button>
                )}
              </div>
              <p className="text-[11px] text-neutral-600 dark:text-neutral-400 mt-1">
                Default: Official Suman Mondal cursive signature.
              </p>
            </div>

            {/* Watermark Toggle */}
            <div className="flex flex-col justify-center">
              <label className="block text-xs font-bold uppercase mb-1 text-black dark:text-white">
                Robogyaan Watermark
              </label>
              <label className="inline-flex items-center gap-2 cursor-pointer select-none">
                <input
                  type="checkbox"
                  checked={data.showWatermark}
                  onChange={(e) => updateField('showWatermark', e.target.checked)}
                  className="w-5 h-5 accent-black border-2 border-black rounded cursor-pointer"
                />
                <span className="text-xs font-bold text-black dark:text-white">
                  Display subtle network logo watermark in table
                </span>
              </label>
            </div>
          </div>

          {/* Reset Action */}
          <div className="pt-3 border-t border-black/20 dark:border-neutral-700 flex justify-end">
            <NeoBrutalButton
              variant="white"
              size="sm"
              onClick={onReset}
              icon={<RotateCcw className="w-3.5 h-3.5" />}
            >
              Reset to Sample Invoice
            </NeoBrutalButton>
          </div>
        </div>
      </NeoBrutalCard>
    </div>
  );
};
