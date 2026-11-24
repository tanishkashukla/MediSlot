import React, { useState, useEffect } from 'react';
import api from '../../api/client';
import { BarChart3, Clock, TrendingUp, Users, Stethoscope, Activity, ShieldCheck, ArrowRight } from 'lucide-react';
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  Legend
} from 'recharts';
import { Link } from 'react-router-dom';

export default function HospitalAnalyticsPage() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      const res = await api.get('/admin/stats');
      setStats(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="py-24 text-center text-slate-500 text-sm">
        <div className="inline-block w-8 h-8 border-4 border-teal-500 border-t-transparent rounded-full animate-spin mb-3"></div>
        <p>Generating hospital analytics...</p>
      </div>
    );
  }

  if (!stats) return null;

  // Format specialization data for Recharts
  const specData = Object.entries(stats.appointmentsBySpecialization || {}).map(([name, count]) => ({
    name: name.length > 12 ? name.slice(0, 10) + '..' : name,
    fullName: name,
    appointments: count,
  }));

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-6">
      
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-slate-200 gap-4">
        <div>
          <h1 className="text-2xl font-black text-slate-900">Hospital Analytics</h1>
          <p className="text-xs text-slate-500">
            Real data from {stats.totalAppointments.toLocaleString()} historical and active appointment records
          </p>
        </div>
      </div>

      {/* Hospital Key Performance Indicators */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-5 rounded-2xl bg-white border border-slate-200 shadow-sm space-y-1">
          <span className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Total Appointments</span>
          <p className="text-2xl font-black text-slate-900">{stats.totalAppointments.toLocaleString()}</p>
          <p className="text-[11px] text-slate-400">Total hospital records</p>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-slate-200 shadow-sm space-y-1">
          <span className="text-[11px] font-bold text-teal-600 uppercase tracking-wider">Median Wait Time</span>
          <p className="text-2xl font-black text-teal-700">{stats.medianWaitingTimeMinutes} <span className="text-sm font-normal text-slate-500">mins</span></p>
          <p className="text-[11px] text-slate-400">50% of patients seen within this time</p>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-slate-200 shadow-sm space-y-1">
          <span className="text-[11px] font-bold text-amber-600 uppercase tracking-wider">90th Percentile Wait</span>
          <p className="text-2xl font-black text-amber-700">{stats.p90WaitingTimeMinutes} <span className="text-sm font-normal text-slate-500">mins</span></p>
          <p className="text-[11px] text-slate-400">Acceptable clinical delay upper bound</p>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-slate-200 shadow-sm space-y-1">
          <span className="text-[11px] font-bold text-rose-600 uppercase tracking-wider">95th Percentile Wait</span>
          <p className="text-2xl font-black text-rose-700">{stats.p95WaitingTimeMinutes} <span className="text-sm font-normal text-slate-500">mins</span></p>
          <p className="text-[11px] text-slate-400">Peak wait time boundary</p>
        </div>
      </div>

      {/* Charts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        
        {/* Appointments by Specialization Bar Chart */}
        <div className="lg:col-span-7 bg-white rounded-3xl border border-slate-200 p-6 shadow-sm space-y-4">
          <h3 className="text-base font-bold text-slate-900">Appointments by Specialization (18 Specialties)</h3>
          <p className="text-xs text-slate-500">Distribution of patient appointments across all hospital departments</p>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={specData}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                <XAxis dataKey="name" tick={{ fontSize: 10 }} />
                <YAxis allowDecimals={false} tick={{ fontSize: 11 }} />
                <Tooltip />
                <Bar dataKey="appointments" fill="#0284c7" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* 14-Day Trends Area Chart */}
        <div className="lg:col-span-5 bg-white rounded-3xl border border-slate-200 p-6 shadow-sm space-y-4">
          <h3 className="text-base font-bold text-slate-900">14-Day Appointment Trends</h3>
          <p className="text-xs text-slate-500">Completed appointments vs total scheduled volume</p>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={stats.appointmentTrends}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                <XAxis dataKey="date" tick={{ fontSize: 10 }} />
                <YAxis allowDecimals={false} tick={{ fontSize: 11 }} />
                <Tooltip />
                <Area type="monotone" dataKey="total" stroke="#0d9488" fill="#ccfbf1" name="Total Scheduled" />
                <Area type="monotone" dataKey="completed" stroke="#0284c7" fill="#e0f2fe" name="Completed" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

      </div>

    </div>
  );
}
