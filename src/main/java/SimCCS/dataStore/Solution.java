package dataStore;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author yaw and martin
 */
public class Solution {

    // Opened sources.
    private HashMap<Source, Double> sourceCaptureAmounts;   // MTCO2/yr
    private HashMap<Source, Double> sourceCosts;

    // Opened sinks.
    private HashMap<Sink, Double> sinkStorageAmounts;
    private final HashMap<Sink, Integer> sinkNumWells;
    private HashMap<Sink, Double> sinkCosts;

    // All sinks.
    private HashMap<Sink, Double> sinkCumStorageAmounts;

    // Opened edges.
    public HashMap<Edge, Double> edgeTransportAmounts;
    private HashMap<Edge, Double> edgeCosts;
    public HashMap<Edge, Double> edgeConstructCosts;
    private final HashMap<Edge, Integer> edgeTrends;
    public HashMap<Edge, Integer> PipelineSize;

    // Other.
    private double captureAmountPerYear;
    private int projectLength;
    public int projectLength_curInterval;
    private int interval;
    private int totalIntervals;
    private double crf;
    private double taxCredit;

    public Solution() {
        sourceCaptureAmounts = new HashMap<>();
        sourceCosts = new HashMap<>();
        sinkStorageAmounts = new HashMap<>();
        sinkCumStorageAmounts = new HashMap<>();
        sinkCosts = new HashMap<>();
        edgeTransportAmounts = new HashMap<>();
        edgeCosts = new HashMap<>();
        sinkNumWells = new HashMap<>();
        edgeTrends = new HashMap<>();
        edgeConstructCosts = new HashMap<>();
        PipelineSize = new HashMap<>();
    }

    public void addSourceCaptureAmount(Source src, double captureAmount) {
        if (!sourceCaptureAmounts.containsKey(src)) {
            sourceCaptureAmounts.put(src, 0.0);
        }
        sourceCaptureAmounts.put(src, sourceCaptureAmounts.get(src) + captureAmount);
        captureAmountPerYear += captureAmount;
    }

    public void addSourceCostComponent(Source src, double cost) {
        if (!sourceCosts.containsKey(src)) {
            sourceCosts.put(src, 0.0);
        }
        sourceCosts.put(src, sourceCosts.get(src) + cost);
    }

    public void addSinkStorageAmount(Sink snk, double captureAmount) {
        if (!sinkStorageAmounts.containsKey(snk)) {
            sinkStorageAmounts.put(snk, 0.0);
        }
        sinkStorageAmounts.put(snk, sinkStorageAmounts.get(snk) + captureAmount);
    }

    public void addSinkCumStorageAmounts(Sink snk, double captureAmount) {
        if (!sinkCumStorageAmounts.containsKey(snk)) {
            sinkCumStorageAmounts.put(snk, 0.0);
        }
        sinkCumStorageAmounts.put(snk, sinkCumStorageAmounts.get(snk) + captureAmount);
    }

    public void addSinkNumWells(Sink snk, int numWells) {
        if (!sinkNumWells.containsKey(snk)) {
            sinkNumWells.put(snk, 0);
        }
        sinkNumWells.put(snk, sinkNumWells.get(snk) + numWells);
    }

    public void addSinkCostComponent(Sink snk, double cost) {
        if (!sinkCosts.containsKey(snk)) {
            sinkCosts.put(snk, 0.0);
        }
        sinkCosts.put(snk, sinkCosts.get(snk) + cost);
    }

    public void addEdgeTransportAmount(Edge edg, double captureAmount) {
        if (!edgeTransportAmounts.containsKey(edg)) {
            edgeTransportAmounts.put(edg, 0.0);
        }
        edgeTransportAmounts.put(edg, edgeTransportAmounts.get(edg) + captureAmount);
    }

    public void setEdgeTrend(Edge edg, int trend) {
        edgeTrends.put(edg, trend);
    }

    public void addEdgeCostComponent(Edge edg, double cost) {
        if (!edgeCosts.containsKey(edg)) {
            edgeCosts.put(edg, 0.0);
        }
        edgeCosts.put(edg, edgeCosts.get(edg) + cost);
    }

    public void addEdgeConstructionCostComponent(Edge edg, double cost) {
        if (!edgeConstructCosts.containsKey(edg)) {
            edgeConstructCosts.put(edg, 0.0);
        }
        edgeConstructCosts.put(edg, edgeConstructCosts.get(edg) + cost);
    }

    public void setSolutionCosts(DataStorer data) {
        for (Source src : sourceCaptureAmounts.keySet()) {
            double cost = src.getOpeningCost(crf) + src.getCaptureCost() * sourceCaptureAmounts.get(
                    src);
            sourceCosts.put(src, cost);
        }

        for (Sink snk : sinkStorageAmounts.keySet()) {
            double cost = snk.getOpeningCost(crf);
            if (sinkNumWells.get(snk) != null) {
                cost += snk.getWellOpeningCost(crf) * sinkNumWells.get(snk);
            }
            cost += snk.getInjectionCost() * sinkStorageAmounts.get(snk);
            sinkCosts.put(snk, cost);
        }

        for (Edge edg : edgeTransportAmounts.keySet()) {
            LinearComponent[] linearComponents = data.getLinearComponents();
            HashMap<Edge, Double> edgeConstructionCosts = data.getGraphEdgeConstructionCosts();
            HashMap<Edge, Double> edgeRightOfWayCosts = data.getGraphEdgeRightOfWayCosts();
            int edgeTrend = edgeTrends.get(edg);
            double fixed = (linearComponents[edgeTrend].getConIntercept() * edgeConstructionCosts.get(
                    edg) + linearComponents[edgeTrend].getRowIntercept() * edgeRightOfWayCosts.get(
                    edg)) * crf;
            double variable = (linearComponents[edgeTrend].getConSlope() * edgeConstructionCosts.get(
                    edg) + linearComponents[edgeTrend].getRowSlope() * edgeRightOfWayCosts.get(edg)) * crf / 1.0;
            double cost = fixed + variable * edgeTransportAmounts.get(edg);
            edgeCosts.put(edg, cost);
        }
    }

    public HashSet<Source> getOpenedSources() {
        return new HashSet<>(sourceCaptureAmounts.keySet());
    }

    public HashSet<Sink> getOpenedSinks() {
        return new HashSet<>(sinkStorageAmounts.keySet());
    }

    public HashSet<Edge> getOpenedEdges() {
        return new HashSet<>(edgeTransportAmounts.keySet());
    }

    public HashMap<Source, Double> getSourceCaptureAmounts() {
        return sourceCaptureAmounts;
    }

    public void setSourceCaptureAmounts(HashMap<Source, Double> sourceCaptureAmounts) {
        this.sourceCaptureAmounts = sourceCaptureAmounts;
    }

    public HashMap<Source, Double> getSourceCosts() {
        return sourceCosts;
    }

    public void setSourceCosts(HashMap<Source, Double> sourceCosts) {
        this.sourceCosts = sourceCosts;
    }

    public HashMap<Sink, Double> getSinkStorageAmounts() {
        return sinkStorageAmounts;
    }

    public HashMap<Sink, Double> getSinkCumStorageAmounts() {
        return sinkCumStorageAmounts;
    }


    public void setSinkStorageAmounts(HashMap<Sink, Double> sinkStorageAmounts) {
        this.sinkStorageAmounts = sinkStorageAmounts;
    }

    public HashMap<Sink, Double> getSinkCosts() {
        return sinkCosts;
    }

    public void setSinkCosts(HashMap<Sink, Double> sinkCosts) {
        this.sinkCosts = sinkCosts;
    }

    public HashMap<Edge, Double> getEdgeTransportAmounts() {
        return edgeTransportAmounts;
    }

    public void setEdgeTransportAmounts(HashMap<Edge, Double> edgeTransportAmounts) {
        this.edgeTransportAmounts = edgeTransportAmounts;
    }

    public HashMap<Edge, Double> getEdgeCosts() {
        return edgeCosts;
    }

    public void setEdgeCosts(HashMap<Edge, Double> edgeCosts) {
        this.edgeCosts = edgeCosts;
    }

    public int getNumOpenedSources() {
        return sourceCaptureAmounts.keySet().size();
    }

    public int getNumOpenedSinks() {
        return sinkStorageAmounts.keySet().size();
    }

    public double getCaptureAmount() {
        double amountCaptured = 0;
        for (Source src : sourceCaptureAmounts.keySet()) {
            amountCaptured += sourceCaptureAmounts.get(src);
        }
        return amountCaptured * projectLength;
    }

    public double getAnnualCaptureAmount() {
        return captureAmountPerYear;
    }

    public int getNumEdgesOpened() {
        return edgeTransportAmounts.keySet().size();
    }

    public int getProjectLength() {
        return projectLength;
    }

    public void setProjectLength(int projectLength) {
        this.projectLength = projectLength;
    }

    public void setProjectLengthCurInterval(int projectLength_curInterval) {
        this.projectLength_curInterval = projectLength_curInterval;
    }
    //  --------------- Martin Ma ---------------------------------------------------
    // Determine pipeline sizes
    public void setPipelineSize(Edge edg, Double max_flowrate) {
        Integer diameter = 0;
        if (max_flowrate <= 0.19){
            diameter = 4;
        }
        else if(max_flowrate <= 0.54 && max_flowrate > 0.19){
            diameter = 6;
        }
        else if(max_flowrate <= 1.13 && max_flowrate > 0.54){
            diameter = 8;
        }
        else if(max_flowrate <= 3.25 && max_flowrate > 1.13){
            diameter = 12;
        }
        else if(max_flowrate <= 6.68 && max_flowrate > 3.25){
            diameter = 16;
        }
        else if(max_flowrate <= 12.26 && max_flowrate > 6.68){
            diameter = 20;
        }
        else if(max_flowrate <= 19.69 && max_flowrate > 12.26){
            diameter = 24;
        }
        else if(max_flowrate <= 56.46 && max_flowrate > 19.69){
            diameter = 36;
        }
        else if(max_flowrate <= 83.95 && max_flowrate > 56.46){
            diameter = 42;
        }
        else if(max_flowrate <= 119.16 && max_flowrate > 83.95){
            diameter = 48;
        }
        PipelineSize.put(edg, diameter);
    }

    public int getPipelineSize(Edge edg) {
        return PipelineSize.get(edg);
    }

    public int getProjectLengthCurInterval() {
        return projectLength_curInterval;
    }

    public double getCRF() {
        return crf;
    }

    public void setCRF(double crf) {
        this.crf = crf;
    }

    public double getTaxCredit() {
        return taxCredit;
    }

    public void setTaxCredit(double taxCredit) {
        this.taxCredit = taxCredit;
    }

    public double getTotalAnnualCaptureCost() {
        double cost = 0;
        for (Source src : sourceCosts.keySet()) {
            cost += sourceCosts.get(src);
        }
        return cost;
    }

    public double getUnitCaptureCost() {
        if (captureAmountPerYear == 0) {
            return 0;
        }
        return getTotalAnnualCaptureCost() / captureAmountPerYear;
    }

    public double getTotalAnnualStorageCost() {
        double cost = 0;
        for (Sink snk : sinkCosts.keySet()) {
            cost += sinkCosts.get(snk);
        }
        return cost;
    }

    public double getUnitStorageCost() {
        if (captureAmountPerYear == 0) {
            return 0;
        }
        return getTotalAnnualStorageCost() / captureAmountPerYear;
    }

    public double getTotalAnnualTransportCost() {
        double cost = 0;
        for (Edge edg : edgeCosts.keySet()) {
            cost += edgeCosts.get(edg);
        }
        return cost;
    }

    //  --------------- Martin Ma ---------------------------------------------------
    public double getTotalAnnualConstructionCost() {
        double cost = 0;
        for (Edge edg : edgeConstructCosts.keySet()) {
            cost += edgeConstructCosts.get(edg);
        }
        return cost;
    }

    public int getConstructedPipelines() {
        int num_NewPipelines = 0;
        num_NewPipelines = edgeConstructCosts.size();
        return num_NewPipelines;
    }

    public HashMap<Edge, Double> getedgeConstructCosts() {
        return edgeConstructCosts;
    }

    public double getUnitConstructionCost() {
        if (captureAmountPerYear == 0) {
            return 0;
        }
        return getTotalAnnualConstructionCost() / captureAmountPerYear;
    }
    // --------------------------------------------------------------------------------------------------

    public double getUnitTransportCost() {
        if (captureAmountPerYear == 0) {
            return 0;
        }
        return getTotalAnnualTransportCost() / captureAmountPerYear;
    }

    public double getTotalCost() {
        return getTotalAnnualCaptureCost() + getTotalAnnualStorageCost() + getTotalAnnualTransportCost() + getTotalAnnualConstructionCost();
    }

    public String getFilePrefix() {
        return "solution_T" + (this.getInterval() + 1);
    }

    public int getInterval() {
        return this.interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getTotalIntervals() {
        return this.totalIntervals;
    }

    public void setTotalIntervals(int totalIntervals) {
        this.totalIntervals = totalIntervals;
    }

    public double getUnitTotalCost() {
        return getUnitCaptureCost() + getUnitStorageCost() + getUnitTransportCost();
    }

    public double getPercentCaptured(Source source) {
        return sourceCaptureAmounts.get(source) / source.getProductionRate();
    }

    public double getPercentStored(Sink sink) {
        return (sinkCumStorageAmounts.get(sink)) / sink.getCapacity();
    }
}
