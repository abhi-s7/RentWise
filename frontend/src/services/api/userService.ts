import axiosInstance from './axiosInstance';

export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  enabled: boolean;
  password?: string; // Optional password field for update operations
}

// Separate interface for user update payload (includes password)
export interface UpdateUserPayload {
  email?: string;
  password?: string;
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

// Register new user
export interface RegisterUserPayload {
  username: string;
  email: string;
  password: string;
  role?: string; // Optional, defaults to "USER" on backend
}

export const registerUser = async (user: RegisterUserPayload): Promise<User> => {
  const response = await axiosInstance.post('/api/users/register', user);
  return response.data;
};

// Update user
export const updateUser = async (id: number, user: Partial<User> | UpdateUserPayload): Promise<User> => {
  const response = await axiosInstance.put(`/api/users/${id}`, user);
  return response.data;
};

