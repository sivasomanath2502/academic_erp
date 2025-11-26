import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { apiClient, endpoints, getErrorMessage } from '../utils/api'
import type {
  Domain,
  PhotoUploadResponse,
  StudentAdmissionForm,
  StudentResponse,
} from '../models'

const emptyForm: StudentAdmissionForm = {
  firstName: '',
  lastName: '',
  email: '',
  domainId: '',
  joinYear: '',
  photographPath: '',
}

const AddStudentPage = () => {
  const [form, setForm] = useState<StudentAdmissionForm>(emptyForm)
  const [domains, setDomains] = useState<Domain[]>([])
  const [fetchingDomains, setFetchingDomains] = useState(true)
  const [status, setStatus] =
    useState<'idle' | 'saving' | 'success' | 'error'>('idle')
  const [feedback, setFeedback] = useState('')
  const [lastStudent, setLastStudent] = useState<StudentResponse | null>(null)
  const [photoStatus, setPhotoStatus] =
    useState<'idle' | 'uploading' | 'success' | 'error'>('idle')
  const [photoError, setPhotoError] = useState('')
  const [photoPreview, setPhotoPreview] = useState<string | null>(null)

  const canSubmit = useMemo(() => {
    return (
      form.firstName.trim().length > 1 &&
      form.lastName.trim().length > 1 &&
      /\S+@\S+\.\S+/.test(form.email) &&
      Boolean(form.domainId) &&
      /^\d{4}$/.test(form.joinYear) &&
      Boolean(form.photographPath)
    )
  }, [form])

  const loadDomains = async () => {
    setFetchingDomains(true)
    try {
      const { data } = await apiClient.get<Domain[]>(endpoints.domains)
      setDomains(data)
    } catch (err) {
      setFeedback(getErrorMessage(err))
      setStatus('error')
    } finally {
      setFetchingDomains(false)
    }
  }

  useEffect(() => {
    loadDomains()
  }, [])

  useEffect(() => {
    return () => {
      if (photoPreview) {
        URL.revokeObjectURL(photoPreview)
      }
    }
  }, [photoPreview])

  const handlePhotoUpload = async (file: File) => {
    setPhotoStatus('uploading')
    setPhotoError('')

    try {
      const formData = new FormData()
      formData.append('file', file)
      const { data } = await apiClient.post<PhotoUploadResponse>(
        endpoints.uploadPhoto,
        formData,
      )

      setForm((prev) => ({ ...prev, photographPath: data.path }))
      setPhotoStatus('success')
    } catch (err) {
      setPhotoStatus('error')
      setPhotoError(getErrorMessage(err))
      setForm((prev) => ({ ...prev, photographPath: '' }))
    }
  }

  const handlePhotoChange = (
    event: React.ChangeEvent<HTMLInputElement>,
  ) => {
    const file = event.target.files?.[0]
    if (!file) return

    if (!file.type.startsWith('image/')) {
      setPhotoStatus('error')
      setPhotoError('Please upload a valid image file.')
      return
    }

    if (file.size > 5 * 1024 * 1024) {
      setPhotoStatus('error')
      setPhotoError('Image must be smaller than 5 MB.')
      return
    }

    const previewUrl = URL.createObjectURL(file)
    setPhotoPreview((prev) => {
      if (prev) URL.revokeObjectURL(prev)
      return previewUrl
    })

    void handlePhotoUpload(file)
  }

  const handlePhotoReset = () => {
    if (photoPreview) {
      URL.revokeObjectURL(photoPreview)
    }
    setPhotoPreview(null)
    setPhotoStatus('idle')
    setPhotoError('')
    setForm((prev) => ({ ...prev, photographPath: '' }))
  }

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>,
  ) => {
    const { name, value } = e.target
    setForm((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()
    if (!canSubmit) return

    setStatus('saving')
    setFeedback('')

    try {
      const payload = {
        firstName: form.firstName.trim(),
        lastName: form.lastName.trim(),
        email: form.email.trim(),
        domainId: Number(form.domainId),
        joinYear: Number(form.joinYear),
        photographPath: form.photographPath?.trim() || undefined,
      }

      const { data } = await apiClient.post<StudentResponse>(
        endpoints.admitStudent,
        payload,
      )

      setFeedback(
        `Student admitted. Generated roll number: ${data.rollNumber}`,
      )
      setStatus('success')
      setForm(emptyForm)
      setLastStudent(data)
      handlePhotoReset()
    } catch (err) {
      setFeedback(getErrorMessage(err))
      setStatus('error')
    }
  }

  return (
    <div className="animate-fade-in">
      <div className="mb-8 flex flex-col gap-4 rounded-2xl border border-white/50 bg-white/80 backdrop-blur-xl p-6 shadow-xl shadow-brand-500/5 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <div className="mb-2 flex items-center gap-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-to-br from-brand-500 to-brand-700 text-white shadow-md">
              <span className="text-sm font-bold">1</span>
            </div>
            <p className="text-xs uppercase tracking-[0.3em] text-brand-600 font-semibold">
              Step 01
            </p>
          </div>
          <h2 className="text-3xl font-bold text-slate-900">
            Add a new student
          </h2>
          <p className="mt-2 text-sm text-slate-600">
            Capture admission details and assign the student to an ERP domain.
          </p>
        </div>
        <button
          onClick={loadDomains}
          className="group rounded-full border-2 border-slate-300 bg-white px-5 py-2.5 text-sm font-semibold text-slate-700 transition-all duration-200 hover:border-brand-500 hover:bg-brand-50 hover:text-brand-700 hover:shadow-md active:scale-95"
        >
          <span className="flex items-center gap-2">
            <svg className="h-4 w-4 transition-transform group-hover:rotate-180" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            Refresh domains
          </span>
        </button>
      </div>

      <form
        onSubmit={handleSubmit}
        className="grid gap-6 rounded-3xl border border-white/50 bg-white/80 backdrop-blur-xl p-8 shadow-2xl shadow-slate-200/40"
      >
        <div className="grid gap-6 sm:grid-cols-2">
          <label className="group text-sm font-semibold text-slate-700">
            First Name
            <input
              type="text"
              name="firstName"
              value={form.firstName}
              onChange={handleChange}
              required
              className="mt-2 w-full rounded-xl border-2 border-slate-200 bg-white px-4 py-3 text-base text-slate-900 transition-all duration-200 focus:border-brand-500 focus:outline-none focus:ring-4 focus:ring-brand-200/50 group-hover:border-slate-300"
              placeholder="Jane"
            />
          </label>

          <label className="group text-sm font-semibold text-slate-700">
            Last Name
            <input
              type="text"
              name="lastName"
              value={form.lastName}
              onChange={handleChange}
              required
              className="mt-2 w-full rounded-xl border-2 border-slate-200 bg-white px-4 py-3 text-base text-slate-900 transition-all duration-200 focus:border-brand-500 focus:outline-none focus:ring-4 focus:ring-brand-200/50 group-hover:border-slate-300"
              placeholder="Doe"
            />
          </label>
        </div>

        <label className="group text-sm font-semibold text-slate-700">
          Email
          <input
            type="email"
            name="email"
            value={form.email}
            onChange={handleChange}
            required
            className="mt-2 w-full rounded-xl border-2 border-slate-200 bg-white px-4 py-3 text-base text-slate-900 transition-all duration-200 focus:border-brand-500 focus:outline-none focus:ring-4 focus:ring-brand-200/50 group-hover:border-slate-300"
            placeholder="student@erp.edu"
          />
        </label>

        <div className="grid gap-6 sm:grid-cols-2">
          <label className="group text-sm font-semibold text-slate-700">
            Domain
            <select
              name="domainId"
              value={form.domainId}
              onChange={handleChange}
              required
              className="mt-2 w-full rounded-xl border-2 border-slate-200 bg-white px-4 py-3 text-base text-slate-900 transition-all duration-200 focus:border-brand-500 focus:outline-none focus:ring-4 focus:ring-brand-200/50 group-hover:border-slate-300"
            >
              <option value="" disabled>
                {fetchingDomains ? 'Loading domains...' : 'Select a domain'}
              </option>
              {domains.map((domain) => (
                <option key={domain.domainId} value={domain.domainId}>
                  {domain.program}
                </option>
              ))}
            </select>
          </label>

          <label className="group text-sm font-semibold text-slate-700">
            Join Year
            <input
              type="text"
              inputMode="numeric"
              name="joinYear"
              value={form.joinYear}
              onChange={handleChange}
              required
              maxLength={4}
              className="mt-2 w-full rounded-xl border-2 border-slate-200 bg-white px-4 py-3 text-base text-slate-900 transition-all duration-200 focus:border-brand-500 focus:outline-none focus:ring-4 focus:ring-brand-200/50 group-hover:border-slate-300"
              placeholder="2025"
            />
          </label>
        </div>

        <label className="group text-sm font-semibold text-slate-700">
          Student photograph
          <div className="mt-2 rounded-xl border-2 border-dashed border-slate-300 bg-slate-50/50 p-4 transition-all duration-200 group-hover:border-brand-400 group-hover:bg-brand-50/30">
            <input
              type="file"
              accept="image/*"
              onChange={handlePhotoChange}
              className="w-full cursor-pointer text-base text-slate-900 file:mr-4 file:cursor-pointer file:rounded-full file:border-0 file:bg-gradient-to-r file:from-brand-500 file:to-brand-700 file:px-5 file:py-2.5 file:text-sm file:font-semibold file:text-white file:shadow-md file:transition-all file:hover:shadow-lg focus:outline-none"
            />
          </div>
          <p className="mt-2 text-xs text-slate-500">
            Accepted types: JPG, PNG, GIF, WEBP. Max size 5 MB.
          </p>
          <div className="mt-4 flex flex-wrap items-center gap-4">
            {photoPreview && (
              <div className="relative group/photo">
                <img
                  src={photoPreview}
                  alt="Preview"
                  className="h-28 w-28 rounded-2xl border-2 border-white object-cover shadow-lg ring-2 ring-brand-200 transition-transform group-hover/photo:scale-105"
                />
                <div className="absolute inset-0 rounded-2xl bg-black/0 transition-colors group-hover/photo:bg-black/10"></div>
              </div>
            )}
            {form.photographPath && photoStatus === 'success' && (
              <div className="flex items-center gap-2 rounded-full bg-gradient-to-r from-emerald-50 to-emerald-100 border border-emerald-200 px-4 py-2 text-sm font-semibold text-emerald-700 shadow-sm">
                <svg className="h-4 w-4" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                </svg>
                Photo uploaded
              </div>
            )}
            {photoStatus === 'uploading' && (
              <div className="flex items-center gap-2 text-sm text-slate-600">
                <div className="h-4 w-4 animate-spin rounded-full border-2 border-brand-200 border-t-brand-600"></div>
                Uploading photo...
              </div>
            )}
            {photoError && (
              <div className="rounded-full border-2 border-red-200 bg-red-50 px-4 py-2 text-sm font-semibold text-red-700 shadow-sm">
                {photoError}
              </div>
            )}
            {(form.photographPath || photoPreview) && (
              <button
                type="button"
                onClick={handlePhotoReset}
                className="rounded-full border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition-all hover:border-red-400 hover:bg-red-50 hover:text-red-600 active:scale-95"
              >
                Remove
              </button>
            )}
          </div>
        </label>

        <div className="flex flex-col items-start gap-4 sm:flex-row sm:items-center sm:justify-between rounded-xl bg-slate-50/50 p-4 border border-slate-200">
          <div className="text-sm text-slate-600">
            {status === 'saving' ? (
              <span className="flex items-center gap-2">
                <div className="h-4 w-4 animate-spin rounded-full border-2 border-brand-200 border-t-brand-600"></div>
                Submitting admission...
              </span>
            ) : (
              'All fields including a photograph are required.'
            )}
          </div>
          <button
            type="submit"
            disabled={!canSubmit || status === 'saving'}
            className="group w-full rounded-full bg-gradient-to-r from-brand-600 to-brand-700 px-8 py-3.5 text-sm font-bold text-white shadow-lg shadow-brand-500/30 transition-all duration-300 hover:scale-105 hover:shadow-xl hover:shadow-brand-500/40 disabled:cursor-not-allowed disabled:scale-100 disabled:bg-slate-300 disabled:shadow-none active:scale-95 sm:w-auto"
          >
            {status === 'saving' ? (
              <span className="flex items-center justify-center gap-2">
                <div className="h-4 w-4 animate-spin rounded-full border-2 border-white/30 border-t-white"></div>
                Saving...
              </span>
            ) : (
              <span className="flex items-center gap-2">
                <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                </svg>
                Admit student
              </span>
            )}
          </button>
        </div>

        {feedback && (
          <div
            className={[
              'rounded-2xl border-2 px-5 py-4 text-sm font-semibold shadow-md',
              status === 'success'
                ? 'border-emerald-200 bg-gradient-to-r from-emerald-50 to-emerald-100 text-emerald-800'
                : 'border-red-200 bg-gradient-to-r from-red-50 to-red-100 text-red-800',
            ].join(' ')}
          >
            {feedback}
          </div>
        )}

        {lastStudent && (
          <div className="rounded-2xl border-2 border-brand-200 bg-gradient-to-r from-brand-50 to-blue-50 px-5 py-4 text-sm font-medium text-slate-800 shadow-md">
            <div className="flex items-center gap-2 mb-2">
              <svg className="h-5 w-5 text-brand-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span className="font-bold">Last admission:</span>
            </div>
            <p className="ml-7">
              {lastStudent.firstName} {lastStudent.lastName} · Roll{' '}
              <span className="font-mono font-bold text-brand-700">{lastStudent.rollNumber}</span> · Domain{' '}
              <span className="font-semibold">{lastStudent.domainProgram}</span> · {lastStudent.joinYear}
            </p>
          </div>
        )}
      </form>
    </div>
  )
}

export default AddStudentPage

