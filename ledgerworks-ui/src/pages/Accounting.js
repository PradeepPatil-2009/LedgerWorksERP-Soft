import { useState } from "react";
import axios from "axios";

function Accounting() {
  const [accountName, setAccountName] = useState("");
  const [accountType, setAccountType] = useState("ASSET");

  const saveAccount = async () => {
    try {
      await axios.post("http://localhost:8080/api/accounts", {
        accountName: accountName,
        accountType: accountType,
        balance: 0
      });

      alert("Account Created ✅");

      setAccountName("");
      setAccountType("ASSET");

    } catch (error) {
      console.error(error);
      alert("Error creating account ❌");
    }
  };

  return (
    <div style={{ textAlign: "center", marginTop: "50px" }}>
      <h2>Create Account</h2>

      <input
        placeholder="Account Name"
        value={accountName}
        onChange={(e) => setAccountName(e.target.value)}
      />

      <br /><br />

      <select
        value={accountType}
        onChange={(e) => setAccountType(e.target.value)}
      >
        <option value="ASSET">ASSET</option>
        <option value="LIABILITY">LIABILITY</option>
        <option value="INCOME">INCOME</option>
        <option value="EXPENSE">EXPENSE</option>
      </select>

      <br /><br />

      <button onClick={saveAccount}>Save Account</button>
    </div>
  );
}

export default Accounting;