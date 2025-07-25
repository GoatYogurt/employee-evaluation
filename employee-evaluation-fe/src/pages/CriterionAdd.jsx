import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../index.css';

function CriterionAdd() {
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();

    const newCriterion = { name, description };

    fetch('http://localhost:8080/api/criteria', {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': 'Basic ' + btoa('admin:123456')
      },
      body: JSON.stringify(newCriterion),
    })
      .then((res) => {
        if (!res.ok) throw new Error('Failed to create criterion');
        return res.json();
      })
      .then(() => navigate('/criterion-list'))
      .catch((err) => alert('Error: ' + err.message));
  };

  return (
    <div>
      <h2>Add New Criterion</h2>
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', width: '300px' }}>
        <label>
          Name:
          <input
            type="text"
            required
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Enter criterion name"
          />
        </label>

        <label style={{ marginTop: '10px' }}>
          Description:
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Enter description"
          />
        </label>

        <button type="submit" style={{ marginTop: '16px' }}>Add Criterion</button>
        <button type="button" onClick={() => navigate('/criteria')} style={{ marginTop: '8px' }}>
          Cancel
        </button>
      </form>
    </div>
  );
}

export default CriterionAdd;
