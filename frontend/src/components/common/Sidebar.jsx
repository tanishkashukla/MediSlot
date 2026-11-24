import React, { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
  LayoutDashboard,
  Calendar,
  Clock,
  User,
  Users,
  UserCheck,
  BarChart3,
  Cpu,
  LogOut,
  ChevronLeft,
  ChevronRight,
  Shield,
  Stethoscope,
  ListOrdered,
  PlusCircle,
  FileText
} from 'lucide-react';

export default function Sidebar({ role }) {
  const [collapsed, setCollapsed] = useState(false);
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const getNavLinks = () => {
    if (role === 'PATIENT') {
      return [
        { to: '/patient', label: 'Dashboard', icon: LayoutDashboard, end: true },
        { to: '/patient/book', label: 'Book Appointment', icon: PlusCircle },
        { to: '/patient/appointments', label: 'My Appointments', icon: Calendar },
        { to: '/patient/waiting-list', label: 'Waiting List & Offers', icon: ListOrdered },
        { to: '/patient/profile', label: 'Medical Profile', icon: User },
      ];
    }
    if (role === 'DOCTOR') {
      return [
        { to: '/doctor', label: 'Today\'s Schedule', icon: LayoutDashboard, end: true },
        { to: '/doctor/schedule', label: 'Slot & Hours Manager', icon: Clock },
        { to: '/doctor/waiting-list', label: 'Patient Queue / Waiting', icon: ListOrdered },
        { to: '/doctor/analytics', label: 'My Utilization', icon: BarChart3 },
        { to: '/doctor/profile', label: 'Doctor Profile', icon: Stethoscope },
      ];
    }
    if (role === 'ADMIN') {
      return [
        { to: '/admin', label: 'Hospital Overview', icon: LayoutDashboard, end: true },
        { to: '/admin/doctors', label: 'Doctor Management', icon: Stethoscope },
        { to: '/admin/patients', label: 'Patient Directory', icon: Users },
        { to: '/admin/appointments', label: 'All Appointments', icon: Calendar },
        { to: '/admin/analytics', label: 'Hospital Analytics', icon: BarChart3 },
      ];
    }
    return [];
  };

  const navLinks = getNavLinks();

  return (
    <aside
      className={`h-[calc(100vh-4rem)] sticky top-16 bg-white border-r border-slate-200 transition-all duration-300 flex flex-col justify-between z-30 ${
        collapsed ? 'w-16' : 'w-64'
      }`}
    >
      {/* Upper Navigation */}
      <div className="p-3">
        {/* Collapse Toggle */}
        <div className="flex items-center justify-between mb-4 px-2">
          {!collapsed && (
            <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">
              {role} PORTAL
            </span>
          )}
          <button
            onClick={() => setCollapsed(!collapsed)}
            className="p-1.5 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition ml-auto"
            title={collapsed ? 'Expand Sidebar' : 'Collapse Sidebar'}
          >
            {collapsed ? <ChevronRight className="w-4 h-4" /> : <ChevronLeft className="w-4 h-4" />}
          </button>
        </div>

        {/* Links */}
        <nav className="space-y-1">
          {navLinks.map((link) => {
            const Icon = link.icon;
            return (
              <NavLink
                key={link.to}
                to={link.to}
                end={link.end}
                className={({ isActive }) =>
                  `flex items-center space-x-3 px-3 py-2.5 rounded-xl font-medium text-sm transition group ${
                    isActive
                      ? link.highlight
                        ? 'bg-teal-500 text-white shadow-sm shadow-teal-500/20'
                        : 'bg-medical-50 text-medical-700 font-semibold'
                      : link.highlight
                      ? 'text-teal-700 bg-teal-50 hover:bg-teal-100 font-semibold'
                      : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                  }`
                }
                title={collapsed ? link.label : undefined}
              >
                <Icon className={`w-5 h-5 flex-shrink-0 ${link.highlight && !collapsed ? 'animate-pulse' : ''}`} />
                {!collapsed && (
                  <span className="truncate flex-1">
                    {link.label}
                  </span>
                )}
              </NavLink>
            );
          })}
        </nav>
      </div>

      {/* Footer Info & Logout */}
      <div className="p-3 border-t border-slate-100">
        {!collapsed && user && (
          <div className="mb-2 p-2.5 rounded-xl bg-slate-50 border border-slate-100">
            <p className="text-xs font-semibold text-slate-900 truncate">{user.fullName}</p>
            <p className="text-[11px] text-slate-500 truncate">{user.email}</p>
          </div>
        )}
        <button
          onClick={handleLogout}
          className={`w-full flex items-center space-x-3 px-3 py-2.5 rounded-xl text-sm font-medium text-slate-600 hover:text-rose-600 hover:bg-rose-50 transition ${
            collapsed ? 'justify-center' : ''
          }`}
          title="Sign Out"
        >
          <LogOut className="w-5 h-5 flex-shrink-0" />
          {!collapsed && <span>Sign Out</span>}
        </button>
      </div>
    </aside>
  );
}
