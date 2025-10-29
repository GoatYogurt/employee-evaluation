import React, { useEffect, useState } from 'react';
import '../index.css';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useContext } from "react";
import { ToastContext } from "../contexts/ToastProvider";

const CriterionTable = () => {
  const [criteria, setCriteria] = useState([]);
  const [criterionGroups, setCriterionGroups] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedGroupId, setSelectedGroupId] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);
  

  const { toast } = useContext(ToastContext);

  const location = useLocation();
  const navigate = useNavigate();

  const [showEditModal, setShowEditModal] = useState(false);
  const [showViewModal, setShowViewModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedCriterion, setSelectedCriterion] = useState(null);
  const [deleteMessage, setDeleteMessage] = useState('');

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const groupId = params.get('groupId') || '';
    setSelectedGroupId(groupId);
  }, [location.search]);

  useEffect(() => {
    const loadData = async () => {
      await fetchCriterionGroups();
      await fetchCriteria(); 
    };
    loadData();
  }, []);

  useEffect(() => {
    fetchCriteria();
  }, [selectedGroupId, criterionGroups]);

  const fetchCriterionGroups = async () => {
    try {
      const res = await fetch('http://localhost:8080/api/criterion-groups', {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken') || localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
        },
      });

      if (res.ok) {
        const data = await res.json();
        setCriterionGroups(data.content || data.data?.content || []); 
      }
    } catch (error) {
      console.error('Failed to fetch criterion groups:', error);
    }
  };

    const fetchCriteria = async () => {
      try {
        const res = await fetch('http://localhost:8080/api/criteria', {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${localStorage.getItem('accessToken') || localStorage.getItem('token')}`,
            'Content-Type': 'application/json',
          },
        });

        if (!res.ok) {
          console.error('API Error:', res.status);
          return;
        }

        const data = await res.json();
        const list = data.content || data.data?.content || [];

        const normalized = list.map((c) => {
          const groupId = c.groupId || null;
          const group = criterionGroups.find((g) => g.id === groupId);

          return {
            id: c.id,
            name: c.name,
            description: c.description,
            weight: c.weight !== undefined && c.weight !== null ? Number(c.weight) : 0,
            criterionGroupId: groupId,
            criterionGroupName: group ? group.name : 'N/A',
            createdAt: c.createdAt,
            updatedAt: c.updatedAt,
            createdBy: c.createdBy,
            updatedBy: c.updatedBy,
          };
        });

        setCriteria(normalized);
      } catch (error) {
        console.error('Failed to fetch criteria:', error);
      }
    };


  const filteredCriteria = criteria.filter((criterion) => {
    const matchesSearch = criterion.name?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesGroup = !selectedGroupId || criterion.criterionGroupId == selectedGroupId;
    return matchesSearch && matchesGroup;
  });

  //------------------------------- Datetime --------------------------------
  const formatDateTime = (dateString) => {
    if (!dateString) return "-";
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");
    return `${hours}:${minutes} - ${day}/${month}/${year}`;
  };


  const totalPages = Math.ceil(filteredCriteria.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentCriteria = filteredCriteria.slice(startIndex, endIndex);


  //------------------------------- Handlers --------------------------------
  const handleGroupChange = (e) => {
    const newGroupId = e.target.value;
    setSelectedGroupId(newGroupId);
    navigate(`/criterion-list${newGroupId ? `?groupId=${newGroupId}` : ''}`);
  };


  const handleEdit = (criterion) => {
    console.log("✏️ Edit criterion:", criterion); 
    setSelectedCriterion(criterion);
    setShowEditModal(true);
  };

  const handleEditConfirm = async () => {
    if (!selectedCriterion || !selectedCriterion.id) {      
      toast.error('Không tìm thấy ID tiêu chí!');
      return;
    }

    try {
      const res = await fetch(`http://localhost:8080/api/criteria/${selectedCriterion.id}`, {
        method: 'PATCH',
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken') || localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          name: selectedCriterion.name,
          description: selectedCriterion.description,
          weight: selectedCriterion.weight,
          groupId: selectedCriterion.criterionGroupId, 
        }),
      });

      if (!res.ok) {
        const errMsg = await res.text();
        console.error(' Update failed:', errMsg);
        toast.error('Sửa tiêu chí thất bại!');
        return;
      }

      const updatedCriterion = await res.json();
      const groupName =
        criterionGroups.find((g) => g.id === (updatedCriterion.groupId || updatedCriterion.criterionGroupId))
          ?.name || 'N/A';

      setCriteria((prev) =>
        prev.map((criterion) =>
          criterion.id === (updatedCriterion.id || updatedCriterion.criterionId)
            ? {
                id: updatedCriterion.id || updatedCriterion.criterionId,
                name: updatedCriterion.name,
                description: updatedCriterion.description,
                criterionGroupId: updatedCriterion.groupId || updatedCriterion.criterionGroupId,
                criterionGroupName: groupName,
                weight: updatedCriterion.weight,
              }
            : criterion
        )
      );

      toast.success('Sửa tiêu chí thành công!');
      setShowEditModal(false);
      fetchCriteria();
    } catch (err) {
      console.error(err);
      toast.error('Có lỗi khi sửa tiêu chí!');
    }
  };

  // Xoá
  const handleDelete = async (id) => {
    if (!id) { // ✅ FIX: kiểm tra id
      toast.error('ID tiêu chí không hợp lệ!');
      return;
    }

    try {
      const res = await fetch(`http://localhost:8080/api/criteria/${id}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken') || localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
        },
      });

      if (!res.ok) throw new Error('Delete failed');

      setCriteria((prev) => prev.filter((criterion) => criterion.id !== id));
      toast.success('Xóa tiêu chí thành công!');
      fetchCriteria();
    } catch (err) {
      console.error(err);
      toast.error('Xóa tiêu chí thất bại!');
    }
  };

  const handleView = (criterion) => {
    setSelectedCriterion(criterion);
    setShowViewModal(true);
  };

  // ==================================== JSX ====================================

  return (
    <div>
      {/* Header */}
      <div className="content-header">
        <h1 className="header-title">Quản lý tiêu chí</h1>
        <div className="header-actions">
          <Link to="/criterion-add">
            <button className="btn btn-primary">
              <i className="fas fa-plus"></i> Thêm tiêu chí
            </button>
          </Link>
        </div>
      </div>

      {/* Table */}
      <div className="excel-container">
        <div className="table-header">
          <h3 className="table-title">Danh sách tiêu chí</h3>
          <div className="table-controls">
            <select
              className="search-box"
              value={selectedGroupId}
              onChange={handleGroupChange}
              style={{ marginRight: '10px' }}
            >
              <option value="">Tất cả nhóm tiêu chí</option>
              {criterionGroups.map((group) => (
                <option key={group.id} value={group.id}>
                  {group.name}
                </option>
              ))}
            </select>

            <input
              type="text"
              placeholder="Tìm kiếm theo tên tiêu chí..."
              className="search-box"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />

            <button className="btn btn-warning" onClick={fetchCriteria}>
              <i className="fas fa-sync-alt"></i> Làm mới
            </button>
            
          </div>
        </div>

        <table className="excel-table" style={{width: "100%", tableLayout: "fixed" }}>
          <thead>
            <tr>
              <th style={{ width: "2%" }}>STT</th>
              <th style={{ width: "20%" }}>Tên tiêu chí</th>
              <th style={{ width: "20%" }}>Mô tả</th>
              <th style={{ width: "20%" }}>Trọng số</th>
              <th style={{ width: "20%" }}>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {currentCriteria.length > 0 ? (
              currentCriteria.map((criterion, index) => (
                <tr key={criterion.id}>
                  <td>{startIndex + index + 1}</td>
                  <td>{criterion.name}</td>
                  <td>{criterion.description}</td>
                  <td>{criterion.weight}</td>
                  <td>
                    <div className="action-buttons">
                      <button
                        className="btn btn-sm btn-edit"
                        title="Sửa"
                        onClick={() => handleEdit(criterion)}
                      >
                        <i className="fas fa-edit"></i>
                      </button>
                      <button
                        className="btn btn-sm btn-delete"
                        title="Xóa"
                        onClick={() => handleDelete(criterion.id)}
                      >
                        <i className="fas fa-trash"></i>
                      </button>
                      <button
                        className="btn btn-sm btn-view"
                        title="Xem chi tiết"
                        onClick={() => handleView(criterion)}
                      >
                        <i className="fas fa-eye"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="5" style={{ textAlign: 'center', padding: '20px' }}>
                  Không tìm thấy tiêu chí nào.
                </td>
              </tr>
            )}
          </tbody>
        </table>

        {/* Pagination */}
        <div className="pagination-container">
          <div className="pagination-info">
            Hiển thị {startIndex + 1}-
            {Math.min(endIndex, filteredCriteria.length)} trong tổng số{' '}
            {filteredCriteria.length} tiêu chí
          </div>
          <div className="pagination-controls">
            <button
              className="pagination-btn"
              onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
              disabled={currentPage === 1}
            >
              ‹ Trước
            </button>
            {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
              const pageNum = i + 1;
              return (
                <button
                  key={pageNum}
                  className={`pagination-btn ${currentPage === pageNum ? 'active' : ''}`}
                  onClick={() => setCurrentPage(pageNum)}
                >
                  {pageNum}
                </button>
              );
            })}
            <button
              className="pagination-btn"
              onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
              disabled={currentPage === totalPages}
            >
              Sau ›
            </button>
          </div>
        </div>
      </div>

      {/* =================== MODALS =================== */}
      {/* Edit Modal */}
      {showEditModal && selectedCriterion && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Sửa tiêu chí</h3>
            <div className="form-group">
              <label>Tên tiêu chí</label>
              <input
                type="text"
                value={selectedCriterion.name || ''}
                onChange={(e) =>
                  setSelectedCriterion({
                    ...selectedCriterion,
                    name: e.target.value,
                  })
                }
              />
            </div>
            <div className="form-group">
              <label>Mô tả</label>
              <input
                type="text"
                value={selectedCriterion.description || ''}
                onChange={(e) =>
                  setSelectedCriterion({
                    ...selectedCriterion,
                    description: e.target.value,
                  })
                }
              />
            </div>
            <div className="form-group">
              <label>Nhóm tiêu chí</label>
              <select
                value={selectedCriterion.criterionGroupId || ''}
                onChange={(e) =>
                  setSelectedCriterion({
                    ...selectedCriterion,
                    criterionGroupId: parseInt(e.target.value),
                  })
                }
              >
                <option value="">-- Chọn nhóm tiêu chí --</option>
                {criterionGroups.map((group) => (
                  <option key={group.id} value={group.id}>
                    {group.name}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Trọng số</label>
                          <input
                type="number"
                min="-1"
                max="1"
                value={selectedCriterion.weight ?? ''} 
                onChange={(e) =>
                  setSelectedCriterion({
                    ...selectedCriterion,
                    weight: e.target.value === '' ? null : parseFloat(e.target.value),
                  })
                }
              />
            </div>

            <div className="modal-actions">
              <button className="btn btn-primary" onClick={handleEditConfirm}>
                Xác nhận
              </button>
              <button
                className="btn btn-secondary"
                onClick={() => setShowEditModal(false)}
              >
                Hủy
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Modal */}
      {showDeleteModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>{deleteMessage}</h3>
            <button
              className="btn btn-secondary"
              onClick={() => toast.success('Success')}
            >
              Đóng
            </button>
          </div>
        </div>
      )}

      {/* View Modal */}
      {showViewModal && selectedCriterion && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Thông tin tiêu chí</h3>
            <table className="excel-table">
              <tbody>
                <tr>
                  <td style={{ fontWeight: 'bold' }}>Tên tiêu chí</td>
                  <td>{selectedCriterion.name}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: 'bold' }}>Mô tả</td>
                  <td>{selectedCriterion.description}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: 'bold' }}>Nhóm tiêu chí</td>
                  <td>{selectedCriterion.criterionGroupName}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: 'bold' }}>Trọng số</td>
                  <td>{selectedCriterion.weight}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Ngày tạo</td>
                  <td>{formatDateTime(selectedCriterion.createdAt)}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Ngày cập nhật</td>
                  <td>{formatDateTime(selectedCriterion.updatedAt)}</td>
                </tr>
                <tr><td style={{ fontWeight: "bold" }}>Người tạo</td><td>{selectedCriterion.createdBy}</td></tr>
                <tr><td style={{ fontWeight: "bold" }}>Người cập nhật</td><td>{selectedCriterion.updatedBy}</td></tr>
              </tbody>
            </table>
            <button
              className="btn btn-secondary"
              onClick={() => setShowViewModal(false)}
            >
              Đóng
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default CriterionTable;
