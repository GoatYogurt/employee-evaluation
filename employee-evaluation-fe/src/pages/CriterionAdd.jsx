// src/pages/CriterionAdd.jsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../index.css';
import { useContext } from "react";
import { ToastContext } from "../contexts/ToastProvider";

function CriterionAdd() {
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [weight, setWeight] = useState('');
  const [criterionGroups, setCriterionGroups] = useState([]);
  const [groupId, setGroupId] = useState('');
  const navigate = useNavigate();

  const { toast } = useContext(ToastContext);

  useEffect(() => {
    fetch('http://localhost:8080/api/criterion-groups', {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    })
      .then(res => res.json())
      .then(data => {
        console.log("Criterion group API response:", data);
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
        if (!res.ok) throw new Error('Thêm tiêu chí thất bại');
        return res.json();
      })
      .then(() => {
        toast.success('Thêm tiêu chí thành công!');
        navigate('/criterion-list');
      })
      .catch(err => toast.error(err.message));
  };

  return (
       <div className="criterion-add-wrapper">
      <div className="criterion-add-container">
        <h2>Thêm tiêu chí mới</h2>

        <form onSubmit={handleSubmit} className="criterion-add-form">
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
            <label>Nhóm tiêu chí:</label>
            <select
              required
              value={groupId}
              onChange={e => setGroupId(e.target.value)}
            >
              <option value="">-- Chọn nhóm --</option>
              {criterionGroups.map(g => (
                <option key={g.id} value={g.id}>{g.name}</option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Trọng số:</label>
            <input
              type="number"
              step="0.01"
              min="-1"
              max="1"
              required
              value={weight}
              onChange={e => setWeight(e.target.value)}
            />
          </div>

          <div className="form-group" style={{ gridColumn: 'span 2' }}>
            <label>Mô tả:</label>
            <input
              type="text"
              required
              value={description}
              onChange={e => setDescription(e.target.value)}
            />
          </div>

          <div className="criterion-add-actions">
            <button type="submit" className="btn-primary">Thêm tiêu chí</button>
            <button
              type="button"
              className="btn-secondary"
              onClick={() => navigate('/criterion-list')}
            >
              Hủy
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default CriterionAdd;