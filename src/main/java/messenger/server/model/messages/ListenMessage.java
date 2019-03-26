package messenger.server.model.messages;

public class ListenMessage {
    private String number;

    public ListenMessage() {
    }

    public ListenMessage(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }
}
