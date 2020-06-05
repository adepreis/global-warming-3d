package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author adepreis
 */
public class ResourceManager {
    public int sampleNumber = 0;
    
    private float minTempAnomaly;
    private float maxTempAnomaly;
    
    private CoordAnomaliesMap anomalyGrid;
    
    public void readTemperatureFile(String path) {
        
        // clear all attribute before updating them :
        sampleNumber = 0;
        minTempAnomaly = Float.MIN_VALUE;
        maxTempAnomaly = Float.MAX_VALUE;
        anomalyGrid = new CoordAnomaliesMap();
        
//        try {
//            // this.getClass().getResource("tempanomaly_4x4grid.csv").toURI().getPath()
//            
//            FileReader file = new FileReader("name_fichier.ext");
//            BufferedReader bufRead = new BufferedReader(file);
//            
//            /* TODO : Line 1 : get years */
//            
//            String line = bufRead.readLine();
//            while ( line != null) {
//                String[] array = line.split(",");
//                
//                int id = Integer.parseInt(array[0]);
//                float val = Float.parseFloat(array[6]);
//                
//                line = bufRead.readLine();
//                
//                /* TODO : Float.NaN pour les "NA" */
//
//
//                /* TODO : update min temperature */
//                /* TODO : update max temperature */
//            }
//            
//            bufRead.close();
//            file.close();
//            
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//            e.printStackTrace();
//        }
        
    }

    public float getMinTempAnomaly() {
        return minTempAnomaly;
    }

    public float getMaxTempAnomaly() {
        return maxTempAnomaly;
    }
}
