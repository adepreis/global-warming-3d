package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * The YearModel class represents a selected year.
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
