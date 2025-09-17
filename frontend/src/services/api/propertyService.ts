import axiosInstance from './axiosInstance';

export interface Property {
  id?: number;
  name: string;
  address: string;
  city: string;
  state: string;
  zipCode: string;
  type: string; // APARTMENT, HOUSE, CONDO
  bedrooms: number;
  bathrooms: number;
  rentAmount: number;
  status?: string; // AVAILABLE, RENTED, MAINTENANCE
  userId?: number;
  createdAt?: string;
  updatedAt?: string;
}

// Get all properties
export const getAllProperties = async (): Promise<Property[]> => {
  const response = await axiosInstance.get('/api/properties');
  return response.data;
};

// Get property by ID
export const getPropertyById = async (id: number): Promise<Property> => {
  const response = await axiosInstance.get(`/api/properties/${id}`);
  return response.data;
};

// Create property
export const createProperty = async (property: Partial<Property>): Promise<Property> => {
  const response = await axiosInstance.post('/api/properties', property);
  return response.data;
};

// Update property
export const updateProperty = async (id: number, property: Partial<Property>): Promise<Property> => {
  const response = await axiosInstance.put(`/api/properties/${id}`, property);
  return response.data;
};

// Delete property
export const deleteProperty = async (id: number): Promise<void> => {
  await axiosInstance.delete(`/api/properties/${id}`);
};

// Get properties by user ID
export const getPropertiesByUserId = async (userId: number): Promise<Property[]> => {
  const response = await axiosInstance.get(`/api/properties/user/${userId}`);
  return response.data;
};

