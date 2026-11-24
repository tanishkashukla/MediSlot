import React, { useState, useEffect } from 'react';
import api from '../../api/client';
import SlotPicker from '../../components/booking/SlotPicker';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import {
  Calendar,
  Clock,
  User,
  Shield,
  Star,
  DollarSign,
  AlertCircle,
  CheckCircle2,
  ListOrdered,
  Search
} from 'lucide-react';

export default function BookAppointmentPage() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [specializations, setSpecializations] = useState([]);
  const [selectedSpecialization, setSelectedSpecialization] = useState('');
  const [doctors, setDoctors] = useState([]);
  const [selectedDoctorId, setSelectedDoctorId] = useState('');
  
  const [date, setDate] = useState(() => {
    const d = new Date();
    d.setDate(d.getDate() + 1);
    return d.toISOString().split('T')[0];
  });
  const [priority, setPriority] = useState('NORMAL');
  const [selectedSlotIndex, setSelectedSlotIndex] = useState(null);
  const [reason, setReason] = useState('');

  const [availability, setAvailability] = useState(null);
  const [loadingSlots, setLoadingSlots] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  // Load specialties and doctors
  useEffect(() => {
    loadSpecialtiesAndDoctors();
  }, []);

  const loadSpecialtiesAndDoctors = async () => {
    try {
      const [specRes, docRes] = await Promise.all([
        api.get('/specializations'),
        api.get('/doctors/directory'),
      ]);
      setSpecializations(specRes.data);
      setDoctors(docRes.data);
      if (docRes.data.length > 0) {
        setSelectedDoctorId(docRes.data[0].id.toString());
      }
    } catch (err) {
      console.error(err);
    }
  };

  // Filter doctors by selected specialty
  const availableDoctors = doctors.filter(
    (d) => !selectedSpecialization || d.specialization === selectedSpecialization
  );

  // Update selected doctor if current selection is not in filtered list
  useEffect(() => {
    if (availableDoctors.length > 0) {
      const exists = availableDoctors.some((d) => d.id.toString() === selectedDoctorId);
      if (!exists) {
        setSelectedDoctorId(availableDoctors[0].id.toString());
      }
    } else {
      setSelectedDoctorId('');
    }
  }, [selectedSpecialization]);

  // Query Segment Tree slots whenever selectedDoctorId or date changes
  useEffect(() => {
    if (selectedDoctorId && date) {
      fetchSlots();
    }
  }, [selectedDoctorId, date]);

  const fetchSlots = async () => {
    setLoadingSlots(true);
    setErrorMsg('');
    setSelectedSlotIndex(null);
    try {
      const res = await api.get('/appointments/slots', {
        params: { doctorId: selectedDoctorId, date },
      });
      setAvailability(res.data);
    } catch (err) {
      setErrorMsg('Failed to fetch slot availability via Segment Tree.');
    } finally {
      setLoadingSlots(false);
    }
  };

  const handleSelectSuggestion = (sug) => {
    if (sug.doctorId) setSelectedDoctorId(sug.doctorId.toString());
    if (sug.date) setDate(sug.date);
    if (sug.slotIndex !== undefined) setSelectedSlotIndex(sug.slotIndex);
  };

  const handleBook = async (e) => {
    e.preventDefault();
    if (!selectedDoctorId) {
      setErrorMsg('Please select a doctor.');
      return;
    }
    if (selectedSlotIndex === null) {
      setErrorMsg('Please select an available 30-minute time slot.');
      return;
    }

    setSubmitting(true);
    setErrorMsg('');
    try {
      await api.post('/patient/book', {
        doctorId: parseInt(selectedDoctorId),
        appointmentDate: date,
        slotIndex: selectedSlotIndex,
        priority: priority,
        reasonForVisit: reason,
      });

      setSuccessMsg('Appointment confirmed! Your slot has been successfully reserved.');
      setTimeout(() => {
        navigate('/patient/appointments');
      }, 1500);
    } catch (err) {
      setErrorMsg(err.response?.data?.message || 'Booking conflict. Double-booking prevented.');
      fetchSlots();
    } finally {
      setSubmitting(false);
    }
  };

  const handleJoinWaitingList = async () => {
    setSubmitting(true);
    setErrorMsg('');
    const doctorObj = doctors.find((d) => d.id.toString() === selectedDoctorId);
    try {
      await api.post('/patient/waiting-list/join', {
        doctorId: doctorObj?.id,
        specialization: doctorObj?.specialization || selectedSpecialization,
        preferredDate: date,
        priorityLevel: priority,
        preferredTimeRange: 'ANY',
      });
      setSuccessMsg('Successfully joined the Priority Waiting List! You will be offered the next cancelled slot.');
      setTimeout(() => {
        navigate('/patient/waiting-list');
      }, 1500);
    } catch (err) {
      setErrorMsg('Failed to join waiting list: ' + (err.response?.data?.message || err.message));
    } finally {
      setSubmitting(false);
    }
  };

  const currentDoctor = doctors.find((d) => d.id.toString() === selectedDoctorId);

  const todayStr = new Date().toISOString().split('T')[0];
  const maxDate = new Date();
  maxDate.setDate(maxDate.getDate() + 30);
  const maxDateStr = maxDate.toISOString().split('T')[0];

  return (
    <div className="p-6 max-w-5xl mx-auto space-y-6">
      
      {/* Title */}
      <div>
        <h1 className="text-2xl font-black text-slate-900">Book Doctor Appointment</h1>
        <p className="text-xs text-slate-500">
          7-Step intelligent scheduling workflow powered by Segment Tree slot queries and race condition prevention.
        </p>
      </div>

      {errorMsg && (
        <div className="p-4 rounded-2xl bg-rose-50 border border-rose-200 text-rose-800 text-xs flex items-center space-x-2">
          <AlertCircle className="w-4 h-4 text-rose-600 flex-shrink-0" />
          <span>{errorMsg}</span>
        </div>
      )}

      {successMsg && (
        <div className="p-4 rounded-2xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs flex items-center space-x-2 font-medium">
          <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0" />
          <span>{successMsg}</span>
        </div>
      )}

      <form onSubmit={handleBook} className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        
        {/* Left Form: Steps 1 to 5 */}
        <div className="lg:col-span-7 bg-white rounded-3xl border border-slate-200 p-6 space-y-5 shadow-sm">
          
          {/* Step 1: Medical Specialization */}
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">
              Step 1: Medical Specialization (18 Specialties)
            </label>
            <select
              value={selectedSpecialization}
              onChange={(e) => setSelectedSpecialization(e.target.value)}
              className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
            >
              <option value="">All Specializations</option>
              {specializations.map((s) => (
                <option key={s.key} value={s.key}>{s.name} - {s.description}</option>
              ))}
            </select>
          </div>

          {/* Step 2: Preferred Doctor */}
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">
              Step 2: Preferred Specialist
            </label>
            <select
              value={selectedDoctorId}
              onChange={(e) => setSelectedDoctorId(e.target.value)}
              className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
              required
            >
              {availableDoctors.map((doc) => (
                <option key={doc.id} value={doc.id}>
                  {doc.fullName} ({doc.specializationDisplayName}) · {doc.yearsOfExperience} yrs exp
                </option>
              ))}
            </select>
          </div>

          {/* Step 3: Appointment Date (Rolling 30-Day Horizon) */}
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">
              Step 3: Appointment Date (30-Day Booking Window)
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

          {/* Step 5: Appointment Priority */}
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">
              Step 4: Scheduling Priority
            </label>
            <div className="grid grid-cols-2 gap-3">
              <label className={`p-3 rounded-xl border text-xs cursor-pointer flex items-center space-x-2.5 transition ${
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

              <label className={`p-3 rounded-xl border text-xs cursor-pointer flex items-center space-x-2.5 transition ${
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

          {/* Reason for Visit */}
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">
              Step 5: Reason for Visit (Optional)
            </label>
            <textarea
              rows={2}
              placeholder="Describe symptoms or reasons for consultation..."
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
            />
          </div>

        </div>

        {/* Right Form: Step 6 & 7 (Segment Tree Slots & Booking Confirmation) */}
        <div className="lg:col-span-5 space-y-5">
          
          {/* Selected Doctor Summary Card */}
          {currentDoctor && (
            <div className="bg-white rounded-3xl border border-slate-200 p-5 shadow-sm space-y-3">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="font-bold text-slate-900 text-sm">{currentDoctor.fullName}</h3>
                  <p className="text-xs text-teal-600 font-semibold">{currentDoctor.specializationDisplayName}</p>
                </div>
              </div>

              <div className="text-xs text-slate-500 space-y-1">
                <p>Qualifications: <span className="text-slate-800 font-medium">{currentDoctor.qualifications}</span></p>
                <p>Location: <span className="text-slate-800 font-medium">{currentDoctor.department} · {currentDoctor.roomNumber}</span></p>
              </div>
            </div>
          )}

          {/* Step 6: Available Time Slots */}
          <div className="bg-white rounded-3xl border border-slate-200 p-5 shadow-sm space-y-3">
            <h3 className="font-bold text-slate-900 text-xs uppercase tracking-wider flex items-center space-x-1.5">
              <Clock className="w-4 h-4 text-teal-600" />
              <span>Step 6: Available Time Slots</span>
            </h3>

            <SlotPicker
              availability={availability}
              selectedSlotIndex={selectedSlotIndex}
              onSelectSlot={setSelectedSlotIndex}
              onSelectSuggestion={handleSelectSuggestion}
              loading={loadingSlots}
            />
          </div>

          {/* Step 7: Confirm Booking Button */}
          <div className="space-y-2">
            <button
              type="submit"
              disabled={submitting || selectedSlotIndex === null}
              className="w-full py-3.5 rounded-2xl font-bold text-white bg-gradient-to-r from-medical-600 via-teal-600 to-teal-500 hover:from-medical-700 hover:to-teal-600 shadow-md shadow-teal-500/20 transition text-xs uppercase tracking-wider disabled:opacity-50"
            >
              {submitting ? 'Confirming Reservation...' : 'Confirm Appointment (Step 7)'}
            </button>

            {availability && availability.availableCount === 0 && (
              <button
                type="button"
                disabled={submitting}
                onClick={handleJoinWaitingList}
                className="w-full py-3 rounded-2xl font-bold text-amber-900 bg-amber-100 hover:bg-amber-200 transition text-xs flex items-center justify-center space-x-2"
              >
                <ListOrdered className="w-4 h-4" />
                <span>Join Priority Waiting List</span>
              </button>
            )}
          </div>

        </div>

      </form>
    </div>
  );
}
