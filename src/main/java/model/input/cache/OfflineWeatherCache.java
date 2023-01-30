package model.input.cache;

import java.sql.ResultSet;

public class OfflineWeatherCache implements WeatherCache {
    private final OfflineResultSet offlineResultSet = new OfflineResultSet();

    @Override
    public ResultSet executeQuery(String sql, Integer cityId) {
        return offlineResultSet;
    }

    @Override
    public void executeUpdate(String sql) {

    }

    @Override
    public void executeUpdate(String sql, Object[] objects) {

    }
}
