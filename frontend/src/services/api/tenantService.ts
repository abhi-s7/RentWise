import axiosInstance from './axiosInstance';

export interface Tenant {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  dateOfBirth?: string;
  emergencyContact?: string;
  emergencyPhone?: string;
  userId?: number;
  propertyId?: number;
  createdAt?: string;
  updatedAt?: string;
}

// Get all tenants
export const getAllTenants = async (): Promise<Tenant[]> => {
  const response = await axiosInstance.get('/api/tenants');
  return response.data;
};

// Get tenant by ID
export const getTenantById = async (id: number): Promise<Tenant> => {
  const response = await axiosInstance.get(`/api/tenants/${id}`);
  return response.data;
};

// Create tenant
export const createTenant = async (tenant: Partial<Tenant>): Promise<Tenant> => {
  const response = await axiosInstance.post('/api/tenants', tenant);
  return response.data;
};

// Update tenant
export const updateTenant = async (id: number, tenant: Partial<Tenant>): Promise<Tenant> => {
  const response = await axiosInstance.put(`/api/tenants/${id}`, tenant);
  return response.data;
};

// Delete tenant
export const deleteTenant = async (id: number): Promise<void> => {
  // Delete endpoint returns 204 No Content on success
  // Axios treats 204 as success automatically
  await axiosInstance.delete(`/api/tenants/${id}`);
};

// Get tenants by user ID
export const getTenantsByUserId = async (userId: number): Promise<Tenant[]> => {
  const response = await axiosInstance.get(`/api/tenants/user/${userId}`);
  return response.data;
};

// Get tenants by property ID
export const getTenantsByPropertyId = async (propertyId: number): Promise<Tenant[]> => {
  const response = await axiosInstance.get(`/api/tenants/property/${propertyId}`);
  return response.data;
};

// Assign property to tenant
export const assignPropertyToTenant = async (tenantId: number, propertyId: number): Promise<Tenant> => {
  const response = await axiosInstance.put(`/api/tenants/${tenantId}/assign-property?propertyId=${propertyId}`);
  return response.data;
};

