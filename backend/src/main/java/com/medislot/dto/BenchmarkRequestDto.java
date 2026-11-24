package com.medislot.dto;

import java.util.List;

public class BenchmarkRequestDto {
    private String algorithmCategory; // "SEGMENT_TREE", "BINARY_HEAP", "QUICKSELECT", "ALL"
    private int datasetSize = 1000;    // 100, 1000, 10000
    private int iterations = 5;        // warmups + measurement runs

    public BenchmarkRequestDto() {}

    public BenchmarkRequestDto(String algorithmCategory, int datasetSize) {
        this.algorithmCategory = algorithmCategory;
        this.datasetSize = datasetSize;
    }

    public String getAlgorithmCategory() { return algorithmCategory; }
    public void setAlgorithmCategory(String algorithmCategory) { this.algorithmCategory = algorithmCategory; }

    public int getDatasetSize() { return datasetSize; }
    public void setDatasetSize(int datasetSize) { this.datasetSize = datasetSize; }

    public int getIterations() { return iterations; }
    public void setIterations(int iterations) { this.iterations = iterations; }
}
