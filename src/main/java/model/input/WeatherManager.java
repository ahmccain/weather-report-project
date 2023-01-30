package model.input;

import model.object.City;
import model.object.Datum;

import java.util.Collection;
import java.util.List;

public interface WeatherManager {
    /**
     * @return The list of cities from the dataset.
     */
    Collection<City> getCities();

    /**
     * @return The list of cities that have been entered as a search term.
     */
    List<City> getSearchedCities();

    /**
     * @return The most recently searched city.
     */
    City getSearchedCity();

    /**
     * @return The most recently removed city.
     */
    City getRemovedCity();

    /**
     * @return The weather data of the searched city.
     */
    Datum getSearchedCityWeather();

    /**
     * @return The list of weather data for all the previously searched cities.
     */
    List<Datum> getSearchedCitiesWeather();

    /**
     * Checks whether the city has available cache data. Sets the searched city for weather retrieval.
     *
     * @param search The city details entered as a search term.
     * @return Whether the city has weather data in the cache.
     */
    boolean inCache(String search);

    /**
     * Returns the weather data either from the cache or fresh. Updates cache if fresh.
     *
     * @param cached Whether to use the cache.
     * @return The weather data.
     */
    Datum getWeatherCache(boolean cached);

    /**
     * Returns the weather data. Adds to the cache.
     *
     * @return The fresh weather data.
     */
    Datum getWeatherNoCache();

    /**
     * Removes a city's weather data from the user history.
     *
     * @param search The city details as selected by drop down.
     */
    void removeCityFromSearch(String search);

    /**
     * Clears all the user history, including cities searched and their weather data.
     */
    void clear();

    /**
     * Creates a list of human-readable weather data from the searched cities.
     *
     * @return The list of weather data. Default value if no searched cities.
     */
    List<String> generateWeatherReport();

    /**
     * Wipes the cache.
     */
    void clearCache();

    /**
     * Sets temperature gap threshold.
     *
     * @param text Number between 2 and 10 inclusive.
     */
    void setTempGap(String text);


    /**
     * Checks if the absolute difference in apparent temperature and temperature for the searched city is bigger than the threshold.
     *
     * @return Whether the difference is bigger than the gap value provided.
     */
    boolean tempDiffBiggerThanGap();
}
