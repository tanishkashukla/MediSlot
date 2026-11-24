package com.medislot.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Baseline Linear Scan implementation for doctor slot availability.
 * Used in the Algorithm Performance Lab to compare against the Segment Tree.
 * 
 * Time Complexities:
 *   - Range Available Count Query: O(N)
 *   - Find Next Available Slot: O(N)
 *   - Point Update: O(1)
 *   - Range Slot Retrieval: O(N)
 * Space Complexity: O(N)
 */
public class LinearScanBaseline {

    private final int n;
    private final int[] slots;
    private long operationCount = 0;

    public LinearScanBaseline(int[] initialSlots) {
        this.n = initialSlots.length;
        this.slots = Arrays.copyOf(initialSlots, n);
    }

    public LinearScanBaseline(int size) {
        this.n = size;
        this.slots = new int[n];
        Arrays.fill(this.slots, 1);
    }

    public synchronized int countAvailable(int ql, int qr) {
        int count = 0;
        int start = Math.max(0, ql);
        int end = Math.min(n - 1, qr);
        for (int i = start; i <= end; i++) {
            operationCount++;
            if (slots[i] == 1) {
                count++;
            }
        }
        return count;
    }

    public synchronized int findNextAvailableSlot(int fromIndex, int toIndex) {
        int start = Math.max(0, fromIndex);
        int end = Math.min(n - 1, toIndex);
        for (int i = start; i <= end; i++) {
            operationCount++;
            if (slots[i] == 1) {
                return i;
            }
        }
        return -1;
    }

    public synchronized List<Integer> getAvailableSlotsInRange(int ql, int qr) {
        List<Integer> list = new ArrayList<>();
        int start = Math.max(0, ql);
        int end = Math.min(n - 1, qr);
        for (int i = start; i <= end; i++) {
            operationCount++;
            if (slots[i] == 1) {
                list.add(i);
            }
        }
        return list;
    }

    public synchronized boolean updateSlot(int slotIndex, boolean isAvailable) {
        operationCount++;
        if (slotIndex < 0 || slotIndex >= n) {
            return false;
        }
        int newVal = isAvailable ? 1 : 0;
        if (slots[slotIndex] == newVal) {
            return false;
        }
        slots[slotIndex] = newVal;
        return true;
    }

    public long getOperationCount() {
        return operationCount;
    }

    public void resetOperationCount() {
        this.operationCount = 0;
    }
}
