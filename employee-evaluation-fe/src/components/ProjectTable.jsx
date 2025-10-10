import React, { useEffect, useState } from "react";
import "../index.css";
import { useNavigate, Link } from "react-router-dom";
import { useLocation } from "react-router-dom";


const ProjectTable = () => {
  const [projects, setProjects] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);

  const navigate = useNavigate();

  // popup state
  const [showViewModal, setShowViewModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedProject, setSelectedProject] = useState(null);
  const [deleteMessage, setDeleteMessage] = useState("");

  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const evaluationCycleId = queryParams.get("evaluationCycleId");


  useEffect(() => {
    fetchProjects();
  }, []);

const fetchProjects = async () => {
  try {
    let url = "http://localhost:8080/api/projects";

    // ✅ SỬA 1: dùng evaluationCycleId thay vì cycleId
    if (evaluationCycleId) {
      url = `http://localhost:8080/api/evaluation-cycles/${evaluationCycleId}/projects`;
    }

    const res = await fetch(url, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
        "Content-Type": "application/json",
      },
    });

    if (!res.ok) {
      const errorText = await res.text();
      console.error("API ERROR:", res.status, errorText);
      return;
    }

    const response = await res.json();
    console.log("API RESPONSE:", response);

    let projectsData = [];

    if (evaluationCycleId) {
      
      projectsData = response.data?.content || [];
    } else {
      projectsData = response.data?.content || [];
    }

    const normalized = projectsData.map((p) => ({
      id: p.id,
      code: p.code,
      isOdc: p.isOdc,
      managerName: p.managerName,
      employees: p.employees || [],
      createdAt: p.createdAt,
      updatedAt: p.updatedAt,
      createdBy: p.createdBy,
      updatedBy: p.updatedBy,
    }));

    setProjects(normalized);
  } catch (error) {
    console.error("Failed to fetch projects:", error);
  }
};

  // tìm kiếm
  const filteredProjects = projects.filter((p) =>
    p.code?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // phân trang
  const totalPages = Math.ceil(filteredProjects.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentProjects = filteredProjects.slice(startIndex, endIndex);

  // === HANDLERS ===
  const handleView = (project) => {
    setSelectedProject(project);
    setShowViewModal(true);
  };

  const handleEdit = (project) => {
    setSelectedProject(project);
    setShowEditModal(true);
  };

  const handleDelete = async (id) => {
    try {
      const res = await fetch(`http://localhost:8080/api/projects/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "application/json",
        },
      });

      if (!res.ok) throw new Error("Delete failed");

      setProjects((prev) => prev.filter((p) => p.id !== id));
      setDeleteMessage("✅ Xóa dự án thành công!");
    } catch (err) {
      console.error(err);
      setDeleteMessage("❌ Xóa dự án thất bại!");
    }
    setShowDeleteModal(true);
  };

  const handleEditConfirm = async () => {
    try {
      const res = await fetch(
        `http://localhost:8080/api/projects/${selectedProject.id}`,
        {
          method: "PATCH",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            code: selectedProject.code,
            isOdc: selectedProject.isOdc,
            managerName: selectedProject.managerName,
          }),
        }
      );

      if (!res.ok) {
        const errMsg = await res.text();
        console.error("Update failed:", errMsg);
        alert("Sửa dự án thất bại!");
        return;
      }

      const updated = await res.json();

      setProjects((prev) =>
        prev.map((p) =>
          p.id === updated.id
            ? {
                ...p,
                code: updated.code,
                isOdc: updated.isOdc,
                managerName: updated.managerName,
              }
            : p
        )
      );

      alert("✅ Sửa dự án thành công!");
      setShowEditModal(false);
    } catch (err) {
      console.error(err);
      alert("❌ Có lỗi khi sửa dự án!");
    }
  };

  const handleViewEmployees = (projectId) => {
    navigate(`/employee-list?projectId=${projectId}`);
  };

  return (
    <div>
      {/* Header */}
      <div className="content-header">
        <h1 className="header-title">Quản lý dự án</h1>
        <div className="header-actions">
          <Link to="/project-add">
            <button className="btn btn-primary">
              <i className="fas fa-plus"></i> Thêm dự án
            </button>
          </Link>
        </div>
      </div>

      {/* Table */}
      <div className="excel-container">
        <div className="table-header">
          <h3 className="table-title">Danh sách dự án</h3>
          <div className="table-controls">
            <input
              type="text"
              placeholder="Tìm kiếm mã dự án..."
              className="search-box"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            <button className="btn btn-warning" onClick={fetchProjects}>
              <i className="fas fa-sync-alt"></i> Làm mới
            </button>
          </div>
        </div>

        <table className="excel-table">
          <thead>
            <tr>
              <th>STT</th>
              <th>Mã dự án</th>
              <th>ODC</th>
              <th>Quản lý</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {currentProjects.length > 0 ? (
              currentProjects.map((p, index) => (
                <tr key={p.id}>
                  <td>{startIndex + index + 1}</td>
                  <td>{p.code}</td>
                  <td>{p.isOdc ? "ODC" : "NOT ODC"}</td>
                  <td>{p.managerName}</td>
                  <td>
                    <div className="action-buttons">
                      <button
                        className="btn btn-sm btn-edit"
                        title="Sửa"
                        onClick={() => handleEdit(p)}
                      >
                        <i className="fas fa-edit"></i>
                      </button>
                      <button
                        className="btn btn-sm btn-delete"
                        title="Xóa"
                        onClick={() => handleDelete(p.id)}
                      >
                        <i className="fas fa-trash"></i>
                      </button>
                      <button
                        className="btn btn-sm btn-view"
                        title="Xem chi tiết"
                        onClick={() => handleView(p)}
                      >
                        <i className="fas fa-eye"></i>
                      </button>
                      <button
                        className="btn btn-sm btn-primary"
                        title="Xem nhân viên"
                        onClick={() => handleViewEmployees(p.id)}
                      >
                        <i className="fas fa-users"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="5" style={{ textAlign: "center", padding: "20px" }}>
                  Không tìm thấy dự án nào.
                </td>
              </tr>
            )}
          </tbody>
        </table>

        {/* Pagination */}
        <div className="pagination-container">
          <div className="pagination-info">
            Hiển thị {startIndex + 1}-
            {Math.min(endIndex, filteredProjects.length)} trong tổng số{" "}
            {filteredProjects.length} dự án
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
                  className={`pagination-btn ${
                    currentPage === pageNum ? "active" : ""
                  }`}
                  onClick={() => setCurrentPage(pageNum)}
                >
                  {pageNum}
                </button>
              );
            })}
            <button
              className="pagination-btn"
              onClick={() =>
                setCurrentPage((prev) => Math.min(prev + 1, totalPages))
              }
              disabled={currentPage === totalPages}
            >
              Sau ›
            </button>
          </div>
        </div>
      </div>

      {/* =================== MODALS =================== */}

      {/* Edit Modal */}
      {showEditModal && selectedProject && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Sửa dự án</h3>
            <div className="form-group">
              <label>Mã dự án</label>
              <input
                type="text"
                value={selectedProject.code || ""}
                onChange={(e) =>
                  setSelectedProject({
                    ...selectedProject,
                    code: e.target.value,
                  })
                }
              />
            </div>
            <div className="form-group">
              <label>ODC</label>
              <select
                value={selectedProject.isOdc ? "true" : "false"}
                onChange={(e) =>
                  setSelectedProject({
                    ...selectedProject,
                    isOdc: e.target.value === "true",
                  })
                }
              >
                <option value="true">ODC</option>
                <option value="false">NOT ODC</option>
              </select>
            </div>
            <div className="form-group">
              <label>Quản lý</label>
              <input
                type="text"
                value={selectedProject.managerName || ""}
                onChange={(e) =>
                  setSelectedProject({
                    ...selectedProject,
                    managerName: e.target.value,
                  })
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

      {/* Delete Modal */}
      {showDeleteModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>{deleteMessage}</h3>
            <button
              className="btn btn-secondary"
              onClick={() => setShowDeleteModal(false)}
            >
              Đóng
            </button>
          </div>
        </div>
      )}

      {/* View Modal */}
      {showViewModal && selectedProject && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Thông tin dự án</h3>
            <table className="excel-table">
              <tbody>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Mã dự án</td>
                  <td>{selectedProject.code}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>ODC</td>
                  <td>{selectedProject.isOdc ? "ODC" : "NOT ODC"}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Quản lý</td>
                  <td>{selectedProject.managerName}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Ngày tạo</td>
                  <td>
                    {selectedProject.createdAt
                      ? new Date(selectedProject.createdAt).toLocaleDateString(
                          "vi-VN"
                        )
                      : "N/A"}
                  </td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Ngày cập nhật</td>
                  <td>
                    {selectedProject.updatedAt
                      ? new Date(selectedProject.updatedAt).toLocaleDateString(
                          "vi-VN"
                        )
                      : "N/A"}
                  </td>
                </tr>
              </tbody>
            </table>
            <button
              className="btn btn-secondary"
              onClick={() => setShowViewModal(false)}
            >
              Đóng
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProjectTable;
