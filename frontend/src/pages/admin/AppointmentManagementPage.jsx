import React, { useState, useEffect } from 'react';
import api from '../../api/client';
import { StatusBadge, PriorityBadge } from '../../components/common/Badge';
import { Calendar, Search, Filter, Clock, MapPin, User, Stethoscope } from 'lucide-react';

export default function AppointmentManagementPage() {
  const [appointments, setAppointments] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [priorityFilter, setPriorityFilter] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadAppointments();
  }, []);

  const loadAppointments = async () => {
    setLoading(true);
    try {
      const res = await api.get('/admin/appointments');
      setAppointments(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const filtered = appointments.filter((a) => {
    const matchesStatus = !statusFilter || a.status === statusFilter;
    const matchesPriority = !priorityFilter || a.priority === priorityFilter;
    const q = searchTerm.toLowerCase();
    const matchesQuery = !searchTerm ||
      a.patientName?.toLowerCase().includes(q) ||
      a.doctorName?.toLowerCase().includes(q) ||
      a.specializationDisplayName?.toLowerCase().includes(q) ||
      a.appointmentDate?.includes(q);

    return matchesStatus && matchesPriority && matchesQuery;
  });

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-6">
      
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-slate-200 gap-4">
        <div>
          <h1 className="text-2xl font-black text-slate-900">Hospital Appointment Registry</h1>
          <p className="text-xs text-slate-500">
            Total records: {appointments.length.toLocaleString()} · Real-time status and wait-time audit
          </p>
        </div>

        {/* Filter bar */}
        <div className="flex flex-wrap items-center gap-2">
          <div className="relative min-w-[200px]">
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-2.5" />
            <input
              type="text"
              placeholder="Search patient or doctor..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-9 pr-3 py-1.5 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
            />
          </div>

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-3 py-1.5 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
          >
            <option value="">All Statuses</option>
            <option value="CONFIRMED">Confirmed</option>
            <option value="CHECKED_IN">Checked In</option>
            <option value="IN_CONSULTATION">In Consultation</option>
            <option value="COMPLETED">Completed</option>
            <option value="CANCELLED">Cancelled</option>
            <option value="NO_SHOW">No Show</option>
          </select>

          <select
            value={priorityFilter}
            onChange={(e) => setPriorityFilter(e.target.value)}
            className="px-3 py-1.5 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
          >
            <option value="">All Priorities</option>
            <option value="URGENT">Urgent Priority</option>
            <option value="NORMAL">Normal Priority</option>
          </select>
        </div>
      </div>

      {loading ? (
        <div className="py-16 text-center text-slate-500 text-sm">
          <div className="inline-block w-8 h-8 border-4 border-teal-500 border-t-transparent rounded-full animate-spin mb-3"></div>
          <p>Loading appointments...</p>
        </div>
      ) : (
        <div className="bg-white rounded-3xl border border-slate-200 overflow-hidden shadow-sm">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-500 font-bold uppercase tracking-wider border-b border-slate-200">
                <tr>
                  <th className="px-5 py-3">Appt #</th>
                  <th className="px-5 py-3">Patient</th>
                  <th className="px-5 py-3">Doctor & Specialty</th>
                  <th className="px-5 py-3">Date & Slot</th>
                  <th className="px-5 py-3">Priority</th>
                  <th className="px-5 py-3">Status</th>
                  <th className="px-5 py-3 text-right">Wait Time</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-slate-700">
                {filtered.slice(0, 50).map((app) => (
                  <tr key={app.id} className="hover:bg-slate-50/80 transition">
                    <td className="px-5 py-3.5 font-mono text-slate-400">
                      #{app.id}
                    </td>
                    <td className="px-5 py-3.5 whitespace-nowrap font-bold text-slate-900">
                      {app.patientName}
                    </td>
                    <td className="px-5 py-3.5 whitespace-nowrap">
                      <p className="font-semibold text-slate-900">{app.doctorName?.startsWith('Dr.') ? app.doctorName : `Dr. ${app.doctorName}`}</p>
                      <p className="text-[11px] text-teal-600 font-medium">{app.specializationDisplayName}</p>
                    </td>
                    <td className="px-5 py-3.5 whitespace-nowrap">
                      <p className="font-bold text-slate-900">{app.appointmentDate}</p>
                      <p className="text-[11px] text-slate-500">{app.startTime} - {app.endTime}</p>
                    </td>
                    <td className="px-5 py-3.5 whitespace-nowrap">
                      <PriorityBadge priority={app.priority} />
                    </td>
                    <td className="px-5 py-3.5 whitespace-nowrap">
                      <StatusBadge status={app.status} />
                    </td>
                    <td className="px-5 py-3.5 text-right whitespace-nowrap font-mono font-bold text-slate-700">
                      {app.waitingTimeMinutes > 0 ? `${Math.round(app.waitingTimeMinutes)} min` : '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {filtered.length > 50 && (
            <div className="p-4 bg-slate-50 text-center text-xs text-slate-500 border-t border-slate-100">
              Showing first 50 of {filtered.length} appointments. Filter to narrow results.
            </div>
          )}
        </div>
      )}

    </div>
  );
}
