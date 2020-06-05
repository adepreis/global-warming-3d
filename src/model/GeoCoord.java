package model;

/**
 *
 * @author adepreis
 */
public class GeoCoord {
    private int  lat;
    private int  lon;

    public GeoCoord(int lat, int lon) {
        /* TODO : restrict -90<= lat <=90 */
        this.lat = lat;
        /* TODO : restrict -180<= lon <=180 */
        this.lon = lon;
    }

    public int getLat() {
        return lat;
    }

    public int getLon() {
        return lon;
    }

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
    public String toString() {
        return "GeoCoord{" + "lat=" + lat + ", lon=" + lon + '}';
    }
    
}
