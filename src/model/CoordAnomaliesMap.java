package model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CoordAnomaliesMap class associates an AnnualAnomaliesMap (a year-anomaly
 * association) with a position.
 *
 * @author adepreis
 */
public class CoordAnomaliesMap extends LinkedHashMap<GeoCoord, AnnualAnomaliesMap> {

    /**
     * Returns an anomaly according to it's coordinates and year.
     * 
     * @param lat an integer corresponding to the searched anomaly's latitude.
     * @param lon an integer corresponding to the searched anomaly's longitude.
     * @param year an integer corresponding to the searched anomaly's year.
     * @return  
     */
    public float getAnomaly(int lat, int lon, int year) {
        return this.get(new GeoCoord(lat, lon)).get(year);
    }
    
    /**
     * Returns all anomaly values according to its coordinates.
     * 
     * @param lat an integer corresponding to the latitude of the anomalies we're looking for.
     * @param lon an integer corresponding to the longitude of the anomalies we're looking for.
     * @return  
     */
    public float[] getAllYearAnomalyByPosition(int lat, int lon) {
        int i = 0;
        
        AnnualAnomaliesMap aam = this.get(new GeoCoord(lat, lon));
        float[] list = new float[aam.size()];
        
        for (Map.Entry<Integer, Float> e : aam.entrySet()) {
            list[i++] = e.getValue();
        }
        
        return list;
    }

    /**
     * Returns all anomaly values according to its coordinates.
     * 
     * @param year an integer corresponding to the year of the anomalies we're looking for.
     * @return  
     */
    public float[] getAllCoordAnomalyByYear(int year) {
        int i = 0;
        float[] list = new float[this.size()];
        
        for (Map.Entry<GeoCoord, AnnualAnomaliesMap> e : this.entrySet()) {
            list[i++] = e.getValue().get(year);
        }
        
        return list;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("CoordAnomaliesMap de taille ");
        strBuilder.append(this.size());
        
//        Iterator<TempAnomaly> it = this.iterator();
//        
//        while(it.hasNext()) {
//            strBuilder.append(it.next().toString()).append("\n");
//        }
        
        return strBuilder.toString();
    }
}
