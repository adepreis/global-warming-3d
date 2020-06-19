package application;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class of the application.
 *
 * @author adepreis
 */
public class GlobalWarmingMain extends Application {
    
    @Override
    public void start(Stage primaryStage) {        
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/ApplicationView.fxml"));
            
            primaryStage.setTitle("Global Warming 3D");
            primaryStage.setResizable(false);
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Make sure that the plateform can handle 3D.
        if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
            throw new RuntimeException("ERREUR: la fonctionnalité SCENE3D n'est"
                    + "pas supporté par votre platforme / installation.");
        }
        
        // Make sure that the project includes the ObjModelImporter library.
        try {
            Class.forName("com.interactivemesh.jfx.importer.obj.ObjModelImporter");
        } catch (ClassNotFoundException ex) {
            System.err.println("Impossible de lancer l'application car la librairie"
                    + " ObjModelImporterJFX n'est pas inclue dans le projet.");
            System.exit(1);
        }
        
        launch(args);
    }
    
}
