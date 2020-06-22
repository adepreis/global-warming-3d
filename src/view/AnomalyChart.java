package view;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import model.ResourceManager;

/**
 * The AnomalyChart class represents a chart dedicated to temperatures'
 * evolution display.
 *
 * @author Antonin
 */
public class AnomalyChart extends LineChart {

    public AnomalyChart(ResourceManager rm) {
        super(new NumberAxis("Années", rm.getMinYear(), rm.getMaxYear(), 25),
              new NumberAxis("Anomalies", (int)rm.getMinTempAnomaly()-1, (int)rm.getMaxTempAnomaly()+1, 2));        
        
        setCreateSymbols(false);
        setLegendVisible(false);
        setPrefHeight(270.0);
        setMaxWidth(250.0);
//        setAnimated(false);
    }

    /**
     * Clears the old line and displays a new set of data.
     * 
     * @param anomalies the array of float used to populate the chart.
     */
    public void updateData(float[] anomalies) {
        LineChart.Series<Number, Number> serie = new LineChart.Series<>();
        
        for (int i = 0; i < anomalies.length; i++) {
            serie.getData().add(new LineChart.Data<>(i+1880, anomalies[i]));
        }
        
        getData().clear();
        getData().add(serie);
                    
        serie.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #DD3300;");
    }
    
}
