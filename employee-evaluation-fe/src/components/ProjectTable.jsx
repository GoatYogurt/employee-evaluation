import React, { useEffect, useState } from "react";
import "../index.css";
import { useNavigate, Link, useLocation } from "react-router-dom";
import { useContext } from "react";
import { ToastContext } from "../contexts/ToastProvider";

const ProjectTable = () => {
  const [projects, setProjects] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(0); // BE phân trang từ 0
  const [itemsPerPage] = useState(4);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);

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
    fetchProjects(currentPage);
    fetchManagers();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage, evaluationCycleId, explicitSource, location.pathname]);

  const fetchProjects = async (page = 0) => {
    try {
      setLoading(true);
      let url = `http://localhost:8080/api/projects?page=${page}&size=${itemsPerPage}`;
      if (evaluationCycleId) {
        url = `http://localhost:8080/api/evaluation-cycles/${evaluationCycleId}/projects?page=${page}&size=${itemsPerPage}`;
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
        toast.error("Không thể tải danh sách dự án!");
        setLoading(false);
        return;
      }

      const response = await res.json();
      const pageData = response.data || {};

      const normalized = (pageData.content || []).map((p) => ({
        id: p.id,
        code: p.code,
        isOdc: p.isOdc === true || p.odc === true,
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
      setTotalPages(pageData.totalPages || 1);
      setTotalElements(pageData.totalElements || normalized.length);
      setLoading(false);
    } catch (error) {
      console.error("Error fetching projects:", error);
      setLoading(false);
    }
  };

  const fetchManagers = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/employees?size=1000", {
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
      const allEmployees = response.data?.content || response.data || [];
      const pmList = Array.isArray(allEmployees)
        ? allEmployees.filter((emp) =>
            emp.role?.toString().trim().toUpperCase().includes("PM")
          )
        : [];
      setManagers(pmList);
    } catch (error) {
      console.error("🔥 Error fetching managers:", error);
    }
  };

  // ===================== SEARCH (FILTER CURRENT PAGE) =====================
  const filteredProjects = projects.filter((p) =>
    p.code?.toLowerCase().includes(searchTerm.toLowerCase())
  );

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
        toast.error("❌ Sửa dự án thất bại!");
        return;
      }

      toast.success("Sửa dự án thành công!");
      setShowEditModal(false);
      fetchProjects(currentPage);
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
        toast.error("❌ Xóa thất bại!");
        return;
      }

      toast.success("Xóa thành công!");
      fetchProjects(currentPage);
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
      <div className="content-header">
        <h1 className="header-title">Quản lý dự án</h1>
        <div className="header-actions">
          {isFromEvaluation ? (
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
                setCurrentPage(0);
              }}
            />
            <button className="btn btn-warning" onClick={() => fetchProjects(currentPage)}>
              <i className="fas fa-sync-alt"></i> Làm mới
            </button>
          </div>
        </div>

        {loading ? (
          <div style={{ textAlign: "center", padding: "20px" }}>Đang tải...</div>
        ) : (
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
              {filteredProjects.length > 0 ? (
                filteredProjects.map((p, index) => (
                  <tr key={p.id}>
                    <td>{index + 1 + currentPage * itemsPerPage}</td>
                    <td>{p.code}</td>
                    <td>{p.isOdc ? "ODC" : "NOT ODC"}</td>
                    <td>{p.managerName}</td>
                    <td>
                      <div className="action-buttons">
                        <button className="btn btn-sm btn-edit" onClick={() => handleEdit(p)}>
                          <i className="fas fa-edit"></i>
                        </button>
                        <button className="btn btn-sm btn-delete" onClick={() => handleDelete(p.id)}>
                          <i className="fas fa-trash"></i>
                        </button>
                        <button className="btn btn-sm btn-view" onClick={() => handleView(p)}>
                          <i className="fas fa-eye"></i>
                        </button>
                        <button
                          className="btn btn-sm btn-primary"
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
        )}

        {/* Pagination */}
        <div className="pagination-container" style={{ marginTop: "16px" }}>
          <div className="pagination-info">
            Trang {currentPage + 1}/{totalPages} — Tổng {totalElements} dự án
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

      {/* Modal giữ nguyên như cũ */}
      {showEditModal && selectedProject && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Sửa dự án</h3>
            <div className="form-group">
              <label>Mã dự án</label>
              <input
                type="text"
                value={selectedProject.code || ""}
                onChange={(e) => setSelectedProject({ ...selectedProject, code: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>ODC</label>
              <select
                value={selectedProject.isOdc ? "true" : "false"}
                onChange={(e) => setSelectedProject({ ...selectedProject, isOdc: e.target.value === "true" })}
              >
                <option value="true">ODC</option>
                <option value="false">NOT ODC</option>
              </select>
            </div>
            <div className="form-group">
              <label>Quản lý</label>
              <select
                value={selectedProject.managerId || ""}
                onChange={(e) => setSelectedProject({ ...selectedProject, managerId: e.target.value })}
              >
                <option value="">Chọn PM</option>
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
                <tr><td><b>Mã dự án</b></td><td>{selectedProject.code}</td></tr>
                <tr><td><b>ODC</b></td><td>{selectedProject.isOdc ? "ODC" : "NOT ODC"}</td></tr>
                <tr><td><b>Quản lý</b></td><td>{selectedProject.managerName}</td></tr>
                <tr><td><b>Ngày tạo</b></td><td>{formatDateTime(selectedProject.createdAt)}</td></tr>
                <tr><td><b>Ngày cập nhật</b></td><td>{formatDateTime(selectedProject.updatedAt)}</td></tr>
                <tr><td><b>Người tạo</b></td><td>{selectedProject.createdBy}</td></tr>
                <tr><td><b>Người cập nhật</b></td><td>{selectedProject.updatedBy}</td></tr>
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
