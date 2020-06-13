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
import javafx.geometry.Point3D;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.AnimationSpeed;
import model.GeoCoord;
import model.GlobeAnomaliesRepresentation;
import model.ResourceManager;
import util.CameraManager;
import util.GeometryManager;

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
    
    @FXML
    private Pane pane3D;
    
    
    private ToggleButton tbColor;
    private ToggleButton tbBars;
    private Label yearLabel;
    
    
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
        //Create a Pane et graph scene root for the 3D content
        Group root3D = new Group();

        root3D.getChildren().add(GeometryManager.load(this.getClass().getResource("/resources/earth/earth.obj")));
        
        
        rm = new ResourceManager();
        rm.readTemperatureFile("src/resources/tempanomaly_4x4grid.csv");
        System.out.println("Max anomaly : " + rm.getMaxTempAnomaly() + ", Min anomaly : " + rm.getMinTempAnomaly());
        
        // !!!!!!!!!!!!   TODO AFTER INITILAIZATION ??!???!!!???   !!!!!!!!!!!!!!!!!!!
        drawEarth(root3D);
        

        //Add a camera group
        PerspectiveCamera camera = new PerspectiveCamera(true);


        // Add ambient light
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        ambientLight.getScope().addAll(root3D);
        root3D.getChildren().add(ambientLight);
        
        
        // Create scene
        SubScene subScene = new SubScene(root3D, 500, 500, true, SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);
        subScene.setFill(Color.GREY);

        pane3D.getChildren().add(subScene);
        
        
        // need to be done after subScene adding to be displayed above
        pane3D.getChildren().add(initAbove3D());
        
        displayType = GlobeAnomaliesRepresentation.BY_COLOR;
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
        
        
        yearLabel = new Label("1880");
        yearLabel.setFont(Font.font("System", FontWeight.BOLD, 35));
        
        
        HBox tgHBox = new HBox(tbBars, tbColor);
        
        Stop[] stops = new Stop[] { new Stop(0, Color.RED), new Stop(1, Color.BLUE)};
        LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        Rectangle scale = new Rectangle(0, 0, 20, 250);
        scale.setFill(lg);
        scale.setStroke(Color.BLACK);
        scale.setStrokeWidth(1);
        
        
//        yearLabel.setLayoutX(pane3D.getWidth()/2);
//        yearLabel.setLayoutY(pane3D.getHeight()/2);
        
//        yearLabel.translateXProperty().set(pane3D.getWidth()/2);
//        yearLabel.translateYProperty().set(pane3D.getHeight()/2);

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
        /*
        TODO : ""alternate"" with pause img :
            playBtn.setGraphic(new ImageView(new Image("/resources/pause.png", 25, 25, true, true)));
        */
        
        
        // change "mode" when a new radio button is selected
        this.tgGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldToggle, Toggle newToggle) {
                if (tgGroup.getSelectedToggle() != null) {
                    if (tgGroup.getSelectedToggle() == tbColor) {
                        displayType = GlobeAnomaliesRepresentation.BY_COLOR;
                    }
                    else if (tgGroup.getSelectedToggle() == tbBars) {
                        displayType = GlobeAnomaliesRepresentation.BY_HISTOGRAM;
                    }
                    
                    // TODO : impossible for the moment because root3D isnt accessible...
                    // drawEarth(root3D);
                    
                    
                }
                System.out.println(displayType);
            }
        });
        
        
        /*
            TODO : use next piece of code to hydrate the graphic (maybe not here) :
        */
        /*
            //  Idem pour le graphe en ligne
            CategoryAxis x2 = new CategoryAxis();
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
        
        
        /*
            TODO : reuse next piece of code to animate slider on playBtn click :
        */
        // Rotate animation
//        final long startNanoTime = System.nanoTime();
//        new AnimationTimer() {
//            @Override
//            public void handle(long currentNanoTime) {
//                double t = (currentNanoTime - startNanoTime) / 1000000000.0;
//                greenCube.setRotationAxis(new Point3D(0, 1, 0));
//                greenCube.setRotate(10 * t);
//            }
//        }.start();


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
    }

    private void drawEarth(Group parent) {
        
        /*
            TODO : see https://stackoverflow.com/a/25214819
            for better color gradient..
        */
        
        
        double matOpacity = 0.05;
        Color blue = new Color(0, 0, 0.5, matOpacity);
        Color red = new Color(0.5, 0, 0, matOpacity);
        Color white = new Color(0.2, 0.2, 0.2, matOpacity);
        
        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(blue);
        blueMaterial.setSpecularColor(red);
        
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(red);
        redMaterial.setSpecularColor(red);
        
        
        
        float anoMax = rm.getMaxTempAnomaly();
        float anoMin = rm.getMinTempAnomaly();
        
                    
        float radiusLayer = 1.01f;
        
        
        
        for (int lat = -88; lat <= 88; lat = lat + 4) {
            for (int lon = -178; lon <= 178; lon = lon + 4) {
                
                float anomaly = rm.getAnomaly(lat, lon, 1880);
                float delta = anomaly > 0 ? anomaly/anoMax : -anomaly/-anoMin;
                
                if (displayType == GlobeAnomaliesRepresentation.BY_COLOR) {
                    PhongMaterial material = new PhongMaterial();
                    if (anomaly > 0.1) {
                        Color customRed = red.deriveColor(1.0, 1.0, 1.0, delta);
                        material.setDiffuseColor(customRed);
                        material.setSpecularColor(customRed);
                    } else if (anomaly < -0.1) {
                        Color customBlue = blue.interpolate(white, delta);
                        material.setDiffuseColor(customBlue);
                        material.setSpecularColor(customBlue);
                    } else {
                        Color transparent = Color.TRANSPARENT;
                        material.setDiffuseColor(transparent);
                        material.setSpecularColor(transparent);
                    }
                    
                    GeometryManager.addQuadrilateral(
                            parent,
                            GeoCoord.geoCoordTo3dCoord(lat + 4, lon + 4, radiusLayer),
                            GeoCoord.geoCoordTo3dCoord(lat, lon + 4, radiusLayer),
                            GeoCoord.geoCoordTo3dCoord(lat, lon, radiusLayer),
                            GeoCoord.geoCoordTo3dCoord(lat + 4, lon, radiusLayer),
                            material );
                } else {
                    Point3D origin = GeoCoord.geoCoordTo3dCoord(lat + 4, lon + 4, 0.99f);
                    Point3D target = GeoCoord.geoCoordTo3dCoord(lat + 4, lon + 4, 0.99f + delta);
                    
                    
                    Cylinder line = GeometryManager.createLine(origin, target);
                    line.setMaterial( anomaly > 0 ? redMaterial : blueMaterial );
                    
                    parent.getChildren().add(line);
                }
                
            }
        }
    }
}
