package vocko.sk.mytracker;

import android.location.Location;

import java.io.Serializable;


public class MyLocation implements Serializable {

    private static final long serialVersionUID = 2L;

    float accuracy;
    double latitude;
    double altitude;
    double distance;
    long time;
    String provider;
    float speed;
    float bearing;
    double longitude;
    long trackid;
    int satellites;

    public MyLocation() {

    }

    public MyLocation(long trackid, Location location) {
        this.trackid = trackid;
        satellites = (Integer) location.getExtras().get("satellites");
        accuracy = location.getAccuracy();
        latitude = location.getLatitude();
        time = location.getTime();
        provider = location.getProvider();
        speed = location.getSpeed();
        bearing = location.getBearing();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Integer getSatellites() {
        return satellites;
    }

    public void setSatellites(Integer satellites) {
        this.satellites = satellites;
    }

    public long getTrackid() {
        return trackid;
    }

    public void setTrackid(long trackid) {
        this.trackid = trackid;
    }
}
