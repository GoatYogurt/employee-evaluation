// src/pages/CriterionAdd.jsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../index.css';

function CriterionAdd() {
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [weight, setWeight] = useState('');
  const [criterionGroups, setCriterionGroups] = useState([]);
  const [groupId, setGroupId] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    // Lấy danh sách nhóm tiêu chí
    fetch('http://localhost:8080/api/criterion-groups', {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    })
      .then(res => res.json())
      .then(data => {
        console.log("Criterion group API response:", data);
        // ✅ Sửa lại cho đúng cấu trúc JSON của backend
        setCriterionGroups(data.data?.content || []);
      })
      .catch(err => console.error("Fetch error:", err));
  }, []);

  const handleSubmit = (e) => {
    e.preventDefault();

    const newCriterion = {
      name,
      description,
      weight: parseFloat(weight),
      groupId: parseInt(groupId)
    };

    fetch('http://localhost:8080/api/criteria', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify(newCriterion)
    })
      .then(res => {
        if (!res.ok) throw new Error('❌ Thêm tiêu chí thất bại');
        return res.json();
      })
      .then(() => {
        alert('✅ Thêm tiêu chí thành công!');
        navigate('/criterion-list');
      })
      .catch(err => alert(err.message));
  };

  return (
    <div style={{ maxWidth: 500, margin: '0 auto', padding: 20 }}>
      <h2>Thêm tiêu chí mới</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Tên tiêu chí:</label>
          <input
            type="text"
            required
            value={name}
            onChange={e => setName(e.target.value)}
          />
        </div>

        <div className="form-group">
          <label>Mô tả:</label>
          <textarea
            required
            value={description}
            onChange={e => setDescription(e.target.value)}
          />
        </div>

        <div className="form-group">
          <label>Nhóm tiêu chí:</label>
          <select
            required
            value={groupId}
            onChange={e => setGroupId(e.target.value)}
          >
            <option value="">-- Chọn nhóm --</option>
            {criterionGroups.map(g => (
              <option key={g.id} value={g.id}>
                {g.name}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>Trọng số:</label>
          <input
            type="number"
            min="-1"
            max="1"
            required
            value={weight}
            onChange={e => setWeight(e.target.value)}
          />
        </div>

        <button type="submit" className="btn btn-primary">Thêm</button>
        <button
          type="button"
          className="btn btn-secondary"
          onClick={() => navigate('/criterion-list')}
          style={{ marginLeft: 10 }}
        >
          Hủy
        </button>
      </form>
    </div>
  );
}

export default CriterionAdd;
