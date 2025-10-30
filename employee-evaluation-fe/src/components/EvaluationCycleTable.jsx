import React, { useEffect, useState } from "react";
import "../index.css";
import { useNavigate, Link } from "react-router-dom";
import { useContext } from "react";
import { ToastContext } from "../contexts/ToastProvider";

const EvaluationCycleTable = () => {
  const [cycles, setCycles] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(0); // BE phân trang bắt đầu từ 0
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [itemsPerPage] = useState(10);

  const navigate = useNavigate();
  const { toast } = useContext(ToastContext);

  const [showViewModal, setShowViewModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedCycle, setSelectedCycle] = useState(null);
  const [deleteMessage, setDeleteMessage] = useState("");

  // Gọi API mỗi khi đổi trang
  useEffect(() => {
    fetchEvaluationCycles(currentPage);
  }, [currentPage]);

  // ---------------- FETCH PHÂN TRANG TỪ BE ----------------
  const fetchEvaluationCycles = async (page = 0) => {
    try {
      const res = await fetch(`http://localhost:8080/api/evaluation-cycles?page=${page}&size=${itemsPerPage}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "application/json",
        },
      });

      if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
      const data = await res.json();

      const pageData = data.data || data; // tùy theo cấu trúc BE

      const convertDate = (d) => {
        if (!d) return "";
        const [day, month, year] = d.split("/");
        return `${year}-${month}-${day}`;
      };

      setCycles(
        (pageData.content || []).map((c) => ({
          id: c.id,
          name: c.name,
          description: c.description,
          status: c.status,
          startDate: convertDate(c.startDate),
          endDate: convertDate(c.endDate),
          projects: c.projects || [],
          createdAt: c.createdAt,
          updatedAt: c.updatedAt,
          createdBy: c.createdBy,
          updatedBy: c.updatedBy,
        }))
      );

      setTotalPages(pageData.totalPages || 1);
      setTotalElements(pageData.totalElements || 0);
    } catch (error) {
      console.error("Fetch evaluation cycles error:", error);
      toast.error("Không thể tải danh sách kỳ đánh giá!");
    }
  };

  // ----------------- FILTER CLIENT-SIDE (chỉ lọc trên trang hiện tại) -----------------
  const filteredCycles = cycles.filter((c) =>
    c.name?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // ----------------- CHỨC NĂNG VIEW / EDIT / DELETE -----------------
  const handleView = (cycle) => {
    setSelectedCycle(cycle);
    setShowViewModal(true);
  };

  const handleEdit = (cycle) => {
    setSelectedCycle({ ...cycle });
    setShowEditModal(true);
  };

  const handleDelete = async (id) => {
    try {
      const res = await fetch(`http://localhost:8080/api/evaluation-cycles/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "application/json",
        },
      });

      if (!res.ok) throw new Error("Delete failed");

      toast.success("Xóa kỳ đánh giá thành công!");
      fetchEvaluationCycles(currentPage); // reload trang hiện tại
    } catch (err) {
      console.error(err);
      toast.error("Xóa kỳ đánh giá thất bại!");
    }
  };

  const handleEditConfirm = async () => {
    try {
      const convertToDDMMYYYY = (dateStr) => {
        if (!dateStr) return null;
        const [year, month, day] = dateStr.split("-");
        return `${day}/${month}/${year}`;
      };

      const payload = {
        name: selectedCycle.name,
        description: selectedCycle.description,
        status: selectedCycle.status,
        startDate: convertToDDMMYYYY(selectedCycle.startDate),
        endDate: convertToDDMMYYYY(selectedCycle.endDate),
      };

      const res = await fetch(
        `http://localhost:8080/api/evaluation-cycles/${selectedCycle.id}`,
        {
          method: "PATCH",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify(payload),
        }
      );

      if (!res.ok) throw new Error("Update failed");
      toast.success("Sửa kỳ đánh giá thành công!");
      setShowEditModal(false);
      fetchEvaluationCycles(currentPage);
    } catch (err) {
      console.error(err);
      toast.error("Có lỗi khi sửa kỳ đánh giá!");
    }
  };

  const handleViewProjects = (cycleId) => {
    navigate(`/project-list?evaluationCycleId=${cycleId}`);
  };

  const formatDateTime = (dateString) => {
    if (!dateString) return "-";
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");
    return `${hours}:${minutes} - ${day}/${month}/${year}`;
  };

  // ----------------- RENDER -----------------
  return (
    <div>
      <div className="content-header">
        <h1 className="header-title">Quản lý kỳ đánh giá</h1>
        <div className="header-actions">
          <Link to="/evaluation-cycle-add">
            <button className="btn btn-primary">
              <i className="fas fa-plus"></i> Thêm kỳ đánh giá
            </button>
          </Link>
        </div>
      </div>

      <div className="excel-container">
        <div className="table-header">
          <h3 className="table-title">Danh sách kỳ đánh giá</h3>
          <div className="table-controls">
            <input
              type="text"
              placeholder="Tìm kiếm tên kỳ đánh giá..."
              className="search-box"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            <button className="btn btn-warning" onClick={() => fetchEvaluationCycles(currentPage)}>
              <i className="fas fa-sync-alt"></i> Làm mới
            </button>
          </div>
        </div>

        <table className="excel-table" style={{ width: "100%", tableLayout: "fixed" }}>
          <thead>
            <tr>
              <th style={{ width: "2%" }}>STT</th>
              <th style={{ width: "5%" }}>Tên kỳ đánh giá</th>
              <th style={{ width: "5%" }}>Ngày bắt đầu</th>
              <th style={{ width: "5%" }}>Ngày kết thúc</th>
              <th style={{ width: "5%" }}>Trạng thái</th>
              <th style={{ width: "10%" }}>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {filteredCycles.length > 0 ? (
              filteredCycles.map((c, index) => (
                <tr key={c.id}>
                  <td>{index + 1 + currentPage * itemsPerPage}</td>
                  <td>{c.name}</td>
                  <td>{c.startDate}</td>
                  <td>{c.endDate}</td>
                  <td>{c.status}</td>
                  <td>
                    <div className="action-buttons">
                      <button
                        className="btn btn-sm btn-edit"
                        title="Sửa"
                        onClick={() => handleEdit(c)}
                      >
                        <i className="fas fa-edit"></i>
                      </button>
                      <button
                        className="btn btn-sm btn-delete"
                        title="Xóa"
                        onClick={() => handleDelete(c.id)}
                      >
                        <i className="fas fa-trash"></i>
                      </button>
                      <button
                        className="btn btn-sm btn-view"
                        title="Xem chi tiết"
                        onClick={() => handleView(c)}
                      >
                        <i className="fas fa-eye"></i>
                      </button>
                      <button
                        className="btn btn-sm btn-primary"
                        title="Xem dự án"
                        onClick={() => handleViewProjects(c.id)}
                      >
                        <i className="fas fa-folder-open"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="6" style={{ textAlign: "center", padding: "20px" }}>
                  Không tìm thấy kỳ đánh giá nào.
                </td>
              </tr>
            )}
          </tbody>
        </table>

        {/* PAGINATION */}
        <div className="pagination-container">
          <div className="pagination-info">
            Trang {currentPage + 1}/{totalPages} — Tổng {totalElements} kỳ đánh giá
          </div>
          <div className="pagination-controls">
            <button
              className="pagination-btn"
              onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 0))}
              disabled={currentPage === 0}
            >
              ‹ Trước
            </button>
            {Array.from({ length: totalPages }, (_, i) => (
              <button
                key={i}
                className={`pagination-btn ${currentPage === i ? "active" : ""}`}
                onClick={() => setCurrentPage(i)}
              >
                {i + 1}
              </button>
            ))}
            <button
              className="pagination-btn"
              onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages - 1))}
              disabled={currentPage === totalPages - 1}
            >
              Sau ›
            </button>
          </div>
        </div>
      </div>

      {/* Modal sửa và xem giữ nguyên y như cũ */}
      {showEditModal && selectedCycle && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Sửa kỳ đánh giá</h3>
            <div className="form-group">
              <label>Tên kỳ</label>
              <input
                type="text"
                value={selectedCycle.name || ""}
                onChange={(e) =>
                  setSelectedCycle({ ...selectedCycle, name: e.target.value })
                }
              />
            </div>
            <div className="form-group">
              <label>Mô tả</label>
              <input
                type="text"
                value={selectedCycle.description || ""}
                onChange={(e) =>
                  setSelectedCycle({ ...selectedCycle, description: e.target.value })
                }
              />
            </div>
            <div className="form-group">
              <label>Trạng thái</label>
              <select
                value={selectedCycle.status || "ACTIVE"}
                onChange={(e) =>
                  setSelectedCycle({ ...selectedCycle, status: e.target.value })
                }
              >
                <option value="ACTIVE">ACTIVE</option>
                <option value="CANCELLED">CANCELLED</option>
                <option value="COMPLETED">COMPLETED</option>
                <option value="NOT_STARTED">NOT_STARTED</option>
              </select>
            </div>
            <div className="form-group">
              <label>Ngày bắt đầu</label>
              <input
                type="date"
                value={selectedCycle.startDate || ""}
                onChange={(e) =>
                  setSelectedCycle({ ...selectedCycle, startDate: e.target.value })
                }
              />
            </div>
            <div className="form-group">
              <label>Ngày kết thúc</label>
              <input
                type="date"
                value={selectedCycle.endDate || ""}
                onChange={(e) =>
                  setSelectedCycle({ ...selectedCycle, endDate: e.target.value })
                }
              />
            </div>
            <div className="modal-actions">
              <button className="btn btn-primary" onClick={handleEditConfirm}>
                Xác nhận
              </button>
              <button
                className="btn btn-secondary"
                onClick={() => setShowEditModal(false)}
              >
                Hủy
              </button>
            </div>
          </div>
        </div>
      )}

      {showViewModal && selectedCycle && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Thông tin kỳ đánh giá</h3>
            <table className="excel-table">
              <tbody>
                <tr><td><b>Tên kỳ</b></td><td>{selectedCycle.name}</td></tr>
                <tr><td><b>Mô tả</b></td><td>{selectedCycle.description}</td></tr>
                <tr><td><b>Trạng thái</b></td><td>{selectedCycle.status}</td></tr>
                <tr><td><b>Ngày bắt đầu</b></td><td>{selectedCycle.startDate}</td></tr>
                <tr><td><b>Ngày kết thúc</b></td><td>{selectedCycle.endDate}</td></tr>
                <tr><td><b>Ngày tạo</b></td><td>{formatDateTime(selectedCycle.createdAt)}</td></tr>
                <tr><td><b>Ngày cập nhật</b></td><td>{formatDateTime(selectedCycle.updatedAt)}</td></tr>
                <tr><td><b>Người tạo</b></td><td>{selectedCycle.createdBy}</td></tr>
                <tr><td><b>Người cập nhật</b></td><td>{selectedCycle.updatedBy}</td></tr>
              </tbody>
            </table>
            <button className="btn btn-secondary" onClick={() => setShowViewModal(false)}>
              Đóng
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default EvaluationCycleTable;
