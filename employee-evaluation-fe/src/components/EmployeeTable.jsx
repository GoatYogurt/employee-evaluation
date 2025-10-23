// EmployeeTable.jsx
import React, { useEffect, useState, useRef } from "react";
import "../index.css";
import { Link, useLocation, useNavigate } from "react-router-dom";

const BASE = "http://localhost:8080/api";

const EditableCell = ({ evaluation, field, updateEvaluationField, style, title }) => {
  // Nếu không có evaluation (chưa có bản đánh giá) -> render td không editable
  if (!evaluation || !evaluation.id) {
    return (
      <td style={{ ...(style || {}), minWidth: 120 }} title="Chưa có bản đánh giá">
        {evaluation?.[field] ?? ""}
      </td>
    );
  }

  const initial = evaluation?.[field] ?? "";
  const [editing, setEditing] = useState(false);
  const [isComposing, setIsComposing] = useState(false);
  const savingRefLocal = useRef(false);
  const tdRef = useRef(null);

  // Khi evaluation thay đổi từ bên ngoài (ví dụ fetch mới), cập nhật nội dung trong td
  useEffect(() => {
    if (!tdRef.current) return;
    // nếu đang chỉnh sửa hoặc đang composition thì không overwrite nội dung
    if (editing || isComposing) return;
    tdRef.current.textContent = initial;
  }, [initial, editing, isComposing]);

  const saveIfChanged = async (newValue) => {
    const evaluationId = evaluation?.id;
    if (!evaluationId) {
      alert("⚠ Không thể lưu: Evaluation ID không tồn tại.");
      return;
    }
    const trimmedNew = (newValue ?? "").trim();
    const currentRemote = evaluation?.[field] ?? "";
    if (trimmedNew === (currentRemote ?? "").trim()) return;
    if (savingRefLocal.current) return;
    try {
      savingRefLocal.current = true;
      await updateEvaluationField(evaluationId, field, trimmedNew);
    } finally {
      savingRefLocal.current = false;
    }
  };

  const handleCompositionStart = () => {
    setIsComposing(true);
  };

  const handleCompositionEnd = async (e) => {
    setIsComposing(false);
    // sau khi composition kết thúc, lưu giá trị
    const target = e.currentTarget;
    if (!target) return;
    const newValue = (target.textContent || "").trim();
    setEditing(false);
    await saveIfChanged(newValue);
  };

  const handleKeyDown = async (e) => {
    if (isComposing) return; // không xử lý khi đang gõ dấu
    if (e.key === "Enter") {
      e.preventDefault();
      const target = e.currentTarget;
      if (!target) return;
      const newValue = (target.textContent || "").trim();
      setEditing(false);
      await saveIfChanged(newValue);
      try { target.blur(); } catch {}
    }
  };

  const handleBlur = async (e) => {
    if (isComposing) return; // tránh lưu khi đang gõ IME
    const target = e.currentTarget;
    if (!target) return;
    const newValue = (target.textContent || "").trim();
    setEditing(false);
    await saveIfChanged(newValue);
  };

  // onInput chỉ cập nhật nội dung nội bộ (không re-render nội dung từ state)
  const handleInput = (e) => {
    // nothing here that forces React to re-render the content; keep it minimal
    // we can still read value from e.currentTarget.textContent when needed
  };

  return (
    <td
      ref={tdRef}
      contentEditable
      suppressContentEditableWarning
      onCompositionStart={handleCompositionStart}
      onCompositionEnd={handleCompositionEnd}
      onFocus={(e) => {
        setEditing(true);
        try {
          if (e.currentTarget) e.currentTarget.dataset.oldValue = (e.currentTarget.textContent || "").trim();
        } catch {}
      }}
      onInput={handleInput}
      onKeyDown={handleKeyDown}
      onBlur={handleBlur}
      style={{ ...(style || {}), outline: "none", cursor: "text", minWidth: 120 }}
      title={title || "Nhấn Enter để lưu"}
    >
      {/* Không render {value} ở đây — để tránh controlled re-render trong khi dùng IME */}
      {initial}
    </td>
  );
};

const EmployeeTable = () => {
  // -------- state --------
  const [employees, setEmployees] = useState([]);
  const [evaluations, setEvaluations] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);

  // modals / selected
  const [showEditModal, setShowEditModal] = useState(false);
  const [showViewModal, setShowViewModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedEmployee, setSelectedEmployee] = useState(null);

  // evaluate
  const [showEvaluateModal, setShowEvaluateModal] = useState(false);
  const [criteriaList, setCriteriaList] = useState([]);
  const [scores, setScores] = useState({});
  const [showGuide, setShowGuide] = useState(null);
  const [submittingEvaluation, setSubmittingEvaluation] = useState(false);

  // router / params
  const location = useLocation();
  const navigate = useNavigate();
  const query = new URLSearchParams(location.search);
  const evaluationCycleIdFromUrl = query.get("evaluationCycleId");
  const projectIdFromUrl = query.get("projectId");

  // determine mode
  const source = query.get("source"); // 'project' hoặc 'evaluation'
  const isEvaluationMode = source === "evaluation";
  const isProjectMode = source === "project";


  // ✅ Ép kiểu và kiểm tra chắc chắn
  const projectId = projectIdFromUrl ? Number(projectIdFromUrl) : null;
  const evalCycleId = evaluationCycleIdFromUrl ? Number(evaluationCycleIdFromUrl) : null;

  const savingRef = useRef({}); // keys `${evaluationId}-${field}`

  // ------------------ initial load ------------------
  useEffect(() => {
    fetchEmployees();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [projectId, evaluationCycleIdFromUrl]);

  // ------------------ FETCH EMPLOYEES ------------------
  const fetchEmployees = async () => {
    try {
      let url = `${BASE}/employees?size=1000`;
      if (projectId) url = `${BASE}/projects/${projectId}`;

      const res = await fetch(url, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "application/json",
        },
      });

      if (!res.ok) {
        const txt = await res.text();
        console.error("API ERROR (fetchEmployees):", res.status, txt);
        setEmployees([]);
        return;
      }

      const response = await res.json();
      let employeesData = [];

      if (projectId) {
        employeesData =
          response.data?.employees ||
          response.data?.data?.employees ||
          (Array.isArray(response.data?.content) ? response.data.content[0]?.employees : null) ||
          response.data ||
          [];
      } else {
        employeesData = response.data?.content || response.data || [];
      }

      if (!Array.isArray(employeesData)) employeesData = []; // fallback

      const normalized = (employeesData || []).map((emp) => ({
        id: emp.id,
        staff_code: emp.staffCode ?? emp.staff_code ?? "",
        full_name: emp.fullName ?? emp.full_name ?? "",
        email: emp.email ?? "",
        department: emp.department ?? "",
        role: emp.role ?? "",
        level: emp.level ?? "",
        createdAt: emp.createdAt ?? "",
        updatedAt: emp.updatedAt ?? "",
        createdBy: emp.createdBy ?? "",
        updatedBy: emp.updatedBy ?? "",
      }));

      setEmployees(normalized);
      setCurrentPage(1);

      await fetchEvaluations();
    } catch (err) {
      console.error("Failed to fetch employees:", err);
      setEmployees([]);
    }
  };


 // ------------------ FETCH EVALUATIONS ------------------
// ------------------ FETCH EVALUATIONS (fixed) ------------------
const fetchEvaluations = async () => {
  try {
    // Build URL with optional filters to increase chance get the newly created record
    // If backend supports filtering by projectId/evaluationCycleId, include them.
    const params = new URLSearchParams();
    // try to get a large page size so the new record isn't paginated away
    params.set("size", "1000");

    if (projectIdFromUrl) params.set("projectId", projectIdFromUrl);
    if (evaluationCycleIdFromUrl) params.set("evaluationCycleId", evaluationCycleIdFromUrl);

    const url = `${BASE}/evaluations?${params.toString()}`;

    console.debug("Fetching evaluations from:", url);

    const res = await fetch(url, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
        "Content-Type": "application/json",
      },
    });

    if (!res.ok) {
      const txt = await res.text();
      console.error("Fetch evaluations failed:", res.status, txt);
      setEvaluations([]);
      return;
    }

    const json = await res.json();
    let evalList = [];
    if (Array.isArray(json.data)) evalList = json.data;
    else if (Array.isArray(json.data?.content)) evalList = json.data.content;
    else if (Array.isArray(json.content)) evalList = json.content;
    else evalList = [];

    // Normalize entries so we always have employeeId/projectId/evaluationCycleId as numbers (or null)
    const normalized = evalList.map((ev) => {
      const employeeIdRaw = ev.employeeId ?? ev.employee?.id;
      const projectIdRaw = ev.projectId ?? ev.project?.id;
      const cycleIdRaw = ev.evaluationCycleId ?? ev.evaluationCycle?.id ?? null;
      return {
        ...ev,
        employeeId: employeeIdRaw != null ? Number(employeeIdRaw) : null,
        projectId: projectIdRaw != null ? Number(projectIdRaw) : null,
        evaluationCycleId: cycleIdRaw != null ? Number(cycleIdRaw) : null,
      };
    });

    // Filter: chỉ giữ evaluations thuộc project nếu projectIdFromUrl có; nếu có evaluationCycleFromUrl thì ưu tiên lọc theo đó
    const filtered = normalized.filter((e) => {
      if (projectIdFromUrl && Number(e.projectId) !== Number(projectIdFromUrl)) return false;

      if (evaluationCycleIdFromUrl) {
        // nếu record có evaluationCycleId, so sánh; nếu record không có field này (null), vẫn giữ nó
        if (e.evaluationCycleId != null && Number(e.evaluationCycleId) !== Number(evaluationCycleIdFromUrl)) return false;
      }
      return true;
    });

    // debug: giúp kiểm tra có thấy id vừa tạo hay không
    const ids = filtered.map((x) => x.id);
    console.debug("Evaluations fetched (filtered) count:", filtered.length, "ids:", ids);

    setEvaluations(filtered);
  } catch (err) {
    console.error("Failed to fetch evaluations:", err);
    setEvaluations([]);
  }
};


  // ------------------ MERGE EMPLOYEES + EVALUATION ------------------
const mergedEmployees = employees
  .map((emp) => {
    // chắc chắn convert id của emp sang Number để so sánh
    const empId = emp?.id != null ? Number(emp.id) : null;

    // Tìm evaluation phù hợp: ưu tiên match cả employee + project + (nếu có) evaluationCycle
    const evaluation =
      evaluations.find((e) => {
        const evEmpId = e.employeeId != null ? Number(e.employeeId) : null;
        const evProjectId = e.projectId != null ? Number(e.projectId) : null;
        const evCycleId = e.evaluationCycleId != null ? Number(e.evaluationCycleId) : null;

        if (empId == null) return false;
        if (evEmpId !== empId) return false;

        // nếu có projectIdFromUrl thì bắt buộc match project
        if (projectIdFromUrl && evProjectId !== Number(projectIdFromUrl)) return false;

        // nếu có evaluationCycleIdFromUrl thì ưu tiên match cycle; 
        // nếu evCycleId is null => không loại (giữ) — vì backend có thể trả null
        if (evaluationCycleIdFromUrl && evCycleId != null && evCycleId !== Number(evaluationCycleIdFromUrl)) return false;

        return true;
      }) ?? null;

    return { ...emp, evaluation };
  })
  .sort((a, b) => (a.full_name || "").localeCompare(b.full_name || "", "vi", { sensitivity: "base" }));

  // ------------------ Edit handlers ------------------
  const handleEdit = (emp) => {
    setSelectedEmployee(emp);
    setShowEditModal(true);
  };

  const handleEditConfirm = async () => {
    try {
      const res = await fetch(`${BASE}/employees/${selectedEmployee.id}`, {
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
      });

      if (!res.ok) {
        const err = await res.text();
        console.error("Update failed:", err);
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

  // ------------------ Delete handler ------------------
    const handleDelete = async (employeeId) => {
    const confirmDelete = window.confirm("Bạn có chắc muốn xóa nhân viên này?");
    if (!confirmDelete) return;

    try {
      let response;
      if (projectId) {
        // ✅ API mới: PUT /api/projects/remove-employee với body JSON
        const url = `${BASE}/projects/remove-employee`;
        const body = {
          projectId: Number(projectId),
          employeeId: Number(employeeId),
        };

        response = await fetch(url, {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify(body),
        });
      } else {
        const url = `${BASE}/employees/${employeeId}`;
        response = await fetch(url, {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
            "Content-Type": "application/json",
          },
        });
      }

      if (!response.ok) {
        const errText = await response.text();
        console.error("Delete failed:", errText);
        alert("Xóa thất bại!");
        return;
      }

      alert("Xóa thành công!");
      await fetchEmployees();
      if (projectId) await fetchEvaluations();
    } catch (err) {
      console.error("Error deleting employee:", err);
      alert("Có lỗi khi xóa nhân viên!");
    }
  };

  const handleView = (emp) => {
    setSelectedEmployee(emp);
    setShowViewModal(true);
  };

  // ------------------ Evaluate modal flow ------------------
  const handleEvaluate = async (emp) => {
    setSelectedEmployee(emp);
    setCriteriaList([]);
    setScores({});
    setShowGuide(null);

    try {
      const res = await fetch(`${BASE}/criteria`, {
        method: "GET",
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}`, "Content-Type": "application/json" },
      });
      if (!res.ok) {
        console.error("Failed to fetch criteria:", res.status, await res.text());
        alert("Không thể tải danh sách tiêu chí");
        return;
      }
      const json = await res.json();
      let list = [];
      if (Array.isArray(json.data?.content)) list = json.data.content;
      else if (Array.isArray(json.data)) list = json.data;
      else if (Array.isArray(json.content)) list = json.content;
      else if (Array.isArray(json)) list = json;

      const normalized = list.map((c) => ({
        id: c.id,
        name: c.name,
        description: c.description || c.guidance || "",
        weight: typeof c.weight === "number" ? c.weight : parseFloat(c.weight) || 0,
      }));
      const initScores = {};
      normalized.forEach((c) => (initScores[c.id] = ""));
      setCriteriaList(normalized);
      setScores(initScores);
      setShowEvaluateModal(true);
    } catch (err) {
      console.error("Fetch criteria error:", err);
      alert("Có lỗi khi tải tiêu chí");
    }
  };

  const handleScoreChange = (criterionId, value) => {
    if (value === "") {
      setScores((prev) => ({ ...prev, [criterionId]: "" }));
      return;
    }
    const sanitized = String(value).replace(",", ".").trim();
    if (!/^(\d+(\.\d*)?|\.\d+)$/.test(sanitized)) return;
    const num = parseFloat(sanitized);
    if (isNaN(num) || num < 0 || num > 5) return;
    setScores((prev) => ({ ...prev, [criterionId]: sanitized }));
  };

  // ------------------ submit evaluation (new flow: call create-multiple directly) ------------------
  const handleSubmitEvaluation = async () => {
    if (!selectedEmployee) return;
    const filled = Object.entries(scores)
      .filter(([_, v]) => v !== "")
      .map(([k, v]) => ({ criterionId: Number(k), score: Number(v) }));
    if (filled.length === 0) {
      alert("Vui lòng nhập ít nhất 1 điểm trước khi xác nhận.");
      return;
    }

    // xác định evaluationCycleId để gửi cùng payload
    let evalCycleIdLocal = evaluationCycleIdFromUrl || null;

    if (!evalCycleIdLocal && projectId) {
      try {
        const cycleRes = await fetch(`${BASE}/evaluation-cycles/active`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
            "Content-Type": "application/json",
          },
        });
        if (cycleRes.ok) {
          const cjson = await cycleRes.json();
          const cycles = cjson.data?.content || [];
          const matched = cycles.find(
            (c) => Array.isArray(c.projectIds) && c.projectIds.map(Number).includes(Number(projectId))
          );
          evalCycleIdLocal = matched ? matched.id : cycles[0]?.id;
        }
      } catch (err) {
        console.error("Failed to fetch active cycles:", err);
      }
    }

    if (!evalCycleIdLocal) {
      alert("Không thể xác định kỳ đánh giá để gửi điểm. Vui lòng chọn evaluation cycle hoặc kiểm tra cấu hình.");
      return;
    }

    const payload = {
      employeeId: Number(selectedEmployee.id),
      projectId: Number(projectId),
      evaluationCycleId: Number(evalCycleIdLocal),
      scores: filled,
    };

    try {
      setSubmittingEvaluation(true);
      const res = await fetch(`${BASE}/evaluation-scores/create-multiple`, {
        method: "POST",
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}`, "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        const txt = await res.text();
        console.error("Evaluation submit failed:", res.status, txt);
        try {
          const jsonErr = JSON.parse(txt);
          alert(`Gửi đánh giá thất bại: ${jsonErr.message || txt}`);
        } catch {
          alert("Gửi đánh giá thất bại");
        }
        return;
      }

      const json = await res.json();
      // Theo mẫu API bạn gửi: response.data là object evaluation mới
      const createdEvaluation = json.data ?? json; // fallback
      if (!createdEvaluation || !createdEvaluation.id) {
        console.warn("API trả về nhưng không có evaluation id:", createdEvaluation);
        // để an toàn: fetch toàn bộ evaluations
        await fetchEvaluations();
      } else {
        const normalized = {
          ...createdEvaluation,
          id: createdEvaluation.id,
          employeeId:
            createdEvaluation.employeeId ?? createdEvaluation.employee?.id ?? Number(selectedEmployee.id),
          projectId: createdEvaluation.projectId ?? createdEvaluation.project?.id ?? Number(projectId),
          evaluationCycleId:
            createdEvaluation.evaluationCycleId ?? createdEvaluation.evaluationCycle?.id ?? Number(evalCycleIdLocal),
        };
        // cập nhật local state (nếu đã có evaluation cũ cho employee+cycle thì replace)
        setEvaluations((prev) => {
          const filtered = prev.filter(
            (e) =>
              !(
                Number(e.employeeId) === Number(normalized.employeeId) &&
                Number(e.evaluationCycleId) === Number(normalized.evaluationCycleId) &&
                Number(e.projectId) === Number(normalized.projectId)
              )
          );
          return [...filtered, normalized];
        });
      }

      setShowEvaluateModal(false);
    } catch (err) {
      console.error("Submit evaluation error:", err);
      alert("Có lỗi khi gửi đánh giá");
    } finally {
      setSubmittingEvaluation(false);
    }
  };

  // ------------------ updateEvaluationField (PATCH) ------------------
  const updateEvaluationField = async (evaluationId, field, newValue) => {
    if (!evaluationId) {
      alert("Không thể lưu: chưa có bản đánh giá (Evaluation) cho nhân viên này trong kỳ đánh giá hiện tại.");
      return;
    }

    const key = `${evaluationId}-${field}`;
    if (savingRef.current[key]) return;

    try {
      savingRef.current[key] = true;
      const payload = { [field]: newValue };
      const res = await fetch(`${BASE}/evaluations/${evaluationId}`, {
        method: "PATCH",
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}`, "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        const txt = await res.text();
        console.error("Update feedback failed:", txt);
        alert("Cập nhật phản hồi thất bại!");
        return;
      }

      // update local evaluations state
      setEvaluations((prev) => prev.map((ev) => (ev.id === evaluationId ? { ...ev, [field]: newValue } : ev)));
      console.log(`Updated evaluation ${evaluationId} ${field}`, newValue);
    } catch (err) {
      console.error("Error updating feedback:", err);
      alert("Có lỗi khi cập nhật phản hồi!");
    } finally {
      savingRef.current[key] = false;
    }
  };

  // ------------------ Filter & Pagination ------------------
  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm, employees.length]);

  const filteredEmployees = mergedEmployees.filter((emp) =>
    (emp.full_name || "").toLowerCase().includes(searchTerm.toLowerCase())
  );

  const totalPages = Math.max(1, Math.ceil(filteredEmployees.length / itemsPerPage));
  useEffect(() => {
    if (currentPage > totalPages) setCurrentPage(totalPages);
  }, [totalPages, currentPage]);

  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentEmployees = filteredEmployees.slice(startIndex, endIndex);

  // ------------------ Export to Excel ------------------
  const handleExportExcel = async () => {
    const fileName = document.getElementById("fileNameInput").value || `EvaluationCycle_${evaluationCycleIdFromUrl}.xlsx`;

    // Lấy token từ localStorage (thường backend trả về lúc login)
    const token = localStorage.getItem("token") || sessionStorage.getItem("token");

    try {
      const response = await fetch(`${BASE}/evaluation-cycles/${evaluationCycleIdFromUrl}/export`, {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });

      if (!response.ok) {
        if (response.status === 403) {
          alert("Bạn không có quyền hoặc token bị hết hạn!");
        } else {
          alert("Xuất file thất bại!");
        }
        return;
      }

      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = fileName;
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
      document.getElementById("exportDialog").close();

      alert("Xuất file thành công!");
    } catch (error) {
      console.error("Lỗi xuất file:", error);
    }
  };


  // ------------------ Styles ------------------
  const editableCellStyle = {
    minWidth: "160px",
    cursor: "text",
    outline: "none",
    backgroundColor: "transparent",
    padding: "6px 8px",
    whiteSpace: "pre-wrap",
    wordBreak: "break-word",
    verticalAlign: "top",
    border: "1px solid #e6e6e6",
    borderCollapse: "collapse",
  };

  // ------------------ Render ------------------
  return (
    <div>
      <div className="content-header">
        <h1 className="header-title">{projectId ? `Danh sách nhân viên của dự án ${projectId}` : "Quản lý nhân viên"}</h1>
        <div className="header-actions">
        {!projectId && 
        <Link to="/employee-add">
          <button className="btn btn-primary">
            <i className="fas fa-plus"></i> Thêm nhân viên
          </button>
        </Link>}

        {isEvaluationMode && (<>
            <button
              className="btn btn-primary"
              onClick={() => document.getElementById("exportDialog").showModal()}
            >
              <i className="fa-solid fa-arrow-right"></i> Xuất Excel
            </button>
          </>
        )}

        {isProjectMode && 
        <Link to={`/employee-add-old?projectId=${projectId}&evaluationCycleId=${evaluationCycleIdFromUrl ?? ""}`}>
          <button className="btn btn-success"><i className="fas fa-user-plus">
            </i> Thêm nhân viên vào dự án
          </button>
        </Link>}
        </div>

      </div>

      <div className="excel-container">
        <div className="table-header">
          <h3 className="table-title">{projectId ? "Danh sách nhân viên thuộc dự án" : "Danh sách nhân viên"}</h3>
          <div className="table-controls">
            <input type="text" placeholder="Điền tên nhân viên" className="search-box" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
            <button className="btn btn-warning" onClick={() => { fetchEmployees(); fetchEvaluations(); }}><i className="fas fa-sync-alt"></i> Làm mới</button>
          </div>
        </div>

        <div style={{ maxHeight: "600px", overflowY: "auto", overflowX: "auto", border: "1px solid #ccc", borderRadius: 6 }}>
          <table className="excel-table" style={{ width: "200%", tableLayout: "fixed" }}>
            <thead>
              <tr>
                <th style={{ width: "1%" }}>STT</th>
                <th style={{ width: "2%" }}>Mã NV</th>
                <th style={{ width: "8%" }}>Họ và tên</th>
                <th style={{ width: "8%" }}>Email</th>
                <th style={{ width: "6%" }}>Phòng/Ban</th>
                <th style={{ width: "8%" }}>Chức vụ</th>
                <th style={{ width: "8.5%" }}>Cấp bậc</th>
                {isEvaluationMode && <>
                  <th>Tổng điểm</th>
                  <th>Mức độ hoàn thành</th>
                  <th>Xếp hạng KI</th>
                  <th>Phản hồi Quản Lý</th>
                  <th>Phản hồi Khách Hàng</th>
                  <th>Ghi chú</th>
                </>}
                <th style={{ width: "12.5%" }}>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {currentEmployees.length > 0 ? currentEmployees.map((emp, idx) => {
                const evaluation = emp.evaluation ?? null;
                return (
                  <tr key={emp.id}>
                    <td>{startIndex + idx + 1}</td>
                    <td>{emp.staff_code}</td>
                    <td>{emp.full_name}</td>
                    <td>{emp.email}</td>
                    <td>{emp.department}</td>
                    <td>{emp.role}</td>
                    <td>{emp.level}</td>

                    {isEvaluationMode && <>
                      <td>{evaluation?.totalScore ?? "-"}</td>
                      <td>{evaluation?.completionLevel ?? "Chưa đánh giá"}</td>
                      <td>{evaluation?.kiRanking ?? "-"}</td>

                      <EditableCell evaluation={evaluation} field="managerFeedback" updateEvaluationField={updateEvaluationField} style={editableCellStyle} title="Nhấn Enter để lưu manager feedback" />
                      <EditableCell evaluation={evaluation} field="customerFeedback" updateEvaluationField={updateEvaluationField} style={editableCellStyle} title="Nhấn Enter để lưu customer feedback" />
                      <EditableCell evaluation={evaluation} field="note" updateEvaluationField={updateEvaluationField} style={editableCellStyle} title="Nhấn Enter để lưu note" />
                    </>}

                    <td>
                      <div className="action-buttons">
                        <button className="btn btn-sm btn-edit" title="Sửa" onClick={() => handleEdit(emp)}><i className="fas fa-edit" /></button>
                        <button className="btn btn-sm btn-delete" title="Xóa" onClick={() => handleDelete(emp.id)}><i className="fas fa-trash" /></button>
                        <button className="btn btn-sm btn-view" title="Xem chi tiết" onClick={() => handleView(emp)}><i className="fas fa-eye" /></button>
                        {isEvaluationMode && <button className="btn btn-sm btn-primary" title="Đánh giá" onClick={() => handleEvaluate(emp)}><i className="fas fa-star" /></button>}
                      </div>
                    </td>
                  </tr>
                );
              }) : (
                <tr>
                  <td colSpan={projectId ? 14 : 8} style={{ textAlign: "center", padding: "20px" }}>Không tìm thấy nhân viên nào.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        <div className="pagination-container">
          <div className="pagination-info">
            Hiển thị {filteredEmployees.length === 0 ? 0 : `${startIndex + 1}-${Math.min(endIndex, filteredEmployees.length)}`} trong tổng số {filteredEmployees.length} nhân viên
          </div>
          <div className="pagination-controls">
            <button className="pagination-btn" onClick={() => setCurrentPage((p) => Math.max(p - 1, 1))} disabled={currentPage === 1}>‹ Trước</button>
            {Array.from({ length: totalPages }, (_, i) => {
              const pageNum = i + 1;
              return <button key={pageNum} className={`pagination-btn ${currentPage === pageNum ? "active" : ""}`} onClick={() => setCurrentPage(pageNum)}>{pageNum}</button>;
            })}
            <button className="pagination-btn" onClick={() => setCurrentPage((p) => Math.min(p + 1, totalPages))} disabled={currentPage === totalPages}>Sau ›</button>
          </div>
        </div>
      </div>

      {/* Edit Modal */}
      {showEditModal && selectedEmployee && (
        <div className="modal-overlay" onClick={() => setShowEditModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>Sửa nhân viên</h3>
            <div className="form-group"><label>Mã NV</label><input type="text" value={selectedEmployee.staff_code || ""} onChange={(e) => setSelectedEmployee({ ...selectedEmployee, staff_code: e.target.value })} /></div>
            <div className="form-group"><label>Họ và tên</label><input type="text" value={selectedEmployee.full_name || ""} onChange={(e) => setSelectedEmployee({ ...selectedEmployee, full_name: e.target.value })} /></div>
            <div className="form-group"><label>Email</label><input type="email" value={selectedEmployee.email || ""} onChange={(e) => setSelectedEmployee({ ...selectedEmployee, email: e.target.value })} /></div>
            <div className="form-group"><label>Phòng/Ban</label><input type="text" value={selectedEmployee.department || ""} onChange={(e) => setSelectedEmployee({ ...selectedEmployee, department: e.target.value })} /></div>
            <div className="form-group"><label>Chức vụ (Role)</label>
              <select value={selectedEmployee.role || "DEV"} onChange={(e) => setSelectedEmployee({ ...selectedEmployee, role: e.target.value })}>
                <option value="PGDTT">PGDTT</option><option value="ADMIN">ADMIN</option><option value="PM">PM</option><option value="DEV">DEV</option><option value="TESTER">TESTER</option><option value="BA">BA</option><option value="UIUX">UIUX</option><option value="AI">AI</option><option value="DATA">DATA</option><option value="QA">QA</option><option value="VHKT">VHKT</option><option value="MKT">MKT</option>
              </select>
            </div>
            <div className="form-group"><label>Cấp bậc (Level)</label>
              <select value={selectedEmployee.level || "FRESHER"} onChange={(e) => setSelectedEmployee({ ...selectedEmployee, level: e.target.value })}>
                <option value="FRESHER">FRESHER</option><option value="JUNIOR">JUNIOR</option><option value="JUNIOR_PLUS">JUNIOR_PLUS</option><option value="MIDDLE">MIDDLE</option><option value="MIDDLE_PLUS">MIDDLE_PLUS</option><option value="SENIOR">SENIOR</option>
              </select>
            </div>
            <button className="btn btn-primary" onClick={handleEditConfirm}>Xác nhận</button>
            <button className="btn btn-secondary" onClick={() => setShowEditModal(false)}>Hủy</button>
          </div>
        </div>
      )}

      {/* View Modal */}
      {showViewModal && selectedEmployee && (
        <div className="modal-overlay" onClick={() => setShowViewModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>Thông tin nhân viên</h3>
            <table className="excel-table"><tbody style={{ border: "none", color: "#000" }}>
              <tr><td style={{ fontWeight: "bold" }}>Mã NV</td><td>{selectedEmployee.staff_code}</td></tr>
              <tr><td style={{ fontWeight: "bold" }}>Họ và tên</td><td>{selectedEmployee.full_name}</td></tr>
              <tr><td style={{ fontWeight: "bold" }}>Email</td><td>{selectedEmployee.email}</td></tr>
              <tr><td style={{ fontWeight: "bold" }}>Phòng/Ban</td><td>{selectedEmployee.department}</td></tr>
              <tr><td style={{ fontWeight: "bold" }}>Chức vụ</td><td>{selectedEmployee.role}</td></tr>
              <tr><td style={{ fontWeight: "bold" }}>Cấp bậc</td><td>{selectedEmployee.level}</td></tr>
              <tr>
                <td style={{ fontWeight: "bold" }}>Ngày tạo</td>
                <td>{formatDateTime(selectedEmployee.createdAt)}</td>
              </tr>
              <tr>
                <td style={{ fontWeight: "bold" }}>Ngày cập nhật</td>
                <td>{formatDateTime(selectedEmployee.updatedAt)}</td>
              </tr>
              <tr><td style={{ fontWeight: "bold" }}>Người tạo</td><td>{selectedEmployee.createdBy}</td></tr>
              <tr><td style={{ fontWeight: "bold" }}>Người cập nhật</td><td>{selectedEmployee.updatedBy}</td></tr>
            </tbody></table>
            <button className="btn btn-secondary" onClick={() => setShowViewModal(false)}>Đóng</button>
          </div>
        </div>
      )}

      {/* Evaluate Modal */}
      {showEvaluateModal && selectedEmployee && (
        <div className="modal-overlay" onClick={() => setShowEvaluateModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>Đánh giá nhân viên</h3>
            <p>Bạn đang đánh giá nhân viên: <strong>{selectedEmployee.full_name}</strong></p>
            <div style={{ marginTop: 12 }}>
              {criteriaList.length === 0 ? <p>Không có tiêu chí.</p> : (
                <table className="excel-table" style={{ width: "100%", marginBottom: 12 }}>
                  <thead><tr><th>Tên tiêu chí</th><th>Trọng số</th><th>Điểm (Max 5)</th><th>Hướng dẫn</th></tr></thead>
                  <tbody>
                    {criteriaList.map(c => (<tr key={c.id}><td>{c.name}</td><td>{Number(c.weight)}</td>
                      <td><input className="search-box" style={{ width: 120 }} placeholder={Math.abs(c.weight) === 1 ? "0-0.5" : "0-5"} value={scores[c.id] ?? ""} onChange={(e) => handleScoreChange(c.id, e.target.value)} /></td>
                      <td><button className="btn btn-sm btn-view" title="Hướng dẫn" onClick={() => setShowGuide(c.id)}><i className="fas fa-info-circle" /></button></td>
                    </tr>))}
                  </tbody>
                </table>
              )}
            </div>
            <div style={{ display: "flex", justifyContent: "flex-end", gap: 8 }}>
              <button className="btn btn-secondary" onClick={() => setShowEvaluateModal(false)}>Hủy</button>
              <button className="btn btn-primary" onClick={handleSubmitEvaluation} disabled={submittingEvaluation}>{submittingEvaluation ? "Đang gửi..." : "Xác nhận"}</button>
            </div>
          </div>
        </div>
      )}

      {/* Guide popup */}
      {showGuide && (
        <div className="modal-overlay" onClick={() => setShowGuide(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h4>Hướng dẫn tiêu chí</h4>
            <div style={{ marginTop: 8 }}>
              {(() => {
                const c = criteriaList.find(x => x.id === showGuide);
                if (!c) return <p>Không tìm thấy tiêu chí.</p>;
                return <p style={{ whiteSpace: "pre-wrap" }}>{c.description || "Không có hướng dẫn"}</p>;
              })()}
            </div>
            <div style={{ display: "flex", justifyContent: "flex-end", marginTop: 12 }}>
              <button className="btn btn-secondary" onClick={() => setShowGuide(null)}>Đóng</button>
            </div>
          </div>
        </div>
      )}

        <dialog id="exportDialog" className="dialog-box">
        <form
          method="dialog"
          style={{ padding: "20px", minWidth: "350px" }}
          onSubmit={(e) => e.preventDefault()}
        >
          <h3>Xuất Excel</h3>
          <label>Tên file:</label>
          <input
            type="text"
            id="fileNameInput"
            className="form-control"
            defaultValue={`EvaluationCycle_${evaluationCycleIdFromUrl || 'Unknown'}.xlsx`}
            style={{ marginBottom: "15px", marginTop: "5px" }}
          />

          <div style={{ display: "flex", justifyContent: "flex-end", gap: "10px" }}>
            <button
              className="btn btn-secondary"
              onClick={() => document.getElementById("exportDialog").close()}
            >
              Hủy
            </button>
            <button
              className="btn btn-success"
              onClick={handleExportExcel}
            >
              Xác nhận xuất
            </button>
          </div>
        </form>
      </dialog>

    </div>
  );
};

export default EmployeeTable;
