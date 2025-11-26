import axios from 'axios'

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export const apiClient = axios.create({
  baseURL: `${API_BASE_URL}/api`,
  withCredentials: true,
  timeout: 10000,
})

// Separate client for non-API endpoints (OAuth)
export const oauthClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  timeout: 10000,
})

export const getErrorMessage = (error: unknown) => {
  if (axios.isAxiosError(error)) {
    return (
      error.response?.data?.message ||
      error.response?.data?.error ||
      error.message
    )
  }
  if (error instanceof Error) return error.message
  return 'Unexpected error. Please try again.'
}

export const endpoints = {
  login: '/login',
  signout: '/signout',
  currentUser: '/auth/me',
  domains: '/domains',
  admitStudent: '/students/admit',
  students: '/students',
  uploadPhoto: '/uploads/photo',
}

