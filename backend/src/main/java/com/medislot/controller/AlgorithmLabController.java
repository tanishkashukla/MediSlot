package com.medislot.controller;

import com.medislot.dto.BenchmarkRequestDto;
import com.medislot.dto.BenchmarkResultDto;
import com.medislot.service.AlgorithmBenchmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Algorithm Performance Lab (Requirement #7).
 * Executes genuine benchmarks for Segment Tree vs Linear Scan,
 * Custom Binary Min Heap vs Linear Search, and Randomized Quickselect vs Full Sort.
 */
@RestController
@RequestMapping("/api/benchmark")
public class AlgorithmLabController {

    @Autowired
    private AlgorithmBenchmarkService benchmarkService;

    @PostMapping("/run")
    public ResponseEntity<BenchmarkResultDto> runBenchmark(@RequestBody(required = false) BenchmarkRequestDto request) {
        if (request == null) {
            request = new BenchmarkRequestDto("ALL", 1000);
        }
        return ResponseEntity.ok(benchmarkService.runBenchmark(request));
    }

    @GetMapping("/run")
    public ResponseEntity<BenchmarkResultDto> runBenchmarkGet(
            @RequestParam(defaultValue = "ALL") String category,
            @RequestParam(defaultValue = "1000") int datasetSize) {

        BenchmarkRequestDto req = new BenchmarkRequestDto(category, datasetSize);
        return ResponseEntity.ok(benchmarkService.runBenchmark(req));
    }
}
