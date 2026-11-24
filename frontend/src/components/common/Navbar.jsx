import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Activity, Calendar, User, LogOut, LayoutDashboard, Shield, Stethoscope } from 'lucide-react';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const getDashboardPath = () => {
    if (!user) return '/login';
    if (user.role === 'ADMIN') return '/admin';
    if (user.role === 'DOCTOR') return '/doctor';
    return '/patient';
  };

  return (
    <nav className="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-slate-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16 items-center">
          {/* Brand Logo */}
          <Link to="/" className="flex items-center space-x-3 group">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-medical-600 to-teal-500 flex items-center justify-center text-white shadow-md shadow-teal-500/20 group-hover:scale-105 transition-transform">
              <Activity className="w-6 h-6" />
            </div>
            <div>
              <span className="text-xl font-bold bg-gradient-to-r from-medical-700 to-teal-600 bg-clip-text text-transparent">
                MediSlot
              </span>
              <span className="hidden sm:inline-block ml-2 text-xs font-semibold px-2 py-0.5 rounded-full bg-teal-50 text-teal-700 border border-teal-200">
                Smart Hospital
              </span>
            </div>
          </Link>

          {/* Navigation Links */}
          <div className="hidden md:flex items-center space-x-8 text-sm font-medium text-slate-600">
            <Link to="/" className="hover:text-medical-600 transition-colors">Home</Link>
            <a href="/#specializations" className="hover:text-medical-600 transition-colors">Specialties</a>
            <a href="/#doctors" className="hover:text-medical-600 transition-colors">Find Doctors</a>
            <a href="/#how-it-works" className="hover:text-medical-600 transition-colors">How It Works</a>
          </div>

          {/* User Auth Actions */}
          <div className="flex items-center space-x-3">
            {user ? (
              <div className="flex items-center space-x-3">
                <Link
                  to={getDashboardPath()}
                  className="inline-flex items-center space-x-2 px-3.5 py-2 rounded-lg bg-medical-50 text-medical-700 hover:bg-medical-100 text-sm font-semibold transition"
                >
                  {user.role === 'ADMIN' && <Shield className="w-4 h-4 text-medical-600" />}
                  {user.role === 'DOCTOR' && <Stethoscope className="w-4 h-4 text-teal-600" />}
                  {user.role === 'PATIENT' && <LayoutDashboard className="w-4 h-4 text-medical-600" />}
                  <span>Dashboard</span>
                </Link>

                <div className="hidden lg:block text-right">
                  <p className="text-xs font-medium text-slate-900 leading-none">{user.fullName}</p>
                  <p className="text-[10px] text-slate-500 mt-0.5 font-semibold uppercase tracking-wider">{user.role}</p>
                </div>

                <button
                  onClick={handleLogout}
                  title="Logout"
                  className="p-2 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
                >
                  <LogOut className="w-4 h-4" />
                </button>
              </div>
            ) : (
              <div className="flex items-center space-x-2">
                <Link
                  to="/login"
                  className="px-4 py-2 text-sm font-medium text-slate-700 hover:text-medical-600 hover:bg-slate-50 rounded-lg transition"
                >
                  Sign In
                </Link>
                <Link
                  to="/register"
                  className="px-4 py-2 text-sm font-semibold text-white bg-gradient-to-r from-medical-600 to-teal-600 hover:from-medical-700 hover:to-teal-700 rounded-lg shadow-sm hover:shadow-md transition"
                >
                  Register
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
