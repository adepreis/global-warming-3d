package controller;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Pair;
import util.CameraManager;

/**
 * ApplicationView FXML Controller class
 *
 * @author Antonin
 */
public class ApplicationController implements Initializable {

    private static final float TEXTURE_LAT_OFFSET = -0.2f;
    private static final float TEXTURE_LON_OFFSET = 2.8f;
    
    @FXML
    private Pane pane3D;

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

        // Draw a line

        // Draw an helix

        // Draw city on the earth
        HashMap<String, Pair<Float, Float>> cities = new HashMap<>();
        cities.put("Brest", new Pair(48.447911f, -4.418539f));
        cities.put("Marseille", new Pair(43.435555f, 5.213611f));
        cities.put("New York", new Pair(40.639751f, -73.778925f));
        cities.put("Cape Town", new Pair(-33.964806f, 18.601667f));
        cities.put("Istanbul", new Pair(40.976922f, 28.814606f));
        cities.put("Reykjavik", new Pair(64.13f, -21.940556f));
        cities.put("Singapore", new Pair(1.350189f, 103.994433f));
        cities.put("Seoul", new Pair(37.469075f, 126.450517f));
        

        double matOpacity = 0.05;
        
        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(new Color(0, 0.3, 0, matOpacity));
        greenMaterial.setSpecularColor(new Color(0, 0.3, 0, matOpacity));
        
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(new Color(0.3, 0, 0, matOpacity));
        redMaterial.setSpecularColor(new Color(0.3, 0, 0, matOpacity));
        
    
        for(Map.Entry<String, Pair<Float, Float>> city : cities.entrySet()) {
            String name = city.getKey();
            float lat = city.getValue().getKey();
            float lon = city.getValue().getValue();

            displayTown(root3D, name, lat, lon);
        }
        
        
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

                // parent, topRight, bottomRight, bottomLeft, topLeft, material

//                Point3D topLeft = geoCoordTo3dCoord(lat + 5, lonradiusLayer);
//                Point3D topRight = geoCoordTo3dCoord(lat + 5, lon + 5radiusLayer);
//                Point3D bottomLeft = geoCoordTo3dCoord(lat, lonradiusLayer);
//                Point3D bottomRight = geoCoordTo3dCoord(lat, lon + 5radiusLayer);
//
//                AddQuadrilateral(root3D, topRight, bottomRight, bottomLeft, topLeft, material);

                
                this.AddQuadrilateral(
                        root3D,
                        geoCoordTo3dCoord(lat + 5, lon + 5, radiusLayer),
                        geoCoordTo3dCoord(lat, lon + 5, radiusLayer),
                        geoCoordTo3dCoord(lat, lon, radiusLayer),
                        geoCoordTo3dCoord(lat + 5, lon, radiusLayer),
                        material );
           }
        }

//        for (int lat = -90; lat < 90; lat+=4) {
//            for (int lon = -180; lon < 180; lon+=4) {
//                this.AddQuadrilateral(
//                        root3D,
//                        geoCoordTo3dCoord(lat+2, lon+2, 1.01f),
//                        geoCoordTo3dCoord(lat-2, lon+2, 1.01f),
//                        geoCoordTo3dCoord(lat-2, lon-2, 1.01f),
//                        geoCoordTo3dCoord(lat+2, lon-2, 1.01f),
//                        redMaterial );
//            }
//        }

        //Add a camera group
        PerspectiveCamera camera = new PerspectiveCamera(true);

        
        /* Is point light useful ? */
        
        // Add point light
//        PointLight light = new PointLight(Color.WHITE);
//        light.setTranslateX(-180);
//        light.setTranslateY(-90);
//        light.setTranslateZ(-120);
//        light.getScope().addAll(root3D);
//        root3D.getChildren().add(light);

        // Add ambient light
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        ambientLight.getScope().addAll(root3D);
        root3D.getChildren().add(ambientLight);
        
        // Create scene
        SubScene subScene = new SubScene(root3D, 600, 600, true, SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);
        subScene.setFill(Color.GREY);

        pane3D.getChildren().add(subScene);
        
        
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
        
        // Build camera manager
        new CameraManager(camera, pane3D, root3D);
    }


    // From Rahel Lüthy : https://netzwerg.ch/blog/2015/03/22/javafx-3d-line/
    public Cylinder createLine(Point3D origin, Point3D target) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder line = new Cylinder(0.01f, height);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return line;
    }

    public static Point3D geoCoordTo3dCoord(float lat, float lon, float radius) {
        float lat_cor = lat + TEXTURE_LAT_OFFSET;
        float lon_cor = lon + TEXTURE_LON_OFFSET;
        return new Point3D(
                -java.lang.Math.sin(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)) * radius,
                -java.lang.Math.sin(java.lang.Math.toRadians(lat_cor)) * radius,
                java.lang.Math.cos(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)) * radius);
    }
    
    public void displayTown(Group parent, String name, float latitude, float longitude) {
        Sphere point = new Sphere(0.01);
        
        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.GREEN);
        greenMaterial.setSpecularColor(Color.GREEN);
        
        point.setMaterial(greenMaterial);
        
        Group town = new Group(point);
        
        town.setId(name);
        
        Point3D position = geoCoordTo3dCoord(latitude, longitude, /* TODO : */1.f/* SPHERE RADIUS !??? */);
        /*  translaté à la bonne position */
        town.setTranslateX(position.getX());
        town.setTranslateY(position.getY());
        town.setTranslateZ(position.getZ());
        
        parent.getChildren().add(town);
    }
    
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
    
}
