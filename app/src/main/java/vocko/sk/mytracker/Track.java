package vocko.sk.mytracker;


public class Track {
    private long trackid = 0;
    private double distance = 0;
    private long time = 0;
    private long movtime = 0;
    private float maxspeed = 0;
    private double maxalt = 0;
    private float avgspeed = 0;
    private String routedesc;
    private String acttype;
    private String status;

    public Track() {

    }

    public long getTrackid() {
        return trackid;
    }

    public void setTrackid(long trackid) {
        this.trackid = trackid;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getMovtime() {
        return movtime;
    }

    public void setMovtime(long movtime) {
        this.movtime = movtime;
    }

    public float getMaxspeed() {
        return maxspeed;
    }

    public void setMaxspeed(float maxspeed) {
        this.maxspeed = maxspeed;
    }

    public double getMaxalt() {
        return maxalt;
    }

    public void setMaxalt(double maxalt) {
        this.maxalt = maxalt;
    }

    public float getAvgspeed() {
        return avgspeed;
    }

    public void setAvgspeed(float avgspeed) {
        this.avgspeed = avgspeed;
    }

    public String getRoutedesc() {
        return routedesc;
    }

    public void setRoutedesc(String routedesc) {
        this.routedesc = routedesc;
    }

    public String getActtype() {
        return acttype;
    }

    public void setActtype(String acttype) {
        this.acttype = acttype;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
