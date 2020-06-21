package util;

import javafx.scene.control.Alert;

/**
 * Utility class used to handle failures.
 *
 * @author Antonin
 */
public class ErrorManager {
    
    /**
     * Displays an alert message on file loading failed attempt.
     * Triggers program's shutdown.
     * 
     * @param path a string corresponding to the unresolved path.
     */
    public static void displayLoadWarning(String path) {
            String fileName = path.substring(path.lastIndexOf("/")+1);
            String pathName = path.substring(0, path.lastIndexOf("/"));
            
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur lors de l'initialisation des données.");
            alert.setHeaderText("Le fichier \"" + fileName + "\" n'a pas pû être atteint.");
            alert.setContentText("Aucune fonctionnalité de l'application ne peut être garantie."
                    + "\nVeuillez vous assurer que le fichier \"" + fileName + "\" "
                    + "est bien présent dans le dossier " + pathName + ".");
            alert.showAndWait();
            System.exit(1);
    }

    /**
     * Displays an informative dialog box on incorrect text field submission.
     * 
     * @param input a string corresponding to the wrong input.
     */
    public static void displayWrongInput(String input) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Date non reconnue.");
            alert.setHeaderText("\"" + input + "\" n'est pas une entrée correcte.");
            alert.setContentText("Veuillez vous assurer que la date est bien écrite en chiffres arabes."
                    + "\nElle doit être comprise entre les bornes du curseur glissable.");
            alert.showAndWait();
    }
    
}
