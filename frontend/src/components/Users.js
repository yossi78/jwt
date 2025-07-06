import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { userService } from '../services/userService';
import {
  Container,
  Table,
  Button,
  Form,
  Modal,
  Alert,
  Navbar,
  FormCheck,
  InputGroup
} from 'react-bootstrap';

const Users = () => {
  const [users, setUsers] = useState([]);
  const [filteredUsers, setFilteredUsers] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedUsers, setSelectedUsers] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    age: '',
    birthday: ''
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const { logout } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadUsers();
  }, []);

  useEffect(() => {
    filterUsers();
  }, [users, searchTerm]);

  const loadUsers = async () => {
    try {
      setLoading(true);
      const data = await userService.getAllUsers();
      setUsers(data);
      setError('');
    } catch (err) {
      setError('Failed to load users');
      console.error('Error loading users:', err);
    } finally {
      setLoading(false);
    }
  };

  const filterUsers = () => {
    if (!searchTerm.trim()) {
      setFilteredUsers(users);
    } else {
      const filtered = users.filter(user =>
        user.firstName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.lastName.toLowerCase().includes(searchTerm.toLowerCase())
      );
      setFilteredUsers(filtered);
    }
  };

  const handleSearch = (e) => {
    setSearchTerm(e.target.value);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleAddUser = () => {
    setEditingUser(null);
    setFormData({
      firstName: '',
      lastName: '',
      age: '',
      birthday: ''
    });
    setShowModal(true);
  };

  const handleEditUser = (user) => {
    setEditingUser(user);
    setFormData({
      firstName: user.firstName,
      lastName: user.lastName,
      age: user.age?.toString() || '',
      birthday: user.birthday ? user.birthday.split('T')[0] : ''
    });
    setShowModal(true);
  };

  const handleDeleteUser = async (id) => {
    if (window.confirm('Are you sure you want to delete this user?')) {
      try {
        await userService.deleteUser(id);
        setSuccess('User deleted successfully');
        loadUsers();
      } catch (err) {
        setError('Failed to delete user');
      }
    }
  };

  const handleBatchDelete = async () => {
    if (selectedUsers.length === 0) {
      setError('Please select users to delete');
      return;
    }

    if (window.confirm(`Are you sure you want to delete ${selectedUsers.length} users?`)) {
      try {
        await userService.deleteUsers(selectedUsers);
        setSuccess(`${selectedUsers.length} users deleted successfully`);
        setSelectedUsers([]);
        loadUsers();
      } catch (err) {
        setError('Failed to delete users');
      }
    }
  };

  const handleSelectUser = (userId) => {
    setSelectedUsers(prev =>
      prev.includes(userId)
        ? prev.filter(id => id !== userId)
        : [...prev, userId]
    );
  };

  const handleSelectAll = () => {
    if (selectedUsers.length === filteredUsers.length) {
      setSelectedUsers([]);
    } else {
      setSelectedUsers(filteredUsers.map(user => user.id));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const userData = {
        ...formData,
        age: formData.age ? parseInt(formData.age) : null,
        birthday: formData.birthday || null
      };

      if (editingUser) {
        await userService.updateUser(editingUser.id, userData);
        setSuccess('User updated successfully');
      } else {
        await userService.createUser(userData);
        setSuccess('User created successfully');
      }

      setShowModal(false);
      loadUsers();
    } catch (err) {
      setError(editingUser ? 'Failed to update user' : 'Failed to create user');
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner-border" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <Container className="users-container">
      <Navbar bg="light" expand="lg" className="mb-3">
        <Container>
          <Navbar.Brand>User Management</Navbar.Brand>
          <Button variant="outline-danger" onClick={handleLogout}>
            Sign Out
          </Button>
        </Container>
      </Navbar>

      {error && <Alert variant="danger" onClose={() => setError('')} dismissible>{error}</Alert>}
      {success && <Alert variant="success" onClose={() => setSuccess('')} dismissible>{success}</Alert>}

      <div className="search-container">
        <InputGroup>
          <Form.Control
            type="text"
            placeholder="Search users by name..."
            value={searchTerm}
            onChange={handleSearch}
          />
          <Button variant="outline-secondary" onClick={() => setSearchTerm('')}>
            Clear
          </Button>
        </InputGroup>
      </div>

      <div className="action-buttons">
        <Button variant="success" onClick={handleAddUser} className="me-2">
          Add User
        </Button>
        {selectedUsers.length > 0 && (
          <Button variant="danger" onClick={handleBatchDelete}>
            Delete Selected ({selectedUsers.length})
          </Button>
        )}
      </div>

      <div className="table-container">
        <Table striped bordered hover responsive>
          <thead>
            <tr>
              <th>
                <FormCheck
                  checked={selectedUsers.length === filteredUsers.length && filteredUsers.length > 0}
                  onChange={handleSelectAll}
                />
              </th>
              <th>ID</th>
              <th>First Name</th>
              <th>Last Name</th>
              <th>Age</th>
              <th>Birthday</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredUsers.map(user => (
              <tr key={user.id}>
                <td>
                  <FormCheck
                    checked={selectedUsers.includes(user.id)}
                    onChange={() => handleSelectUser(user.id)}
                  />
                </td>
                <td>{user.id}</td>
                <td>{user.firstName}</td>
                <td>{user.lastName}</td>
                <td>{user.age}</td>
                <td>{user.birthday ? new Date(user.birthday).toLocaleDateString() : ''}</td>
                <td>
                  <Button
                    variant="primary"
                    size="sm"
                    onClick={() => handleEditUser(user)}
                    className="me-2"
                  >
                    Edit
                  </Button>
                  <Button
                    variant="danger"
                    size="sm"
                    onClick={() => handleDeleteUser(user.id)}
                  >
                    Delete
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
        {filteredUsers.length === 0 && (
          <div className="text-center text-muted mt-3">
            {searchTerm ? 'No users found matching your search' : 'No users found'}
          </div>
        )}
      </div>

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>{editingUser ? 'Edit User' : 'Add User'}</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleSubmit}>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>First Name</Form.Label>
              <Form.Control
                type="text"
                name="firstName"
                value={formData.firstName}
                onChange={handleInputChange}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Last Name</Form.Label>
              <Form.Control
                type="text"
                name="lastName"
                value={formData.lastName}
                onChange={handleInputChange}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Age</Form.Label>
              <Form.Control
                type="number"
                name="age"
                value={formData.age}
                onChange={handleInputChange}
                min="0"
                max="150"
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Birthday</Form.Label>
              <Form.Control
                type="date"
                name="birthday"
                value={formData.birthday}
                onChange={handleInputChange}
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit">
              {editingUser ? 'Update' : 'Create'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
};

export default Users; 