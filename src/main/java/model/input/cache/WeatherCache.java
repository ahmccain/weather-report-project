package model.input.cache;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface WeatherCache {

    /**
     *
     * @param sql The SQL query.
     * @param cityId The city ID for the query.
     * @return The set of data from the query.
     * @throws SQLException If there is an exception when accessing the cache.
     */
    ResultSet executeQuery(String sql, Integer cityId) throws SQLException;

    /**
     *
     * @param sql The SQL query.
     * @throws SQLException If there is an exception when accessing the cache.
     */
    void executeUpdate(String sql) throws SQLException;

    /**
     *
     * @param sql The SQL query.
     * @param objects The parameters for the query.
     * @throws SQLException If there is an exception when accessing the cache.
     */
    void executeUpdate(String sql, Object[] objects) throws SQLException;
}
