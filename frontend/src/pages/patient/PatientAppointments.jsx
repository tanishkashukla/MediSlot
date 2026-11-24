import React, { useState, useEffect } from 'react';
import api from '../../api/client';
import { StatusBadge, PriorityBadge } from '../../components/common/Badge';
import Modal from '../../components/common/Modal';
import SlotPicker from '../../components/booking/SlotPicker';
import { Calendar, Clock, AlertCircle, CheckCircle2, RotateCcw, XCircle, User, MapPin } from 'lucide-react';

const formatDoctorName = (name) => {
  if (!name) return '';
  return name.startsWith('Dr.') ? name : `Dr. ${name}`;
};

const formatRoomNumber = (room) => {
  if (!room) return 'Room TBD';
  return room.startsWith('Room') ? room : `Room ${room}`;
};

export default function PatientAppointments() {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL'); // ALL, UPCOMING, COMPLETED, CANCELLED

  // Reschedule state
  const [rescheduleAppointment, setRescheduleAppointment] = useState(null);
  const [newDate, setNewDate] = useState(() => {
    const d = new Date();
    d.setDate(d.getDate() + 2);
    return d.toISOString().split('T')[0];
  });
  const [newSlotIndex, setNewSlotIndex] = useState(null);
  const [rescheduleAvailability, setRescheduleAvailability] = useState(null);
  const [loadingRescheduleSlots, setLoadingRescheduleSlots] = useState(false);

  // Cancellation state
  const [cancelTargetId, setCancelTargetId] = useState(null);

  const [feedbackMsg, setFeedbackMsg] = useState({ type: '', text: '' });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    loadAppointments();
  }, []);

  const loadAppointments = async () => {
    setLoading(true);
    try {
      const res = await api.get('/patient/appointments');
      setAppointments(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async () => {
    if (!cancelTargetId) return;
    setSubmitting(true);
    try {
      await api.post(`/patient/cancel/${cancelTargetId}`);
      setFeedbackMsg({ type: 'success', text: 'Appointment cancelled successfully. Slot freed and offered to waiting list.' });
      setCancelTargetId(null);
      loadAppointments();
    } catch (err) {
      setFeedbackMsg({ type: 'error', text: err.response?.data?.message || 'Failed to cancel appointment.' });
    } finally {
      setSubmitting(false);
    }
  };

  const openRescheduleModal = (app) => {
    setRescheduleAppointment(app);
    setNewSlotIndex(null);
    loadRescheduleSlots(app.doctorId, newDate);
  };

  const loadRescheduleSlots = async (doctorId, date) => {
    setLoadingRescheduleSlots(true);
    try {
      const res = await api.get('/appointments/slots', {
        params: { doctorId, date },
      });
      setRescheduleAvailability(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoadingRescheduleSlots(false);
    }
  };

  const handleRescheduleSubmit = async (e) => {
    e.preventDefault();
    if (newSlotIndex === null) {
      setFeedbackMsg({ type: 'error', text: 'Please select an available slot.' });
      return;
    }
    setSubmitting(true);
    try {
      await api.post(`/patient/reschedule/${rescheduleAppointment.id}`, {
        newAppointmentDate: newDate,
        newSlotIndex: newSlotIndex,
      });
      setFeedbackMsg({ type: 'success', text: 'Appointment successfully rescheduled.' });
      setRescheduleAppointment(null);
      loadAppointments();
    } catch (err) {
      setFeedbackMsg({ type: 'error', text: err.response?.data?.message || 'Failed to reschedule appointment.' });
    } finally {
      setSubmitting(false);
    }
  };

  const filteredAppointments = appointments.filter((a) => {
    if (filter === 'UPCOMING') return a.status === 'CONFIRMED' || a.status === 'CHECKED_IN';
    if (filter === 'COMPLETED') return a.status === 'COMPLETED';
    if (filter === 'CANCELLED') return a.status === 'CANCELLED';
    return true;
  });

  const todayStr = new Date().toISOString().split('T')[0];
  const maxDate = new Date();
  maxDate.setDate(maxDate.getDate() + 30);
  const maxDateStr = maxDate.toISOString().split('T')[0];

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-6">
      
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-slate-200 gap-4">
        <div>
          <h1 className="text-2xl font-black text-slate-900">My Appointments</h1>
          <p className="text-xs text-slate-500">Track current visits, manage bookings, cancel or reschedule</p>
        </div>

        {/* Filter Pills */}
        <div className="flex items-center space-x-2 bg-slate-100 p-1 rounded-xl text-xs font-semibold">
          {['ALL', 'UPCOMING', 'COMPLETED', 'CANCELLED'].map((f) => (
            <button
              key={f}
              onClick={() => setFilter(f)}
              className={`px-3 py-1.5 rounded-lg transition ${
                filter === f ? 'bg-white text-slate-900 shadow-sm font-bold' : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              {f}
            </button>
          ))}
        </div>
      </div>

      {feedbackMsg.text && (
        <div className={`p-4 rounded-2xl text-xs font-semibold flex items-center justify-between border ${
          feedbackMsg.type === 'success'
            ? 'bg-emerald-50 text-emerald-900 border-emerald-200'
            : 'bg-rose-50 text-rose-900 border-rose-200'
        }`}>
          <span>{feedbackMsg.text}</span>
          <button onClick={() => setFeedbackMsg({ type: '', text: '' })} className="font-bold underline ml-2">
            Dismiss
          </button>
        </div>
      )}

      {/* Appointment Cards */}
      {loading ? (
        <div className="py-16 text-center text-slate-500 text-sm">
          <div className="inline-block w-8 h-8 border-4 border-teal-500 border-t-transparent rounded-full animate-spin mb-3"></div>
          <p>Loading appointments...</p>
        </div>
      ) : filteredAppointments.length === 0 ? (
        <div className="py-16 text-center bg-white rounded-3xl border border-slate-200 text-slate-500 text-sm">
          <p>No appointments found in this category.</p>
        </div>
      ) : (
        <div className="space-y-4">
          {filteredAppointments.map((app) => (
            <div
              key={app.id}
              className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm hover:shadow-md transition flex flex-col md:flex-row md:items-center justify-between gap-4"
            >
              <div className="space-y-2">
                <div className="flex items-center space-x-2">
                  <StatusBadge status={app.status} />
                  <PriorityBadge priority={app.priority} />
                  <span className="text-[11px] text-slate-400 font-mono">Appt #{app.id}</span>
                </div>

                <div>
                  <h3 className="text-base font-bold text-slate-900">{formatDoctorName(app.doctorName)}</h3>
                  <p className="text-xs text-teal-600 font-semibold">{app.specializationDisplayName}</p>
                </div>

                <div className="flex flex-wrap items-center gap-4 text-xs text-slate-600">
                  <div className="flex items-center space-x-1.5">
                    <Calendar className="w-3.5 h-3.5 text-slate-400" />
                    <span>{app.appointmentDate}</span>
                  </div>
                  <div className="flex items-center space-x-1.5 font-bold text-slate-800">
                    <Clock className="w-3.5 h-3.5 text-teal-600" />
                    <span>{app.startTime} - {app.endTime}</span>
                  </div>
                  <div className="flex items-center space-x-1.5">
                    <MapPin className="w-3.5 h-3.5 text-slate-400" />
                    <span>{formatRoomNumber(app.roomNumber)}</span>
                  </div>
                </div>

                {app.reasonForVisit && (
                  <p className="text-xs text-slate-500 italic">
                    Reason: "{app.reasonForVisit}"
                  </p>
                )}
                {app.consultationNotes && (
                  <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-200 text-xs text-slate-700">
                    <span className="font-bold text-slate-900">Doctor's Notes:</span> {app.consultationNotes}
                  </div>
                )}
              </div>

              {/* Action Buttons for Confirmed Appointments */}
              {app.status === 'CONFIRMED' && (
                <div className="flex items-center space-x-2 border-t md:border-t-0 pt-3 md:pt-0">
                  <button
                    onClick={() => openRescheduleModal(app)}
                    className="px-3.5 py-2 rounded-xl text-xs font-semibold text-slate-700 bg-slate-100 hover:bg-slate-200 transition flex items-center space-x-1.5"
                  >
                    <RotateCcw className="w-3.5 h-3.5" />
                    <span>Reschedule</span>
                  </button>
                  <button
                    onClick={() => setCancelTargetId(app.id)}
                    className="px-3.5 py-2 rounded-xl text-xs font-semibold text-rose-700 bg-rose-50 hover:bg-rose-100 transition flex items-center space-x-1.5"
                  >
                    <XCircle className="w-3.5 h-3.5" />
                    <span>Cancel</span>
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Cancel Confirmation Modal */}
      <Modal
        isOpen={cancelTargetId !== null}
        onClose={() => setCancelTargetId(null)}
        title="Confirm Appointment Cancellation"
      >
        <div className="space-y-4">
          <p className="text-xs text-slate-600">
            Are you sure you want to cancel this appointment?
            The reserved slot will be restored immediately and automatically offered to the highest-priority eligible patient waiting in queue.
          </p>
          <div className="flex justify-end space-x-3 pt-2">
            <button
              onClick={() => setCancelTargetId(null)}
              className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-600 hover:bg-slate-100"
            >
              Keep Appointment
            </button>
            <button
              onClick={handleCancel}
              disabled={submitting}
              className="px-4 py-2 rounded-xl text-xs font-bold text-white bg-rose-600 hover:bg-rose-700 transition"
            >
              {submitting ? 'Cancelling...' : 'Yes, Cancel Appointment'}
            </button>
          </div>
        </div>
      </Modal>

      {/* Reschedule Modal */}
      <Modal
        isOpen={rescheduleAppointment !== null}
        onClose={() => setRescheduleAppointment(null)}
        title="Reschedule Appointment"
        maxWidth="max-w-lg"
      >
        {rescheduleAppointment && (
          <form onSubmit={handleRescheduleSubmit} className="space-y-4">
            <div className="p-3 rounded-xl bg-slate-50 border border-slate-200 text-xs">
              <p className="font-bold text-slate-900">{formatDoctorName(rescheduleAppointment.doctorName)}</p>
              <p className="text-slate-500">Current Slot: {rescheduleAppointment.appointmentDate} at {rescheduleAppointment.startTime}</p>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">Select New Date</label>
              <input
                type="date"
                min={todayStr}
                max={maxDateStr}
                value={newDate}
                onChange={(e) => {
                  setNewDate(e.target.value);
                  loadRescheduleSlots(rescheduleAppointment.doctorId, e.target.value);
                }}
                className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
                required
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">Select New Time Slot</label>
              <SlotPicker
                availability={rescheduleAvailability}
                selectedSlotIndex={newSlotIndex}
                onSelectSlot={setNewSlotIndex}
                loading={loadingRescheduleSlots}
              />
            </div>

            <div className="flex justify-end space-x-3 pt-2 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setRescheduleAppointment(null)}
                className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-600 hover:bg-slate-100"
              >
                Close
              </button>
              <button
                type="submit"
                disabled={submitting || newSlotIndex === null}
                className="px-5 py-2 rounded-xl text-xs font-bold text-white bg-teal-600 hover:bg-teal-700 transition disabled:opacity-50"
              >
                {submitting ? 'Rescheduling...' : 'Confirm Reschedule'}
              </button>
            </div>
          </form>
        )}
      </Modal>

    </div>
  );
}
