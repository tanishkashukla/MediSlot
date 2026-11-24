import React, { useState } from 'react';
import { Search, Star, Award, Calendar, DollarSign, MapPin, ArrowRight } from 'lucide-react';

export default function DoctorFinder({ doctors, specializations, selectedSpecialization, onSelectSpecialization, onBookDoctor }) {
  const [searchTerm, setSearchTerm] = useState('');

  const filteredDoctors = doctors.filter((doc) => {
    const matchesSpec = !selectedSpecialization || doc.specialization === selectedSpecialization;
    const matchesQuery = !searchTerm ||
      doc.fullName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      doc.department?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      doc.specializationDisplayName?.toLowerCase().includes(searchTerm.toLowerCase());
    return matchesSpec && matchesQuery;
  });

  return (
    <section id="doctors" className="py-20 bg-slate-50 border-t border-slate-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Section Header */}
        <div className="flex flex-col md:flex-row md:items-end justify-between mb-10 gap-4">
          <div>
            <span className="text-xs font-bold uppercase tracking-wider text-medical-600 bg-medical-50 px-3 py-1 rounded-full border border-medical-200">
              Verified Medical Faculty
            </span>
            <h2 className="text-3xl font-extrabold text-slate-900 mt-2 tracking-tight">
              Find Doctors & Check Slot Availability
            </h2>
            <p className="text-slate-600 mt-1 text-sm">
              All 36 faculty specialists maintain synchronized real-time schedules.
            </p>
          </div>

          {/* Search bar & filter */}
          <div className="flex flex-col sm:flex-row gap-3 w-full md:w-auto">
            <div className="relative min-w-[240px]">
              <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="text"
                placeholder="Search doctor or specialty..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-9 pr-4 py-2 text-sm bg-white border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-teal-500"
              />
            </div>

            <select
              value={selectedSpecialization || ''}
              onChange={(e) => onSelectSpecialization(e.target.value || null)}
              className="px-3 py-2 text-sm bg-white border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-teal-500 font-medium text-slate-700"
            >
              <option value="">All 18 Specialties</option>
              {specializations.map((s) => (
                <option key={s.key} value={s.key}>{s.name}</option>
              ))}
            </select>
          </div>
        </div>

        {/* Doctor Cards Grid */}
        {filteredDoctors.length === 0 ? (
          <div className="text-center py-16 bg-white rounded-3xl border border-slate-200">
            <p className="text-slate-500 text-sm">No doctors found matching your criteria.</p>
            <button
              onClick={() => { setSearchTerm(''); onSelectSpecialization(null); }}
              className="mt-3 text-xs font-semibold text-teal-600 hover:underline"
            >
              Reset Filters
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredDoctors.map((doc) => (
              <div
                key={doc.id}
                className="bg-white rounded-2xl border border-slate-200/90 p-5 shadow-sm hover:shadow-md transition flex flex-col justify-between group"
              >
                <div>
                  {/* Top Bar with Spec & Rating */}
                  <div className="flex items-center justify-between mb-3">
                    <span className="text-[11px] font-bold px-2.5 py-0.5 rounded-full bg-teal-50 text-teal-700 border border-teal-200">
                      {doc.specializationDisplayName}
                    </span>
                    <div className="flex items-center space-x-1 text-amber-500 text-xs font-bold">
                      <Star className="w-3.5 h-3.5 fill-current" />
                      <span>{doc.rating}</span>
                    </div>
                  </div>

                  {/* Doctor Info */}
                  <h3 className="text-base font-bold text-slate-900 group-hover:text-medical-600 transition-colors">
                    {doc.fullName}
                  </h3>
                  <p className="text-xs text-slate-500 font-medium">{doc.qualifications}</p>

                  <div className="mt-3 space-y-1.5 text-xs text-slate-600">
                    <div className="flex items-center space-x-2">
                      <Award className="w-3.5 h-3.5 text-teal-600 flex-shrink-0" />
                      <span>{doc.yearsOfExperience} Years Clinical Experience</span>
                    </div>
                    <div className="flex items-center space-x-2">
                      <MapPin className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" />
                      <span>{doc.department} · {doc.roomNumber}</span>
                    </div>
                  </div>

                  {doc.bio && (
                    <p className="mt-3 text-xs text-slate-500 line-clamp-2 leading-relaxed">
                      {doc.bio}
                    </p>
                  )}
                </div>

                {/* Footer with Booking Button */}
                <div className="mt-5 pt-4 border-t border-slate-100 flex items-center justify-end">
                  <button
                    onClick={() => onBookDoctor(doc)}
                    className="w-full inline-flex items-center justify-center space-x-1.5 px-4 py-2.5 rounded-xl text-xs font-bold text-white bg-medical-600 hover:bg-medical-700 shadow-sm hover:shadow transition"
                  >
                    <Calendar className="w-3.5 h-3.5" />
                    <span>Book Appointment</span>
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

      </div>
    </section>
  );
}
