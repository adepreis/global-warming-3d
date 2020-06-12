package controller;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
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
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import model.AnimationSpeed;
import model.GeoCoord;
import model.GlobeAnomaliesRepresentation;
import util.CameraManager;

/**
 * ApplicationView FXML Controller class
 *
 * @author Antonin
 */
public class ApplicationController implements Initializable {

    
    private ToggleGroup tgGroup;
    private GlobeAnomaliesRepresentation displayType;
    private AnimationSpeed currentSpeed;
    
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
    private Button slowDownBtn;
    
    @FXML
    private Button speedUpBtn;
    
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

        // Load geometry
        ObjModelImporter objImporter = new ObjModelImporter();
        try {
            URL modelUrl = this.getClass().getResource("/resources/earth/earth.obj");
            objImporter.read(modelUrl);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        MeshView[] meshViews = objImporter.getImport();
        Group earth = new Group(meshViews);
        
        root3D.getChildren().add(earth);
        

        double matOpacity = 0.05;
        
        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(new Color(0, 0.3, 0, matOpacity));
        greenMaterial.setSpecularColor(new Color(0, 0.3, 0, matOpacity));
        
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(new Color(0.3, 0, 0, matOpacity));
        redMaterial.setSpecularColor(new Color(0.3, 0, 0, matOpacity));
        
        
        for (int lat = -90; lat < 90; lat = lat + 5) {
            for (int lon = -180; lon < 180; lon = lon + 5) {
                PhongMaterial material;
                if (lat % 2 == 0 && lon % 2 == 0) {
                     material = redMaterial;
                }
                else if (lat % 2 == 0 && lon % 2 != 0) {
                     material = greenMaterial;
                }
                else if (lat % 2 != 0 && lon % 2 == 0) {
                     material = greenMaterial;
                }
                else {
                     material = redMaterial;
                }
                
                float radiusLayer = 1.01f;
                
                this.AddQuadrilateral(
                        root3D,
                        GeoCoord.geoCoordTo3dCoord(lat + 5, lon + 5, radiusLayer),
                        GeoCoord.geoCoordTo3dCoord(lat, lon + 5, radiusLayer),
                        GeoCoord.geoCoordTo3dCoord(lat, lon, radiusLayer),
                        GeoCoord.geoCoordTo3dCoord(lat + 5, lon, radiusLayer),
                        material );
           }
        }

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


    /*
        TODO : reuse this method later ??
        (defined in TutoJFx3D)
    */
    // public Cylinder createLine(Point3D origin, Point3D target) { }
    
    /*
        TODO : reuse this method to display user click on globe ??
        (defined in TutoJFx3D)
    */
    // public void displayTown(Group parent, String name, float latitude, float longitude) { }
    
    /**
     * 
     * @param parent
     * @param topRight
     * @param bottomRight
     * @param bottomLeft
     * @param topLeft
     * @param material 
     */
    private void AddQuadrilateral(Group parent, Point3D topRight, Point3D bottomRight, Point3D bottomLeft, Point3D topLeft, PhongMaterial material)
    {
        final TriangleMesh triangleMesh = new TriangleMesh();
        
        final float[] points = {
            (float)topRight.getX(),     (float)topRight.getY(),     (float)topRight.getZ(),
            (float)topLeft.getX(),      (float)topLeft.getY(),      (float)topLeft.getZ(),
            (float)bottomLeft.getX(),   (float)bottomLeft.getY(),   (float)bottomLeft.getZ(),
            (float)bottomRight.getX(),  (float)bottomRight.getY(),  (float)bottomRight.getZ(),
        };
        
        final float[] texCoords = {
            1, 1,
            1, 0,
            0, 1,
            0, 0
        };
        
        final int[] faces = {
            0, 1, 1, 0, 2, 2,
            0, 1, 2, 2, 3, 3
        };
        
        /*
            points :
            1       0
            ---------   texture :
            |      /|   1,1(0)  1,0(1)
            |     / |     --------
            |    /  |     |      |
            |   /   |     |      |
            |  /    |     --------
            ---------   0,1(2)  0,0(3)
            2       3
        */
        
        triangleMesh.getPoints().setAll(points);
        triangleMesh.getTexCoords().setAll(texCoords);
        triangleMesh.getFaces().setAll(faces);
        
        final MeshView meshView = new MeshView(triangleMesh);
        meshView.setMaterial(material);
        parent.getChildren().addAll(meshView);
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
        
        
//        yearLabel.setLayoutX(pane3D.getWidth()/2);
//        yearLabel.setLayoutY(pane3D.getHeight()/2);
        
//        yearLabel.translateXProperty().set(pane3D.getWidth()/2);
//        yearLabel.translateYProperty().set(pane3D.getHeight()/2);

        yearLabel.layoutXProperty().bind(pane3D.widthProperty().subtract(yearLabel.widthProperty()).divide(2));
        yearLabel.layoutYProperty().bind(pane3D.heightProperty().subtract(yearLabel.heightProperty()));
        
        tgHBox.layoutXProperty().bind(pane3D.widthProperty().multiply(0.03f));
        tgHBox.layoutYProperty().bind(pane3D.heightProperty().multiply(0.03f));
        
        
        return new Group(yearLabel, tgHBox);
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
                    }
                    else if (tgGroup.getSelectedToggle() == tbBars) {
                        displayType = GlobeAnomaliesRepresentation.BY_HISTOGRAM;
                    }
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
        speedUpBtn.setOnMouseClicked(event -> {
            currentSpeed.speedUp();
            speedLabel.setText(currentSpeed.toString());
        });
        
        slowDownBtn.setOnMouseClicked(event -> {
            currentSpeed.slowDown();
            speedLabel.setText(currentSpeed.toString());
        });
    }
}
