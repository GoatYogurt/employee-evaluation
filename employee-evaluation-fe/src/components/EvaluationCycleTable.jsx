import React, { useEffect, useState } from "react";
import "../index.css";
import { useNavigate, Link } from "react-router-dom";


const EvaluationCycleTable = () => {
  const [cycles, setCycles] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);
  const navigate = useNavigate();

  const [showViewModal, setShowViewModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedCycle, setSelectedCycle] = useState(null);
  const [deleteMessage, setDeleteMessage] = useState("");

  useEffect(() => {
    fetchEvaluationCycles();
  }, []);

  // ✅ Fetch danh sách kỳ đánh giá
  const fetchEvaluationCycles = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/evaluation-cycles", {
        method: "GET",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "application/json",
        },
      });

      if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
      const data = await res.json();

      let cycleList = [];
      if (Array.isArray(data.content)) cycleList = data.content;
      else if (Array.isArray(data.data?.content)) cycleList = data.data.content;
      else if (Array.isArray(data.data)) cycleList = data.data;
      else console.error("Unexpected API structure:", data);

      // 🔧 Chuyển LocalDate dd/MM/yyyy → yyyy-MM-dd để hiển thị đúng trong input type="date"
      const convertDate = (d) => {
        if (!d) return "";
        const [day, month, year] = d.split("/");
        return `${year}-${month}-${day}`;
      };

      setCycles(
        cycleList.map((c) => ({
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
    } catch (error) {
      console.error("Fetch evaluation cycles error:", error);
    }
  };

  // 🔍 Tìm kiếm
  const filteredCycles = cycles.filter((c) =>
    c.name?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // 📄 Phân trang
  const totalPages = Math.ceil(filteredCycles.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentCycles = filteredCycles.slice(startIndex, endIndex);

  const handleView = (cycle) => {
    setSelectedCycle(cycle);
    setShowViewModal(true);
  };

  const handleEdit = (cycle) => {
    setSelectedCycle({ ...cycle }); // copy để không ảnh hưởng trực tiếp
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

      setCycles((prev) => prev.filter((c) => c.id !== id));
      setDeleteMessage("✅ Xóa kỳ đánh giá thành công!");
    } catch (err) {
      console.error(err);
      setDeleteMessage("❌ Xóa kỳ đánh giá thất bại!");
    }
    setShowDeleteModal(true);
  };

  // ✅ Khi xác nhận sửa
  const handleEditConfirm = async () => {
    try {
      // Chuyển yyyy-MM-dd → dd/MM/yyyy để gửi lên backend
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

      const updated = await res.json();

      // Cập nhật lại danh sách
      setCycles((prev) =>
        prev.map((c) =>
          c.id === updated.id
            ? {
                ...c,
                name: updated.name,
                status: updated.status,
                description: updated.description,
                startDate: selectedCycle.startDate,
                endDate: selectedCycle.endDate,
              }
            : c
        )
      );

      alert("Sửa kỳ đánh giá thành công!");
      setShowEditModal(false);
    } catch (err) {
      console.error(err);
      alert("Có lỗi khi sửa kỳ đánh giá!");
    }
  };

  const handleViewProjects = (cycleId) => {
    navigate(`/project-list?evaluationCycleId=${cycleId}`);
  };



  return (
    <div>
      <div className="content-header">
        <h1 className="header-title">Quản lý kỳ đánh giá</h1>
        <div className="header-actions">
          <Link to="/evaluationcycle-add">
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
            <button className="btn btn-warning" onClick={fetchEvaluationCycles}>
              <i className="fas fa-sync-alt"></i> Làm mới
            </button>
          </div>
        </div>

        <table className="excel-table">
          <thead>
            <tr>
              <th>STT</th>
              <th>Tên kỳ đánh giá</th>
              <th>Ngày bắt đầu</th>
              <th>Ngày kết thúc</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {currentCycles.length > 0 ? (
              currentCycles.map((c, index) => (
                <tr key={c.id}>
                  <td>{startIndex + index + 1}</td>
                  <td>{c.name}</td>
                  <td>{c.startDate}</td>
                  <td>{c.endDate}</td>
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
                <td colSpan="5" style={{ textAlign: "center", padding: "20px" }}>
                  Không tìm thấy kỳ đánh giá nào.
                </td>
              </tr>
            )}
          </tbody>
        </table>

        {/* Pagination */}
        <div className="pagination-container">
          <div className="pagination-info">
            Hiển thị {startIndex + 1}-{Math.min(endIndex, filteredCycles.length)} trong tổng số{" "}
            {filteredCycles.length} kỳ đánh giá
          </div>
          <div className="pagination-controls">
            <button
              className="pagination-btn"
              onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
              disabled={currentPage === 1}
            >
              ‹ Trước
            </button>
            {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
              const pageNum = i + 1;
              return (
                <button
                  key={pageNum}
                  className={`pagination-btn ${currentPage === pageNum ? "active" : ""}`}
                  onClick={() => setCurrentPage(pageNum)}
                >
                  {pageNum}
                </button>
              );
            })}
            <button
              className="pagination-btn"
              onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
              disabled={currentPage === totalPages}
            >
              Sau ›
            </button>
          </div>
        </div>
      </div>

      {/* Edit Modal */}
      {showEditModal && selectedCycle && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Sửa kỳ đánh giá</h3>
            <div className="form-group">
              <label>Tên kỳ</label>
              <input
                type="text"
                value={selectedCycle.name || ""}
                onChange={(e) => setSelectedCycle({ ...selectedCycle, name: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>Mô tả</label>
              <input
                type="text"
                value={selectedCycle.description || ""}
                onChange={(e) => setSelectedCycle({ ...selectedCycle, description: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>Trạng thái</label>
              <input
                type="text"
                value={selectedCycle.status || ""}
                onChange={(e) => setSelectedCycle({ ...selectedCycle, status: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>Ngày bắt đầu</label>
              <input
                type="date"
                value={selectedCycle.startDate || ""}
                onChange={(e) =>
                  setSelectedCycle({
                    ...selectedCycle,
                    startDate: e.target.value,
                  })
                }
              />
            </div>
            <div className="form-group">
              <label>Ngày kết thúc</label>
              <input
                type="date"
                value={selectedCycle.endDate || ""}
                onChange={(e) =>
                  setSelectedCycle({
                    ...selectedCycle,
                    endDate: e.target.value,
                  })
                }
              />
            </div>
            <div className="modal-actions">
              <button className="btn btn-primary" onClick={handleEditConfirm}>
                Xác nhận
              </button>
              <button className="btn btn-secondary" onClick={() => setShowEditModal(false)}>
                Hủy
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Modal */}
      {showDeleteModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>{deleteMessage}</h3>
            <button className="btn btn-secondary" onClick={() => setShowDeleteModal(false)}>
              Đóng
            </button>
          </div>
        </div>
      )}

      {/* View Modal */}
      {showViewModal && selectedCycle && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Thông tin kỳ đánh giá</h3>
            <table className="excel-table">
              <tbody>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Tên kỳ</td>
                  <td>{selectedCycle.name}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Mô tả</td>
                  <td>{selectedCycle.description}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Trạng thái</td>
                  <td>{selectedCycle.status}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Ngày bắt đầu</td>
                  <td>{selectedCycle.startDate}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Ngày kết thúc</td>
                  <td>{selectedCycle.endDate}</td>
                </tr>
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
