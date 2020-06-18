package controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.AnimationSpeed;
import model.GlobeAnomaliesRepresentation;
import model.ResourceManager;
import model.YearModel;
import util.CameraManager;
import util.GeometryManager;
import view.Scale;

/**
 * ApplicationView FXML Controller class
 *
 * @author Antonin
 */
public class ApplicationController implements Initializable {
    
    private ToggleGroup tgGroup;
    private GlobeAnomaliesRepresentation displayType;
    private AnimationSpeed currentSpeed;
    private ResourceManager rm;
    
    private YearModel year;
    private boolean isPlaying = false;
    
    @FXML
    private BorderPane mainPane;
    
    @FXML
    private Pane pane3D;
    
    Group root3D;
    Group anoGroup;
    
    
    private ToggleButton tbColor;
    private ToggleButton tbBars;
    private Label yearLabel;
    private Scale scale;
    
    
    @FXML
    private TextField tfYear;
    
    @FXML
    private Slider yearsSlider;
    
    @FXML
    private Button playBtn;
    
    @FXML
    private ImageView slowDown;
    
    @FXML
    private ImageView speedUp;
    
    @FXML
    private Label speedLabel;
    
    @FXML
    private Label latitudeLabel;
    
    @FXML
    private Label longitudeLabel;
    
    @FXML
    private LineChart anomaliesChart;
    
    @FXML
    private ImageView searchIcon;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //Create a graph scene root for the 3D content
        root3D = new Group();
        
        anoGroup = new Group();

        root3D.getChildren().add(GeometryManager.load(this.getClass().getResource("/resources/earth/earth.obj")));
        
        
        rm = new ResourceManager();
        rm.readTemperatureFile("src/resources/tempanomaly_4x4grid.csv");
        System.out.println(rm.toString());
        
        
        year = new YearModel(rm.getMinYear());
        
        root3D.getChildren().add(anoGroup);
        
        // !!!!!!!!!!!!   TODO AFTER INITILAIZATION ??!???!!!???   !!!!!!!!!!!!!!!!!!!
        displayType = GlobeAnomaliesRepresentation.BY_COLOR;
        GeometryManager.drawAnomalies(anoGroup, rm, year.getCurrentYear(), displayType);
        

        //Add a camera group
        PerspectiveCamera camera = new PerspectiveCamera(true);


        // Add ambient light
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        ambientLight.getScope().add(root3D);
        root3D.getChildren().add(ambientLight);
        
        
        // Create scene
        SubScene subScene = new SubScene(root3D, 500, 500, true, SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);
        subScene.setFill(Color.GREY);

        pane3D.getChildren().add(subScene);
        
        
        // need to be done after subScene adding to be displayed above
        
        pane3D.getChildren().add(initAbove3D());
        
        Tooltip tooltip = new Tooltip("Ctrl + Clic pour obtenir des informations sur une zone.");
        Tooltip.install(pane3D, tooltip);
        
        currentSpeed = new AnimationSpeed(1);
        
        init2D();
        
        initListeners();
        
        // Build camera manager
        new CameraManager(camera, pane3D, root3D);
    }

    
    private Group initAbove3D() {
        // Controls above 3D scene
        tgGroup = new ToggleGroup();
        
        tbColor = new ToggleButton("Couleurs");
        tbBars = new ToggleButton("Barres");
        
        // links the toggle buttons to the toggle group
        tbColor.setToggleGroup(tgGroup);
        tbBars.setToggleGroup(tgGroup);
        
        tbColor.setSelected(true);
        
        
        yearLabel = new Label(Integer.toString(rm.getMinYear()));
        yearLabel.setFont(Font.font("System", FontWeight.BOLD, 35));
        
        
        HBox tgHBox = new HBox(tbBars, tbColor);
        
        // initialize a scale adapted to the Color display mode :
        scale = new Scale(20, 250, Color.RED, Color.ORANGE, Color.YELLOW,
                                Color.YELLOW.invert(), Color.ORANGE.invert(), Color.RED.invert());
        
//        yearLabel.setLayoutX(...); or yearLabel.translateXProperty().set(..); doesnt works

        yearLabel.layoutXProperty().bind(pane3D.widthProperty().subtract(yearLabel.widthProperty()).divide(2));
        yearLabel.layoutYProperty().bind(pane3D.heightProperty().subtract(yearLabel.heightProperty()));
        
        tgHBox.layoutXProperty().bind(pane3D.widthProperty().multiply(0.03f));
        tgHBox.layoutYProperty().bind(pane3D.heightProperty().multiply(0.03f));
        
        scale.layoutXProperty().bind(pane3D.widthProperty().subtract(scale.widthProperty().add(5)));
        scale.layoutYProperty().bind(pane3D.heightProperty().divide(2).subtract(scale.heightProperty().divide(2)));
        
        return new Group(yearLabel, tgHBox, scale);
    }
    
    private void init2D() {
        
        // Restrict input length "client side" :
        Pattern pattern = Pattern.compile(".{0,4}");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });

        tfYear.setTextFormatter(formatter);

        playBtn.setGraphic(new ImageView(new Image("/resources/play.png", 25, 25, true, true)));
        
        
        // change "mode" when a new radio button is selected
        this.tgGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldToggle, Toggle newToggle) {
                if (tgGroup.getSelectedToggle() != null) {

                    if (tgGroup.getSelectedToggle() == tbColor) {
                        displayType = GlobeAnomaliesRepresentation.BY_COLOR;
                        scale.setGradient(Color.RED, Color.ORANGE, Color.YELLOW,
                                Color.YELLOW.invert(), Color.ORANGE.invert(), Color.RED.invert());
                    }
                    else if (tgGroup.getSelectedToggle() == tbBars) {
                        displayType = GlobeAnomaliesRepresentation.BY_HISTOGRAM;
                        scale.setGradient(Color.RED, Color.BLUE);
                    }
                    
                    GeometryManager.drawAnomalies(anoGroup, rm, year.getCurrentYear(), displayType);
                }
            }
        });
        
    }

    private void initListeners() {
        /*
        * Listener of the year field : updates slider and label when the
        * "enter" keyboard button is hit (in the field).
        */
        EventHandler textFieldListener = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                if (e.getCode().equals(KeyCode.ENTER)) {
                    try {
                        Double newValue = Double.parseDouble(tfYear.getText());

                        if (newValue <= yearsSlider.getMax() && newValue >= yearsSlider.getMin()) {
                            yearsSlider.setValue(newValue);
                        }
                    } catch (Exception ex) {
                        System.err.println("Impossible to parse the input given.");
                    } finally {
                        tfYear.setText("");
                    }
                }
            }
        };
        
        tfYear.setOnKeyPressed(textFieldListener);
        
        // Associate click on the magnifying glass icon to a textfield Entrer-pressed event
        searchIcon.setOnMouseClicked(event -> {
            tfYear.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "",
                    "", KeyCode.ENTER, false, false, false, false));
        });


        // Bind label w/ slider
        yearLabel.textProperty().bind(
            Bindings.format(
                "%.0f",
                yearsSlider.valueProperty()
            ));
        
        
//        year.currentYearProperty().bindBidirectional(yearsSlider.valueProperty());
        
        yearsSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                year.setCurrentYear(newValue.intValue());
                GeometryManager.drawAnomalies(anoGroup, rm, year.getCurrentYear(), displayType);
            }
        });
        
        /*
            TODO : tempo :
                - later in a dedicated class
                - add "maintained click"
        */
        speedUp.setOnMouseClicked(event -> {
            currentSpeed.speedUp();
            speedLabel.setText(currentSpeed.toString());
        });
        
        slowDown.setOnMouseClicked(event -> {
            currentSpeed.slowDown();
            speedLabel.setText(currentSpeed.toString());
        });
        
        
        playBtn.setOnMouseClicked(event -> {
            playBtn.setGraphic(new ImageView(new Image("/resources/pause.png", 25, 25, true, true)));
            
            // toggle boolean
            isPlaying = !isPlaying;
            
            final long startTime = System.nanoTime();
            new AnimationTimer() {
                
                long nextTimeStamp = startTime/100000 + (1000 * (6- currentSpeed.getSpeed()));
                    
                @Override
                public void handle(long now) {
                    if (yearsSlider.getValue() == yearsSlider.getMax()
                        || !isPlaying) {
                        stop();
                        playBtn.setGraphic(new ImageView(new Image("/resources/play.png", 25, 25, true, true)));
                        return;
                    }
                    
                    if (nextTimeStamp < now/100000) {
                        yearsSlider.increment();
                        nextTimeStamp = now/100000 + (1000 * (6- currentSpeed.getSpeed()));
                    }
                }
            }.start();
            
        });
        
        
        
        /*
            TODO : 
            Continue right panel display
            Invert current behaviour (hide panel when selected)
        */
        pane3D.setOnMouseClicked(event -> {
            if (event.isControlDown()) {
                System.out.println("Clicked x:" + event.getX() + " y:" + event.getY());
                
                /*
                    TODO : reuse "displayTown(parent, latitude, longitude)" to display user click on globe ??
                    (defined in TutoJFx3D)
                */
                
                // Useful to select zone :
                // https://docs.oracle.com/javase/8/javafx/graphics-tutorial/picking.htm

                
                // GeoCoord gc = ????.getZoneFromClick(event.getX(), event.getY());
                // latitudeLabel.setText(gc.latToString());
                // longitudeLabel.setText(gc.lonToString());
                
                
                /*
                    TODO : use next piece of code to hydrate the graphic (maybe not here) :
                */
                /*
                    //  Idem pour le graphe en ligne
                    CategoryAxis x2 = new CategoryAxis();
                    // or ?
                    final NumberAxis xAxis = new NumberAxis(1880, 2020, 25);
                
                    x2.setLabel("Avancement");
                    NumberAxis y2 = new NumberAxis();
                    y2.setLabel("Etat avancement");

                    LineChart lineGraphic = new LineChart(x2, y2);        

                    XYChart.Series<String, Number> serie2 = new XYChart.Series<>();
                    serie2.setName("Etat en fonction de l'avancement");

                    serie2.getData().add(new XYChart.Data<>("Jour 1", 0));
                    serie2.getData().add(new XYChart.Data<>("Jour 2", 2));
                    serie2.getData().add(new XYChart.Data<>("Jour 3", 3));
                    serie2.getData().add(new XYChart.Data<>("Jour 4", 7));
                    lineGraphic.getData().add(serie2);
                    lineGraphic.getStyleClass().add("chart-content");
                    root.add(lineGraphic, 0, 6);
                */
                
                
                
                // Hide right panel :
                
                VBox vb = (VBox)mainPane.getRight();
//                vb.setPrefWidth(0);   --> save vb here ?? before replacing it by null ??
//                                      --> animate width decreasing ??
                mainPane.setRight(null);
                
                
//                pane3D.setPrefWidth(mainPane.getWidth());     // resize only pane3d not root3d
            }
        });
    }

}
