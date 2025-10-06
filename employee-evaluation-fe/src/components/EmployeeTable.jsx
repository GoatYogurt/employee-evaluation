import React, { useEffect, useState } from "react";
import "../index.css";
import { Link } from "react-router-dom";

const EmployeeTable = () => {
  const [employees, setEmployees] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);

  const [showEditModal, setShowEditModal] = useState(false);
  const [showViewModal, setShowViewModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [deleteMessage, setDeleteMessage] = useState("");

  useEffect(() => {
    fetchEmployees();
  }, []);

  // Fetch employees
  const fetchEmployees = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/employees", {
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
      const employees = response.data?.content || [];

      const normalized = employees.map((emp) => ({
        id: emp.id,
        staff_code: emp.staffCode,
        full_name: emp.fullName,
        email: emp.email,
        department: emp.department,
        role: emp.role,
        level: emp.level,
      }));

      setEmployees(normalized);
    } catch (error) {
      console.error("Failed to fetch employees:", error);
    }
  };

  // Lọc theo fullname
  const filteredEmployees = employees.filter((emp) =>
    emp.full_name?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Phân trang
  const totalPages = Math.ceil(filteredEmployees.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentEmployees = filteredEmployees.slice(startIndex, endIndex);

  // Xử lý sửa
  const handleEdit = (emp) => {
    setSelectedEmployee(emp);
    setShowEditModal(true);
  };

  const handleEditConfirm = async () => {
    try {
      const res = await fetch(
        `http://localhost:8080/api/employees/${selectedEmployee.id}`, // 👈 dùng id từ BE
        {
          method: "PATCH",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            staffCode: selectedEmployee.staff_code,
            fullName: selectedEmployee.full_name,
            email: selectedEmployee.email,
            department: selectedEmployee.department,
            role: selectedEmployee.role,
            level: selectedEmployee.level,
          }),
        }
      );

      if (!res.ok) {
        const errMsg = await res.text();
        console.error("Update failed:", errMsg);
        alert("Sửa nhân viên thất bại!");
        return;
      }

      const result = await res.json();
      const updatedEmp = result.data;

      setEmployees((prev) =>
        prev.map((emp) =>
          emp.id === updatedEmp.id
            ? {
                id: updatedEmp.id,
                staff_code: updatedEmp.staffCode,
                full_name: updatedEmp.fullName,
                email: updatedEmp.email,
                department: updatedEmp.department,
                role: updatedEmp.role,
                level: updatedEmp.level,
              }
            : emp
        )
      );

      alert("Sửa nhân viên thành công!");
      setShowEditModal(false);
    } catch (err) {
      console.error(err);
      alert("Có lỗi khi sửa nhân viên!");
    }
  };

  // Xử lý xóa
  const handleDelete = async (id) => {
    try {
      const res = await fetch(`http://localhost:8080/api/employees/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "application/json",
        },
      });

      if (!res.ok) throw new Error("Delete failed");

      setEmployees((prev) => prev.filter((emp) => emp.id !== id));
      setDeleteMessage("Xóa nhân viên thành công!");
    } catch (err) {
      console.error(err);
      setDeleteMessage("Xóa nhân viên thất bại!");
    }
    setShowDeleteModal(true);
  };

  // Xử lý xem
  const handleView = (emp) => {
    setSelectedEmployee(emp);
    setShowViewModal(true);
  };

  // =================== JSX ===================
  return (
    <div>
      {/* Header */}
      <div className="content-header">
        <h1 className="header-title">Quản lý nhân viên</h1>
        <div className="header-actions">
          <Link to="/employee-add">
            <button className="btn btn-primary">
              <i className="fas fa-plus"></i> Thêm nhân viên
            </button>
          </Link>
        </div>
      </div>

      {/* Table */}
      <div className="excel-container">
        <div className="table-header">
          <h3 className="table-title">Danh sách nhân viên</h3>
          <div className="table-controls">
            <input
              type="text"
              placeholder="Điền tên nhân viên"
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
            {currentEmployees.length > 0 ? (
              currentEmployees.map((emp, index) => (
                <tr key={emp.id}>
                  <td>{startIndex + index + 1}</td>
                  <td>{emp.staff_code}</td>
                  <td>{emp.full_name}</td>
                  <td>{emp.email}</td>
                  <td>{emp.department}</td>
                  <td>{emp.role}</td>
                  <td>{emp.level}</td>
                  <td>
                    <div className="action-buttons">
                      <button
                        className="btn btn-sm btn-edit"
                        title="Sửa"
                        onClick={() => handleEdit(emp)}
                      >
                        <i className="fas fa-edit"></i>
                      </button>
                      <button
                        className="btn btn-sm btn-delete"
                        title="Xóa"
                        onClick={() => handleDelete(emp.id)}
                      >
                        <i className="fas fa-trash"></i>
                      </button>
                      <button
                        className="btn btn-sm btn-view"
                        title="Xem chi tiết"
                        onClick={() => handleView(emp)}
                      >
                        <i className="fas fa-eye"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="8" style={{ textAlign: "center", padding: "20px" }}>
                  Không tìm thấy nhân viên nào.
                </td>
              </tr>
            )}
          </tbody>
        </table>

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
      </div>

      {/* =================== MODALS =================== */}

      {/* Edit Modal */}
      {showEditModal && selectedEmployee && (
        <div className="modal-overlay" onClick={() => setShowEditModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>Sửa nhân viên</h3>
            <div className="form-group">
              <label>Mã NV</label>
              <input
                type="text"
                value={selectedEmployee.staff_code || ""}
                onChange={(e) =>
                  setSelectedEmployee({
                    ...selectedEmployee,
                    staff_code: e.target.value,
                  })
                }
              />
            </div>
            <div className="form-group">
              <label>Họ và tên</label>
              <input
                type="text"
                value={selectedEmployee.full_name || ""}
                onChange={(e) =>
                  setSelectedEmployee({
                    ...selectedEmployee,
                    full_name: e.target.value,
                  })
                }
              />
            </div>
            <div className="form-group">
              <label>Email</label>
              <input
                type="email"
                value={selectedEmployee.email || ""}
                onChange={(e) =>
                  setSelectedEmployee({
                    ...selectedEmployee,
                    email: e.target.value,
                  })
                }
              />
            </div>
            <div className="form-group">
              <label>Phòng/Ban</label>
              <input
                type="text"
                value={selectedEmployee.department || ""}
                onChange={(e) =>
                  setSelectedEmployee({
                    ...selectedEmployee,
                    department: e.target.value,
                  })
                }
              />
            </div>
            {/* Role select */}
            <div className="form-group">
              <label>Chức vụ (Role)</label>
              <select
                value={selectedEmployee.role || "DEV"}
                onChange={(e) =>
                  setSelectedEmployee({
                    ...selectedEmployee,
                    role: e.target.value,
                  })
                }
              >
                <option value="PGDTT">PGDTT</option>
                <option value="ADMIN">ADMIN</option>
                <option value="PM">PM</option>
                <option value="DEV">DEV</option>
                <option value="TESTER">TESTER</option>
                <option value="BA">BA</option>
                <option value="UIUX">UIUX</option>
                <option value="AI">AI</option>
                <option value="DATA">DATA</option>
                <option value="QA">QA</option>
                <option value="VHKT">VHKT</option>
                <option value="MKT">MKT</option>
              </select>
            </div>
            {/* Level select */}
            <div className="form-group">
              <label>Cấp bậc (Level)</label>
              <select
                value={selectedEmployee.level || "FRESHER"}
                onChange={(e) =>
                  setSelectedEmployee({
                    ...selectedEmployee,
                    level: e.target.value,
                  })
                }
              >
                <option value="FRESHER">FRESHER</option>
                <option value="JUNIOR">JUNIOR</option>
                <option value="JUNIOR_PLUS">JUNIOR_PLUS</option>
                <option value="MIDDLE">MIDDLE</option>
                <option value="MIDDLE_PLUS">MIDDLE_PLUS</option>
                <option value="SENIOR">SENIOR</option>
              </select>
            </div>

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
      )}

      {/* Delete Modal */}
      {showDeleteModal && (
        <div className="modal-overlay" onClick={() => setShowDeleteModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
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
      {showViewModal && selectedEmployee && (
        <div className="modal-overlay" onClick={() => setShowViewModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>Thông tin nhân viên</h3>
            <table className="excel-table">
              <tbody style={{ border: "none", color: "#000" }}>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Mã NV</td>
                  <td>{selectedEmployee.staff_code}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Họ và tên</td>
                  <td>{selectedEmployee.full_name}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Email</td>
                  <td>{selectedEmployee.email}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Phòng/Ban</td>
                  <td>{selectedEmployee.department}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Chức vụ</td>
                  <td>{selectedEmployee.role}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Cấp bậc</td>
                  <td>{selectedEmployee.level}</td>
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

export default EmployeeTable;
