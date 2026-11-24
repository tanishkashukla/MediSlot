import React, { useState, useEffect } from 'react';
import api from '../../api/client';
import { StatusBadge, PriorityBadge } from '../../components/common/Badge';
import Modal from '../../components/common/Modal';
import {
  Calendar,
  Clock,
  UserCheck,
  CheckCircle2,
  PlayCircle,
  XCircle,
  FileText,
  AlertCircle,
  Stethoscope,
  Activity,
  Users
} from 'lucide-react';

export default function DoctorDashboard() {
  const [appointments, setAppointments] = useState([]);
  const [stats, setStats] = useState(null);
  const [selectedDate, setSelectedDate] = useState(() => new Date().toISOString().split('T')[0]);
  const [loading, setLoading] = useState(true);

  // Complete consultation modal state
  const [activeConsultation, setActiveConsultation] = useState(null);
  const [consultationNotes, setConsultationNotes] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [feedbackMsg, setFeedbackMsg] = useState({ type: '', text: '' });

  useEffect(() => {
    loadDailySchedule();
    loadDoctorStats();
  }, [selectedDate]);

  const loadDailySchedule = async () => {
    setLoading(true);
    try {
      const res = await api.get('/doctor/appointments', {
        params: { date: selectedDate },
      });
      setAppointments(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const loadDoctorStats = async () => {
    try {
      const res = await api.get('/doctor/stats');
      setStats(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleCheckIn = async (id) => {
    try {
      await api.post(`/doctor/appointment/${id}/checkin`);
      setFeedbackMsg({ type: 'success', text: 'Patient checked in successfully.' });
      loadDailySchedule();
      loadDoctorStats();
    } catch (err) {
      setFeedbackMsg({ type: 'error', text: err.response?.data?.message || 'Check-in failed.' });
    }
  };

  const handleStartConsultation = async (id) => {
    try {
      await api.post(`/doctor/appointment/${id}/start`);
      setFeedbackMsg({ type: 'success', text: 'Consultation started. Actual patient waiting time recorded.' });
      loadDailySchedule();
    } catch (err) {
      setFeedbackMsg({ type: 'error', text: err.response?.data?.message || 'Could not start consultation.' });
    }
  };

  const handleOpenCompleteModal = (app) => {
    setActiveConsultation(app);
    setConsultationNotes(app.consultationNotes || '');
  };

  const handleCompleteSubmit = async (e) => {
    e.preventDefault();
    if (!activeConsultation) return;
    setSubmitting(true);
    try {
      await api.post(`/doctor/appointment/${activeConsultation.id}/complete`, {
        notes: consultationNotes,
      });
      setFeedbackMsg({ type: 'success', text: 'Consultation marked completed. Clinical notes recorded.' });
      setActiveConsultation(null);
      loadDailySchedule();
      loadDoctorStats();
    } catch (err) {
      setFeedbackMsg({ type: 'error', text: err.response?.data?.message || 'Failed to complete consultation.' });
    } finally {
      setSubmitting(false);
    }
  };

  const handleMarkNoShow = async (id) => {
    try {
      await api.post(`/doctor/appointment/${id}/noshow`);
      setFeedbackMsg({ type: 'success', text: 'Appointment marked as No-Show.' });
      loadDailySchedule();
    } catch (err) {
      setFeedbackMsg({ type: 'error', text: 'Error marking no-show.' });
    }
  };

  const handleCancelByDoctor = async (id) => {
    try {
      await api.post(`/doctor/appointment/${id}/cancel`);
      setFeedbackMsg({ type: 'success', text: 'Appointment cancelled. Slot restored via Segment Tree & offered to waiting list.' });
      loadDailySchedule();
    } catch (err) {
      setFeedbackMsg({ type: 'error', text: 'Error cancelling appointment.' });
    }
  };

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-6">
      
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-slate-200 gap-4">
        <div>
          <span className="text-xs font-bold uppercase tracking-wider text-teal-600 bg-teal-50 px-2.5 py-1 rounded-md border border-teal-200">
            Doctor Clinical Portal
          </span>
          <h1 className="text-2xl font-black text-slate-900 mt-1">
            Daily Clinic Schedule & Patient Queue
          </h1>
          <p className="text-xs text-slate-500">
            Check in patients, start consultations with recorded waiting times, and manage clinical progress
          </p>
        </div>

        {/* Date Selector */}
        <div className="flex items-center space-x-2">
          <Calendar className="w-4 h-4 text-slate-400" />
          <input
            type="date"
            value={selectedDate}
            onChange={(e) => setSelectedDate(e.target.value)}
            className="px-3 py-1.5 text-xs font-bold bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500"
          />
        </div>
      </div>

      {feedbackMsg.text && (
        <div className={`p-4 rounded-2xl text-xs font-semibold flex items-center justify-between border ${
          feedbackMsg.type === 'success'
            ? 'bg-emerald-50 text-emerald-900 border-emerald-200'
            : 'bg-rose-50 text-rose-900 border-rose-200'
        }`}>
          <span>{feedbackMsg.text}</span>
          <button onClick={() => setFeedbackMsg({ type: '', text: '' })} className="font-bold underline ml-2">Dismiss</button>
        </div>
      )}

      {/* KPI Stats */}
      {stats && (
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
          <div className="p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
            <span className="text-[11px] text-slate-400 font-semibold uppercase">Today's Total Patients</span>
            <p className="text-2xl font-black text-slate-900">{stats.todayAppointmentsCount}</p>
          </div>
          <div className="p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
            <span className="text-[11px] text-slate-400 font-semibold uppercase">Checked-In / In Waiting Area</span>
            <p className="text-2xl font-black text-teal-600">{stats.todayCheckedInCount}</p>
          </div>
          <div className="p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
            <span className="text-[11px] text-slate-400 font-semibold uppercase">Completed Patient Visits</span>
            <p className="text-2xl font-black text-medical-600">{stats.totalCompletedConsultations}</p>
          </div>
          <div className="p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
            <span className="text-[11px] text-slate-400 font-semibold uppercase">Clinical Room</span>
            <p className="text-2xl font-black text-slate-900">{stats.roomNumber ? (stats.roomNumber.startsWith('Room') ? stats.roomNumber : `Room ${stats.roomNumber}`) : 'Room 401'}</p>
          </div>
        </div>
      )}

      {/* Daily Patient List Table */}
      <div className="bg-white rounded-3xl border border-slate-200 overflow-hidden shadow-sm">
        <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
          <h2 className="text-base font-bold text-slate-900">
            Schedule for {selectedDate} ({appointments.length} Patients)
          </h2>
          <span className="text-xs text-slate-500 font-medium">30-Min Precision Slots</span>
        </div>

        {loading ? (
          <div className="py-16 text-center text-slate-500 text-sm">
            <div className="inline-block w-8 h-8 border-4 border-teal-500 border-t-transparent rounded-full animate-spin mb-3"></div>
            <p>Loading schedule...</p>
          </div>
        ) : appointments.length === 0 ? (
          <div className="py-16 text-center text-slate-500 text-sm">
            No patients scheduled for this date.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-500 font-bold uppercase tracking-wider border-b border-slate-200">
                <tr>
                  <th className="px-5 py-3">Slot Time</th>
                  <th className="px-5 py-3">Patient Name</th>
                  <th className="px-5 py-3">Priority</th>
                  <th className="px-5 py-3">Status</th>
                  <th className="px-5 py-3">Wait Time</th>
                  <th className="px-5 py-3">Reason</th>
                  <th className="px-5 py-3 text-right">Clinical Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-slate-700">
                {appointments.map((app) => (
                  <tr key={app.id} className="hover:bg-slate-50/80 transition">
                    <td className="px-5 py-3.5 font-bold text-slate-900 whitespace-nowrap">
                      {app.startTime} - {app.endTime}
                    </td>
                    <td className="px-5 py-3.5 whitespace-nowrap">
                      <p className="font-bold text-slate-900">{app.patientName}</p>
                      <p className="text-[11px] text-slate-400">{app.patientPhone || app.patientEmail}</p>
                    </td>
                    <td className="px-5 py-3.5 whitespace-nowrap">
                      <PriorityBadge priority={app.priority} />
                    </td>
                    <td className="px-5 py-3.5 whitespace-nowrap">
                      <StatusBadge status={app.status} />
                    </td>
                    <td className="px-5 py-3.5 whitespace-nowrap font-mono text-slate-600">
                      {app.waitingTimeMinutes > 0 ? `${Math.round(app.waitingTimeMinutes)}m` : '—'}
                    </td>
                    <td className="px-5 py-3.5 max-w-xs truncate text-slate-500">
                      {app.reasonForVisit || 'General review'}
                    </td>
                    <td className="px-5 py-3.5 text-right whitespace-nowrap space-x-1.5">
                      {app.status === 'CONFIRMED' && (
                        <button
                          onClick={() => handleCheckIn(app.id)}
                          className="px-2.5 py-1 rounded-lg bg-teal-50 text-teal-700 hover:bg-teal-100 font-bold transition"
                        >
                          Check In
                        </button>
                      )}
                      {(app.status === 'CONFIRMED' || app.status === 'CHECKED_IN') && (
                        <button
                          onClick={() => handleStartConsultation(app.id)}
                          className="px-2.5 py-1 rounded-lg bg-blue-50 text-blue-700 hover:bg-blue-100 font-bold transition"
                        >
                          Start Visit
                        </button>
                      )}
                      {app.status === 'IN_CONSULTATION' && (
                        <button
                          onClick={() => handleOpenCompleteModal(app)}
                          className="px-2.5 py-1 rounded-lg bg-emerald-600 text-white hover:bg-emerald-700 font-bold shadow-sm transition"
                        >
                          Complete Visit
                        </button>
                      )}
                      {(app.status === 'CONFIRMED' || app.status === 'CHECKED_IN') && (
                        <button
                          onClick={() => handleMarkNoShow(app.id)}
                          className="px-2 py-1 rounded-lg text-slate-400 hover:text-amber-700 hover:bg-amber-50 font-medium transition"
                          title="Mark No-Show"
                        >
                          No-Show
                        </button>
                      )}
                      {app.status === 'CONFIRMED' && (
                        <button
                          onClick={() => handleCancelByDoctor(app.id)}
                          className="px-2 py-1 rounded-lg text-slate-400 hover:text-rose-700 hover:bg-rose-50 font-medium transition"
                          title="Cancel Visit"
                        >
                          Cancel
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Complete Consultation Modal */}
      <Modal
        isOpen={activeConsultation !== null}
        onClose={() => setActiveConsultation(null)}
        title="Complete Patient Visit"
        maxWidth="max-w-lg"
      >
        {activeConsultation && (
          <form onSubmit={handleCompleteSubmit} className="space-y-4">
            <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200 text-xs">
              <p className="font-bold text-slate-900">Patient: {activeConsultation.patientName}</p>
              <p className="text-slate-500">Scheduled: {activeConsultation.startTime} - {activeConsultation.endTime}</p>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">
                Clinical Diagnosis & Treatment Notes
              </label>
              <textarea
                rows={4}
                required
                placeholder="Prescription, diagnostic summary, follow-up instructions..."
                value={consultationNotes}
                onChange={(e) => setConsultationNotes(e.target.value)}
                className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
              />
            </div>

            <div className="flex justify-end space-x-3 pt-2 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setActiveConsultation(null)}
                className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-600 hover:bg-slate-100"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={submitting}
                className="px-5 py-2 rounded-xl text-xs font-bold text-white bg-emerald-600 hover:bg-emerald-700 transition"
              >
                {submitting ? 'Saving...' : 'Finalize & Complete'}
              </button>
            </div>
          </form>
        )}
      </Modal>

    </div>
  );
}
