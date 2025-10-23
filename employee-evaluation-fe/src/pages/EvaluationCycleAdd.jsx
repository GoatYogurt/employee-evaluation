import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./dashboard.css";

function EvaluationCycleAdd() {
  const navigate = useNavigate();
  const [cycle, setCycle] = useState({
    name: "",
    description: "",
    startDate: "",
    endDate: "",
    status: "ACTIVE", 
  });
  const [error, setError] = useState("");

  // ✅ enum status list
  const statusOptions = [
    "ACTIVE",
    "CANCELLED",
    "COMPLETED",
    "NOT_STARTED",
  ];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCycle((prev) => ({ ...prev, [name]: value }));
  };

    const formatDate = (dateStr) => {
    if (!dateStr) return "";
    const [year, month, day] = dateStr.split("-");
    return `${day}/${month}/${year}`;
    };

    const handleSubmit = async (e) => {
    e.preventDefault();
    const formattedData = {
        ...cycle,
        startDate: formatDate(cycle.startDate),
        endDate: formatDate(cycle.endDate),
    };

  try {
    const res = await fetch("http://localhost:8080/api/evaluation-cycles", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
      body: JSON.stringify(formattedData),
    });

    if (!res.ok) {
      const text = await res.text();
      throw new Error(`Lỗi API: ${res.status} - ${text}`);
    }

    alert("Thêm kỳ đánh giá thành công!");
    navigate("/evaluation-cycle-list");
  } catch (err) {
    console.error("❌ Lỗi khi thêm kỳ đánh giá:", err);
    alert("Không thể thêm kỳ đánh giá");
  }
};


  return (
    <div style={{ maxWidth: 600, margin: "0 auto", padding: 20 }}>
      <h2>Thêm kỳ đánh giá mới</h2>
      {error && <div style={{ color: "red", marginBottom: 10 }}>{error}</div>}

      <form onSubmit={handleSubmit}>
        <label>Tên kỳ đánh giá:</label>
        <input
          type="text"
          name="name"
          required
          value={cycle.name}
          onChange={handleChange}
        />

        <label>Mô tả:</label>
        <textarea
          name="description"
          required
          value={cycle.description}
          onChange={handleChange}
          rows="3"
        ></textarea>

        <label>Ngày bắt đầu:</label>
        <input
          type="date"
          name="startDate"
          required
          value={cycle.startDate}
          onChange={handleChange}
        />

        <label>Ngày kết thúc:</label>
        <input
          type="date"
          name="endDate"
          required
          value={cycle.endDate}
          onChange={handleChange}
        />

        <label>Trạng thái:</label>
        <select
          name="status"
          value={cycle.status}
          onChange={handleChange}
          required
        >
          {statusOptions.map((s) => (
            <option key={s} value={s}>
              {s}
            </option>
          ))}
        </select>

        <div style={{ marginTop: 20 }}>
          <button type="submit" className="btn btn-primary" style={{ marginRight: 10 }}>
            Thêm
          </button>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => navigate("/evaluation-cycle-list")}
          >
            Hủy
          </button>
        </div>
      </form>
    </div>
  );
}

export default EvaluationCycleAdd;
