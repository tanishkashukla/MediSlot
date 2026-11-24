import React, { useState, useEffect } from 'react';
import api from '../../api/client';
import { StatusBadge, PriorityBadge } from '../../components/common/Badge';
import { ListOrdered, Clock, CheckCircle2, XCircle, AlertCircle, Bell, ArrowRight } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function WaitingListPage() {
  const [waitingEntries, setWaitingEntries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionMsg, setActionMsg] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    loadWaitingList();
    // 5-second polling interval for live queue updates and timer
    const interval = setInterval(loadWaitingList, 5000);
    return () => clearInterval(interval);
  }, []);

  const loadWaitingList = async () => {
    try {
      const res = await api.get('/patient/waiting-list');
      setWaitingEntries(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleAccept = async (id) => {
    setSubmitting(true);
    try {
      await api.post(`/patient/waiting-list/offer/${id}/accept`);
      setActionMsg('Offer Accepted! Appointment is now confirmed.');
      loadWaitingList();
    } catch (err) {
      setActionMsg('Failed to accept: ' + (err.response?.data?.message || err.message));
    } finally {
      setSubmitting(false);
    }
  };

  const handleDecline = async (id) => {
    setSubmitting(true);
    try {
      await api.post(`/patient/waiting-list/offer/${id}/decline`);
      setActionMsg('Offer declined. The slot has been re-allocated to the next patient in the Min Heap.');
      loadWaitingList();
    } catch (err) {
      setActionMsg('Error: ' + (err.response?.data?.message || err.message));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-6">
      
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-slate-200 gap-4">
        <div>
          <h1 className="text-2xl font-black text-slate-900">Waiting List & Priority Offers</h1>
          <p className="text-xs text-slate-500">
            Priority patients receive higher administrative precedence on appointment cancellations.
          </p>
        </div>

        <Link
          to="/patient/book"
          className="inline-flex items-center space-x-1.5 px-4 py-2 rounded-xl text-xs font-bold text-white bg-teal-600 hover:bg-teal-700 shadow-sm transition"
        >
          <span>Find Available Slots</span>
          <ArrowRight className="w-3.5 h-3.5" />
        </Link>
      </div>

      {actionMsg && (
        <div className="p-3.5 rounded-2xl bg-teal-50 border border-teal-200 text-teal-900 text-xs font-semibold flex items-center justify-between">
          <span>{actionMsg}</span>
          <button onClick={() => setActionMsg('')} className="underline font-bold ml-2">Dismiss</button>
        </div>
      )}

      {loading ? (
        <div className="py-16 text-center text-slate-500 text-sm">
          <div className="inline-block w-8 h-8 border-4 border-teal-500 border-t-transparent rounded-full animate-spin mb-3"></div>
          <p>Loading waiting list requests...</p>
        </div>
      ) : waitingEntries.length === 0 ? (
        <div className="py-16 text-center bg-white rounded-3xl border border-slate-200 text-slate-500 text-sm space-y-2">
          <p>You have no active waiting list requests.</p>
          <p className="text-xs text-slate-400">
            When all slots for a doctor are booked, you can join the waiting list from the booking interface.
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {waitingEntries.map((entry) => {
            const isOffered = entry.status === 'OFFERED';

            return (
              <div
                key={entry.id}
                className={`p-6 rounded-3xl border transition-all ${
                  isOffered
                    ? 'bg-gradient-to-r from-amber-50 to-orange-50 border-amber-300 shadow-md ring-2 ring-amber-400/20'
                    : 'bg-white border-slate-200 shadow-sm'
                }`}
              >
                <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                  <div className="space-y-2">
                    <div className="flex items-center space-x-2">
                      <StatusBadge status={entry.status} />
                      <PriorityBadge priority={entry.priorityLevel} />
                      <span className="text-[11px] font-mono text-slate-400">Request #{entry.id}</span>
                    </div>

                    <h3 className="text-base font-bold text-slate-900">
                      {entry.doctorName ? (entry.doctorName.startsWith('Dr.') ? entry.doctorName : `Dr. ${entry.doctorName}`) : 'Any Available Specialist'}
                    </h3>
                    <p className="text-xs text-teal-600 font-semibold">{entry.specializationDisplayName}</p>

                    <div className="flex flex-wrap items-center gap-4 text-xs text-slate-600">
                      <div className="flex items-center space-x-1.5">
                        <span className="font-semibold text-slate-400 uppercase text-[10px]">Preferred Date:</span>
                        <span className="font-medium text-slate-800">{entry.preferredDate}</span>
                      </div>
                      <div className="flex items-center space-x-1.5">
                        <span className="font-semibold text-slate-400 uppercase text-[10px]">Time Window:</span>
                        <span className="font-medium text-slate-800">{entry.preferredTimeRange}</span>
                      </div>
                      {entry.status === 'WAITING' && (
                        <div className="flex items-center space-x-1 px-2.5 py-0.5 rounded-full bg-slate-100 text-slate-800 font-bold text-xs">
                          <span>Live Queue Position:</span>
                          <span className="text-teal-600 font-black">#{entry.queuePosition}</span>
                        </div>
                      )}
                    </div>

                    {/* Offer Details if Slot is Ready */}
                    {isOffered && (
                      <div className="mt-3 p-3.5 rounded-2xl bg-white border border-amber-200 text-xs text-amber-900 space-y-1">
                        <p className="font-bold flex items-center space-x-1.5 text-amber-800">
                          <Bell className="w-4 h-4 text-amber-600 animate-bounce" />
                          <span>Slot Available for Booking! (15-Minute Countdown Window)</span>
                        </p>
                        <p>
                          Date: <span className="font-bold">{entry.offeredDate}</span> at{' '}
                          <span className="font-bold">{entry.offeredStartTime} - {entry.offeredEndTime}</span>
                        </p>
                        <p className="text-[11px] text-amber-700">
                          Time remaining to accept: <span className="font-bold font-mono text-amber-950">{entry.secondsRemaining} seconds</span>
                        </p>
                      </div>
                    )}
                  </div>

                  {/* Actions for OFFERED state */}
                  {isOffered && (
                    <div className="flex flex-col sm:flex-row items-center gap-2 border-t md:border-t-0 pt-3 md:pt-0">
                      <button
                        onClick={() => handleAccept(entry.id)}
                        disabled={submitting}
                        className="w-full sm:w-auto px-5 py-2.5 rounded-xl font-bold text-xs text-white bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-700 hover:to-teal-700 shadow-md transition flex items-center justify-center space-x-1.5"
                      >
                        <CheckCircle2 className="w-4 h-4" />
                        <span>Accept Slot Now</span>
                      </button>
                      <button
                        onClick={() => handleDecline(entry.id)}
                        disabled={submitting}
                        className="w-full sm:w-auto px-4 py-2.5 rounded-xl font-semibold text-xs text-slate-600 bg-white border border-slate-200 hover:bg-slate-100 transition flex items-center justify-center space-x-1.5"
                      >
                        <XCircle className="w-4 h-4" />
                        <span>Decline Offer</span>
                      </button>
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

    </div>
  );
}
