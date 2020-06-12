package util;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import java.net.URL;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 *
 * @author Antonin
 */
public class GeometryManager {
    
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
     * @param parent
     * @param topRight
     * @param bottomRight
     * @param bottomLeft
     * @param topLeft
     * @param material 
     */
    public static void addQuadrilateral(Group parent, Point3D topRight, Point3D bottomRight, Point3D bottomLeft, Point3D topLeft, PhongMaterial material)
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
