import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../../api/client';
import {
  Users,
  Stethoscope,
  Calendar,
  CheckCircle2,
  XCircle,
  Clock,
  Activity,
  Cpu,
  BarChart3,
  TrendingUp,
  ShieldCheck,
  ListOrdered,
  ArrowRight
} from 'lucide-react';
import { ResponsiveContainer, AreaChart, Area, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    setLoading(true);
    try {
      const res = await api.get('/admin/stats');
      setStats(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-6">
      
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-slate-200 gap-4">
        <div>
          <span className="text-xs font-bold uppercase tracking-wider text-medical-700 bg-medical-50 px-2.5 py-1 rounded-md border border-medical-200">
            Hospital Executive Administration
          </span>
          <h1 className="text-2xl font-black text-slate-900 mt-1">
            Hospital System Overview
          </h1>
          <p className="text-xs text-slate-500">
            Live database analytics, doctor faculty utilization, and patient throughput metrics
          </p>
        </div>
      </div>

      {loading ? (
        <div className="py-20 text-center text-slate-500 text-sm">
          <div className="inline-block w-8 h-8 border-4 border-teal-500 border-t-transparent rounded-full animate-spin mb-3"></div>
          <p>Compiling live hospital statistics...</p>
        </div>
      ) : stats ? (
        <>
          {/* Top KPI Cards Grid */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="p-5 rounded-2xl bg-white border border-slate-200 shadow-sm space-y-1">
              <div className="flex items-center justify-between text-slate-400">
                <span className="text-xs font-bold uppercase">Total Doctors</span>
                <Stethoscope className="w-4 h-4 text-teal-600" />
              </div>
              <p className="text-2xl font-black text-slate-900">{stats.totalDoctors}</p>
              <p className="text-[11px] text-teal-600 font-semibold">{stats.activeDoctors} Active in Practice</p>
            </div>

            <div className="p-5 rounded-2xl bg-white border border-slate-200 shadow-sm space-y-1">
              <div className="flex items-center justify-between text-slate-400">
                <span className="text-xs font-bold uppercase">Total Patients</span>
                <Users className="w-4 h-4 text-medical-600" />
              </div>
              <p className="text-2xl font-black text-slate-900">{stats.totalPatients}</p>
              <p className="text-[11px] text-slate-500 font-medium">Registered Patient Records</p>
            </div>

            <div className="p-5 rounded-2xl bg-white border border-slate-200 shadow-sm space-y-1">
              <div className="flex items-center justify-between text-slate-400">
                <span className="text-xs font-bold uppercase">Total Appointments</span>
                <Calendar className="w-4 h-4 text-purple-600" />
              </div>
              <p className="text-2xl font-black text-slate-900">{stats.totalAppointments.toLocaleString()}</p>
              <p className="text-[11px] text-emerald-600 font-semibold">{stats.completedAppointments.toLocaleString()} Completed</p>
            </div>

            <div className="p-5 rounded-2xl bg-white border border-slate-200 shadow-sm space-y-1">
              <div className="flex items-center justify-between text-slate-400">
                <span className="text-xs font-bold uppercase">Faculty Utilization</span>
                <TrendingUp className="w-4 h-4 text-amber-500" />
              </div>
              <p className="text-2xl font-black text-slate-900">{stats.overallDoctorUtilization}%</p>
              <p className="text-[11px] text-slate-500 font-medium">30-Day Rolling Capacity</p>
            </div>
          </div>

          {/* Patient Waiting Time Metrics */}
          <div className="bg-white rounded-3xl border border-slate-200 p-6 shadow-sm space-y-4">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
              <div>
                <h3 className="text-base font-bold text-slate-900">
                  Patient Waiting Time Analytics
                </h3>
                <p className="text-xs text-slate-500">
                  Compiled across {stats.waitingTimeSampleSize?.toLocaleString()} completed hospital visits
                </p>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 pt-1">
              <div className="p-4 rounded-2xl bg-teal-50/70 border border-teal-100">
                <span className="text-xs text-teal-800 font-bold uppercase tracking-wider">Median Wait Time</span>
                <p className="text-2xl font-black text-teal-900 mt-1">{stats.medianWaitingTimeMinutes} <span className="text-xs font-normal text-slate-600">min</span></p>
                <p className="text-[11px] text-teal-700 mt-0.5">50% of patients seen within this time</p>
              </div>

              <div className="p-4 rounded-2xl bg-amber-50/70 border border-amber-100">
                <span className="text-xs text-amber-800 font-bold uppercase tracking-wider">90th Percentile Wait</span>
                <p className="text-2xl font-black text-amber-900 mt-1">{stats.p90WaitingTimeMinutes} <span className="text-xs font-normal text-slate-600">min</span></p>
                <p className="text-[11px] text-amber-700 mt-0.5">90% of visits seen within this time</p>
              </div>

              <div className="p-4 rounded-2xl bg-rose-50/70 border border-rose-100">
                <span className="text-xs text-rose-800 font-bold uppercase tracking-wider">95th Percentile Wait</span>
                <p className="text-2xl font-black text-rose-900 mt-1">{stats.p95WaitingTimeMinutes} <span className="text-xs font-normal text-slate-600">min</span></p>
                <p className="text-[11px] text-rose-700 mt-0.5">Peak wait time boundary</p>
              </div>
            </div>
          </div>

          {/* 14-Day Trend Chart */}
          {stats.appointmentTrends && stats.appointmentTrends.length > 0 && (
            <div className="bg-white rounded-3xl border border-slate-200 p-6 shadow-sm space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="font-bold text-slate-900 text-sm">14-Day Hospital Appointment Trends</h3>
                  <p className="text-xs text-slate-500">Daily appointment volume and completed visits</p>
                </div>
                <Link to="/admin/analytics" className="text-xs font-bold text-teal-600 hover:underline">
                  Full Analytics →
                </Link>
              </div>

              <div className="h-64">
                <ResponsiveContainer width="100%" height="100%">
                  <AreaChart data={stats.appointmentTrends}>
                    <defs>
                      <linearGradient id="totalGrad" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor="#0d9488" stopOpacity={0.3}/>
                        <stop offset="95%" stopColor="#0d9488" stopOpacity={0}/>
                      </linearGradient>
                    </defs>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                    <XAxis dataKey="date" tick={{ fontSize: 11 }} />
                    <YAxis allowDecimals={false} tick={{ fontSize: 11 }} />
                    <Tooltip />
                    <Area type="monotone" dataKey="total" stroke="#0d9488" strokeWidth={2} fillOpacity={1} fill="url(#totalGrad)" />
                  </AreaChart>
                </ResponsiveContainer>
              </div>
            </div>
          )}

          {/* Quick Management Links Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Link
              to="/admin/doctors"
              className="p-5 rounded-2xl bg-white border border-slate-200 hover:border-teal-400 hover:shadow-md transition space-y-2 group"
            >
              <div className="flex items-center justify-between">
                <Stethoscope className="w-5 h-5 text-teal-600" />
                <ArrowRight className="w-4 h-4 text-slate-300 group-hover:translate-x-1 transition-transform" />
              </div>
              <h4 className="font-bold text-slate-900 text-sm">Doctor Management</h4>
              <p className="text-xs text-slate-500">Add, edit, activate or deactivate doctor faculty across 18 specialties.</p>
            </Link>

            <Link
              to="/admin/appointments"
              className="p-5 rounded-2xl bg-white border border-slate-200 hover:border-medical-400 hover:shadow-md transition space-y-2 group"
            >
              <div className="flex items-center justify-between">
                <Calendar className="w-5 h-5 text-medical-600" />
                <ArrowRight className="w-4 h-4 text-slate-300 group-hover:translate-x-1 transition-transform" />
              </div>
              <h4 className="font-bold text-slate-900 text-sm">Appointment Records</h4>
              <p className="text-xs text-slate-500">Filter, inspect, reassign or cancel appointments in real time.</p>
            </Link>
          </div>

        </>
      ) : null}

    </div>
  );
}
