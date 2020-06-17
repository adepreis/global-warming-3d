package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Antonin
 */
public class YearModel {
    private final IntegerProperty currentYear;

    public YearModel(int year) {
        currentYear = new SimpleIntegerProperty(year);
    }

    public int getCurrentYear() {
        return currentYear.get();
    }

    public void setCurrentYear(int value) {
        currentYear.set(value);
    }

    public IntegerProperty currentYearProperty() {
        return currentYear;
    }
    
}
