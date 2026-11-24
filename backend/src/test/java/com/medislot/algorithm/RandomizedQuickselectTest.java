package com.medislot.algorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and Correctness Tests for Randomized Quickselect (Unit 6 / CO5).
 * Tests:
 *   1. Correct k-th selection (k=0 min, k=N-1 max, median)
 *   2. Median computation for both odd and even sized arrays
 *   3. 90th and 95th percentile calculation matching sorted order
 *   4. Heavy duplicate resilience using Three-Way Partitioning (Dutch National Flag)
 *   5. Randomized differential testing against Arrays.sort across varying sizes
 */
public class RandomizedQuickselectTest {

    private RandomizedQuickselect quickselect;

    @BeforeEach
    public void setUp() {
        quickselect = new RandomizedQuickselect();
    }

    @Test
    @DisplayName("Test 1: Exact k-th Element Selection on Unsorted Data")
    public void testSelectKthElement() {
        double[] data = {45.0, 12.0, 89.0, 3.0, 27.0, 64.0, 18.0};
        double[] sorted = Arrays.copyOf(data, data.length);
        Arrays.sort(sorted);

        // Test k=0 (minimum = 3.0)
        double min = quickselect.select(Arrays.copyOf(data, data.length), 0);
        assertEquals(sorted[0], min, 0.001);

        // Test k=N-1 (maximum = 89.0)
        double max = quickselect.select(Arrays.copyOf(data, data.length), data.length - 1);
        assertEquals(sorted[sorted.length - 1], max, 0.001);

        // Test all intermediate indices
        for (int k = 0; k < data.length; k++) {
            double val = quickselect.select(Arrays.copyOf(data, data.length), k);
            assertEquals(sorted[k], val, 0.001, "Mismatch at rank " + k);
        }
    }

    @Test
    @DisplayName("Test 2: Median Computation on Odd and Even Length Arrays")
    public void testMedianCalculation() {
        // Odd length: [10, 20, 30, 40, 50] -> median is 30
        double[] odd = {50.0, 10.0, 40.0, 20.0, 30.0};
        assertEquals(30.0, quickselect.calculateMedian(odd), 0.001);

        // Even length: [10, 20, 30, 40] -> median is (20 + 30) / 2 = 25
        double[] even = {40.0, 10.0, 30.0, 20.0};
        assertEquals(25.0, quickselect.calculateMedian(even), 0.001);
    }

    @Test
    @DisplayName("Test 3: 90th and 95th Percentile Calculations")
    public void testPercentileCalculations() {
        // 100 elements: 1.0 to 100.0
        double[] data = new double[100];
        for (int i = 0; i < 100; i++) {
            data[i] = i + 1.0;
        }
        // Shuffle
        Random rand = new Random(42);
        for (int i = data.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            double temp = data[i];
            data[i] = data[j];
            data[j] = temp;
        }

        FullSortBaseline baseline = new FullSortBaseline();
        double expectedP90 = baseline.calculatePercentile(data, 90.0);
        double expectedP95 = baseline.calculatePercentile(data, 95.0);

        double actualP90 = quickselect.calculatePercentile(data, 90.0);
        double actualP95 = quickselect.calculatePercentile(data, 95.0);

        assertEquals(expectedP90, actualP90, 0.001, "90th percentile must match sorted rank");
        assertEquals(expectedP95, actualP95, 0.001, "95th percentile must match sorted rank");
    }

    @Test
    @DisplayName("Test 4: Three-Way Partitioning Duplicate Handling")
    public void testHeavyDuplicateArray() {
        // Real waiting times often have large duplicate sets (e.g., standard 15 min or 30 min slots)
        double[] duplicateData = new double[500];
        Arrays.fill(duplicateData, 25.0); // all identical
        duplicateData[0] = 5.0;
        duplicateData[499] = 90.0;

        // Must execute in O(N) without stack overflow or performance degradation
        double median = quickselect.calculateMedian(duplicateData);
        assertEquals(25.0, median, 0.001);

        double min = quickselect.select(duplicateData, 0);
        assertEquals(5.0, min, 0.001);

        double max = quickselect.select(duplicateData, duplicateData.length - 1);
        assertEquals(90.0, max, 0.001);
    }

    @Test
    @DisplayName("Test 5: Randomized Comparison with Arrays.sort over Multiple Sizes")
    public void testRandomizedComparisonWithDualPivotSort() {
        Random rand = new Random(98765);
        int[] sizes = {50, 200, 1000};

        for (int size : sizes) {
            double[] arr = new double[size];
            for (int i = 0; i < size; i++) {
                arr[i] = rand.nextDouble() * 100.0;
            }

            double[] sorted = Arrays.copyOf(arr, arr.length);
            Arrays.sort(sorted);

            // Test 10 random k queries for each dataset size
            for (int q = 0; q < 10; q++) {
                int k = rand.nextInt(size);
                double result = quickselect.select(Arrays.copyOf(arr, arr.length), k);
                assertEquals(sorted[k], result, 0.0001,
                        "Mismatch for size=" + size + " at k=" + k);
            }
        }
    }
}
