package com.medislot.algorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and Correctness Tests for Segment Tree (Unit 3).
 * Contains documented tests verifying:
 *   1. Range available count queries
 *   2. Find next available slot
 *   3. Point updates (booking & cancellation restoration)
 *   4. Boundary and edge-case handling (empty/single slot/out-of-bounds)
 *   5. Randomized differential testing against LinearScanBaseline
 */
public class SegmentTreeTest {

    private SegmentTree tree;
    private final int[] sampleSlots = {1, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 1, 1, 0, 1}; // 16 slots

    @BeforeEach
    public void setUp() {
        tree = new SegmentTree(sampleSlots);
    }

    @Test
    @DisplayName("Test 1: Total and Sub-Range Available Slot Counting")
    public void testRangeCountAvailable() {
        // Total available slots
        int totalExpected = 0;
        for (int s : sampleSlots) totalExpected += s;
        assertEquals(totalExpected, tree.countAvailable(0, sampleSlots.length - 1),
                "Segment tree must accurately count all available slots in full range");

        // Sub-range [2, 6]: slots are {1, 1, 0, 0, 1} -> count = 3
        assertEquals(3, tree.countAvailable(2, 6),
                "Sub-range count must match sum of slots in [2, 6]");

        // Empty sub-range where all are 0: [4, 5] -> {0, 0}
        assertEquals(0, tree.countAvailable(4, 5),
                "Range of blocked slots must return 0");
    }

    @Test
    @DisplayName("Test 2: Find Next Available Slot within Time Window")
    public void testFindNextAvailableSlot() {
        // From index 0 (which is 1): should return 0
        assertEquals(0, tree.findNextAvailableSlot(0, 15));

        // From index 1 (which is 0): next available is index 2
        assertEquals(2, tree.findNextAvailableSlot(1, 15));

        // From index 4 (0) up to 5 (0): neither is available -> should return -1
        assertEquals(-1, tree.findNextAvailableSlot(4, 5));

        // From index 4 up to 7: next available is index 6
        assertEquals(6, tree.findNextAvailableSlot(4, 7));
    }

    @Test
    @DisplayName("Test 3: Point Updates for Booking and Cancellation Restoration")
    public void testUpdateSlotBookingAndCancellation() {
        // Slot 2 is currently available (1)
        assertTrue(tree.isSlotAvailable(2));
        int initialCount = tree.countAvailable(0, 15);

        // Book slot 2 (set to false / 0)
        boolean booked = tree.updateSlot(2, false);
        assertTrue(booked, "Update to book available slot should succeed");
        assertFalse(tree.isSlotAvailable(2), "Slot 2 must now be unavailable");
        assertEquals(initialCount - 1, tree.countAvailable(0, 15),
                "Total available count must decrement by 1");

        // Cancel appointment on slot 2 (restore to true / 1)
        boolean restored = tree.updateSlot(2, true);
        assertTrue(restored, "Update to restore cancelled slot should succeed");
        assertTrue(tree.isSlotAvailable(2), "Slot 2 must be restored to available");
        assertEquals(initialCount, tree.countAvailable(0, 15),
                "Total available count must match original count after restoration");
    }

    @Test
    @DisplayName("Test 4: Boundary and Edge-Case Robustness")
    public void testBoundaryConditions() {
        // Single element Segment Tree
        SegmentTree singleTree = new SegmentTree(new int[]{1});
        assertEquals(1, singleTree.countAvailable(0, 0));
        assertEquals(0, singleTree.findNextAvailableSlot(0, 0));
        singleTree.updateSlot(0, false);
        assertEquals(0, singleTree.countAvailable(0, 0));
        assertEquals(-1, singleTree.findNextAvailableSlot(0, 0));

        // Invalid query bounds (out of range)
        assertEquals(0, tree.countAvailable(-5, 2));
        assertEquals(0, tree.countAvailable(10, 25));
        assertEquals(0, tree.countAvailable(8, 4)); // left > right
        assertEquals(-1, tree.findNextAvailableSlot(-1, 5));
        assertEquals(-1, tree.findNextAvailableSlot(5, 2));

        // All slots zero
        SegmentTree emptyTree = new SegmentTree(new int[]{0, 0, 0, 0});
        assertEquals(0, emptyTree.countAvailable(0, 3));
        assertEquals(-1, emptyTree.findNextAvailableSlot(0, 3));
        assertTrue(emptyTree.getAvailableSlotsInRange(0, 3).isEmpty());
    }

    @Test
    @DisplayName("Test 5: Randomized Correctness Verification against Linear Scan Baseline")
    public void testRandomizedComparisonWithBaseline() {
        Random rand = new Random(12345);
        int n = 200;
        int[] slots = new int[n];
        for (int i = 0; i < n; i++) {
            slots[i] = rand.nextBoolean() ? 1 : 0;
        }

        SegmentTree segTree = new SegmentTree(slots);
        LinearScanBaseline scanBaseline = new LinearScanBaseline(slots);

        for (int trial = 0; trial < 1000; trial++) {
            int ql = rand.nextInt(n);
            int qr = ql + rand.nextInt(n - ql);

            // Compare range count
            int countTree = segTree.countAvailable(ql, qr);
            int countScan = scanBaseline.countAvailable(ql, qr);
            assertEquals(countScan, countTree, "Count mismatch on trial " + trial + " for [" + ql + ", " + qr + "]");

            // Compare find next
            int nextTree = segTree.findNextAvailableSlot(ql, qr);
            int nextScan = scanBaseline.findNextAvailableSlot(ql, qr);
            assertEquals(nextScan, nextTree, "Next slot mismatch on trial " + trial + " for [" + ql + ", " + qr + "]");

            // Perform randomized update
            if (trial % 10 == 0) {
                int targetIdx = rand.nextInt(n);
                boolean newVal = rand.nextBoolean();
                segTree.updateSlot(targetIdx, newVal);
                scanBaseline.updateSlot(targetIdx, newVal);
            }
        }
    }
}
