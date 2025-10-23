import React, { useEffect, useState } from "react";
import "./dashboard.css";
import { Link, useLocation, useNavigate } from "react-router-dom";

const EmployeeAddOld = () => {
  const navigate = useNavigate();
  const [employees, setEmployees] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const projectId = queryParams.get("projectId");
  const evaluationCycleId = queryParams.get("evaluationCycleId");
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);

  useEffect(() => {
    if (projectId) {
      fetchEmployees();
    }
  }, [projectId]);

  const fetchEmployees = async () => {
    try {
      const resAll = await fetch("http://localhost:8080/api/employees", {
        method: "GET",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "application/json",
        },
      });
      const allData = await resAll.json();
      const allEmployees = allData.data?.content || [];

      const resProject = await fetch(
        `http://localhost:8080/api/projects/${projectId}`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
            "Content-Type": "application/json",
          },
        }
      );
      const projectData = await resProject.json();
      const projectEmployees = projectData.data?.employees || [];

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
    console.log("==== Thêm nhân viên vào dự án ====");
    console.log("projectId:", projectId);
    console.log("evaluationCycleId:", evaluationCycleId);
    console.log("employeeId:", employeeId);

    if (!evaluationCycleId) {
      alert("⚠ Không xác định được Evaluation Cycle. Vui lòng quay lại từ màn hình chọn chu kỳ đánh giá.");
      return;
    }

    const payload = {
      employeeId: Number(employeeId),
      projectId: Number(projectId),
      evaluationCycleId: Number(evaluationCycleId),
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
      console.log("Response từ API:", data);

      if (!res.ok) {
        alert("❌ Thêm thất bại: " + (data?.message || "Lỗi không xác định"));
        return;
      }

      // nếu BE trả về evaluation object trong data, ưu tiên lấy id từ đó
      let foundEvalId = data?.data?.id ?? null;

      // nếu BE không trả evaluation id, gọi API lấy evaluations và tìm bằng employee+project+cycle
      if (!foundEvalId) {
        try {
          // fetch all evaluations (hoặc nếu có API filter thì gọi API filter)
          const evalRes = await fetch(`http://localhost:8080/api/evaluations`, {
            method: "GET",
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
              "Content-Type": "application/json",
            },
          });
          if (evalRes.ok) {
            const evalJson = await evalRes.json();
            let evalList = [];
            if (Array.isArray(evalJson.data)) evalList = evalJson.data;
            else if (Array.isArray(evalJson.data?.content)) evalList = evalJson.data.content;
            else if (Array.isArray(evalJson.content)) evalList = evalJson.content;

            // tìm evaluation match theo employeeId, projectId, evaluationCycleId
            const matched = evalList.find((e) => {
              const eEmployeeId = e.employeeId ?? e.employee?.id;
              const eProjectId = e.projectId ?? e.project?.id;
              const eCycleId = e.evaluationCycleId ?? e.evaluationCycle?.id;
              return (
                Number(eEmployeeId) === Number(employeeId) &&
                Number(eProjectId) === Number(projectId) &&
                Number(eCycleId) === Number(evaluationCycleId)
              );
            });
            if (matched) foundEvalId = matched.id;
          } else {
            console.warn("Không thể fetch evaluations để tìm evaluationId sau khi add-employee.");
          }
        } catch (err) {
          console.error("Lỗi khi fetch evaluations sau add:", err);
        }
      }

      alert("✅ Đã thêm nhân viên vào dự án thành công!");

      // Navigate về trang employee-list, kèm thông tin vừa thêm (dùng để FE hiện 0.0 hoặc id tạm)
      // truyền cả evaluationId (nếu tìm được) để EmployeeTable không báo lỗi khi edit
      navigate(
        `/employee-list?projectId=${projectId}&evaluationCycleId=${evaluationCycleId}`,
        { state: { justAddedEmployeeId: Number(employeeId), justAddedEvaluationId: foundEvalId ?? null } }
      );

    } catch (error) {
      console.error("❌ Add employee error:", error);
      alert("Lỗi kết nối server!");
    }
  };


  const filteredEmployees = employees.filter((emp) =>
    emp.fullName?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const totalPages = Math.ceil(filteredEmployees.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentEmployees = filteredEmployees.slice(startIndex, endIndex);

  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm, employees]);

  return (
    <div>
      <div className="content-header">
        <h1 className="header-title">Thêm nhân viên vào dự án {projectId}</h1>
        <div className="header-actions">
          <Link
            to={
              evaluationCycleId
                ? `/employee-list?projectId=${projectId}&evaluationCycleId=${evaluationCycleId}`
                : `/employee-list?projectId=${projectId}`
            }
          >
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
    </div>
  );
};

export default EmployeeAddOld;