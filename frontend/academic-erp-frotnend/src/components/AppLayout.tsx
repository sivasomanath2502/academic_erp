import { useEffect } from 'react'
import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../utils/useAuth'

const AppLayout = () => {
  const { user, logout, refreshUser, isLoading } = useAuth()
  const navigate = useNavigate()

  // Refresh user on mount to ensure we have latest auth state
  useEffect(() => {
    if (!isLoading && !user) {
      void refreshUser()
    }
  }, [isLoading, refreshUser, user])

  const handleLogout = async () => {
    try {
      await logout()
      navigate('/', { replace: true })
    } catch (error) {
      // Even if logout fails, redirect to home
      navigate('/', { replace: true })
    }
  }

  const navLinks = [
    { to: '/add-student', label: 'Add Student' },
    { to: '/students', label: 'View Students' },
  ]

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50/30 to-slate-50">
      <header className="sticky top-0 z-50 border-b border-slate-200/50 bg-white/80 backdrop-blur-xl shadow-sm">
        <div className="mx-auto flex max-w-6xl flex-col gap-4 px-4 py-4 sm:flex-row sm:items-center sm:justify-between">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-br from-brand-500 to-brand-700 shadow-lg shadow-brand-500/30">
              <svg className="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
              </svg>
            </div>
            <div>
              <p className="text-xs uppercase tracking-[0.3em] text-brand-600 font-semibold">
                Academic ERP
              </p>
              <h1 className="text-xl font-bold text-slate-900">
                Admissions Console
              </h1>
            </div>
          </div>

          <nav className="flex flex-wrap gap-2 text-sm">
            {navLinks.map((link) => (
              <NavLink
                key={link.to}
                to={link.to}
                className={({ isActive }) =>
                  [
                    'relative rounded-full px-5 py-2.5 font-medium transition-all duration-200',
                    isActive
                      ? 'bg-gradient-to-r from-brand-600 to-brand-700 text-white shadow-lg shadow-brand-500/30 scale-105'
                      : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900 hover:scale-105',
                  ].join(' ')
                }
              >
                {({ isActive }) => (
                  <>
                    {link.label}
                    {isActive && (
                      <span className="absolute -bottom-1 left-1/2 h-1 w-1 -translate-x-1/2 rounded-full bg-white"></span>
                    )}
                  </>
                )}
              </NavLink>
            ))}
          </nav>

          <div className="flex items-center gap-4">
            <div className="flex items-center gap-3 rounded-full bg-slate-50 px-3 py-2 border border-slate-200">
              {user?.picture ? (
                <img
                  src={user.picture}
                  alt={user.name}
                  className="h-10 w-10 rounded-full border-2 border-white shadow-md object-cover ring-2 ring-brand-200"
                />
              ) : (
                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-gradient-to-br from-brand-400 to-brand-600 text-white font-semibold shadow-md ring-2 ring-brand-200">
                  {user?.name?.[0]?.toUpperCase() || 'E'}
                </div>
              )}
              <div className="hidden sm:block text-sm leading-tight">
                <p className="font-semibold text-slate-900">{user?.name}</p>
                <p className="text-xs text-slate-500">{user?.email}</p>
              </div>
            </div>
            <button
              onClick={handleLogout}
              className="group rounded-full border-2 border-slate-300 bg-white px-5 py-2.5 text-sm font-semibold text-slate-700 transition-all duration-200 hover:border-red-400 hover:bg-red-50 hover:text-red-600 hover:shadow-md active:scale-95"
            >
              <span className="flex items-center gap-2">
                <svg className="h-4 w-4 transition-transform group-hover:rotate-12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                </svg>
                Log out
              </span>
            </button>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-6xl px-4 py-8 animate-fade-in">
        <Outlet />
      </main>
    </div>
  )
}

export default AppLayout

