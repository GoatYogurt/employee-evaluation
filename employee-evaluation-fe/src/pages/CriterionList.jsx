import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../index.css';

function CriterionList() {
    const [criteria, setCriteria] = useState([]);
    const navigate = useNavigate();

    // Fetch all criteria from backend
    useEffect(() => {
        fetch('http://localhost:8080/api/criteria', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Basic ' + btoa('admin:123456')
            },
        })
            .then((res) => res.json())
            .then((data) => setCriteria(data))
            .catch((err) => console.error('Failed to fetch criteria:', err));
    }, []);

    // Delete a criterion by ID
    const handleDelete = (id) => {
        if (!window.confirm('Are you sure you want to delete this criterion?')) return;

        fetch(`http://localhost:8080/api/criteria/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Basic ' + btoa('admin:123456')
            },
        })
            .then((res) => {
                if (!res.ok) throw new Error('Failed to delete');
                setCriteria((prev) => prev.filter((c) => c.id !== id));
            })
            .catch((err) => console.error(err));
    };

    return (
        <div>
            <h2>Criteria List</h2>
            <button onClick={() => navigate('/criterion-add')}>Add New Criterion</button>
            <table border="1" cellPadding="8" cellSpacing="0" style={{ marginTop: '1rem', width: '100%' }}>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Criterion Name</th>
                        <th>Description</th>
                        <th>Weight</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {criteria.length === 0 ? (
                        <tr><td colSpan="4">No criteria found.</td></tr>
                    ) : (
                        criteria.map((criterion) => (
                            <tr key={criterion.id}>
                                <td>{criterion.id}</td>
                                <td>{criterion.name}</td>
                                <td>{criterion.description}</td>
                                <td>{criterion.weight}</td>
                                <td>
                                    <button onClick={() => navigate(`/criterion-view/${criterion.id}`)}>View</button>
                                    <button onClick={() => handleDelete(criterion.id)}>Delete</button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>
        </div>
    );
}

export default CriterionList;
