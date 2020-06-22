package view;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

/**
 * ModeSwitcher is the view of a switch composed of 2 toggle buttons.
 * It is used to select how to display anomalies.
 *
 * @author Antonin
 */
public final class ModeSwitcher extends HBox {
    private ToggleGroup tgGroup;
    private ToggleButton tbColors = new ToggleButton("Couleurs");
    private ToggleButton tbBars = new ToggleButton("Barres");;

    /**
     * Initializes buttons' aspect and properties.
     * 
     * @param btnWidth a double value corresponding to the width of a (single) button.
     */
    public ModeSwitcher(double btnWidth) {
        super();
        // Class is final because of "Overridable method call in constructor" here :
        getChildren().addAll(tbBars, tbColors);
                
        tgGroup = new ToggleGroup();
        
        tbBars.setPrefWidth(btnWidth);
        tbBars.setStyle("-fx-background-radius: 10 0 0 10");
        
        tbColors.setPrefWidth(btnWidth);
        tbColors.setStyle("-fx-background-radius: 0 10 10 0");
        
        // links the toggle buttons to the toggle group
        tbBars.setToggleGroup(tgGroup);
        tbColors.setToggleGroup(tgGroup);
        
        // Color mode is selected by default
        tbColors.setSelected(true);
        tbColors.setDisable(true);
    }

    public ReadOnlyObjectProperty<Toggle> selectedToggleProperty() {
        return tgGroup.selectedToggleProperty();
    }

    public boolean hasSelectedToggle() {
        return tgGroup.getSelectedToggle() != null;
    }

    public boolean isColorModeSelected() {
        return tgGroup.getSelectedToggle() == tbColors;
    }

    public boolean isBarModeSelected() {
        return tgGroup.getSelectedToggle() == tbBars;
    }
    
    /**
     * Inverts toggles aspect : the selected one should'nt be accessible.
     */
    public void toggleDisabledButton() {
        tbBars.setDisable(!tbBars.isDisable());
        tbColors.setDisable(!tbBars.isDisable());
    }
}
