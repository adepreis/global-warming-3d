package model;

import javafx.geometry.Point3D;

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
        return lat < 0 ? lat + "° Nord" : lat + "° Sud" ;
    }
    
    public String lonToString() {
        return lon < 0 ? lon + "° Ouest" : lat + "° Est" ;
    }
    

    public static Point3D geoCoordTo3dCoord(float lat, float lon, float radius) {
        float lat_cor = lat + TEXTURE_LAT_OFFSET;
        float lon_cor = lon + TEXTURE_LON_OFFSET;
        return new Point3D(
                -java.lang.Math.sin(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)) * radius,
                -java.lang.Math.sin(java.lang.Math.toRadians(lat_cor)) * radius,
                java.lang.Math.cos(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)) * radius);
    }
    
}
