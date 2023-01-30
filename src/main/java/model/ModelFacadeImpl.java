package model;

import model.input.WeatherManager;
import model.object.City;
import model.object.Datum;
import model.object.Email;
import model.output.EmailManager;

import java.util.Collection;
import java.util.List;

public class ModelFacadeImpl implements ModelFacade {
    private final WeatherManager weatherManager;
    private final EmailManager emailManager;

    public ModelFacadeImpl(WeatherManager weatherManager, EmailManager emailManager) {
        this.weatherManager = weatherManager;
        this.emailManager = emailManager;
    }

    @Override
    public Collection<City> getCities() {
        return weatherManager.getCities();
    }

    @Override
    public List<City> getSearchedCities() {
        return weatherManager.getSearchedCities();
    }

    @Override
    public void clear() {
        weatherManager.clear();
    }

    @Override
    public void removeCityFromSearch(String search) {
        if (search == null || search.isEmpty()) {
            return;
        }
        weatherManager.removeCityFromSearch(search);
    }

    @Override
    public City getSearchedCity() {
        return weatherManager.getSearchedCity();
    }

    @Override
    public City getRemovedCity() {
        return weatherManager.getRemovedCity();
    }

    @Override
    public boolean sendReport(String toEmail) {
        Email email = emailManager.styleReport(toEmail, weatherManager.generateWeatherReport());
        return emailManager.sendReport(email);
    }

    @Override
    public boolean inCache(String search) {
        if (search == null || search.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return weatherManager.inCache(search);
    }

    @Override
    public Datum getWeatherCache(boolean cached) {
        return weatherManager.getWeatherCache(cached);
    }

    @Override
    public Datum getWeatherNoCache() {
        return weatherManager.getWeatherNoCache();
    }

    @Override
    public Datum getSearchedCityWeather() {
        return weatherManager.getSearchedCityWeather();
    }

    @Override
    public List<Datum> getSearchedCitiesWeather() {
        return weatherManager.getSearchedCitiesWeather();
    }

    @Override
    public void clearCache() {
        weatherManager.clearCache();
    }

    @Override
    public void setTempGap(String text) {
        weatherManager.setTempGap(text);
    }

    @Override
    public boolean tempDiffBiggerThanGap() {
        return weatherManager.tempDiffBiggerThanGap();
    }


}
