import React, { useEffect, useState } from "react";
import "./dashboard.css";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useContext } from "react";
import { ToastContext } from "../contexts/ToastProvider";

const EmployeeAddOld = () => {
  const navigate = useNavigate();
  const [employees, setEmployees] = useState([]); 
  const [searchTerm, setSearchTerm] = useState("");
  const [sortOrder, setSortOrder] = useState("asc"); // asc hoặc desc
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const projectId = queryParams.get("projectId");

  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);

  const { toast } = useContext(ToastContext);

  useEffect(() => {
    if (projectId) {
      fetchEmployees();
    }
  }, [projectId]);

  // ------------------ FETCH EMPLOYEES ------------------
  const fetchEmployees = async () => {
    try {
      const resAll = await fetch("http://localhost:8080/api/employees?size=10000", {
        method: "GET",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "application/json",
        },
      });

      if (!resAll.ok) {
        const txt = await resAll.text();
        console.error("Fetch all employees failed:", resAll.status, txt);
        toast.error("Không thể tải danh sách nhân viên!");
        setEmployees([]);
        return;
      }

      const allData = await resAll.json();
      const allEmployees = allData.data?.content || allData.data || [];

      // Lấy danh sách nhân viên đã thuộc dự án
      const resProject = await fetch(`http://localhost:8080/api/projects/${projectId}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "application/json",
        },
      });

      if (!resProject.ok) {
        const txt = await resProject.text();
        console.error("Fetch project employees failed:", resProject.status, txt);
        toast.error("Không thể tải thông tin dự án!");
        setEmployees(normalizeEmployees(allEmployees));
        return;
      }

      const projectData = await resProject.json();
      const projectEmployees = projectData.data?.employees || [];
      const projectEmployeeIds = projectEmployees.map((e) => e.id);

      // Lọc giữ lại nhân viên chưa thuộc dự án
      const availableEmployeesRaw = allEmployees.filter((e) => !projectEmployeeIds.includes(e.id));

      // Chuẩn hoá trường để đồng bộ hiển thị
      const availableEmployees = normalizeEmployees(availableEmployeesRaw);

      setEmployees(availableEmployees);
      setCurrentPage(1);
    } catch (error) {
      console.error("Fetch employees error:", error);
      toast.error("Có lỗi khi tải danh sách nhân viên!");
      setEmployees([]);
    }
  };

  // ------------------ Normalize fields ------------------
  const normalizeEmployees = (list) =>
    (list || []).map((emp) => ({
      id: emp.id,
      staffCode: emp.staffCode ?? emp.staff_code ?? "",
      fullName: emp.fullName ?? emp.full_name ?? "",
      email: emp.email ?? "",
      department: emp.department ?? "",
      role: emp.role ?? "",
      level: emp.level ?? "",
      createdAt: emp.createdAt ?? "",
      updatedAt: emp.updatedAt ?? "",
      createdBy: emp.createdBy ?? "",
      updatedBy: emp.updatedBy ?? "",
    }));

  // ------------------ Thêm nhân viên vào dự án ------------------
  const handleAddToProject = async (employeeId) => {
    if (!projectId) {
      toast.error("Không xác định được dự án. Vui lòng quay lại trang trước.");
      return;
    }

    const payload = {
      employeeId: Number(employeeId),
      projectId: Number(projectId),
    };

    try {
      const res = await fetch(`http://localhost:8080/api/projects/add-employee`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      const data = await res.json();

      if (!res.ok || data.code !== 200) {
        toast.error("Thêm thất bại: " + (data?.message || "Lỗi không xác định"));
        return;
      }

      toast.success("Đã thêm nhân viên vào dự án thành công!");
      setEmployees((prev) => prev.filter((e) => Number(e.id) !== Number(employeeId)));
    } catch (error) {
      console.error("Add employee error:", error);
      toast.error("Lỗi kết nối server!");
    }
  };

  // ------------------ Filter & Sort ------------------
  const filteredEmployees = employees
    .filter((emp) =>
      (emp.fullName || "").toLowerCase().includes(searchTerm.toLowerCase())
    )
    .sort((a, b) =>
      sortOrder === "asc"
        ? (a.fullName || "").localeCompare(b.fullName || "", "vi", { sensitivity: "base" })
        : (b.fullName || "").localeCompare(a.fullName || "", "vi", { sensitivity: "base" })
    );

  // ------------------ Pagination ------------------
  const totalPages = Math.max(1, Math.ceil(filteredEmployees.length / itemsPerPage));
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentEmployees = filteredEmployees.slice(startIndex, endIndex);

  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm]);

  // ------------------ Toggle sort order ------------------
  const toggleSortOrder = () => {
    setSortOrder((prev) => (prev === "asc" ? "desc" : "asc"));
  };

  // ------------------ Render ------------------
  return (
    <div>
      <div className="content-header">
        <h1 className="header-title">Thêm nhân viên vào dự án {projectId}</h1>
        <div className="header-actions">
          <Link to={`/employee-list?source=project&projectId=${projectId}`}>
            <button className="btn btn-secondary">
              <i className="fas fa-arrow-left"></i> Quay lại
            </button>
          </Link>
        </div>
      </div>

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
            {/* <button className="btn btn-info" onClick={toggleSortOrder}>
              <i className="fas fa-sort-alpha-down"></i>{" "}
              {sortOrder === "asc" ? "A → Z" : "Z → A"}
            </button> */}
          </div>
        </div>

        <table width="100%" className="excel-table">
          <thead>
            <tr>
              <th width="1%">STT</th>
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
            {currentEmployees.length > 0 ? (
              currentEmployees.map((emp, index) => (
                <tr key={emp.id}>
                  <td>{startIndex + index + 1}</td>
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
        </table>

        {/* Pagination UI */}
        <div className="pagination-container">
          <div className="pagination-info">
            Hiển thị{" "}
            {filteredEmployees.length === 0
              ? 0
              : `${startIndex + 1}-${Math.min(endIndex, filteredEmployees.length)}`}{" "}
            trong tổng số {filteredEmployees.length} nhân viên
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
    </div>
  );
};

export default EmployeeAddOld;
