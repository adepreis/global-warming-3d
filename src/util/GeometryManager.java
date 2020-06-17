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
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import model.GeoCoord;
import model.GlobeAnomaliesRepresentation;
import model.ResourceManager;

/**
 *
 * @author Antonin
 */
public class GeometryManager {
    
    /**
     *
     * @param path
     * @return
     */
    public static Group load(URL path) {// Load geometry
        ObjModelImporter objImporter = new ObjModelImporter();
        try {
            URL modelUrl = path;
            objImporter.read(modelUrl);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        MeshView[] meshViews = objImporter.getImport();
        
        return new Group(meshViews);
    }
    
    // From Rahel Lüthy : https://netzwerg.ch/blog/2015/03/22/javafx-3d-line/
    public static Cylinder createLine(Point3D origin, Point3D target) {
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
    
    /*
        TODO : reuse this method to display user click on globe ??
        (defined in TutoJFx3D)
    */
    // public void displayTown(Group parent, String name, float latitude, float longitude) { }
    
    /**
     * 
     * @param topRight
     * @param bottomRight
     * @param bottomLeft
     * @param topLeft
     * @param material 
     * @return  
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
        
        return meshView;
    }
    
    /**
     *
     * @param anoGroup
     * @param rm
     * @param year
     * @param displayType
     */
    public static void drawAnomalies(Group anoGroup, ResourceManager rm, int year, GlobeAnomaliesRepresentation displayType) {
        
        /*
            TODO : see Scale.getColorForValue() for better color gradient..
        */
        
        double matOpacity = 0.08;
        Color blue = new Color(0, 0, 0.5, matOpacity);
        Color lightBlue = new Color(0, 0.1, 0.2, matOpacity);
        Color white = new Color(0.3, 0.3, 0.3, matOpacity);
        Color red = new Color(0.5, 0, 0, matOpacity);
        Color yellow = new Color(0.5, 0.5, 0.0, matOpacity);
        Color orange = new Color(0.5, 0.3, 0.0, matOpacity);
        
        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(blue);
        blueMaterial.setSpecularColor(red);
        
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(red);
        redMaterial.setSpecularColor(red);
        
        
        float anoMax = rm.getMaxTempAnomaly();
        float anoMin = rm.getMinTempAnomaly();
        
                    
        float radiusLayer = 1.01f;
        
        int index = 0;
        
        for (int lat = -88; lat <= 88; lat = lat + 4) {
            for (int lon = -178; lon <= 178; lon = lon + 4) {
                
                float anomaly = rm.getAnomaly(lat, lon, year);
                float delta = anomaly > 0 ? anomaly/anoMax : -anomaly/-anoMin;
                
                PhongMaterial material = new PhongMaterial();
                
                if (displayType == GlobeAnomaliesRepresentation.BY_COLOR) {
                    Color quadColor = Color.TRANSPARENT;
                    
                    // Math.round(delta); ???
                    //Color quadColor = (anomaly > 0.f ? getColorOnScale(delta) : getColorOnScale(-delta));
                    
                    if (anomaly > 0.f) {
                        quadColor = delta > 0.2 ? (delta > 0.7 ? red : orange) : yellow;
                    } else if (anomaly < 0.f) {
                        quadColor = delta > 0.2 ? (delta > 0.7 ? blue : lightBlue) : white;
                    }
                    
                    material.setDiffuseColor(quadColor);
                    material.setSpecularColor(quadColor);
                    
                    
                    MeshView quad;
                    
                    try {
                        // Change material color instead of replace entire quad !!!
                        Node existingNode = anoGroup.getChildren().get(index);
                        
                        if(existingNode instanceof MeshView) {
                            quad = (MeshView) existingNode;
                            quad.setMaterial(material);
                        } else {
                            anoGroup.getChildren().remove(existingNode);      // !!!! if the node is a line
                            throw new Exception();
                        }
                        
                        // replace quadrilateral n°index
                        anoGroup.getChildren().set(index++, quad);
                    } catch (Exception e) {
                        quad = createQuadrilateral(
                                GeoCoord.geoCoordTo3dCoord(lat + 4, lon + 4, radiusLayer),
                                GeoCoord.geoCoordTo3dCoord(lat, lon + 4, radiusLayer),
                                GeoCoord.geoCoordTo3dCoord(lat, lon, radiusLayer),
                                GeoCoord.geoCoordTo3dCoord(lat + 4, lon, radiusLayer),
                                material );
                        
                        anoGroup.getChildren().add(index++, quad);
                    }
                } else {
                    material =  anomaly > 0 ? redMaterial : blueMaterial;                    
                    
                    Cylinder line;
                    
                    try {
                        // Change material color instead of replace entire line
                        Node existingNode = anoGroup.getChildren().get(index);
                        
                        if(existingNode instanceof Cylinder) {
                            line = (Cylinder) existingNode;
                            line.setMaterial(material);
                            
                            
                            // TODO : ALSO CHANGE HEIGHT !!!!!!!!!
                            line.setHeight(delta);
                            
                            
                        } else {
                            anoGroup.getChildren().remove(existingNode);  // !!!! if the node is a meshview
                            throw new Exception();
                        }
                        
                        // replace line n°index
                        anoGroup.getChildren().set(index++, line);
                    } catch (Exception e) {
                        Point3D origin = GeoCoord.geoCoordTo3dCoord(lat + 4, lon + 4, 0.99f);
                        Point3D target = GeoCoord.geoCoordTo3dCoord(lat + 4, lon + 4, 0.99f + delta);

                        line = createLine(origin, target);
                        
                        line.setMaterial(material);
                        anoGroup.getChildren().add(line);
                    }
                }
                
            }
        }
    }
}
