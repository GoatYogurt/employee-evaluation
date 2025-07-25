import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../index.css';

function CriterionView() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [criterion, setCriterion] = useState(null);
  const [isEditing, setIsEditing] = useState(false);

  const username = 'admin';
  const password = '123456';
  const base64 = btoa(`${username}:${password}`);

  useEffect(() => {
    fetch(`http://localhost:8080/api/criteria/${id}`, {
      method: 'GET',
      headers: {
        'Authorization': `Basic ${base64}`,
        'Content-Type': 'application/json',
      },
    })
      .then((res) => {
        if (!res.ok) throw new Error('Failed to fetch criterion');
        return res.json();
      })
      .then((data) => setCriterion(data))
      .catch((err) => alert('Error: ' + err.message));
  }, [id]);

  if (!criterion) return <div>Loading...</div>;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCriterion((prev) => ({ ...prev, [name]: value }));
  };

  const handleUpdate = (e) => {
    e.preventDefault();

    fetch(`http://localhost:8080/api/criteria/${id}`, {
      method: 'PUT',
      headers: {
        'Authorization': `Basic ${base64}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(criterion),
    })
      .then((res) => {
        if (!res.ok) throw new Error('Update failed');
        alert('Criterion updated successfully');
        setIsEditing(false);
      })
      .catch((err) => alert('Error: ' + err.message));
  };

  return (
    <div>
      <h2>Criterion Details</h2>

      {isEditing ? (
        <form onSubmit={handleUpdate}>
          <label>
            Name:
            <input
              name="name"
              value={criterion.name}
              onChange={handleChange}
              required
            />
          </label>
          <br />

          <label>
            Description:
            <textarea
              name="description"
              value={criterion.description}
              onChange={handleChange}
            />
          </label>
          <br />

          <button type="submit">Save</button>
          <button type="button" onClick={() => setIsEditing(false)}>Cancel</button>
        </form>
      ) : (
        <div>
          <p><strong>Name:</strong> {criterion.name}</p>
          <p><strong>Description:</strong> {criterion.description}</p>
          <button onClick={() => setIsEditing(true)}>Edit</button>
          <button onClick={() => navigate('/criterion-list')}>Back</button>
        </div>
      )}
    </div>
  );
}

export default CriterionView;