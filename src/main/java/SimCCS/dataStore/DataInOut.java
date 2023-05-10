package dataStore;

import com.bbn.openmap.dataAccess.shape.*;
import com.bbn.openmap.omGraphics.OMGraphic;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static utilities.Utilities.*;

/**
 * @author yaw and martin
 */

public class DataInOut {

    private String basePath;
    private String dataset;
    private String scenario;
    private DataStorer data;

    public void loadData(String basePath, String dataset, String scenario, DataStorer data) {
        this.basePath = basePath;
        this.dataset = dataset;
        this.scenario = scenario;
        this.data = data;

        System.out.println("Loading Geography...");
        loadGeography();
        System.out.println("Loading Source Data...");
        loadSources();

        // ------------- Martin Ma -----------------------------------------------------------------------------
        if (scenario.startsWith("time")) {
            System.out.println("Loading Source Evolution Data...");
            loadSourceEvo();
            System.out.println("Loading Sink credits Data...");
            loadSinkcredits();
        }

        System.out.println("Loading Exist Network Size...");
        loadExistNetworkSizes();
        // -----------------------------------------------------------------------------------------------------

        System.out.println("Loading Sink Data...");
        loadSinks();
        System.out.println("Loading Transport Data...");
        loadTransport();
        System.out.print("Loading Delaunay Pairs...");
        loadDelaunayPairs();
        System.out.print("Loading Candidate Graph...");
        loadCandidateGraph();
        System.out.println("Data Loaded.");
    }

    private void loadGeography() {
        String path = basePath + "/" + dataset + "/BaseData/CostNetwork/Construction Costs.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();
            br.readLine();

            // Read dimensions.
            String line = br.readLine();
            String[] elements = line.split("\\s+");
            data.setWidth(Integer.parseInt(elements[1]));

            line = br.readLine();
            elements = line.split("\\s+");
            data.setHeight(Integer.parseInt(elements[1]));

            // Read conversions.
            line = br.readLine();
            elements = line.split("\\s+");
            data.setLowerLeftX(Double.parseDouble(elements[1]));

            line = br.readLine();
            elements = line.split("\\s+");
            data.setLowerLeftY(Double.parseDouble(elements[1]));

            line = br.readLine();
            elements = line.split("\\s+");
            data.setCellSize(Double.valueOf(elements[1]));
        } catch (IOException e) {
            path = basePath + "/" + dataset + "/BaseData/CostNetwork/Construction Costs.csv";
            try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                br.readLine();
                br.readLine();

                // Read dimensions.
                String line = br.readLine();
                String[] elements = line.split(",");
                data.setWidth(Integer.parseInt(elements[1]));

                line = br.readLine();
                elements = line.split(",");
                data.setHeight(Integer.parseInt(elements[1]));

                // Read conversions.
                line = br.readLine();
                elements = line.split(",");
                data.setLowerLeftX(Double.parseDouble(elements[1]));

                line = br.readLine();
                elements = line.split(",");
                data.setLowerLeftY(Double.parseDouble(elements[1]));

                line = br.readLine();
                elements = line.split(",");
                data.setCellSize(Double.valueOf(elements[1]));
            } catch (IOException e2) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void loadCosts() {
        double[][] rightOfWayCosts = new double[0][0];
        double[][] constructionCosts = new double[0][0];
        double[][] routingCosts = new double[0][0];

        String path = basePath + "/" + dataset + "/BaseData/CostNetwork/Construction Costs.csv";

        // Load construction costs from csv file.
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            // Create construction costs array.
            constructionCosts = new double[data.getWidth() * data.getHeight() + 1][8];
            for (int i = 0; i < constructionCosts.length; i++) {
                for (int j = 0; j < constructionCosts[i].length; j++) {
                    constructionCosts[i][j] = Double.MAX_VALUE;
                }
            }

            for (int i = 0; i < 8; i++) {
                br.readLine();
            }

            String line = br.readLine();
            while (line != null) {
                String costLine = br.readLine();

                int currentCellIndex = 0;
                int nextCellIndex = line.indexOf(",");
                int centerCell = Integer.parseInt(line.substring(currentCellIndex, nextCellIndex));
                currentCellIndex = nextCellIndex + 1;
                int currentCostIndex = 0;
                int nextCostIndex = 0;
                boolean moreNeighbors = true;
                while (moreNeighbors) {
                    nextCellIndex = line.indexOf(",", currentCellIndex);
                    if (nextCellIndex == -1) {
                        moreNeighbors = false;
                        nextCellIndex = line.length();
                    }
                    int neighborCell = Integer.parseInt(line.substring(currentCellIndex,
                            nextCellIndex));
                    currentCellIndex = nextCellIndex + 1;

                    nextCostIndex = costLine.indexOf(",", currentCostIndex);
                    if (nextCostIndex == -1) {
                        nextCostIndex = costLine.length();
                    }
                    double cost = Double.parseDouble(costLine.substring(currentCostIndex,
                            nextCostIndex));
                    currentCostIndex = nextCostIndex + 1;

                    constructionCosts[centerCell][data.getNeighborNum(centerCell,
                            neighborCell)] = cost;
                }

                line = br.readLine();
            }

        } catch (IOException e) {
            // Load construction costs from text file.
            path = basePath + "/" + dataset + "/BaseData/CostNetwork/Construction Costs.txt";
            try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                // Create construction costs array.
                constructionCosts = new double[data.getWidth() * data.getHeight() + 1][8];
                for (int i = 0; i < constructionCosts.length; i++) {
                    for (int j = 0; j < constructionCosts[i].length; j++) {
                        constructionCosts[i][j] = Double.MAX_VALUE;
                    }
                }

                for (int i = 0; i < 8; i++) {
                    br.readLine();
                }

                String line = br.readLine();
                while (line != null) {
                    String costLine = br.readLine();
                    String[] costs = costLine.split("\\s+");
                    String[] cells = line.split("\\s+");

                    int centerCell = Integer.parseInt(cells[0]);
                    for (int i = 1; i < costs.length; i++) {
                        constructionCosts[centerCell][data.getNeighborNum(centerCell,
                                Integer.parseInt(cells[i]))] = Double.parseDouble(
                                costs[i]);
                    }
                    line = br.readLine();
                }
            } catch (IOException e2) {
                System.out.println(e2.getMessage());
            }
        }

        // Load right of way costs.  
        path = basePath + "/" + dataset + "/BaseData/CostNetwork/RightOfWay Costs.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            // Create right of way cost array.
            rightOfWayCosts = new double[data.getWidth() * data.getHeight() + 1][8];
            for (int i = 0; i < rightOfWayCosts.length; i++) {
                for (int j = 0; j < rightOfWayCosts[i].length; j++) {
                    rightOfWayCosts[i][j] = Double.MAX_VALUE;
                }
            }

            for (int i = 0; i < 8; i++) {
                br.readLine();
            }

            String line = br.readLine();
            while (line != null) {
                String costLine = br.readLine();
                String[] costs = costLine.split("\\s+");
                String[] cells = line.split("\\s+");
                int centerCell = Integer.parseInt(cells[0]);
                for (int i = 1; i < costs.length; i++) {
                    rightOfWayCosts[centerCell][data.getNeighborNum(centerCell,
                            Integer.parseInt(cells[i]))] = Double.parseDouble(
                            costs[i]);
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            rightOfWayCosts = null;
        }

        // Load routing costs.
        path = basePath + "/" + dataset + "/BaseData/CostNetwork/Routing Costs.csv";

        routingCosts = new double[data.getWidth() * data.getHeight() + 1][8];
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            for (int i = 0; i < routingCosts.length; i++) {
                for (int j = 0; j < routingCosts[i].length; j++) {
                    routingCosts[i][j] = Double.MAX_VALUE;
                }
            }

            for (int i = 0; i < 8; i++) {
                br.readLine();
            }

            String line = br.readLine();
            while (line != null) {
                String costLine = br.readLine();

                int currentCellIndex = 0;
                int nextCellIndex = line.indexOf(",");
                int centerCell = Integer.parseInt(line.substring(currentCellIndex, nextCellIndex));
                currentCellIndex = nextCellIndex + 1;
                int currentCostIndex = 0;
                int nextCostIndex = 0;
                boolean moreNeighbors = true;
                while (moreNeighbors) {
                    nextCellIndex = line.indexOf(",", currentCellIndex);
                    if (nextCellIndex == -1) {
                        moreNeighbors = false;
                        nextCellIndex = line.length();
                    }
                    int neighborCell = Integer.parseInt(line.substring(currentCellIndex,
                            nextCellIndex));
                    currentCellIndex = nextCellIndex + 1;

                    nextCostIndex = costLine.indexOf(",", currentCostIndex);
                    if (nextCostIndex == -1) {
                        nextCostIndex = costLine.length();
                    }
                    double cost = Double.parseDouble(costLine.substring(currentCostIndex,
                            nextCostIndex));
                    currentCostIndex = nextCostIndex + 1;

                    routingCosts[centerCell][data.getNeighborNum(centerCell, neighborCell)] = cost;
                }

                line = br.readLine();
            }
            System.out.println("Custom Routing Loaded.");
        } catch (IOException e) {
            for (int i = 0; i < routingCosts.length; i++) {
                for (int j = 0; j < routingCosts[i].length; j++) {
                    routingCosts[i][j] = constructionCosts[i][j];
                    if (rightOfWayCosts != null) {
                        routingCosts[i][j] += rightOfWayCosts[i][j];
                    }
                }
            }
        }

        data.setConstructionCosts(constructionCosts);
        data.setRightOfWayCosts(rightOfWayCosts);
        data.setRoutingCosts(routingCosts);
    }

    private void loadSources() {
        String sourcePath = basePath + "/" + dataset + "/Scenarios/" + scenario + "/Sources/Sources.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(sourcePath))) {
            br.readLine();
            br.readLine();
            br.readLine();
            String line = br.readLine();
            ArrayList<Source> sources = new ArrayList<>();
            while (line != null && !line.startsWith(",") && !line.startsWith(" ")) {
                String[] elements = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                Source source = new Source(data);
                source.setLabel(elements[1]);
                source.setCellNum(data.latLonToCell(Double.parseDouble(elements[8]),
                        Double.parseDouble(elements[7])));

                if (elements[3].equals("") || (isDouble(elements[3]) && Double.parseDouble(elements[3]) == 0)) {
                    if (elements[4].equals("")) {
                        source.setOpeningCost(0.0);
                    } else {
                        source.setOpeningCost(Double.parseDouble(elements[4]));
                    }

                    if (elements[5].equals("")) {
                        source.setOMCost(0.0);
                    } else {
                        source.setOMCost(Double.parseDouble(elements[5]));
                    }

                    if (elements[6].equals("")) {
                        source.setCaptureCost(0.0);
                    } else {
                        source.setCaptureCost(Double.parseDouble(elements[6]));
                    }
                } else {
                    source.setOpeningCost(0.0);
                    source.setOMCost(0.0);
                    source.setCaptureCost(Double.parseDouble(elements[3]));
                }

                source.setProductionRate(Double.parseDouble(elements[2]));
                sources.add(source);
                line = br.readLine();
            }
            data.setSources(sources.toArray(new Source[0]));
        } catch (IOException e1) {
            sourcePath = basePath + "/" + dataset + "/Scenarios/" + scenario + "/Sources/Sources.txt";
            try (BufferedReader br = new BufferedReader(new FileReader(sourcePath))) {
                br.readLine();
                String line = br.readLine();
                ArrayList<Source> sources = new ArrayList<>();
                while (line != null) {
                    String[] elements = line.split("\\s+");
                    Source source = new Source(data);
                    source.setLabel(elements[0]);
                    source.setCellNum(data.latLonToCell(Double.parseDouble(elements[7]),
                            Double.parseDouble(elements[6])));
                    source.setOpeningCost(Double.parseDouble(elements[1]));
                    source.setOMCost(Double.parseDouble(elements[2]));
                    source.setCaptureCost(Double.parseDouble(elements[3]));
                    source.setProductionRate(Double.parseDouble(elements[4]));
                    sources.add(source);
                    line = br.readLine();
                }
                data.setSources(sources.toArray(new Source[0]));
            } catch (IOException e2) {
                System.out.println(e2.getMessage());
            }
        }
    }


    // Load dynamic source open/close----------------- Martin Ma ------------------------------------------------
    private void loadSourceEvo() {
        String sourceEvoPath = basePath + "/" + dataset + "/Scenarios/" + scenario + "/Sources/SourcesEvo.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(sourceEvoPath))) {
            br.readLine();
            br.readLine();
            String line = br.readLine();
            ArrayList<SourceEvo> sourceEvos = new ArrayList<>();
            while (line != null && !line.startsWith(",") && !line.startsWith(" ")) {
                String[] elements = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                SourceEvo source_evo = new SourceEvo(data);

                int num_source_evo_interval = elements.length - 2;

                source_evo.setNumTimeInterval(num_source_evo_interval);

                ArrayList<Integer> source_evo_indicator = new ArrayList<Integer>();

                for (int i = 0; i < num_source_evo_interval; i++) {
                    source_evo_indicator.add(Integer.parseInt(elements[i + 2]));
                }
                source_evo.setSourceEvo_indicator(source_evo_indicator);
                sourceEvos.add(source_evo);
                line = br.readLine();
            }
            data.setSourceEvos(sourceEvos.toArray(new SourceEvo[0]));
        } catch (IOException e) {
            System.out.println(e.getMessage());

            Source[] sources = data.getSources();
            ArrayList<SourceEvo> sourceEvos = new ArrayList<>();
            for (int idx_src = 0; idx_src < sources.length; idx_src++) {
                SourceEvo source_evo = new SourceEvo(data);
                int num_source_evo_interval = 99;
                source_evo.setNumTimeInterval(num_source_evo_interval);
                ArrayList<Integer> source_evo_indicator = new ArrayList<Integer>();
                for (int i = 0; i < num_source_evo_interval; i++) {
                    source_evo_indicator.add(-1);
                }
                source_evo.setSourceEvo_indicator(source_evo_indicator);
                sourceEvos.add(source_evo);
            }
            data.setSourceEvos(sourceEvos.toArray(new SourceEvo[0]));
        }
    }
    // ------------------------------------------------------------------------------------------------------------


    private void loadSinks() {
        String sinkPath = basePath + "/" + dataset + "/Scenarios/" + scenario + "/Sinks/Sinks.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(sinkPath))) {
            br.readLine();
            br.readLine();
            br.readLine();
            String line = br.readLine();
            ArrayList<Sink> sinks = new ArrayList<>();
            while (line != null && !line.startsWith(",") && !line.startsWith(" ")) {
                String[] elements = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                Sink sink = new Sink(data);
                sink.setLabel(elements[1]);
                sink.setCellNum(data.latLonToCell(Double.parseDouble(elements[11]),
                        Double.parseDouble(elements[10])));

                if (elements[3].equals("") || (isDouble(elements[3]) && Double.parseDouble(elements[3]) == 0)) {
                    if (elements[4].equals("")) {
                        sink.setOpeningCost(0.0);
                    } else {
                        sink.setOpeningCost(Double.parseDouble(elements[4]));
                    }

                    if (elements[5].equals("")) {
                        sink.setOMCost(0.0);
                    } else {
                        sink.setOMCost(Double.parseDouble(elements[5]));
                    }

                    if (elements[6].equals("")) {
                        sink.setWellCapacity(0);
                    } else {
                        sink.setWellCapacity(Double.parseDouble(elements[6]));
                    }

                    if (elements[7].equals("")) {
                        sink.setWellOpeningCost(0);
                    } else {
                        sink.setWellOpeningCost(Double.parseDouble(elements[7]));
                    }

                    if (elements[8].equals("")) {
                        sink.setWellOMCost(0);
                    } else {
                        sink.setWellOMCost(Double.parseDouble(elements[8]));
                    }

                    if (elements[9].equals("")) {
                        sink.setInjectionCost(0.0);
                    } else {
                        sink.setInjectionCost(Double.parseDouble(elements[9]));
                    }
                } else {
                    sink.setOpeningCost(0.0);
                    sink.setOMCost(0.0);
                    sink.setWellCapacity(Double.MAX_VALUE);
                    sink.setWellOpeningCost(0.0);
                    sink.setWellOMCost(0.0);
                    sink.setInjectionCost(Double.parseDouble(elements[3]));
                }
                sink.setCapacity(Double.parseDouble(elements[2]));
                sinks.add(sink);
                line = br.readLine();
            }
            data.setSinks(sinks.toArray(new Sink[0]));
        } catch (IOException e1) {
            sinkPath = basePath + "/" + dataset + "/Scenarios/" + scenario + "/Sinks/Sinks.txt";
            try (BufferedReader br = new BufferedReader(new FileReader(sinkPath))) {
                br.readLine();
                String line = br.readLine();
                ArrayList<Sink> sinks = new ArrayList<>();
                while (line != null) {
                    String[] elements = line.split("\\s+");
                    Sink sink = new Sink(data);
                    sink.setLabel(elements[0]);
                    sink.setCellNum(data.latLonToCell(Double.parseDouble(elements[11]),
                            Double.parseDouble(elements[10])));
                    sink.setOpeningCost(Double.parseDouble(elements[3]));
                    sink.setOMCost(Double.parseDouble(elements[4]));
                    sink.setWellOpeningCost(Double.parseDouble(elements[6]));
                    sink.setWellOMCost(Double.parseDouble(elements[7]));
                    sink.setInjectionCost(Double.parseDouble(elements[8]));
                    sink.setWellCapacity(Double.parseDouble(elements[5]));
                    sink.setCapacity(Double.parseDouble(elements[2]));
                    sinks.add(sink);
                    line = br.readLine();
                }
                data.setSinks(sinks.toArray(new Sink[0]));
            } catch (IOException e2) {
                System.out.println(e2.getMessage());
            }
        }
    }

    // Load dynamic tax credits ---------- Martin Ma -----------------------------------------------------------------
    private void loadSinkcredits() {
        String sinkCreditPath = basePath + "/" + dataset + "/Scenarios/" + scenario + "/Sinks/Sinks credits.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(sinkCreditPath))) {
            br.readLine();
            String line = br.readLine();
            ArrayList<Sinkcredit> sinkcredits = new ArrayList<>();
            while (line != null) {
                Sinkcredit sinkcredit = new Sinkcredit(data);
                String[] elements = line.split("\\s+");
                sinkcredit.setId_sinkcredit(Integer.parseInt(elements[0]));
                sinkcredit.setSinkcredit(Double.parseDouble(elements[1]));
                sinkcredits.add(sinkcredit);
                line = br.readLine();
            }
            data.setSinkcredits(sinkcredits.toArray(new Sinkcredit[0]));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            ArrayList<Sinkcredit> sinkcredits = new ArrayList<>();
            int max_num_internal = 99; // Assume the maximum number of interval is 99
            for (int idx_tc = 0; idx_tc < max_num_internal; idx_tc++) {
                Sinkcredit sinkcredit = new Sinkcredit(data);
                sinkcredit.setId_sinkcredit(idx_tc + 1);
                sinkcredit.setSinkcredit(0.0);
                sinkcredits.add(sinkcredit);
            }
            data.setSinkcredits(sinkcredits.toArray(new Sinkcredit[0]));
        }
    }
    // ---------------------------------------------------------------------------------------------------------------

    private void loadTransport() {
        String transportPath = basePath + "/" + dataset + "/Scenarios/" + scenario + "/Transport/Linear.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(transportPath))) {
            br.readLine();
            String line = br.readLine();
            ArrayList<LinearComponent> linearComponents = new ArrayList<>();
            while (line != null) {
                String[] elements = line.split("\\s+");
                LinearComponent linearComponent = new LinearComponent(data);
                linearComponent.setConSlope(Double.parseDouble(elements[1]));
                linearComponent.setConIntercept(Double.parseDouble(elements[2]));
                if (elements.length > 3) {
                    linearComponent.setRowSlope(Double.parseDouble(elements[3]));
                    linearComponent.setRowIntercept(Double.parseDouble(elements[4]));
                }
                linearComponents.add(linearComponent);
                line = br.readLine();
            }

            // Set max pipeline capacities.
            for (int c = 0; c < linearComponents.size(); c++) {
                // double maxCap = data.getMaxAnnualCapturable();  // Do not make Double.MaxValue. CPLEX does not do well with infinity here.
                double maxCap = 119.16; // based on 48 inch pipeline
                if (c < linearComponents.size() - 1) {
                    double slope1 = linearComponents.get(c).getConSlope() + linearComponents.get(c)
                            .getRowSlope();
                    double intercept1 = linearComponents.get(c)
                            .getConIntercept() + linearComponents.get(c).getRowIntercept();
                    double slope2 = linearComponents.get(c + 1)
                            .getConSlope() + linearComponents.get(c + 1).getRowSlope();
                    double intercept2 = linearComponents.get(c + 1)
                            .getConIntercept() + linearComponents.get(c + 1).getRowIntercept();
                    maxCap = (intercept2 - intercept1) / (slope1 - slope2);
                }
                linearComponents.get(c).setMaxCapacity(maxCap);
            }

            data.setLinearComponents(linearComponents.toArray(new LinearComponent[0]));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // load revised candidate graph with existing pipelines-------------------- Martin Ma ------------------------------
    private void loadCandidateGraph() {
        // Check if file exists
        String candidateGraphPath = basePath + "/" + dataset + "/Scenarios/" + scenario + "/Network/CandidateNetwork/CandidateNetwork.txt";
        String existNetworkPath = basePath + "/" + dataset + "/Scenarios/" + scenario + "/Network/ExistNetwork/ExistNetwork.txt";

        if ((new File(candidateGraphPath).exists()) && !(new File(existNetworkPath).exists())) {
            // Load from file.
            try (BufferedReader br = new BufferedReader(new FileReader(candidateGraphPath))) {
                String line = br.readLine();
                // Determine data version
                int routeStarting = 5;
                if (!line.startsWith("Vertex1")) {
                    routeStarting = 4;
                    br.readLine();
                    br.readLine();
                    br.readLine();
                }
                if (!line.contains("ConCost")) {
                    routeStarting = 3;
                }
                line = br.readLine();

                HashSet<Integer> graphVertices = new HashSet<>();
                HashMap<Edge, Double> graphEdgeCosts = new HashMap<>();
                HashMap<Edge, Double> graphEdgeConstructionCosts = new HashMap<>();
                HashMap<Edge, Double> graphEdgeRightOfWayCosts = new HashMap<>();

                HashMap<Edge, int[]> graphEdgeRoutes = new HashMap<>();

                while (line != null) {
                    String[] elements = line.split("\\s+");
                    int v1 = Integer.parseInt(elements[0]);
                    int v2 = Integer.parseInt(elements[1]);
                    Edge edge = new Edge(v1, v2);
                    graphVertices.add(v1);
                    graphVertices.add(v2);
                    double cost = Double.parseDouble(elements[2]);

                    double conCost = 0;
                    double rowCost = 0;
                    if (routeStarting == 5) {
                        conCost = Double.parseDouble(elements[3]);
                        rowCost = Double.parseDouble(elements[4]);
                    }

                    ArrayList<Integer> route = new ArrayList<>();
                    for (int i = routeStarting; i < elements.length; i++) {
                        route.add(Integer.parseInt(elements[i]));
                    }

                    graphEdgeCosts.put(edge, cost);
                    graphEdgeRoutes.put(edge, convertIntegerArray(route.toArray(new Integer[0])));

                    if (routeStarting == 5) {
                        graphEdgeConstructionCosts.put(edge, conCost);
                        graphEdgeRightOfWayCosts.put(edge, rowCost);
                    }

                    // Prepare for next entry
                    line = br.readLine();
                }

                int[] vertices = new int[graphVertices.size()];
                int i = 0;
                for (int vertex : graphVertices) {
                    vertices[i++] = vertex;
                }
                Arrays.sort(vertices);

                data.setGraphVertices(vertices);
                data.setGraphEdgeCosts(graphEdgeCosts);
                data.setGraphEdgeRoutes(graphEdgeRoutes);

                if (routeStarting == 5) {
                    data.setGraphEdgeConstructionCosts(graphEdgeConstructionCosts);
                    data.setGraphEdgeRightOfWayCosts(graphEdgeRightOfWayCosts);
                }

                System.out.println();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            System.out.println("Not Exist Networks.");

        } else if ((new File(candidateGraphPath).exists()) && (new File(existNetworkPath).exists())) {
            // Both canadiate and exist networks are exist
            // Load from file.
            try (BufferedReader br = new BufferedReader(new FileReader(candidateGraphPath))) {
                String line = br.readLine();
                // Determine data version
                int routeStarting = 5;
                if (!line.startsWith("Vertex1")) {
                    routeStarting = 4;
                    br.readLine();
                    br.readLine();
                    br.readLine();
                }
                if (!line.contains("ConCost")) {
                    routeStarting = 3;
                }
                line = br.readLine();

                HashSet<Integer> graphVertices = new HashSet<>();
                HashMap<Edge, Double> graphEdgeCosts = new HashMap<>();
                HashMap<Edge, Double> graphEdgeConstructionCosts = new HashMap<>();
                HashMap<Edge, Double> graphEdgeRightOfWayCosts = new HashMap<>();
                HashMap<Edge, int[]> graphEdgeRoutes = new HashMap<>();

                // ----------------- Martin Ma ----------------------------------------------------------------
                HashMap<Edge, Integer> ExistNetworkGraphEdgeIndex = new HashMap<>();
                // --------------------------------------------------------------------------------------------
                while (line != null) {
                    String[] elements = line.split("\\s+");
                    int v1 = Integer.parseInt(elements[0]);
                    int v2 = Integer.parseInt(elements[1]);
                    Edge edge = new Edge(v1, v2);
                    graphVertices.add(v1);
                    graphVertices.add(v2);
                    double cost = Double.parseDouble(elements[2]);

                    double conCost = 0;
                    double rowCost = 0;
                    if (routeStarting == 5) {
                        conCost = Double.parseDouble(elements[3]);
                        rowCost = Double.parseDouble(elements[4]);
                    }

                    ArrayList<Integer> route = new ArrayList<>();
                    for (int i = routeStarting; i < elements.length; i++) {
                        route.add(Integer.parseInt(elements[i]));
                    }

                    graphEdgeCosts.put(edge, cost);
                    graphEdgeRoutes.put(edge, convertIntegerArray(route.toArray(new Integer[0])));

                    if (routeStarting == 5) {
                        graphEdgeConstructionCosts.put(edge, conCost);
                        graphEdgeRightOfWayCosts.put(edge, rowCost);
                    }

                    // Prepare for next entry
                    line = br.readLine();
                }

                try (BufferedReader br_e = new BufferedReader(new FileReader(existNetworkPath))) {
                    int Num_candidateNetwork = 0;
                    String line_e = br_e.readLine();
                    // Determine data version
                    routeStarting = 5;
                    if (!line_e.startsWith("Vertex1")) {
                        routeStarting = 4;
                        br_e.readLine();
                        br_e.readLine();
                        br_e.readLine();
                    }
                    if (!line_e.contains("ConCost")) {
                        routeStarting = 3;
                    }
                    line_e = br_e.readLine();

                    // read exist networwork
                    while (line_e != null) {
                        Num_candidateNetwork = Num_candidateNetwork + 1;
                        String[] elements = line_e.split("\\s+");
                        int v1 = Integer.parseInt(elements[0]);
                        int v2 = Integer.parseInt(elements[1]);
                        Edge edge = new Edge(v1, v2);
                        graphVertices.add(v1);
                        graphVertices.add(v2);
                        double cost = Double.parseDouble(elements[2]);

                        double conCost = 0;
                        double rowCost = 0;
                        if (routeStarting == 5) {
                            conCost = Double.parseDouble(elements[3]);
                            rowCost = Double.parseDouble(elements[4]);
                        }

                        ArrayList<Integer> route = new ArrayList<>();
                        for (int i = routeStarting; i < elements.length; i++) {
                            route.add(Integer.parseInt(elements[i]));
                        }

                        graphEdgeCosts.put(edge, cost);
                        graphEdgeRoutes.put(edge, convertIntegerArray(route.toArray(new Integer[0])));

                        // --------------------- Martin Ma ----------------------------------------------------------------
                        ExistNetworkGraphEdgeIndex.put(edge, Num_candidateNetwork);
                        // ------------------------------------------------------------------------------------------------

                        if (routeStarting == 5) {
                            graphEdgeConstructionCosts.put(edge, conCost);
                            graphEdgeRightOfWayCosts.put(edge, rowCost);
                        }

                        // Prepare for next entry
                        line_e = br_e.readLine();
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

                int[] vertices = new int[graphVertices.size()];
                int i = 0;
                for (int vertex : graphVertices) {
                    vertices[i++] = vertex;
                }
                Arrays.sort(vertices);

                data.setGraphVertices(vertices);
                data.setGraphEdgeCosts(graphEdgeCosts);
                data.setGraphEdgeRoutes(graphEdgeRoutes);
                // ------------------------------ Martin Ma ------------------------------------------------------------
                data.setExistNetworkGraphEdgeIndex(ExistNetworkGraphEdgeIndex);
                // -----------------------------------------------------------------------------------------------------
                if (routeStarting == 5) {
                    data.setGraphEdgeConstructionCosts(graphEdgeConstructionCosts);
                    data.setGraphEdgeRightOfWayCosts(graphEdgeRightOfWayCosts);
                }

                System.out.println();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else if (!(new File(candidateGraphPath).exists())) {
            System.out.println("Not Yet Generated.");
        }
    }

    // load existing pipeline sizes ------------------ Martin Ma -------------------------------------------------------
    private void loadExistNetworkSizes() {
        String existNetworkSizesPath = basePath + "/" + dataset + "/Scenarios/" + scenario + "/Network/ExistNetwork/ExistNetworkSizes.txt";
        if (new File(existNetworkSizesPath).exists()) {
            // Load from file.
            try (BufferedReader br = new BufferedReader(new FileReader(existNetworkSizesPath))){
                String line = br.readLine();
                line = br.readLine();
                HashSet<Integer> graphVertices = new HashSet<>();
                HashMap<Edge, Integer> existNetworkSizes = new HashMap<>();
                while (line != null) {
                    String[] elements = line.split("\\s+");
                    int v1 = Integer.parseInt(elements[0]);
                    int v2 = Integer.parseInt(elements[1]);
                    Edge edge = new Edge(v1, v2);
                    graphVertices.add(v1);
                    graphVertices.add(v2);
                    Integer size = Integer.parseInt(elements[2]);
                    existNetworkSizes.put(edge, size);
                    // Prepare for next entry
                    line = br.readLine();
                }
                data.setExistNetworkSizes(existNetworkSizes);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    private void loadDelaunayPairs() {
        // Check if file exists
        String delaunayPairsPath = basePath + "/" + dataset + "/Scenarios/" + scenario + "/Network/DelaunayNetwork/DelaunayPaths.txt";
        if (new File(delaunayPairsPath).exists()) {
            // Load from file.
            try (BufferedReader br = new BufferedReader(new FileReader(delaunayPairsPath))) {
                br.readLine();
                String line = br.readLine();

                HashSet<Edge> pairs = new HashSet<>();
                while (line != null) {
                    String[] elements = line.split("\\s+");
                    int v1 = Integer.parseInt(elements[4]);
                    int v2 = Integer.parseInt(elements[5]);
                    Edge edge = new Edge(v1, v2);
                    pairs.add(edge);

                    // Prepare for next entry
                    line = br.readLine();
                }

                data.setDelaunayPairs(pairs);
                System.out.println();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Not Yet Generated.");
        }
    }

    public void loadPriceConfiguration() {
        // Check if file exists
        String pricesPath = basePath + "/" + dataset + "/Scenarios/" + scenario + "/Configurations/priceInput.csv";
        if (new File(pricesPath).exists()) {
            // Load from file.
            try (BufferedReader br = new BufferedReader(new FileReader(pricesPath))) {
                br.readLine();
                String line = br.readLine();

                String[] elements = line.split(",");
                double min = Double.parseDouble(elements[0]);
                double max = Double.parseDouble(elements[1]);
                double step = Double.parseDouble(elements[2]);

                // Make prices array
                int num = (int) Math.floor((max - min + 1) / step);
                double[] prices = new double[num];
                for (int i = 0; i < prices.length; i++) {
                    prices[i] = min + i * step;
                }
                data.setPriceConfiguration(prices);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No price configuration file.");
        }
    }

    public void saveDelaunayPairs() {
        HashSet<Edge> delaunayPairs = data.getDelaunayPairs();

        String delaunayPairsPath = basePath + "/" + dataset + "/Scenarios/" + scenario + "/Network/DelaunayNetwork/DelaunayPaths.txt";

        // Save to file.
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(delaunayPairsPath))) {
            bw.write("#  Selected node pairs\n");
            for (Edge pair : delaunayPairs) {
                int vNum = data.sourceNum(pair.v1);
                if (vNum > -1) {
                    bw.write("SOURCE\t" + data.getSources()[vNum].getLabel() + "\t");
                } else {
                    bw.write("SINK\t" + data.getSinks()[data.sinkNum(pair.v1)].getLabel() + "\t");
                }
                vNum = data.sourceNum(pair.v2);
                if (vNum > -1) {
                    bw.write("SOURCE\t" + data.getSources()[vNum].getLabel() + "\t");
                } else {
                    bw.write("SINK\t" + data.getSinks()[data.sinkNum(pair.v2)].getLabel() + "\t");
                }
                bw.write(pair.v1 + "\t" + pair.v2 + "\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveCandidateGraph() {
        HashMap<Edge, Double> graphEdgeCosts = data.getGraphEdgeCosts();
        HashMap<Edge, int[]> graphEdgeRoutes = data.getGraphEdgeRoutes();
        HashMap<Edge, Double> graphEdgeConstructionCosts = data.getGraphEdgeConstructionCosts();
        HashMap<Edge, Double> graphEdgeRightOfWayCosts = data.getGraphEdgeRightOfWayCosts();

        String rawPathsPath = basePath + "/" + dataset + "/Scenarios/" + scenario + "/Network/CandidateNetwork/CandidateNetwork.txt";

        // Save to file.
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rawPathsPath))) {
            bw.write("Vertex1\tVertex2\tCost\tConCost\tROWCost\tCellRoute\n");
            for (Edge e : graphEdgeRoutes.keySet()) {
                bw.write(e.v1 + "\t" + e.v2 + "\t" + graphEdgeCosts.get(e) + "\t" + graphEdgeConstructionCosts.get(
                        e) + "\t" + graphEdgeRightOfWayCosts.get(e));
                int[] route = graphEdgeRoutes.get(e);
                for (int vertex : route) {
                    bw.write("\t" + vertex);
                }
                bw.write("\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public Integer parseNumberOfIntervals(String solutionPath) {
        int num_intervals = -1;
        File solFile = null;

        for (File f : new File(solutionPath).listFiles()) {
            if (f.getName().endsWith(".sol")) {
                solFile = f;
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(solFile))) {
            String line = br.readLine();
            while (!line.equals(" <variables>")) {
                line = br.readLine();
            }
            line = br.readLine();

            while (!line.equals(" </variables>")) {
                String[] partition = line.split("\"");
                String variable = partition[1];
                String[] components = variable.split("\\]\\[|\\[|\\]");

                if (components.length == 4) {
                    // p[18][1][1] --> parse the last '1' to get variable interval
                    int parsed_interval = Integer.parseInt(components[3]) + 1;
                    num_intervals = parsed_interval > num_intervals ? parsed_interval :
                            num_intervals;
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        if (num_intervals <= 0) {
            num_intervals = 1;
        }

        return num_intervals;
    }

    public Solution[] loadSolution(String solutionPath) {
        Boolean isTimeScenario = this.scenario.startsWith("time");
        Integer offset = isTimeScenario ? 1 : 0;
        double threshold = .000001;

        // Make file paths.
        File solFile = null;
        File mpsFile = null;
        for (File f : new File(solutionPath).listFiles()) {
            if (f.getName().endsWith(".sol")) {
                solFile = f;
            } else if (f.getName().endsWith(".mps")) {
                mpsFile = f;
            }
        }

        // Collect data.
        Source[] sources = data.getSources();
        Sink[] sinks = data.getSinks();
        int[] graphVertices = data.getGraphVertices();
        HashMap<Edge, Double> edgeConstructionCosts = data.getGraphEdgeConstructionCosts();

        // Make cell/index maps.
        HashMap<Source, Integer> sourceCellToIndex = new HashMap<>();
        HashMap<Integer, Source> sourceIndexToCell = new HashMap<>();
        HashMap<Sink, Integer> sinkCellToIndex = new HashMap<>();
        HashMap<Integer, Sink> sinkIndexToCell = new HashMap<>();
        HashMap<Integer, Integer> vertexCellToIndex = new HashMap<>();
        HashMap<Integer, Integer> vertexIndexToCell = new HashMap<>();
        HashMap<UnidirEdge, Integer> edgeToIndex = new HashMap<>();
        HashMap<Integer, UnidirEdge> edgeIndexToEdge = new HashMap<>();

        // Initialize cell/index maps.
        for (int i = 0; i < sources.length; i++) {
            sourceCellToIndex.put(sources[i], i);
            sourceIndexToCell.put(i, sources[i]);
        }
        for (int i = 0; i < sinks.length; i++) {
            sinkCellToIndex.put(sinks[i], i);
            sinkIndexToCell.put(i, sinks[i]);
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

        HashMap<String, Double> variableValues = new HashMap<>();
        HashMap<String, Double> variableValues_new = new HashMap<>();

        Integer numIntervals = this.parseNumberOfIntervals(solutionPath);
        Solution[] allSolutions = new Solution[numIntervals];

        for (int i = 0; i < numIntervals; i++) {
            allSolutions[i] = new Solution();
            allSolutions[i].setInterval(i);
            allSolutions[i].setTotalIntervals(numIntervals);
        }

        Solution soln = allSolutions[0];

        try (BufferedReader br = new BufferedReader(new FileReader(solFile))) {
            String line = br.readLine();
            while (!line.equals(" <variables>")) {
                line = br.readLine();
            }
            line = br.readLine();

            while (!line.equals(" </variables>")) {
                String[] partition = line.split("\"");
                String[] variable = new String[]{partition[1], partition[3], partition[5]};

                if (Double.parseDouble(variable[2]) > threshold && variable[0].charAt(0) != 'z') {
                    variableValues.put(variable[0], Double.parseDouble(variable[2]));
                    String[] components = variable[0].split("\\]\\[|\\[|\\]");

                    if (isTimeScenario && components.length > 1) {
                        Integer interval = Integer.parseInt(components[components.length - 1]);
                        soln = allSolutions[interval];
                    }

                    if (components[0].equals("a")) {
                        soln.addSourceCaptureAmount(sources[Integer.parseInt(components[1])],
                                Double.parseDouble(variable[2]));
                    } else if (components[0].equals("b")) {
                        soln.addSinkStorageAmount(sinks[Integer.parseInt(components[1])],
                                Double.parseDouble(variable[2]));
                    } else if (components[0].equals("p")) {
                        if (components.length == 4 + offset) {
                            soln.addEdgeTransportAmount(new Edge(vertexIndexToCell.get(Integer.parseInt(
                                            components[1])),
                                            vertexIndexToCell.get(Integer.parseInt(
                                                    components[2]))),
                                    Double.parseDouble(variable[2]));
                            soln.setEdgeTrend(new Edge(vertexIndexToCell.get(Integer.parseInt(
                                            components[1])),
                                            vertexIndexToCell.get(Integer.parseInt(
                                                    components[2]))),
                                    Integer.parseInt(components[3]));
                        } else {
                            UnidirEdge unidirEdge = edgeIndexToEdge.get(Integer.parseInt(components[1]));
                            soln.addEdgeTransportAmount(new Edge(unidirEdge.v1, unidirEdge.v2),
                                    Double.parseDouble(variable[2]));
                            soln.setEdgeTrend(new Edge(unidirEdge.v1, unidirEdge.v2),
                                    Integer.parseInt(components[2]));
                        }
                    } else if (components[0].equals("w")) {
                        soln.addSinkNumWells(sinks[Integer.parseInt(components[1])],
                                (int) Math.round(Double.parseDouble(variable[2])));
                    }

                    if (variable[0].equals("crf")) {
                        for (Solution s_i : allSolutions) {
                            s_i.setCRF(Double.parseDouble(variable[2]));
                        }
                    } else if (variable[0].equals("taxCreditValue")) {
                        soln.setTaxCredit(Double.parseDouble(variable[2]));
                    } else if (variable[0].contains("projectLength")) {
                        String[] tmpInterval = variable[0].split("\\]\\[|\\[|\\]");

                        if (tmpInterval.length > 1) {
                            Integer idx = Integer.parseInt(tmpInterval[1]);
                            allSolutions[idx].setProjectLength(Integer.parseInt(variable[2]));
                        } else {
                            soln.setProjectLength(Integer.parseInt(variable[2]));
                        }
                    }
                }
                line = br.readLine();
            }
            variableValues.forEach((key, value)
                            -> {if (key.charAt(0) == 'p'){
                        variableValues_new.put(key, value);
                        variableValues_new.put(key.replace('p','z'), 1.0);
                    }
                    else{
                        variableValues_new.put(key, value);
                    }
                    }
            );
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        Integer totalProjectLength = 0;
        for (int i = 0; i < numIntervals; i++) {
            totalProjectLength += allSolutions[i].getProjectLength();
            allSolutions[i].setProjectLength(totalProjectLength);
            if (i == 0){
                allSolutions[i].setProjectLengthCurInterval(allSolutions[i].getProjectLength());
            }
            else{
                allSolutions[i].setProjectLengthCurInterval(allSolutions[i].getProjectLength() - allSolutions[i-1].getProjectLength());
            }
        }

        // load costs into solution.
        try (BufferedReader br = new BufferedReader(new FileReader(mpsFile))) {
            String line = br.readLine();
            while (!line.equals("COLUMNS")) {
                line = br.readLine();
            }
            br.readLine();
            line = br.readLine();

            while (!line.equals("RHS")) {
                String[] column = line.replaceFirst("\\s+", "").split("\\s+");
                if (column[1].equals("OBJ") && variableValues_new.containsKey(column[0])) {
                    String[] components = column[0].split("\\]\\[|\\[|\\]");

                    if (isTimeScenario && components.length > 1) {
                        Integer interval = Integer.parseInt(components[components.length - 1]);
                        soln = allSolutions[interval];
                    }
                    if (column[0].charAt(0) == 's' || column[0].charAt(0) == 'a') {
                        double cost = variableValues_new.get(column[0]) * Double.parseDouble(column[2]);
                        soln.addSourceCostComponent(sources[Integer.parseInt(components[1])], cost);
                    } else if (column[0].charAt(0) == 'r' || column[0].charAt(0) == 'w' || column[0].charAt(
                            0) == 'b') {
                        double cost = variableValues_new.get(column[0]) * Double.parseDouble(column[2]);
                        soln.addSinkCostComponent(sinks[Integer.parseInt(components[1])], cost);
                    } else if (column[0].charAt(0) == 'p') {
                        double cost = variableValues_new.get(column[0]) * Double.parseDouble(column[2]);
                        if (components.length == 4 + offset) {
                            soln.addEdgeCostComponent(new Edge(vertexIndexToCell.get(Integer.parseInt(
                                    components[1])),
                                    vertexIndexToCell.get(Integer.parseInt(
                                            components[2]))), cost);
                        } else {
                            UnidirEdge unidirEdge = edgeIndexToEdge.get(Integer.parseInt(components[1]));
                            soln.addEdgeCostComponent(new Edge(unidirEdge.v1, unidirEdge.v2), cost);
                        }
                    } else if (column[0].charAt(0) == 'z'){
                        double cost = variableValues_new.get(column[0]) * Double.parseDouble(column[2]) / soln.getProjectLengthCurInterval();
                        if (components.length == 4 + offset) {
                            soln.addEdgeConstructionCostComponent(new Edge(vertexIndexToCell.get(Integer.parseInt(
                                    components[1])),
                                    vertexIndexToCell.get(Integer.parseInt(
                                            components[2]))), cost);
                        } else {
                            UnidirEdge unidirEdge = edgeIndexToEdge.get(Integer.parseInt(components[1]));
                            soln.addEdgeConstructionCostComponent(new Edge(unidirEdge.v1, unidirEdge.v2), cost);
                        }
                    }
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // --------------------------------------------------------------------------------------------------------
        for (int idx = 1; idx < numIntervals; idx++) {
            int idx2 = 0;
            while (idx2 < idx){
                for (int ll = allSolutions[idx].edgeConstructCosts.size()-1; ll >-1; ll--){
                    if (allSolutions[idx2].edgeConstructCosts.containsKey(allSolutions[idx].edgeConstructCosts.keySet().toArray()[ll]) == true){
                        allSolutions[idx].edgeConstructCosts.remove(allSolutions[idx].edgeConstructCosts.keySet().toArray()[ll]);
                    }
                }
                idx2++;
            }
        }

        for (int idx = 0; idx < numIntervals; idx++){
            if (idx == 0){
                for (Edge e : allSolutions[idx].edgeConstructCosts.keySet()) {
                    allSolutions[idx].addEdgeCumConstructCosts(e, allSolutions[idx].edgeConstructCosts.get(e)
                            * allSolutions[idx].projectLength_curInterval);
                }
            }
            else{
                for (Edge e : allSolutions[idx].edgeConstructCosts.keySet()) {
                    allSolutions[idx].addEdgeCumConstructCosts(e, allSolutions[idx].edgeConstructCosts.get(e)
                            * allSolutions[idx].projectLength_curInterval);
                }
                for (Edge e : allSolutions[idx-1].edgeCumConstructCosts.keySet()) {
                    allSolutions[idx].addEdgeCumConstructCosts(e, allSolutions[idx-1].edgeCumConstructCosts.get(e));
                }
            }
        }

        // -------------------------------------------------------------------------------------------------------
        HashMap<Edge, Double> edgeTransportAmounts_max = new HashMap<>();
        for (int idx = 0; idx < numIntervals; idx++) {
            for (Edge e : allSolutions[idx].edgeTransportAmounts.keySet()) {
                if (edgeTransportAmounts_max.containsKey(e) == true) {
                    if (edgeTransportAmounts_max.get(e) < allSolutions[idx].edgeTransportAmounts.get(e)) {
                        edgeTransportAmounts_max.replace(e, allSolutions[idx].edgeTransportAmounts.get(e));
                    }
                } else {
                    edgeTransportAmounts_max.put(e, allSolutions[idx].edgeTransportAmounts.get(e));
                }
            }
        }
        for (int idx = 0; idx < numIntervals; idx++) {
            for (Edge e : allSolutions[idx].edgeTransportAmounts.keySet()) {
                allSolutions[idx].setPipelineSize(e, edgeTransportAmounts_max.get(e));
            }
        }

        for (int idx = 0; idx < numIntervals; idx++) {
            if (idx == 0){
                for (int i = 0; i < sinks.length; i++) {
                    if (allSolutions[idx].getSinkStorageAmounts().get(sinks[i]) == null)
                    {
                        allSolutions[idx].addSinkCumStorageAmounts(sinks[i], 0.0);
                    }
                    else{
                        allSolutions[idx].addSinkCumStorageAmounts(sinks[i], allSolutions[idx].getSinkStorageAmounts().get(sinks[i])
                                * allSolutions[idx].projectLength_curInterval);
                    }
                }
            }
            else{
                for (int i = 0; i < sinks.length; i++) {
                    if (allSolutions[idx].getSinkStorageAmounts().get(sinks[i]) == null)
                    {
                        allSolutions[idx].addSinkCumStorageAmounts(sinks[i], 0.0);
                    }
                    else{
                        allSolutions[idx].addSinkCumStorageAmounts(sinks[i], allSolutions[idx].getSinkStorageAmounts().get(sinks[i])
                                * allSolutions[idx].projectLength_curInterval
                                + allSolutions[idx-1].getSinkCumStorageAmounts().get(sinks[i]));
                    }
                }
            }
        }
        return allSolutions;
    }

    public void makeShapeFiles(String path, Solution soln) {
        // Make shapefiles if they do not already exist.
        File newDir = new File(path + "/shapeFiles/");
        newDir.mkdir();

        // Collect data.
        Source[] sources = data.getSources();
        Sink[] sinks = data.getSinks();
        HashMap<Source, Double> sourceCaptureAmounts = soln.getSourceCaptureAmounts();
        HashMap<Sink, Double> sinkStorageAmounts = soln.getSinkStorageAmounts();
        HashMap<Edge, Double> edgeTransportAmounts = soln.getEdgeTransportAmounts();
        HashMap<Edge, int[]> graphEdgeRoutes = data.getGraphEdgeRoutes();
        HashMap<Edge, Double> graphEdgeLengths = data.getGraphEdgeLengths();

        // Make source shapefiles.
        EsriPointList sourceList = new EsriPointList();
        String[] sourceAttributeNames = {"Id", "X", "Y", "CO2Cptrd", "MxSpply", "PieWdge", "GensUsed", "MaxGens", "ActlCst", "TtlCst", "Name", "Cell#"};
        int[] sourceAttributeDecimals = {0, 6, 6, 6, 6, 6, 0, 0, 0, 0, 0, 0};
        DbfTableModel sourceAttributeTable = new DbfTableModel(sourceAttributeNames.length);   //12
        for (int colNum = 0; colNum < sourceAttributeNames.length; colNum++) {
            sourceAttributeTable.setColumnName(colNum, sourceAttributeNames[colNum]);
            sourceAttributeTable.setDecimalCount(colNum, (byte) sourceAttributeDecimals[colNum]);
            sourceAttributeTable.setLength(colNum, 10);
            if (sourceAttributeNames[colNum].equals("Id")) {
                sourceAttributeTable.setType(colNum, DbfTableModel.TYPE_CHARACTER);
            } else {
                sourceAttributeTable.setType(colNum, DbfTableModel.TYPE_NUMERIC);
            }
        }

        // Order sources.
        TreeMap<Double, ArrayList<Source>> orderedSources = new TreeMap<>();
        for (Source src : sources) {
            if (orderedSources.get(-src.getProductionRate()) == null) {
                orderedSources.put(-src.getProductionRate(), new ArrayList<Source>());
            }
            orderedSources.get(-src.getProductionRate()).add(src);
        }

        for (ArrayList<Source> sameCapSources : orderedSources.values()) {
            for (Source src : sameCapSources) {
                if (sourceCaptureAmounts.containsKey(src)) {
                    EsriPoint source = new EsriPoint(data.cellToLatLon(src.getCellNum())[0],
                            data.cellToLatLon(src.getCellNum())[1]);
                    sourceList.add(source);
                    // Add attributes.
                    ArrayList row = new ArrayList();
                    row.add(src.getLabel());
                    row.add(data.cellToLatLon(src.getCellNum())[1]);
                    row.add(data.cellToLatLon(src.getCellNum())[0]);
                    row.add(sourceCaptureAmounts.get(src));
                    row.add(src.getProductionRate());
                    row.add(src.getProductionRate() - sourceCaptureAmounts.get(src));

                    for (int i = 0; i < 6; i++) {
                        row.add(0);
                    }
                    sourceAttributeTable.addRecord(row);
                }
            }
        }

        EsriShapeExport writeSourceShapefiles = new EsriShapeExport(sourceList,
                sourceAttributeTable,
                newDir + "/Sources_" + Integer.toString(soln.getInterval() + 1));
        writeSourceShapefiles.export();
        makeProjectionFile("Sources_" + Integer.toString(soln.getInterval() + 1), newDir.toString());

        // Make sink shapefiles.
        EsriPointList sinkList = new EsriPointList();
        String[] sinkAttributeNames = {"Id", "X", "Y", "CO2Strd", "MxStrg", "PieWdge", "WllsUsed", "MxWlls", "ActCst", "TtlCst", "Name", "Cell#"};
        int[] sinkAttributeDecimals = {0, 6, 6, 6, 6, 6, 0, 0, 0, 0, 0, 0};
        DbfTableModel sinkAttributeTable = new DbfTableModel(sinkAttributeNames.length);   //12
        for (int colNum = 0; colNum < sinkAttributeNames.length; colNum++) {
            sinkAttributeTable.setColumnName(colNum, sinkAttributeNames[colNum]);
            sinkAttributeTable.setDecimalCount(colNum, (byte) sinkAttributeDecimals[colNum]);
            sinkAttributeTable.setLength(colNum, 10);
            if (sinkAttributeNames[colNum].equals("Id")) {
                sinkAttributeTable.setType(colNum, DbfTableModel.TYPE_CHARACTER);
            } else {
                sinkAttributeTable.setType(colNum, DbfTableModel.TYPE_NUMERIC);
            }
        }

        // Order sinks.
        TreeMap<Double, ArrayList<Sink>> orderedSinks = new TreeMap<>();
        for (Sink snk : sinks) {
            if (orderedSinks.get(-snk.getCapacity()) == null) {
                orderedSinks.put(-snk.getCapacity(), new ArrayList<Sink>());
            }
            orderedSinks.get(-snk.getCapacity()).add(snk);
        }

        for (ArrayList<Sink> sameCapSinks : orderedSinks.values()) {
            for (Sink snk : sameCapSinks) {
                if (sinkStorageAmounts.containsKey(snk)) {
                    EsriPoint source = new EsriPoint(data.cellToLatLon(snk.getCellNum())[0],
                            data.cellToLatLon(snk.getCellNum())[1]);
                    sinkList.add(source);

                    // Add attributes.
                    ArrayList row = new ArrayList();
                    row.add(snk.getLabel());
                    row.add(data.cellToLatLon(snk.getCellNum())[1]);
                    row.add(data.cellToLatLon(snk.getCellNum())[0]);
                    row.add(sinkStorageAmounts.get(snk));
                    row.add(snk.getCapacity() / soln.getProjectLength());
                    row.add(snk.getCapacity() / soln.getProjectLength() - sinkStorageAmounts.get(snk));

                    for (int i = 0; i < 6; i++) {
                        row.add(0);
                    }

                    sinkAttributeTable.addRecord(row);
                }
            }
        }

        EsriShapeExport writeSinkShapefiles = new EsriShapeExport(sinkList,
                sinkAttributeTable,
                newDir + "/Sinks_" + Integer.toString(soln.getInterval() + 1));
        writeSinkShapefiles.export();
        makeProjectionFile("Sinks_" + Integer.toString(soln.getInterval() + 1), newDir.toString());

        // Make network shapefiles.
        EsriPolylineList edgeList = new EsriPolylineList();
        String[] edgeAttributeNames = {"Id", "CapID", "CapValue", "Flow", "Cost", "LengKM", "LengROW", "LengCONS", "Variable", "PipeSize"};
        int[] edgeAttributeDecimals = {0, 0, 0, 6, 0, 3, 0, 0, 0, 1};
        DbfTableModel edgeAttributeTable = new DbfTableModel(edgeAttributeNames.length);   //12
        for (int colNum = 0; colNum < edgeAttributeNames.length; colNum++) {
            edgeAttributeTable.setColumnName(colNum, edgeAttributeNames[colNum]);
            edgeAttributeTable.setDecimalCount(colNum, (byte) edgeAttributeDecimals[colNum]);
            edgeAttributeTable.setLength(colNum, 20);
            if (edgeAttributeNames[colNum].equals("PipeSize")) {
                edgeAttributeTable.setType(colNum, DbfTableModel.TYPE_CHARACTER);
            } else {
                edgeAttributeTable.setType(colNum, DbfTableModel.TYPE_NUMERIC);
            }
        }
        for (Edge edg : soln.getOpenedEdges()) {
            // Build route
            int[] route = graphEdgeRoutes.get(edg);
            double[] routeLatLon = new double[route.length * 2];    // Route cells translated into: lat, lon, lat, lon,...
            for (int i = 0; i < route.length; i++) {
                int cell = route[i];
                routeLatLon[i * 2] = data.cellToLatLon(cell)[0];
                routeLatLon[i * 2 + 1] = data.cellToLatLon(cell)[1];
            }

            EsriPolyline edge = new EsriPolyline(routeLatLon,
                    OMGraphic.DECIMAL_DEGREES,
                    OMGraphic.LINETYPE_STRAIGHT);
            edgeList.add(edge);

            // Add attributes.
            ArrayList row = new ArrayList();

            for (int i = 0; i < 3; i++) {
                row.add(0);
            }

            row.add(edgeTransportAmounts.get(edg));
            row.add(0);
            row.add(graphEdgeLengths.get(edg));

            for (int i = 0; i < 3; i++) {
                row.add(0);
            }

            row.add(Integer.toString(soln.getPipelineSize(edg)));

            edgeAttributeTable.addRecord(row);
        }
        EsriShapeExport writeEdgeShapefiles = new EsriShapeExport(edgeList,
                edgeAttributeTable,
                newDir + "/Network_" + Integer.toString(soln.getInterval() + 1));
        writeEdgeShapefiles.export();
        makeProjectionFile("Network_" + Integer.toString(soln.getInterval() + 1), newDir.toString());
    }

    public void makeCandidateShapeFiles(String path) {
        // Make shapefiles if they do not already exist.
        File newDir = new File(path + "/shapeFiles/");
        newDir.mkdir();

        // Collect data.
        Source[] sources = data.getSources();
        Sink[] sinks = data.getSinks();
        HashMap<Edge, int[]> graphEdgeRoutes = data.getGraphEdgeRoutes();

        // Make source shapefiles.
        EsriPointList sourceList = new EsriPointList();
        String[] sourceAttributeNames = {"Id", "X", "Y"};
        int[] sourceAttributeDecimals = {0, 6, 6};
        DbfTableModel sourceAttributeTable = new DbfTableModel(sourceAttributeNames.length);   //12
        for (int colNum = 0; colNum < sourceAttributeNames.length; colNum++) {
            sourceAttributeTable.setColumnName(colNum, sourceAttributeNames[colNum]);
            sourceAttributeTable.setDecimalCount(colNum, (byte) sourceAttributeDecimals[colNum]);
            sourceAttributeTable.setLength(colNum, 10);
            if (sourceAttributeNames[colNum].equals("Id")) {
                sourceAttributeTable.setType(colNum, DbfTableModel.TYPE_CHARACTER);
            } else {
                sourceAttributeTable.setType(colNum, DbfTableModel.TYPE_NUMERIC);
            }
        }

        // Order sources.
        TreeMap<Double, ArrayList<Source>> orderedSources = new TreeMap<>();
        for (Source src : sources) {
            if (orderedSources.get(-src.getProductionRate()) == null) {
                orderedSources.put(-src.getProductionRate(), new ArrayList<Source>());
            }
            orderedSources.get(-src.getProductionRate()).add(src);
        }

        for (ArrayList<Source> sameCapSources : orderedSources.values()) {
            for (Source src : sameCapSources) {
                EsriPoint source = new EsriPoint(data.cellToLatLon(src.getCellNum())[0],
                        data.cellToLatLon(src.getCellNum())[1]);
                sourceList.add(source);

                // Add attributes.
                ArrayList row = new ArrayList();
                row.add(src.getLabel());
                row.add(data.cellToLatLon(src.getCellNum())[1]);
                row.add(data.cellToLatLon(src.getCellNum())[0]);

                sourceAttributeTable.addRecord(row);
            }
        }

        EsriShapeExport writeSourceShapefiles = new EsriShapeExport(sourceList,
                sourceAttributeTable,
                newDir + "/Sources");
        writeSourceShapefiles.export();
        makeProjectionFile("Sources", newDir.toString());

        // Make sink shapefiles.
        EsriPointList sinkList = new EsriPointList();
        String[] sinkAttributeNames = {"Id", "X", "Y"};
        int[] sinkAttributeDecimals = {0, 6, 6};
        DbfTableModel sinkAttributeTable = new DbfTableModel(sinkAttributeNames.length);   //12
        for (int colNum = 0; colNum < sinkAttributeNames.length; colNum++) {
            sinkAttributeTable.setColumnName(colNum, sinkAttributeNames[colNum]);
            sinkAttributeTable.setDecimalCount(colNum, (byte) sinkAttributeDecimals[colNum]);
            sinkAttributeTable.setLength(colNum, 10);
            if (sinkAttributeNames[colNum].equals("Id")) {
                sinkAttributeTable.setType(colNum, DbfTableModel.TYPE_CHARACTER);
            } else {
                sinkAttributeTable.setType(colNum, DbfTableModel.TYPE_NUMERIC);
            }
        }

        // Order sinks.
        TreeMap<Double, ArrayList<Sink>> orderedSinks = new TreeMap<>();
        for (Sink snk : sinks) {
            if (orderedSinks.get(-snk.getCapacity()) == null) {
                orderedSinks.put(-snk.getCapacity(), new ArrayList<Sink>());
            }
            orderedSinks.get(-snk.getCapacity()).add(snk);
        }

        for (ArrayList<Sink> sameCapSinks : orderedSinks.values()) {
            for (Sink snk : sameCapSinks) {
                EsriPoint source = new EsriPoint(data.cellToLatLon(snk.getCellNum())[0],
                        data.cellToLatLon(snk.getCellNum())[1]);
                sinkList.add(source);

                // Add attributes.
                ArrayList row = new ArrayList();
                row.add(snk.getLabel());
                row.add(data.cellToLatLon(snk.getCellNum())[1]);
                row.add(data.cellToLatLon(snk.getCellNum())[0]);

                sinkAttributeTable.addRecord(row);
            }
        }

        EsriShapeExport writeSinkShapefiles = new EsriShapeExport(sinkList,
                sinkAttributeTable,
                newDir + "/Sinks");
        writeSinkShapefiles.export();
        makeProjectionFile("Sinks", newDir.toString());

        // Make network shapefiles.
        EsriPolylineList edgeList = new EsriPolylineList();
        String[] edgeAttributeNames = {"Id"};
        int[] edgeAttributeDecimals = {0};
        DbfTableModel edgeAttributeTable = new DbfTableModel(edgeAttributeNames.length);   //12
        for (int colNum = 0; colNum < edgeAttributeNames.length; colNum++) {
            edgeAttributeTable.setColumnName(colNum, edgeAttributeNames[colNum]);
            edgeAttributeTable.setDecimalCount(colNum, (byte) edgeAttributeDecimals[colNum]);
            edgeAttributeTable.setLength(colNum, 10);
            if (edgeAttributeNames[colNum].equals("Id")) {
                edgeAttributeTable.setType(colNum, DbfTableModel.TYPE_CHARACTER);
            } else {
                edgeAttributeTable.setType(colNum, DbfTableModel.TYPE_NUMERIC);
            }
        }
        for (Edge edg : graphEdgeRoutes.keySet()) {
            // Build route
            int[] route = graphEdgeRoutes.get(edg);
            double[] routeLatLon = new double[route.length * 2];    // Route cells translated into: lat, lon, lat, lon,...
            for (int i = 0; i < route.length; i++) {
                int cell = route[i];
                routeLatLon[i * 2] = data.cellToLatLon(cell)[0];
                routeLatLon[i * 2 + 1] = data.cellToLatLon(cell)[1];
            }

            EsriPolyline edge = new EsriPolyline(routeLatLon,
                    OMGraphic.DECIMAL_DEGREES,
                    OMGraphic.LINETYPE_STRAIGHT);
            edgeList.add(edge);

            // Add attributes.
            ArrayList row = new ArrayList();
            for (int i = 0; i < 1; i++) {
                row.add(0);
            }
            edgeAttributeTable.addRecord(row);
        }

        EsriShapeExport writeEdgeShapefiles = new EsriShapeExport(edgeList,
                edgeAttributeTable,
                newDir + "/Network");
        writeEdgeShapefiles.export();
        makeProjectionFile("Network", newDir.toString());
    }

    public void makeProjectionFile(String name, String path) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path,
                name + ".prj")))) {
            bw.write(
                    "GEOGCS[\"GCS_North_American_1983\",DATUM[\"D_North_American_1983\",SPHEROID[\"GRS_1980\",6378137.0,298.257222101]],PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]]");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public void makeSolutionFile(String path, Solution soln) {
        HashMap<Edge, Double> graphEdgeLengths = data.getGraphEdgeLengths();

        try (BufferedWriter bw =
                     new BufferedWriter(new FileWriter(new File(path,
                             soln.getFilePrefix() + ".csv")))) {
            bw.write("Project Length," + soln.getProjectLength() + "\n");
            bw.write("CRF," + soln.getCRF() + "\n");
            if (soln.getTaxCredit() != 0) {
                bw.write("Tax or Credit," + soln.getTaxCredit() + "\n");
            }
            bw.write("Annual Capture Amount (MTCO2/yr)," + soln.getAnnualCaptureAmount() + "\n");
            bw.write("Total Cost ($M/yr)," + soln.getTotalCost() + "\n");
            bw.write("Capture Cost ($M/yr)," + soln.getTotalAnnualCaptureCost() + "\n");
            bw.write("Pipeline Construction Cost ($M/yr)," + soln.getTotalAnnualConstructionCost() + "\n");
            //bw.write("Transport Cost ($M/yr)," + soln.getTotalAnnualTransportCost() + "\n");
            bw.write("O&M Cost ($M/yr)," + soln.getTotalAnnualOMCost() + "\n");
            bw.write("Energy Cost ($M/yr)," + soln.getTotalAnnualEnergyCost() + "\n");
            bw.write("Storage Cost ($M/yr)," + soln.getTotalAnnualStorageCost() + "\n\n");
            bw.write("Source,Capture Amount (MTCO2/yr),Capture Cost ($M/yr)\n");
            HashMap<Source, Double> sourceCaptureAmounts = soln.getSourceCaptureAmounts();
            HashMap<Source, Double> sourceCosts = soln.getSourceCosts();
            for (Source src : sourceCaptureAmounts.keySet()) {
                bw.write(src.getLabel() + ",");
                bw.write(sourceCaptureAmounts.get(src) + ",");
                bw.write(sourceCosts.get(src) + "\n");
            }
            bw.write("\n");

            bw.write("Sink,Storage Amount (MTCO2/yr),Storage Cost ($M/yr)\n");
            HashMap<Sink, Double> sinkStorageAmounts = soln.getSinkStorageAmounts();
            HashMap<Sink, Double> sinkCosts = soln.getSinkCosts();
            for (Sink snk : sinkStorageAmounts.keySet()) {
                bw.write(snk.getLabel() + ",");
                bw.write(sinkStorageAmounts.get(snk) + ",");
                bw.write(sinkCosts.get(snk) + "\n");
            }

            bw.write("\n");

            bw.write("Edge Source,Edge Sink,Amount (MTCO2/yr),Transport Cost ($M/yr), Pipeline Construction Cost ($M/yr), Pipeline Size (inch), Length (km)\n");
            HashMap<Edge, Double> edgeTransportAmounts = soln.getEdgeTransportAmounts();
            HashMap<Edge, Double> edgeCosts = soln.getEdgeCosts();
            HashMap<Edge, Double> edgeConstructCosts = soln.getedgeConstructCosts();

            for (Edge edg : edgeTransportAmounts.keySet()) {
                bw.write(edg.v1 + "," + edg.v2 + ",");
                bw.write(edgeTransportAmounts.get(edg) + ",");
                bw.write(edgeCosts.get(edg) + ",");
                bw.write(edgeConstructCosts.get(edg) + ",");
                bw.write(soln.getPipelineSize(edg) + ",");
                bw.write(graphEdgeLengths.get(edg) + "\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    public void makeGenerateFile(String path, Solution soln) {
        File newDir = new File(path + "/genFiles");
        if (true) {
            newDir.mkdir();
            Source[] sources = data.getSources();
            Sink[] sinks = data.getSinks();
            HashMap<Source, Double> sourceCaptureAmounts = soln.getSourceCaptureAmounts();
            HashMap<Sink, Double> sinkStorageAmounts = soln.getSinkStorageAmounts();
            HashMap<Edge, Double> edgeTransportAmounts = soln.getEdgeTransportAmounts();
            HashMap<Edge, int[]> graphEdgeRoutes = data.getGraphEdgeRoutes();

            // Make Sources.
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(newDir,
                    "Sources.txt")))) {
                bw.write(
                        "ID,X,Y,CO2Cptrd,MxSpply,PieWdge,GensUsed,MaxGens,ActlCst,TtlCst,Name,Cell#\n");
                for (Source src : sources) {
                    bw.write(src.getLabel() + "," + data.cellToLatLon(src.getCellNum())[1] + "," + data.cellToLatLon(
                            src.getCellNum())[0] + ",");
                    if (sourceCaptureAmounts.containsKey(src)) {
                        bw.write(sourceCaptureAmounts.get(src) + "," + src.getProductionRate() + "," + (src.getProductionRate() - sourceCaptureAmounts.get(
                                src)));
                    } else {
                        bw.write("0," + src.getProductionRate() + "," + src.getProductionRate());
                    }
                    bw.write(",0,0,0,0,0,0\n");
                }
                bw.write("END");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            // Make Sinks.
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(newDir,
                    "Sinks.txt")))) {
                bw.write("ID,X,Y,CO2Strd,MxStrg,PieWdge,WllsUsd,MxWlls,ActCst,TtlCst,Name,Cell#\n");
                for (Sink snk : sinks) {
                    bw.write(snk.getLabel() + "," + data.cellToLatLon(snk.getCellNum())[1] + "," + data.cellToLatLon(
                            snk.getCellNum())[0] + ",");
                    if (sinkStorageAmounts.containsKey(snk)) {
                        bw.write(sinkStorageAmounts.get(snk) + "," + snk.getCapacity() + "," + (snk.getCapacity() - sinkStorageAmounts.get(
                                snk)));
                    } else {
                        bw.write("0," + snk.getCapacity() + "," + snk.getCapacity());
                    }
                    bw.write(",0,0,0,0,0,0\n");
                }
                bw.write("END");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            // Make PipeDiameters.
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(newDir,
                    "PipeDiameters.txt")))) {
                bw.write("ID,CapID,CapValue,Flow,Cost,LengKM,LengROW,LengCONS,Variable\n");
                for (Edge e : soln.getOpenedEdges()) {
                    bw.write("0,0,0," + edgeTransportAmounts.get(e) + ",0,0,0,0,0\n");
                    int[] route = graphEdgeRoutes.get(e);
                    for (int vertex : route) {
                        bw.write(round(data.cellToLatLon(vertex)[1],
                                5) + "," + round(data.cellToLatLon(vertex)[0], 5) + "\n");
                    }
                    bw.write("END\n");
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Download file from url
    public void downloadFile(String urlPath) {
        HttpURLConnection connection;

        try {
            URL url = new URL(urlPath);
            connection = (HttpURLConnection) url.openConnection();

            // change the time formate ------------------- Martin Ma ----------------------------------------------------
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmssss");
            // ----------------------------------------------------------------------------------------------------------

            Date date = new Date();
            String run = "run" + dateFormat.format(date);

            String directoryPath = basePath + "/" + dataset + "/Scenarios/" + scenario + "/Results/" + run;
            File directory = new File(directoryPath);
            directory.mkdir();

            // Copy MPS file into results file.
            String mipPath = basePath + "/" + dataset + "/Scenarios/" + scenario + "/MIP/mip.mps";
            Path from = Paths.get(mipPath);
            Path to = Paths.get(directoryPath + "/mip.mps");
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);

            FileOutputStream outputStream = null;
            InputStream inputStream = null;
            try {
                outputStream = new FileOutputStream(directoryPath + "/run0.sol");
                inputStream = connection.getInputStream();
                int BUFFER_SIZE = 10240;
                int bytesRead = -1;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                }
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                }
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
