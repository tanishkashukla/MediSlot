import React, { useState, useEffect } from 'react';
import api from '../../api/client';
import { Calendar, Clock, Lock, Unlock, AlertCircle, CheckCircle2, Shield } from 'lucide-react';

export default function DoctorSchedulePage() {
  const [date, setDate] = useState(() => new Date().toISOString().split('T')[0]);
  const [availability, setAvailability] = useState(null);
  const [loading, setLoading] = useState(true);
  const [feedback, setFeedback] = useState({ type: '', text: '' });

  // Hours config
  const [startTime, setStartTime] = useState('09:00');
  const [endTime, setEndTime] = useState('17:00');
  const [duration, setDuration] = useState(30);
  const [savingHours, setSavingHours] = useState(false);

  useEffect(() => {
    loadSchedule();
  }, [date]);

  const loadSchedule = async () => {
    setLoading(true);
    try {
      const res = await api.get('/doctor/schedule', { params: { date } });
      setAvailability(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleToggleBlock = async (slotIndex, currentlyBlocked) => {
    try {
      await api.post(`/doctor/schedule/slot/${slotIndex}/toggle-block`, null, {
        params: {
          date: date,
          block: !currentlyBlocked,
        },
      });
      setFeedback({
        type: 'success',
        text: `Slot #${slotIndex} ${currentlyBlocked ? 'unblocked' : 'blocked'} successfully! Schedule availability updated in real-time.`,
      });
      loadSchedule();
    } catch (err) {
      setFeedback({ type: 'error', text: 'Failed to update slot status.' });
    }
  };

  const handleSaveHours = async (e) => {
    e.preventDefault();
    setSavingHours(true);
    try {
      await api.put('/doctor/schedule', {
        scheduleDate: date,
        startTime: startTime,
        endTime: endTime,
        slotDurationMinutes: parseInt(duration),
      });
      setFeedback({ type: 'success', text: 'Working hours and slot capacity updated successfully.' });
      loadSchedule();
    } catch (err) {
      setFeedback({ type: 'error', text: 'Failed to update hours.' });
    } finally {
      setSavingHours(false);
    }
  };

  const todayStr = new Date().toISOString().split('T')[0];

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-6">
      
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-slate-200 gap-4">
        <div>
          <h1 className="text-2xl font-black text-slate-900">Calendar & Schedule Management</h1>
        </div>

        <div className="flex items-center space-x-2">
          <Calendar className="w-4 h-4 text-slate-400" />
          <input
            type="date"
            min={todayStr}
            value={date}
            onChange={(e) => setDate(e.target.value)}
            className="px-3 py-1.5 text-xs font-bold bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500"
          />
        </div>
      </div>

      {feedback.text && (
        <div className={`p-4 rounded-2xl text-xs font-semibold flex items-center justify-between border ${
          feedback.type === 'success'
            ? 'bg-emerald-50 text-emerald-900 border-emerald-200'
            : 'bg-rose-50 text-rose-900 border-rose-200'
        }`}>
          <span>{feedback.text}</span>
          <button onClick={() => setFeedback({ type: '', text: '' })} className="font-bold underline ml-2">Dismiss</button>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        
        {/* Slot Grid & Management */}
        <div className="lg:col-span-8 bg-white rounded-3xl border border-slate-200 p-6 shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-base font-bold text-slate-900">
                Time Slots for {date}
              </h2>
              <p className="text-xs text-slate-500">Click a slot to toggle Blocked / Unblocked status</p>
            </div>
            {availability && (
              <span className="text-xs font-bold px-3 py-1 rounded-full bg-teal-50 text-teal-800 border border-teal-200">
                {availability.availableCount} Available / {availability.totalSlots} Total
              </span>
            )}
          </div>

          {loading ? (
            <div className="py-12 text-center text-slate-500 text-sm">
              <div className="inline-block w-8 h-8 border-4 border-teal-500 border-t-transparent rounded-full animate-spin mb-3"></div>
              <p>Loading available slots...</p>
            </div>
          ) : !availability ? (
            <p className="text-xs text-slate-500">No schedule found for this date.</p>
          ) : (
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
              {availability.slots.map((slot) => {
                const isBlocked = slot.reason === 'BLOCKED';
                const isBooked = slot.reason === 'BOOKED';
                const isPast = slot.reason === 'PAST';

                return (
                  <div
                    key={slot.slotIndex}
                    className={`p-3.5 rounded-2xl border flex flex-col justify-between transition ${
                      isBooked
                        ? 'bg-slate-100 border-slate-200 text-slate-600'
                        : isBlocked
                        ? 'bg-rose-50 border-rose-200 text-rose-900'
                        : isPast
                        ? 'bg-slate-50 border-slate-200 text-slate-400 opacity-60'
                        : 'bg-emerald-50/60 border-emerald-200 text-emerald-900'
                    }`}
                  >
                    <div>
                      <div className="flex items-center justify-between mb-1">
                        <span className="text-[10px] font-bold font-mono">#{slot.slotIndex}</span>
                        <span className={`text-[10px] font-black uppercase px-1.5 py-0.2 rounded-md ${
                          isBooked ? 'bg-slate-200 text-slate-700' :
                          isBlocked ? 'bg-rose-200 text-rose-800' :
                          isPast ? 'bg-slate-200 text-slate-500' :
                          'bg-emerald-200 text-emerald-800'
                        }`}>
                          {slot.reason}
                        </span>
                      </div>
                      <p className="text-xs font-black">{slot.startTime} - {slot.endTime}</p>
                    </div>

                    {!isBooked && !isPast && (
                      <button
                        onClick={() => handleToggleBlock(slot.slotIndex, isBlocked)}
                        className={`mt-3 py-1 px-2 rounded-lg text-[11px] font-bold flex items-center justify-center space-x-1 transition ${
                          isBlocked
                            ? 'bg-emerald-600 hover:bg-emerald-700 text-white'
                            : 'bg-rose-600 hover:bg-rose-700 text-white'
                        }`}
                      >
                        {isBlocked ? <Unlock className="w-3 h-3" /> : <Lock className="w-3 h-3" />}
                        <span>{isBlocked ? 'Unblock Slot' : 'Block Slot'}</span>
                      </button>
                    )}

                    {isBooked && (
                      <span className="mt-3 text-[10px] text-slate-500 text-center font-medium">
                        Patient Booked
                      </span>
                    )}
                  </div>
                );
              })}
            </div>
          )}
        </div>

        {/* Working Hours Settings */}
        <div className="lg:col-span-4 bg-white rounded-3xl border border-slate-200 p-6 shadow-sm space-y-4">
          <h2 className="text-base font-bold text-slate-900 flex items-center space-x-2">
            <Clock className="w-4 h-4 text-teal-600" />
            <span>Configure Working Hours</span>
          </h2>
          <p className="text-xs text-slate-500">
            Set default clinic shift hours and slot length.
          </p>

          <form onSubmit={handleSaveHours} className="space-y-4 pt-2">
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">Shift Start Time</label>
              <input
                type="time"
                value={startTime}
                onChange={(e) => setStartTime(e.target.value)}
                className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">Shift End Time</label>
              <input
                type="time"
                value={endTime}
                onChange={(e) => setEndTime(e.target.value)}
                className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">Slot Duration (Minutes)</label>
              <select
                value={duration}
                onChange={(e) => setDuration(e.target.value)}
                className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
              >
                <option value="15">15 Minutes</option>
                <option value="30">30 Minutes (Standard)</option>
                <option value="45">45 Minutes</option>
                <option value="60">60 Minutes</option>
              </select>
            </div>

            <button
              type="submit"
              disabled={savingHours}
              className="w-full py-2.5 rounded-xl font-bold text-xs text-white bg-teal-600 hover:bg-teal-700 transition disabled:opacity-50"
            >
              {savingHours ? 'Saving Changes...' : 'Save Working Hours'}
            </button>
          </form>
        </div>

      </div>
    </div>
  );
}
