import { Navigate, Route, Routes } from 'react-router-dom'
import AccessDeniedPage from './pages/AccessDeniedPage'
import AddStudentPage from './pages/AddStudentPage'
import ViewStudentsPage from './pages/ViewStudentsPage'
import WelcomePage from './pages/WelcomePage'
import ProtectedRoute from './components/ProtectedRoute'
import AppLayout from './components/AppLayout'
import { useAuth } from './utils/useAuth'

function App() {
  const { user } = useAuth()

  return (
    <Routes>
      <Route path="/" element={<WelcomePage />} />
      <Route path="/access-denied" element={<AccessDeniedPage />} />
      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route path="/add-student" element={<AddStudentPage />} />
          <Route path="/students" element={<ViewStudentsPage />} />
        </Route>
      </Route>
      <Route
        path="*"
        element={<Navigate to={user ? '/add-student' : '/'} replace />}
      />
    </Routes>
  )
}

export default App
