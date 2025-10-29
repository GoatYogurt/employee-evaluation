import { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import './dashboard.css';
import { ToastContext } from "../contexts/ToastProvider";

function CriterionGroupAdd() {
  const [formData, setFormData] = useState({ name: '', description: '', weight: 0 });
  const [error, setError] = useState('');
  const { toast } = useContext(ToastContext);
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

      if (!res.ok) throw new Error('Thêm nhóm tiêu chí thất bại!');
      toast.success('Thêm nhóm tiêu chí thành công!');
      navigate('/criterion-group-list');
    } catch (err) {
      setError(err.message);
      toast.error(err.message);
    }
  };

  return (
    <div className="criterion-form-wrapper">
      <div className="criterion-form-container">
        <h2>Thêm nhóm tiêu chí mới</h2>
        {error && <div style={{ color: 'red', textAlign: 'center' }}>{error}</div>}

        <form className="criterion-form" onSubmit={handleSubmit}>
          <div>
            <label>Tên nhóm:</label>
            <input
              required
              value={formData.name}
              onChange={e => setFormData({ ...formData, name: e.target.value })}
            />
          </div>

          <div>
            <label>Mô tả:</label>
            <input
              required
              value={formData.description}
              onChange={e => setFormData({ ...formData, description: e.target.value })}
            />
          </div>

          <div className="criterion-form-actions">
            <button type="submit" className="btn-group">Thêm nhóm</button>
            <button type="button" className="btn-group" onClick={() => navigate('/criterion-group-list')}>
              Hủy
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default CriterionGroupAdd;
