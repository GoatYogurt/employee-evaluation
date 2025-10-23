import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './dashboard.css';

function EmployeeAdd() {
  const navigate = useNavigate();
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

      alert("Thêm nhân viên thành công!");
      navigate('/employee-list?added=true');
    } catch (err) {
      console.error("❌ Lỗi khi thêm nhân viên:", err);
      alert("Không thể thêm nhân viên");
    }
  };

  return (
    <div className="employ-table">
      <h2>Thêm nhân viên mới</h2>
      <form onSubmit={handleSubmit}>
        <label>Username: <input name="username" value={employee.username} onChange={handleChange} /></label><br/>
        <label>Fullname: <input name="fullName" value={employee.fullName} onChange={handleChange} /></label><br/>
        <label>Staffcode: <input name="staffCode" value={employee.staffCode} onChange={handleChange} /></label><br/>
        <label>Email: <input name="email" value={employee.email} onChange={handleChange} /></label><br/>
        <label>Password: <input name="password" value={employee.password} onChange={handleChange} type="password" /></label><br/>
        <label>Department: <input name="department" value={employee.department} onChange={handleChange} /></label><br/>

        <label>Level:
          <select name="level" value={employee.level} onChange={handleChange}>
            {levels.map(level => (
              <option key={level} value={level}>{level}</option>
            ))}
          </select>
        </label>

        <label>Role:
          <select name="role" value={employee.role} onChange={handleChange}>
            {roles.map(role => (
              <option key={role} value={role}>{role}</option>
            ))}
          </select>
        </label>

        <br/>
        <button type="submit" className="btn btn-success">Add Employee</button>
      </form>
    </div>
  );
}

export default EmployeeAdd;
