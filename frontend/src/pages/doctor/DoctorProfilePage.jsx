import React, { useState, useEffect } from 'react';
import api from '../../api/client';
import { Stethoscope, Award, DollarSign, MapPin, CheckCircle2 } from 'lucide-react';

export default function DoctorProfilePage() {
  const [profile, setProfile] = useState(null);
  const [bio, setBio] = useState('');
  const [qualifications, setQualifications] = useState('');
  const [consultationFee, setConsultationFee] = useState(100);
  const [department, setDepartment] = useState('');
  const [roomNumber, setRoomNumber] = useState('');
  const [saving, setSaving] = useState(false);
  const [successMsg, setSuccessMsg] = useState('');

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      const res = await api.get('/doctor/profile');
      setProfile(res.data);
      setBio(res.data.bio || '');
      setQualifications(res.data.qualifications || '');
      setConsultationFee(res.data.consultationFee || 100);
      setDepartment(res.data.department || '');
      setRoomNumber(res.data.roomNumber || '');
    } catch (err) {
      console.error(err);
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setSuccessMsg('');
    try {
      await api.put('/doctor/profile', {
        bio,
        qualifications,
        consultationFee: parseFloat(consultationFee),
        department,
        roomNumber,
      });
      setSuccessMsg('Profile updated successfully.');
    } catch (err) {
      console.error(err);
    } finally {
      setSaving(false);
    }
  };

  if (!profile) return null;

  return (
    <div className="p-6 max-w-4xl mx-auto space-y-6">
      
      <div>
        <h1 className="text-2xl font-black text-slate-900">Doctor Professional Profile</h1>
        <p className="text-xs text-slate-500">Configure your public directory credentials, fees, and consultation room</p>
      </div>

      {successMsg && (
        <div className="p-4 rounded-2xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs font-semibold flex items-center space-x-2">
          <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0" />
          <span>{successMsg}</span>
        </div>
      )}

      <div className="bg-white rounded-3xl border border-slate-200 p-8 shadow-sm space-y-6">
        
        {/* Read-Only Top Summary */}
        <div className="p-4 rounded-2xl bg-slate-50 border border-slate-200 grid grid-cols-1 sm:grid-cols-3 gap-4 text-xs">
          <div>
            <span className="text-[10px] uppercase font-bold text-slate-400">Physician Name</span>
            <p className="text-sm font-bold text-slate-900">{profile.fullName}</p>
          </div>
          <div>
            <span className="text-[10px] uppercase font-bold text-slate-400">Specialty</span>
            <p className="text-sm font-bold text-teal-600">{profile.specializationDisplayName}</p>
          </div>
          <div>
            <span className="text-[10px] uppercase font-bold text-slate-400">Clinical Experience</span>
            <p className="text-sm font-bold text-slate-900">{profile.yearsOfExperience} Years</p>
          </div>
        </div>

        <form onSubmit={handleSave} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">Medical Degrees & Certifications</label>
            <input
              type="text"
              value={qualifications}
              onChange={(e) => setQualifications(e.target.value)}
              className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">Consultation Fee ($ USD)</label>
              <input
                type="number"
                step="5"
                min="0"
                value={consultationFee}
                onChange={(e) => setConsultationFee(e.target.value)}
                className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">Clinical Department</label>
              <input
                type="text"
                value={department}
                onChange={(e) => setDepartment(e.target.value)}
                className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">Consultation Room</label>
              <input
                type="text"
                value={roomNumber}
                onChange={(e) => setRoomNumber(e.target.value)}
                className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">Professional Bio & Clinical Focus</label>
            <textarea
              rows={4}
              value={bio}
              onChange={(e) => setBio(e.target.value)}
              className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
            />
          </div>

          <div className="pt-2 flex justify-end">
            <button
              type="submit"
              disabled={saving}
              className="px-6 py-2.5 rounded-xl font-bold text-xs text-white bg-teal-600 hover:bg-teal-700 shadow-sm transition disabled:opacity-50"
            >
              {saving ? 'Saving...' : 'Save Profile'}
            </button>
          </div>
        </form>

      </div>
    </div>
  );
}
