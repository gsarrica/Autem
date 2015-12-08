package io.autem;

/**
 * Created by gsarrica on 12/7/15.
 */
public class AutemConversation {

    private AutemContact autemContact;
    private AutemTextMessage autemTextMessage;

    public AutemContact getAutemContact() {
        return autemContact;
    }

    public void setAutemContact(AutemContact autemContact) {
        this.autemContact = autemContact;
    }

    public AutemTextMessage getAutemTextMessage() {
        return autemTextMessage;
    }

    public void setAutemTextMessage(AutemTextMessage autemTextMessage) {
        this.autemTextMessage = autemTextMessage;
    }

    @Override
    public String toString() {
        return "AutemConversation{" +
                "autemContact=" + autemContact +
                ", autemTextMessage=" + autemTextMessage +
                '}';
    }
}
