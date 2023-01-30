package model.output;

import model.object.Content;
import model.object.Email;
import model.object.EmailName;
import model.object.Personalization;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.random.RandomGenerator;

public class EmailManagerImpl implements EmailManager {
    private final EmailStrategy strategy;
    private final String SENDGRID_API_EMAIL;
    private final RandomGenerator generator;

    public EmailManagerImpl(EmailStrategy strategy, RandomGenerator generator) {
        this.SENDGRID_API_EMAIL = System.getenv("SENDGRID_API_EMAIL");
        this.strategy = strategy;
        this.generator = generator;
    }

    @Override
    public boolean sendReport(Email email) {
        return strategy.sendReport(email);
    }

    @Override
    public Email styleReport(String toEmail, List<String> reportList) {
        StringBuilder report = new StringBuilder();
        String[] colors = {"FireBrick", "DeepPink", "Tomato", "DarkKhaki", "BlueViolet", "Olive", "Goldenrod", "SlateGray"};

        for (String weather : reportList) {
            int i = generator.nextInt(colors.length);
            String colorName = colors[i];

            report.append("<p style=\"color :")
                    .append(colorName)
                    .append(";\">")
                    .append(weather)
                    .append("<br/><br/></p>");
        }

        StringBuilder email = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader("src/main/resources/email.html"));
            String str;
            while ((str = in.readLine()) != null) {
                email.append(str);
            }
            in.close();
        } catch (IOException e) {
            System.err.println("Cannot read html email");
        }

        String value = email.toString();
        value = value.replace("$body", report);

        EmailName to = new EmailName(toEmail, "");
        Personalization personalization = new Personalization(List.of(to), "Weather Report");
        Content content = new Content("text/html", value);
        EmailName from = new EmailName(SENDGRID_API_EMAIL, "");
        EmailName replyTo = new EmailName(SENDGRID_API_EMAIL, "");
        return new Email(List.of(personalization), List.of(content), from, replyTo);
    }
}
