import React, { useEffect, useState } from "react";
import "./dashboard.css";
import { Link, useLocation } from "react-router-dom";

const EmployeeAddOld = () => {
  const [employees, setEmployees] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const projectId = queryParams.get("projectId");
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);


  useEffect(() => {
    if (projectId) {
      fetchEmployees();
    }
  }, [projectId]);

  const fetchEmployees = async () => {
    try {
      // Lấy tất cả nhân viên
      const resAll = await fetch("http://localhost:8080/api/employees", {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "application/json",
        },
      });
      const allData = await resAll.json();
      const allEmployees = allData.data?.content || [];

      // Lấy danh sách nhân viên đã trong dự án
      const resProject = await fetch(
        `http://localhost:8080/api/projects/${projectId}`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
            "Content-Type": "application/json",
          },
        }
      );
      const projectData = await resProject.json();
      const projectEmployees = projectData.data?.employees || [];

      // Lọc ra những nhân viên chưa có trong dự án
      const projectEmployeeIds = projectEmployees.map((e) => e.id);
      const availableEmployees = allEmployees.filter(
        (e) => !projectEmployeeIds.includes(e.id)
      );

      setEmployees(availableEmployees);
    } catch (error) {
      console.error("Fetch employees error:", error);
    }
  };
  

  const handleAddToProject = async (employeeId) => {
    try {
      const res = await fetch(
        `http://localhost:8080/api/projects/${projectId}/add-employee/${employeeId}`,
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
        alert("✅ Đã thêm nhân viên vào dự án!");
        fetchEmployees();
      } else {
        alert("❌ Thêm thất bại: " + data.message);
      }
    } catch (error) {
      console.error("Add employee error:", error);
      alert("Lỗi kết nối server!");
    }
  };

  const filteredEmployees = employees.filter((emp) =>
    emp.fullName?.toLowerCase().includes(searchTerm.toLowerCase())
  );

    // ✅ Phân trang
  const totalPages = Math.ceil(filteredEmployees.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentEmployees = filteredEmployees.slice(startIndex, endIndex);

  // Reset lại về trang 1 khi tìm kiếm
  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm, employees]);



  return (
    <div>
      {/* Header */}
      <div className="content-header">
        <h1 className="header-title">
          Thêm nhân viên vào dự án {projectId}
        </h1>
        <div className="header-actions">
          <Link to={`/employee-list?projectId=${projectId}`}>
            <button className="btn btn-secondary">
              <i className="fas fa-arrow-left"></i> Quay lại
            </button>
          </Link>
        </div>
      </div>

      {/* Table Section */}
      <div className="excel-container">
        <div className="table-header">
          <h3 className="table-title">Danh sách nhân viên khả dụng</h3>
          <div className="table-controls">
            <input
              type="text"
              placeholder="Tìm nhân viên..."
              className="search-box"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            <button className="btn btn-warning" onClick={fetchEmployees}>
              <i className="fas fa-sync-alt"></i> Làm mới
            </button>
          </div>
        </div>

        <table className="excel-table">
          <thead>
            <tr>
              <th>STT</th>
              <th>Mã NV</th>
              <th>Họ và tên</th>
              <th>Email</th>
              <th>Phòng/Ban</th>
              <th>Chức vụ</th>
              <th>Cấp bậc</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {filteredEmployees.length > 0 ? (
              filteredEmployees.map((emp, index) => (
                <tr key={emp.id}>
                  <td>{index + 1}</td>
                  <td>{emp.staffCode}</td>
                  <td>{emp.fullName}</td>
                  <td>{emp.email}</td>
                  <td>{emp.department}</td>
                  <td>{emp.role}</td>
                  <td>{emp.level}</td>
                  <td>
                    <div className="action-buttons">
                      <button
                        className="btn btn-sm btn-success"
                        title="Thêm vào dự án"
                        onClick={() => handleAddToProject(emp.id)}
                      >
                        <i className="fas fa-user-plus"></i> Thêm
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="8" style={{ textAlign: "center", padding: "20px" }}>
                  Không có nhân viên khả dụng để thêm.
                </td>
              </tr>
            )}
          </tbody>
          {/* Pagination */}
        <div className="pagination-container">
          <div className="pagination-info">
            Hiển thị {startIndex + 1}-
            {Math.min(endIndex, filteredEmployees.length)} trong tổng số{" "}
            {filteredEmployees.length} nhân viên
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
        </table>
      </div>
    </div>
  );
};

export default EmployeeAddOld;
