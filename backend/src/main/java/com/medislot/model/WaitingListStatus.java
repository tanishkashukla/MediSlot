package com.medislot.model;

public enum WaitingListStatus {
    WAITING,       // Active in waiting list
    OFFERED,       // Offered cancelled slot, 15-min countdown running
    ACCEPTED,      // Patient accepted offer; converted to Confirmed appointment
    DECLINED,      // Patient manually declined offer
    EXPIRED,       // 15-minute countdown elapsed without response
    CANCELLED      // Cancelled by patient or administrator
}
