import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/common/Navbar';
import Sidebar from './components/common/Sidebar';

// Public Pages
import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';

// Patient Pages
import PatientDashboard from './pages/patient/PatientDashboard';
import BookAppointmentPage from './pages/patient/BookAppointmentPage';
import PatientAppointments from './pages/patient/PatientAppointments';
import WaitingListPage from './pages/patient/WaitingListPage';
import PatientProfilePage from './pages/patient/PatientProfilePage';

// Doctor Pages
import DoctorDashboard from './pages/doctor/DoctorDashboard';
import DoctorSchedulePage from './pages/doctor/DoctorSchedulePage';
import DoctorWaitingListPage from './pages/doctor/DoctorWaitingListPage';
import DoctorAnalyticsPage from './pages/doctor/DoctorAnalyticsPage';
import DoctorProfilePage from './pages/doctor/DoctorProfilePage';

// Admin Pages
import AdminDashboard from './pages/admin/AdminDashboard';
import DoctorManagementPage from './pages/admin/DoctorManagementPage';
import PatientManagementPage from './pages/admin/PatientManagementPage';
import AppointmentManagementPage from './pages/admin/AppointmentManagementPage';
import HospitalAnalyticsPage from './pages/admin/HospitalAnalyticsPage';
import AlgorithmPerformanceLab from './pages/admin/AlgorithmPerformanceLab';

// Protected Route Component
function ProtectedRoute({ children, allowedRole }) {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50">
        <div className="w-8 h-8 border-4 border-teal-500 border-t-transparent rounded-full animate-spin"></div>
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRole && user.role !== allowedRole && user.role !== 'ADMIN') {
    // Redirect to user's appropriate portal
    if (user.role === 'DOCTOR') return <Navigate to="/doctor" replace />;
    if (user.role === 'PATIENT') return <Navigate to="/patient" replace />;
    return <Navigate to="/" replace />;
  }

  return children;
}

// Portal Layout with Navbar and Collapsible Sidebar
function PortalLayout({ role, children }) {
  return (
    <div className="min-h-screen flex flex-col bg-slate-50">
      <Navbar />
      <div className="flex-1 flex overflow-hidden">
        <Sidebar role={role} />
        <main className="flex-1 overflow-y-auto">
          {children}
        </main>
      </div>
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<LandingPage />} />
          <Route
            path="/login"
            element={
              <div className="min-h-screen flex flex-col bg-slate-50">
                <Navbar />
                <LoginPage />
              </div>
            }
          />
          <Route
            path="/register"
            element={
              <div className="min-h-screen flex flex-col bg-slate-50">
                <Navbar />
                <RegisterPage />
              </div>
            }
          />

          {/* Patient Portal Routes */}
          <Route
            path="/patient"
            element={
              <ProtectedRoute allowedRole="PATIENT">
                <PortalLayout role="PATIENT">
                  <PatientDashboard />
                </PortalLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/patient/book"
            element={
              <ProtectedRoute allowedRole="PATIENT">
                <PortalLayout role="PATIENT">
                  <BookAppointmentPage />
                </PortalLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/patient/appointments"
            element={
              <ProtectedRoute allowedRole="PATIENT">
                <PortalLayout role="PATIENT">
                  <PatientAppointments />
                </PortalLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/patient/waiting-list"
            element={
              <ProtectedRoute allowedRole="PATIENT">
                <PortalLayout role="PATIENT">
                  <WaitingListPage />
                </PortalLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/patient/profile"
            element={
              <ProtectedRoute allowedRole="PATIENT">
                <PortalLayout role="PATIENT">
                  <PatientProfilePage />
                </PortalLayout>
              </ProtectedRoute>
            }
          />

          {/* Doctor Portal Routes */}
          <Route
            path="/doctor"
            element={
              <ProtectedRoute allowedRole="DOCTOR">
                <PortalLayout role="DOCTOR">
                  <DoctorDashboard />
                </PortalLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/doctor/schedule"
            element={
              <ProtectedRoute allowedRole="DOCTOR">
                <PortalLayout role="DOCTOR">
                  <DoctorSchedulePage />
                </PortalLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/doctor/waiting-list"
            element={
              <ProtectedRoute allowedRole="DOCTOR">
                <PortalLayout role="DOCTOR">
                  <DoctorWaitingListPage />
                </PortalLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/doctor/analytics"
            element={
              <ProtectedRoute allowedRole="DOCTOR">
                <PortalLayout role="DOCTOR">
                  <DoctorAnalyticsPage />
                </PortalLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/doctor/profile"
            element={
              <ProtectedRoute allowedRole="DOCTOR">
                <PortalLayout role="DOCTOR">
                  <DoctorProfilePage />
                </PortalLayout>
              </ProtectedRoute>
            }
          />

          {/* Admin Portal Routes */}
          <Route
            path="/admin"
            element={
              <ProtectedRoute allowedRole="ADMIN">
                <PortalLayout role="ADMIN">
                  <AdminDashboard />
                </PortalLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/doctors"
            element={
              <ProtectedRoute allowedRole="ADMIN">
                <PortalLayout role="ADMIN">
                  <DoctorManagementPage />
                </PortalLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/patients"
            element={
              <ProtectedRoute allowedRole="ADMIN">
                <PortalLayout role="ADMIN">
                  <PatientManagementPage />
                </PortalLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/appointments"
            element={
              <ProtectedRoute allowedRole="ADMIN">
                <PortalLayout role="ADMIN">
                  <AppointmentManagementPage />
                </PortalLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/analytics"
            element={
              <ProtectedRoute allowedRole="ADMIN">
                <PortalLayout role="ADMIN">
                  <HospitalAnalyticsPage />
                </PortalLayout>
              </ProtectedRoute>
            }
          />

          {/* Catch-all */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
