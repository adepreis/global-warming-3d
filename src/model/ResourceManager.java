package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author adepreis
 */
public class ResourceManager {
    public int sampleNumber = 0;
    
    private float minTempAnomaly;
    private float maxTempAnomaly;
    
    private CoordAnomaliesMap anomalyGrid;

    /**
     *
     */
    public ResourceManager() {
        this.minTempAnomaly = Float.NaN;
        this.maxTempAnomaly = Float.NaN;
    }
    

    public float getMinTempAnomaly() { return minTempAnomaly; }

    public float getMaxTempAnomaly() { return maxTempAnomaly; }
    
    /**
     * 
     * @param lat
     * @param lon 
     * @param year 
     * @return  
     */
    public float getAnomaly(int lat, int lon, int year) {
        return anomalyGrid.getAnomaly(lat, lon, year);
    }
    
    /**
     * 
     * @param lat
     * @param lon 
     * @return  
     */
    public float[] getAllYearsFromCoord(int lat, int lon) {
        return anomalyGrid.getAllYearAnomalyByPosition(lat, lon);
    }

    /**
     * 
     * @param year
     * @return  
     */
    public float[] getAllCoordFromYear(int year) {
        return anomalyGrid.getAllCoordAnomalyByYear(year);
    }
    
    /**
     *
     * @param path
     */
    public void readTemperatureFile(String path) {
        
        // clear all attribute before updating them :
        sampleNumber = 0;
        minTempAnomaly = Float.MAX_VALUE;
        maxTempAnomaly = Float.MIN_VALUE;
        anomalyGrid = new CoordAnomaliesMap();
        
        try {
            // this.getClass().getResource("tempanomaly_4x4grid.csv").toURI().getPath()
            
            FileReader file = new FileReader(path);
            BufferedReader bufRead = new BufferedReader(file);
            
            //bufRead.read();     // skip first "\""
            String line = bufRead.readLine();
            line = line.substring(1, line.length()-1);     // skip first and last "
            
            String[] data = line.split("\",\"", -1);
            
            
            if (!data[0].equals("lat") || !data[1].equals("lon")) {
                System.err.println("Les 2 premières colonnes ne correspondent pas au format de fichier attendu.");
                // TODO : throw exception here
                return;
            }
            
            // remove first 2 strings..
            data = Arrays.copyOfRange(data, 2, data.length);
            // and cast data to an int array (list of years of the 1st line) :
            int[] years = Arrays.stream(data).mapToInt(Integer::parseInt).toArray();
            
            
            System.out.println("[INFO] The file at " + path + " contain " + years.length + " columns.");
            
            sampleNumber = years.length;
            
            data = null;
            
            //Read the file line by line
            while ((line = bufRead.readLine()) != null)
            {
                //Get data from the line
                data = line.split(",");
                
                int lat = Integer.parseInt(data[0]);
                int lon = Integer.parseInt(data[1]);
                
                // remove first 2 int..
                data = Arrays.copyOfRange(data, 2, data.length);
                
                AnnualAnomaliesMap yearAno = new AnnualAnomaliesMap();
                
                for (int i = 0; i < years.length; i++) {
                    // Float.NaN for the "NA" anomalies
                    float value = data[i].equals("NA") ? Float.NaN : Float.parseFloat(data[i]);
                    
                    if(value < minTempAnomaly) {
                        minTempAnomaly = value;         // update min temperature
                    } else if(value > maxTempAnomaly) {
                        maxTempAnomaly = value;         // update max temperature
                    }
                    
                    yearAno.put(years[i], value);
                }
                
                anomalyGrid.put(new GeoCoord(lat, lon), yearAno);
            }
            
            bufRead.close();
            file.close();
            
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        
    }
}
