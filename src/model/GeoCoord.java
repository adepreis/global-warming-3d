package model;

import javafx.geometry.Point3D;
import javafx.util.Pair;

/**
 * The GeoCoord class represents geographical coordinates.
 * Its latitude should range from -90 to 90.
 * Its longitude should range from -180 to 180.
 *
 * @author adepreis
 */
public class GeoCoord {
    private static final float TEXTURE_LAT_OFFSET = -0.2f;
    private static final float TEXTURE_LON_OFFSET = 2.8f;
    
    private final int lat;
    private final int lon;

    /**
     * Constructs a GeoCoord instance with the specified latitude and longitude.
     * 
     * @param lat an integer corresponding to the instance's latitude.
     * @param lon an integer corresponding to the instance's longitude.
     */
    public GeoCoord(int lat, int lon) {
        // restrict lat between -90° and 90°
        this.lat = lat < -90 ? -90 : (lat > 90 ? 90 : lat);
        
        // restrict lon between -180° and 180°
        this.lon = lon < -180 ? -180 : (lon > 180 ? 180 : lon);
    }

    public int getLat() { return lat; }

    public int getLon() { return lon; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GeoCoord other = (GeoCoord) obj;
        if (this.lat != other.lat) {
            return false;
        }
        if (this.lon != other.lon) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + this.lat;
        hash = 61 * hash + this.lon;
        return hash;
    }

    @Override
    public String toString() {
        return "GeoCoord : " + "lat=" + latToString() + ", lon=" + lonToString() + '}';
    }
    
    public String latToString() {
        return latToString(lat);
    }
    
    public String lonToString() {
        return lonToString(lon);
    }
    
    public static String latToString(int lati) {
        return lati <= 0 ? -lati + "° Nord" : lati + "° Sud" ;
    }
    
    public static String lonToString(int longi) {
        return longi <= 0 ? -longi + "° Ouest" : longi + "° Est" ;
    }
    
    /**
     * Converts geographic coordinates into a 3D point.
     * 
     * @param lat
     * @param lon
     * @param radius
     * @return 
     */
    public static Point3D geoCoordTo3dCoord(float lat, float lon, double radius) {
        float lat_cor = lat + TEXTURE_LAT_OFFSET;
        float lon_cor = lon + TEXTURE_LON_OFFSET;
        return new Point3D(
                -Math.sin(Math.toRadians(lon_cor))
                        * Math.cos(Math.toRadians(lat_cor)) * radius,
                -Math.sin(Math.toRadians(lat_cor)) * radius,
                Math.cos(Math.toRadians(lon_cor))
                        * Math.cos(Math.toRadians(lat_cor)) * radius);
    }

    /**
     * Converts a 3D point into geographic coordinates.
     * 
     * @param point3D
     * @return a pair corresponding to geographic coordinates.
     * (key : latitude, value : longitude)
     */
    public static Pair<Integer, Integer> coord3dToGeoCoord(Point3D point3D) {
        double adjustedY = (point3D.getY() > 0) ? Math.min(point3D.getY(), 1.0) : Math.max(point3D.getY(), -1.0);
        
        double lat_cor = Math.asin(adjustedY / 1.0f);
        double lon_cor = Math.atan2(point3D.getZ(), point3D.getX());
        
        lat_cor = (float)Math.toDegrees(lat_cor);
        lon_cor = (float)Math.toDegrees(lon_cor);
        
        lat_cor -= TEXTURE_LAT_OFFSET;
        lon_cor -= TEXTURE_LON_OFFSET;
        
        // TO FIX. Caution : return 92° for south pole latitude (which doesnt exists !)
        return new Pair<>((int)(4*(Math.round(lat_cor/4))), (int)(4*(Math.round(lon_cor/4))+2));
    }
    
}