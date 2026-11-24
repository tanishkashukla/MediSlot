import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../../api/client';
import { useAuth } from '../../context/AuthContext';
import { StatusBadge, PriorityBadge } from '../../components/common/Badge';
import {
  Calendar,
  Clock,
  ListOrdered,
  PlusCircle,
  Bell,
  CheckCircle2,
  XCircle,
  AlertTriangle,
  ArrowRight,
  User,
  Activity
} from 'lucide-react';

export default function PatientDashboard() {
  const { user } = useAuth();
  const [appointments, setAppointments] = useState([]);
  const [waitingList, setWaitingList] = useState([]);
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [actionMsg, setActionMsg] = useState('');

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    setLoading(true);
    try {
      const [appRes, waitRes, profRes] = await Promise.all([
        api.get('/patient/appointments'),
        api.get('/patient/waiting-list'),
        api.get('/patient/profile'),
      ]);
      setAppointments(appRes.data);
      setWaitingList(waitRes.data);
      setProfile(profRes.data);
    } catch (err) {
      console.error('Failed to load patient dashboard:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleAcceptOffer = async (entryId) => {
    try {
      await api.post(`/patient/waiting-list/offer/${entryId}/accept`);
      setActionMsg('Offer Accepted! Your appointment is now Confirmed.');
      loadDashboard();
    } catch (err) {
      setActionMsg('Failed to accept offer: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleDeclineOffer = async (entryId) => {
    try {
      await api.post(`/patient/waiting-list/offer/${entryId}/decline`);
      setActionMsg('Offer declined. The slot has been released to the next patient.');
      loadDashboard();
    } catch (err) {
      setActionMsg('Error: ' + (err.response?.data?.message || err.message));
    }
  };

  const isPast = (appointmentDate, endTime) => {
    const todayStr = new Date().toISOString().split('T')[0];
    if (appointmentDate < todayStr) return true;
    if (appointmentDate === todayStr && endTime) {
      const nowStr = new Date().toTimeString().slice(0, 5);
      return endTime < nowStr;
    }
    return false;
  };

  const upcomingAppointments = appointments
    .filter((a) => !isPast(a.appointmentDate, a.endTime))
    .filter((a) => a.status === 'CONFIRMED' || a.status === 'CHECKED_IN' || a.status === 'IN_CONSULTATION');

  const activeOffers = waitingList.filter((w) => w.status === 'OFFERED');

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-6">
      
      {/* Welcome Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-slate-200 gap-4">
        <div>
          <span className="text-xs font-bold uppercase tracking-wider text-teal-600 bg-teal-50 px-2.5 py-1 rounded-md border border-teal-200">
            Patient Portal
          </span>
          <h1 className="text-2xl font-black text-slate-900 mt-1">
            Hello, {user?.fullName || 'Patient'}
          </h1>
          <p className="text-xs text-slate-500">
            Hospital ID: #{user?.userId} · Blood Group: {profile?.bloodGroup || 'Not Specified'}
          </p>
        </div>

        <Link
          to="/patient/book"
          className="inline-flex items-center space-x-2 px-5 py-2.5 rounded-xl font-bold text-white bg-gradient-to-r from-medical-600 to-teal-600 hover:from-medical-700 hover:to-teal-700 shadow-sm transition text-xs"
        >
          <PlusCircle className="w-4 h-4" />
          <span>Book New Appointment</span>
        </Link>
      </div>

      {actionMsg && (
        <div className="p-3 rounded-xl bg-teal-50 border border-teal-200 text-teal-900 text-xs font-semibold flex items-center justify-between">
          <span>{actionMsg}</span>
          <button onClick={() => setActionMsg('')} className="text-teal-700 font-bold ml-2">Dismiss</button>
        </div>
      )}

      {/* ACTIVE 15-MINUTE WAITING LIST OFFER BANNER (Requirement #6) */}
      {activeOffers.length > 0 && (
        <div className="p-5 rounded-2xl bg-gradient-to-r from-amber-500 via-orange-500 to-amber-600 text-white shadow-lg space-y-3 animate-pulse">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-2 font-bold text-sm">
              <Bell className="w-5 h-5 text-white" />
              <span>APPOINTMENT SLOT OFFER AVAILABLE (15-Minute Priority Window)</span>
            </div>
            <span className="text-xs px-2.5 py-0.5 rounded-full bg-white/20 font-mono font-bold">
              Priority Waitlist Allocation
            </span>
          </div>

          {activeOffers.map((offer) => (
            <div key={offer.id} className="p-4 rounded-xl bg-white/10 backdrop-blur-md flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
              <div>
                <p className="font-bold text-base">
                  {offer.doctorName?.startsWith('Dr.') ? offer.doctorName : (offer.doctorName ? `Dr. ${offer.doctorName}` : 'Faculty Specialist')} · {offer.specializationDisplayName}
                </p>
                <p className="text-xs text-amber-100 mt-0.5">
                  Slot Date: <span className="font-bold">{offer.offeredDate}</span> at{' '}
                  <span className="font-bold">{offer.offeredStartTime} - {offer.offeredEndTime}</span>
                </p>
                <p className="text-[11px] text-amber-200 mt-1">
                  Seconds Remaining on this offer:{' '}
                  <span className="font-bold font-mono text-white text-xs">{offer.secondsRemaining}s</span>
                </p>
              </div>

              <div className="flex items-center space-x-2">
                <button
                  onClick={() => handleAcceptOffer(offer.id)}
                  className="px-4 py-2 rounded-xl bg-white text-amber-900 font-bold text-xs hover:bg-amber-50 shadow-sm transition flex items-center space-x-1"
                >
                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
                  <span>Accept Slot</span>
                </button>
                <button
                  onClick={() => handleDeclineOffer(offer.id)}
                  className="px-4 py-2 rounded-xl bg-black/20 text-white font-medium text-xs hover:bg-black/30 transition flex items-center space-x-1"
                >
                  <XCircle className="w-3.5 h-3.5" />
                  <span>Decline</span>
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">
        <div className="p-5 rounded-2xl bg-white border border-slate-200 shadow-sm flex items-center space-x-4">
          <div className="w-12 h-12 rounded-xl bg-medical-50 text-medical-600 flex items-center justify-center">
            <Calendar className="w-6 h-6" />
          </div>
          <div>
            <span className="text-xs text-slate-500 font-medium">Upcoming Appointments</span>
            <p className="text-2xl font-black text-slate-900">{upcomingAppointments.length}</p>
          </div>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-slate-200 shadow-sm flex items-center space-x-4">
          <div className="w-12 h-12 rounded-xl bg-teal-50 text-teal-600 flex items-center justify-center">
            <ListOrdered className="w-6 h-6" />
          </div>
          <div>
            <span className="text-xs text-slate-500 font-medium">Active Waiting Lists</span>
            <p className="text-2xl font-black text-slate-900">
              {waitingList.filter((w) => w.status === 'WAITING').length}
            </p>
          </div>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-slate-200 shadow-sm flex items-center space-x-4">
          <div className="w-12 h-12 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center">
            <Activity className="w-6 h-6" />
          </div>
          <div>
            <span className="text-xs text-slate-500 font-medium">Completed Consultations</span>
            <p className="text-2xl font-black text-slate-900">
              {appointments.filter((a) => a.status === 'COMPLETED').length}
            </p>
          </div>
        </div>
      </div>

      {/* Next Upcoming Appointment Highlight */}
      <div className="bg-white rounded-3xl border border-slate-200 p-6 shadow-sm">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-base font-bold text-slate-900 flex items-center space-x-2">
            <Clock className="w-5 h-5 text-teal-600" />
            <span>Next Scheduled Visit</span>
          </h2>
          <Link to="/patient/appointments" className="text-xs font-semibold text-teal-600 hover:underline">
            View All ({appointments.length})
          </Link>
        </div>

        {upcomingAppointments.length === 0 ? (
          <div className="text-center py-8 text-slate-500 text-xs">
            <p>You have no upcoming appointments booked.</p>
            <Link
              to="/patient/book"
              className="mt-3 inline-block font-bold text-teal-600 hover:underline"
            >
              Book an appointment now →
            </Link>
          </div>
        ) : (
          <div className="p-5 rounded-2xl bg-slate-50 border border-slate-200/80 flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div className="space-y-1">
              <div className="flex items-center space-x-2">
                <StatusBadge status={upcomingAppointments[0].status} />
                <PriorityBadge priority={upcomingAppointments[0].priority} />
              </div>
              <h3 className="text-lg font-bold text-slate-900 mt-2">
                {upcomingAppointments[0].doctorName?.startsWith('Dr.') ? upcomingAppointments[0].doctorName : `Dr. ${upcomingAppointments[0].doctorName}`}
              </h3>
              <p className="text-xs text-slate-600 font-medium">
                {upcomingAppointments[0].specializationDisplayName} · {upcomingAppointments[0].roomNumber ? (upcomingAppointments[0].roomNumber.startsWith('Room') ? upcomingAppointments[0].roomNumber : `Room ${upcomingAppointments[0].roomNumber}`) : 'Room TBD'}
              </p>
              {upcomingAppointments[0].reasonForVisit && (
                <p className="text-xs text-slate-500 italic mt-1">
                  Reason: "{upcomingAppointments[0].reasonForVisit}"
                </p>
              )}
            </div>

            <div className="text-left md:text-right space-y-1">
              <span className="text-[11px] font-semibold text-slate-400 uppercase">Scheduled Time</span>
              <p className="text-base font-black text-slate-900">
                {upcomingAppointments[0].appointmentDate}
              </p>
              <p className="text-sm font-bold text-teal-600">
                {upcomingAppointments[0].startTime} - {upcomingAppointments[0].endTime}
              </p>
            </div>
          </div>
        )}
      </div>

    </div>
  );
}
