import React from 'react';

export function StatusBadge({ status }) {
  const styles = {
    CONFIRMED: 'bg-emerald-50 text-emerald-700 border-emerald-200',
    CHECKED_IN: 'bg-blue-50 text-blue-700 border-blue-200',
    IN_CONSULTATION: 'bg-purple-50 text-purple-700 border-purple-200 animate-pulse',
    COMPLETED: 'bg-slate-100 text-slate-700 border-slate-300',
    CANCELLED: 'bg-rose-50 text-rose-700 border-rose-200',
    NO_SHOW: 'bg-amber-50 text-amber-700 border-amber-200',
    WAITING: 'bg-sky-50 text-sky-700 border-sky-200',
    OFFERED: 'bg-amber-50 text-amber-800 border-amber-300 animate-pulse font-bold',
    ACCEPTED: 'bg-teal-50 text-teal-700 border-teal-200',
    DECLINED: 'bg-slate-100 text-slate-600 border-slate-200',
    EXPIRED: 'bg-red-50 text-red-600 border-red-200',
  };

  const labels = {
    CONFIRMED: 'Confirmed',
    CHECKED_IN: 'Checked In',
    IN_CONSULTATION: 'In Consultation',
    COMPLETED: 'Completed',
    CANCELLED: 'Cancelled',
    NO_SHOW: 'No Show',
    WAITING: 'Waiting in Queue',
    OFFERED: 'Offer Ready! (15m)',
    ACCEPTED: 'Offer Accepted',
    DECLINED: 'Declined',
    EXPIRED: 'Offer Expired',
  };

  const style = styles[status] || 'bg-slate-50 text-slate-600 border-slate-200';
  const label = labels[status] || status;

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border ${style}`}>
      {label}
    </span>
  );
}

export function PriorityBadge({ priority }) {
  if (priority === 'URGENT') {
    return (
      <span className="inline-flex items-center px-2 py-0.5 rounded-md text-[11px] font-bold bg-amber-100 text-amber-900 border border-amber-300">
        URGENT PRIORITY
      </span>
    );
  }
  return (
    <span className="inline-flex items-center px-2 py-0.5 rounded-md text-[11px] font-medium bg-slate-100 text-slate-700 border border-slate-200">
      Normal
    </span>
  );
}
