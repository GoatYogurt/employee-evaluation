import React, { useEffect, useState } from 'react';

const EvaluationCycleList = () => {
    const [cycles, setCycles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [employeesMap, setEmployeesMap] = useState({});


    const fetchCycles = () => {
        setLoading(true);
        fetch('http://localhost:8080/api/evaluation-cycles', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem('accessToken'),
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch evaluation cycles');
                }
                return response.json();
            })
            .then(data => {
                setCycles(data.content);
                setLoading(false);
            })
            .catch(err => {
                setError(err.message);
                setLoading(false);
            });
    };

    const fetchEmployees = () => {
        fetch('http://localhost:8080/api/employees', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem('accessToken'),
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch employees');
                }
                return response.json();
            })
            .then(data => {
                const map = {};
                data.forEach(emp => {
                    map[emp.id] = emp.name;
                });
                setEmployeesMap(map);
            })
            .catch(err => {
                setError(err.message);
            });
    };

    useEffect(() => {
        fetchCycles();
        fetchEmployees();
    }, []);

    if (loading) return <p>Loading evaluation cycles...</p>;
    if (error) return <p style={{ color: 'red' }}>{error}</p>;

    return (
        <div>
            <h2>Evaluation Cycles</h2>
            <button onClick={fetchCycles}>Refresh</button>
            <table border="1" cellPadding="8" cellSpacing="0">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Start Date</th>
                        <th>End Date</th>
                        <th>Status</th>
                        <th>Managers</th>
                        <th>Employees</th>
                    </tr>
                </thead>
                <tbody>
                    {cycles.map(cycle => (
                        <tr key={cycle.id}>
                            <td>{cycle.id}</td>
                            <td>{cycle.name}</td>
                            <td>{cycle.description}</td>
                            <td>{cycle.startDate}</td>
                            <td>{cycle.endDate}</td>
                            <td>{cycle.status}</td>
                            <td>
                                {cycle.managers && cycle.managers.length > 0
                                    ? cycle.managers.map(id => employeesMap[id] || id).join(', ')
                                    : 'No managers assigned'}
                            </td>
                            <td>
                                {cycle.employees && cycle.employees.length > 0
                                    ? cycle.employees.map(id => employeesMap[id] || id).join(', ')
                                    : 'No employees assigned'}
                            </td>

                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default EvaluationCycleList;
