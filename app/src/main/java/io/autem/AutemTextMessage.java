package io.autem;

import org.joda.time.LocalDateTime;

/**
 * Created by gsarrica on 12/1/15.
 */
public class AutemTextMessage {
    private String contactName;
    private String from;
    private String to;
    private String message;
    private LocalDateTime timestamp;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AutemTextMessage{" +
                "contactName='" + contactName + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
