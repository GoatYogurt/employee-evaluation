import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../index.css';

function ProjectAdd() {
    const [formData, setFormData] = useState({
        code: '',
        isOdc: '',
        managerId: ''
    });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const res = await fetch('http://localhost:8080/api/projects', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify(formData)
            });

            if (!res.ok) throw new Error('❌ Thêm dự án thất bại!');
            alert('✅ Thêm dự án thành công!');
            navigate('/project-list');
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <div style={{ maxWidth: 500, margin: '0 auto', padding: 20 }}>
            <h2>Thêm dự án mới</h2>
            {error && <div style={{ color: 'red' }}>{error}</div>}

            <form onSubmit={handleSubmit}>
                <label>Mã dự án:</label>
                <input
                    required
                    value={formData.code}
                    onChange={e => setFormData({ ...formData, code: e.target.value })}
                />

                <label>Loại dự án (ODC/Onsite):</label>
                <input
                    required
                    value={formData.isOdc}
                    onChange={e => setFormData({ ...formData, isOdc: e.target.value })}
                />

                <label>Quản lý :</label>
                <input
                    required
                    value={formData.manangerName}
                    onChange={e => setFormData({ ...formData, manangerName: e.target.value })}
                />

                <button type="submit" className="btn btn-primary">Thêm</button>
                <button type="button" className="btn btn-secondary" onClick={() => navigate('/project-list')}>
                    Hủy
                </button>
            </form>
        </div>
    );
};