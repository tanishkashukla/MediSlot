package com.medislot.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Segment Tree Implementation (Unit 3 - Advanced Data Structures).
 * 
 * Manages doctor daily appointment availability slots.
 * Each leaf node represents a time slot:
 *   1 = available
 *   0 = unavailable (booked, blocked, or outside working hours)
 * Internal nodes store the sum of available slots in their subtree range.
 * 
 * Time Complexities:
 *   - Build: O(N)
 *   - Range Available Count Query: O(log N)
 *   - Find Next Available Slot: O(log N)
 *   - Point Update (Book / Cancel / Block / Unblock): O(log N)
 *   - Find All Available Slots in Range: O(K + log N) where K is matching slots
 * Space Complexity: O(N) (allocated 4N array)
 */
public class SegmentTree {

    private final int n;
    private final int[] tree;
    private final int[] rawSlots; // copy of current slot availability (0 or 1)
    private long operationCount = 0; // benchmark operation counter

    /**
     * Constructs a SegmentTree from initial slot array (0 = booked/blocked, 1 = available).
     * @param slots array where 1 indicates available, 0 indicates unavailable
     */
    public SegmentTree(int[] slots) {
        if (slots == null || slots.length == 0) {
            throw new IllegalArgumentException("Slot array must not be null or empty");
        }
        this.n = slots.length;
        this.rawSlots = Arrays.copyOf(slots, n);
        this.tree = new int[4 * n];
        build(1, 0, n - 1);
    }

    /**
     * Constructs a SegmentTree with 'size' slots, all initially available (1).
     */
    public SegmentTree(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
        this.n = size;
        this.rawSlots = new int[n];
        Arrays.fill(this.rawSlots, 1);
        this.tree = new int[4 * n];
        build(1, 0, n - 1);
    }

    /**
     * Internal recursive build: O(N)
     */
    private void build(int node, int start, int end) {
        operationCount++;
        if (start == end) {
            tree[node] = rawSlots[start];
            return;
        }
        int mid = start + (end - start) / 2;
        build(2 * node, start, mid);
        build(2 * node + 1, mid + 1, end);
        tree[node] = tree[2 * node] + tree[2 * node + 1];
    }

    /**
     * Counts available slots in range [ql, qr] in O(log N) time.
     */
    public synchronized int countAvailable(int ql, int qr) {
        if (ql < 0 || qr >= n || ql > qr) {
            return 0;
        }
        return queryRange(1, 0, n - 1, ql, qr);
    }

    private int queryRange(int node, int start, int end, int ql, int qr) {
        operationCount++;
        if (ql <= start && end <= qr) {
            return tree[node];
        }
        if (end < ql || start > qr) {
            return 0;
        }
        int mid = start + (end - start) / 2;
        int leftSum = queryRange(2 * node, start, mid, ql, qr);
        int rightSum = queryRange(2 * node + 1, mid + 1, end, ql, qr);
        return leftSum + rightSum;
    }

    /**
     * Finds the earliest available slot index >= fromIndex within [fromIndex, toIndex].
     * Returns -1 if no slot is available.
     * Time Complexity: O(log N)
     */
    public synchronized int findNextAvailableSlot(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex >= n || fromIndex > toIndex) {
            return -1;
        }
        return findNext(1, 0, n - 1, fromIndex, toIndex);
    }

    private int findNext(int node, int start, int end, int ql, int qr) {
        operationCount++;
        // If range has zero available slots or is completely disjoint
        if (tree[node] == 0 || end < ql || start > qr) {
            return -1;
        }
        // Leaf node reached and is available
        if (start == end) {
            return start;
        }
        int mid = start + (end - start) / 2;
        // Search left subtree first
        int leftRes = findNext(2 * node, start, mid, ql, qr);
        if (leftRes != -1) {
            return leftRes;
        }
        // Search right subtree if not found in left
        return findNext(2 * node + 1, mid + 1, end, ql, qr);
    }

    /**
     * Retrieves all available slot indices in range [ql, qr] in O(K + log N).
     */
    public synchronized List<Integer> getAvailableSlotsInRange(int ql, int qr) {
        List<Integer> result = new ArrayList<>();
        if (ql < 0 || qr >= n || ql > qr) {
            return result;
        }
        collectSlots(1, 0, n - 1, ql, qr, result);
        return result;
    }

    private void collectSlots(int node, int start, int end, int ql, int qr, List<Integer> result) {
        operationCount++;
        if (tree[node] == 0 || end < ql || start > qr) {
            return;
        }
        if (start == end) {
            if (rawSlots[start] == 1) {
                result.add(start);
            }
            return;
        }
        int mid = start + (end - start) / 2;
        collectSlots(2 * node, start, mid, ql, qr, result);
        collectSlots(2 * node + 1, mid + 1, end, ql, qr, result);
    }

    /**
     * Updates slot availability:
     *   set to false (0) when booked or blocked
     *   set to true (1) when cancelled or unblocked
     * Time Complexity: O(log N)
     */
    public synchronized boolean updateSlot(int slotIndex, boolean isAvailable) {
        if (slotIndex < 0 || slotIndex >= n) {
            return false;
        }
        int newVal = isAvailable ? 1 : 0;
        if (rawSlots[slotIndex] == newVal) {
            return false; // no state change
        }
        rawSlots[slotIndex] = newVal;
        update(1, 0, n - 1, slotIndex, newVal);
        return true;
    }

    private void update(int node, int start, int end, int idx, int val) {
        operationCount++;
        if (start == end) {
            tree[node] = val;
            return;
        }
        int mid = start + (end - start) / 2;
        if (idx <= mid) {
            update(2 * node, start, mid, idx, val);
        } else {
            update(2 * node + 1, mid + 1, end, idx, val);
        }
        tree[node] = tree[2 * node] + tree[2 * node + 1];
    }

    public synchronized boolean isSlotAvailable(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= n) {
            return false;
        }
        return rawSlots[slotIndex] == 1;
    }

    public int getSize() {
        return n;
    }

    public synchronized int[] getRawSlots() {
        return Arrays.copyOf(rawSlots, n);
    }

    public synchronized long getOperationCount() {
        return operationCount;
    }

    public synchronized void resetOperationCount() {
        this.operationCount = 0;
    }
}
