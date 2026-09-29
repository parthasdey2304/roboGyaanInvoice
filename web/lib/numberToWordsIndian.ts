/**
 * Converts any non-negative integer up to 99,99,99,999 into Indian English words.
 * Formats: Crores, Lakhs, Thousands, Hundreds.
 * Example: 27200 -> "Twenty Seven Thousand Two Hundred Only"
 * Example: 150500 -> "One Lakh Fifty Thousand Five Hundred Only"
 */
export function numberToWordsIndian(amount: number): string {
  if (isNaN(amount) || amount === null || amount === undefined) {
    return "Zero Only";
  }

  const intVal = Math.floor(Math.abs(amount));
  if (intVal === 0) {
    return "Zero Only";
  }

  const units = [
    "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
    "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
    "Seventeen", "Eighteen", "Nineteen"
  ];

  const tens = [
    "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
  ];

  function convertTwoDigits(n: number): string {
    if (n === 0) return "";
    if (n < 20) return units[n];
    const t = Math.floor(n / 10);
    const u = n % 10;
    return (tens[t] + (u > 0 ? " " + units[u] : "")).trim();
  }

  function convertThreeDigits(n: number): string {
    const h = Math.floor(n / 100);
    const r = n % 100;
    const parts: string[] = [];
    if (h > 0) {
      parts.push(units[h] + " Hundred");
    }
    if (r > 0) {
      parts.push(convertTwoDigits(r));
    }
    return parts.join(" ");
  }

  const crore = Math.floor(intVal / 10000000);
  const lakh = Math.floor((intVal % 10000000) / 100000);
  const thousand = Math.floor((intVal % 100000) / 1000);
  const remainder = intVal % 1000;

  const resultParts: string[] = [];

  if (crore > 0) {
    resultParts.push(convertTwoDigits(crore) + " Crore");
  }
  if (lakh > 0) {
    resultParts.push(convertTwoDigits(lakh) + " Lakh");
  }
  if (thousand > 0) {
    resultParts.push(convertTwoDigits(thousand) + " Thousand");
  }
  if (remainder > 0) {
    resultParts.push(convertThreeDigits(remainder));
  }

  return (resultParts.join(" ") + " Only").trim();
}

export function formatINR(val: number): string {
  if (isNaN(val)) return "0.00";
  return new Intl.NumberFormat('en-IN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(val);
}
