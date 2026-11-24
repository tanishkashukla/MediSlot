import React from 'react';
import { Activity, Heart, Shield } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function Footer() {
  return (
    <footer className="bg-slate-900 text-slate-400 py-12 border-t border-slate-800 text-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
          
          <div className="md:col-span-2 space-y-3">
            <div className="flex items-center space-x-2 text-white font-bold text-lg">
              <div className="w-8 h-8 rounded-lg bg-teal-500 flex items-center justify-center text-white">
                <Activity className="w-5 h-5" />
              </div>
              <span>MediSlot Smart Scheduling</span>
            </div>
            <p className="text-xs text-slate-400 max-w-sm leading-relaxed">
              Hospital appointment scheduling system designed to automatically find available slots,
              eliminate double-booking, and streamline waiting list management.
            </p>
          </div>

          <div>
            <h4 className="text-white text-xs font-bold uppercase tracking-wider mb-3">Quick Navigation</h4>
            <ul className="space-y-2 text-xs">
              <li><a href="/#specializations" className="hover:text-white transition">18 Specializations</a></li>
              <li><a href="/#doctors" className="hover:text-white transition">Doctor Directory</a></li>
              <li><a href="/#how-it-works" className="hover:text-white transition">How It Works</a></li>
            </ul>
          </div>

          <div>
            <h4 className="text-white text-xs font-bold uppercase tracking-wider mb-3">Quick Login Credentials</h4>
            <div className="space-y-1.5 text-[11px] font-mono bg-slate-800/80 p-3 rounded-xl border border-slate-700/60 text-slate-300">
              <p><span className="text-teal-400">Admin:</span> admin@medislot.com / Admin@123</p>
              <p><span className="text-teal-400">Doctor:</span> doctor.cardio@medislot.com / Doctor@123</p>
              <p><span className="text-teal-400">Patient:</span> patient.john@medislot.com / Patient@123</p>
            </div>
          </div>

        </div>

        <div className="pt-6 border-t border-slate-800 flex flex-col sm:flex-row justify-between items-center text-xs text-slate-500 gap-2">
          <p>© 2026 MediSlot Hospital Systems. B.Tech Advanced Algorithm CIA 3 Assessment.</p>
          <p className="flex items-center gap-1">
            Built with React, Spring Boot & Custom Java Algorithms
          </p>
        </div>
      </div>
    </footer>
  );
}
