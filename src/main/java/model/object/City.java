package model.object;

public class City {
    private final Integer cityId;
    private final String cityName;
    private final String stateCode;
    private final String countryCode;
    private final String countryFull;
    private final Location location;
    private final String stateName;

    public City(Integer cityId, String cityName, String stateCode, String countryCode, String countryFull, Double lat, Double lon, String stateName) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.stateCode = stateCode;
        this.countryCode = countryCode;
        this.countryFull = countryFull;
        this.location = new Location(lat, lon);
        this.stateName = stateName;
    }

    public Integer getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public String getStateCode() {
        return stateCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryFull() {
        return countryFull;
    }

    public Location getLocation() {
        return location;
    }

    public String getStateName() {
        return stateName;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (cityName != null) {
            stringBuilder.append(cityName);
        }
        if (stateName != null) {
            stringBuilder.append(", ").append(stateName);
        }
        if (countryFull != null && !countryFull.isEmpty()) {
            stringBuilder.append(", ").append(countryFull);
        }
        stringBuilder.append(", ").append(cityId);
        return stringBuilder.toString();
    }
}
