package com.samudrala.Text2Speech;

import java.util.List;

public class Conversation {
    private List<Message> messages;

    public Conversation(List<Message> messages) {
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}

