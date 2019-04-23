package messenger.controller;

/**
 * Implement this interface to connect TapiMessenger with another database.
 * From implemented database connection information about phone number will be retrieved.
 */
public interface DBController {

    public String getNumberFromSession(String session);

}
