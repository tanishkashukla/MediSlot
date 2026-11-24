package com.medislot.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Custom Binary Min Heap Implementation (Unit 2 - Priority Queues & Heaps).
 * 
 * Used for priority-based waiting list management.
 * Strictly adheres to academic requirements:
 *   - No java.util.PriorityQueue is used.
 *   - Urgent priority (rank 1) takes administrative precedence over Normal (rank 2).
 *   - Ties in priority are resolved First-Come-First-Served (FCFS) using entry timestamps.
 * 
 * Time Complexities:
 *   - Insert (Enqueue): O(log N)
 *   - Extract Min (Dequeue Highest Priority): O(log N)
 *   - Peek: O(1)
 *   - Remove by ID: O(N)
 *   - Size / IsEmpty: O(1)
 * Space Complexity: O(N)
 */
public class CustomBinaryMinHeap {

    public static class HeapNode {
        private final Long entryId;
        private final Long patientId;
        private final String patientName;
        private final Long doctorId;
        private final String specialization;
        private final int priorityRank; // 1 = Urgent, 2 = Normal
        private final long createdAtTimestamp; // Milliseconds epoch for FCFS tie-breaking
        private final String preferredDate;
        private final String preferredTimeRange;

        public HeapNode(Long entryId, Long patientId, String patientName, Long doctorId,
                        String specialization, int priorityRank, long createdAtTimestamp,
                        String preferredDate, String preferredTimeRange) {
            this.entryId = entryId;
            this.patientId = patientId;
            this.patientName = patientName;
            this.doctorId = doctorId;
            this.specialization = specialization;
            this.priorityRank = priorityRank;
            this.createdAtTimestamp = createdAtTimestamp;
            this.preferredDate = preferredDate;
            this.preferredTimeRange = preferredTimeRange;
        }

        public Long getEntryId() { return entryId; }
        public Long getPatientId() { return patientId; }
        public String getPatientName() { return patientName; }
        public Long getDoctorId() { return doctorId; }
        public String getSpecialization() { return specialization; }
        public int getPriorityRank() { return priorityRank; }
        public long getCreatedAtTimestamp() { return createdAtTimestamp; }
        public String getPreferredDate() { return preferredDate; }
        public String getPreferredTimeRange() { return preferredTimeRange; }

        /**
         * Compares this node with another node for Min-Heap ordering.
         * Returns negative if this node has strictly higher priority (should be closer to root).
         */
        public int comparePriorityTo(HeapNode other) {
            // 1. Lower priorityRank is higher priority (Urgent=1 beats Normal=2)
            if (this.priorityRank != other.priorityRank) {
                return Integer.compare(this.priorityRank, other.priorityRank);
            }
            // 2. FCFS: earlier registration timestamp beats later timestamp
            if (this.createdAtTimestamp != other.createdAtTimestamp) {
                return Long.compare(this.createdAtTimestamp, other.createdAtTimestamp);
            }
            // 3. Fallback tie-breaker on entry ID
            return Long.compare(this.entryId != null ? this.entryId : 0L,
                                other.entryId != null ? other.entryId : 0L);
        }
    }

    private static final int DEFAULT_CAPACITY = 16;
    private HeapNode[] heap;
    private int size;
    private long operationCount = 0; // Operation / comparison counter for benchmark lab

    public CustomBinaryMinHeap() {
        this(DEFAULT_CAPACITY);
    }

    public CustomBinaryMinHeap(int capacity) {
        if (capacity <= 0) capacity = DEFAULT_CAPACITY;
        this.heap = new HeapNode[capacity];
        this.size = 0;
    }

    /**
     * Inserts a new node into the priority queue in O(log N).
     */
    public synchronized void insert(HeapNode node) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }
        ensureCapacity();
        heap[size] = node;
        siftUp(size);
        size++;
    }

    /**
     * Extracts and returns the highest priority (minimum key) node in O(log N).
     */
    public synchronized HeapNode extractMin() {
        if (isEmpty()) {
            return null;
        }
        HeapNode root = heap[0];
        HeapNode last = heap[size - 1];
        heap[0] = last;
        heap[size - 1] = null;
        size--;

        if (size > 0) {
            siftDown(0);
        }
        return root;
    }

    /**
     * Peeks at the highest priority node without removing it in O(1).
     */
    public synchronized HeapNode peek() {
        if (isEmpty()) {
            return null;
        }
        return heap[0];
    }

    /**
     * Removes an entry by its unique entry ID in O(N).
     */
    public synchronized boolean removeById(Long entryId) {
        if (entryId == null || isEmpty()) {
            return false;
        }
        int index = -1;
        for (int i = 0; i < size; i++) {
            operationCount++;
            if (heap[i] != null && entryId.equals(heap[i].getEntryId())) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return false;
        }

        // Replace with last element and restore heap properties
        HeapNode last = heap[size - 1];
        heap[index] = last;
        heap[size - 1] = null;
        size--;

        if (index < size) {
            siftUp(index);
            siftDown(index);
        }
        return true;
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parentIdx = (index - 1) / 2;
            operationCount++;
            if (heap[index].comparePriorityTo(heap[parentIdx]) < 0) {
                swap(index, parentIdx);
                index = parentIdx;
            } else {
                break;
            }
        }
    }

    private void siftDown(int index) {
        while (true) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            int smallest = index;

            if (leftChild < size) {
                operationCount++;
                if (heap[leftChild].comparePriorityTo(heap[smallest]) < 0) {
                    smallest = leftChild;
                }
            }

            if (rightChild < size) {
                operationCount++;
                if (heap[rightChild].comparePriorityTo(heap[smallest]) < 0) {
                    smallest = rightChild;
                }
            }

            if (smallest != index) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        HeapNode temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    private void ensureCapacity() {
        if (size >= heap.length) {
            heap = Arrays.copyOf(heap, heap.length * 2);
        }
    }

    public synchronized int size() {
        return size;
    }

    public synchronized boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns a copy of the heap's elements in sorted priority order without mutating this heap.
     * Time Complexity: O(N log N)
     */
    public synchronized List<HeapNode> getSortedEntries() {
        CustomBinaryMinHeap clone = new CustomBinaryMinHeap(size);
        for (int i = 0; i < size; i++) {
            clone.insert(this.heap[i]);
        }
        List<HeapNode> sortedList = new ArrayList<>(size);
        while (!clone.isEmpty()) {
            sortedList.add(clone.extractMin());
        }
        return sortedList;
    }

    public synchronized long getOperationCount() {
        return operationCount;
    }

    public synchronized void resetOperationCount() {
        this.operationCount = 0;
    }
}
