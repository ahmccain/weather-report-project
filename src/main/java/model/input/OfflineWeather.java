package model.input;

import com.google.gson.Gson;
import model.object.CurrentWeather;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;


public class OfflineWeather implements WeatherStrategy {
    @Override
    public CurrentWeather getCityCurrentWeather(Integer city_id) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("src/main/resources/raleigh.json"));
            return gson.fromJson(reader, CurrentWeather.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
