package model.input;

import com.google.gson.Gson;
import model.object.CurrentWeather;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OnlineWeather implements WeatherStrategy {
    private final String INPUT_API_KEY = System.getenv("INPUT_API_KEY");
    private final HttpClient client;
    private final Gson gson;

    public OnlineWeather() {
        this.client = HttpClient.newBuilder().build();
        this.gson = new Gson();
    }

    @Override
    public CurrentWeather getCityCurrentWeather(Integer cityId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.weatherbit.io/v2.0/current?city_id=" + cityId + "&key=" + INPUT_API_KEY + "&include=minutely"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return gson.fromJson(response.body(), CurrentWeather.class);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
