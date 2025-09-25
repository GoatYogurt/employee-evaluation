import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function EmployeeAdd() {
  const navigate = useNavigate();
  const [employee, setEmployee] = useState({
    name: '',
    department: '',
    position: '',
    salary: '',
    role: 'EMPLOYEE', // default
  });

  const roles = ['ADMIN', 'MANAGER', 'EMPLOYEE'];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEmployee((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    fetch('http://localhost:8080/api/employees', {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('accessToken')
    },
      body: JSON.stringify(employee),
    })
      .then((res) => res.json())
      .then(() => navigate('/employee-list'))
      .catch((err) => console.error(err));
  };

  return (
    <div>
      <h2>Add New Employee</h2>
      <form onSubmit={handleSubmit}>
        <label>Name: <input name="name" value={employee.name} onChange={handleChange} /></label><br/>
        <label>Department: <input name="department" value={employee.department} onChange={handleChange} /></label><br/>
        <label>Position: <input name="position" value={employee.position} onChange={handleChange} /></label><br/>
        <label>Salary: <input name="salary" type="number" value={employee.salary} onChange={handleChange} /></label><br/>
        <label>Role:
          <select name="role" value={employee.role} onChange={handleChange}>
            {roles.map(role => (
              <option key={role} value={role}>{role}</option>
            ))}
          </select>
        </label><br/>
        <button type="submit">Add Employee</button>
      </form>
    </div>
  );
}

export default EmployeeAdd;
