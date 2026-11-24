import React, { useState, useEffect } from 'react';
import api from '../../api/client';
import Modal from '../../components/common/Modal';
import { Stethoscope, PlusCircle, Search, Edit2, Power, CheckCircle2, AlertCircle } from 'lucide-react';

export default function DoctorManagementPage() {
  const [doctors, setDoctors] = useState([]);
  const [specializations, setSpecializations] = useState([]);
  const [selectedSpec, setSelectedSpec] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);

  // Modal states
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [editDoctor, setEditDoctor] = useState(null);

  // New Doctor Form
  const [newDocData, setNewDocData] = useState({
    fullName: '',
    email: '',
    phone: '',
    password: 'Doctor@123',
    specialization: 'CARDIOLOGY',
    qualifications: 'MD, DM',
    yearsOfExperience: 8,
    consultationFee: 100,
    department: 'Cardiovascular Sciences',
    roomNumber: 'Room 405',
    bio: '',
  });

  const [feedback, setFeedback] = useState({ type: '', text: '' });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    loadDoctorsAndSpecs();
  }, []);

  const loadDoctorsAndSpecs = async () => {
    setLoading(true);
    try {
      const [docRes, specRes] = await Promise.all([
        api.get('/admin/doctors'),
        api.get('/specializations'),
      ]);
      setDoctors(docRes.data);
      setSpecializations(specRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleToggleActive = async (id) => {
    try {
      await api.patch(`/admin/doctors/${id}/toggle-active`);
      setFeedback({ type: 'success', text: 'Doctor account status updated.' });
      loadDoctorsAndSpecs();
    } catch (err) {
      setFeedback({ type: 'error', text: 'Failed to toggle status.' });
    }
  };

  const handleCreateDoctor = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await api.post('/admin/doctors', newDocData);
      setFeedback({ type: 'success', text: 'Doctor created successfully.' });
      setIsAddModalOpen(false);
      loadDoctorsAndSpecs();
    } catch (err) {
      setFeedback({ type: 'error', text: err.response?.data?.message || 'Error creating doctor.' });
    } finally {
      setSubmitting(false);
    }
  };

  const handleEditDoctorSubmit = async (e) => {
    e.preventDefault();
    if (!editDoctor) return;
    setSubmitting(true);
    try {
      await api.put(`/admin/doctors/${editDoctor.id}`, editDoctor);
      setFeedback({ type: 'success', text: 'Doctor profile updated.' });
      setEditDoctor(null);
      loadDoctorsAndSpecs();
    } catch (err) {
      setFeedback({ type: 'error', text: 'Failed to update doctor.' });
    } finally {
      setSubmitting(false);
    }
  };

  const filteredDoctors = doctors.filter((doc) => {
    const matchesSpec = !selectedSpec || doc.specialization === selectedSpec;
    const matchesQuery = !searchTerm ||
      doc.fullName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      doc.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      doc.department?.toLowerCase().includes(searchTerm.toLowerCase());
    return matchesSpec && matchesQuery;
  });

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-6">
      
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-slate-200 gap-4">
        <div>
          <h1 className="text-2xl font-black text-slate-900">Doctor Faculty Management</h1>
          <p className="text-xs text-slate-500">
            Admin oversight: create, edit, and control active status for 36 doctors across 18 specializations.
          </p>
        </div>

        <button
          onClick={() => setIsAddModalOpen(true)}
          className="inline-flex items-center space-x-1.5 px-4 py-2 rounded-xl text-xs font-bold text-white bg-teal-600 hover:bg-teal-700 shadow-sm transition"
        >
          <PlusCircle className="w-4 h-4" />
          <span>Add New Doctor</span>
        </button>
      </div>

      {feedback.text && (
        <div className={`p-4 rounded-2xl text-xs font-semibold flex items-center justify-between border ${
          feedback.type === 'success' ? 'bg-emerald-50 text-emerald-900 border-emerald-200' : 'bg-rose-50 text-rose-900 border-rose-200'
        }`}>
          <span>{feedback.text}</span>
          <button onClick={() => setFeedback({ type: '', text: '' })} className="font-bold underline ml-2">Dismiss</button>
        </div>
      )}

      {/* Filter and Search Bar */}
      <div className="flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
          <input
            type="text"
            placeholder="Search doctor name, email, department..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-9 pr-4 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
          />
        </div>

        <select
          value={selectedSpec}
          onChange={(e) => setSelectedSpec(e.target.value)}
          className="px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium text-slate-700"
        >
          <option value="">All 18 Specializations</option>
          {specializations.map((s) => (
            <option key={s.key} value={s.key}>{s.name}</option>
          ))}
        </select>
      </div>

      {/* Doctor Table */}
      <div className="bg-white rounded-3xl border border-slate-200 overflow-hidden shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 text-slate-500 font-bold uppercase tracking-wider border-b border-slate-200">
              <tr>
                <th className="px-5 py-3">Doctor</th>
                <th className="px-5 py-3">Specialization</th>
                <th className="px-5 py-3">Qualifications</th>
                <th className="px-5 py-3">Experience</th>
                <th className="px-5 py-3">Fee</th>
                <th className="px-5 py-3">Status</th>
                <th className="px-5 py-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-slate-700">
              {filteredDoctors.map((doc) => (
                <tr key={doc.id} className="hover:bg-slate-50/80 transition">
                  <td className="px-5 py-3.5 whitespace-nowrap">
                    <p className="font-bold text-slate-900">{doc.fullName}</p>
                    <p className="text-[11px] text-slate-400">{doc.email}</p>
                  </td>
                  <td className="px-5 py-3.5 font-semibold text-teal-700 whitespace-nowrap">
                    {doc.specializationDisplayName}
                  </td>
                  <td className="px-5 py-3.5 max-w-xs truncate text-slate-600 font-medium">
                    {doc.qualifications}
                  </td>
                  <td className="px-5 py-3.5 whitespace-nowrap font-medium">
                    {doc.yearsOfExperience} Years
                  </td>
                  <td className="px-5 py-3.5 whitespace-nowrap font-black text-slate-900">
                    ${doc.consultationFee}
                  </td>
                  <td className="px-5 py-3.5 whitespace-nowrap">
                    <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                      doc.active ? 'bg-emerald-100 text-emerald-800' : 'bg-rose-100 text-rose-800'
                    }`}>
                      {doc.active ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td className="px-5 py-3.5 text-right whitespace-nowrap space-x-2">
                    <button
                      onClick={() => setEditDoctor({ ...doc })}
                      className="p-1.5 text-slate-500 hover:text-teal-700 hover:bg-slate-100 rounded-lg transition"
                      title="Edit Doctor Profile"
                    >
                      <Edit2 className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => handleToggleActive(doc.id)}
                      className={`p-1.5 rounded-lg transition ${
                        doc.active ? 'text-emerald-600 hover:bg-rose-50 hover:text-rose-600' : 'text-slate-400 hover:bg-emerald-50 hover:text-emerald-600'
                      }`}
                      title={doc.active ? 'Deactivate Doctor' : 'Activate Doctor'}
                    >
                      <Power className="w-3.5 h-3.5" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Add Doctor Modal */}
      <Modal isOpen={isAddModalOpen} onClose={() => setIsAddModalOpen(false)} title="Create New Doctor Account" maxWidth="max-w-xl">
        <form onSubmit={handleCreateDoctor} className="space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">Full Name</label>
              <input
                type="text"
                required
                placeholder="Dr. Gregory House"
                value={newDocData.fullName}
                onChange={(e) => setNewDocData({ ...newDocData, fullName: e.target.value })}
                className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">Email</label>
              <input
                type="email"
                required
                placeholder="doctor@medislot.com"
                value={newDocData.email}
                onChange={(e) => setNewDocData({ ...newDocData, email: e.target.value })}
                className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">Specialization</label>
              <select
                value={newDocData.specialization}
                onChange={(e) => setNewDocData({ ...newDocData, specialization: e.target.value })}
                className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
              >
                {specializations.map((s) => (
                  <option key={s.key} value={s.key}>{s.name}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">Fee ($ USD)</label>
              <input
                type="number"
                value={newDocData.consultationFee}
                onChange={(e) => setNewDocData({ ...newDocData, consultationFee: parseFloat(e.target.value) })}
                className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">Qualifications</label>
            <input
              type="text"
              value={newDocData.qualifications}
              onChange={(e) => setNewDocData({ ...newDocData, qualifications: e.target.value })}
              className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
            />
          </div>

          <div className="flex justify-end space-x-3 pt-2 border-t border-slate-100">
            <button
              type="button"
              onClick={() => setIsAddModalOpen(false)}
              className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-600 hover:bg-slate-100"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="px-5 py-2 rounded-xl text-xs font-bold text-white bg-teal-600 hover:bg-teal-700 transition"
            >
              {submitting ? 'Creating...' : 'Create Doctor'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Edit Doctor Modal */}
      <Modal isOpen={editDoctor !== null} onClose={() => setEditDoctor(null)} title="Edit Doctor Profile" maxWidth="max-w-xl">
        {editDoctor && (
          <form onSubmit={handleEditDoctorSubmit} className="space-y-4">
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">Full Name</label>
                <input
                  type="text"
                  value={editDoctor.fullName}
                  onChange={(e) => setEditDoctor({ ...editDoctor, fullName: e.target.value })}
                  className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
                />
              </div>
              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">Fee ($ USD)</label>
                <input
                  type="number"
                  value={editDoctor.consultationFee}
                  onChange={(e) => setEditDoctor({ ...editDoctor, consultationFee: parseFloat(e.target.value) })}
                  className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">Qualifications</label>
              <input
                type="text"
                value={editDoctor.qualifications}
                onChange={(e) => setEditDoctor({ ...editDoctor, qualifications: e.target.value })}
                className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">Department</label>
                <input
                  type="text"
                  value={editDoctor.department || ''}
                  onChange={(e) => setEditDoctor({ ...editDoctor, department: e.target.value })}
                  className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
                />
              </div>
              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">Room Number</label>
                <input
                  type="text"
                  value={editDoctor.roomNumber || ''}
                  onChange={(e) => setEditDoctor({ ...editDoctor, roomNumber: e.target.value })}
                  className="w-full px-3 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
                />
              </div>
            </div>

            <div className="flex justify-end space-x-3 pt-2 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setEditDoctor(null)}
                className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-600 hover:bg-slate-100"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={submitting}
                className="px-5 py-2 rounded-xl text-xs font-bold text-white bg-teal-600 hover:bg-teal-700 transition"
              >
                {submitting ? 'Saving...' : 'Save Changes'}
              </button>
            </div>
          </form>
        )}
      </Modal>

    </div>
  );
}
