import React, { useState, useEffect } from 'react';
import Modal from '../common/Modal';
import SlotPicker from './SlotPicker';
import api from '../../api/client';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { Calendar, AlertCircle, CheckCircle2, UserCheck, Shield, Clock } from 'lucide-react';

export default function BookingModal({ isOpen, onClose, doctor, onBookingSuccess }) {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [date, setDate] = useState(() => {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    return tomorrow.toISOString().split('T')[0];
  });
  const [priority, setPriority] = useState('NORMAL');
  const [reason, setReason] = useState('');
  const [availability, setAvailability] = useState(null);
  const [selectedSlotIndex, setSelectedSlotIndex] = useState(null);
  const [loadingSlots, setLoadingSlots] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  // Fetch slots whenever doctor or date changes
  useEffect(() => {
    if (isOpen && doctor && date) {
      fetchSlots();
    }
  }, [isOpen, doctor, date]);

  const fetchSlots = async () => {
    setLoadingSlots(true);
    setErrorMsg('');
    setSelectedSlotIndex(null);
    try {
      const res = await api.get('/appointments/slots', {
        params: { doctorId: doctor.id, date },
      });
      setAvailability(res.data);
    } catch (err) {
      setErrorMsg('Failed to load doctor slots.');
    } finally {
      setLoadingSlots(false);
    }
  };

  const handleSelectSuggestion = (sug) => {
    if (sug.date) setDate(sug.date);
    if (sug.slotIndex !== undefined) setSelectedSlotIndex(sug.slotIndex);
  };

  const handleBook = async (e) => {
    e.preventDefault();
    if (!user) {
      navigate('/login');
      return;
    }
    if (selectedSlotIndex === null) {
      setErrorMsg('Please select an available time slot.');
      return;
    }

    setSubmitting(true);
    setErrorMsg('');
    try {
      const payload = {
        doctorId: doctor.id,
        appointmentDate: date,
        slotIndex: selectedSlotIndex,
        priority: priority,
        reasonForVisit: reason,
      };
      const res = await api.post('/patient/book', payload);
      setSuccessMsg('Appointment booked successfully! Your slot has been reserved.');
      if (onBookingSuccess) onBookingSuccess(res.data);
      setTimeout(() => {
        onClose();
        setSuccessMsg('');
      }, 1500);
    } catch (err) {
      const msg = err.response?.data?.message || 'Booking conflict or error. Double-booking prevented.';
      setErrorMsg(msg);
      // Refresh slots
      fetchSlots();
    } finally {
      setSubmitting(false);
    }
  };

  const handleJoinWaitlist = async () => {
    if (!user) {
      navigate('/login');
      return;
    }
    setSubmitting(true);
    setErrorMsg('');
    try {
      const payload = {
        doctorId: doctor.id,
        specialization: doctor.specialization,
        preferredDate: date,
        priorityLevel: priority,
        preferredTimeRange: 'ANY',
      };
      await api.post('/patient/waiting-list/join', payload);
      setSuccessMsg('Successfully joined the Priority Waiting List! You will be offered the next cancelled slot.');
      setTimeout(() => {
        onClose();
        setSuccessMsg('');
      }, 1800);
    } catch (err) {
      setErrorMsg('Failed to join waiting list: ' + (err.response?.data?.message || err.message));
    } finally {
      setSubmitting(false);
    }
  };

  if (!doctor) return null;

  // Max booking date: 30 days from now
  const maxDate = new Date();
  maxDate.setDate(maxDate.getDate() + 30);
  const maxDateStr = maxDate.toISOString().split('T')[0];
  const todayStr = new Date().toISOString().split('T')[0];

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`Book Appointment with ${doctor.fullName}`} maxWidth="max-w-xl">
      <form onSubmit={handleBook} className="space-y-4">
        
        {/* Doctor Summary Header */}
        <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200 flex justify-between items-center text-xs">
          <div>
            <p className="font-bold text-slate-900">{doctor.fullName}</p>
            <p className="text-slate-500">{doctor.specializationDisplayName} · {doctor.department}</p>
          </div>
        </div>

        {errorMsg && (
          <div className="p-3 rounded-xl bg-rose-50 border border-rose-200 text-rose-800 text-xs flex items-center space-x-2">
            <AlertCircle className="w-4 h-4 flex-shrink-0 text-rose-600" />
            <span>{errorMsg}</span>
          </div>
        )}

        {successMsg && (
          <div className="p-3 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs flex items-center space-x-2 font-medium">
            <CheckCircle2 className="w-4 h-4 flex-shrink-0 text-emerald-600" />
            <span>{successMsg}</span>
          </div>
        )}

        {/* Step 3: Appointment Date Selection (Rolling 30-day horizon) */}
        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1">
            Appointment Date (Rolling 30-Day Horizon)
          </label>
          <input
            type="date"
            min={todayStr}
            max={maxDateStr}
            value={date}
            onChange={(e) => setDate(e.target.value)}
            className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
            required
          />
        </div>

        {/* Step 5: Priority Selection */}
        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1">
            Appointment Priority
          </label>
          <div className="grid grid-cols-2 gap-3">
            <label className={`p-2.5 rounded-xl border text-xs cursor-pointer flex items-center space-x-2.5 transition ${
              priority === 'NORMAL' ? 'border-teal-500 bg-teal-50/60 font-semibold' : 'border-slate-200 hover:bg-slate-50'
            }`}>
              <input
                type="radio"
                name="priority"
                value="NORMAL"
                checked={priority === 'NORMAL'}
                onChange={() => setPriority('NORMAL')}
                className="text-teal-600 focus:ring-teal-500"
              />
              <div>
                <p className="font-bold text-slate-800">Normal Priority</p>
                <p className="text-[10px] text-slate-500">Standard scheduling queue</p>
              </div>
            </label>

            <label className={`p-2.5 rounded-xl border text-xs cursor-pointer flex items-center space-x-2.5 transition ${
              priority === 'URGENT' ? 'border-amber-500 bg-amber-50/60 font-semibold' : 'border-slate-200 hover:bg-slate-50'
            }`}>
              <input
                type="radio"
                name="priority"
                value="URGENT"
                checked={priority === 'URGENT'}
                onChange={() => setPriority('URGENT')}
                className="text-amber-600 focus:ring-amber-500"
              />
              <div>
                <p className="font-bold text-amber-900">Urgent Priority</p>
                <p className="text-[10px] text-amber-700">Higher administrative precedence</p>
              </div>
            </label>
          </div>
        </div>

        {/* Step 6: Available Time Slots */}
        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1">
            Available 30-Minute Time Slot
          </label>
          <SlotPicker
            availability={availability}
            selectedSlotIndex={selectedSlotIndex}
            onSelectSlot={setSelectedSlotIndex}
            onSelectSuggestion={handleSelectSuggestion}
            loading={loadingSlots}
          />
        </div>

        {/* Reason for Visit */}
        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1">
            Reason for Visit (Optional)
          </label>
          <input
            type="text"
            placeholder="e.g. Chest pain, routine checkup, skin review..."
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500"
          />
        </div>

        {/* Action Buttons */}
        <div className="pt-2 flex items-center justify-end space-x-3 border-t border-slate-100">
          {availability && availability.availableCount === 0 && (
            <button
              type="button"
              disabled={submitting}
              onClick={handleJoinWaitlist}
              className="px-4 py-2.5 rounded-xl text-xs font-bold text-amber-800 bg-amber-100 hover:bg-amber-200 transition"
            >
              Join Priority Waiting List
            </button>
          )}

          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2.5 rounded-xl text-xs font-semibold text-slate-600 hover:bg-slate-100 transition"
          >
            Cancel
          </button>

          <button
            type="submit"
            disabled={submitting || selectedSlotIndex === null}
            className="px-5 py-2.5 rounded-xl text-xs font-bold text-white bg-gradient-to-r from-medical-600 to-teal-600 hover:from-medical-700 hover:to-teal-700 shadow-sm transition disabled:opacity-50"
          >
            {submitting ? 'Confirming...' : 'Confirm Appointment'}
          </button>
        </div>

      </form>
    </Modal>
  );
}
