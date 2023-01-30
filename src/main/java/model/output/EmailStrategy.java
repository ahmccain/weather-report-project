package model.output;

import model.object.Email;

public interface EmailStrategy {
    /**
     *
     * @param email The email object to be converted to JSON for sending.
     * @return Whether the email was successfully sent.
     */
    boolean sendReport(Email email);
}
