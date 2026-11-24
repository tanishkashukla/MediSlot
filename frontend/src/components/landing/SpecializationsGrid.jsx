import React from 'react';
import {
  Heart,
  Sparkles,
  Smile,
  Brain,
  Bone,
  Baby,
  Users,
  Ear,
  Eye,
  SmilePlus,
  Stethoscope,
  Activity,
  Flame,
  Wind,
  Dna,
  ShieldAlert,
  Scan,
  Droplets
} from 'lucide-react';

const SPECIALTY_ICONS = {
  CARDIOLOGY: Heart,
  DERMATOLOGY: Sparkles,
  DENTISTRY: Smile,
  NEUROLOGY: Brain,
  ORTHOPEDICS: Bone,
  PEDIATRICS: Baby,
  GYNECOLOGY: Users,
  ENT: Ear,
  OPHTHALMOLOGY: Eye,
  PSYCHIATRY: SmilePlus,
  GENERAL_MEDICINE: Stethoscope,
  UROLOGY: Droplets,
  GASTROENTEROLOGY: Flame,
  PULMONOLOGY: Wind,
  ENDOCRINOLOGY: Dna,
  ONCOLOGY: ShieldAlert,
  RADIOLOGY: Scan,
  NEPHROLOGY: Activity,
};

export default function SpecializationsGrid({ specializations, onSelectSpecialization, selectedSpecialization }) {
  return (
    <section id="specializations" className="py-20 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto mb-16">
          <span className="text-xs font-bold uppercase tracking-wider text-teal-600 bg-teal-50 px-3 py-1 rounded-full border border-teal-200">
            Comprehensive Medical Divisions
          </span>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-slate-900 mt-4 tracking-tight">
            18 Hospital Specializations
          </h2>
          <p className="text-slate-600 mt-3 text-base">
            Select a specialty to explore doctors, examine Segment-Tree managed appointment slots, or join our intelligent waiting list.
          </p>
        </div>

        {/* Grid of 18 Specialties */}
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-4">
          {specializations.map((spec) => {
            const Icon = SPECIALTY_ICONS[spec.key] || Stethoscope;
            const isSelected = selectedSpecialization === spec.key;

            return (
              <button
                key={spec.key}
                onClick={() => onSelectSpecialization(spec.key === selectedSpecialization ? null : spec.key)}
                className={`p-4 rounded-2xl border text-left flex flex-col justify-between transition-all group ${
                  isSelected
                    ? 'border-teal-500 bg-teal-50/60 shadow-md ring-2 ring-teal-500/20'
                    : 'border-slate-200 hover:border-teal-300 hover:bg-slate-50/80 hover:shadow-sm'
                }`}
              >
                <div className={`w-10 h-10 rounded-xl flex items-center justify-center mb-3 transition-colors ${
                  isSelected
                    ? 'bg-teal-500 text-white'
                    : 'bg-teal-50 text-teal-600 group-hover:bg-teal-100'
                }`}>
                  <Icon className="w-5 h-5" />
                </div>

                <div>
                  <h3 className={`text-sm font-bold truncate ${isSelected ? 'text-teal-900' : 'text-slate-900'}`}>
                    {spec.name}
                  </h3>
                  <p className="text-[11px] text-slate-500 line-clamp-2 mt-0.5 leading-tight">
                    {spec.description}
                  </p>
                </div>
              </button>
            );
          })}
        </div>

        {selectedSpecialization && (
          <div className="mt-6 text-center">
            <button
              onClick={() => onSelectSpecialization(null)}
              className="text-xs font-semibold text-slate-500 hover:text-slate-800 underline"
            >
              Clear Specialization Filter
            </button>
          </div>
        )}

      </div>
    </section>
  );
}
