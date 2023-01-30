package model.input.cache;

import java.io.File;
import java.sql.*;

public class OnlineWeatherCache implements WeatherCache {
    private static final String dbName = "weather.db";
    private Connection connection;

    public OnlineWeatherCache() {
        String dbURL = "jdbc:sqlite:weather.db";
//        removeDB();
        createDB();
        try {
            connection = DriverManager.getConnection(dbURL);
            String createWeather =
                    """
                            CREATE TABLE IF NOT EXISTS Weather (
                                city_id integer PRIMARY KEY,
                                city_name text NOT NULL,
                                temp float,
                                wind_spd float,
                                wind_dir float,
                                clouds float,
                                precip float,
                                aqi float,
                                app_temp float
                            );
                            """;
            connection.createStatement().execute(createWeather);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createDB() {
        File dbFile = new File(dbName);
        if (dbFile.exists()) {
            System.out.println("Database already created");
        }
    }

    private void removeDB() {
        File dbFile = new File(dbName);
        if (dbFile.exists()) {
            boolean result = dbFile.delete();
            if (!result) {
                System.out.println("Couldn't delete existing db file");
                System.exit(-1);
            } else {
                System.out.println("Removed existing DB file.");
            }
        } else {
            System.out.println("No existing DB file.");
        }
    }

    @Override
    public ResultSet executeQuery(String sql, Integer cityId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setObject(1, cityId);
        return preparedStatement.executeQuery();
    }

    @Override
    public void executeUpdate(String sql) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.executeUpdate();
    }

    @Override
    public void executeUpdate(String sql, Object[] objects) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < objects.length; i++) {
            preparedStatement.setObject(i + 1, objects[i]);
        }
        preparedStatement.executeUpdate();
    }
}
