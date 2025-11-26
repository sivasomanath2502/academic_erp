export interface Domain {
  domainId: number
  program: string
  capacity: number
}

export interface Student {
  studentId: number
  rollNumber: string
  firstName: string
  lastName: string
  email: string
  domainProgram: string
  joinYear: number
}

export interface StudentAdmissionForm {
  firstName: string
  lastName: string
  email: string
  domainId: string
  joinYear: string
  photographPath?: string
}

export interface StudentResponse {
  studentId: number
  rollNumber: string
  firstName: string
  lastName: string
  email: string
  domainProgram: string
  joinYear: number
}

export interface UserProfile {
  email: string
  name: string
  picture?: string
  token: string
}

export interface ApiError {
  message?: string
  status?: number
}

export interface PhotoUploadResponse {
  path: string
  originalName: string
  size: number
}

