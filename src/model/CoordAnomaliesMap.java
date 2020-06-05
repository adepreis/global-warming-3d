package model;

import java.util.HashMap;

/**
 *
 * @author adepreis
 */
public class CoordAnomaliesMap extends HashMap<GeoCoord, AnnualAnomaliesMap> {

    /**
     * 
     * @param lat
     * @param lon 
     * @param year 
     * @return  
     */
    public float getAnomaly(int lat, int lon, int year) {
        float anomaly = Float.NaN;
        
        /* TODO */
        
        return anomaly;
    }
    /**
     * 
     * @param lat
     * @param lon 
     * @return  
     */
    public float[] getAllYearsFromCoord(int lat, int lon) {
        int i = 0;
        float[] list = new float[this.size()];
        
        /* TODO */
        
        return list;
    }

    /**
     * 
     * @param year
     * @return  
     */
    public float[] getAllCoordFromYear(int year) {
        int i = 0;
        float[] list = new float[this.size()];
        
        /* TODO */
        
        return list;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("CoordAnomaliesMap : \n");
        
//        Iterator<TempAnomaly> it = this.iterator();
//        
//        while(it.hasNext()) {
//            strBuilder.append(it.next().toString()).append("\n");
//        }
        
        return strBuilder.toString();
    }
}
