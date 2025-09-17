import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getAllTenants,
  deleteTenant,
  type Tenant,
} from '../../services/api/tenantService';
import { getAllUsers, type User } from '../../services/api/userService';
import { getAllProperties, type Property } from '../../services/api/propertyService';
import TenantForm from './TenantForm';

const TenantsPage = () => {
  const [tenants, setTenants] = useState<Tenant[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [properties, setProperties] = useState<Property[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingTenant, setEditingTenant] = useState<Tenant | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [tenantsData, usersData, propertiesData] = await Promise.all([
        getAllTenants(),
        getAllUsers(),
        getAllProperties(),
      ]);
      setTenants(tenantsData);
      setUsers(usersData);
      setProperties(propertiesData);
      setError('');
    } catch (err: any) {
      setError('Failed to load data');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // Helper function to get user name by ID
  const getUserName = (userId?: number): string => {
    if (!userId) return 'N/A';
    const user = users.find(u => u.id === userId);
    return user ? user.username : `User ID: ${userId}`;
  };

  // Helper function to get property name by ID
  const getPropertyName = (propertyId?: number): string => {
    if (!propertyId) return 'N/A';
    const property = properties.find(p => p.id === propertyId);
    return property ? property.name : `Property ID: ${propertyId}`;
  };

  const handleAdd = () => {
    setEditingTenant(null);
    setShowForm(true);
  };

  const handleEdit = (tenant: Tenant) => {
    setEditingTenant(tenant);
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this tenant?')) {
      return;
    }
    try {
      await deleteTenant(id);
      // Optimistically remove from UI immediately
      setTenants((prev) => prev.filter((tenant) => tenant.id !== id));
      // Then refresh all data to ensure consistency
      await fetchData();
    } catch (err: any) {
      console.error('Delete error:', err);
      // If deletion failed, refresh to get current state
      await fetchData();
      alert(err.response?.data || 'Failed to delete tenant');
    }
  };

  const handleFormClose = () => {
    setShowForm(false);
    setEditingTenant(null);
  };

  const handleFormSuccess = () => {
    setShowForm(false);
    setEditingTenant(null);
    fetchData();
  };

  if (loading) {
    return (
      <div style={{ 
        minHeight: '100vh', 
        backgroundColor: '#f5f5f5', 
        padding: '40px', 
        textAlign: 'center' 
      }}>
        <p style={{ fontSize: '18px', color: '#666' }}>Loading tenants...</p>
      </div>
    );
  }

  return (
    <div style={{ 
      minHeight: '100vh', 
      backgroundColor: '#f5f5f5', 
      padding: '20px',
      fontFamily: 'Arial, sans-serif'
    }}>
      <div style={{
        maxWidth: '1200px',
        margin: '0 auto',
        background: 'white',
        padding: '20px',
        borderRadius: '8px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
      }}>
        {/* Header */}
        <div style={{ marginBottom: '20px' }}>
          <button
            onClick={() => navigate('/dashboard')}
            style={{
              padding: '10px 20px',
              backgroundColor: '#28a745',
              color: 'white',
              borderRadius: '4px',
              border: 'none',
              cursor: 'pointer',
              marginRight: '10px',
              marginBottom: '20px',
              fontSize: '14px',
              fontWeight: '500',
              transition: 'all 0.3s ease'
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.backgroundColor = '#218838';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.backgroundColor = '#28a745';
            }}
          >
            ← Back to Dashboard
          </button>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h1 style={{ color: '#333', margin: 0, fontSize: '28px' }}>Tenants Management</h1>
            <button
              onClick={handleAdd}
              style={{
                padding: '10px 20px',
                backgroundColor: '#007bff',
                color: 'white',
                borderRadius: '4px',
                border: 'none',
                cursor: 'pointer',
                fontSize: '14px',
                fontWeight: '500',
                transition: 'all 0.3s ease'
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.backgroundColor = '#0056b3';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.backgroundColor = '#007bff';
              }}
            >
              Add New Tenant
            </button>
          </div>
        </div>

        {error && (
          <div style={{
            padding: '15px',
            marginBottom: '20px',
            borderRadius: '4px',
            backgroundColor: '#f8d7da',
            color: '#721c24',
            borderLeft: '4px solid #dc3545'
          }}>
            {error}
          </div>
        )}

        {showForm && (
          <TenantForm
            tenant={editingTenant}
            onClose={handleFormClose}
            onSuccess={handleFormSuccess}
          />
        )}

        {/* Tenants Table */}
        <table style={{
          width: '100%',
          borderCollapse: 'collapse',
          marginTop: '20px'
        }}>
          <thead>
            <tr>
              <th style={{
                padding: '12px',
                textAlign: 'left',
                backgroundColor: '#007bff',
                color: 'white',
                borderBottom: '2px solid #0056b3'
              }}>
                ID
              </th>
              <th style={{
                padding: '12px',
                textAlign: 'left',
                backgroundColor: '#007bff',
                color: 'white',
                borderBottom: '2px solid #0056b3'
              }}>
                Name
              </th>
              <th style={{
                padding: '12px',
                textAlign: 'left',
                backgroundColor: '#007bff',
                color: 'white',
                borderBottom: '2px solid #0056b3'
              }}>
                Email
              </th>
              <th style={{
                padding: '12px',
                textAlign: 'left',
                backgroundColor: '#007bff',
                color: 'white',
                borderBottom: '2px solid #0056b3'
              }}>
                Phone
              </th>
              <th style={{
                padding: '12px',
                textAlign: 'left',
                backgroundColor: '#007bff',
                color: 'white',
                borderBottom: '2px solid #0056b3'
              }}>
                Roommate Of
              </th>
              <th style={{
                padding: '12px',
                textAlign: 'left',
                backgroundColor: '#007bff',
                color: 'white',
                borderBottom: '2px solid #0056b3'
              }}>
                Property
              </th>
              <th style={{
                padding: '12px',
                textAlign: 'left',
                backgroundColor: '#007bff',
                color: 'white',
                borderBottom: '2px solid #0056b3'
              }}>
                Actions
              </th>
            </tr>
          </thead>
          <tbody>
            {tenants.length === 0 ? (
              <tr>
                <td colSpan={7} style={{
                  padding: '40px',
                  textAlign: 'center',
                  color: '#999',
                  fontSize: '16px'
                }}>
                  No tenants found
                </td>
              </tr>
            ) : (
              tenants.map((tenant) => (
                <tr
                  key={tenant.id}
                  style={{
                    borderBottom: '1px solid #ddd',
                    transition: 'background-color 0.2s'
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.backgroundColor = '#f5f5f5';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.backgroundColor = 'transparent';
                  }}
                >
                  <td style={{ padding: '12px' }}>{tenant.id}</td>
                  <td style={{ padding: '12px', fontWeight: '500' }}>
                    {tenant.firstName} {tenant.lastName}
                  </td>
                  <td style={{ padding: '12px' }}>{tenant.email}</td>
                  <td style={{ padding: '12px' }}>{tenant.phone || 'N/A'}</td>
                  <td style={{ padding: '12px' }}>
                    {getUserName(tenant.userId)}
                  </td>
                  <td style={{ padding: '12px' }}>
                    {getPropertyName(tenant.propertyId)}
                  </td>
                  <td style={{ padding: '12px' }}>
                    <div style={{ display: 'flex', gap: '8px' }}>
                      <button
                        onClick={() => handleEdit(tenant)}
                        style={{
                          padding: '6px 12px',
                          backgroundColor: '#007bff',
                          color: 'white',
                          borderRadius: '4px',
                          border: 'none',
                          cursor: 'pointer',
                          fontSize: '13px',
                          fontWeight: '500',
                          transition: 'all 0.3s ease'
                        }}
                        onMouseEnter={(e) => {
                          e.currentTarget.style.backgroundColor = '#0056b3';
                        }}
                        onMouseLeave={(e) => {
                          e.currentTarget.style.backgroundColor = '#007bff';
                        }}
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => tenant.id && handleDelete(tenant.id)}
                        style={{
                          padding: '6px 12px',
                          backgroundColor: '#dc3545',
                          color: 'white',
                          borderRadius: '4px',
                          border: 'none',
                          cursor: 'pointer',
                          fontSize: '13px',
                          fontWeight: '500',
                          transition: 'all 0.3s ease'
                        }}
                        onMouseEnter={(e) => {
                          e.currentTarget.style.backgroundColor = '#c82333';
                        }}
                        onMouseLeave={(e) => {
                          e.currentTarget.style.backgroundColor = '#dc3545';
                        }}
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default TenantsPage;

