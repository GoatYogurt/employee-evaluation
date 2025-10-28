import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./dashboard.css";
import { useContext } from "react";
import { ToastContext } from "../contexts/ToastProvider";

function EvaluationCycleAdd() {
  const navigate = useNavigate();
  const { toast } = useContext(ToastContext);


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

    toast.success("Thêm kỳ đánh giá thành công!");
    navigate("/evaluation-cycle-list");
  } catch (err) {
    console.error("❌ Lỗi khi thêm kỳ đánh giá:", err);
    toast.error("Không thể thêm kỳ đánh giá");
  }
};


  return (
     <div className="evaluation-add-wrapper">
      <div className="evaluation-add-container">
        <h2>Thêm kỳ đánh giá mới</h2>
        {error && <div className="evaluation-add-error">{error}</div>}

        <form onSubmit={handleSubmit} className="evaluation-add-form">
          <div>
            <label>Tên kỳ đánh giá:</label>
            <input
              type="text"
              name="name"
              required
              value={cycle.name}
              onChange={handleChange}
            />
          </div>

          <div>
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
          </div>

          <div className="date-group">
            <div>
              <label>Ngày bắt đầu:</label>
              <input
                type="date"
                name="startDate"
                required
                value={cycle.startDate}
                onChange={handleChange}
              />
            </div>
            <div>
              <label>Ngày kết thúc:</label>
              <input
                type="date"
                name="endDate"
                required
                value={cycle.endDate}
                onChange={handleChange}
              />
            </div>
          </div>

          <div style={{ gridColumn: "span 2" }}>
            <label>Mô tả:</label>
            <textarea
              name="description"
              required
              value={cycle.description}
              onChange={handleChange}
              rows="3"
            ></textarea>
          </div>

          <div className="evaluation-add-actions">
            <button type="submit" className="btn-primary">
              Thêm kỳ đánh giá
            </button>
            <button
              type="button"
              className="btn-secondary"
              onClick={() => navigate("/evaluation-cycle-list")}
            >
              Hủy
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default EvaluationCycleAdd;