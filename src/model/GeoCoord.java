package model;

/**
 *
 * @author adepreis
 */
public class GeoCoord {
    private final int lat;
    private final int lon;

    public GeoCoord(int lat, int lon) {
        // restrict lat between -90° and 90°
        this.lat = lat < -90 ? -90 : (lat > 90 ? 90 : lat);
        
        // restrict lon between -180° and 180°
        this.lon = lon < -180 ? -180 : (lon > 180 ? 180 : lon);
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
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + this.lat;
        hash = 61 * hash + this.lon;
        return hash;
    }

    @Override
    public String toString() {
        return "GeoCoord{" + "lat=" + lat + ", lon=" + lon + '}';
    }
    
}
