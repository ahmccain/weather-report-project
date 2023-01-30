package model.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Email {

    @SerializedName("personalizations")
    @Expose
    private List<Personalization> personalizations;
    @SerializedName("content")
    @Expose
    private List<Content> content;
    @SerializedName("from")
    @Expose
    private EmailName from;
    @SerializedName("reply_to")
    @Expose
    private EmailName replyTo;

    public Email(List<Personalization> personalizations, List<Content> content, EmailName from, EmailName replyTo) {
        this.personalizations = personalizations;
        this.content = content;
        this.from = from;
        this.replyTo = replyTo;
    }

    public Email() {

    }

    public List<Personalization> getPersonalizations() {
        return personalizations;
    }

    public void setPersonalizations(List<Personalization> personalizations) {
        this.personalizations = personalizations;
    }

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    public EmailName getFrom() {
        return from;
    }

    public void setFrom(EmailName from) {
        this.from = from;
    }

    public EmailName getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(EmailName replyTo) {
        this.replyTo = replyTo;
    }

}