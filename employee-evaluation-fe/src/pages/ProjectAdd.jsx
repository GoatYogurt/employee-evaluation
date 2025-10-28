import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './dashboard.css';
import { ToastContext } from "../contexts/ToastProvider";

function ProjectAdd() {
  const navigate = useNavigate();

  // ✅ Dữ liệu dự án
  const [project, setProject] = useState({
    code: '',
    isOdc: false,
    managerId: '',
  });

  // ✅ Danh sách nhân viên PM
  const [managers, setManagers] = useState([]);

  // ✅ Lỗi hiển thị (nếu có)
  const [error, setError] = useState('');

  // ✅ Lấy danh sách nhân viên có role = PM
  useEffect(() => {
    const fetchManagers = async () => {
      try {
        const res = await fetch('http://localhost:8080/api/employees', {
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('token'),
          },
        });
        if (!res.ok) throw new Error('Không thể lấy danh sách nhân viên');
        const data = await res.json();

        // ✅ Lọc chỉ lấy những người có role === 'PM'
        const pmList = data.data?.content?.filter(
          (emp) => emp.role === 'PM'
        ) || [];

        setManagers(pmList);
      } catch (err) {
        console.error('Lỗi khi tải danh sách PM:', err);
        setError('Không thể tải danh sách PM');
      }
    };
    fetchManagers();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProject((prev) => ({
      ...prev,
      [name]: name === 'isOdc' ? value === 'true' : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch('http://localhost:8080/api/projects', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + localStorage.getItem('token'),
        },
        body: JSON.stringify(project),
      });

      if (!res.ok) {
        const text = await res.text();
        throw new Error(`Lỗi API: ${res.status} - ${text}`);
      }

      toast.success('Thêm dự án thành công!');
      navigate('/project-list?added=true');
    } catch (err) {
      console.error('❌ Lỗi khi thêm dự án:', err);
      toast.error('Không thể thêm dự án');
    }
  };

  return (
     <div className="project-add-wrapper">
      <div className="project-add-container">
        <h2>Thêm dự án mới</h2>
        {error && <div className="project-error">{error}</div>}

        <form onSubmit={handleSubmit} className="project-add-form">
          <div>
            <label>Mã dự án:</label>
            <input
              name="code"
              required
              value={project.code}
              onChange={handleChange}
            />
          </div>

          <div>
            <label>Loại dự án:</label>
            <select
              name="isOdc"
              required
              value={project.isOdc}
              onChange={handleChange}
            >
              <option value={true}>ODC</option>
              <option value={false}>NOT ODC</option>
            </select>
          </div>

          <div style={{ gridColumn: 'span 2' }}>
            <label>Quản lý (PM):</label>
            <select
              name="managerId"
              required
              value={project.managerId}
              onChange={handleChange}
            >
              <option value="">-- Chọn Quản lý (PM) --</option>
              {managers.map((m) => (
                <option key={m.id} value={m.id}>
                  {m.fullName} ({m.email})
                </option>
              ))}
            </select>
          </div>

          <div className="project-add-actions">
            <button type="submit" className="btn-primary">Thêm dự án</button>
            <button
              type="button"
              className="btn-secondary"
              onClick={() => navigate('/project-list')}
            >
              Hủy
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default ProjectAdd;