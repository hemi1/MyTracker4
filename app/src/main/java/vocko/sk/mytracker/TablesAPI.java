package vocko.sk.mytracker;

import android.provider.BaseColumns;


public final class TablesAPI implements BaseColumns {

    // Verzia databazy
    public static final int DATABASE_VERSION = 1;
    // Nazov databazy
    public static final String DATABASE = "trackerdb";
    // Tabulka pre sumarne informacie o trase
    public static final String TABLE_TRACKER_SUMMARY = "summary";
    // Tabulka pre identifikaciu uzivatela
    public static final String TABLE_TRACKER_IDENTITY = "identity";
    // Tabulka pre ukladanie informacii z GPS
    public static final String TABLE_TRACKER_BASE = "tracker";

    // Podrobnosti tabulky TABLE_TRACKER_SUMMARY
    // Identifikator trasy
    public static final String COL_SUMMARY_TRACKID = "trackid";
    // Celkova vzdialenost
    public static final String COL_SUMMARY_DISTANCE = "distance";
    // Celkovy cas
    public static final String COL_SUMMARY_TIME = "time";
    // Cas pohybu
    public static final String COL_SUMMARY_MOVTIME = "movtime";
    // Max.rychlost
    public static final String COL_SUMMARY_MAXSPEED= "maxspeed";
    // Max.vyska
    public static final String COL_SUMMARY_MAXALT = "maxalt";
    // Priemerna rychlost
    public static final String COL_SUMMARY_AVGSPEED = "avgspeed";
    // Nazov trasy
    public static final String COL_SUMMARY_ROUTEDESC = "routedesc";
    // Typ aktivity (chodza, beh, bike,..)
    public static final String COL_SUMMARY_ACTTYPE = "acttype";
    // Status trasy (prebieha, ukoncena,..)
    public static final String COL_SUMMARY_STATUS = "status";

    // Podrobnosti tabulky TABLE_TRACKER_IDENTITY
    // Jednoznacny identifikator uzivatela
    public static final String COL_IDENTITY_UID = "uid";
    // Meno uzivatela
    public static final String COL_IDENTITY_FIRSTNAME = "firstname";
    // Priezvisko uzivatela
    public static final String COL_IDENTITY_LASTNAME = "lastname";
    // e-mail uzivatela
    public static final String COL_IDENTITY_EMAIL = "email";

    // Podrobnosti tabulky TABLE_TRACKER_BASE
    // Poskytovatel merania (GPS, NETWORK,..)
    public static final String COL_BASE_PROVIDER = "provider";
    // Presnost merania
    public static final String COL_BASE_ACCURACCY = "accuracy";
    // Cas merania
    public static final String COL_BASE_TIME = "time";
    // Longitude
    public static final String COL_BASE_LONGITUDE = "longitude";
    // Latitude
    public static final String COL_BASE_LATITUDE = "latitude";
    // Vyska
    public static final String COL_BASE_ALTITUDE = "altitude";
    // Bearing
    public static final String COL_BASE_BEARING = "bearing";
    // Rychlost
    public static final String COL_BASE_SPEED = "speed";
    // Identifikator trasy pre ktoru sa robi meranie
    public static final String COL_BASE_TRACKID = "trackid";
    // Prejdena vzdialenost od posledneho merana
    public static final String COL_BASE_DISTANCE = "distance";
    // Pocet nameranych satelitov
    public static final String COL_BASE_SATELLITES = "satellites";

    private TablesAPI() {}
}
