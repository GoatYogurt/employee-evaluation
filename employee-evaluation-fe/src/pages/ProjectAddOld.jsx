import React, { useEffect, useState, useContext } from "react";
import "./dashboard.css";
import { Link, useLocation } from "react-router-dom";
import { ToastContext } from "../contexts/ToastProvider";

const ProjectAddOld = () => {
  const [projects, setProjects] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(0); // BE dùng page = 0-based
  const [pageSize, setPageSize] = useState(5);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);

  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const evaluationCycleId = queryParams.get("evaluationCycleId");
  const { toast } = useContext(ToastContext);


  // ================== FETCH PROJECTS ==================
  const fetchProjects = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem("token");
      if (!token) {
        toast.error("❌ Token không tồn tại. Vui lòng đăng nhập lại!");
        setLoading(false);
        return;
      }

      // Gọi API dự án đã có trong chu kỳ
      const resExisting = await fetch(
        `http://localhost:8080/api/evaluation-cycles/${evaluationCycleId}/projects`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );
      const existRes = await resExisting.json();
      const existingProjects = existRes?.data?.content || [];
      const existingIds = existingProjects.map((p) => Number(p.id));

      // Gọi API tất cả dự án (có phân trang)
      let url = `http://localhost:8080/api/projects?page=${currentPage}&size=${pageSize}`;
      if (searchTerm) url += `&code=${encodeURIComponent(searchTerm)}`;

      const resAll = await fetch(url, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });
      const allRes = await resAll.json();
      const pageData = allRes?.data || {};
      const allProjects = pageData?.content || [];

      // Lọc bỏ các dự án đã tồn tại trong chu kỳ
      const availableProjects = allProjects.filter(
        (p) => !existingIds.includes(Number(p.id))
      );

      setProjects(availableProjects);
      setTotalPages(pageData.totalPages || 0);
      setTotalElements(pageData.totalElements || 0);
      setLoading(false);
    } catch (error) {
      console.error("Fetch projects error:", error);
      toast.error("Không thể tải danh sách dự án!");
      setLoading(false);
    }
  };

  useEffect(() => {
    if (evaluationCycleId) fetchProjects();
  }, [evaluationCycleId, currentPage, pageSize, searchTerm]);

  // ================== ADD PROJECT TO CYCLE ==================
  const handleAddToCycle = async (projectId) => {
    try {
      const res = await fetch(
        `http://localhost:8080/api/projects/${projectId}/add-evaluation-cycle/${evaluationCycleId}`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
            "Content-Type": "application/json",
          },
        }
      );

      const data = await res.json();
      if (res.ok) {
        toast.success("✅ Đã thêm dự án vào chu kỳ đánh giá!");
        fetchProjects();
      } else {
        toast.error("❌ Thêm thất bại: " + (data.message || "Lỗi không xác định"));
      }
    } catch (error) {
      console.error("Add project error:", error);
      toast.error("Lỗi kết nối server!");
    }
  };

  const formatDate = (date) => {
    if (!date) return "-";
    const d = new Date(date);
    return d.toLocaleDateString("vi-VN");
  };

  return (
    <div>
      {/* Header */}
      <div className="content-header">
        <h1 className="header-title">
          Thêm dự án vào chu kỳ đánh giá {evaluationCycleId}
        </h1>
        <div className="header-actions">
          <Link to={`/project-list?evaluationCycleId=${evaluationCycleId}`}>
            <button className="btn btn-secondary">
              <i className="fas fa-arrow-left"></i> Quay lại
            </button>
          </Link>
        </div>
      </div>

      {/* Table Section */}
      <div className="excel-container">
        <div className="table-header">
          <h3 className="table-title">Danh sách dự án khả dụng</h3>
          <div className="table-controls">
            <input
              type="text"
              placeholder="Tìm dự án..."
              className="search-box"
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value);
                setCurrentPage(0);
              }}
            />
            <button className="btn btn-warning" onClick={fetchProjects}>
              <i className="fas fa-sync-alt"></i> Làm mới
            </button>
          </div>
        </div>

        {loading ? (
          <div style={{ textAlign: "center", padding: "20px" }}>
            Đang tải dữ liệu...
          </div>
        ) : (
          <table width="100%" className="excel-table">
            <thead>
              <tr>
                <th width="1%">STT</th>
                <th>Mã dự án</th>
                <th>Tên quản lý</th>
                <th>Ngày tạo</th>
                <th>Người tạo</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {projects.length > 0 ? (
                projects.map((proj, index) => (
                  <tr key={proj.id}>
                    <td>{currentPage * pageSize + index + 1}</td>
                    <td>{proj.code}</td>
                    <td>{proj.managerName || "-"}</td>
                    <td>{formatDate(proj.createdAt)}</td>
                    <td>{proj.createdBy || "-"}</td>
                    <td>
                      <button
                        className="btn btn-sm btn-success"
                        onClick={() => handleAddToCycle(proj.id)}
                      >
                        <i className="fas fa-folder-plus"></i> Thêm
                      </button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="6" style={{ textAlign: "center", padding: "20px" }}>
                    Không có dự án khả dụng để thêm.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        )}

        {/* Pagination */}
        {totalPages > 0 && (
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
            <div className="pagination-info" style={{ fontSize: "14px" }}>
              Trang {currentPage + 1} / {totalPages} — Tổng: {totalElements} dự án
            </div>

            {/* <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
              <span style={{ fontSize: "14px" }}>Số dòng mỗi trang:</span>
              <select
                value={pageSize}
                onChange={(e) => {
                  setPageSize(Number(e.target.value));
                  setCurrentPage(0);
                }}
                style={{
                  padding: "4px 8px",
                  borderRadius: "6px",
                  border: "1px solid #ccc",
                  background: "#fff",
                }}
              >
                {[5, 10, 20, 50].map((size) => (
                  <option key={size} value={size}>
                    {size}
                  </option>
                ))}
              </select>
            </div> */}

            <div
              className="pagination-controls"
              style={{ display: "flex", gap: "4px", flexWrap: "wrap" }}
            >
              <button
                className="pagination-btn"
                onClick={() => setCurrentPage((p) => Math.max(p - 1, 0))}
                disabled={currentPage === 0}
                style={{
                  padding: "6px 10px",
                  borderRadius: "6px",
                  border: "1px solid #ccc",
                  background: currentPage === 0 ? "#eee" : "#fff",
                  cursor: currentPage === 0 ? "not-allowed" : "pointer",
                }}
              >
                ‹ Trước
              </button>

              {Array.from({ length: totalPages }, (_, i) => (
                <button
                  key={i}
                  className={`pagination-btn ${
                    currentPage === i ? "active" : ""
                  }`}
                  onClick={() => setCurrentPage(i)}
                  style={{
                    padding: "6px 10px",
                    borderRadius: "6px",
                    border: "1px solid #ccc",
                    background: currentPage === i ? "#007bff" : "#fff",
                    color: currentPage === i ? "#fff" : "#000",
                    cursor: "pointer",
                  }}
                >
                  {i + 1}
                </button>
              ))}

              <button
                className="pagination-btn"
                onClick={() =>
                  setCurrentPage((p) => Math.min(p + 1, totalPages - 1))
                }
                disabled={currentPage + 1 === totalPages}
                style={{
                  padding: "6px 10px",
                  borderRadius: "6px",
                  border: "1px solid #ccc",
                  background:
                    currentPage + 1 === totalPages ? "#eee" : "#fff",
                  cursor:
                    currentPage + 1 === totalPages
                      ? "not-allowed"
                      : "pointer",
                }}
              >
                Sau ›
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProjectAddOld;
