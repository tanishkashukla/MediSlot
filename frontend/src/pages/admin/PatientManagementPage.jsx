import React, { useState, useEffect } from 'react';
import api from '../../api/client';
import { Users, Search, Heart, Phone, MapPin, FileText } from 'lucide-react';

export default function PatientManagementPage() {
  const [patients, setPatients] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadPatients();
  }, []);

  const loadPatients = async () => {
    setLoading(true);
    try {
      const res = await api.get('/admin/patients');
      setPatients(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const filteredPatients = patients.filter((p) => {
    if (!searchTerm) return true;
    const q = searchTerm.toLowerCase();
    return (
      p.user?.fullName?.toLowerCase().includes(q) ||
      p.user?.email?.toLowerCase().includes(q) ||
      p.user?.phone?.includes(q) ||
      p.bloodGroup?.toLowerCase().includes(q)
    );
  });

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-6">
      
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-slate-200 gap-4">
        <div>
          <h1 className="text-2xl font-black text-slate-900">Patient Records Directory</h1>
          <p className="text-xs text-slate-500">
            Registered hospital patients ({patients.length} records), emergency contacts, and medical profiles.
          </p>
        </div>

        {/* Search */}
        <div className="relative min-w-[260px]">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
          <input
            type="text"
            placeholder="Search patient, email, phone, blood..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-9 pr-4 py-2 text-xs bg-white border border-slate-200 rounded-xl focus:ring-2 focus:ring-teal-500 font-medium"
          />
        </div>
      </div>

      {loading ? (
        <div className="py-16 text-center text-slate-500 text-sm">
          <div className="inline-block w-8 h-8 border-4 border-teal-500 border-t-transparent rounded-full animate-spin mb-3"></div>
          <p>Loading patient records...</p>
        </div>
      ) : (
        <div className="bg-white rounded-3xl border border-slate-200 overflow-hidden shadow-sm">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-500 font-bold uppercase tracking-wider border-b border-slate-200">
                <tr>
                  <th className="px-5 py-3">Patient</th>
                  <th className="px-5 py-3">Contact</th>
                  <th className="px-5 py-3">DOB & Gender</th>
                  <th className="px-5 py-3">Blood Group</th>
                  <th className="px-5 py-3">Emergency Contact</th>
                  <th className="px-5 py-3">Medical History</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-slate-700">
                {filteredPatients.slice(0, 50).map((p) => (
                  <tr key={p.id} className="hover:bg-slate-50/80 transition">
                    <td className="px-5 py-3.5 whitespace-nowrap">
                      <p className="font-bold text-slate-900">{p.user?.fullName}</p>
                      <p className="text-[11px] text-slate-400">{p.user?.email}</p>
                    </td>
                    <td className="px-5 py-3.5 whitespace-nowrap text-slate-600 font-medium">
                      {p.user?.phone || '—'}
                    </td>
                    <td className="px-5 py-3.5 whitespace-nowrap text-slate-600">
                      {p.dateOfBirth || '—'} · {p.gender}
                    </td>
                    <td className="px-5 py-3.5 whitespace-nowrap">
                      <span className="px-2 py-0.5 rounded-md text-[10px] font-black bg-rose-50 text-rose-700 border border-rose-200">
                        {p.bloodGroup || 'O+'}
                      </span>
                    </td>
                    <td className="px-5 py-3.5 whitespace-nowrap text-slate-600">
                      {p.emergencyContact || '—'}
                    </td>
                    <td className="px-5 py-3.5 max-w-xs truncate text-slate-500">
                      {p.medicalHistory || 'No recorded history'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {filteredPatients.length > 50 && (
            <div className="p-4 bg-slate-50 text-center text-xs text-slate-500 border-t border-slate-100">
              Showing first 50 of {filteredPatients.length} patient records. Use search to filter specific records.
            </div>
          )}
        </div>
      )}

    </div>
  );
}
