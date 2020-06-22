package application;

import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.ErrorManager;

/**
 * Main class of the application.
 *
 * @author adepreis
 */
public class GlobalWarmingMain extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        final String fxmlPath = "/view/ApplicationView.fxml";
        
        try {            
            // Initialize the application's window
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            
            primaryStage.setTitle("Global Warming 3D");
            primaryStage.setResizable(false);
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ErrorManager.displayLoadWarning(fxmlPath);
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
