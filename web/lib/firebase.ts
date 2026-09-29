import { initializeApp, getApps, getApp } from "firebase/app";
import { 
  getFirestore, 
  collection, 
  addDoc, 
  getDocs, 
  deleteDoc, 
  updateDoc, 
  doc, 
  query, 
  orderBy, 
  serverTimestamp 
} from "firebase/firestore";
import { getAnalytics, isSupported } from "firebase/analytics";
import { InvoiceData } from "./types";

// User's Web App Firebase configuration
export const firebaseConfig = {
  apiKey: "AIzaSyDu4Cow7uyLC-pUlmslehzI59gK-luFNbg",
  authDomain: "robogyaan-invoice.firebaseapp.com",
  projectId: "robogyaan-invoice",
  storageBucket: "robogyaan-invoice.firebasestorage.app",
  messagingSenderId: "1053212037957",
  appId: "1:1053212037957:web:3d047a78e07543bea6242c",
  measurementId: "G-NHCDCPWQKV"
};

// Initialize Firebase safely (avoid multi-initialization in Next.js Turbopack)
export const app = getApps().length > 0 ? getApp() : initializeApp(firebaseConfig);
export const db = getFirestore(app);

// Initialize analytics safely if in browser and supported
export const initAnalytics = async () => {
  if (typeof window !== "undefined" && (await isSupported())) {
    return getAnalytics(app);
  }
  return null;
};

// History entry data contract
export interface InvoiceHistoryEntry {
  id?: string;
  promptDescription?: string;
  invoiceNo: string;
  clientName: string;
  totalAmount: number;
  invoiceData: InvoiceData;
  createdAt?: string | number | null;
}

const COLLECTION_NAME = "invoice_prompts";
const LOCAL_STORAGE_KEY = "robogyaan_invoice_history";

/**
 * Save invoice and prompt to Firestore (with local fallback)
 */
export async function saveInvoicePrompt(
  invoiceData: InvoiceData,
  promptDescription: string = "Standard RoboGyaan Invoice"
): Promise<string> {
  const totalAmount = invoiceData.items.reduce(
    (sum, item) => sum + (Number(item.amountPerHead) || 0) * (Number(item.studentCount) || 0),
    0
  );

  const entry: Omit<InvoiceHistoryEntry, "id"> = {
    promptDescription,
    invoiceNo: invoiceData.invoiceNo,
    clientName: invoiceData.billedTo.name || "Client",
    totalAmount,
    invoiceData,
    createdAt: new Date().toISOString(),
  };

  let firestoreId: string = `local_${Date.now()}`;

  try {
    const colRef = collection(db, COLLECTION_NAME);
    const docRef = await addDoc(colRef, {
      ...entry,
      serverTime: serverTimestamp(),
    });
    firestoreId = docRef.id;
  } catch (err) {
    console.warn("Firestore cloud save warning (saving to local cache as fallback):", err);
  }

  // Always mirror in localStorage for instant offline access
  if (typeof window !== "undefined") {
    try {
      const existing = getLocalHistory();
      const updated = [{ id: firestoreId, ...entry }, ...existing.filter(i => i.id !== firestoreId)];
      localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(updated.slice(0, 50)));
    } catch (e) {
      console.error("Local storage error:", e);
    }
  }

  return firestoreId;
}

/**
 * Retrieve invoice prompt history from Firestore (falling back to local cache if offline)
 */
export async function fetchInvoiceHistory(): Promise<InvoiceHistoryEntry[]> {
  try {
    const colRef = collection(db, COLLECTION_NAME);
    const q = query(colRef, orderBy("serverTime", "desc"));
    const snapshot = await getDocs(q);

    if (!snapshot.empty) {
      const cloudEntries = snapshot.docs.map((docSnap) => {
        const d = docSnap.data();
        return {
          id: docSnap.id,
          promptDescription: d.promptDescription || "Saved Invoice",
          invoiceNo: d.invoiceNo || "N/A",
          clientName: d.clientName || "Client",
          totalAmount: d.totalAmount || 0,
          invoiceData: d.invoiceData as InvoiceData,
          createdAt: d.createdAt || new Date().toISOString(),
        };
      });

      // Update local storage cache
      if (typeof window !== "undefined") {
        localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(cloudEntries));
      }
      return cloudEntries;
    }
  } catch (err) {
    console.warn("Firestore fetch error, reading from local cache:", err);
  }

  return getLocalHistory();
}

/**
 * Delete a history entry
 */
export async function deleteInvoiceHistoryEntry(id: string): Promise<void> {
  try {
    if (!id.startsWith("local_")) {
      const docRef = doc(db, COLLECTION_NAME, id);
      await deleteDoc(docRef);
    }
  } catch (err) {
    console.warn("Firestore delete warning:", err);
  }

  if (typeof window !== "undefined") {
    const existing = getLocalHistory();
    const updated = existing.filter((item) => item.id !== id);
    localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(updated));
  }
}

/**
 * Update / Modify an existing history entry in Firestore and local storage
 */
export async function updateInvoiceHistoryEntry(
  id: string,
  invoiceData: InvoiceData,
  promptDescription?: string
): Promise<boolean> {
  const totalAmount = invoiceData.items.reduce(
    (sum, item) => sum + (Number(item.amountPerHead) || 0) * (Number(item.studentCount) || 0),
    0
  );

  const updatedPayload: Record<string, any> = {
    invoiceNo: invoiceData.invoiceNo,
    clientName: invoiceData.billedTo.name || "Client",
    totalAmount,
    invoiceData,
    updatedAt: new Date().toISOString(),
  };

  if (promptDescription && promptDescription.trim().length > 0) {
    updatedPayload.promptDescription = promptDescription.trim();
  }

  try {
    if (!id.startsWith("local_")) {
      const docRef = doc(db, COLLECTION_NAME, id);
      await updateDoc(docRef, updatedPayload);
    }
  } catch (err) {
    console.warn("Firestore update error, updating local cache:", err);
  }

  if (typeof window !== "undefined") {
    const existing = getLocalHistory();
    const updated = existing.map((item) =>
      item.id === id ? { ...item, ...updatedPayload } : item
    );
    localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(updated));
  }

  return true;
}

function getLocalHistory(): InvoiceHistoryEntry[] {
  if (typeof window === "undefined") return [];
  try {
    const raw = localStorage.getItem(LOCAL_STORAGE_KEY);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}
