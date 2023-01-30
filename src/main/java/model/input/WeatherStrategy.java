package model.input;

import model.object.CurrentWeather;

public interface WeatherStrategy {
    /**
     *
     * @param cityId The city ID.
     * @return The weather data for the city.
     */
    CurrentWeather getCityCurrentWeather(Integer cityId);
}
