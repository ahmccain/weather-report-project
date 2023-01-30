package model.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("wind_cdir")
    @Expose
    private String windCdir;
    @SerializedName("rh")
    @Expose
    private Double rh;
    @SerializedName("pod")
    @Expose
    private String pod;
    @SerializedName("lon")
    @Expose
    private String lon;
    @SerializedName("pres")
    @Expose
    private Double pres;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("ob_time")
    @Expose
    private String obTime;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("clouds")
    @Expose
    private Double clouds;
    @SerializedName("vis")
    @Expose
    private Double vis;
    @SerializedName("wind_spd")
    @Expose
    private Double windSpd;
    @SerializedName("wind_cdir_full")
    @Expose
    private String windCdirFull;
    @SerializedName("app_temp")
    @Expose
    private Double appTemp;
    @SerializedName("state_code")
    @Expose
    private String stateCode;
    @SerializedName("ts")
    @Expose
    private Double ts;
    @SerializedName("h_angle")
    @Expose
    private Double hAngle;
    @SerializedName("dewpt")
    @Expose
    private Double dewpt;
    @SerializedName("weather")
    @Expose
    private Weather weather;
    @SerializedName("uv")
    @Expose
    private Double uv;
    @SerializedName("aqi")
    @Expose
    private Double aqi;
    @SerializedName("station")
    @Expose
    private String station;
    @SerializedName("wind_dir")
    @Expose
    private Double windDir;
    @SerializedName("elev_angle")
    @Expose
    private Double elevAngle;
    @SerializedName("datetime")
    @Expose
    private String datetime;
    @SerializedName("precip")
    @Expose
    private Double precip;
    @SerializedName("ghi")
    @Expose
    private Double ghi;
    @SerializedName("dni")
    @Expose
    private Double dni;
    @SerializedName("dhi")
    @Expose
    private Double dhi;
    @SerializedName("solar_rad")
    @Expose
    private Double solarRad;
    @SerializedName("city_name")
    @Expose
    private String cityName;
    @SerializedName("sunrise")
    @Expose
    private String sunrise;
    @SerializedName("sunset")
    @Expose
    private String sunset;
    @SerializedName("temp")
    @Expose
    private Double temp;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("slp")
    @Expose
    private Double slp;

    public Datum() {
    }

    public Datum(String cityName, Double temp, Double windSpd, Double windDir, Double clouds, Double precip, Double aqi) {
        this.cityName = cityName;
        this.temp = temp;
        this.windSpd = windSpd;
        this.windDir = windDir;
        this.clouds = clouds;
        this.precip = precip;
        this.aqi = aqi;
    }

    public String getLon() {
        return lon;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public Double getClouds() {
        return clouds;
    }

    public void setClouds(Double clouds) {
        this.clouds = clouds;
    }

    public Double getWindSpd() {
        return windSpd;
    }

    public void setWindSpd(Double windSpd) {
        this.windSpd = windSpd;
    }

    public String getStateCode() {
        return stateCode;
    }

    public Weather getWeather() {
        return weather;
    }

    public Double getAqi() {
        return aqi;
    }

    public void setAqi(Double aqi) {
        this.aqi = aqi;
    }

    public Double getWindDir() {
        return windDir;
    }

    public void setWindDir(Double windDir) {
        this.windDir = windDir;
    }

    public Double getPrecip() {
        return precip;
    }

    public void setPrecip(Double precip) {
        this.precip = precip;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public String getLat() {
        return lat;
    }

    public Double getAppTemp() {
        return appTemp;
    }

    public void setAppTemp(Double appTemp) {
        this.appTemp = appTemp;
    }

    @Override
    public String toString() {
        return "Current weather for " + cityName +
                "\nTemperature is now " + temp + "\u00B0C" +
                "\nWind speed is " + windSpd + " m/s" +
                "\nWind direction is " + windDir + "\u00B0" +
                "\n" + clouds + " cloud(s)" +
                "\nPrecipitation in mm/hr: " + precip +
                "\nAir quality index is " + aqi;
    }

    public String toStringReport() {
        return "<br>Current weather for " + cityName +
                "<br/>Temperature is now " + temp + "\u00B0C" +
                "<br/>Wind speed is " + windSpd + " m/s" +
                "<br/>Wind direction is " + windDir + "\u00B0" +
                "<br/>" + clouds + " cloud(s)" +
                "<br/>Precipitation in mm/hr: " + precip +
                "<br/>Air quality index is " + aqi;
    }
}