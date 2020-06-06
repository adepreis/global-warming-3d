package model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author adepreis
 */
public class CoordAnomaliesMap extends LinkedHashMap<GeoCoord, AnnualAnomaliesMap> {

    /**
     * 
     * @param lat
     * @param lon 
     * @param year 
     * @return  
     */
    public float getAnomaly(int lat, int lon, int year) {
        return this.get(new GeoCoord(lat, lon)).get(year);
    }
    
    /**
     * 
     * @param lat
     * @param lon 
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
     * 
     * @param year
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
