package util;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import java.net.URL;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import model.GeoCoord;
import model.GlobeAnomaliesRepresentation;
import model.ResourceManager;

/**
 * Utility class used to build 3D shapes.
 *
 * @author Antonin
 */
public class GeometryManager {
    
    private static final double MAT_OPACITY = 0.08;
    private static final Color BLUE = new Color(0, 0, 0.5, MAT_OPACITY);
    private static final Color RED = new Color(0.5, 0, 0, MAT_OPACITY);
    private static final Color YELLOW = new Color(0.5, 0.5, 0.0, MAT_OPACITY);
    private static final Color ORANGE = new Color(0.5, 0.3, 0.0, MAT_OPACITY);
    
    private static final PhongMaterial BLUE_MATERIAL = new PhongMaterial(BLUE);
    
    private static final PhongMaterial RED_MATERIAL = new PhongMaterial(RED);
    
    private static final PhongMaterial GREEN_MATERIAL = new PhongMaterial(Color.GREEN);
    
    
    /**
     * Load geometry from a 3D model path using ObjModelImporterJFX lib.
     *
     * @param path
     * @return
     */
    public static Group load(String path) {
        ObjModelImporter objImporter = new ObjModelImporter();
        try {
            URL modelUrl = GeometryManager.class.getClass().getResource(path);
            objImporter.read(modelUrl);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            ErrorManager.displayLoadWarning(path);
        }
        MeshView[] meshViews = objImporter.getImport();
        
        return new Group(meshViews);
    }
    
    /**
     * Returns a Cylinder shape from the origin to the target point.
     * 
     * @param target
     * @return a Cylinder object.
     */
    public static Cylinder createLine(Point3D target) {
        return createLine(Point3D.ZERO, target);
    }
    
    // From Rahel Lüthy : https://netzwerg.ch/blog/2015/03/22/javafx-3d-line/
    public static Cylinder createLine(Point3D origin, Point3D target) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();
        
        
        // TO FIX : Issue #4 : Uninitialized Histograms
        if (Double.isNaN(height)) {
            height = 1.0;
        }
        
        
        
        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());
        
        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);
        
        Cylinder line = new Cylinder(0.01f, height);
        
        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);
        
        return line;
    }
    
    
    /**
     * Returns a quadrilateral shape corresponding to a geographical zone.
     * 
     * @param topRight      top right of the quadrilateral.
     * @param bottomRight   bottom right of the quadrilateral.
     * @param bottomLeft    bottom left of the quadrilateral.
     * @param topLeft       top left of the quadrilateral.
     * @param material make the quad' in the specified PhongMaterial.
     * @return a MeshView object.
     */
    public static MeshView createQuadrilateral(Point3D topRight, Point3D bottomRight, Point3D bottomLeft, Point3D topLeft, PhongMaterial material)
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
        
        /*      points :
                1       0
                ---------   texture :
                |      /|   1,1(0)  1,0(1)
                |     / |     --------
                |    /  |     |      |
                |   /   |     |      |
                |  /    |     --------
                ---------   0,1(2)  0,0(3)
                2       3                           */
        
        triangleMesh.getPoints().setAll(points);
        triangleMesh.getTexCoords().setAll(texCoords);
        triangleMesh.getFaces().setAll(faces);
        
        final MeshView meshView = new MeshView(triangleMesh);
        meshView.setMaterial(material);
        
        return meshView;
    }
    
    /**
     * Creates and attachs anomalies shapes (corresponding to the chosen year
     * and display mode) to the specified Group.
     *
     * @param anoGroup
     * @param rm
     * @param year
     * @param displayType
     */
    public static void drawAnomalies(Group anoGroup, ResourceManager rm, int year, GlobeAnomaliesRepresentation displayType) {
        // it's too bad to do this every time :
        BLUE_MATERIAL.setSpecularColor(BLUE);
        RED_MATERIAL.setSpecularColor(RED);
    
        
        float anoMax = rm.getMaxTempAnomaly();
        float anoMin = rm.getMinTempAnomaly();
        
                    
        float radiusLayer = 1.01f;
        
        int index = 0;
        
        for (int lat = -88; lat <= 88; lat = lat + 4) {
            for (int lon = -178; lon <= 178; lon = lon + 4) {
                
                float anomaly = rm.getAnomaly(lat, lon, year);
                double delta = anomaly > 0 ? anomaly/anoMax : -anomaly/-anoMin;
                delta = Math.floor(delta*100)/100;
                
                PhongMaterial material = new PhongMaterial();
                
                if (displayType == GlobeAnomaliesRepresentation.BY_COLOR) {
                    Color quadColor = Color.TRANSPARENT;
                    
                    // TODO : see Scale.getColorForValue() for better color gradient or
                    // Math.round(delta); ???
                    // Color quadColor = (anomaly > 0.f ? getColorOnScale(delta) : getColorOnScale(-delta));
                    
                    if (anomaly > 0.f) {
                        quadColor = delta > 0.2 ? (delta > 0.7 ? RED : ORANGE) : YELLOW;
                    } else if (anomaly < 0.f) {
                        quadColor = delta > 0.2 ? (delta > 0.7 ? RED.invert() : ORANGE.invert()) : YELLOW.invert();                
                    }
                    
                    material.setDiffuseColor(quadColor);
                    material.setSpecularColor(quadColor);
                    
                    
                    MeshView quad;
                    
                    try {
                        // Change material color instead of replacing entire quad
                        Node existingNode = anoGroup.getChildren().get(index);
                        
                        if(existingNode instanceof MeshView) {
                            quad = (MeshView) existingNode;
                            quad.setMaterial(material);
                        } else {
                            // the node isnt a meshview
                            anoGroup.getChildren().remove(existingNode);
                            throw new Exception();
                        }
                        
                        // replace quadrilateral n°index
                        anoGroup.getChildren().set(index++, quad);
                    } catch (Exception e) {
                        // When no quad exists before :
                        
                        quad = createQuadrilateral(
                                GeoCoord.geoCoordTo3dCoord(lat + 4, lon + 4, radiusLayer),
                                GeoCoord.geoCoordTo3dCoord(lat, lon + 4, radiusLayer),
                                GeoCoord.geoCoordTo3dCoord(lat, lon, radiusLayer),
                                GeoCoord.geoCoordTo3dCoord(lat + 4, lon, radiusLayer),
                                material );
                        
                        anoGroup.getChildren().add(index++, quad);
                    }
                } else {
                    material =  anomaly > 0 ? RED_MATERIAL : BLUE_MATERIAL;                    
                    
                    Cylinder line;
                    
                    try {
                        // Change material color instead of replacing entire line
                        Node existingNode = anoGroup.getChildren().get(index);
                        
                        if(existingNode instanceof Cylinder) {
                            line = (Cylinder) existingNode;
                            
                            line.setMaterial(material);
                            line.setHeight(0.99f + delta);
                        } else {
                            // the node isnt a line
                            anoGroup.getChildren().remove(existingNode);
                            throw new Exception();
                        }
                        
                        // replace line n°index
                        anoGroup.getChildren().set(index++, line);
                    } catch (Exception e) {
                        // When no line exists before :
                        
                        Point3D target = GeoCoord.geoCoordTo3dCoord(lat + 4, lon + 4, 0.99f + delta);

                        line = createLine(target);
                        
                        line.setMaterial(material);
                        anoGroup.getChildren().add(line);
                    }
                }
                
            }
        }
    }
    
    /**
     * Adds a green point to the specified group, at the given position.
     * 
     * @param parent targeted group
     * @param position 3D point
     */
    public static void displayPoint(Group parent, Point3D position) {
        Sphere point = new Sphere(0.02);
        
        GREEN_MATERIAL.setSpecularColor(Color.GREEN);
        
        point.setMaterial(GREEN_MATERIAL);
        
        Group town = new Group(point);
        
        /*  translaté à la bonne position */
        town.setTranslateX(position.getX());
        town.setTranslateY(position.getY());
        town.setTranslateZ(position.getZ());
        
        parent.getChildren().add(town);
    }
}
