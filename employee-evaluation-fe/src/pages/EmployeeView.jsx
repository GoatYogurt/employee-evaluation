import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import '../index.css';

function EmployeeView() {
    const { id } = useParams();
    const [employee, setEmployee] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [evaluations, setEvaluations] = useState([]);
    const [newEvaluation, setNewEvaluation] = useState({
        criterionId: '',
        score: '',
        evaluationDate: '',
        comment: '',
    });
    const [criteriaList, setCriteriaList] = useState([]);

    const username = 'admin';
    const password = '123456';
    const base64 = btoa(`${username}:${password}`);
    const roles = ['ADMIN', 'MANAGER', 'EMPLOYEE'];

    useEffect(() => {
        fetch(`http://localhost:8080/api/employees/${id}`, {
            headers: { 'Authorization': `Basic ${base64}` },
        })
            .then(res => res.json())
            .then(data => setEmployee(data))
            .catch(err => console.error('Failed to fetch employee:', err));

        fetch(`http://localhost:8080/api/evaluations/employee/${id}`, {
            headers: { 'Authorization': `Basic ${base64}` },
        })
            .then(res => res.json())
            .then(data => setEvaluations(data))
            .catch(err => console.error('Failed to fetch evaluations:', err));

        fetch(`http://localhost:8080/api/criteria`, {
            headers: { 'Authorization': `Basic ${base64}` },
        })
            .then(res => res.json())
            .then(data => setCriteriaList(data))
            .catch(err => console.error('Failed to fetch criteria:', err));
    }, [id]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setEmployee(prev => ({ ...prev, [name]: value }));
    };

    const handleUpdate = (e) => {
        e.preventDefault();
        fetch(`http://localhost:8080/api/employees/${id}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Basic ${base64}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(employee),
        })
            .then(res => {
                if (res.ok) {
                    alert('Employee updated successfully');
                    setIsEditing(false);
                } else {
                    alert('Failed to update employee');
                }
            })
            .catch(err => console.error('Error updating employee:', err));
    };

    const handleEvaluationChange = (e) => {
        const { name, value } = e.target;
        setNewEvaluation(prev => ({ ...prev, [name]: value }));
    };

    const handleAddEvaluation = (e) => {
        e.preventDefault();
        const payload = {
            employeeId: id,
            criterionId: newEvaluation.criterionId,
            score: newEvaluation.score,
            evaluationDate: newEvaluation.evaluationDate,
            comment: newEvaluation.comment
        };

        fetch(`http://localhost:8080/api/evaluations`, {
            method: 'POST',
            headers: {
                'Authorization': `Basic ${base64}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(payload),
        })
            .then(res => {
                if (!res.ok) throw new Error('Failed to add evaluation');
                return res.json();
            })
            .then(data => {
                setEvaluations(prev => [...prev, data]);
                setNewEvaluation({
                    criterionId: '',
                    score: '',
                    evaluationDate: '',
                    comment: '',
                });
                alert('Evaluation added');
            })
            .catch(err => alert(err.message));
    };

    const handleDeleteEvaluation = (evaluationId) => {
        if (!window.confirm('Are you sure you want to delete this evaluation?')) return;
        fetch(`http://localhost:8080/api/evaluations/${evaluationId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Basic ${base64}`,
                'Content-Type': 'application/json',
            },
        })
            .then(res => {
                if (res.ok) {
                    setEvaluations(prev => prev.filter(e => e.id !== evaluationId));
                    alert('Evaluation deleted');
                } else {
                    alert('Failed to delete evaluation');
                }
            })
            .catch(err => console.error('Error deleting evaluation:', err));
    };

    if (!employee) return <div>Loading...</div>;

    return (
        <div>
            <h2>Employee Details</h2>

            {isEditing ? (
                <form onSubmit={handleUpdate}>
                    <label>Name: <input name="name" value={employee.name} onChange={handleChange} /></label><br />
                    <label>Department: <input name="department" value={employee.department} onChange={handleChange} /></label><br />
                    <label>Position: <input name="position" value={employee.position} onChange={handleChange} /></label><br />
                    <label>Salary: <input name="salary" type="number" value={employee.salary} onChange={handleChange} /></label><br />
                    <label>Role:
                        <select name="role" value={employee.role} onChange={handleChange}>
                            {roles.map(role => (
                                <option key={role} value={role}>{role}</option>
                            ))}
                        </select>
                    </label><br />
                    <button type="submit">Save</button>
                    <button type="button" onClick={() => setIsEditing(false)}>Cancel</button>
                </form>
            ) : (
                <div>
                    <p><strong>Name:</strong> {employee.name}</p>
                    <p><strong>Department:</strong> {employee.department}</p>
                    <p><strong>Position:</strong> {employee.position}</p>
                    <p><strong>Salary:</strong> {employee.salary}</p>
                    <p><strong>Role:</strong> {employee.role}</p>
                    <button onClick={() => setIsEditing(true)}>Edit</button>
                </div>
            )}

            <hr />

            <h3>Evaluations</h3>
            {evaluations.length === 0 ? (
                <p>No evaluations found.</p>
            ) : (
                <table border="1" cellPadding="8" style={{ borderCollapse: 'collapse' }}>
                    <thead>
                        <tr>
                            <th>Criterion</th>
                            <th>Score</th>
                            <th>Weight</th>
                            <th>Date</th>
                            <th>Comment</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {evaluations.map(e => (
                            <tr key={e.id}>
                                <td>
                                    {criteriaList.find(c => c.id === e.criterionId)?.name || 'Unknown'}
                                </td>

                                <td>{e.score}</td>
                                <td>
                                    {criteriaList.find(c => c.id === e.criterionId)?.weight || 'N/A'}
                                </td>
                                <td>{e.evaluationDate}</td>
                                <td>{e.comment}</td>
                                <td>
                                    <button onClick={() => {
                                        // Handle edit evaluation logic here
                                        alert('Edit functionality not implemented yet');
                                    }}>Edit</button>
                                    <button onClick={() => handleDeleteEvaluation(e.id)}>Delete</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}

            <h4>Add Evaluation</h4>
            <form onSubmit={handleAddEvaluation}>
                <label>
                    Criterion:
                    <select name="criterionId" value={newEvaluation.criterionId} onChange={handleEvaluationChange} required>
                        <option value="">--Select--</option>
                        {criteriaList.map(c => (
                            <option key={c.id} value={c.id}>{c.name}</option>
                        ))}
                    </select>
                </label><br />
                <label>
                    Score: <input name="score" type="number" value={newEvaluation.score} onChange={handleEvaluationChange} required />
                </label><br />
                <label>
                    Date: <input name="evaluationDate" type="date" value={newEvaluation.evaluationDate} onChange={handleEvaluationChange} required />
                </label><br />
                <label>
                    Comment: <textarea name="comment" value={newEvaluation.comment} onChange={handleEvaluationChange} rows="3" />
                </label><br />
                <button type="submit">Add Evaluation</button>
            </form>
        </div>
    );
}

export default EmployeeView;