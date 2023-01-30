package model.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Personalization {

    @SerializedName("to")
    @Expose
    private List<EmailName> to;
    @SerializedName("subject")
    @Expose
    private String subject;

    public Personalization(List<EmailName> to, String subject) {
        this.to = to;
        this.subject = subject;
    }

    public List<EmailName> getTo() {
        return to;
    }

    public void setTo(List<EmailName> to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

}