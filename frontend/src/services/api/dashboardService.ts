import axiosInstance from './axiosInstance';

export interface Property {
  id: number;
  name: string;
  address: string;
  city: string;
  type: string;
  rentAmount: number;
  status: string;
  ownerName?: string;
  tenantCount?: number;
}

export interface Tenant {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  roommateOf?: string;
  propertyName?: string;
  propertyId?: number;
}

export interface TenantRequest {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  status: string;
  requestedByUserId: number;
}

// Admin Dashboard APIs
export const getAdminProperties = async (): Promise<Property[]> => {
  const response = await axiosInstance.get('/api/dashboard/admin/properties');
  return response.data;
};

export const getAdminTenants = async (): Promise<Tenant[]> => {
  const response = await axiosInstance.get('/api/dashboard/admin/tenants');
  return response.data;
};

export const getPendingRequests = async (): Promise<TenantRequest[]> => {
  const response = await axiosInstance.get('/api/dashboard/admin/pending-requests');
  return response.data;
};

// User Dashboard APIs
export const getUserTenants = async (userId: number): Promise<Tenant[]> => {
  const response = await axiosInstance.get(`/api/dashboard/user/tenants?userId=${userId}`);
  return response.data;
};

export const getUserRequests = async (userId: number): Promise<TenantRequest[]> => {
  const response = await axiosInstance.get(`/api/dashboard/user/requests?userId=${userId}`);
  return response.data;
};

export const getUserProperties = async (userId: number): Promise<Property[]> => {
  const response = await axiosInstance.get(`/api/dashboard/user/properties?userId=${userId}`);
  return response.data;
};

// Action APIs
export const createTenantRequest = async (request: Partial<TenantRequest>): Promise<TenantRequest> => {
  const response = await axiosInstance.post('/api/dashboard/user/tenant-request', request);
  return response.data;
};

export const approveTenantRequest = async (requestId: number): Promise<TenantRequest> => {
  const response = await axiosInstance.put(`/api/dashboard/admin/tenant-requests/${requestId}/approve`);
  return response.data;
};

export const rejectTenantRequest = async (requestId: number): Promise<TenantRequest> => {
  const response = await axiosInstance.put(`/api/dashboard/admin/tenant-requests/${requestId}/reject`);
  return response.data;
};

export const assignPropertyToTenant = async (tenantId: number, propertyId: number): Promise<Tenant> => {
  const response = await axiosInstance.put(`/api/dashboard/admin/tenants/${tenantId}/assign-property?propertyId=${propertyId}`);
  return response.data;
};

