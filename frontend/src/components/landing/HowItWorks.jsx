import React from 'react';
import { Search, Calendar, Cpu, BellRing, ShieldCheck, ArrowRight } from 'lucide-react';

export default function HowItWorks() {
  const steps = [
    {
      number: '01',
      title: 'Find Specialty & Doctor',
      description: 'Choose from 18 clinical departments and 36 board-certified specialists with transparent fee schedules.',
      icon: Search,
      tag: 'Step 1',
    },
    {
      number: '02',
      title: 'Real-Time Slot Lookup',
      description: 'Our scheduling engine evaluates daily schedule intervals in real time, presenting only genuinely free slots.',
      icon: Calendar,
      tag: 'Live Availability',
    },
    {
      number: '03',
      title: 'Double-Booking Prevention',
      description: 'Atomic database locking guarantees that concurrent patient bookings never collide or overlap.',
      icon: ShieldCheck,
      tag: 'Concurrency Safe',
    },
    {
      number: '04',
      title: 'Priority Waiting List',
      description: 'If a slot is booked, join the waiting queue. On cancellation, the system offers the slot with a 15-min decision window.',
      icon: BellRing,
      tag: 'Automated Reallocation',
    },
  ];

  return (
    <section id="how-it-works" className="py-20 bg-white border-t border-slate-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto mb-16">
          <span className="text-xs font-bold uppercase tracking-wider text-teal-600 bg-teal-50 px-3 py-1 rounded-full border border-teal-200">
            Intelligent Booking Workflow
          </span>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-slate-900 mt-4 tracking-tight">
            How MediSlot Works
          </h2>
          <p className="text-slate-600 mt-3 text-base">
            From specialist search to confirmed appointment, every step is streamlined for fast and reliable care.
          </p>
        </div>

        {/* 4 Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {steps.map((step, idx) => {
            const Icon = step.icon;
            return (
              <div
                key={idx}
                className="relative bg-slate-50/70 rounded-3xl p-6 border border-slate-200/80 hover:bg-white hover:shadow-lg transition-all flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center justify-between mb-4">
                    <span className="text-3xl font-black text-slate-300 font-mono">
                      {step.number}
                    </span>
                    <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-teal-100 text-teal-800">
                      {step.tag}
                    </span>
                  </div>

                  <div className="w-12 h-12 rounded-2xl bg-medical-50 text-medical-600 flex items-center justify-center mb-4">
                    <Icon className="w-6 h-6" />
                  </div>

                  <h3 className="text-base font-bold text-slate-900 mb-2">
                    {step.title}
                  </h3>

                  <p className="text-xs text-slate-600 leading-relaxed">
                    {step.description}
                  </p>
                </div>

                <div className="mt-6 pt-4 border-t border-slate-200/60 flex items-center text-[11px] font-semibold text-teal-600">
                  <span>Guaranteed Integrity</span>
                  <ArrowRight className="w-3.5 h-3.5 ml-1" />
                </div>
              </div>
            );
          })}
        </div>

      </div>
    </section>
  );
}
