import React, { forwardRef } from 'react';
import Image from 'next/image';
import { InvoiceData } from '../lib/types';
import { formatINR, numberToWordsIndian } from '../lib/numberToWordsIndian';
import { RobogyaanLogo } from './RobogyaanLogo';

interface InvoicePreviewProps {
  data: InvoiceData;
  scale?: number;
}

export const InvoicePreview = forwardRef<HTMLDivElement, InvoicePreviewProps>(
  ({ data }, ref) => {
    // Calculate total amount
    const totalAmount = data.items.reduce(
      (sum, item) => sum + (Number(item.amountPerHead) || 0) * (Number(item.studentCount) || 0),
      0
    );

    const amountInWords = numberToWordsIndian(totalAmount);

    return (
      <div className="w-full flex justify-center py-2 sm:py-4">
        {/* A4 Sheet Container */}
        <div
          ref={ref}
          id="invoice-sheet"
          className="print-invoice-sheet bg-white text-black font-sans relative shadow-[0_10px_35px_rgba(0,0,0,0.15)] border-2 border-black box-border w-full max-w-[760px] aspect-[1/1.414] min-h-[960px] p-6 sm:p-8 flex flex-col justify-between"
          style={{
            fontFamily: 'var(--font-virgil), Comic Sans MS, cursive, sans-serif',
          }}
        >
          {/* TOP HEADER SECTION */}
          <div>
            <div className="flex items-start justify-between relative mb-2">
              {/* Brand Header */}
              <div className="pt-1">
                <RobogyaanLogo />
              </div>

              {/* Right Angle Blocks */}
              <div className="flex flex-col items-end gap-1.5 w-7/12 max-w-[340px]">
                {/* Black Polygon Header */}
                <div
                  className="bg-[#121212] text-white py-1.5 px-6 font-bold text-xs sm:text-sm tracking-wide text-right w-full"
                  style={{
                    clipPath: 'polygon(12% 0, 100% 0, 100% 100%, 0% 100%)',
                  }}
                >
                  <span className="opacity-90">Invoice No. : </span>
                  <span className="font-extrabold">{data.invoiceNo}</span>
                </div>

                {/* Orange Badge */}
                <div
                  className="bg-[#FFA500] text-black py-1 px-5 font-bold text-xs sm:text-sm tracking-wide text-right w-10/12"
                  style={{
                    clipPath: 'polygon(14% 0, 100% 0, 100% 100%, 0% 100%)',
                  }}
                >
                  Registration No. - <span className="font-extrabold">{data.registrationNo}</span>
                </div>
              </div>
            </div>

            {/* Header Separator Line */}
            <hr className="border-t-[1.5px] border-neutral-300 my-3" />

            {/* SENDER & RECIPIENT BLOCKS */}
            <div className="grid grid-cols-2 gap-6 my-4 text-xs sm:text-sm leading-relaxed">
              {/* BILL To Column */}
              <div>
                <div className="inline-block bg-[#E5E7EB] text-black px-4 py-0.5 rounded-full font-bold text-xs sm:text-sm mb-2 shadow-sm border border-neutral-300">
                  BILL To :
                </div>
                <div className="font-bold text-sm sm:text-base tracking-wide uppercase text-neutral-900 mt-1">
                  {data.billedTo.name || 'CLIENT / SCHOOL NAME'}
                </div>
                <div className="text-neutral-700">{data.billedTo.address}</div>
                <div className="text-neutral-700 font-medium">{data.billedTo.pinState}</div>
              </div>

              {/* From Column */}
              <div>
                <div className="inline-block bg-[#E5E7EB] text-black px-4 py-0.5 rounded-full font-bold text-xs sm:text-sm mb-2 shadow-sm border border-neutral-300">
                  From :
                </div>
                <div className="font-bold text-sm sm:text-base tracking-wide uppercase text-neutral-900 mt-1">
                  {data.from.company}
                </div>
                <div className="text-neutral-700">{data.from.address}</div>
                <div className="text-neutral-700 font-medium">{data.from.cityPinState}</div>
              </div>
            </div>

            {/* META SUMMARY BAR (4 Columns) */}
            <div className="grid grid-cols-4 gap-2 my-5 text-center">
              {/* Box 1: Invoice No */}
              <div className="bg-[#FFB800] text-white p-2 rounded-sm border border-neutral-300/40 shadow-sm flex flex-col justify-center">
                <span className="text-[10px] sm:text-xs font-semibold opacity-90 block leading-tight">
                  Invoice No.
                </span>
                <span className="text-xs sm:text-sm font-extrabold truncate">
                  {data.invoiceNo}
                </span>
              </div>

              {/* Box 2: Issue Date */}
              <div className="bg-[#FFB800] text-white p-2 rounded-sm border border-neutral-300/40 shadow-sm flex flex-col justify-center">
                <span className="text-[10px] sm:text-xs font-semibold opacity-90 block leading-tight">
                  Issue Date:
                </span>
                <span className="text-xs sm:text-sm font-extrabold">{data.issueDate}</span>
              </div>

              {/* Box 3: Due Date */}
              <div className="bg-[#FFB800] text-white p-2 rounded-sm border border-neutral-300/40 shadow-sm flex flex-col justify-center">
                <span className="text-[10px] sm:text-xs font-semibold opacity-90 block leading-tight">
                  Due Date:
                </span>
                <span className="text-xs sm:text-sm font-extrabold">{data.dueDate}</span>
              </div>

              {/* Box 4: Total Due */}
              <div className="bg-[#505050] text-white p-2 rounded-sm border border-black shadow-sm flex flex-col justify-center">
                <span className="text-[10px] sm:text-xs font-medium opacity-90 block leading-tight">
                  Total Due :
                </span>
                <span className="text-xs sm:text-sm font-extrabold">
                  ₹ {formatINR(totalAmount)}/-
                </span>
              </div>
            </div>

            {/* ITEMIZED BILLING TABLE */}
            <div className="border-[2px] border-black my-4 relative overflow-hidden bg-white">
              {/* Background Watermark */}
              {data.showWatermark && (
                <div className="absolute inset-0 flex items-center justify-center pointer-events-none z-0">
                  <RobogyaanLogo isWatermark={true} />
                </div>
              )}

              {/* Table Header */}
              <div className="grid grid-cols-12 border-b-[2px] border-black bg-white/90 text-xs sm:text-sm font-bold text-center z-10 relative">
                <div className="col-span-5 p-2 border-r-[2px] border-black text-center">Item</div>
                <div className="col-span-3 p-2 border-r-[2px] border-black leading-tight text-center">
                  Amount/ <br /> Student head
                </div>
                <div className="col-span-2 p-2 border-r-[2px] border-black leading-tight text-center">
                  No. of Students
                </div>
                <div className="col-span-2 p-2 text-center">Total Amount</div>
              </div>

              {/* Table Rows Body */}
              <div className="min-h-[220px] relative z-10 flex flex-col">
                {data.items.map((item, index) => {
                  const lineTotal =
                    (Number(item.amountPerHead) || 0) * (Number(item.studentCount) || 0);

                  return (
                    <div
                      key={item.id || index}
                      className="grid grid-cols-12 text-xs sm:text-sm border-b border-neutral-300/80 last:border-b-0 min-h-[44px] items-stretch"
                    >
                      <div className="col-span-5 p-2 sm:p-2.5 border-r-[2px] border-black flex items-start">
                        <span>
                          {index + 1}. {item.description}
                        </span>
                      </div>
                      <div className="col-span-3 p-2 sm:p-2.5 border-r-[2px] border-black text-center flex items-center justify-center font-medium">
                        ₹{formatINR(item.amountPerHead)}
                      </div>
                      <div className="col-span-2 p-2 sm:p-2.5 border-r-[2px] border-black text-center flex items-center justify-center font-medium">
                        {item.studentCount}
                      </div>
                      <div className="col-span-2 p-2 sm:p-2.5 text-right flex items-center justify-end pr-3 font-semibold">
                        ₹{formatINR(lineTotal)}
                      </div>
                    </div>
                  );
                })}

                {/* Empty Filler Space for Authentic Layout */}
                <div className="flex-1 min-h-[80px]" />
              </div>

              {/* Table Footer Subtotal Row */}
              <div className="border-t-[2px] border-black flex justify-end z-10 relative bg-white">
                <div className="border-l-[2px] border-black px-4 py-2 text-xs sm:text-sm font-bold flex items-center gap-2">
                  <span>Total Amount :</span>
                  <span className="text-sm sm:text-base font-extrabold">
                    ₹{formatINR(totalAmount)}
                  </span>
                </div>
              </div>
            </div>

            {/* PAYMENT & LEGAL DETAILS */}
            <div className="space-y-2 mt-3">
              {/* Amount in words */}
              <div className="border-[2px] border-black px-3 py-1.5 text-xs sm:text-sm font-bold flex items-center gap-2 bg-white">
                <span className="shrink-0">Amount (In words) :</span>
                <span className="font-extrabold">{amountInWords}</span>
              </div>

              {/* Payment Method */}
              <div className="border-[2px] border-black px-3 py-1.5 text-xs sm:text-sm font-bold flex items-center gap-2 bg-white">
                <span className="shrink-0">Payment Method :</span>
                <span className="font-extrabold uppercase">{data.paymentMethod}</span>
              </div>
            </div>
          </div>

          {/* SIGNATURE FOOTER */}
          <div className="grid grid-cols-2 gap-8 pt-8 mt-6">
            {/* Customer Signature Box */}
            <div className="flex flex-col items-center justify-end">
              <div className="h-14 sm:h-16 w-full flex items-center justify-center">
                {/* Physical ink signature area */}
              </div>
              <div className="border-[2px] border-black px-6 py-1 text-xs sm:text-sm font-bold text-center w-full max-w-[240px]">
                {data.customerSignatureLabel || 'Customer Signature'}
              </div>
            </div>

            {/* Authorised Signatory Box */}
            <div className="flex flex-col items-center justify-end relative">
              <div className="h-14 sm:h-16 w-full relative flex items-center justify-center -mb-2">
                {data.signatureImage && (
                  <div className="relative w-44 h-16 pointer-events-none">
                    <Image
                      src={data.signatureImage}
                      alt="Authorised Signature"
                      fill
                      className="object-contain"
                      unoptimized
                    />
                  </div>
                )}
              </div>
              <div className="border-[2px] border-black px-6 py-1 text-xs sm:text-sm font-bold text-center w-full max-w-[240px] z-10 bg-white">
                Authorised Signatory
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }
);

InvoicePreview.displayName = 'InvoicePreview';
