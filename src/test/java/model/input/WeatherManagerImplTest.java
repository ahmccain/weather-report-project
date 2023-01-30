package model.input;

import model.input.cache.WeatherCache;
import model.object.City;
import model.object.CurrentWeather;
import model.object.Datum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class WeatherManagerImplTest {

    private WeatherCache cache;
    private WeatherStrategy strategy;
    private WeatherManager manager;
    private ResultSet resultSet;

    @BeforeEach
    void setup() {
        cache = mock(WeatherCache.class);
        strategy = mock(WeatherStrategy.class);
        manager = new WeatherManagerImpl(strategy, cache);
        resultSet = mock(ResultSet.class);
    }

    @Test
    void setupTest() {
        assertThat(manager.getCities().size(), is(23524));
        assertThat(manager.getSearchedCity(), is(nullValue()));
        assertThat(manager.getSearchedCities(), is(empty()));
        assertThat(manager.getSearchedCitiesWeather(), is(empty()));
        assertThat(manager.getSearchedCityWeather(), is(nullValue()));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void inCacheNullEmpty(String s) {
        assertThrows(IllegalArgumentException.class, () -> manager.inCache(s));
    }

    @Test
    void inCacheNoID() {
        assertThrows(NumberFormatException.class, () -> manager.inCache("sydney, australia"));
    }

    @Test
    void inCacheInvalidID() {
        assertThrows(IllegalStateException.class, () -> manager.inCache("Sydney, New South Wales, Australia, 0"));
    }

    @Test
    void inCacheWrongCity() {
        assertThrows(IllegalStateException.class, () -> manager.inCache("Sydneya, New South Wales, Australia, 2147714"));
    }

    @Test
    void inCacheDataLengthThreeWrong() {
        assertThrows(IllegalStateException.class, () -> manager.inCache("Swords, Irelaand, 2961297"));
    }

    @Test
    void inCacheDataLengthFourWrong() {
        assertThrows(IllegalStateException.class, () -> manager.inCache("Sydney, New South Waales, Australia, 2147714"));
    }

    @Test
    void inCacheDataLengthFourWrong2() {
        assertThrows(IllegalStateException.class, () -> manager.inCache("Sydney, New South Wales, Australiaa, 2147714"));
    }

    @Test
    void inCacheValidCorrectSearchedCity() throws SQLException {
        when(cache.executeQuery(anyString(), anyInt())).thenReturn(resultSet);
        when(resultSet.isBeforeFirst()).thenReturn(false);

        assertThat(manager.inCache("Sydney, New South Wales, Australia, 2147714"), is(false));
        City city = manager.getSearchedCity();
        assertThat(city.getCityName(), is("Sydney"));
        assertThat(city.getStateName(), is("New South Wales"));
        assertThat(city.getCountryFull(), is("Australia"));
        assertThat(city.getCityId(), is(2147714));

        assertThat(manager.getSearchedCities().size(), is(1));
        city = manager.getSearchedCities().get(0);
        assertThat(city.getCityName(), is("Sydney"));
        assertThat(city.getStateName(), is("New South Wales"));
        assertThat(city.getCountryFull(), is("Australia"));
        assertThat(city.getCityId(), is(2147714));

        verify(cache, times(1)).executeQuery("""
                SELECT *
                FROM Weather
                WHERE city_id = ?""", 2147714);
    }

    @Test
    void inCacheValidSameCityTwiceCorrectList() throws SQLException {
        when(cache.executeQuery(anyString(), anyInt())).thenReturn(resultSet);
        when(resultSet.isBeforeFirst()).thenReturn(false).thenReturn(true);

        manager.inCache("Sydney, New South Wales, Australia, 2147714");
        assertThat(manager.inCache("Sydney, New South Wales, Australia, 2147714"), is(true));
        assertThat(manager.getSearchedCities().size(), is(1));

        verify(cache, times(2)).executeQuery("""
                SELECT *
                FROM Weather
                WHERE city_id = ?""", 2147714);
    }

    @Test
    void inCacheValidDifferentCityCorrectList() throws SQLException {
        when(cache.executeQuery(anyString(), anyInt())).thenReturn(resultSet);
        when(resultSet.isBeforeFirst()).thenReturn(false).thenReturn(false).thenReturn(false);

        manager.inCache("Sydney, New South Wales, Australia, 2147714");
        City syd = manager.getSearchedCity();
        assertThat(manager.inCache("Melbourne, Victoria, Australia, 2158177"), is(false));
        City melb = manager.getSearchedCity();
        assertThat(manager.inCache("Swords, Ireland, 2961297"), is(false));
        City swords = manager.getSearchedCity();

        assertThat(manager.getSearchedCities().size(), is(3));
        assertThat(manager.getSearchedCities().contains(syd), is(true));
        assertThat(manager.getSearchedCities().contains(melb), is(true));
        assertThat(manager.getSearchedCities().contains(swords), is(true));

        verify(cache, times(1)).executeQuery("""
                SELECT *
                FROM Weather
                WHERE city_id = ?""", 2147714);
        verify(cache, times(1)).executeQuery("""
                SELECT *
                FROM Weather
                WHERE city_id = ?""", 2158177);
        verify(cache, times(1)).executeQuery("""
                SELECT *
                FROM Weather
                WHERE city_id = ?""", 2961297);
    }

    @Test
    void getWeatherCached() throws SQLException {
        when(resultSet.getString("city_name")).thenReturn("Sydney");
        when(resultSet.getDouble("temp")).thenReturn(1.0);
        when(resultSet.getDouble("wind_spd")).thenReturn(1.0);
        when(resultSet.getDouble("wind_dir")).thenReturn(1.0);
        when(resultSet.getDouble("clouds")).thenReturn(1.0);
        when(resultSet.getDouble("precip")).thenReturn(1.0);
        when(resultSet.getDouble("aqi")).thenReturn(1.0);
        when(cache.executeQuery(anyString(), anyInt())).thenReturn(resultSet);

        manager.inCache("Sydney, New South Wales, Australia, 2147714");

        Datum datum = manager.getWeatherCache(true);
        assertThat(datum.getCityName(), is("Sydney"));
        assertThat(datum.getTemp(), is(1.0));
        assertThat(datum.getWindSpd(), is(1.0));
        assertThat(datum.getWindDir(), is(1.0));
        assertThat(datum.getClouds(), is(1.0));
        assertThat(datum.getPrecip(), is(1.0));
        assertThat(datum.getAqi(), is(1.0));
        assertThat(manager.getSearchedCitiesWeather().size(), is(1));
        assertThat(manager.getSearchedCitiesWeather().get(0), is(datum));
        assertThat(manager.getSearchedCityWeather(), is(datum));

        verify(cache, times(2)).executeQuery("""
                SELECT *
                FROM Weather
                WHERE city_id = ?""", 2147714);
    }

    @Test
    void getWeatherNotCached() throws SQLException {
        Datum datum = new Datum("Sydney", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        datum.setAppTemp(1.0);
        List<Datum> data = List.of(datum);
        CurrentWeather weather = new CurrentWeather(data);

        when(strategy.getCityCurrentWeather(anyInt())).thenReturn(weather);
        when(cache.executeQuery(anyString(), anyInt())).thenReturn(resultSet);

        manager.inCache("Sydney, New South Wales, Australia, 2147714");

        assertThat(manager.getWeatherCache(false), is(datum));
        assertThat(manager.getSearchedCityWeather(), is(datum));
        assertThat(manager.getSearchedCitiesWeather().size(), is(1));
        assertThat(manager.getSearchedCitiesWeather().get(0), is(datum));

        Object[] objects = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2147714};
        verify(cache, times(1)).executeUpdate("""
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

    @Test
    void getFreshWeather() throws SQLException {
        Datum datum = new Datum("Sydney", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        datum.setAppTemp(1.0);
        List<Datum> data = List.of(datum);
        CurrentWeather weather = new CurrentWeather(data);

        when(strategy.getCityCurrentWeather(anyInt())).thenReturn(weather);
        when(cache.executeQuery(anyString(), anyInt())).thenReturn(resultSet);

        manager.inCache("Sydney, New South Wales, Australia, 2147714");

        assertThat(manager.getWeatherNoCache(), is(datum));
        assertThat(manager.getSearchedCityWeather(), is(datum));
        assertThat(manager.getSearchedCitiesWeather().size(), is(1));
        assertThat(manager.getSearchedCitiesWeather().get(0), is(datum));

        Object[] objects = {2147714, "Sydney", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
        verify(cache, times(1)).executeUpdate("""
                INSERT INTO Weather(city_id, city_name, temp, wind_spd, wind_dir, clouds, precip, aqi, app_temp) VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?)""", objects);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void removeNullEmptyFromSearch(String s) {
        assertThrows(IllegalArgumentException.class, () -> manager.removeCityFromSearch(s));
    }

    @Test
    void removeNoID() {
        assertThrows(NumberFormatException.class, () -> manager.removeCityFromSearch("sydney, australia"));
    }

    @Test
    void removeInvalidID() {
        assertThrows(IllegalStateException.class, () -> manager.removeCityFromSearch("Sydney, New South Wales, Australia, 0"));
    }

    @Test
    void removeWrongCity() {
        assertThrows(IllegalStateException.class, () -> manager.removeCityFromSearch("Sydneya, New South Wales, Australia, 2147714"));
    }

    @Test
    void removeDataLengthThreeWrong() {
        assertThrows(IllegalStateException.class, () -> manager.removeCityFromSearch("Swords, Irelaand, 2961297"));
    }

    @Test
    void removeDataLengthFourWrong() {
        assertThrows(IllegalStateException.class, () -> manager.removeCityFromSearch("Sydney, New South Waales, Australia, 2147714"));
    }

    @Test
    void removeDataLengthFourWrong2() {
        assertThrows(IllegalStateException.class, () -> manager.removeCityFromSearch("Sydney, New South Wales, Australiaa, 2147714"));
    }

    @Test
    void removeValidNotInList() {
        assertThrows(IllegalStateException.class, () -> manager.removeCityFromSearch("Sydney, New South Wales, Australia, 2147714"));
    }

    @Test
    void removeValid() throws SQLException {
        when(cache.executeQuery(anyString(), anyInt())).thenReturn(resultSet);

        manager.inCache("Sydney, New South Wales, Australia, 2147714");
        City city = manager.getSearchedCity();
        manager.removeCityFromSearch("Sydney, New South Wales, Australia, 2147714");

        assertThat(manager.getRemovedCity(), is(city));
        assertThat(manager.getSearchedCities(), is(empty()));

        Object[] objects = {2147714};
        verify(cache, times(1)).executeUpdate("""
                DELETE FROM Weather
                WHERE city_id = ?""", objects);
    }

    @Test
    void removeSameTwice() throws SQLException {
        when(cache.executeQuery(anyString(), anyInt())).thenReturn(resultSet);
        manager.inCache("Sydney, New South Wales, Australia, 2147714");
        manager.removeCityFromSearch("Sydney, New South Wales, Australia, 2147714");

        assertThrows(IllegalStateException.class, () -> manager.removeCityFromSearch("Sydney, New South Wales, Australia, 2147714"));
        assertThat(manager.getSearchedCities(), is(empty()));

        Object[] objects = {2147714};
        verify(cache, times(1)).executeUpdate("""
                DELETE FROM Weather
                WHERE city_id = ?""", objects);
    }

    @Test
    void clearTest() {
        manager.clear();
        assertThat(manager.getSearchedCities(), is(empty()));
        assertThat(manager.getSearchedCity(), is(nullValue()));
        assertThat(manager.getSearchedCitiesWeather(), is(empty()));
        assertThat(manager.getSearchedCityWeather(), is(nullValue()));
    }

    @Test
    void generateEmptyReport() {
        String s = "Did you select a city yet? There is currently no weather to report";
        assertThat(manager.generateWeatherReport().size(), is(1));
        assertThat(manager.generateWeatherReport(), containsInAnyOrder(s));
    }

    @Test
    void generateReport() throws SQLException {
        Datum datum = new Datum("Sydney", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        List<Datum> data = List.of(datum);
        CurrentWeather weather = new CurrentWeather(data);

        Datum datum1 = new Datum("Swords", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        List<Datum> data1 = List.of(datum1);
        CurrentWeather weather1 = new CurrentWeather(data1);
        when(strategy.getCityCurrentWeather(anyInt())).thenReturn(weather).thenReturn(weather1);
        when(cache.executeQuery(anyString(), anyInt())).thenReturn(resultSet);

        manager.inCache("Sydney, New South Wales, Australia, 2147714");
        manager.getWeatherNoCache();
        manager.inCache("Swords, Ireland, 2961297");
        manager.getWeatherNoCache();

        String syd = "<br>Current weather for Sydney" +
                "<br/>Temperature is now 1.0" + "\u00B0C" +
                "<br/>Wind speed is 1.0" + " m/s" +
                "<br/>Wind direction is 1.0" + "\u00B0" +
                "<br/>1.0 cloud(s)" +
                "<br/>Precipitation in mm/hr: 1.0" +
                "<br/>Air quality index is 1.0";
        String swo = "<br>Current weather for Swords" +
                "<br/>Temperature is now 1.0" + "\u00B0C" +
                "<br/>Wind speed is 1.0" + " m/s" +
                "<br/>Wind direction is 1.0" + "\u00B0" +
                "<br/>1.0 cloud(s)" +
                "<br/>Precipitation in mm/hr: 1.0" +
                "<br/>Air quality index is 1.0";
        assertThat(manager.generateWeatherReport(), contains(syd, swo));
    }

    @Test
    void clearCache() throws SQLException {
        manager.clearCache();
        verify(cache, times(1)).executeUpdate("""
                DELETE FROM Weather""");
    }


    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"two", "1.5"})
    void setGapWord(String s) {
        assertThrows(NumberFormatException.class, () -> manager.setTempGap(s));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "11"})
    void setGapLimits(String s) {
        assertThrows(IllegalArgumentException.class, () -> manager.setTempGap(s));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2", "10"})
    void setGapValid(String s) {
        manager.setTempGap(s);
    }

    @Test
    void tempGapNull() {
        assertThat(manager.tempDiffBiggerThanGap(), is(false));
    }

    @Test
    void tempGapInvalid() throws SQLException {
        Datum datum = new Datum("Sydney", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        datum.setAppTemp(1.0);
        List<Datum> data = List.of(datum);
        CurrentWeather weather = new CurrentWeather(data);

        when(strategy.getCityCurrentWeather(anyInt())).thenReturn(weather);
        when(cache.executeQuery(anyString(), anyInt())).thenReturn(resultSet);

        manager.inCache("Sydney, New South Wales, Australia, 2147714");
        manager.getWeatherNoCache();
        manager.setTempGap("2");

        assertThat(manager.tempDiffBiggerThanGap(), is(false));
    }

    @Test
    void tempGapInvalid2() throws SQLException {
        Datum datum = new Datum("Sydney", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        datum.setAppTemp(11.0);
        List<Datum> data = List.of(datum);
        CurrentWeather weather = new CurrentWeather(data);

        when(strategy.getCityCurrentWeather(anyInt())).thenReturn(weather);
        when(cache.executeQuery(anyString(), anyInt())).thenReturn(resultSet);

        manager.inCache("Sydney, New South Wales, Australia, 2147714");
        manager.getWeatherNoCache();
        manager.setTempGap("10");

        assertThat(manager.tempDiffBiggerThanGap(), is(false));
    }

    @Test
    void tempGapValid() throws SQLException {
        Datum datum = new Datum("Sydney", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        datum.setAppTemp(3.1);
        List<Datum> data = List.of(datum);
        CurrentWeather weather = new CurrentWeather(data);

        when(strategy.getCityCurrentWeather(anyInt())).thenReturn(weather);
        when(cache.executeQuery(anyString(), anyInt())).thenReturn(resultSet);

        manager.inCache("Sydney, New South Wales, Australia, 2147714");
        manager.getWeatherNoCache();
        manager.setTempGap("2");

        assertThat(manager.tempDiffBiggerThanGap(), is(true));
    }

    @Test
    void tempGapValid2() throws SQLException {
        Datum datum = new Datum("Sydney", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        datum.setAppTemp(11.1);
        List<Datum> data = List.of(datum);
        CurrentWeather weather = new CurrentWeather(data);

        when(strategy.getCityCurrentWeather(anyInt())).thenReturn(weather);
        when(cache.executeQuery(anyString(), anyInt())).thenReturn(resultSet);

        manager.inCache("Sydney, New South Wales, Australia, 2147714");
        manager.getWeatherNoCache();
        manager.setTempGap("10");

        assertThat(manager.tempDiffBiggerThanGap(), is(true));
    }

    @Test
    void tempGapValidCache() throws SQLException {
        when(resultSet.getString("city_name")).thenReturn("Sydney");
        when(resultSet.getDouble("temp")).thenReturn(1.0);
        when(resultSet.getDouble("wind_spd")).thenReturn(1.0);
        when(resultSet.getDouble("wind_dir")).thenReturn(1.0);
        when(resultSet.getDouble("clouds")).thenReturn(1.0);
        when(resultSet.getDouble("precip")).thenReturn(1.0);
        when(resultSet.getDouble("aqi")).thenReturn(1.0);
        when(resultSet.getDouble("app_temp")).thenReturn(11.1);
        when(cache.executeQuery(anyString(), anyInt())).thenReturn(resultSet);

        manager.inCache("Sydney, New South Wales, Australia, 2147714");

        manager.getWeatherCache(true);
        manager.setTempGap("10");

        assertThat(manager.tempDiffBiggerThanGap(), is(true));
    }

    @Test
    void tempGapNotCached() throws SQLException {
        Datum datum = new Datum("Sydney", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        datum.setAppTemp(11.1);
        List<Datum> data = List.of(datum);
        CurrentWeather weather = new CurrentWeather(data);

        when(strategy.getCityCurrentWeather(anyInt())).thenReturn(weather);
        when(cache.executeQuery(anyString(), anyInt())).thenReturn(resultSet);

        manager.inCache("Sydney, New South Wales, Australia, 2147714");

        manager.getWeatherCache(false);
        manager.setTempGap("10");

        assertThat(manager.tempDiffBiggerThanGap(), is(true));
    }
}
