import React, { forwardRef, useEffect } from 'react';
import Image from 'next/image';
import { InvoiceData } from '../lib/types';
import { formatINR, numberToWordsIndian } from '../lib/numberToWordsIndian';
import { RobogyaanLogo } from './RobogyaanLogo';
import { paginateInvoiceItems, InvoicePageSlice } from '../lib/pagination';

export interface InvoicePreviewProps {
  data: InvoiceData;
  scale?: number;
  selectedPage?: number | 'all'; // 0-based page index, or 'all'
  onTotalPagesChange?: (total: number) => void;
  forceShowAllForExport?: boolean;
}

export const InvoicePreview = forwardRef<HTMLDivElement, InvoicePreviewProps>(
  (
    {
      data,
      selectedPage = 'all',
      onTotalPagesChange,
      forceShowAllForExport = false,
    },
    ref
  ) => {
    const totalAmount = data.items.reduce(
      (sum, item) => sum + (Number(item.amountPerHead) || 0) * (Number(item.studentCount) || 0),
      0
    );

    const amountInWords = numberToWordsIndian(totalAmount);
    const pages = paginateInvoiceItems(data.items);

    useEffect(() => {
      if (onTotalPagesChange) {
        onTotalPagesChange(pages.length);
      }
    }, [pages.length, onTotalPagesChange]);

    return (
      <div ref={ref} className="invoice-preview-container w-full flex flex-col items-center gap-8 py-2 sm:py-4 print:p-0 print:m-0 print:gap-0 print:block">
        {pages.map((pageSlice: InvoicePageSlice, pageIndex: number) => {
          const isVisibleOnScreen =
            forceShowAllForExport ||
            selectedPage === 'all' ||
            selectedPage === pageIndex;

          return (
            <div
              key={pageSlice.pageNumber}
              id={`invoice-page-${pageSlice.pageNumber}`}
              data-page-number={pageSlice.pageNumber}
              className={`print-invoice-page !bg-white !text-black font-sans relative shadow-[0_10px_35px_rgba(0,0,0,0.15)] border-2 border-black rounded-xl sm:rounded-2xl overflow-hidden print:rounded-none print:shadow-none print:border-none print:overflow-visible box-border w-full max-w-[760px] h-auto min-h-fit md:min-h-[1020px] md:aspect-[1/1.414] p-4 sm:p-8 flex flex-col justify-between ${
                isVisibleOnScreen ? 'flex' : 'hidden print:flex'
              }`}
              style={{
                fontFamily: 'var(--font-poppins), Poppins, sans-serif',
              }}
            >
              {/* TOP CONTENT WRAPPER */}
              <div>
                {/* 1. HEADER SECTION */}
                {pageSlice.isFirstPage ? (
                  // Full First-Page Header
                  <div>
                    <div className="flex items-start justify-between relative mb-2">
                      <div className="pt-1">
                        <RobogyaanLogo />
                      </div>

                      <div className="flex flex-col items-end gap-1.5 w-7/12 max-w-[340px]">
                        <div
                          className="bg-[#121212] text-white py-1.5 px-6 font-bold text-xs sm:text-sm tracking-wide text-right w-full"
                          style={{
                            clipPath: 'polygon(12% 0, 100% 0, 100% 100%, 0% 100%)',
                          }}
                        >
                          <span className="opacity-90">Invoice No. : </span>
                          <span className="font-extrabold">{data.invoiceNo}</span>
                        </div>

                        <div
                          className="bg-[#FFA500] text-black py-1 px-5 font-bold text-xs sm:text-sm tracking-wide text-right w-10/12"
                          style={{
                            clipPath: 'polygon(14% 0, 100% 0, 100% 100%, 0% 100%)',
                          }}
                        >
                          Registration No. -{' '}
                          <span className="font-extrabold">{data.registrationNo}</span>
                        </div>
                      </div>
                    </div>

                    <hr className="border-t-[1.5px] border-neutral-300 my-3" />

                    {/* SENDER & RECIPIENT BLOCKS */}
                    <div className="grid grid-cols-2 gap-6 my-4 text-xs sm:text-sm leading-relaxed">
                      <div>
                        <div className="inline-flex items-center whitespace-nowrap bg-[#E5E7EB] text-black px-4 py-0.5 rounded-full font-bold text-xs sm:text-sm mb-2 shadow-sm border border-neutral-300">
                          BILL To:
                        </div>
                        <div className="font-bold text-sm sm:text-base tracking-wide uppercase text-neutral-900 mt-1">
                          {data.billedTo.name || 'CLIENT / SCHOOL NAME'}
                        </div>
                        <div className="text-neutral-700">{data.billedTo.address}</div>
                        <div className="text-neutral-700 font-medium">
                          {data.billedTo.pinState}
                        </div>
                      </div>

                      <div>
                        <div className="inline-flex items-center whitespace-nowrap bg-[#E5E7EB] text-black px-4 py-0.5 rounded-full font-bold text-xs sm:text-sm mb-2 shadow-sm border border-neutral-300">
                          From:
                        </div>
                        <div className="font-bold text-sm sm:text-base tracking-wide uppercase text-neutral-900 mt-1">
                          {data.from.company}
                        </div>
                        <div className="text-neutral-700">{data.from.address}</div>
                        <div className="text-neutral-700 font-medium">
                          {data.from.cityPinState}
                        </div>
                      </div>
                    </div>

                    {/* META SUMMARY BAR */}
                    <div className="grid grid-cols-4 gap-2 my-4 text-center">
                      <div className="bg-[#FFB800] text-white p-2 rounded-sm border border-neutral-300/40 shadow-sm flex flex-col justify-center">
                        <span className="text-[10px] sm:text-xs font-black opacity-95 block leading-tight uppercase tracking-wider">
                          Invoice No.
                        </span>
                        <span className="text-xs sm:text-sm font-extrabold truncate">
                          {data.invoiceNo}
                        </span>
                      </div>

                      <div className="bg-[#FFB800] text-white p-2 rounded-sm border border-neutral-300/40 shadow-sm flex flex-col justify-center">
                        <span className="text-[10px] sm:text-xs font-black opacity-95 block leading-tight uppercase tracking-wider">
                          Issue Date:
                        </span>
                        <span className="text-xs sm:text-sm font-extrabold">
                          {data.issueDate}
                        </span>
                      </div>

                      <div className="bg-[#FFB800] text-white p-2 rounded-sm border border-neutral-300/40 shadow-sm flex flex-col justify-center">
                        <span className="text-[10px] sm:text-xs font-black opacity-95 block leading-tight uppercase tracking-wider">
                          Due Date:
                        </span>
                        <span className="text-xs sm:text-sm font-extrabold">
                          {data.dueDate}
                        </span>
                      </div>

                      <div className="bg-[#505050] text-white p-2 rounded-sm border border-black shadow-sm flex flex-col justify-center">
                        <span className="text-[10px] sm:text-xs font-black opacity-95 block leading-tight uppercase tracking-wider">
                          Total Due :
                        </span>
                        <span className="text-xs sm:text-sm font-extrabold">
                          ₹ {formatINR(totalAmount)}/-
                        </span>
                      </div>
                    </div>
                  </div>
                ) : (
                  // Continuation Page Header
                  <div>
                    <div className="flex items-start justify-between relative mb-2">
                      <div className="pt-1">
                        <RobogyaanLogo />
                      </div>

                      <div className="flex flex-col items-end gap-1.5 w-7/12 max-w-[340px]">
                        <div
                          className="bg-[#121212] text-white py-1.5 px-6 font-bold text-xs sm:text-sm tracking-wide text-right w-full whitespace-nowrap"
                          style={{
                            clipPath: 'polygon(12% 0, 100% 0, 100% 100%, 0% 100%)',
                          }}
                        >
                          <span className="opacity-90">Invoice No. : </span>
                          <span className="font-extrabold">{data.invoiceNo}</span>
                        </div>

                        <div
                          className="bg-[#FFA500] text-black py-1 px-5 font-bold text-xs sm:text-sm tracking-wide text-right w-10/12 whitespace-nowrap"
                          style={{
                            clipPath: 'polygon(14% 0, 100% 0, 100% 100%, 0% 100%)',
                          }}
                        >
                          <span className="font-extrabold">PAGE {pageSlice.pageNumber} OF {pageSlice.totalPages}</span>
                        </div>

                        <div className="text-[11px] sm:text-xs font-bold uppercase tracking-wider text-neutral-600 text-right pr-1 truncate max-w-full">
                          {data.billedTo.name}
                        </div>
                      </div>
                    </div>

                    <hr className="border-t-[1.5px] border-neutral-300 my-3" />
                  </div>
                )}

                {/* 2. ITEMIZED BILLING TABLE */}
                <div className="border-[2px] border-black my-3 relative overflow-hidden bg-white">
                  {/* Watermark */}
                  {data.showWatermark && (
                    <div className="absolute inset-0 flex items-center justify-center pointer-events-none z-0">
                      <RobogyaanLogo isWatermark={true} />
                    </div>
                  )}

                  {/* Table Header */}
                  <div className="grid grid-cols-12 border-b-[2px] border-black bg-white/95 text-xs sm:text-sm font-black uppercase tracking-wider text-center z-10 relative">
                    <div className="col-span-5 p-2 border-r-[2px] border-black text-center">
                      Item
                    </div>
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
                    {pageSlice.items.map((item, itemIdx) => {
                      const absoluteIndex = pageSlice.itemStartIndex + itemIdx + 1;
                      const lineTotal =
                        (Number(item.amountPerHead) || 0) * (Number(item.studentCount) || 0);

                      return (
                        <div
                          key={item.id || itemIdx}
                          className="grid grid-cols-12 text-xs sm:text-sm min-h-[42px] items-stretch"
                        >
                          <div className="col-span-5 p-2 sm:p-2.5 border-r-[2px] border-black flex items-start">
                            <span>
                              {absoluteIndex}. {item.description}
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

                    {/* Empty Filler Space with continuous vertical borders */}
                    <div className="flex-1 grid grid-cols-12 min-h-[40px] sm:min-h-[80px]">
                      <div className="col-span-5 border-r-[2px] border-black h-full" />
                      <div className="col-span-3 border-r-[2px] border-black h-full" />
                      <div className="col-span-2 border-r-[2px] border-black h-full" />
                      <div className="col-span-2 h-full" />
                    </div>
                  </div>

                  {/* Table Footer: Subtotal on Last Page OR Continued Indicator */}
                  {pageSlice.showSummaryAndSignatures ? (
                    <div className="border-t-[2px] border-black grid grid-cols-12 z-10 relative bg-white text-xs sm:text-sm font-bold">
                      <div className="col-span-8" />
                      {/* Box 1: In the column of 'No. of Students' */}
                      <div className="col-span-2 border-l-[2px] border-r-[2px] border-black p-2 flex items-center justify-end pr-2 text-right">
                        <span className="font-black text-black">Total Amount :</span>
                      </div>
                      {/* Box 2: In the column of 'Total Amount' */}
                      <div className="col-span-2 p-2 sm:p-2.5 flex items-center justify-end pr-3 text-right">
                        <span className="text-sm sm:text-base font-black text-black">
                          ₹{formatINR(totalAmount)}
                        </span>
                      </div>
                    </div>
                  ) : (
                    <div className="border-t-[2px] border-black px-4 py-1.5 z-10 relative bg-neutral-50 flex justify-between items-center text-xs font-bold text-neutral-600">
                      <span>Items continued on next page...</span>
                      <span className="font-black text-black">
                        Continued on Page {pageSlice.pageNumber + 1} &rarr;
                      </span>
                    </div>
                  )}
                </div>

                {/* 3. PAYMENT & LEGAL DETAILS (Rendered on final page with signatures) */}
                {pageSlice.showSummaryAndSignatures && (
                  <div className="space-y-2 mt-3">
                    <div className="border-[2px] border-black px-3 py-1.5 text-xs sm:text-sm font-bold flex items-center gap-2 bg-white">
                      <span className="shrink-0 font-black">Amount (In words) :</span>
                      <span className="font-extrabold">{amountInWords}</span>
                    </div>

                    <div className="border-[2px] border-black px-3 py-1.5 text-xs sm:text-sm font-bold flex items-center gap-2 bg-white">
                      <span className="shrink-0 font-black">Payment Method :</span>
                      <span className="font-black uppercase">{data.paymentMethod}</span>
                    </div>
                  </div>
                )}
              </div>

              {/* BOTTOM FOOTER WRAPPER */}
              <div>
                {/* 4. SIGNATURES (Rendered on final page) */}
                {pageSlice.showSummaryAndSignatures ? (
                  <div className="grid grid-cols-2 gap-4 sm:gap-8 pt-4 sm:pt-6 mt-2 pb-2">
                    <div className="flex flex-col items-center justify-end">
                      <div className="h-10 sm:h-12 w-full flex items-center justify-center" />
                      <div className="border-[2px] border-black px-2 sm:px-6 py-1 text-[11px] sm:text-sm font-black text-center w-full max-w-[200px] sm:max-w-[240px]">
                        {data.customerSignatureLabel || 'Customer Signature'}
                      </div>
                    </div>

                    <div className="flex flex-col items-center justify-end relative">
                      <div className="h-10 sm:h-12 w-full relative flex items-center justify-center -mb-2">
                        {data.signatureImage && (
                          <div className="relative w-28 sm:w-40 h-10 sm:h-12 pointer-events-none">
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
                      <div className="border-[2px] border-black px-2 sm:px-6 py-1 text-[11px] sm:text-sm font-black text-center w-full max-w-[200px] sm:max-w-[240px] z-10 bg-white">
                        Authorised Signatory
                      </div>
                    </div>
                  </div>
                ) : null}

                {/* Page Number Indicator Footer */}
                <div className="border-t border-neutral-300 pt-2 mt-2 flex items-center justify-between text-[10px] sm:text-[11px] font-bold text-neutral-500">
                  <span>
                    RoboGyaan Invoice Suite &bull; {data.invoiceNo}
                  </span>
                  <span className="bg-black text-white px-2 py-0.5 rounded font-black text-[10px]">
                    PAGE {pageSlice.pageNumber} OF {pageSlice.totalPages}
                  </span>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    );
  }
);

InvoicePreview.displayName = 'InvoicePreview';
