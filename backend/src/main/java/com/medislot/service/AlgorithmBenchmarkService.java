package com.medislot.service;

import com.medislot.algorithm.*;
import com.medislot.dto.BenchmarkRequestDto;
import com.medislot.dto.BenchmarkResultDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Service driving real algorithmic benchmarks in the Algorithm Performance Lab.
 * Executes genuine workloads with warm-up cycles, repeated runs, and identical datasets.
 */
@Service
public class AlgorithmBenchmarkService {

    public BenchmarkResultDto runBenchmark(BenchmarkRequestDto request) {
        int n = request.getDatasetSize();
        if (n <= 0) n = 1000;
        // Clamp to allowed test sizes: 100, 1000, 10000
        if (n != 100 && n != 1000 && n != 10000) {
            n = 1000;
        }

        String cat = request.getAlgorithmCategory() != null ? request.getAlgorithmCategory().toUpperCase() : "ALL";
        List<BenchmarkResultDto.ExperimentComparison> experiments = new ArrayList<>();

        if ("SEGMENT_TREE".equals(cat) || "ALL".equals(cat)) {
            experiments.add(benchmarkSegmentTreeVsLinearScan(n));
        }

        if ("BINARY_HEAP".equals(cat) || "ALL".equals(cat)) {
            experiments.add(benchmarkHeapVsLinearSearch(n));
        }

        if ("QUICKSELECT".equals(cat) || "ALL".equals(cat)) {
            experiments.add(benchmarkQuickselectVsFullSort(n));
        }

        BenchmarkResultDto result = new BenchmarkResultDto();
        result.setRequestedDatasetSize(n);
        result.setExecutionTimestamp(System.currentTimeMillis());
        result.setExperiments(experiments);
        return result;
    }

    /**
     * Benchmark 1: Segment Tree vs Linear Scan
     */
    private BenchmarkResultDto.ExperimentComparison benchmarkSegmentTreeVsLinearScan(int n) {
        Random rand = new Random(42);
        int[] initialSlots = new int[n];
        for (int i = 0; i < n; i++) {
            initialSlots[i] = rand.nextDouble() < 0.65 ? 1 : 0;
        }

        // Generate query windows
        int numQueries = Math.min(2000, n * 2);
        int[][] queries = new int[numQueries][2];
        for (int i = 0; i < numQueries; i++) {
            int ql = rand.nextInt(n);
            int qr = ql + rand.nextInt(Math.max(1, n - ql));
            queries[i][0] = ql;
            queries[i][1] = qr;
        }

        // --- Warmup ---
        SegmentTree warmupTree = new SegmentTree(initialSlots);
        LinearScanBaseline warmupScan = new LinearScanBaseline(initialSlots);
        for (int i = 0; i < Math.min(500, numQueries); i++) {
            warmupTree.countAvailable(queries[i][0], queries[i][1]);
            warmupScan.countAvailable(queries[i][0], queries[i][1]);
        }

        // --- Measure Segment Tree ---
        SegmentTree segTree = new SegmentTree(initialSlots);
        segTree.resetOperationCount();
        long startTree = System.nanoTime();
        int treeSum = 0;
        for (int i = 0; i < numQueries; i++) {
            treeSum += segTree.countAvailable(queries[i][0], queries[i][1]);
            if (i % 5 == 0) {
                segTree.findNextAvailableSlot(queries[i][0], queries[i][1]);
            }
        }
        long durationTreeNanos = System.nanoTime() - startTree;
        long treeOps = segTree.getOperationCount();

        // --- Measure Linear Scan Baseline ---
        LinearScanBaseline scanBaseline = new LinearScanBaseline(initialSlots);
        scanBaseline.resetOperationCount();
        long startScan = System.nanoTime();
        int scanSum = 0;
        for (int i = 0; i < numQueries; i++) {
            scanSum += scanBaseline.countAvailable(queries[i][0], queries[i][1]);
            if (i % 5 == 0) {
                scanBaseline.findNextAvailableSlot(queries[i][0], queries[i][1]);
            }
        }
        long durationScanNanos = System.nanoTime() - startScan;
        long scanOps = scanBaseline.getOperationCount();

        boolean matched = (treeSum == scanSum);

        double treeMicros = Math.max(0.1, durationTreeNanos / 1000.0);
        double scanMicros = Math.max(0.1, durationScanNanos / 1000.0);
        double speedup = Math.round((scanMicros / treeMicros) * 100.0) / 100.0;
        double opsReduction = Math.round(((double) (scanOps - treeOps) / Math.max(1, scanOps)) * 1000.0) / 10.0;

        BenchmarkResultDto.ExperimentComparison exp = new BenchmarkResultDto.ExperimentComparison();
        exp.setCategory("Availability Management");
        exp.setCustomAlgorithm("Segment Tree (Custom Unit 3)");
        exp.setBaselineAlgorithm("Linear Array Scan");
        exp.setDatasetSize(n);
        exp.setCustomTimeMicros(Math.round(treeMicros * 10.0) / 10.0);
        exp.setBaselineTimeMicros(Math.round(scanMicros * 10.0) / 10.0);
        exp.setCustomOperations(treeOps);
        exp.setBaselineOperations(scanOps);
        exp.setTimeComplexityCustom("O(log N) query / O(log N) update");
        exp.setTimeComplexityBaseline("O(N) query / O(1) update");
        exp.setSpaceComplexityCustom("O(N) - 4N tree nodes");
        exp.setSpaceComplexityBaseline("O(N) - flat array");
        exp.setSpeedup(speedup);
        exp.setOperationsReductionPercent(opsReduction);
        exp.setCorrectnessMatched(matched);
        exp.setCorrectnessDetails(matched ? "Identical total available count (" + treeSum + ") across " + numQueries + " queries"
                                          : "Discrepancy detected: Tree=" + treeSum + ", Scan=" + scanSum);
        return exp;
    }

    /**
     * Benchmark 2: Custom Binary Min Heap vs Linear Search
     */
    private BenchmarkResultDto.ExperimentComparison benchmarkHeapVsLinearSearch(int n) {
        Random rand = new Random(101);
        int opsCount = Math.min(3000, n);

        List<CustomBinaryMinHeap.HeapNode> nodes = new ArrayList<>(opsCount);
        for (int i = 0; i < opsCount; i++) {
            int priority = rand.nextDouble() < 0.3 ? 1 : 2; // 30% Urgent, 70% Normal
            long timestamp = 1000000L + rand.nextInt(500000);
            nodes.add(new CustomBinaryMinHeap.HeapNode(
                    (long) i, (long) (100 + i), "Patient " + i, 1L, "CARDIOLOGY", priority, timestamp, "2026-11-25", "ANY"));
        }

        // --- Warmup ---
        CustomBinaryMinHeap warmupHeap = new CustomBinaryMinHeap(100);
        LinearSearchHeapBaseline warmupLinear = new LinearSearchHeapBaseline();
        for (int i = 0; i < Math.min(100, nodes.size()); i++) {
            warmupHeap.insert(nodes.get(i));
            warmupLinear.insert(nodes.get(i));
        }

        // --- Measure Custom Binary Min Heap ---
        CustomBinaryMinHeap heap = new CustomBinaryMinHeap(opsCount);
        heap.resetOperationCount();
        long startHeap = System.nanoTime();
        for (CustomBinaryMinHeap.HeapNode node : nodes) {
            heap.insert(node);
        }
        List<Long> heapExtractedIds = new ArrayList<>();
        while (!heap.isEmpty()) {
            CustomBinaryMinHeap.HeapNode top = heap.extractMin();
            if (top != null) heapExtractedIds.add(top.getEntryId());
        }
        long durationHeapNanos = System.nanoTime() - startHeap;
        long heapOps = heap.getOperationCount();

        // --- Measure Linear Search Baseline ---
        LinearSearchHeapBaseline linearList = new LinearSearchHeapBaseline();
        linearList.resetOperationCount();
        long startLinear = System.nanoTime();
        for (CustomBinaryMinHeap.HeapNode node : nodes) {
            linearList.insert(node);
        }
        List<Long> linearExtractedIds = new ArrayList<>();
        while (!linearList.isEmpty()) {
            CustomBinaryMinHeap.HeapNode top = linearList.extractMin();
            if (top != null) linearExtractedIds.add(top.getEntryId());
        }
        long durationLinearNanos = System.nanoTime() - startLinear;
        long linearOps = linearList.getOperationCount();

        boolean matched = heapExtractedIds.equals(linearExtractedIds);

        double heapMicros = Math.max(0.1, durationHeapNanos / 1000.0);
        double linearMicros = Math.max(0.1, durationLinearNanos / 1000.0);
        double speedup = Math.round((linearMicros / heapMicros) * 100.0) / 100.0;
        double opsReduction = Math.round(((double) (linearOps - heapOps) / Math.max(1, linearOps)) * 1000.0) / 10.0;

        BenchmarkResultDto.ExperimentComparison exp = new BenchmarkResultDto.ExperimentComparison();
        exp.setCategory("Priority Waiting-List Queue");
        exp.setCustomAlgorithm("Custom Binary Min Heap (Unit 2)");
        exp.setBaselineAlgorithm("Unsorted List + Linear Scan");
        exp.setDatasetSize(n);
        exp.setCustomTimeMicros(Math.round(heapMicros * 10.0) / 10.0);
        exp.setBaselineTimeMicros(Math.round(linearMicros * 10.0) / 10.0);
        exp.setCustomOperations(heapOps);
        exp.setBaselineOperations(linearOps);
        exp.setTimeComplexityCustom("O(log N) insert / O(log N) extractMin");
        exp.setTimeComplexityBaseline("O(1) insert / O(N) extractMin (O(N^2) total)");
        exp.setSpaceComplexityCustom("O(N) - array heap");
        exp.setSpaceComplexityBaseline("O(N) - dynamic list");
        exp.setSpeedup(speedup);
        exp.setOperationsReductionPercent(opsReduction);
        exp.setCorrectnessMatched(matched);
        exp.setCorrectnessDetails(matched ? "100% exact priority extraction order agreement on all " + opsCount + " nodes"
                                          : "Priority extraction order mismatch");
        return exp;
    }

    /**
     * Benchmark 3: Randomized Quickselect vs Full Sorting
     */
    private BenchmarkResultDto.ExperimentComparison benchmarkQuickselectVsFullSort(int n) {
        Random rand = new Random(777);
        double[] dataset = new double[n];
        // Generate realistic waiting times with duplicate clusters
        for (int i = 0; i < n; i++) {
            if (rand.nextDouble() < 0.4) {
                // clustered common wait times
                double[] common = {10.0, 15.0, 20.0, 25.0, 30.0, 40.0, 45.0};
                dataset[i] = common[rand.nextInt(common.length)];
            } else {
                dataset[i] = 5.0 + rand.nextInt(75) + rand.nextDouble();
            }
        }

        // --- Warmup ---
        RandomizedQuickselect warmupQs = new RandomizedQuickselect();
        FullSortBaseline warmupSort = new FullSortBaseline();
        warmupQs.calculateMedian(Arrays.copyOf(dataset, Math.min(200, n)));
        warmupSort.calculateMedian(Arrays.copyOf(dataset, Math.min(200, n)));

        // --- Measure Randomized Quickselect ---
        RandomizedQuickselect quickselect = new RandomizedQuickselect();
        quickselect.resetOperationCounts();
        long startQs = System.nanoTime();
        double medianQs = quickselect.calculateMedian(dataset);
        double p90Qs = quickselect.calculatePercentile(dataset, 90.0);
        double p95Qs = quickselect.calculatePercentile(dataset, 95.0);
        long durationQsNanos = System.nanoTime() - startQs;
        long qsOps = quickselect.getTotalOperations();

        // --- Measure Full Sorting Baseline ---
        FullSortBaseline fullSort = new FullSortBaseline();
        fullSort.resetOperationCounts();
        long startSort = System.nanoTime();
        double medianSort = fullSort.calculateMedian(dataset);
        double p90Sort = fullSort.calculatePercentile(dataset, 90.0);
        double p95Sort = fullSort.calculatePercentile(dataset, 95.0);
        long durationSortNanos = System.nanoTime() - startSort;
        long sortOps = fullSort.getComparisonsCount();

        boolean matched = (Math.abs(medianQs - medianSort) < 0.001) &&
                          (Math.abs(p90Qs - p90Sort) < 0.001) &&
                          (Math.abs(p95Qs - p95Sort) < 0.001);

        double qsMicros = Math.max(0.1, durationQsNanos / 1000.0);
        double sortMicros = Math.max(0.1, durationSortNanos / 1000.0);
        double speedup = Math.round((sortMicros / qsMicros) * 100.0) / 100.0;
        double opsReduction = Math.round(((double) (sortOps - qsOps) / Math.max(1, sortOps)) * 1000.0) / 10.0;

        BenchmarkResultDto.ExperimentComparison exp = new BenchmarkResultDto.ExperimentComparison();
        exp.setCategory("Order Statistics & Percentiles (CO5)");
        exp.setCustomAlgorithm("Randomized Quickselect (3-Way Partition)");
        exp.setBaselineAlgorithm("Dual-Pivot Quicksort (Arrays.sort)");
        exp.setDatasetSize(n);
        exp.setCustomTimeMicros(Math.round(qsMicros * 10.0) / 10.0);
        exp.setBaselineTimeMicros(Math.round(sortMicros * 10.0) / 10.0);
        exp.setCustomOperations(qsOps);
        exp.setBaselineOperations(sortOps);
        exp.setTimeComplexityCustom("Expected O(N) linear time");
        exp.setTimeComplexityBaseline("O(N log N) full sort");
        exp.setSpaceComplexityCustom("O(1) auxiliary (in-place iterative)");
        exp.setSpaceComplexityBaseline("O(N) cloned array allocation");
        exp.setSpeedup(speedup);
        exp.setOperationsReductionPercent(opsReduction);
        exp.setCorrectnessMatched(matched);
        exp.setCorrectnessDetails(matched ? "Identical Percentiles (Median=" + Math.round(medianQs*10)/10.0 + "m, P90=" + Math.round(p90Qs*10)/10.0 + "m, P95=" + Math.round(p95Qs*10)/10.0 + "m)"
                                          : "Percentile mismatch detected");
        return exp;
    }
}
