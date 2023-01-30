package model.input;

import model.input.cache.WeatherCache;
import model.object.City;
import model.object.CurrentWeather;
import model.object.Datum;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class WeatherManagerImpl implements WeatherManager {
    private final WeatherCache cache;
    private final WeatherStrategy strategy;

    private final Map<Integer, City> cities;
    private final List<City> searchedCities;
    private final List<Datum> searchedCitiesWeather;
    private City searchedCity;
    private City removedCity;
    private Datum searchedCityWeather;
    private Integer tempGap;

    public WeatherManagerImpl(WeatherStrategy strategy, WeatherCache cache) {
        this.cache = cache;
        this.strategy = strategy;
        this.cities = new HashMap<>();
        this.searchedCities = new ArrayList<>();
        this.searchedCitiesWeather = new ArrayList<>();
        setup();
    }

    private void setup() {
        String line;
        boolean skipHeader = true;
        try {
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/output.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }
                String[] data = line.split(",");
                City city;
                Integer cityId = Integer.parseInt(data[0]);
                if (data.length == 7) {
                    city = new City(cityId, data[1], data[2], data[3], data[4], Double.parseDouble(data[5]), Double.parseDouble(data[6]), null);
                } else {
                    city = new City(cityId, data[1], data[2], data[3], data[4], Double.parseDouble(data[5]), Double.parseDouble(data[6]), data[7]);
                }
                cities.put(cityId, city);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<City> getCities() {
        return cities.values();
    }

    @Override
    public List<City> getSearchedCities() {
        return searchedCities;
    }

    @Override
    public City getSearchedCity() {
        return searchedCity;
    }

    @Override
    public City getRemovedCity() {
        return removedCity;
    }

    @Override
    public Datum getSearchedCityWeather() {
        return searchedCityWeather;
    }

    @Override
    public List<Datum> getSearchedCitiesWeather() {
        return searchedCitiesWeather;
    }

    @Override
    public boolean inCache(String search) {
        if (search == null || search.isEmpty()) {
            throw new IllegalArgumentException("Null or empty search");
        }
        try {
            String[] data = search.split(",");
            Integer cityID = Integer.parseInt(data[data.length - 1].trim());
            City city = cities.get(cityID);
            if (city == null || !city.getCityName().equals(data[0])) {
                throw new IllegalStateException("Select an auto-completed city");
            }
            if (data.length == 3) {
                if (!data[1].trim().equals(city.getStateName()) && !data[1].trim().equals(city.getCountryFull())) {
                    throw new IllegalStateException("Select an auto-completed city");
                }
            }
            if (data.length == 4) {
                if (!data[1].trim().equals(city.getStateName()) || !data[2].trim().equals(city.getCountryFull())) {
                    throw new IllegalStateException("Select an auto-completed city");
                }
            }
            searchedCity = city;
            if (!searchedCities.contains(city)) {
                searchedCities.add(city);
            }
            ResultSet resultSet = cache.executeQuery("""
                    SELECT *
                    FROM Weather
                    WHERE city_id = ?""", cityID);

            return resultSet.isBeforeFirst();

        } catch (SQLException e) {
            System.err.println("SQL exception");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Datum getWeatherCache(boolean cached) {
        Integer cityID = searchedCity.getCityId();
        try {
            if (cached) {
                ResultSet resultSet = cache.executeQuery("""
                        SELECT *
                        FROM Weather
                        WHERE city_id = ?""", cityID);
                this.searchedCityWeather = new Datum();
                searchedCityWeather.setCityName(resultSet.getString("city_name"));
                searchedCityWeather.setTemp(resultSet.getDouble("temp"));
                searchedCityWeather.setWindSpd(resultSet.getDouble("wind_spd"));
                searchedCityWeather.setWindDir(resultSet.getDouble("wind_dir"));
                searchedCityWeather.setClouds(resultSet.getDouble("clouds"));
                searchedCityWeather.setPrecip(resultSet.getDouble("precip"));
                searchedCityWeather.setAqi(resultSet.getDouble("aqi"));
                searchedCityWeather.setAppTemp(resultSet.getDouble("app_temp"));
                searchedCitiesWeather.add(searchedCityWeather);
            } else {
                CurrentWeather weather = strategy.getCityCurrentWeather(cityID);
                this.searchedCityWeather = weather.getData().get(0);
                searchedCitiesWeather.add(searchedCityWeather);
                Object[] objects = {searchedCityWeather.getTemp(), searchedCityWeather.getWindSpd(), searchedCityWeather.getWindDir(), searchedCityWeather.getClouds(), searchedCityWeather.getPrecip(), searchedCityWeather.getAqi(), searchedCityWeather.getAppTemp(), cityID};
                cache.executeUpdate("""
                        UPDATE Weather
                        SET temp = ?,
                            wind_spd = ?,
                            wind_dir = ?,
                            clouds = ?,
                            precip = ?,
                            aqi = ?,
                            app_temp = ?
                        WHERE city_id = ?""", objects);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return searchedCityWeather;
    }

    @Override
    public Datum getWeatherNoCache() {
        CurrentWeather weather = strategy.getCityCurrentWeather(searchedCity.getCityId());
        this.searchedCityWeather = weather.getData().get(0);
        searchedCitiesWeather.add(searchedCityWeather);
        Object[] objects = {searchedCity.getCityId(), searchedCity.getCityName(), searchedCityWeather.getTemp(), searchedCityWeather.getWindSpd(), searchedCityWeather.getWindDir(), searchedCityWeather.getClouds(), searchedCityWeather.getPrecip(), searchedCityWeather.getAqi(), searchedCityWeather.getAppTemp()};

        try {
            cache.executeUpdate("""
                    INSERT INTO Weather(city_id, city_name, temp, wind_spd, wind_dir, clouds, precip, aqi, app_temp) VALUES
                    (?, ?, ?, ?, ?, ?, ?, ?, ?)""", objects);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return searchedCityWeather;
    }

    @Override
    public void removeCityFromSearch(String search) {
        if (search == null || search.isEmpty()) {
            throw new IllegalArgumentException();
        }
        try {
            String[] data = search.split(",");
            Integer cityID = Integer.parseInt(data[data.length - 1].trim());
            City city = cities.get(cityID);
            if (city == null || !city.getCityName().equals(data[0])) {
                throw new IllegalStateException();
            }
            if (data.length == 3) {
                if (!data[1].trim().equals(city.getStateName()) && !data[1].trim().equals(city.getCountryFull())) {
                    throw new IllegalStateException();
                }
            }
            if (data.length == 4) {
                if (!data[1].trim().equals(city.getStateName()) || !data[2].trim().equals(city.getCountryFull())) {
                    throw new IllegalStateException();
                }
            }
            if (!searchedCities.contains(city)) {
                throw new IllegalStateException();
            }
            removedCity = city;
            searchedCities.remove(city);
            Object[] objects = {cityID};
            cache.executeUpdate("""
                    DELETE FROM Weather
                    WHERE city_id = ?""", objects);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {
        searchedCities.clear();
        searchedCity = null;
        searchedCitiesWeather.clear();
        searchedCityWeather = null;
    }

    @Override
    public List<String> generateWeatherReport() {
        List<String> reportList = new ArrayList<>();
        if (searchedCitiesWeather == null || searchedCitiesWeather.isEmpty()) {
            return List.of("Did you select a city yet? There is currently no weather to report");
        }
        for (Datum datum : searchedCitiesWeather) {
            reportList.add(datum.toStringReport());
        }
        return reportList;
    }

    @Override
    public void clearCache() {
        try {
            cache.executeUpdate("""
                    DELETE FROM Weather""");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setTempGap(String text) {
        int val = Integer.parseInt(text);
        if (val < 2 || val > 10) {
            throw new IllegalArgumentException("Number must be between 2 and 10");
        }
        this.tempGap = val;
    }

    @Override
    public boolean tempDiffBiggerThanGap() {
        if (tempGap == null) {
            return false;
        }
        return Math.abs(searchedCityWeather.getAppTemp() - searchedCityWeather.getTemp()) > tempGap;
    }
}
