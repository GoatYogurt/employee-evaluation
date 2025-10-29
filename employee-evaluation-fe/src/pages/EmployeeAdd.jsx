import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './dashboard.css';
import { useContext } from "react";
import { ToastContext } from "../contexts/ToastProvider";

function EmployeeAdd() {
  const navigate = useNavigate();
  const { toast } = useContext(ToastContext);

  const [employee, setEmployee] = useState({
    username: '',
    fullName: '',
    staffCode: '',
    email: '',
    password: '',
    department: '',
    level: 'FRESHER',
    role: 'EMPLOYEE',
  });

  const roles = ['PGDTT','ADMIN','PM','DEV','BA','TESTER','UIUX','AI','DATA','QA','VHKT','MKT'];
  const levels = ['FRESHER','JUNIOR','JUNIOR_PLUS','MIDDLE','MIDDLE_PLUS','SENIOR'];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEmployee(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch('http://localhost:8080/api/employees', {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + localStorage.getItem('token')
        },
        body: JSON.stringify(employee),
      });

      if (!res.ok) {
        const text = await res.text();
        throw new Error(`Lỗi API: ${res.status} - ${text}`);
      }

      toast.success("Thêm nhân viên thành công!");
      navigate('/employee-list?added=true');
    } catch (err) {
      console.error("Lỗi khi thêm nhân viên:", err);
      toast.error("Không thể thêm nhân viên");
    }
  };

  return (
    <div className="employ-table">
      <h2>Thêm nhân viên mới</h2>
      <form onSubmit={handleSubmit} className="form-container">
          <div className="form-group">
            <label>Username:
              <input name="username" value={employee.username} onChange={handleChange} />
            </label>
          </div>
          <div className="form-group">
            <label>Fullname:
              <input name="fullName" value={employee.fullName} onChange={handleChange} />
            </label>
          </div>
          <div className="form-group">
            <label>Staffcode:
              <input name="staffCode" value={employee.staffCode} onChange={handleChange} />
            </label>
          </div>
          <div className="form-group">
            <label>Email:
              <input name="email" value={employee.email} onChange={handleChange} />
            </label>
          </div>
          <div className="form-group">
            <label>Password:
              <input name="password" value={employee.password} onChange={handleChange} type="password" />
            </label>
          </div>
          <div className="form-group">
            <label>Department:
              <input name="department" value={employee.department} onChange={handleChange} />
            </label>
          </div>
          <div className="form-group">
            <label>Level:
              <select name="level" value={employee.level} onChange={handleChange}>
                {levels.map(level => (
                  <option key={level} value={level}>{level}</option>
                ))}
              </select>
            </label>
          </div>
      <div className="form-group">
        <label>Role:
          <select name="role" value={employee.role} onChange={handleChange}>
            {roles.map(role => (
              <option key={role} value={role}>{role}</option>
            ))}
          </select>
        </label>
      </div>

      <div className="form-action">
        <button type="submit" className="btn-add-employee">Thêm nhân viên</button>
      </div>
    </form>

    </div>
  );
}

export default EmployeeAdd;
