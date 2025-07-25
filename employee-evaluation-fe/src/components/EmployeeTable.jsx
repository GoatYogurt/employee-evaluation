import React, { useEffect, useState } from 'react';
import '../index.css';
import { Link } from 'react-router-dom';

const EmployeeTable = () => {
	const [employees, setEmployees] = useState([]);
	const username = 'admin';
	const password = '123456';
	const base64 = btoa(`${username}:${password}`);

	useEffect(() => {
		fetch('http://localhost:8080/api/employees', {
			method: 'GET',
			headers: {
				'Authorization': `Basic ${base64}`,
				'Content-Type': 'application/json',
			},
		})
			.then((res) => res.json())
			.then((data) => setEmployees(data))
			.catch((err) => console.error('Failed to fetch employees:', err));
	}, []);

	const handleDelete = (id) => {
		const confirm = window.confirm('Are you sure you want to delete this employee?');
		if (!confirm) return;

		fetch(`http://localhost:8080/api/employees/${id}`, {
			method: 'DELETE',
			headers: {
				'Authorization': `Basic ${base64}`,
				'Content-Type': 'application/json',
			},
		})
			.then((res) => {
				if (!res.ok) throw new Error('Delete failed');
				setEmployees((prev) => prev.filter((emp) => emp.id !== id));
			})
			.catch((err) => {
				console.error(err);
				alert('Failed to delete employee');
			});
	};

	return (
		<div className="container">
			<h2>Employee List</h2>
			<table className="employee-table">
				<thead>
					<tr>
						<th>ID</th>
						<th>Name</th>
						<th>Department</th>
						<th>Position</th>
						<th>Salary</th>
						<th>Role</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					{employees.length > 0 ? (
						employees.map((emp) => (
							<tr key={emp.id}>
								<td>{emp.id}</td>
								<td>{emp.name}</td>
								<td>{emp.department}</td>
								<td>{emp.position}</td>
								<td>{emp.salary}</td>
								<td>{emp.role}</td>
								<td>
									<button onClick={() => handleDelete(emp.id)}>Delete</button> 
									<Link to={`/employee-view/${emp.id}`}>
										<button>View</button>
									</Link>
								</td>
							</tr>
						))
					) : (
						<tr>
							<td colSpan="7">No employees found.</td>
						</tr>
					)}
				</tbody>
			</table>
		</div>
	);
};

export default EmployeeTable;
