import { useState } from "react";

import {
  saveInvoicePayment
} from "../api/api";

function InvoicePayment() {

  const [invoiceNumber,
    setInvoiceNumber] = useState("");

  const [amount,
    setAmount] = useState("");

  const [paymentMode,
    setPaymentMode] =
      useState("CASH");

  const [reference,
    setReference] =
      useState("");

  const resetForm = () => {

    setInvoiceNumber("");

    setAmount("");

    setPaymentMode("CASH");

    setReference("");
  };

  const save = async () => {

    if (
      !invoiceNumber ||
      !amount
    ) {

      alert(
        "Invoice Number and Amount are required"
      );

      return;
    }

    try {

      await saveInvoicePayment({

        invoiceNumber: invoiceNumber.trim(),

        amount,

        paymentMode,

        reference,
      });

      alert(
        "Payment Saved Successfully"
      );

      resetForm();

    } catch (err) {

      console.error(err);

      alert(
        "Error saving payment"
      );
    }
  };

  return (

    <div>

      <h2>Invoice Payment</h2>

      <input
        placeholder="Invoice Number"
        value={invoiceNumber}
        onChange={(e) =>
          setInvoiceNumber(
            e.target.value
          )
        }
      />

      <input
        placeholder="Amount"
        value={amount}
        onChange={(e) =>
          setAmount(
            e.target.value
          )
        }
      />

      <select
        value={paymentMode}
        onChange={(e) =>
          setPaymentMode(
            e.target.value
          )
        }
      >

        <option value="CASH">
          CASH
        </option>

        <option value="BANK">
          BANK
        </option>

      </select>

      <input
        placeholder="Reference"
        value={reference}
        onChange={(e) =>
          setReference(
            e.target.value
          )
        }
      />

      <button onClick={save}>
        Pay
      </button>

    </div>
  );
}

export default InvoicePayment;