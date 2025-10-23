import React, { useEffect, useState } from "react";
import "./dashboard.css";
import { Link, useLocation } from "react-router-dom";

const ProjectAddOld = () => {
  const [projects, setProjects] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const evaluationCycleId = queryParams.get("evaluationCycleId");

  // ✅ Hàm fetchProjects — tách riêng ra ngoài useEffect
  const fetchProjects = async () => {
    try {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("❌ Token không tồn tại. Vui lòng đăng nhập lại!");
        return;
      }

      // 🟢 Lấy tất cả dự án
      const resAll = await fetch("http://localhost:8080/api/projects", {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });
      const allRes = await resAll.json();
      const allProjects = allRes?.data?.content || [];

      // 🟢 Lấy dự án đã thuộc chu kỳ đánh giá
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

      // 🟢 Lọc ra các dự án chưa thuộc chu kỳ này
      const existingIds = existingProjects.map((p) => Number(p.id));
      const availableProjects = allProjects.filter(
        (p) => !existingIds.includes(Number(p.id))
      );

      setProjects(availableProjects);
    } catch (error) {
      console.error("Fetch projects error:", error);
      alert("❌ Không thể tải danh sách dự án!");
    }
  };

  // ✅ Tải dữ liệu khi component mount hoặc evaluationCycleId đổi
  useEffect(() => {
    if (evaluationCycleId) {
      fetchProjects();
    }
  }, [evaluationCycleId]);

  // ✅ Thêm dự án vào evaluation cycle
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
        alert("✅ Đã thêm dự án vào chu kỳ đánh giá!");
        fetchProjects(); // Làm mới danh sách
      } else {
        alert("❌ Thêm thất bại: " + (data.message || "Lỗi không xác định"));
      }
    } catch (error) {
      console.error("Add project error:", error);
      alert("❌ Lỗi kết nối server!");
    }
  };

  // ✅ Lọc dự án theo tìm kiếm
  const filteredProjects = projects.filter(
    (p) =>
      p.code?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.managerName?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // ✅ Phân trang
  const totalPages = Math.ceil(filteredProjects.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentProjects = filteredProjects.slice(startIndex, endIndex);

  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm, projects]);

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
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            <button className="btn btn-warning" onClick={fetchProjects}>
              <i className="fas fa-sync-alt"></i> Làm mới
            </button>
          </div>
        </div>

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
            {currentProjects.length > 0 ? (
              currentProjects.map((proj, index) => (
                <tr key={proj.id}>
                  <td>{startIndex + index + 1}</td>
                  <td>{proj.code}</td>
                  <td>{proj.managerName || "-"}</td>
                  <td>
                    {proj.createdAt
                      ? new Date(proj.createdAt).toLocaleDateString()
                      : "-"}
                  </td>
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
                {Array.from({ length: totalPages }, (_, i) => {
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
    </div>
  );
};

export default ProjectAddOld;
