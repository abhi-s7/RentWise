import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import type { RootState } from '../../store/store';
import {
  getAdminProperties,
  getAdminTenants,
  getPendingRequests,
  approveTenantRequest,
  rejectTenantRequest,
  assignPropertyToTenant,
  type Property,
  type Tenant,
  type TenantRequest,
} from '../../services/api/dashboardService';
import { useWebSocket, type TenantRequestEvent } from '../../services/websocket/useWebSocket';

const AdminDashboard = () => {
  const { user } = useSelector((state: RootState) => state.auth);
  const navigate = useNavigate();
  const [properties, setProperties] = useState<Property[]>([]);
  const [tenants, setTenants] = useState<Tenant[]>([]);
  const [pendingRequests, setPendingRequests] = useState<TenantRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchData();
  }, []);

  // WebSocket: Listen for tenant request events
  useWebSocket({
    onMessage: (event: TenantRequestEvent) => {
      console.log('Admin Dashboard received WebSocket event:', event);
      if (event.status === 'CREATED') {
        // New request created - refresh pending requests
        fetchPendingRequests();
      } else if (event.status === 'APPROVED' || event.status === 'REJECTED') {
        // Request approved/rejected - refresh both requests and tenants
        fetchPendingRequests();
        fetchTenants();
      }
    },
    onConnect: () => {
      console.log('Admin Dashboard WebSocket connected');
    },
    onError: (error) => {
      console.error('Admin Dashboard WebSocket error:', error);
    },
  });

  const fetchData = async () => {
    try {
      setLoading(true);
      const [props, tnts, requests] = await Promise.all([
        getAdminProperties(),
        getAdminTenants(),
        getPendingRequests(),
      ]);
      setProperties(props);
      setTenants(tnts);
      setPendingRequests(requests);
      setError('');
    } catch (err: any) {
      setError('Failed to load dashboard data');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchPendingRequests = async () => {
    try {
      const requests = await getPendingRequests();
      setPendingRequests(requests);
    } catch (err) {
      console.error('Failed to refresh pending requests:', err);
    }
  };

  const fetchTenants = async () => {
    try {
      const tnts = await getAdminTenants();
      setTenants(tnts);
    } catch (err) {
      console.error('Failed to refresh tenants:', err);
    }
  };

  const handleApprove = async (requestId: number) => {
    try {
      await approveTenantRequest(requestId);
      // WebSocket will handle the update, but refresh to ensure consistency
      await fetchPendingRequests();
      await fetchTenants();
    } catch (err) {
      alert('Failed to approve request');
    }
  };

  const handleReject = async (requestId: number) => {
    try {
      await rejectTenantRequest(requestId);
      // WebSocket will handle the update, but refresh to ensure consistency
      await fetchPendingRequests();
    } catch (err) {
      alert('Failed to reject request');
    }
  };

  const handleAssignProperty = async (tenantId: number, propertyId: number) => {
    try {
      await assignPropertyToTenant(tenantId, propertyId);
      await fetchData();
    } catch (err) {
      alert('Failed to assign property');
    }
  };

  if (loading) {
    return (
      <div style={{ padding: '40px', textAlign: 'center' }}>
        <p>Loading dashboard...</p>
      </div>
    );
  }

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f3f4f6', padding: '32px' }}>
      <div style={{ marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1 style={{ fontSize: '32px', fontWeight: 'bold', color: '#111827' }}>Admin Dashboard</h1>
        <p style={{ color: '#6b7280' }}>Welcome, {user?.username}!</p>
      </div>

      {error && (
        <div style={{
          backgroundColor: '#fee2e2',
          border: '1px solid #fca5a5',
          color: '#991b1b',
          padding: '12px',
          borderRadius: '6px',
          marginBottom: '20px'
        }}>
          {error}
        </div>
      )}

      {/* Pending Tenant Requests */}
      <div style={{
        backgroundColor: 'white',
        padding: '24px',
        borderRadius: '8px',
        marginBottom: '24px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
      }}>
        <h2 style={{ fontSize: '24px', fontWeight: '600', marginBottom: '16px', color: '#111827' }}>
          Pending Tenant Requests
        </h2>
        {pendingRequests.length === 0 ? (
          <p style={{ color: '#6b7280' }}>No pending requests</p>
        ) : (
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ backgroundColor: '#f9fafb' }}>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Name</th>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Email</th>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Phone</th>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {pendingRequests.map((request) => (
                <tr key={request.id} style={{ borderBottom: '1px solid #e5e7eb' }}>
                  <td style={{ padding: '12px' }}>{request.firstName} {request.lastName}</td>
                  <td style={{ padding: '12px' }}>{request.email}</td>
                  <td style={{ padding: '12px' }}>{request.phone}</td>
                  <td style={{ padding: '12px' }}>
                    <button
                      onClick={() => handleApprove(request.id)}
                      style={{
                        backgroundColor: '#28a745',
                        color: 'white',
                        padding: '6px 12px',
                        borderRadius: '4px',
                        border: 'none',
                        cursor: 'pointer',
                        marginRight: '8px'
                      }}
                    >
                      Approve
                    </button>
                    <button
                      onClick={() => handleReject(request.id)}
                      style={{
                        backgroundColor: '#dc3545',
                        color: 'white',
                        padding: '6px 12px',
                        borderRadius: '4px',
                        border: 'none',
                        cursor: 'pointer'
                      }}
                    >
                      Reject
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Properties */}
      <div style={{
        backgroundColor: 'white',
        padding: '24px',
        borderRadius: '8px',
        marginBottom: '24px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
          <h2 style={{ fontSize: '24px', fontWeight: '600', color: '#111827' }}>
            All Properties
          </h2>
          <button
            onClick={() => navigate('/properties')}
            style={{
              backgroundColor: '#2563eb',
              color: 'white',
              padding: '8px 16px',
              borderRadius: '6px',
              border: 'none',
              cursor: 'pointer',
              fontSize: '14px',
              fontWeight: '600'
            }}
          >
            Manage Properties
          </button>
        </div>
        {properties.length === 0 ? (
          <p style={{ color: '#6b7280' }}>No properties found</p>
        ) : (
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ backgroundColor: '#f9fafb' }}>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Name</th>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Address</th>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>City</th>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Rent</th>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Owner</th>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Tenants</th>
              </tr>
            </thead>
            <tbody>
              {properties.map((property) => (
                <tr key={property.id} style={{ borderBottom: '1px solid #e5e7eb' }}>
                  <td style={{ padding: '12px' }}>{property.name}</td>
                  <td style={{ padding: '12px' }}>{property.address}</td>
                  <td style={{ padding: '12px' }}>{property.city}</td>
                  <td style={{ padding: '12px' }}>${property.rentAmount}</td>
                  <td style={{ padding: '12px' }}>{property.ownerName || 'N/A'}</td>
                  <td style={{ padding: '12px' }}>{property.tenantCount || 0}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Tenants */}
      <div style={{
        backgroundColor: 'white',
        padding: '24px',
        borderRadius: '8px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
          <h2 style={{ fontSize: '24px', fontWeight: '600', color: '#111827' }}>
            All Tenants
          </h2>
          <button
            onClick={() => navigate('/tenants')}
            style={{
              backgroundColor: '#007bff',
              color: 'white',
              padding: '8px 16px',
              borderRadius: '6px',
              border: 'none',
              cursor: 'pointer',
              fontSize: '14px',
              fontWeight: '600'
            }}
          >
            Manage Tenants
          </button>
        </div>
        {tenants.length === 0 ? (
          <p style={{ color: '#6b7280' }}>No tenants found</p>
        ) : (
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ backgroundColor: '#f9fafb' }}>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Name</th>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Email</th>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Roommate Of</th>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Property</th>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Assign Property</th>
              </tr>
            </thead>
            <tbody>
              {tenants.map((tenant) => (
                <tr key={tenant.id} style={{ borderBottom: '1px solid #e5e7eb' }}>
                  <td style={{ padding: '12px' }}>{tenant.firstName} {tenant.lastName}</td>
                  <td style={{ padding: '12px' }}>{tenant.email}</td>
                  <td style={{ padding: '12px' }}>{tenant.roommateOf || 'N/A'}</td>
                  <td style={{ padding: '12px' }}>{tenant.propertyName || 'Not assigned'}</td>
                  <td style={{ padding: '12px' }}>
                    <select
                      onChange={(e) => {
                        const propertyId = Number(e.target.value);
                        if (propertyId) {
                          handleAssignProperty(tenant.id, propertyId);
                        }
                      }}
                      value={tenant.propertyId || ''}
                      style={{
                        padding: '6px',
                        borderRadius: '4px',
                        border: '1px solid #d1d5db'
                      }}
                    >
                      <option value="">Select Property</option>
                      {properties.map((prop) => (
                        <option key={prop.id} value={prop.id}>
                          {prop.name}
                        </option>
                      ))}
                    </select>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default AdminDashboard;

