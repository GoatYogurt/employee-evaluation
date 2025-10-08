// src/pages/CriterionGroupAdd.jsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../index.css';

function CriterionGroupAdd() {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    weight: 0
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch('http://localhost:8080/api/criterion-groups', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(formData)
      });

      if (!res.ok) throw new Error('❌ Thêm nhóm tiêu chí thất bại!');
      alert('✅ Thêm nhóm tiêu chí thành công!');
      navigate('/criterion-group-list');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div style={{ maxWidth: 500, margin: '0 auto', padding: 20 }}>
      <h2>Thêm nhóm tiêu chí mới</h2>
      {error && <div style={{ color: 'red' }}>{error}</div>}

      <form onSubmit={handleSubmit}>
        <label>Tên nhóm:</label>
        <input
          required
          value={formData.name}
          onChange={e => setFormData({ ...formData, name: e.target.value })}
        />

        <label>Mô tả:</label>
        <textarea
          required
          value={formData.description}
          onChange={e => setFormData({ ...formData, description: e.target.value })}
        />

        <button type="submit" className="btn btn-primary">Thêm</button>
        <button type="button" className="btn btn-secondary" onClick={() => navigate('/criterion-group-list')}>
          Hủy
        </button>
      </form>
    </div>
  );
}

export default CriterionGroupAdd;
