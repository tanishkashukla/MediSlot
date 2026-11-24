import React, { useState, useEffect } from 'react';
import api from '../../api/client';
import { BarChart3, Users, Clock, Award, Activity, CalendarCheck } from 'lucide-react';
import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';

export default function DoctorAnalyticsPage() {
  const [stats, setStats] = useState(null);
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadAnalytics();
  }, []);

  const loadAnalytics = async () => {
    try {
      const [statsRes, appRes] = await Promise.all([
        api.get('/doctor/stats'),
        api.get('/doctor/appointments'),
      ]);
      setStats(statsRes.data);
      setAppointments(appRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // Group appointments by date for small bar chart
  const dateCounts = {};
  appointments.slice(0, 30).forEach((a) => {
    dateCounts[a.appointmentDate] = (dateCounts[a.appointmentDate] || 0) + 1;
  });
  const chartData = Object.keys(dateCounts).map((k) => ({
    date: k.slice(5),
    patients: dateCounts[k],
  }));

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-6">
      
      <div>
        <h1 className="text-2xl font-black text-slate-900">Doctor Utilization Statistics</h1>
        <p className="text-xs text-slate-500">Clinical performance metrics, patient load, and completed consultations</p>
      </div>

      {stats && (
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">
          <div className="p-5 rounded-3xl bg-white border border-slate-200 shadow-sm flex items-center space-x-4">
            <div className="w-12 h-12 rounded-2xl bg-teal-50 text-teal-600 flex items-center justify-center">
              <CalendarCheck className="w-6 h-6" />
            </div>
            <div>
              <span className="text-xs text-slate-500 font-semibold">Total Consultations Completed</span>
              <p className="text-2xl font-black text-slate-900">{stats.totalCompletedConsultations}</p>
            </div>
          </div>

          <div className="p-5 rounded-3xl bg-white border border-slate-200 shadow-sm flex items-center space-x-4">
            <div className="w-12 h-12 rounded-2xl bg-medical-50 text-medical-600 flex items-center justify-center">
              <Award className="w-6 h-6" />
            </div>
            <div>
              <span className="text-xs text-slate-500 font-semibold">Patient Rating</span>
              <p className="text-2xl font-black text-slate-900">{stats.rating} / 5.0</p>
            </div>
          </div>

          <div className="p-5 rounded-3xl bg-white border border-slate-200 shadow-sm flex items-center space-x-4">
            <div className="w-12 h-12 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
              <Activity className="w-6 h-6" />
            </div>
            <div>
              <span className="text-xs text-slate-500 font-semibold">Today's Appointment Load</span>
              <p className="text-2xl font-black text-slate-900">{stats.todayAppointmentsCount} Patients</p>
            </div>
          </div>
        </div>
      )}

      {/* Bar Chart */}
      <div className="bg-white rounded-3xl border border-slate-200 p-6 shadow-sm space-y-4">
        <h3 className="font-bold text-slate-900 text-sm">Recent Consultation Volume by Date</h3>
        <div className="h-64">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
              <XAxis dataKey="date" tick={{ fontSize: 11 }} />
              <YAxis allowDecimals={false} tick={{ fontSize: 11 }} />
              <Tooltip />
              <Bar dataKey="patients" fill="#0d9488" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

    </div>
  );
}
