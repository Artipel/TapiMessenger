package messenger.controller;

public interface WebSocketCallback {

    String listenerSubscribed(String sessionId, String applicationSessionId) throws Exception;

    void listenerUnsubscribed(String sessionId);

    void askForNewCall(String sessionId, String number);

}
