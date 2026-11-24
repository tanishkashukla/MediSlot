package com.medislot.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Baseline Linear Priority List implementation using an unsorted ArrayList.
 * In enqueue: appends to list in O(1).
 * In dequeueMin: performs a linear scan over all N elements to find minimum, then removes it in O(N).
 * Used in the Algorithm Performance Lab to benchmark against CustomBinaryMinHeap.
 * 
 * Time Complexities:
 *   - Insert: O(1)
 *   - ExtractMin: O(N)
 *   - Total for N extractions: O(N^2) vs O(N log N) for Heap
 * Space Complexity: O(N)
 */
public class LinearSearchHeapBaseline {

    private final List<CustomBinaryMinHeap.HeapNode> list = new ArrayList<>();
    private long operationCount = 0;

    public synchronized void insert(CustomBinaryMinHeap.HeapNode node) {
        operationCount++;
        list.add(node);
    }

    public synchronized CustomBinaryMinHeap.HeapNode extractMin() {
        if (list.isEmpty()) {
            return null;
        }
        int minIdx = 0;
        for (int i = 1; i < list.size(); i++) {
            operationCount++;
            if (list.get(i).comparePriorityTo(list.get(minIdx)) < 0) {
                minIdx = i;
            }
        }
        return list.remove(minIdx);
    }

    public synchronized CustomBinaryMinHeap.HeapNode peek() {
        if (list.isEmpty()) return null;
        int minIdx = 0;
        for (int i = 1; i < list.size(); i++) {
            operationCount++;
            if (list.get(i).comparePriorityTo(list.get(minIdx)) < 0) {
                minIdx = i;
            }
        }
        return list.get(minIdx);
    }

    public synchronized int size() {
        return list.size();
    }

    public synchronized boolean isEmpty() {
        return list.isEmpty();
    }

    public synchronized long getOperationCount() {
        return operationCount;
    }

    public synchronized void resetOperationCount() {
        this.operationCount = 0;
    }
}
