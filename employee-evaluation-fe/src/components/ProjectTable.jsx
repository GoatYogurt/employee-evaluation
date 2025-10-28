// ProjectTable.jsx
import React, { useEffect, useState } from "react";
import "../index.css";
import { useNavigate, Link, useLocation } from "react-router-dom";
import { useContext } from "react";
import { ToastContext } from "../contexts/ToastProvider";

const ProjectTable = () => {
  const [projects, setProjects] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(5);
  const [pageSize, setPageSize] = useState(10);
  // const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [totalElements, setTotalElements] = useState(0);

  const [showViewModal, setShowViewModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedProject, setSelectedProject] = useState(null);

  const [managers, setManagers] = useState([]);

  const { toast } = useContext(ToastContext);

  const navigate = useNavigate();
  const location = useLocation();

  const queryParams = new URLSearchParams(location.search);
  const evaluationCycleId = queryParams.get("evaluationCycleId");
  const explicitSource = queryParams.get("source");
  const pathname = location.pathname || "";

  const isFromEvaluation =
    explicitSource === "evaluation" ||
    pathname.toLowerCase().includes("evaluation") ||
    Boolean(evaluationCycleId);

  const isFromProject =
    explicitSource === "project" ||
    (!explicitSource && !isFromEvaluation);

  // ===================== FETCH PROJECTS & MANAGERS =====================
  useEffect(() => {
    fetchProjects();
    fetchManagers();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [evaluationCycleId, explicitSource, location.pathname]);

  const fetchProjects = async () => {
    try {
      let url = "http://localhost:8080/api/projects";

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
        console.error("Fetch projects failed:", res.status, await res.text());
        return;
      }

      const response = await res.json();
      console.log("📦 API RESPONSE:", response);

      const projectsData = response.data?.content || response.data || [];

      const normalized = (projectsData || []).map((p) => ({
        id: p.id,
        code: p.code,
        isOdc: p.isOdc === true || p.isOdc === "true" || p.odc === true || p.odc === 1,
        managerName: p.managerName,
        managerId: p.managerId ?? null,
        employees: p.employees || [],
        evaluationCycleIds: Array.isArray(p.evaluationCycleIds)
          ? p.evaluationCycleIds
          : p.evaluationCycleIds
          ? [p.evaluationCycleIds]
          : [],
        createdAt: p.createdAt,
        updatedAt: p.updatedAt,
        createdBy: p.createdBy,
        updatedBy: p.updatedBy,
      }));

      setProjects(normalized);

      // If backend provided paging metadata, capture it
      const maybeData = response.data || {};
      if (typeof maybeData.totalElements !== "undefined") {
        setTotalElements(maybeData.totalElements);
      } else {
        // fallback to length
        setTotalElements(normalized.length);
      }

      console.log("FULL EMPLOYEE RESPONSE:", response);
    } catch (error) {
      console.error("Error fetching projects:", error);
    }
  };

  const fetchManagers = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/employees", {
        method: "GET",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "application/json",
        },
      });

      if (!res.ok) {
        console.error("Fetch managers failed:", res.status, await res.text());
        return;
      }

      const response = await res.json();
      console.log("📌 FULL EMPLOYEE RESPONSE:", response);

      // Kiểm tra nơi chứa data
      const allEmployees = response.data?.content || response.data || response || [];
      console.log("✅ Danh sách nhân viên chuẩn hoá:", allEmployees);

      const pmList = Array.isArray(allEmployees)
        ? allEmployees.filter((emp) =>
            emp.role?.toString().trim().toUpperCase().includes("PM")
          )
        : [];

      console.log("✨ Danh sách PM:", pmList);
      setManagers(pmList);
    } catch (error) {
      console.error("🔥 Error fetching managers:", error);
    }
  };

  // ===================== SEARCH & PAGINATION =====================
  const filteredProjects = projects.filter((p) =>
    p.code?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const totalPages = Math.ceil(filteredProjects.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentProjects = filteredProjects.slice(startIndex, endIndex);

  // ===================== HANDLERS =====================
  const handleView = (project) => {
    setSelectedProject(project);
    setShowViewModal(true);
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

  const handleEdit = (project) => {
    setSelectedProject(project);
    setShowEditModal(true);
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
            managerId: selectedProject.managerId ?? null,
          }),
        }
      );

      if (!res.ok) {
        const errMsg = await res.text();
        console.error("Update failed:", errMsg);
        toast.error("❌ Sửa dự án thất bại!");
        return;
      }

      toast.success("Sửa dự án thành công!");
      setShowEditModal(false);
      fetchProjects();
    } catch (err) {
      console.error("Error:", err);
      toast.error("Có lỗi khi sửa dự án!");
    }
  };

  const handleDelete = async (projectId) => {
    const confirmDelete = window.confirm("Bạn có chắc muốn xóa dự án này?");
    if (!confirmDelete) return;

    try {
      let response;

      if (evaluationCycleId) {
        response = await fetch(
          `http://localhost:8080/api/projects/${projectId}/remove-evaluation-cycle/${evaluationCycleId}`,
          {
            method: "PUT",
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
              "Content-Type": "application/json",
            },
          }
        );
      } else {
        response = await fetch(`http://localhost:8080/api/projects/${projectId}`, {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
            "Content-Type": "application/json",
          },
        });
      }

      if (!response.ok) {
        console.error("Delete failed:", await response.text());
        alert("❌ Xóa thất bại!");
        return;
      }

      toast.success("Xóa thành công!");
      fetchProjects();
    } catch (err) {
      console.error("Error deleting project:", err);
      toast.error("Có lỗi khi xóa dự án!");
    }
  };

  const handleViewEmployees = (project) => {
    const projectId = project.id;
    if (isFromEvaluation) {
      const evalIdFromUrl = evaluationCycleId;
      const evalToUse =
        evalIdFromUrl ||
        (project?.evaluationCycleIds && project.evaluationCycleIds.length > 0
          ? project.evaluationCycleIds[0]
          : null);

      if (evalToUse != null) {
        navigate(
          `/employee-list?projectId=${projectId}&evaluationCycleId=${String(
            evalToUse
          )}&source=evaluation`
        );
      } else {
        navigate(`/employee-list?projectId=${projectId}&source=evaluation`);
      }
    } else {
      navigate(`/employee-list?projectId=${projectId}&source=project`);
    }
  };

  // ===================== RENDER =====================
  return (
    <div>
      {/* Header */}
      <div className="content-header">
        <h1 className="header-title">Quản lý dự án</h1>
        <div className="header-actions">
          {isFromEvaluation ? (
            // When in evaluation context, allow adding project into evaluation cycle
            <Link to={`/project-add-old?evaluationCycleId=${evaluationCycleId || ""}&source=evaluation`}>
              <button className="btn btn-success">
                <i className="fas fa-folder-plus"></i> Thêm vào kỳ đánh giá
              </button>
            </Link>
          ) : (
            <Link to="/project-add">
              <button className="btn btn-primary">
                <i className="fas fa-plus"></i> Thêm dự án
              </button>
            </Link>
          )}
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
              onChange={(e) => {
                setSearchTerm(e.target.value);
                setCurrentPage(1);
              }}
            />
            <button className="btn btn-warning" onClick={fetchProjects}>
              <i className="fas fa-sync-alt"></i> Làm mới
            </button>
          </div>
        </div>

        <table className="excel-table" style={{ width: "100%", tableLayout: "fixed" }}>
          <thead>
            <tr>
              <th style={{ width: "2%" }}>STT</th>
              <th style={{ width: "8%" }}>Mã dự án</th>
              <th style={{ width: "4%" }}>ODC</th>
              <th style={{ width: "20%" }}>Quản lý</th>
              <th style={{ width: "20%" }}>Thao tác</th>
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

                      {/* Xem nhân viên: behavior thay đổi theo ngữ cảnh (project vs evaluation) */}
                      <button
                        className="btn btn-sm btn-primary"
                        title={isFromEvaluation ? "Xem nhân viên (đánh giá)" : "Xem nhân viên (quản lý/add)"}
                        onClick={() => handleViewEmployees(p)}
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
        {/* Hiển thị controls khi cần (khi số kết quả lớn hơn itemsPerPage) */}
        {filteredProjects.length > 0 && (
          <div
            className="pagination-container"
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              marginTop: "16px",
              flexWrap: "wrap",
              gap: "10px",
            }}
          >
            {/* Thông tin hiển thị */}
            <div className="pagination-info" style={{ fontSize: "14px" }}>
              Hiển thị {Math.min(startIndex + 1, filteredProjects.length)}-
              {Math.min(endIndex, filteredProjects.length)} trong tổng số{" "}
              {filteredProjects.length} dự án
            </div>

            {/* Bộ chọn số dòng mỗi trang */}
            {/* <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
              <span style={{ fontSize: "14px" }}>Số dòng mỗi trang:</span>
              <select
                value={itemsPerPage}
                onChange={(e) => {
                  const newSize = Number(e.target.value);
                  setItemsPerPage(newSize);
                  setPageSize(newSize);
                  setCurrentPage(1);
                }}
                style={{
                  padding: "4px 8px",
                  borderRadius: "6px",
                  border: "1px solid #ccc",
                  background: "#fff",
                }}
              >
                {[10, 20, 50].map((size) => (
                  <option key={size} value={size}>
                    {size}
                  </option>
                ))}
              </select>
            </div> */}

            {/* Nút phân trang */}
            <div
              className="pagination-controls"
              style={{ display: "flex", gap: "4px", flexWrap: "wrap" }}
            >
              <button
                className="pagination-btn"
                onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
                disabled={currentPage === 1}
                style={{
                  padding: "6px 10px",
                  borderRadius: "6px",
                  border: "1px solid #ccc",
                  background: currentPage === 1 ? "#eee" : "#fff",
                  cursor: currentPage === 1 ? "not-allowed" : "pointer",
                }}
              >
                ‹ Trước
              </button>
              {Array.from({ length: totalPages }, (_, i) => {
                const pageNum = i + 1;
                return (
                  <button
                    key={pageNum}
                    className={`pagination-btn ${currentPage === pageNum ? "active" : ""}`}
                    onClick={() => setCurrentPage(pageNum)}
                    style={{
                      padding: "6px 10px",
                      borderRadius: "6px",
                      border: "1px solid #ccc",
                      background: currentPage === pageNum ? "#007bff" : "#fff",
                      color: currentPage === pageNum ? "#fff" : "#000",
                      cursor: "pointer",
                    }}
                  >
                    {pageNum}
                  </button>
                );
              })}
              <button
                className="pagination-btn"
                onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
                disabled={currentPage === totalPages}
                style={{
                  padding: "6px 10px",
                  borderRadius: "6px",
                  border: "1px solid #ccc",
                  background: currentPage === totalPages ? "#eee" : "#fff",
                  cursor: currentPage === totalPages ? "not-allowed" : "pointer",
                }}
              >
                Sau ›
              </button>
            </div>
          </div>
        )}
      </div>

      {/* =================== MODALS =================== */}
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
              <label>Quản lý:</label>
              <select
                name="managerId"
                required
                value={selectedProject.managerId || ""}
                onChange={(e) =>
                  setSelectedProject({ ...selectedProject, managerId: e.target.value })
                }
              >
                <option value="">Chọn Quản lý (PM)</option>
                {managers.map((m) => (
                  <option key={m.id} value={m.id}>
                    {m.fullName} ({m.email})
                  </option>
                ))}
              </select>
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
                  <td>{formatDateTime(selectedProject.createdAt)}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Ngày cập nhật</td>
                  <td>{formatDateTime(selectedProject.updatedAt)}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Người tạo</td>
                  <td>{selectedProject.createdBy}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Người cập nhật</td>
                  <td>{selectedProject.updatedBy}</td>
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

export default ProjectTable;
