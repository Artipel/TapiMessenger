package messenger.controller;

/**
 * Implement this interface to connect TapiMessenger with another database.
 * From implemented database connection information about phone number will be retrieved.
 */
public interface DBController {
    /**
     * Evaluate phone number of a user with given session id.
     * @param session application session of a user
     * @return phone number
     */
    String getNumberFromSession(String session) throws Exception;

}
