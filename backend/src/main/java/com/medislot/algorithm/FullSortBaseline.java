package com.medislot.algorithm;

import java.util.Arrays;

/**
 * Baseline Full Sorting implementation using Dual-Pivot Quicksort (Arrays.sort).
 * Used in the Algorithm Performance Lab to benchmark against Randomized Quickselect.
 * 
 * Time Complexities:
 *   - Average / Best / Worst: O(N log N)
 * Space Complexity: O(N) due to array cloning and sorting overhead
 */
public class FullSortBaseline {

    private long comparisonsCount = 0;

    public FullSortBaseline() {
    }

    public double calculateMedian(double[] arr) {
        if (arr == null || arr.length == 0) return 0.0;
        double[] sorted = Arrays.copyOf(arr, arr.length);
        Arrays.sort(sorted);
        // Estimate comparisons for Dual-Pivot Quicksort: approx N * log2(N)
        comparisonsCount += (long) (sorted.length * (Math.log(sorted.length) / Math.log(2)));

        int mid = sorted.length / 2;
        if (sorted.length % 2 == 1) {
            return sorted[mid];
        } else {
            return (sorted[mid - 1] + sorted[mid]) / 2.0;
        }
    }

    public double calculatePercentile(double[] arr, double percentile) {
        if (arr == null || arr.length == 0) return 0.0;
        double[] sorted = Arrays.copyOf(arr, arr.length);
        Arrays.sort(sorted);
        comparisonsCount += (long) (sorted.length * (Math.log(sorted.length) / Math.log(2)));

        int k = (int) Math.round((percentile / 100.0) * (sorted.length - 1));
        k = Math.max(0, Math.min(sorted.length - 1, k));
        return sorted[k];
    }

    public long getComparisonsCount() {
        return comparisonsCount;
    }

    public void resetOperationCounts() {
        this.comparisonsCount = 0;
    }
}
