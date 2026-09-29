import { InvoiceItem } from './types';

export interface InvoicePageSlice {
  pageNumber: number;
  totalPages: number;
  items: InvoiceItem[];
  itemStartIndex: number; // 0-based index of the first item on this page
  isFirstPage: boolean;
  isLastPage: boolean;
  showSummaryAndSignatures: boolean;
}

// Layout constraints for clean A4 rendering:
export const FIRST_PAGE_SINGLE_CAPACITY = 7;     // Max items on Page 1 if only 1 page
export const FIRST_PAGE_MULTI_CAPACITY = 8;      // Max items on Page 1 if multi-page (no signatures on p1)
export const CONTINUATION_PAGE_MAX_ITEMS = 15;   // Max items on a middle continuation page
export const FINAL_PAGE_WITH_FOOTER_MAX = 11;    // Max items on final page with summary & signatures

/**
 * Deterministically splits an array of invoice items into A4 page slices.
 * Ensures table rows, summary boxes, and signature blocks never collide or clip.
 */
export function paginateInvoiceItems(items: InvoiceItem[]): InvoicePageSlice[] {
  const total = items.length;

  // Single-page invoice
  if (total <= FIRST_PAGE_SINGLE_CAPACITY) {
    return [
      {
        pageNumber: 1,
        totalPages: 1,
        items,
        itemStartIndex: 0,
        isFirstPage: true,
        isLastPage: true,
        showSummaryAndSignatures: true,
      },
    ];
  }

  // Multi-page invoice
  const pages: InvoicePageSlice[] = [];
  let currentIndex = 0;

  // Page 1:
  const p1Count = Math.min(total, FIRST_PAGE_MULTI_CAPACITY);
  pages.push({
    pageNumber: 1,
    totalPages: 1, // updated below
    items: items.slice(0, p1Count),
    itemStartIndex: 0,
    isFirstPage: true,
    isLastPage: false,
    showSummaryAndSignatures: false,
  });
  currentIndex += p1Count;

  let pageNum = 2;
  while (currentIndex < total) {
    const remaining = total - currentIndex;

    if (remaining <= FINAL_PAGE_WITH_FOOTER_MAX) {
      // Remaining items fit on the final page together with summary and signatures
      pages.push({
        pageNumber: pageNum,
        totalPages: pageNum,
        items: items.slice(currentIndex, currentIndex + remaining),
        itemStartIndex: currentIndex,
        isFirstPage: false,
        isLastPage: true,
        showSummaryAndSignatures: true,
      });
      currentIndex += remaining;
      pageNum++;
    } else if (remaining <= CONTINUATION_PAGE_MAX_ITEMS) {
      // Too many items for a single final page with footer.
      // Split remaining items: take some on this continuation page, and leave the rest for final page with footer.
      const thisPageCount = Math.max(1, remaining - FINAL_PAGE_WITH_FOOTER_MAX);
      pages.push({
        pageNumber: pageNum,
        totalPages: pageNum,
        items: items.slice(currentIndex, currentIndex + thisPageCount),
        itemStartIndex: currentIndex,
        isFirstPage: false,
        isLastPage: false,
        showSummaryAndSignatures: false,
      });
      currentIndex += thisPageCount;
      pageNum++;
    } else {
      // Full continuation page
      pages.push({
        pageNumber: pageNum,
        totalPages: pageNum,
        items: items.slice(currentIndex, currentIndex + CONTINUATION_PAGE_MAX_ITEMS),
        itemStartIndex: currentIndex,
        isFirstPage: false,
        isLastPage: false,
        showSummaryAndSignatures: false,
      });
      currentIndex += CONTINUATION_PAGE_MAX_ITEMS;
      pageNum++;
    }
  }

  // Finalize totalPages count across all slices
  const totalPages = pages.length;
  pages.forEach((p, idx) => {
    p.totalPages = totalPages;
    p.isLastPage = idx === totalPages - 1;
  });

  return pages;
}
