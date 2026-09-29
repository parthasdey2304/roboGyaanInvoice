import { InvoiceData } from './types';

export const defaultInvoiceData: InvoiceData = {
  invoiceNo: 'RG-JPS-2608-001',
  registrationNo: '273744',
  issueDate: '01/09/2026',
  dueDate: '08/09/2026',
  billedTo: {
    name: 'JYOTIRMOY PUBLIC SCHOOL',
    address: 'Tematha , Sonarpur',
    pinState: 'Pin:743330 , West Bengal',
  },
  from: {
    company: 'ROBOGYAAN',
    address: 'Dakshin Gobindopur , Sonarpur',
    cityPinState: 'Kolkata - 700145 , West Bengal',
  },
  items: [
    {
      id: 'item-1',
      description: 'Robogyaan ECA Programme (Premium)',
      amountPerHead: 200,
      studentCount: 136,
    },
  ],
  paymentMethod: 'CASH',
  authoriserName: 'Suman Mondal',
  customerSignatureLabel: 'Customer Signature',
  signatureImage: '/signature.png',
  showWatermark: true,
};
