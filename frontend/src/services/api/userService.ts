import axiosInstance from './axiosInstance';

export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  enabled: boolean;
}

// Get all users
export const getAllUsers = async (): Promise<User[]> => {
  const response = await axiosInstance.get('/api/users');
  return response.data;
};

// Get user by ID
export const getUserById = async (id: number): Promise<User> => {
  const response = await axiosInstance.get(`/api/users/${id}`);
  return response.data;
};

// Update user
export const updateUser = async (id: number, user: Partial<User>): Promise<User> => {
  const response = await axiosInstance.put(`/api/users/${id}`, user);
  return response.data;
};

