import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.ResourceManager;

/**
 * Main class of the application.
 *
 * @author adepreis
 */
public class GlobalWarmingMain extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        ResourceManager rm = new ResourceManager();
        
        rm.readTemperatureFile("src/data/tempanomaly_4x4grid.csv");
        
        System.out.println("Max anomaly : " + rm.getMaxTempAnomaly() + ", Min anomaly : " + rm.getMinTempAnomaly());
        
//        try {
//            Parent root = FXMLLoader.load(getClass().getResource("ApplicationView.fxml"));
//            
//            primaryStage.setTitle("Global Warming");
//            primaryStage.setScene(new Scene(root));
//            primaryStage.show();
//        } catch (IOException ex) {
//            System.err.println(ex.getMessage());
//            ex.printStackTrace();
//        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        launch(args);
    }
    
}
