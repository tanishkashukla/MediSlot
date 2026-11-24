package com.medislot.algorithm;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Randomized Quickselect Implementation (Unit 6 / CO5 - Order Statistics & Randomized Algorithms).
 * 
 * Computes the k-th smallest element, Median, 90th Percentile, and 95th Percentile
 * of patient waiting times in Expected O(N) time and O(1) auxiliary space,
 * avoiding the O(N log N) overhead of full array sorting.
 * 
 * Features:
 *   1. True Random Pivot Selection (prevents worst-case O(N^2) on pre-sorted data).
 *   2. Three-Way Partitioning (Dutch National Flag algorithm):
 *      Partitions array into [< pivot], [== pivot], [> pivot].
 *      Optimally handles frequent duplicate waiting times (e.g., standard 15, 30 min intervals),
 *      eliminating degradation on non-unique datasets.
 *   3. Iterative Elimination of Recursion for O(1) stack space guarantee.
 * 
 * Time Complexities:
 *   - Average / Expected Case: O(N)
 *   - Best Case: O(N)
 *   - Worst Case: O(N^2) (probability exponentially vanishing with random pivot)
 * Space Complexity: O(1) auxiliary memory (in-place)
 */
public class RandomizedQuickselect {

    private long comparisonsCount = 0;
    private long swapsCount = 0;

    public RandomizedQuickselect() {
    }

    /**
     * Selects the k-th smallest element (0-indexed, so k=0 is minimum, k=N-1 is maximum).
     * Modifies the array in-place.
     * 
     * @param arr the input array
     * @param k 0-based target index
     * @return the k-th smallest value
     */
    public double select(double[] arr, int k) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("Array must not be null or empty");
        }
        if (k < 0 || k >= arr.length) {
            throw new IllegalArgumentException("Index k must be between 0 and " + (arr.length - 1));
        }

        int low = 0;
        int high = arr.length - 1;

        while (low <= high) {
            if (low == high) {
                return arr[low];
            }

            // Step 1: Randomized pivot selection
            int pivotIndex = ThreadLocalRandom.current().nextInt(low, high + 1);
            double pivotValue = arr[pivotIndex];

            // Step 2: Three-Way Partitioning (Dutch National Flag)
            // Partition arr[low..high] into:
            //   [low..lt-1]  < pivotValue
            //   [lt..gt]     == pivotValue
            //   [gt+1..high] > pivotValue
            int lt = low;
            int i = low;
            int gt = high;

            while (i <= gt) {
                comparisonsCount++;
                if (arr[i] < pivotValue) {
                    swap(arr, lt, i);
                    lt++;
                    i++;
                } else if (arr[i] > pivotValue) {
                    swap(arr, i, gt);
                    gt--;
                } else {
                    i++;
                }
            }

            // Step 3: Check which partition contains index k
            if (k < lt) {
                // Target is in the left sub-array (< pivot)
                high = lt - 1;
            } else if (k > gt) {
                // Target is in the right sub-array (> pivot)
                low = gt + 1;
            } else {
                // Target is within the equal partition [lt..gt]!
                return arr[k];
            }
        }

        return arr[k];
    }

    private void swap(double[] arr, int i, int j) {
        swapsCount++;
        double temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    /**
     * Calculates the Median waiting time using Quickselect.
     */
    public double calculateMedian(double[] arr) {
        if (arr == null || arr.length == 0) return 0.0;
        double[] clone = Arrays.copyOf(arr, arr.length);
        int mid = clone.length / 2;
        if (clone.length % 2 == 1) {
            return select(clone, mid);
        } else {
            // Average of two middle elements
            double m1 = select(clone, mid - 1);
            double m2 = select(clone, mid);
            return (m1 + m2) / 2.0;
        }
    }

    /**
     * Calculates the requested Percentile (0 to 100) using Quickselect.
     * E.g. percentile = 90.0 for 90th percentile.
     */
    public double calculatePercentile(double[] arr, double percentile) {
        if (arr == null || arr.length == 0) return 0.0;
        if (percentile < 0.0 || percentile > 100.0) {
            throw new IllegalArgumentException("Percentile must be between 0 and 100");
        }
        double[] clone = Arrays.copyOf(arr, arr.length);
        int k = (int) Math.round((percentile / 100.0) * (clone.length - 1));
        k = Math.max(0, Math.min(clone.length - 1, k));
        return select(clone, k);
    }

    public long getComparisonsCount() {
        return comparisonsCount;
    }

    public long getSwapsCount() {
        return swapsCount;
    }

    public long getTotalOperations() {
        return comparisonsCount + swapsCount;
    }

    public void resetOperationCounts() {
        this.comparisonsCount = 0;
        this.swapsCount = 0;
    }
}
