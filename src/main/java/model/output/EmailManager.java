package model.output;

import model.object.Email;

import java.util.List;

public interface EmailManager {
    /**
     *
     * @param email The email object to be converted to JSON for sending.
     * @return Whether the email was successfully sent.
     */
    boolean sendReport(Email email);

    /**
     * Styles the email to add image, heading and randomly colours text. Constructs the email object for sending.
     * @param toEmail The email address to send the report to.
     * @param reportList The list of each searched cities' weather data in human-readable form.
     * @return The email object.
     */
    Email styleReport(String toEmail, List<String> reportList);
}
