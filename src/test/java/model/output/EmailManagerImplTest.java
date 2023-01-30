package model.output;

import model.object.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class EmailManagerImplTest {
    private EmailManager manager;
    private EmailStrategy strategy;
    private RandomGenerator generator;

    @BeforeEach
    void setup() {
        strategy = mock(EmailStrategy.class);
        generator = mock(RandomGenerator.class);
        manager = new EmailManagerImpl(strategy, generator);
    }

    @Test
    void sendReport() {
        Email email = new Email();
        when(strategy.sendReport(email)).thenReturn(true);

        assertThat(manager.sendReport(email), is(true));
        verify(strategy, times(1)).sendReport(email);
    }

    @Test
    void styleReport() {
        when(generator.nextInt(8)).thenReturn(0).thenReturn(1);

        List<String> reportList = new ArrayList<>();
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
        reportList.add(syd);
        reportList.add(swo);


        StringBuilder email = new StringBuilder();
        try {
            InputStream is = new FileInputStream("src/test/resources/email.html");
            BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String str;
            while ((str = in.readLine()) != null) {
                email.append(str);
            }
            in.close();
        } catch (IOException e) {
            System.err.println("Cannot read html email");
        }

        String value = email.toString();

        Email email1 = manager.styleReport("test@gmail.com", reportList);
        assertThat(email1.getContent().get(0).getValue(), is(value));
    }
}
