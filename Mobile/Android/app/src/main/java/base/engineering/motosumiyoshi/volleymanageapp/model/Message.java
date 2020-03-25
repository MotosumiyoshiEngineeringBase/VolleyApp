package base.engineering.motosumiyoshi.volleymanageapp.model;

import java.util.List;

public class Message {

    private String id;
    private String name;
    private List<FriendlyMessage> messages;

    public Message () {}

    public Message (String id, String name, List<FriendlyMessage> messages) {
        this.id = id;
        this.name = name;
        this.messages = messages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FriendlyMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<FriendlyMessage> messages) {
        this.messages = messages;
    }
}
