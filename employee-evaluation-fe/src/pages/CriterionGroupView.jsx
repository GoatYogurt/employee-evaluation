import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../index.css';

function CriterionGroupView() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [criterionGroup, setCriterionGroup] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchCriterionGroup();
  }, [id]);

  const fetchCriterionGroup = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/criterion-groups/${id}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        setCriterionGroup(data);
      } else {
        alert('Không tìm thấy nhóm tiêu chí!');
        navigate('/criterion-group-list');
      }
    } catch (error) {
      console.error('Error fetching criterion group:', error);
      alert('Có lỗi xảy ra!');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div style={{ textAlign: 'center', padding: '20px' }}>Đang tải...</div>;
  }

  if (!criterionGroup) {
    return <div style={{ textAlign: 'center', padding: '20px' }}>Không tìm thấy nhóm tiêu chí!</div>;
  }

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto', padding: '20px' }}>
      <h2>Chi tiết nhóm tiêu chí</h2>
      
      <div style={{ 
        backgroundColor: '#f8f9fa', 
        padding: '20px', 
        borderRadius: '8px', 
        marginBottom: '20px' 
      }}>
        <div style={{ marginBottom: '10px' }}>
          <strong>ID:</strong> {criterionGroup.id}
        </div>
        <div style={{ marginBottom: '10px' }}>
          <strong>Tên nhóm tiêu chí:</strong> {criterionGroup.name}
        </div>
        <div style={{ marginBottom: '10px' }}>
          <strong>Mô tả:</strong> {criterionGroup.description}
        </div>
        <div style={{ marginBottom: '10px' }}>
          <strong>Trọng số:</strong> {criterionGroup.weight}%
        </div>
        <div style={{ marginBottom: '10px' }}>
          <strong>Ngày tạo:</strong> {criterionGroup.createdDate ? new Date(criterionGroup.createdDate).toLocaleDateString('vi-VN') : 'N/A'}
        </div>
        <div>
          <strong>Ngày cập nhật:</strong> {criterionGroup.updatedDate ? new Date(criterionGroup.updatedDate).toLocaleDateString('vi-VN') : 'N/A'}
        </div>
      </div>

      <div style={{ display: 'flex', gap: '10px' }}>
        <button 
          onClick={() => navigate(`/criterion-list?groupId=${id}`)}
          style={{ 
            backgroundColor: '#007bff', 
            color: 'white', 
            padding: '10px 20px', 
            border: 'none', 
            borderRadius: '4px', 
            cursor: 'pointer' 
          }}
        >
          Xem tiêu chí trong nhóm
        </button>
        <button 
          onClick={() => navigate('/criterion-group-list')}
          style={{ 
            backgroundColor: '#6c757d', 
            color: 'white', 
            padding: '10px 20px', 
            border: 'none', 
            borderRadius: '4px', 
            cursor: 'pointer' 
          }}
        >
          Quay lại
        </button>
      </div>
    </div>
  );
}

export default CriterionGroupView;
