import React, { useState, useEffect } from 'react';
import api from '../../api/client';
import { StatusBadge, PriorityBadge } from '../../components/common/Badge';
import { ListOrdered, Clock, Bell, User, Calendar } from 'lucide-react';

export default function DoctorWaitingListPage() {
  const [waitingList, setWaitingList] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadWaitingList();
  }, []);

  const loadWaitingList = async () => {
    setLoading(true);
    try {
      const res = await api.get('/doctor/waiting-list');
      setWaitingList(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-6">
      
      <div>
        <h1 className="text-2xl font-black text-slate-900">Waiting-List & Slot Offers</h1>
      </div>

      {loading ? (
        <div className="py-16 text-center text-slate-500 text-sm">
          <div className="inline-block w-8 h-8 border-4 border-teal-500 border-t-transparent rounded-full animate-spin mb-3"></div>
          <p>Loading waiting list...</p>
        </div>
      ) : waitingList.length === 0 ? (
        <div className="py-16 text-center bg-white rounded-3xl border border-slate-200 text-slate-500 text-sm">
          No patients currently waiting for your schedule.
        </div>
      ) : (
        <div className="bg-white rounded-3xl border border-slate-200 overflow-hidden shadow-sm">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-500 font-bold uppercase tracking-wider border-b border-slate-200">
                <tr>
                  <th className="px-5 py-3">Queue Rank</th>
                  <th className="px-5 py-3">Patient Name</th>
                  <th className="px-5 py-3">Priority Level</th>
                  <th className="px-5 py-3">Target Date</th>
                  <th className="px-5 py-3">Preferred Window</th>
                  <th className="px-5 py-3">Status</th>
                  <th className="px-5 py-3 text-right">Offer Info</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 text-slate-700">
                {waitingList.map((entry) => (
                  <tr key={entry.id} className="hover:bg-slate-50/80 transition">
                    <td className="px-5 py-3.5 font-black text-teal-600">
                      #{entry.queuePosition}
                    </td>
                    <td className="px-5 py-3.5 font-bold text-slate-900">
                      {entry.patientName}
                    </td>
                    <td className="px-5 py-3.5">
                      <PriorityBadge priority={entry.priorityLevel} />
                    </td>
                    <td className="px-5 py-3.5 text-slate-600 font-medium">
                      {entry.preferredDate}
                    </td>
                    <td className="px-5 py-3.5 text-slate-500">
                      {entry.preferredTimeRange}
                    </td>
                    <td className="px-5 py-3.5">
                      <StatusBadge status={entry.status} />
                    </td>
                    <td className="px-5 py-3.5 text-right font-medium text-slate-500">
                      {entry.status === 'OFFERED' ? (
                        <span className="text-amber-700 font-bold">
                          Offered {entry.offeredStartTime} ({entry.secondsRemaining}s left)
                        </span>
                      ) : (
                        '—'
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

    </div>
  );
}
