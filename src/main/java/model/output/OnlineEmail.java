package model.output;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.object.Email;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OnlineEmail implements EmailStrategy {
    private final String SENDGRID_API_KEY;
    private final HttpClient client;
    private final Gson gson;

    public OnlineEmail() {
        this.SENDGRID_API_KEY = System.getenv("SENDGRID_API_KEY");
        this.client = HttpClient.newHttpClient();
        this.gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    @Override
    public boolean sendReport(Email email) {
        String emailJson = gson.toJson(email);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.sendgrid.com/v3/mail/send"))
                .headers("Authorization", "Bearer ".concat(SENDGRID_API_KEY), "Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(emailJson))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() < 400;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
