import React from 'react';
import { Clock, AlertTriangle, CheckCircle, Lightbulb, UserCheck, Calendar } from 'lucide-react';

export default function SlotPicker({ availability, selectedSlotIndex, onSelectSlot, onSelectSuggestion, loading }) {
  if (loading) {
    return (
      <div className="py-12 text-center text-slate-500 text-sm">
        <div className="inline-block w-8 h-8 border-4 border-teal-500 border-t-transparent rounded-full animate-spin mb-3"></div>
        <p>Loading available doctor slots...</p>
      </div>
    );
  }

  if (!availability) {
    return null;
  }

  const { slots, availableCount, totalSlots, suggestions } = availability;

  return (
    <div className="space-y-4">
      {/* Availability Header */}
      <div className="flex items-center justify-between p-3 rounded-xl bg-slate-50 border border-slate-200 text-xs">
        <span className="font-semibold text-slate-700">
          Available Slots:
        </span>
        <span className={`font-bold px-2.5 py-0.5 rounded-full ${
          availableCount > 0
            ? 'bg-emerald-100 text-emerald-800'
            : 'bg-rose-100 text-rose-800'
        }`}>
          {availableCount} of {totalSlots} Available
        </span>
      </div>

      {/* Slots Grid */}
      {availableCount === 0 ? (
        <div className="p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-800 text-xs text-center">
          <AlertTriangle className="w-5 h-5 mx-auto mb-1 text-rose-600" />
          <p className="font-bold">No available slots for this doctor on the selected date.</p>
          <p className="text-[11px] text-rose-600 mt-0.5">
            You can select an alternative recommendation below or join our Priority Waiting List.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-2.5 max-h-60 overflow-y-auto p-1">
          {slots.map((slot) => {
            const isSelected = selectedSlotIndex === slot.slotIndex;
            const isAvailable = slot.available;

            return (
              <button
                key={slot.slotIndex}
                type="button"
                disabled={!isAvailable}
                onClick={() => onSelectSlot(slot.slotIndex)}
                className={`py-2 px-2.5 rounded-xl text-xs font-semibold border flex flex-col items-center justify-center transition ${
                  isSelected
                    ? 'border-teal-600 bg-teal-600 text-white shadow-md'
                    : isAvailable
                    ? 'border-slate-200 bg-white text-slate-800 hover:border-teal-400 hover:bg-teal-50/50'
                    : 'border-slate-100 bg-slate-100/60 text-slate-400 cursor-not-allowed line-through'
                }`}
                title={!isAvailable ? `Slot is ${slot.reason.toLowerCase()}` : undefined}
              >
                <span>{slot.startTime}</span>
                <span className={`text-[10px] mt-0.5 font-normal ${isSelected ? 'text-teal-100' : 'text-slate-400'}`}>
                  {slot.endTime}
                </span>
              </button>
            );
          })}
        </div>
      )}

      {/* Intelligent Alternative Suggestions Box (Requirement #3) */}
      {suggestions && suggestions.length > 0 && (
        <div className="mt-4 p-4 rounded-2xl bg-gradient-to-r from-teal-50/80 to-medical-50/80 border border-teal-200/80 space-y-2.5">
          <div className="flex items-center space-x-1.5 text-xs font-bold text-teal-900">
            <Lightbulb className="w-4 h-4 text-teal-600" />
            <span>Intelligent Alternative Suggestions:</span>
          </div>

          <div className="space-y-2">
            {suggestions.map((sug, idx) => (
              <div
                key={idx}
                className="p-2.5 rounded-xl bg-white border border-teal-100 text-xs flex items-center justify-between"
              >
                <div className="flex-1 pr-2">
                  <p className="font-semibold text-slate-800">{sug.message}</p>
                  <p className="text-[10px] text-slate-500">
                    {sug.date} · {sug.startTime} - {sug.endTime}
                  </p>
                </div>
                {onSelectSuggestion && (
                  <button
                    type="button"
                    onClick={() => onSelectSuggestion(sug)}
                    className="px-3 py-1 rounded-lg bg-teal-600 hover:bg-teal-700 text-white font-bold text-[11px] transition flex-shrink-0"
                  >
                    Select
                  </button>
                )}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
