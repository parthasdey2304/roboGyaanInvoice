export type PaymentMethod = 'CASH' | 'UPI' | 'CHEQUE' | 'BANK DRAFT' | 'NEFT';

export interface InvoiceItem {
  id: string;
  description: string;
  amountPerHead: number;
  studentCount: number;
  totalAmount?: number;
}

export interface BilledParty {
  name: string;
  address: string;
  pinState: string;
}

export interface SenderParty {
  company: string;
  address: string;
  cityPinState: string;
}

export interface InvoiceData {
  invoiceNo: string;
  registrationNo: string;
  issueDate: string;
  dueDate: string;
  billedTo: BilledParty;
  from: SenderParty;
  items: InvoiceItem[];
  paymentMethod: PaymentMethod;
  authoriserName: string;
  customerSignatureLabel: string;
  signatureImage: string;
  showWatermark: boolean;
}
