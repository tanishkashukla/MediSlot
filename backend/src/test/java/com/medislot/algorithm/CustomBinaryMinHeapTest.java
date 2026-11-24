package com.medislot.algorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and Correctness Tests for Custom Binary Min Heap (Unit 2).
 * Strictly tests:
 *   1. Urgent (rank 1) precedence over Normal (rank 2)
 *   2. FCFS tie-breaking: earlier timestamp processed before later timestamp
 *   3. Dynamic resizing and capacity expansion
 *   4. Boundary conditions: extract from empty heap, peek, single node
 *   5. Randomized differential testing against LinearSearchHeapBaseline
 */
public class CustomBinaryMinHeapTest {

    private CustomBinaryMinHeap heap;

    @BeforeEach
    public void setUp() {
        heap = new CustomBinaryMinHeap(4); // start with small capacity to test resizing
    }

    @Test
    @DisplayName("Test 1: Urgent Priority Precedence over Normal Priority")
    public void testUrgentPrecedence() {
        // Insert Normal patient (priorityRank = 2) at time 100
        heap.insert(new CustomBinaryMinHeap.HeapNode(1L, 101L, "Normal Patient", 1L, "CARDIOLOGY", 2, 100L, "2026-11-25", "ANY"));

        // Insert Urgent patient (priorityRank = 1) at time 200 (later timestamp, but Urgent priority)
        heap.insert(new CustomBinaryMinHeap.HeapNode(2L, 102L, "Urgent Patient", 1L, "CARDIOLOGY", 1, 200L, "2026-11-25", "ANY"));

        // Extract minimum must return the Urgent patient first!
        CustomBinaryMinHeap.HeapNode first = heap.extractMin();
        assertNotNull(first);
        assertEquals(1, first.getPriorityRank(), "Urgent patient (rank 1) must be extracted before Normal patient (rank 2)");
        assertEquals(2L, first.getEntryId());

        CustomBinaryMinHeap.HeapNode second = heap.extractMin();
        assertNotNull(second);
        assertEquals(2, second.getPriorityRank());
        assertEquals(1L, second.getEntryId());
    }

    @Test
    @DisplayName("Test 2: FCFS Tie-Breaking on Equal Priority")
    public void testFcfsTieBreaking() {
        // Insert three Urgent patients with different timestamps
        heap.insert(new CustomBinaryMinHeap.HeapNode(1L, 101L, "Urgent Patient C", 1L, "CARDIOLOGY", 1, 500L, "2026-11-25", "ANY"));
        heap.insert(new CustomBinaryMinHeap.HeapNode(2L, 102L, "Urgent Patient A", 1L, "CARDIOLOGY", 1, 100L, "2026-11-25", "ANY"));
        heap.insert(new CustomBinaryMinHeap.HeapNode(3L, 103L, "Urgent Patient B", 1L, "CARDIOLOGY", 1, 300L, "2026-11-25", "ANY"));

        // Should extract in order of timestamps: 100L (id 2) -> 300L (id 3) -> 500L (id 1)
        assertEquals(2L, heap.extractMin().getEntryId(), "Earliest timestamp must be extracted first (FCFS)");
        assertEquals(3L, heap.extractMin().getEntryId(), "Second earliest timestamp must be extracted next");
        assertEquals(1L, heap.extractMin().getEntryId(), "Latest timestamp must be extracted last");
    }

    @Test
    @DisplayName("Test 3: Dynamic Capacity Expansion")
    public void testCapacityExpansion() {
        // Initial capacity is 4; insert 50 nodes to trigger multiple expansions
        for (int i = 0; i < 50; i++) {
            heap.insert(new CustomBinaryMinHeap.HeapNode(
                    (long) i, (long) (100 + i), "Patient " + i, 1L, "CARDIOLOGY", (i % 2) + 1, (long) i, "2026-11-25", "ANY"));
        }
        assertEquals(50, heap.size(), "Heap size must be 50 after dynamic resizing");

        // Verify min-heap property: all Urgent (rank 1) nodes extracted before Normal (rank 2)
        int previousRank = 1;
        while (!heap.isEmpty()) {
            CustomBinaryMinHeap.HeapNode top = heap.extractMin();
            assertTrue(top.getPriorityRank() >= previousRank, "Extracted rank should be non-decreasing");
            previousRank = top.getPriorityRank();
        }
    }

    @Test
    @DisplayName("Test 4: Boundary Cases & Node Removal")
    public void testBoundaryAndRemoval() {
        // Extract on empty heap returns null
        assertTrue(heap.isEmpty());
        assertNull(heap.extractMin());
        assertNull(heap.peek());

        // Single element
        heap.insert(new CustomBinaryMinHeap.HeapNode(99L, 199L, "Solo", 1L, "ENT", 1, 10L, "2026-11-25", "ANY"));
        assertEquals(1, heap.size());
        assertEquals(99L, heap.peek().getEntryId());
        assertEquals(99L, heap.extractMin().getEntryId());
        assertTrue(heap.isEmpty());

        // Remove by entry ID
        heap.insert(new CustomBinaryMinHeap.HeapNode(10L, 110L, "P10", 1L, "ENT", 2, 10L, "2026-11-25", "ANY"));
        heap.insert(new CustomBinaryMinHeap.HeapNode(20L, 120L, "P20", 1L, "ENT", 1, 20L, "2026-11-25", "ANY"));
        heap.insert(new CustomBinaryMinHeap.HeapNode(30L, 130L, "P30", 1L, "ENT", 2, 30L, "2026-11-25", "ANY"));

        boolean removed = heap.removeById(20L); // remove the urgent node
        assertTrue(removed);
        assertEquals(2, heap.size());

        // Non-existent ID removal
        assertFalse(heap.removeById(999L));
    }

    @Test
    @DisplayName("Test 5: Randomized Differential Testing against Linear Search Baseline")
    public void testRandomizedComparisonWithBaseline() {
        Random rand = new Random(54321);
        int n = 300;

        List<CustomBinaryMinHeap.HeapNode> testNodes = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            int rank = rand.nextBoolean() ? 1 : 2;
            long ts = rand.nextInt(10000);
            testNodes.add(new CustomBinaryMinHeap.HeapNode((long) i, (long) (100 + i), "P" + i, 1L, "SPEC", rank, ts, "2026-11-25", "ANY"));
        }

        LinearSearchHeapBaseline baseline = new LinearSearchHeapBaseline();
        CustomBinaryMinHeap minHeap = new CustomBinaryMinHeap(16);

        for (CustomBinaryMinHeap.HeapNode node : testNodes) {
            baseline.insert(node);
            minHeap.insert(node);
        }

        assertEquals(baseline.size(), minHeap.size());

        // Both must extract elements in exact identical priority order!
        while (!minHeap.isEmpty()) {
            CustomBinaryMinHeap.HeapNode heapNode = minHeap.extractMin();
            CustomBinaryMinHeap.HeapNode baseNode = baseline.extractMin();

            assertNotNull(heapNode);
            assertNotNull(baseNode);
            assertEquals(baseNode.getPriorityRank(), heapNode.getPriorityRank(), "Priority rank must match");
            assertEquals(baseNode.getCreatedAtTimestamp(), heapNode.getCreatedAtTimestamp(), "Timestamp must match");
            assertEquals(baseNode.getEntryId(), heapNode.getEntryId(), "Entry ID must match");
        }
        assertTrue(baseline.isEmpty());
    }
}
