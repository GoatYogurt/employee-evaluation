import React, { useEffect, useState } from 'react';
import '../index.css';
import { useNavigate, Link } from 'react-router-dom';

const CriterionGroupTable = () => {
  const [criterionGroups, setCriterionGroups] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);

  // popup state
  const [showEditModal, setShowEditModal] = useState(false);
  const [showViewModal, setShowViewModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedCriterionGroup, setSelectedCriterionGroup] = useState(null);
  const [deleteMessage, setDeleteMessage] = useState('');

  const navigate = useNavigate();

  // Lấy danh sách nhóm tiêu chí
  useEffect(() => {
    fetchCriterionGroups();
  }, []);

  const fetchCriterionGroups = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/criterion-groups", {
        method: "GET",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "application/json",
        },
      });

      if (!res.ok) {
        const errorText = await res.text();
        console.error("API ERROR:", res.status, errorText);
        return;
      }

      const data = await res.json();
      const content = data.data?.content || [];  
      const normalized = content.map((group) => ({
      id: group.id,
      name: group.name,
      description: group.description,
      createdDate: group.createdAt, 
      }));
      setCriterionGroups(normalized);

    } catch (error) {
      console.error("Failed to fetch criterion groups:", error);
    }
  };

  // Lọc dữ liệu theo tên
  const filteredCriterionGroups = criterionGroups.filter((group) =>
    group.name?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Phân trang
  const totalPages = Math.ceil(filteredCriterionGroups.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentCriterionGroups = filteredCriterionGroups.slice(startIndex, endIndex);

  // Hàm xử lý
  const handleEdit = (group) => {
    setSelectedCriterionGroup(group);
    setShowEditModal(true);
  };

  const handleEditConfirm = async () => {
    try {
      const res = await fetch(
        `http://localhost:8080/api/criterion-groups/${selectedCriterionGroup.id}`,
        {
          method: "PATCH",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            name: selectedCriterionGroup.name,
            description: selectedCriterionGroup.description,
          }),
        }
      );

      if (!res.ok) {
        const errMsg = await res.text();
        console.error("Update failed:", errMsg);
        alert("Sửa nhóm tiêu chí thất bại!");
        return;
      }

      const updatedGroup = await res.json();
      setCriterionGroups((prev) =>
        prev.map((group) =>
          group.id === updatedGroup.id
            ? {
                id: updatedGroup.id,
                name: updatedGroup.name,
                description: updatedGroup.description,
                createdDate: updatedGroup.createdDate,
              }
            : group
        )
      );

      alert("Sửa nhóm tiêu chí thành công!");
      setShowEditModal(false);
    } catch (err) {
      console.error(err);
      alert("Có lỗi khi sửa nhóm tiêu chí!");
    }
  };

  const handleDelete = async (id) => {
    try {
      const res = await fetch(`http://localhost:8080/api/criterion-groups/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "application/json",
        },
      });

      if (!res.ok) throw new Error("Delete failed");

      setCriterionGroups((prev) => prev.filter((group) => group.id !== id));
      setDeleteMessage("Xóa nhóm tiêu chí thành công!");
    } catch (err) {
      console.error(err);
      setDeleteMessage("Xóa nhóm tiêu chí thất bại!");
    }
    setShowDeleteModal(true);
  };

  const handleView = (group) => {
    setSelectedCriterionGroup(group);
    setShowViewModal(true);
  };

  // Chuyển sang trang danh sách tiêu chí của nhóm
  const handleViewCriteria = (groupId) => {
    if (!groupId) return;
    navigate(`/criterion-list?groupId=${groupId}`);
  };

  return (
    <div>
      {/* Header */}
      <div className="content-header">
        <h1 className="header-title">Quản lý nhóm tiêu chí</h1>
        <div className="header-actions">
          <Link to="/criterion-group-add">
            <button className="btn btn-primary">
              <i className="fas fa-plus"></i> Thêm nhóm tiêu chí
            </button>
          </Link>
        </div>
      </div>

      {/* Table */}
      <div className="excel-container">
        <div className="table-header">
          <h3 className="table-title">Danh sách nhóm tiêu chí</h3>
          <div className="table-controls">
            <input
              type="text"
              placeholder="Tìm kiếm nhóm tiêu chí..."
              className="search-box"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            <button className="btn btn-warning" onClick={fetchCriterionGroups}>
              <i className="fas fa-sync-alt"></i> Làm mới
            </button>
          </div>
        </div>

        <table className="excel-table">
          <thead>
            <tr>
              <th>STT</th>
              <th>Tên nhóm tiêu chí</th>
              <th>Mô tả</th>
              <th>Ngày tạo</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {currentCriterionGroups.length > 0 ? (
              currentCriterionGroups.map((group, index) => (
                <tr key={group.id}>
                  <td>{startIndex + index + 1}</td>
                  <td>{group.name}</td>
                  <td>{group.description}</td>
                  <td>
                    {group.createdDate
                      ? new Date(group.createdDate).toLocaleDateString('vi-VN')
                      : 'N/A'}
                  </td>
                  <td>
                    <div className="action-buttons">
                      <button
                        type="button"
                        className="btn btn-sm btn-edit"
                        title="Sửa"
                        onClick={() => handleEdit(group)}
                      >
                        <i className="fas fa-edit"></i>
                      </button>
                      <button
                        type="button"
                        className="btn btn-sm btn-delete"
                        title="Xóa"
                        onClick={() => handleDelete(group.id)}
                      >
                        <i className="fas fa-trash"></i>
                      </button>
                      <button
                        type="button"
                        className="btn btn-sm btn-view"
                        title="Xem chi tiết"
                        onClick={() => handleView(group)}
                      >
                        <i className="fas fa-eye"></i>
                      </button>
                      {/* <button
                        type="button"
                        className="btn btn-sm btn-primary"
                        title="Xem tiêu chí trong nhóm"
                        onClick={() => handleViewCriteria(group.id)}
                      >
                        <i className="fas fa-list"></i>
                      </button> */}
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="5" style={{ textAlign: "center", padding: "20px" }}>
                  Không tìm thấy nhóm tiêu chí nào.
                </td>
              </tr>
            )}
          </tbody>
        </table>

        {/* Pagination */}
        <div className="pagination-container">
          <div className="pagination-info">
            Hiển thị {startIndex + 1}-
            {Math.min(endIndex, filteredCriterionGroups.length)} trong tổng số{" "}
            {filteredCriterionGroups.length} nhóm tiêu chí
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
                  className={`pagination-btn ${
                    currentPage === pageNum ? "active" : ""
                  }`}
                  onClick={() => setCurrentPage(pageNum)}
                >
                  {pageNum}
                </button>
              );
            })}
            <button
              className="pagination-btn"
              onClick={() =>
                setCurrentPage((prev) => Math.min(prev + 1, totalPages))
              }
              disabled={currentPage === totalPages}
            >
              Sau ›
            </button>
          </div>
        </div>
      </div>

      {/* =================== MODALS =================== */}

      {/* Edit Modal */}
      {showEditModal && selectedCriterionGroup && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Sửa nhóm tiêu chí</h3>
            <div className="form-group">
              <label>Tên nhóm tiêu chí</label>
              <input
                type="text"
                value={selectedCriterionGroup.name || ""}
                onChange={(e) =>
                  setSelectedCriterionGroup({
                    ...selectedCriterionGroup,
                    name: e.target.value,
                  })
                }
              />
            </div>
            <div className="form-group">
              <label>Mô tả</label>
              <input
                type="text"
                value={selectedCriterionGroup.description || ""}
                onChange={(e) =>
                  setSelectedCriterionGroup({
                    ...selectedCriterionGroup,
                    description: e.target.value,
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
              onClick={() => setShowDeleteModal(false)}
            >
              Đóng
            </button>
          </div>
        </div>
      )}

      {/* View Modal */}
      {showViewModal && selectedCriterionGroup && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Thông tin nhóm tiêu chí</h3>
            <table className="excel-table">
              <tbody style={{ border: "none", color: "#000" }}>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Tên nhóm tiêu chí</td>
                  <td>{selectedCriterionGroup.name}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Mô tả</td>
                  <td>{selectedCriterionGroup.description}</td>
                </tr>
                <tr>
                  <td style={{ fontWeight: "bold" }}>Ngày tạo</td>
                  <td>
                    {selectedCriterionGroup.createdDate
                      ? new Date(selectedCriterionGroup.createdDate).toLocaleDateString('vi-VN')
                      : 'N/A'}
                  </td>
                </tr>
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

export default CriterionGroupTable;
