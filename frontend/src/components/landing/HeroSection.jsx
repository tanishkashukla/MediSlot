import React from 'react';
import { Link } from 'react-router-dom';
import { ShieldCheck, Clock, Zap, ArrowRight, CheckCircle2, Award, Calendar } from 'lucide-react';

export default function HeroSection({ onBookClick }) {
  return (
    <section className="relative overflow-hidden pt-12 pb-20 lg:pt-20 lg:pb-28 bg-gradient-to-b from-medical-50/60 via-white to-slate-50">
      {/* Decorative background glow */}
      <div className="absolute top-0 right-1/4 w-96 h-96 bg-teal-200/30 rounded-full blur-3xl pointer-events-none"></div>
      <div className="absolute bottom-0 left-1/4 w-96 h-96 bg-medical-200/30 rounded-full blur-3xl pointer-events-none"></div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
          
          {/* Left Hero Content */}
          <div className="lg:col-span-7 space-y-6 text-center lg:text-left">
            <div className="inline-flex items-center space-x-2 px-3.5 py-1.5 rounded-full bg-teal-50 border border-teal-200 text-teal-800 text-xs sm:text-sm font-semibold shadow-sm">
              <span className="flex h-2 w-2 rounded-full bg-teal-500 animate-ping"></span>
              <span>Smart Healthcare Appointment Scheduling</span>
            </div>

            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-extrabold text-slate-900 tracking-tight leading-[1.15]">
              Intelligent Care, <br />
              <span className="bg-gradient-to-r from-medical-600 to-teal-500 bg-clip-text text-transparent">
                Zero Waiting Disappointment.
              </span>
            </h1>

            <p className="text-lg text-slate-600 max-w-2xl mx-auto lg:mx-0 leading-relaxed">
              MediSlot automatically finds available doctor slots, eliminates double-booking,
              prioritizes urgent requests, and manages waiting lists across 36 top specialists in real time.
            </p>

            {/* CTA Buttons */}
            <div className="flex flex-col sm:flex-row items-center justify-center lg:justify-start gap-4 pt-2">
              <button
                onClick={onBookClick}
                className="w-full sm:w-auto px-7 py-3.5 rounded-xl font-bold text-white bg-gradient-to-r from-medical-600 via-teal-600 to-teal-500 hover:from-medical-700 hover:to-teal-600 shadow-lg shadow-teal-500/25 hover:shadow-xl transition flex items-center justify-center space-x-2.5 group"
              >
                <Calendar className="w-5 h-5" />
                <span>Book Doctor Appointment</span>
                <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
              </button>

              <a
                href="#how-it-works"
                className="w-full sm:w-auto px-7 py-3.5 rounded-xl font-semibold text-slate-700 bg-white border border-slate-200 hover:bg-slate-50 hover:border-slate-300 transition text-center"
              >
                How It Works
              </a>
            </div>

            {/* Key Value Points */}
            <div className="pt-6 grid grid-cols-3 gap-4 border-t border-slate-200/80 max-w-lg mx-auto lg:mx-0">
              <div className="flex items-center space-x-2 text-left">
                <CheckCircle2 className="w-4 h-4 text-teal-600 flex-shrink-0" />
                <span className="text-xs sm:text-sm font-medium text-slate-700">No Double-Booking</span>
              </div>
              <div className="flex items-center space-x-2 text-left">
                <CheckCircle2 className="w-4 h-4 text-teal-600 flex-shrink-0" />
                <span className="text-xs sm:text-sm font-medium text-slate-700">15-Min Offer Realloc</span>
              </div>
              <div className="flex items-center space-x-2 text-left">
                <CheckCircle2 className="w-4 h-4 text-teal-600 flex-shrink-0" />
                <span className="text-xs sm:text-sm font-medium text-slate-700">36 Board Specialists</span>
              </div>
            </div>
          </div>

          {/* Right Hero Visual Card */}
          <div className="lg:col-span-5 relative">
            <div className="bg-white/80 backdrop-blur-xl border border-slate-200/80 rounded-3xl p-6 sm:p-8 shadow-2xl relative">

              <div className="flex items-center space-x-4 mb-6">
                <div className="w-14 h-14 rounded-2xl bg-teal-100 flex items-center justify-center text-teal-700 font-bold text-xl">
                  +
                </div>
                <div>
                  <h3 className="font-bold text-slate-900 text-lg">Hospital Live Availability</h3>
                  <p className="text-xs text-slate-500">18 Departments · 30-Min Precision Slots</p>
                </div>
              </div>

              {/* Sample Slot Preview */}
              <div className="space-y-3 mb-6">
                <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-100 flex justify-between items-center text-sm">
                  <div>
                    <span className="font-semibold text-slate-900">Cardiovascular Clinic</span>
                    <p className="text-xs text-slate-500">Dr. Marcus Vance (FACC)</p>
                  </div>
                  <span className="text-xs font-bold px-2.5 py-1 rounded-full bg-emerald-100 text-emerald-800">
                    6 Slots Free
                  </span>
                </div>

                <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-100 flex justify-between items-center text-sm">
                  <div>
                    <span className="font-semibold text-slate-900">Neurosciences Pavilion</span>
                    <p className="text-xs text-slate-500">Dr. Arthur Pendelton (DM)</p>
                  </div>
                  <span className="text-xs font-bold px-2.5 py-1 rounded-full bg-amber-100 text-amber-800">
                    Waitlist Active
                  </span>
                </div>

                <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-100 flex justify-between items-center text-sm">
                  <div>
                    <span className="font-semibold text-slate-900">Pediatric Care Center</span>
                    <p className="text-xs text-slate-500">Dr. Emily Watson (DCH)</p>
                  </div>
                  <span className="text-xs font-bold px-2.5 py-1 rounded-full bg-emerald-100 text-emerald-800">
                    9 Slots Free
                  </span>
                </div>
              </div>

              {/* Quick Info Bar */}
              <div className="p-4 rounded-2xl bg-gradient-to-r from-medical-50 to-teal-50 border border-teal-100 flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <Clock className="w-5 h-5 text-teal-600" />
                  <div>
                    <p className="text-xs font-bold text-slate-900">Median Wait Time</p>
                    <p className="text-[11px] text-slate-500">Real-time patient throughput</p>
                  </div>
                </div>
                <span className="text-lg font-black text-teal-700">~24.5 min</span>
              </div>
            </div>
          </div>

        </div>
      </div>
    </section>
  );
}
