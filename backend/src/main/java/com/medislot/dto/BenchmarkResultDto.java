package com.medislot.dto;

import java.util.List;

public class BenchmarkResultDto {

    public static class ExperimentComparison {
        private String category;
        private String customAlgorithm;
        private String baselineAlgorithm;
        private int datasetSize;
        private double customTimeMicros;
        private double baselineTimeMicros;
        private long customOperations;
        private long baselineOperations;
        private String timeComplexityCustom;
        private String timeComplexityBaseline;
        private String spaceComplexityCustom;
        private String spaceComplexityBaseline;
        private double speedup;
        private double operationsReductionPercent;
        private boolean correctnessMatched;
        private String correctnessDetails;

        public ExperimentComparison() {}

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getCustomAlgorithm() { return customAlgorithm; }
        public void setCustomAlgorithm(String customAlgorithm) { this.customAlgorithm = customAlgorithm; }

        public String getBaselineAlgorithm() { return baselineAlgorithm; }
        public void setBaselineAlgorithm(String baselineAlgorithm) { this.baselineAlgorithm = baselineAlgorithm; }

        public int getDatasetSize() { return datasetSize; }
        public void setDatasetSize(int datasetSize) { this.datasetSize = datasetSize; }

        public double getCustomTimeMicros() { return customTimeMicros; }
        public void setCustomTimeMicros(double customTimeMicros) { this.customTimeMicros = customTimeMicros; }

        public double getBaselineTimeMicros() { return baselineTimeMicros; }
        public void setBaselineTimeMicros(double baselineTimeMicros) { this.baselineTimeMicros = baselineTimeMicros; }

        public long getCustomOperations() { return customOperations; }
        public void setCustomOperations(long customOperations) { this.customOperations = customOperations; }

        public long getBaselineOperations() { return baselineOperations; }
        public void setBaselineOperations(long baselineOperations) { this.baselineOperations = baselineOperations; }

        public String getTimeComplexityCustom() { return timeComplexityCustom; }
        public void setTimeComplexityCustom(String timeComplexityCustom) { this.timeComplexityCustom = timeComplexityCustom; }

        public String getTimeComplexityBaseline() { return timeComplexityBaseline; }
        public void setTimeComplexityBaseline(String timeComplexityBaseline) { this.timeComplexityBaseline = timeComplexityBaseline; }

        public String getSpaceComplexityCustom() { return spaceComplexityCustom; }
        public void setSpaceComplexityCustom(String spaceComplexityCustom) { this.spaceComplexityCustom = spaceComplexityCustom; }

        public String getSpaceComplexityBaseline() { return spaceComplexityBaseline; }
        public void setSpaceComplexityBaseline(String spaceComplexityBaseline) { this.spaceComplexityBaseline = spaceComplexityBaseline; }

        public double getSpeedup() { return speedup; }
        public void setSpeedup(double speedup) { this.speedup = speedup; }

        public double getOperationsReductionPercent() { return operationsReductionPercent; }
        public void setOperationsReductionPercent(double operationsReductionPercent) { this.operationsReductionPercent = operationsReductionPercent; }

        public boolean isCorrectnessMatched() { return correctnessMatched; }
        public void setCorrectnessMatched(boolean correctnessMatched) { this.correctnessMatched = correctnessMatched; }

        public String getCorrectnessDetails() { return correctnessDetails; }
        public void setCorrectnessDetails(String correctnessDetails) { this.correctnessDetails = correctnessDetails; }
    }

    private int requestedDatasetSize;
    private long executionTimestamp;
    private List<ExperimentComparison> experiments;

    public BenchmarkResultDto() {}

    public int getRequestedDatasetSize() { return requestedDatasetSize; }
    public void setRequestedDatasetSize(int requestedDatasetSize) { this.requestedDatasetSize = requestedDatasetSize; }

    public long getExecutionTimestamp() { return executionTimestamp; }
    public void setExecutionTimestamp(long executionTimestamp) { this.executionTimestamp = executionTimestamp; }

    public List<ExperimentComparison> getExperiments() { return experiments; }
    public void setExperiments(List<ExperimentComparison> experiments) { this.experiments = experiments; }
}
