package model.input;

import model.ModelFacade;
import model.ModelFacadeImpl;
import model.input.cache.OnlineWeatherCache;
import model.object.CurrentWeather;
import model.object.Datum;
import model.output.EmailManager;
import model.output.EmailStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class WeatherStrategyTest {
    private WeatherStrategy weatherStrategy;
    private EmailStrategy emailStrategy;
    private ModelFacade modelFacade;
    private OnlineWeatherCache cache;

    private OfflineWeather offlineWeather;
    private OnlineWeather onlineWeather;

    @BeforeEach
    void setUp() {
        weatherStrategy = mock(WeatherStrategy.class);
        emailStrategy = mock(EmailStrategy.class);
        cache = new OnlineWeatherCache();
//        modelFacade = new ModelFacadeImpl(weatherStrategy, emailStrategy);
        WeatherManager weatherManager = mock(WeatherManager.class);
        EmailManager emailManager = mock(EmailManager.class);
        modelFacade = new ModelFacadeImpl(weatherManager, emailManager);

        offlineWeather = new OfflineWeather();
        onlineWeather = new OnlineWeather();
    }

    @Test
    void offlineGetWeather1() {
        CurrentWeather weather = offlineWeather.getCityCurrentWeather(1);
        assertThat(weather, is(notNullValue()));

        Datum datum = weather.getData().get(0);
        assertThat(datum.getAqi(), is(45.0));
        assertThat(datum.getPrecip(), is(0.0));
        assertThat(datum.getClouds(), is(75.0));
        assertThat(datum.getTemp(), is(24.19));
        assertThat(datum.getWindDir(), is(50.0));
        assertThat(datum.getWindSpd(), is(6.17));
    }

    @Test
    void onlineGetWeather() {
        weatherStrategy = mock(OnlineWeather.class);
        CurrentWeather cw = mock(CurrentWeather.class);
        when(weatherStrategy.getCityCurrentWeather(anyInt())).thenReturn(cw);
        CurrentWeather cw1 = weatherStrategy.getCityCurrentWeather(1);
        assertThat(cw, is(cw1));
        verify(weatherStrategy, times(1)).getCityCurrentWeather(anyInt());
    }

    @Test
    void offlineGetWeather() {
        weatherStrategy = new OfflineWeather();
        CurrentWeather cw = weatherStrategy.getCityCurrentWeather(1);
        Datum d = cw.getData().get(0);
        assertThat(cw, is(notNullValue()));
        assertThat(d.getCityName(), is("Raleigh"));
        assertThat(d.getTemp(), is(24.19));
        assertThat(d.getWindSpd(), is(6.17));
        assertThat(d.getWindDir(), is(50.0));
        assertThat(d.getClouds(), is(75.0));
        assertThat(d.getPrecip(), is(0.0));
        assertThat(d.getAqi(), is(45.0));
        assertThat(d.getLat(), is("35.7721"));
        assertThat(d.getLon(), is("-78.63861"));
    }

}
