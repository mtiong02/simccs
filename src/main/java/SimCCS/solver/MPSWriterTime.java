package solver;

/*
- Interval 1:
    - Range: 1 - 3
    - Capture Target: 3 MT/yr
- Interval 2:
    - Range: 4 - 6
    - Capture Target: 6 MT/yr
*/

import dataStore.DataStorer;
import dataStore.Edge;
import dataStore.TimeInterval;
import dataStore.LinearComponent;
import dataStore.Sink;
import dataStore.Source;
import dataStore.UnidirEdge;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author yaw
 */
public class MPSWriterTime {

    public static void writeCapPriceMPS(DataStorer data, double crf, double tmp1, double tmp2, String basePath, String dataset, String scenario, String modelVersion) {
        //model version: c - cap, p - price

        // Collect data
        Source[] sources = data.getSources();
        Sink[] sinks = data.getSinks();
        LinearComponent[] linearComponents = data.getLinearComponents();
        int[] graphVertices = data.getGraphVertices();
        HashMap<Integer, HashSet<Integer>> neighbors = data.getGraphNeighbors();

        HashMap<Edge, Double> edgeConstructionCosts = data.getGraphEdgeConstructionCosts();
        HashMap<Edge, Double> edgeRightOfWayCosts = data.getGraphEdgeRightOfWayCosts();
        HashMap<Source, Integer> sourceCellToIndex = new HashMap<>();
        HashMap<Integer, Source> sourceIndexToCell = new HashMap<>();
        HashMap<Sink, Integer> sinkCellToIndex = new HashMap<>();
        HashMap<Integer, Sink> sinkIndexToCell = new HashMap<>();
        HashMap<Integer, Integer> vertexCellToIndex = new HashMap<>();
        HashMap<Integer, Integer> vertexIndexToCell = new HashMap<>();
        HashMap<UnidirEdge, Integer> edgeToIndex = new HashMap<>();
        HashMap<Integer, UnidirEdge> edgeIndexToEdge = new HashMap<>();
        HashSet<Integer> sourceCells = new HashSet<>();
        HashSet<Integer> sinkCells = new HashSet<>();

        HashMap<String, HashSet<ConstraintTerm>> intVariableToConstraints = new HashMap<>();
        HashMap<String, HashSet<ConstraintTerm>> contVariableToConstraints = new HashMap<>();
        HashMap<String, String> constraintToSign = new HashMap<>();
        HashMap<String, Double> constraintRHS = new HashMap<>();
        HashMap<String, VariableBound> variableBounds = new HashMap<>();

        // Set pipe capacity factor if right of way costs are provided.
        double pipeUtilization = 1.0;
        if (linearComponents[0].getRowSlope() != 0) {
            pipeUtilization = .93;
        }

        // Initialize cell/index maps
        for (int i = 0; i < sources.length; i++) {
            sourceCellToIndex.put(sources[i], i);
            sourceIndexToCell.put(i, sources[i]);
            sourceCells.add(sources[i].getCellNum());
        }
        for (int i = 0; i < sinks.length; i++) {
            sinkCellToIndex.put(sinks[i], i);
            sinkIndexToCell.put(i, sinks[i]);
            sinkCells.add(sinks[i].getCellNum());
        }
        for (int i = 0; i < graphVertices.length; i++) {
            vertexCellToIndex.put(graphVertices[i], i);
            vertexIndexToCell.put(i, graphVertices[i]);
        }
        int index = 0;
        for (Edge e : edgeConstructionCosts.keySet()) {
            UnidirEdge e1 = new UnidirEdge(e.v1, e.v2);
            edgeToIndex.put(e1, index);
            edgeIndexToEdge.put(index, e1);
            index++;

            UnidirEdge e2 = new UnidirEdge(e.v2, e.v1);
            edgeToIndex.put(e2, index);
            edgeIndexToEdge.put(index, e2);
            index++;
        }

        TimeInterval intervals = new TimeInterval();
        //intervals.addInterval(6.0, 6.0);
        intervals.addInterval(3.0, 6.0);
        intervals.addInterval(3.0, 6.0);

        Integer num_intervals = intervals.numIntervals();

        // Build model
        // Make variables
        // Source openings
        String[][] s = new String[sources.length][num_intervals];
        for (int t = 0; t < num_intervals; t++) {
            for (int i = 0; i < sources.length; i++) {
                s[i][t] = "s[" + i + "]" + "[" + t + "]";
                variableBounds.put(s[i][t], new VariableBound("UP", 1));
            }
        }


        // Capture amounts
        String[][] a = new String[sources.length][num_intervals];
        for (int t = 0; t < num_intervals; t++) {
            for (int i = 0; i < sources.length; i++) {
                a[i][t] = "a[" + i + "][" + t + "]";
            }
        }

        // Reservoir openings
        String[][] r = new String[sinks.length][num_intervals];
        for (int t = 0; t < num_intervals; t++) {
            for (int i = 0; i < sinks.length; i++) {
                r[i][t] = "r[" + i + "][" + t + "]";
                variableBounds.put(r[i][t], new VariableBound("UP", 1));
            }
        }

        // Injection amounts
        String[][] b = new String[sinks.length][num_intervals];
        for (int t = 0; t < num_intervals; t++) {
            for (int i = 0; i < sinks.length; i++) {
                b[i][t] = "b[" + i + "][" + t + "]";
            }
        }

        // Well openings
        String[][] w = new String[sinks.length][num_intervals];
        for (int t = 0; t < num_intervals; t++) {
            for (int i = 0; i < sinks.length; i++) {
                w[i][t] = "w[" + i + "][" + t + "]";
                variableBounds.put(w[i][t], new VariableBound("LI", 0));
            }
        }

        // Pipeline between i and j with trend c
        String[][][] y = new String[edgeToIndex.size()][linearComponents.length][num_intervals];
        for (int t = 0; t < num_intervals; t++) {
            for (int e = 0; e < edgeToIndex.size(); e++) {
                for (int c = 0; c < linearComponents.length; c++) {
                    y[e][c][t] = "y[" + e + "][" + c + "][" + t + "]";
                    variableBounds.put(y[e][c][t], new VariableBound("UP", 1));
                }
            }
        }

        // Pipeline capacity
        String[][][] p = new String[edgeToIndex.size()][linearComponents.length][num_intervals];
        for (int t = 0; t < num_intervals; t++) {
            for (int e = 0; e < edgeToIndex.size(); e++) {
                for (int c = 0; c < linearComponents.length; c++) {
                    p[e][c][t] = "p[" + e + "][" + c + "][" + t + "]";
                }
            }
        }

        // Make constraints
        // Pipeline capacity constraints
        int constraintCounter = 1;
        for (int t = 0; t < num_intervals; t++) {
            for (int e = 0; e < edgeToIndex.size(); e++) {
                for (int c = 0; c < linearComponents.length; c++) {
                    String constraint = "A" + constraintCounter++;
                    if (!contVariableToConstraints.containsKey(p[e][c][t])) {
                        contVariableToConstraints.put(p[e][c][t], new HashSet<ConstraintTerm>());
                    }
                    contVariableToConstraints.get(p[e][c][t]).add(new ConstraintTerm(constraint, 1));

                    if (!intVariableToConstraints.containsKey(y[e][c][t])) {
                        intVariableToConstraints.put(y[e][c][t], new HashSet<ConstraintTerm>());
                    }
                    intVariableToConstraints.get(y[e][c][t]).add(new ConstraintTerm(constraint, -linearComponents[c].getMaxCapacity()));

                    constraintToSign.put(constraint, "L");
                    constraintRHS.put(constraint, 0.0);

                    constraint = "A" + constraintCounter++;
                    contVariableToConstraints.get(p[e][c][t]).add(new ConstraintTerm(constraint, 1));
                    constraintToSign.put(constraint, "G");
                }
            }
        }

        // No pipeline loops
        constraintCounter = 1;
        for (int t = 0; t < num_intervals; t++) {
            for (int e = 0; e < edgeToIndex.size(); e++) {
                String constraint = "B" + constraintCounter++;
                for (int c = 0; c < linearComponents.length; c++) {
                    if (!intVariableToConstraints.containsKey(y[e][c][t])) {
                        intVariableToConstraints.put(y[e][c][t], new HashSet<ConstraintTerm>());
                    }
                    intVariableToConstraints.get(y[e][c][t]).add(new ConstraintTerm(constraint, 1));
                }
                constraintToSign.put(constraint, "L");
                constraintRHS.put(constraint, 1.0);
            }
        }

        // Conservation of flow
        constraintCounter = 1;
        for (int t = 0; t < num_intervals; t++) {
            for (int src : graphVertices) {
                String constraint = "C" + constraintCounter++;
                for (int dest : neighbors.get(src)) {
                    UnidirEdge edge = new UnidirEdge(src, dest);
                    for (int c = 0; c < linearComponents.length; c++) {
                        if (!contVariableToConstraints.containsKey(p[edgeToIndex.get(edge)][c][t])) {
                            contVariableToConstraints.put(p[edgeToIndex.get(edge)][c][t], new HashSet<ConstraintTerm>());
                        }
                        contVariableToConstraints.get(p[edgeToIndex.get(edge)][c][t]).add(new ConstraintTerm(constraint, 1));
                    }
                }

                for (int dest : neighbors.get(src)) {
                    UnidirEdge edge = new UnidirEdge(dest, src);
                    for (int c = 0; c < linearComponents.length; c++) {
                        if (!contVariableToConstraints.containsKey(p[edgeToIndex.get(edge)][c][t])) {
                            contVariableToConstraints.put(p[edgeToIndex.get(edge)][c][t], new HashSet<ConstraintTerm>());
                        }
                        contVariableToConstraints.get(p[edgeToIndex.get(edge)][c][t]).add(new ConstraintTerm(constraint, -1));
                    }
                }

                // Set right hand side
                if (sourceCells.contains(src)) {
                    for (Source source : sources) {
                        if (source.getCellNum() == src) {
                            if (!contVariableToConstraints.containsKey(a[sourceCellToIndex.get(source)][t])) {
                                contVariableToConstraints.put(a[sourceCellToIndex.get(source)][t], new HashSet<ConstraintTerm>());
                            }
                            contVariableToConstraints.get(a[sourceCellToIndex.get(source)][t]).add(new ConstraintTerm(constraint, -1));
                        }
                    }
                }
                if (sinkCells.contains(src)) {
                    for (Sink sink : sinks) {
                        if (sink.getCellNum() == src) {
                            if (!contVariableToConstraints.containsKey(b[sinkCellToIndex.get(sink)][t])) {
                                contVariableToConstraints.put(b[sinkCellToIndex.get(sink)][t], new HashSet<ConstraintTerm>());
                            }
                            contVariableToConstraints.get(b[sinkCellToIndex.get(sink)][t]).add(new ConstraintTerm(constraint, 1));
                        }
                    }
                }
                constraintToSign.put(constraint, "E");
            }
        }

        // Capture capped by max production
        constraintCounter = 1;
        for (int t = 0; t < num_intervals; t++) {
            for (Source src : sources) {
                String constraint = "D" + constraintCounter++;

                if (!intVariableToConstraints.containsKey(s[sourceCellToIndex.get(src)][t])) {
                    intVariableToConstraints.put(s[sourceCellToIndex.get(src)][t], new HashSet<ConstraintTerm>());
                }
                intVariableToConstraints.get(s[sourceCellToIndex.get(src)][t]).add(new ConstraintTerm(constraint, src.getProductionRate()));

                if (!contVariableToConstraints.containsKey(a[sourceCellToIndex.get(src)][t])) {
                    contVariableToConstraints.put(a[sourceCellToIndex.get(src)][t], new HashSet<ConstraintTerm>());
                }
                contVariableToConstraints.get(a[sourceCellToIndex.get(src)][t]).add(new ConstraintTerm(constraint, -1));
                constraintToSign.put(constraint, "G");
            }
        }

        // Well injection capped by max injectivity
        constraintCounter = 1;
        for (int t = 0; t < num_intervals; t++) {
            for (Sink snk : sinks) {
                String constraint = "E" + constraintCounter++;

                if (!intVariableToConstraints.containsKey(w[sinkCellToIndex.get(snk)][t])) {
                    intVariableToConstraints.put(w[sinkCellToIndex.get(snk)][t], new HashSet<ConstraintTerm>());
                }
                intVariableToConstraints.get(w[sinkCellToIndex.get(snk)][t]).add(new ConstraintTerm(constraint, snk.getWellCapacity()));

                if (!contVariableToConstraints.containsKey(b[sinkCellToIndex.get(snk)][t])) {
                    contVariableToConstraints.put(b[sinkCellToIndex.get(snk)][t], new HashSet<ConstraintTerm>());
                }
                contVariableToConstraints.get(b[sinkCellToIndex.get(snk)][t]).add(new ConstraintTerm(constraint, -1));
                constraintToSign.put(constraint, "G");
            }
        }

        // Storage capped by max capacity
        constraintCounter = 1;
        for (int t = 0; t < num_intervals; t++) {
            for (Sink snk : sinks) {
                String constraint = "F" + constraintCounter++;
                if (!intVariableToConstraints.containsKey(r[sinkCellToIndex.get(snk)][t])) {
                    intVariableToConstraints.put(r[sinkCellToIndex.get(snk)][t], new HashSet<ConstraintTerm>());
                }

                intVariableToConstraints.get(
                        r[sinkCellToIndex.get(snk)][t]).add(
                                new ConstraintTerm(
                                        constraint,
                                        snk.getCapacity() / intervals.getYears(t)
                                )
                );

                if (!contVariableToConstraints.containsKey(b[sinkCellToIndex.get(snk)][t])) {
                    contVariableToConstraints.put(b[sinkCellToIndex.get(snk)][t], new HashSet<ConstraintTerm>());
                }

                contVariableToConstraints.get(b[sinkCellToIndex.get(snk)][t]).add(new ConstraintTerm(constraint, -1));
                constraintToSign.put(constraint, "G");
            }
        }

        String constraint;

        // Set amount of CO2 to capture
        if (modelVersion.equals("c") || modelVersion.equals("t")) {
            for (int t = 0; t < num_intervals; t++) {
                constraintCounter = 1;
                constraint = "G" + constraintCounter++;

                for (Source src : sources) {
                    if (!contVariableToConstraints.containsKey(a[sourceCellToIndex.get(src)][t])) {
                        contVariableToConstraints.put(a[sourceCellToIndex.get(src)][t], new HashSet<ConstraintTerm>());
                    }
                    contVariableToConstraints.get(a[sourceCellToIndex.get(src)][t]).add(new ConstraintTerm(constraint, 1));
                }

                constraintToSign.put(constraint, "E");
                constraintRHS.put(constraint, intervals.getValue(t));
            }
        }

        // Hardcode constants.
        for (int t = 0; t < num_intervals; t++) {
            String constr = "H[1][" + t + "]";
            if (modelVersion.equals("c")) {
                contVariableToConstraints.put("captureTarget[" + t + "]", new HashSet<ConstraintTerm>());
                contVariableToConstraints.get("captureTarget[" + t + "]").add(new ConstraintTerm(constr, 1));
            } else if (modelVersion.equals("p")) {
                contVariableToConstraints.put("taxCreditValue", new HashSet<ConstraintTerm>());
                contVariableToConstraints.get("taxCreditValue").add(new ConstraintTerm(constr, 1));
            } else if (modelVersion.equals("t")) {
                contVariableToConstraints.put("captureTarget["+t+"]", new HashSet<ConstraintTerm>());
                contVariableToConstraints.get("captureTarget["+t+"]").add(new ConstraintTerm(constr, 1));
            }
            constraintToSign.put(constr, "E");
            constraintRHS.put(constr, intervals.getValue(t));
        }

        contVariableToConstraints.put("crf", new HashSet<ConstraintTerm>());
        contVariableToConstraints.get("crf").add(new ConstraintTerm("H[2]", 1));
        constraintToSign.put("H[2]", "E");
        constraintRHS.put("H[2]", crf);

        for (int t = 0; t < num_intervals; t++) {
            String constr = "H[3][" + t + "]";
            contVariableToConstraints.put("projectLength["+t+"]", new HashSet<ConstraintTerm>());
            contVariableToConstraints.get("projectLength["+t+"]").add(new ConstraintTerm(constr, 1));
            constraintToSign.put(constr, "E");
            constraintRHS.put(constr, intervals.getYears(t));
        }

        // Make objective
        constraint = "OBJ";
        for (int t = 0; t < num_intervals; t++) {
            for (Source src : sources) {
                if (!intVariableToConstraints.containsKey(s[sourceCellToIndex.get(src)][t])) {
                    intVariableToConstraints.put(s[sourceCellToIndex.get(src)][t], new HashSet<ConstraintTerm>());
                }
                intVariableToConstraints.get(s[sourceCellToIndex.get(src)][t]).add(new ConstraintTerm(constraint, src.getOpeningCost(crf)));

                if (!contVariableToConstraints.containsKey(a[sourceCellToIndex.get(src)][t])) {
                    contVariableToConstraints.put(a[sourceCellToIndex.get(src)][t], new HashSet<ConstraintTerm>());
                }
                if (modelVersion.equals("p")) {
                    contVariableToConstraints.get(a[sourceCellToIndex.get(src)][t]).add(new ConstraintTerm(constraint, (src.getCaptureCost() + intervals.getValue(t))));
                } else {
                    contVariableToConstraints.get(a[sourceCellToIndex.get(src)][t]).add(new ConstraintTerm(constraint, src.getCaptureCost()));
                }
            }
        }

        for (int t = 0; t < num_intervals; t++) {
            for (int e = 0; e < edgeToIndex.size(); e++) {
                for (int c = 0; c < linearComponents.length; c++) {
                    UnidirEdge unidirEdge = edgeIndexToEdge.get(e);
                    Edge bidirEdge = new Edge(unidirEdge.v1, unidirEdge.v2);

                    if (!intVariableToConstraints.containsKey(y[e][c][t])) {
                        intVariableToConstraints.put(y[e][c][t], new HashSet<ConstraintTerm>());
                    }
                    double coefficient = (linearComponents[c].getConIntercept() * edgeConstructionCosts.get(bidirEdge) + linearComponents[c].getRowIntercept() * edgeRightOfWayCosts.get(bidirEdge)) * crf;
                    intVariableToConstraints.get(y[e][c][t]).add(new ConstraintTerm(constraint, coefficient));

                    if (!contVariableToConstraints.containsKey(p[e][c][t])) {
                        contVariableToConstraints.put(p[e][c][t], new HashSet<ConstraintTerm>());
                    }
                    coefficient = (linearComponents[c].getConSlope() * edgeConstructionCosts.get(bidirEdge) + linearComponents[c].getRowSlope() * edgeRightOfWayCosts.get(bidirEdge)) * crf / pipeUtilization;
                    contVariableToConstraints.get(p[e][c][t]).add(new ConstraintTerm(constraint, coefficient));
                }
            }
        }

        for (int t = 0; t < num_intervals; t++) {
            for (Sink snk : sinks) {
                if (!intVariableToConstraints.containsKey(r[sinkCellToIndex.get(snk)][t])) {
                    intVariableToConstraints.put(r[sinkCellToIndex.get(snk)][t], new HashSet<ConstraintTerm>());
                }
                intVariableToConstraints.get(r[sinkCellToIndex.get(snk)][t]).add(new ConstraintTerm(constraint, snk.getOpeningCost(crf)));

                if (!intVariableToConstraints.containsKey(w[sinkCellToIndex.get(snk)][t])) {
                    intVariableToConstraints.put(w[sinkCellToIndex.get(snk)][t], new HashSet<ConstraintTerm>());
                }
                intVariableToConstraints.get(w[sinkCellToIndex.get(snk)][t]).add(new ConstraintTerm(constraint, snk.getWellOpeningCost(crf)));

                if (!contVariableToConstraints.containsKey(b[sinkCellToIndex.get(snk)][t])) {
                    contVariableToConstraints.put(b[sinkCellToIndex.get(snk)][t], new HashSet<ConstraintTerm>());
                }
                contVariableToConstraints.get(b[sinkCellToIndex.get(snk)][t]).add(new ConstraintTerm(constraint, snk.getInjectionCost()));
            }
        }

        constraintToSign.put(constraint, "N");

        String fileName = "";
        if (modelVersion.equals("c")) {
            fileName = "cap.mps";
        } else if (modelVersion.equals("p")) {
            fileName = "price.mps";
        } else if (modelVersion.equals("t")) {
            fileName = "time.mps";
        }

        makeFile(fileName, basePath, dataset, scenario, intVariableToConstraints, contVariableToConstraints, constraintToSign, constraintRHS, variableBounds);
    }

    private static void makeFile(String fileName, String basePath, String dataset, String scenario, HashMap<String, HashSet<ConstraintTerm>> intVariableToConstraints, HashMap<String, HashSet<ConstraintTerm>> contVariableToConstraints, HashMap<String, String> constraintToSign, HashMap<String, Double> constraintRHS, HashMap<String, VariableBound> variableBounds) {
        StringBuilder problemFormulation = new StringBuilder("NAME\tSimCCS\n");

        // Identify constraints.
        problemFormulation.append("ROWS\n");
        for (String constraint : constraintToSign.keySet()) {
            problemFormulation.append("\t" + constraintToSign.get(constraint) + "\t" + constraint + "\n");
        }

        // Identify columns.
        problemFormulation.append("COLUMNS\n");
        problemFormulation.append("\tMARK0000\t'MARKER'\t'INTORG'\n");
        for (String intVar : intVariableToConstraints.keySet()) {
            for (ConstraintTerm term : intVariableToConstraints.get(intVar)) {
                problemFormulation.append("\t" + intVar + "\t" + term.constraint + "\t" + term.coefficient + "\n");
            }
        }
        problemFormulation.append("\tMARK0001\t'MARKER'\t'INTEND'\n");
        for (String contVar : contVariableToConstraints.keySet()) {
            for (ConstraintTerm term : contVariableToConstraints.get(contVar)) {
                problemFormulation.append("\t" + contVar + "\t" + term.constraint + "\t" + term.coefficient + "\n");
            }
        }

        // Identify RHSs.
        problemFormulation.append("RHS\n");
        for (String constraint : constraintRHS.keySet()) {
            problemFormulation.append("\trhs\t" + constraint + "\t" + constraintRHS.get(constraint) + "\n");
        }

        // Identify bounds.
        problemFormulation.append("BOUNDS\n");
        for (String variable : variableBounds.keySet()) {
            problemFormulation.append("\t" + variableBounds.get(variable).type + " bnd\t" + variable + "\t" + variableBounds.get(variable).bound + "\n");
        }

        // End file.
        problemFormulation.append("ENDATA");

        // Clear mip directory.
        String mipDirectory = basePath + "/" + dataset + "/Scenarios/" + scenario + "/MIP/";
        File mipFolder = new File(mipDirectory);
        File[] mips = mipFolder.listFiles();
        if (mips != null) {
            for (File mip : mips) {
                mip.delete();
            }
        }

        System.out.println("WROTE MPS TIME");

        // Save to file.
        String mipPath = mipDirectory + fileName;
        System.out.println("Writing file to: " + mipPath);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(mipPath))) {
            bw.write(problemFormulation.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static class ConstraintTerm {

        String constraint;
        double coefficient;

        public ConstraintTerm(String constraint, double coefficient) {
            this.constraint = constraint;
            this.coefficient = Math.round(coefficient * 100000.0) / 100000.0;
        }
    }

    private static class VariableBound {

        String type;
        double bound;

        public VariableBound(String type, double bound) {
            this.type = type;
            this.bound = bound;
        }
    }
}