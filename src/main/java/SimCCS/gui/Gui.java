package gui;

import java.beans.FeatureDescriptor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import dataStore.TimeInterval;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;

/**
 * @author yaw and martin 2022
 */
public class Gui extends Application {

    protected Integer selectedSolutionFileInterval;
    private NetworkDisplay displayPane;
    private ChoiceBox scenarioChoice;
    private RadioButton dispDelaunayEdges;
    private RadioButton dispCandidateNetwork;

    // ------------- Martin Ma ----------------------------------------------------------------------------
    private RadioButton dispExistNetwork;
    private ImageView imageView;
    private TitledPane ModelsContainer;
    private ChoiceBox runChoice;
    private Pane LogoPane;
    // -----------------------------------------------------------------------------------------------------

    private RadioButton sourceLabeled;
    private RadioButton sourceVisible;
    private RadioButton sinkLabeled;
    private RadioButton sinkVisible;
    private RadioButton dispCostSurface;
    private AnchorPane solutionPane;
    private TextArea messenger;
    private TitledPane timeSettingsContainer;
    private String loadedSolutionFile;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = buildGUI(stage);
        scene.getRoot().setStyle("-fx-font-family: 'serif'");
        stage.setScene(scene);
        stage.setTitle("SimCCS version 3.0: Developed by Los Alamos National Laboratory.");
        stage.show();
    }

    public Scene buildGUI(Stage stage) {
        Group group = new Group();

        // Build display pane.
        displayPane = new NetworkDisplay();
        // Offset Network Display to account for controlPane.
        displayPane.setTranslateX(220);
        // Associate scroll/navigation actions.
        SceneGestures sceneGestures = new SceneGestures(displayPane);
        displayPane.addEventFilter(MouseEvent.MOUSE_PRESSED,
                sceneGestures.getOnMousePressedEventHandler());
        displayPane.addEventFilter(MouseEvent.MOUSE_DRAGGED,
                sceneGestures.getOnMouseDraggedEventHandler());
        displayPane.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

        // Make background.
        Rectangle background = new Rectangle();
        background.setStroke(Color.WHITE);
        background.setFill(Color.WHITE);
        displayPane.getChildren().add(background);

        // Add base cost surface display.
        PixelatedImageView map = new PixelatedImageView();
        map.setPreserveRatio(true);
        map.setFitWidth(830);
        map.setFitHeight(660);
        map.setSmooth(false);
        displayPane.getChildren().add(map);

        // Action handler.
        ControlActions controlActions = new ControlActions(map, this);
        displayPane.setControlActions(controlActions);

        // Build tab background with messenger.
        AnchorPane messengerPane = new AnchorPane();
        messengerPane.setStyle("-fx-background-color: white; -fx-border-color: lightgrey;");
        messengerPane.setPrefSize(220, 80);
        messengerPane.setLayoutX(0);
        messengerPane.setLayoutY(580);
        messenger = new TextArea();
        messenger.setEditable(false);
        messenger.setWrapText(true);
        messenger.setPrefSize(192, 70);
        messenger.setLayoutX(14);
        messenger.setLayoutY(5);
        messengerPane.getChildren().add(messenger);
        controlActions.addMessenger(messenger);
        // Build tab pane and tabs.
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab dataTab = new Tab();
        dataTab.setText("Data");
        tabPane.getTabs().add(dataTab);
        Tab modelTab = new Tab();
        modelTab.setText("Model");
        tabPane.getTabs().add(modelTab);
        Tab resultsTab = new Tab();
        resultsTab.setText("Results");
        tabPane.getTabs().add(resultsTab);

        // Clear messenger when clicking in control area.
        tabPane.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                //messenger.clear();
            }
        });

        // Build data pane.
        AnchorPane dataPane = new AnchorPane();
        dataPane.setStyle("-fx-background-color: white; -fx-border-color: lightgrey;");
        dataPane.setPrefSize(220, 580);
        dataTab.setContent(dataPane);

        // Build model pane.
        AnchorPane modelPane = new AnchorPane();
        modelPane.setStyle("-fx-background-color: white; -fx-border-color: lightgrey;");
        modelPane.setPrefSize(220, 580);
        modelTab.setContent(modelPane);

        // Build results pane.
        AnchorPane resultsPane = new AnchorPane();
        resultsPane.setStyle("-fx-background-color: white; -fx-border-color: lightgrey;");
        resultsPane.setPrefSize(220, 580);
        resultsTab.setContent(resultsPane);

        // Populate data pane.
        // Build scenario selection control and add to control pane.
        scenarioChoice = new ChoiceBox();
        scenarioChoice.setPrefSize(150, 27);
        TitledPane scenarioContainer = new TitledPane("Scenario", scenarioChoice);
        scenarioContainer.setCollapsible(false);
        scenarioContainer.setPrefSize(192, 63);
        scenarioContainer.setLayoutX(14);
        scenarioContainer.setLayoutY(73);
        dataPane.getChildren().add(scenarioContainer);
        runChoice = new ChoiceBox();

        // Build display pane for SimCCS logo.
        LogoPane = new Pane();
        LogoPane.setTranslateX(220);

        try {
            //creating the image object
            FileInputStream input = new FileInputStream("SimCCS.png");
            Image image = new Image(input);
            ImageView imageView = new ImageView(image);
            imageView.setImage(image);
            imageView.setX(50);
            imageView.setY(50);
            imageView.setFitWidth(600);
            imageView.setSmooth(true);
            imageView.setCache(true);
            imageView.setPreserveRatio(true);
            imageView.fitWidthProperty().bind(stage.heightProperty());
            LogoPane.getChildren().add(imageView);
        } catch (Exception e){
            System.out.println(e.getClass());
        }

        scenarioChoice.getSelectionModel()
                .selectedItemProperty()
                .addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> selected,
                                        String oldScenario,
                                        String newScenario) {
                        controlActions.selectScenario(newScenario, background, runChoice);
                    }
                });

        // Build dataset selection control and add to control pane.
        Button openDataset = new Button("[Open Dataset]");
        openDataset.setMnemonicParsing(false);
        openDataset.setPrefSize(150, 27);
        openDataset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Open Dataset");
                File selectedDataset = directoryChooser.showDialog(stage);
                if (selectedDataset != null) {
                    openDataset.setText(selectedDataset.getName());
                    controlActions.selectDataset(selectedDataset, scenarioChoice);
                    LogoPane.getChildren().clear();
                }
            }
        });
        TitledPane datasetContainer = new TitledPane("Dataset", openDataset);
        datasetContainer.setCollapsible(false);
        datasetContainer.setPrefSize(192, 63);
        datasetContainer.setLayoutX(14);
        datasetContainer.setLayoutY(5);
        dataPane.getChildren().add(datasetContainer);

        //Build network buttons and add to control pane.
        Button candidateNetwork = new Button("Candidate Network");
        candidateNetwork.setLayoutX(27);
        candidateNetwork.setLayoutY(4);
        candidateNetwork.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                controlActions.generateCandidateGraph();
            }
        });

        AnchorPane buttonPane = new AnchorPane();
        buttonPane.setPrefSize(190, 30);
        buttonPane.setMinSize(0, 0);
        buttonPane.getChildren().addAll(candidateNetwork);

        TitledPane networkContainer = new TitledPane("Network Generation", buttonPane);
        networkContainer.setCollapsible(false);
        networkContainer.setPrefSize(192, 63);
        networkContainer.setLayoutX(14);
        networkContainer.setLayoutY(141);
        dataPane.getChildren().add(networkContainer);

        //Build display selection legend and add to control pane.
        AnchorPane selectionPane = new AnchorPane();
        selectionPane.setPrefSize(206, 237);
        selectionPane.setMinSize(0, 0);

        dispDelaunayEdges = new RadioButton("Raw Delaunay Edges");
        dispDelaunayEdges.setLayoutX(4);
        dispDelaunayEdges.setLayoutY(75); // Martin --
        selectionPane.getChildren().add(dispDelaunayEdges);

        Pane rawDelaunayLayer = new Pane();
        sceneGestures.addEntityToResize(rawDelaunayLayer);
        displayPane.getChildren().add(rawDelaunayLayer);
        controlActions.addRawDelaunayLayer(rawDelaunayLayer);
        dispDelaunayEdges.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> selected,
                                Boolean oldVal,
                                Boolean show) {
                controlActions.toggleRawDelaunayDisplay(show);
            }
        });

        dispCandidateNetwork = new RadioButton("Candidate Network");
        dispCandidateNetwork.setLayoutX(4);
//        dispCandidateNetwork.setLayoutY(106);
        dispCandidateNetwork.setLayoutY(95); // Martin --
        selectionPane.getChildren().add(dispCandidateNetwork);
        Pane candidateNetworkLayer = new Pane();
        sceneGestures.addEntityToResize(candidateNetworkLayer);
        displayPane.getChildren().add(candidateNetworkLayer);
        controlActions.addCandidateNetworkLayer(candidateNetworkLayer);
        dispCandidateNetwork.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> selected,
                                Boolean oldVal,
                                Boolean show) {
                controlActions.toggleCandidateNetworkDisplay(show);
            }
        });

        // ------------- Martin Ma -----------------------------------------------------------------------------
        dispExistNetwork = new RadioButton("Existing Network");
        dispExistNetwork.setLayoutX(4);
        dispExistNetwork.setLayoutY(115);
        selectionPane.getChildren().add(dispExistNetwork);
        Pane existNetworkLayer = new Pane();
        sceneGestures.addEntityToResize(existNetworkLayer);
        displayPane.getChildren().add(existNetworkLayer);
        controlActions.addExistNetworkLayer(existNetworkLayer);
        dispExistNetwork.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> selected,
                                Boolean oldVal,
                                Boolean show) {
                controlActions.toggleExistNetworkDisplay(show);
            }
        });
        // -----------------------------------------------------------------------------------------------------

        Label sourceLabel = new Label("Sources:");
        sourceLabel.setLayoutX(2);
        sourceLabel.setLayoutY(5);
        selectionPane.getChildren().add(sourceLabel);

        // Toggle source locations display button.
        sourceLabeled = new RadioButton("Label");  // Need reference before definition.
        sourceVisible = new RadioButton("Visible");
        sourceVisible.setLayoutX(62);
        sourceVisible.setLayoutY(4);
        selectionPane.getChildren().add(sourceVisible);

        Pane sourcesLayer = new Pane();
        displayPane.getChildren().add(sourcesLayer);
        controlActions.addSourceLocationsLayer(sourcesLayer);
        sceneGestures.addEntityToResize(sourcesLayer);
        sourceVisible.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> selected,
                                Boolean oldVal,
                                Boolean show) {
                if (!show) {
                    sourceLabeled.setSelected(false);
                }
                controlActions.toggleSourceDisplay(show);
            }
        });

        // Toggle source labels display button.
        sourceLabeled.setLayoutX(131);
        sourceLabeled.setLayoutY(4);
        selectionPane.getChildren().add(sourceLabeled);

        Pane sourceLabelsLayer = new Pane();
        displayPane.getChildren().add(sourceLabelsLayer);
        controlActions.addSourceLabelsLayer(sourceLabelsLayer);
        sceneGestures.addEntityToResize(sourceLabelsLayer);
        sourceLabeled.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> selected,
                                Boolean oldVal,
                                Boolean show) {
                if (!sourceVisible.isSelected()) {
                    show = false;
                    sourceLabeled.setSelected(false);
                }
                controlActions.toggleSourceLabels(show);
            }
        });

        Label sinkLabel = new Label("Sinks:");
        sinkLabel.setLayoutX(19);
        sinkLabel.setLayoutY(30);
        selectionPane.getChildren().add(sinkLabel);

        // Toggle sink locations display button.
        sinkLabeled = new RadioButton("Label");  // Need reference before definition.
        sinkVisible = new RadioButton("Visible");
        sinkVisible.setLayoutX(62);
        sinkVisible.setLayoutY(29);
        selectionPane.getChildren().add(sinkVisible);
        Pane sinksLayer = new Pane();
        displayPane.getChildren().add(sinksLayer);
        controlActions.addSinkLocationsLayer(sinksLayer);
        sceneGestures.addEntityToResize(sinksLayer);
        sinkVisible.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> selected,
                                Boolean oldVal,
                                Boolean show) {
                if (!show) {
                    sinkLabeled.setSelected(false);
                }
                controlActions.toggleSinkDisplay(show);
            }
        });

        // Toggle sink labels.
        sinkLabeled.setLayoutX(131);
        sinkLabeled.setLayoutY(29);
        selectionPane.getChildren().add(sinkLabeled);
        Pane sinkLabelsLayer = new Pane();
        displayPane.getChildren().add(sinkLabelsLayer);
        controlActions.addSinkLabelsLayer(sinkLabelsLayer);
        sceneGestures.addEntityToResize(sinkLabelsLayer);
        sinkLabeled.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> selected,
                                Boolean oldVal,
                                Boolean show) {
                if (!sinkVisible.isSelected()) {
                    show = false;
                    sinkLabeled.setSelected(false);
                }
                controlActions.toggleSinkLabels(show);
            }
        });

        // Toggle cost surface button.
        dispCostSurface = new RadioButton("Cost Surface");
        dispCostSurface.setLayoutX(4);
//        dispCostSurface.setLayoutY(60);
        dispCostSurface.setLayoutY(55); // Martin -----
        selectionPane.getChildren().add(dispCostSurface);
        dispCostSurface.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> selected,
                                Boolean oldVal,
                                Boolean show) {
                controlActions.toggleCostSurface(show, background);
            }
        });

        TitledPane selectionContainer = new TitledPane("Legend", selectionPane);
        selectionContainer.setCollapsible(false);
        selectionContainer.setPrefSize(192, 156);
        selectionContainer.setLayoutX(14);
        selectionContainer.setLayoutY(210);
        dataPane.getChildren().add(selectionContainer);

        // Solution area
        AnchorPane formulationPane = new AnchorPane();
        formulationPane.setPrefSize(206, 237);
        formulationPane.setMinSize(0, 0);

        Label crfLabel = new Label("Capital Recovery Rate");
        crfLabel.setLayoutX(4);
        crfLabel.setLayoutY(8);
        formulationPane.getChildren().add(crfLabel);
        TextField crfValue = new TextField(".1");
        crfValue.setEditable(true);
        crfValue.setPrefColumnCount(2);
        crfValue.setLayoutX(143);
        crfValue.setLayoutY(4);
        formulationPane.getChildren().add(crfValue);

        Label yearLabel = new Label("Number of Years");
        yearLabel.setLayoutX(4);
        yearLabel.setLayoutY(38);
        formulationPane.getChildren().add(yearLabel);
        TextField yearValue = new TextField("30");
        yearValue.setEditable(true);
        yearValue.setPrefColumnCount(2);
        yearValue.setLayoutX(143);
        yearValue.setLayoutY(34);
        formulationPane.getChildren().add(yearValue);

        Label paramLabel = new Label("Capture Target (MT/y)");
        paramLabel.setLayoutX(4);
        paramLabel.setLayoutY(68);
        formulationPane.getChildren().add(paramLabel);
        TextField paramValue = new TextField("15");
        paramValue.setEditable(true);
        paramValue.setPrefColumnCount(2);
        paramValue.setLayoutX(143);
        paramValue.setLayoutY(64);
        formulationPane.getChildren().add(paramValue);

        RadioButton capVersion = new RadioButton("Cap");
        RadioButton priceVersion = new RadioButton("Price");
        RadioButton timeVersion = new RadioButton("Time");
        ToggleGroup capVsPrice = new ToggleGroup();
        capVersion.setToggleGroup(capVsPrice);
        priceVersion.setToggleGroup(capVsPrice);
        timeVersion.setToggleGroup(capVsPrice);

        capVersion.setLayoutX(5);
        capVersion.setLayoutY(94);
        formulationPane.getChildren().add(capVersion);
        capVersion.setSelected(true);
        capVersion.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> selected,
                                Boolean oldVal,
                                Boolean show) {
                if (!oldVal) {
                    paramLabel.setText("Capture Target (MT/y)");
                    paramValue.setText("15");
                    priceVersion.setSelected(false);
                    timeVersion.setSelected(false);

                    paramLabel.setVisible(true);
                    paramValue.setVisible(true);
                    yearLabel.setVisible(true);
                    yearValue.setVisible(true);

                    timeSettingsContainer.setDisable(true);
                    timeSettingsContainer.setExpanded(false);
                }
            }
        });

        priceVersion.setLayoutX(65);
        priceVersion.setLayoutY(94);
        formulationPane.getChildren().add(priceVersion);
        priceVersion.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> selected,
                                Boolean oldVal,
                                Boolean show) {
                if (!oldVal) {
                    capVersion.setSelected(false);
                    timeVersion.setSelected(false);
                    paramLabel.setText("Tax/Credit ($/t)");
                    paramValue.setText("0");

                    paramLabel.setVisible(true);
                    paramValue.setVisible(true);
                    yearLabel.setVisible(true);
                    yearValue.setVisible(true);

                    timeSettingsContainer.setDisable(true);
                    timeSettingsContainer.setExpanded(false);
                }
            }
        });

        timeVersion.setLayoutX(125);
        timeVersion.setLayoutY(94);
        formulationPane.getChildren().add(timeVersion);
        timeVersion.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> selected,
                                Boolean oldVal,
                                Boolean show) {
                if (!oldVal) {
                    capVersion.setSelected(false);
                    priceVersion.setSelected(false);

                    paramLabel.setVisible(false);
                    paramValue.setVisible(false);
                    yearLabel.setVisible(false);
                    yearValue.setVisible(false);

                    timeSettingsContainer.setDisable(false);
                    timeSettingsContainer.setExpanded(true);
                }
            }
        });

        //////////////////////////////////////////////////////////////////
        // TIME SETTINGS PANE
        //////////////////////////////////////////////////////////////////

        ObservableList<TimeIntervalProto> data = FXCollections.observableArrayList();
        TableView timeTable = new TableView();
        timeTable.setEditable(true);

        TableColumn intervalYearsCol = new TableColumn("Years");
        intervalYearsCol.setSortable(false);
        intervalYearsCol.setMinWidth(50);
        intervalYearsCol.setCellValueFactory(
                new PropertyValueFactory<TimeIntervalProto, String>("timeInterval")
        );
        intervalYearsCol.setCellFactory(TextFieldTableCell.forTableColumn());
        intervalYearsCol.setOnEditCommit(
                new EventHandler<CellEditEvent<TimeIntervalProto, String>>() {
                    @Override
                    public void handle(CellEditEvent<TimeIntervalProto, String> t) {
                        t.getTableView().getItems().get(
                                t.getTablePosition().getRow()).setTimeInterval(t.getNewValue());
                    }
                }
        );

        TableColumn intervalValuesCol = new TableColumn("Values (MT/y)");
        intervalValuesCol.setSortable(false);
        intervalValuesCol.setMinWidth(100);
        intervalValuesCol.setCellValueFactory(
                new PropertyValueFactory<TimeIntervalProto, String>("captureTarget")
        );
        intervalValuesCol.setCellFactory(TextFieldTableCell.forTableColumn());
        intervalValuesCol.setOnEditCommit(
                new EventHandler<CellEditEvent<TimeIntervalProto, String>>() {
                    @Override
                    public void handle(CellEditEvent<TimeIntervalProto, String> t) {
                        t.getTableView().getItems().get(
                                t.getTablePosition().getRow()).setCaptureTarget(t.getNewValue());
                    }
                }
        );
        timeTable.setItems(data);
        timeTable.getColumns().addAll(intervalYearsCol, intervalValuesCol);

        final Button addIntervalButton = new Button("+");
        addIntervalButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                // Add new interval to bottom of data stack (LIFO)
                data.add(new TimeIntervalProto("-", "-"));
            }
        });

        // Martin Ma for DOE ---------------------------------------------------------------------------------
        final Button ReadIntervalDataButton = new Button("Read data");
        ReadIntervalDataButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Boolean show = true;
                controlActions.toggleSourceTargetEvo(show, timeTable, data);
            }
        });
        // -----------------------------------------------------------------------------------------------

        final Button subIntervalButton = new Button("-");
        subIntervalButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                // Remove the last object added (LIFO)
                if (data.size() > 0) {
                    data.remove(data.size() - 1);
                }
            }
        });

        final HBox timeIntervalBtnHBox = new HBox();
        addIntervalButton.setMinWidth(50);
        // Martin Ma ---------------------------------------------------------------------------------
        subIntervalButton.setMinWidth(50);
        // timeIntervalBtnHBox.getChildren().addAll(addIntervalButton, subIntervalButton);
        ReadIntervalDataButton.setMinWidth(100);
        timeIntervalBtnHBox.getChildren().addAll(addIntervalButton, subIntervalButton, ReadIntervalDataButton);
        // ---------------------------------------------------------------------------------

        final VBox timeTableVBox = new VBox();
        timeTableVBox.setSpacing(5);
        timeTableVBox.setPadding(new Insets(2, 2, 2, 2));
        timeTableVBox.getChildren().addAll(timeTable, timeIntervalBtnHBox);

        //////////////////////////////////////////////////////////////////

        // Populate model pane.
        TitledPane modelContainer = new TitledPane("Problem Formulation", formulationPane);
        modelContainer.setCollapsible(false);
        modelContainer.setPrefSize(192, 147);
        modelContainer.setLayoutX(14);
        modelContainer.setLayoutY(5);
        modelPane.getChildren().add(modelContainer);

        timeSettingsContainer = new TitledPane("Time Intervals", timeTableVBox);
        timeSettingsContainer.setCollapsible(true);
        timeSettingsContainer.setPrefSize(192, 282);
        timeSettingsContainer.setLayoutX(14);
        timeSettingsContainer.setLayoutY(259);
        modelPane.getChildren().add(timeSettingsContainer);

        timeSettingsContainer.setDisable(true);
        timeSettingsContainer.setExpanded(false);

        // Solution pane.
        AnchorPane mipSolutionPane = new AnchorPane();
        mipSolutionPane.setPrefSize(192, 100);
        mipSolutionPane.setMinSize(0, 0);

        Button generateSolutionFile = new Button("Generate MPS File");
        generateSolutionFile.setLayoutX(33);
        generateSolutionFile.setLayoutY(5);
        mipSolutionPane.getChildren().add(generateSolutionFile);
        generateSolutionFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                TimeInterval intervals = new TimeInterval();
                String modelVersion = "";

                if (capVersion.isSelected()) {
                    modelVersion = "c";
                    intervals.addInterval(
                            Double.parseDouble(yearValue.getText()),
                            Double.parseDouble(paramValue.getText())
                    );
                } else if (priceVersion.isSelected()) {
                    modelVersion = "p";
                    intervals.addInterval(
                            Double.parseDouble(yearValue.getText()),
                            Double.parseDouble(paramValue.getText())
                    );
                } else if (timeVersion.isSelected()) {
                    modelVersion = "t";
                    if (data.size() == 0) {
                        System.err.println("No intervals have been set");
                        Alert alert = new Alert(AlertType.ERROR,
                                "No intervals have been set",
                                ButtonType.OK);
                        alert.showAndWait();
                        return;
                    }

                    int cum_t = 0;
                    System.out.println("Current time" + "\t" + "Cumulative project length" + "\t" + "Current capature amount");
                    for (TimeIntervalProto t : data) {
                        String t_time = t.getTimeInterval();
                        String t_value = t.getCaptureTarget();
                        cum_t = cum_t + Integer.parseInt(t_time);
                        System.out.println(t_time + "\t\t\t\t" + Integer.toString(cum_t) + "\t\t\t\t" + t_value);

                        if (t_time.isEmpty() && t_value.isEmpty()) continue;

                        try {
                            intervals.addInterval(Double.parseDouble(t_time),
                                    Double.parseDouble(t_value));
                        } catch (NumberFormatException | NullPointerException err) {
                            System.err.println("Invalid interval: (" + t.getTimeInterval() + ", " + t.getCaptureTarget() + ")");
                            Alert alert = new Alert(AlertType.ERROR,
                                    "Invalid interval: (" + t_time + ", " + t_value + ")",
                                    ButtonType.OK);
                            alert.showAndWait();
                            return;
                        }
                    }
                }
                controlActions.generateMPSFile(crfValue.getText(), intervals, modelVersion);
            }
        });

        Label solverLabel = new Label("Solver:");
        solverLabel.setLayoutX(4);
        solverLabel.setLayoutY(44);
        mipSolutionPane.getChildren().add(solverLabel);

        Button cplexSolve = new Button("CPLEX");
        cplexSolve.setLayoutX(51);
        cplexSolve.setLayoutY(38);
        mipSolutionPane.getChildren().add(cplexSolve);
        cplexSolve.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                controlActions.runCPLEX();
            }
        });

        // Populate MIP solution method pane.
        TitledPane mipSolutionContainer = new TitledPane("MIP Solver", mipSolutionPane);
        mipSolutionContainer.setCollapsible(false);
        mipSolutionContainer.setPrefSize(192, 95);
        mipSolutionContainer.setLayoutX(14);
        mipSolutionContainer.setLayoutY(158);
        modelPane.getChildren().add(mipSolutionContainer);

        // Populate results pane.
        // Build solution selection control.
        runChoice.setPrefSize(150, 27);
        runChoice.setLayoutX(20);
        runChoice.setLayoutY(4);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // SOLUTIONS PANE
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        solutionPane = new AnchorPane();
        solutionPane.setPrefSize(190, 30);
        solutionPane.setMinSize(0, 0);
        solutionPane.getChildren().add(runChoice);

        TitledPane resultsContainer = new TitledPane("Load Solution", solutionPane);
        resultsContainer.setCollapsible(false);
        resultsContainer.setPrefSize(192, 94);
        resultsContainer.setLayoutX(14);
        resultsContainer.setLayoutY(5);
        resultsPane.getChildren().add(resultsContainer);

        // Build solution display area.
        AnchorPane solutionDisplayPane = new AnchorPane();
        solutionDisplayPane.setPrefSize(190, 30);
        solutionDisplayPane.setMinSize(0, 0);
        solutionDisplayPane.setLayoutX(0);
        solutionDisplayPane.setLayoutY(110);
        resultsPane.getChildren().add(solutionDisplayPane);

        // Time Interval selection
        ////////////////////////////////////////////////////
        HBox timeIntervalHBox = new HBox();
        Label timeIntervalLabel = new Label("Time Interval:");
        timeIntervalLabel.setPrefHeight(27);
        timeIntervalLabel.setAlignment(Pos.CENTER);
        ChoiceBox timeIntervalChoices = new ChoiceBox();
        timeIntervalChoices.setPrefWidth(50);
        timeIntervalChoices.setPrefHeight(27);
        Separator separator = new Separator(Orientation.VERTICAL);
        separator.setPrefWidth(25);
        separator.setVisible(false);
        timeIntervalHBox.getChildren().addAll(timeIntervalLabel, separator, timeIntervalChoices);

        timeIntervalHBox.setPrefWidth(150);
        timeIntervalHBox.setLayoutX(35);
        timeIntervalHBox.setLayoutY(67);

        resultsPane.getChildren().add(timeIntervalHBox);

        // Solution labels.
        Label sources = new Label("Sources:");
        sources.setLayoutX(75);
        sources.setLayoutY(0);
        Label sourcesValue = new Label("-");
        sourcesValue.setLayoutX(135);
        sourcesValue.setLayoutY(0);
        solutionDisplayPane.getChildren().addAll(sources, sourcesValue);

        Label sinks = new Label("Sinks:");
        sinks.setLayoutX(86);
        sinks.setLayoutY(20);
        Label sinksValue = new Label("-");
        sinksValue.setLayoutX(135);
        sinksValue.setLayoutY(20);
        solutionDisplayPane.getChildren().addAll(sinks, sinksValue);

        Label stored = new Label("Annual CO2 Stored:");
        stored.setLayoutX(18);
        stored.setLayoutY(40);
        Label storedValue = new Label("-");
        storedValue.setLayoutX(135);
        storedValue.setLayoutY(40);
        solutionDisplayPane.getChildren().addAll(stored, storedValue);

        Label edges = new Label("Edges:");
        edges.setLayoutX(85);
        edges.setLayoutY(60);
        Label edgesValue = new Label("-");
        edgesValue.setLayoutX(135);
        edgesValue.setLayoutY(60);
        solutionDisplayPane.getChildren().addAll(edges, edgesValue);

        Label edges_new = new Label("New Edges:");
        edges_new.setLayoutX(60);
        edges_new.setLayoutY(80);
        Label edges_newValue = new Label("-");
        edges_newValue.setLayoutX(135);
        edges_newValue.setLayoutY(80);
        solutionDisplayPane.getChildren().addAll(edges_new, edges_newValue);

        Label length = new Label("Project Length:");
        length.setLayoutX(43);
        length.setLayoutY(100);
        Label lengthValue = new Label("-");
        lengthValue.setLayoutX(135);
        lengthValue.setLayoutY(100);
        solutionDisplayPane.getChildren().addAll(length, lengthValue);

        Label total = new Label("Total Cost\n   ($m/yr)");
        total.setLayoutX(65);
        total.setLayoutY(140);
        Label unit = new Label("Unit Cost\n ($/tCO2)");
        unit.setLayoutX(150);
        unit.setLayoutY(140);
        solutionDisplayPane.getChildren().addAll(total, unit);

        Label cap = new Label("Capture:");
        cap.setLayoutX(4);
        cap.setLayoutY(180);
        Label capT = new Label("-");
        capT.setLayoutX(75);
        capT.setLayoutY(180);
        Label capU = new Label("-");
        capU.setLayoutX(160);
        capU.setLayoutY(180);
        solutionDisplayPane.getChildren().addAll(cap, capT, capU);

        Label cons = new Label("Construction:");
        cons.setLayoutX(4);
        cons.setLayoutY(200);
        Label consT = new Label("-");
        consT.setLayoutX(75);
        consT.setLayoutY(200);
        Label consU = new Label("-");
        consU.setLayoutX(160);
        consU.setLayoutY(200);
        solutionDisplayPane.getChildren().addAll(cons, consT, consU);

        Label om = new Label("O&M:");
        om.setLayoutX(4);
        om.setLayoutY(220);
        Label omT = new Label("-");
        omT.setLayoutX(75);
        omT.setLayoutY(220);
        Label omU = new Label("-");
        omU.setLayoutX(160);
        omU.setLayoutY(220);
        solutionDisplayPane.getChildren().addAll(om, omT, omU);

        Label energy = new Label("Energy:");
        energy.setLayoutX(4);
        energy.setLayoutY(240);
        Label energyT = new Label("-");
        energyT.setLayoutX(75);
        energyT.setLayoutY(240);
        Label energyU = new Label("-");
        energyU.setLayoutX(160);
        energyU.setLayoutY(240);
        solutionDisplayPane.getChildren().addAll(energy, energyT, energyU);

        Label stor = new Label("Storage:");
        stor.setLayoutX(4);
        stor.setLayoutY(260);
        Label storT = new Label("-");
        storT.setLayoutX(75);
        storT.setLayoutY(260);
        Label storU = new Label("-");
        storU.setLayoutX(160);
        storU.setLayoutY(260);
        solutionDisplayPane.getChildren().addAll(stor, storT, storU);

        Label tot = new Label("Total:");
        tot.setLayoutX(4);
        tot.setLayoutY(280);
        Label totT = new Label("-");
        totT.setLayoutX(75);
        totT.setLayoutY(280);
        Label totU = new Label("-");
        totU.setLayoutX(160);
        totU.setLayoutY(280);
        solutionDisplayPane.getChildren().addAll(tot, totT, totU);

        Label solnIntervals = new Label("Solution Intervals:");
        solnIntervals.setLayoutX(30);
        solnIntervals.setLayoutY(300);
        Label solnIntervalsValue = new Label("-");
        solnIntervalsValue.setLayoutX(135);
        solnIntervalsValue.setLayoutY(300);
        solutionDisplayPane.getChildren().addAll(solnIntervals, solnIntervalsValue);

        Label[] solutionValues = new Label[]{
                sourcesValue, sinksValue, storedValue, edgesValue,edges_newValue,
                lengthValue, capT, capU, consT, consU,
                omT, omU,
                energyT, energyU,
                storT, storU,
                totT, totU, solnIntervalsValue};

        timeIntervalChoices.getSelectionModel()
                .selectedItemProperty()
                .addListener(new ChangeListener<String>() {
                                 @Override
                                 public void changed(ObservableValue<? extends String> selected,
                                                     String oldValue,
                                                     String newValue) {
                                     if (newValue != null) {
                                         selectedSolutionFileInterval =
                                                 Integer.parseInt(newValue) - 1;
                                         controlActions.selectSolution(loadedSolutionFile,
                                                 solutionValues,
                                                 selectedSolutionFileInterval);
                                     }
                                 }
                             }
                );

        // Run selection action.
        runChoice.getSelectionModel()
                .selectedItemProperty()
                .addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> selected,
                                        String oldSolution,
                                        String newSolution) {
                        loadedSolutionFile = newSolution;
                        selectedSolutionFileInterval = 0;
                        controlActions.selectSolution(newSolution, solutionValues, 0);
                        setNumIntervalChoices(timeIntervalChoices, solutionValues[18].getText());
                    }
                });
        runChoice.showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (isShowing) {
                controlActions.initializeSolutionSelection(runChoice);
            }
        });
        runChoice.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent s) {
                double direction = s.getDeltaY();
                String currentChoice = (String) runChoice.getValue();
                ObservableList<String> choices = runChoice.getItems();
                int index = choices.indexOf(currentChoice);
                if (direction < 0 && index < choices.size() - 1) {
                    runChoice.setValue(choices.get(index + 1));
                } else if (direction > 0 && index > 0) {
                    runChoice.setValue(choices.get(index - 1));
                }
            }
        });

        Pane solutionLayer = new Pane();
        displayPane.getChildren().add(solutionLayer);
        controlActions.addSolutionLayer(solutionLayer);
        sceneGestures.addEntityToResize(solutionLayer);

        // Add everything to group and display.
        group.getChildren().addAll(LogoPane, displayPane, tabPane, messengerPane);
        return new Scene(group, 1050, 660);
    }

    public void displayCostSurface() {
        dispCostSurface.setSelected(true);
    }

    public void fullReset() {
        //scenarioChoice;
        dispDelaunayEdges.setSelected(false);
        dispCandidateNetwork.setSelected(false);
        sourceLabeled.setSelected(false);
        sourceVisible.setSelected(false);
        sinkLabeled.setSelected(false);
        sinkVisible.setSelected(false);
        dispCostSurface.setSelected(false);
        messenger.setText("");
    }

    public void setNumIntervalChoices(ChoiceBox timeIntervalChoices, String numIntervalsLabel) {

        Integer numIntervals = null;

        try {
            numIntervals = Integer.parseInt(numIntervalsLabel);
        } catch (NumberFormatException err) {
            return;
        } finally {
            if (numIntervals == null) return;
        }

        if (!timeIntervalChoices.getSelectionModel().isEmpty() || timeIntervalChoices.getItems().size() > 0) {
            timeIntervalChoices.getItems().removeAll(timeIntervalChoices.getItems());
        }

        for (int i = 0; i < numIntervals; i++) {
            timeIntervalChoices.getItems().add(String.valueOf(i + 1));
        }
    }

    public void softReset() {
        dispDelaunayEdges.setSelected(false);
        dispCandidateNetwork.setSelected(false);
        sourceLabeled.setSelected(false);
        sourceVisible.setSelected(false);
        sinkLabeled.setSelected(false);
        sinkVisible.setSelected(false);
        dispCostSurface.setSelected(false);
        messenger.setText("");
    }

    public double getScale() {
        return displayPane.getScale();
    }

    public static class TimeIntervalProto {
        private static String id = "0";
        private String timeInterval;
        private String captureTarget;

        public TimeIntervalProto(String time_interval, String capture_target) {
            TimeIntervalProto.id = String.valueOf(Integer.parseInt(id) + 1);
            this.timeInterval = time_interval;
            this.captureTarget = capture_target;
        }

        public String getTimeInterval() {
            return this.timeInterval;
        }

        public void setTimeInterval(String time_interval) {
            this.timeInterval = time_interval;
        }

        public String getCaptureTarget() {
            return this.captureTarget;
        }

        public void setCaptureTarget(String capture_target) {
            this.captureTarget = capture_target;
        }

    }
}
