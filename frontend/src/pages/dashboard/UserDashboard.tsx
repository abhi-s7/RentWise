import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import type { RootState } from '../../store/store';
import {
  getUserTenants,
  getUserRequests,
  getUserProperties,
  createTenantRequest,
  type Tenant,
  type TenantRequest,
  type Property,
} from '../../services/api/dashboardService';
import { useWebSocket, type TenantRequestEvent } from '../../services/websocket/useWebSocket';

const UserDashboard = () => {
  const { user } = useSelector((state: RootState) => state.auth);
  const navigate = useNavigate();
  const [tenants, setTenants] = useState<Tenant[]>([]);
  const [requests, setRequests] = useState<TenantRequest[]>([]);
  const [properties, setProperties] = useState<Property[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showRequestForm, setShowRequestForm] = useState(false);
  const [requestForm, setRequestForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
  });

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }
    fetchData();
  }, [user, navigate]);

  // WebSocket: Listen for tenant request events (only for this user)
  useWebSocket({
    onMessage: (event: TenantRequestEvent) => {
      console.log('User Dashboard received WebSocket event:', event);
      // Only process events for this user
      if (event.requestedByUserId === user?.id) {
        if (event.status === 'APPROVED') {
          // Request approved - refresh tenants and requests
          fetchTenants();
          fetchRequests();
        } else if (event.status === 'REJECTED') {
          // Request rejected - refresh requests
          fetchRequests();
        }
      }
    },
    onConnect: () => {
      console.log('User Dashboard WebSocket connected');
    },
    onError: (error) => {
      console.error('User Dashboard WebSocket error:', error);
    },
  });

  const fetchData = async () => {
    if (!user?.id) return;
    try {
      setLoading(true);
      const [tnts, reqs, props] = await Promise.all([
        getUserTenants(user.id),
        getUserRequests(user.id),
        getUserProperties(user.id),
      ]);
      setTenants(tnts);
      setRequests(reqs);
      setProperties(props);
      setError('');
    } catch (err: any) {
      setError('Failed to load dashboard data');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchTenants = async () => {
    if (!user?.id) return;
    try {
      const tnts = await getUserTenants(user.id);
      setTenants(tnts);
    } catch (err) {
      console.error('Failed to refresh tenants:', err);
    }
  };

  const fetchRequests = async () => {
    if (!user?.id) return;
    try {
      const reqs = await getUserRequests(user.id);
      setRequests(reqs);
    } catch (err) {
      console.error('Failed to refresh requests:', err);
    }
  };

  const handleSubmitRequest = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user?.id) return;
    try {
      await createTenantRequest({
        ...requestForm,
        requestedByUserId: user.id,
        status: 'PENDING',
      });
      setShowRequestForm(false);
      setRequestForm({ firstName: '', lastName: '', email: '', phone: '' });
      // WebSocket will notify admin, but refresh requests to show it in user's list
      await fetchRequests();
    } catch (err) {
      alert('Failed to submit request');
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
        <h1 style={{ fontSize: '32px', fontWeight: 'bold', color: '#111827' }}>User Dashboard</h1>
        <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
          <p style={{ color: '#6b7280', margin: 0 }}>Welcome, {user?.username}!</p>
          <button
            onClick={() => navigate('/profile/edit')}
            style={{
              padding: '8px 16px',
              backgroundColor: '#007bff',
              color: 'white',
              borderRadius: '6px',
              border: 'none',
              cursor: 'pointer',
              fontSize: '14px',
              fontWeight: '600',
              transition: 'all 0.3s ease'
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.backgroundColor = '#0056b3';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.backgroundColor = '#007bff';
            }}
          >
            Edit Profile
          </button>
        </div>
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

      {/* My Properties */}
      <div style={{
        backgroundColor: 'white',
        padding: '24px',
        borderRadius: '8px',
        marginBottom: '24px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
      }}>
        <h2 style={{ fontSize: '24px', fontWeight: '600', marginBottom: '16px', color: '#111827' }}>
          My Properties
        </h2>
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
              </tr>
            </thead>
            <tbody>
              {properties.map((property) => (
                <tr key={property.id} style={{ borderBottom: '1px solid #e5e7eb' }}>
                  <td style={{ padding: '12px' }}>{property.name}</td>
                  <td style={{ padding: '12px' }}>{property.address}</td>
                  <td style={{ padding: '12px' }}>{property.city}</td>
                  <td style={{ padding: '12px' }}>${property.rentAmount}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* My Roommates */}
      <div style={{
        backgroundColor: 'white',
        padding: '24px',
        borderRadius: '8px',
        marginBottom: '24px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
          <h2 style={{ fontSize: '24px', fontWeight: '600', color: '#111827' }}>My Roommates</h2>
          <button
            onClick={() => setShowRequestForm(!showRequestForm)}
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
            {showRequestForm ? 'Cancel' : 'Request New Roommate'}
          </button>
        </div>

        {showRequestForm && (
          <form onSubmit={handleSubmitRequest} style={{
            backgroundColor: '#f9fafb',
            padding: '20px',
            borderRadius: '6px',
            marginBottom: '20px'
          }}>
            <h3 style={{ marginBottom: '16px', fontSize: '18px', fontWeight: '600' }}>New Roommate Request</h3>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '12px' }}>
              <input
                type="text"
                placeholder="First Name"
                value={requestForm.firstName}
                onChange={(e) => setRequestForm({ ...requestForm, firstName: e.target.value })}
                required
                style={{ padding: '8px', borderRadius: '4px', border: '1px solid #d1d5db' }}
              />
              <input
                type="text"
                placeholder="Last Name"
                value={requestForm.lastName}
                onChange={(e) => setRequestForm({ ...requestForm, lastName: e.target.value })}
                required
                style={{ padding: '8px', borderRadius: '4px', border: '1px solid #d1d5db' }}
              />
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '12px' }}>
              <input
                type="email"
                placeholder="Email"
                value={requestForm.email}
                onChange={(e) => setRequestForm({ ...requestForm, email: e.target.value })}
                required
                style={{ padding: '8px', borderRadius: '4px', border: '1px solid #d1d5db' }}
              />
              <input
                type="tel"
                placeholder="Phone"
                value={requestForm.phone}
                onChange={(e) => setRequestForm({ ...requestForm, phone: e.target.value })}
                required
                style={{ padding: '8px', borderRadius: '4px', border: '1px solid #d1d5db' }}
              />
            </div>
            <button
              type="submit"
              style={{
                backgroundColor: '#28a745',
                color: 'white',
                padding: '8px 16px',
                borderRadius: '4px',
                border: 'none',
                cursor: 'pointer',
                fontSize: '14px',
                fontWeight: '600'
              }}
            >
              Submit Request
            </button>
          </form>
        )}

        {tenants.length === 0 ? (
          <p style={{ color: '#6b7280' }}>No roommates found</p>
        ) : (
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ backgroundColor: '#f9fafb' }}>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Name</th>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Email</th>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Phone</th>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Property</th>
              </tr>
            </thead>
            <tbody>
              {tenants.map((tenant) => (
                <tr key={tenant.id} style={{ borderBottom: '1px solid #e5e7eb' }}>
                  <td style={{ padding: '12px' }}>{tenant.firstName} {tenant.lastName}</td>
                  <td style={{ padding: '12px' }}>{tenant.email}</td>
                  <td style={{ padding: '12px' }}>{tenant.phone}</td>
                  <td style={{ padding: '12px' }}>{tenant.propertyName || 'Not assigned'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* My Requests */}
      <div style={{
        backgroundColor: 'white',
        padding: '24px',
        borderRadius: '8px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
      }}>
        <h2 style={{ fontSize: '24px', fontWeight: '600', marginBottom: '16px', color: '#111827' }}>
          My Tenant Requests
        </h2>
        {requests.length === 0 ? (
          <p style={{ color: '#6b7280' }}>No requests found</p>
        ) : (
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ backgroundColor: '#f9fafb' }}>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Name</th>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Email</th>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Phone</th>
                <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #e5e7eb' }}>Status</th>
              </tr>
            </thead>
            <tbody>
              {requests.map((request) => (
                <tr key={request.id} style={{ borderBottom: '1px solid #e5e7eb' }}>
                  <td style={{ padding: '12px' }}>{request.firstName} {request.lastName}</td>
                  <td style={{ padding: '12px' }}>{request.email}</td>
                  <td style={{ padding: '12px' }}>{request.phone}</td>
                  <td style={{ padding: '12px' }}>
                    <span style={{
                      padding: '4px 8px',
                      borderRadius: '4px',
                      fontSize: '12px',
                      fontWeight: '600',
                      backgroundColor: request.status === 'APPROVED' ? '#d4edda' : 
                                       request.status === 'REJECTED' ? '#f8d7da' : '#fff3cd',
                      color: request.status === 'APPROVED' ? '#155724' : 
                             request.status === 'REJECTED' ? '#721c24' : '#856404'
                    }}>
                      {request.status}
                    </span>
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

export default UserDashboard;

