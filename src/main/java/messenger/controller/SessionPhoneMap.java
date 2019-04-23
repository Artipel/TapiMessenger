package messenger.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * This map represent 1 to n relation. 1 phone can be associated with multiple sessions, however 1 session
 * can be associated with only one phone. Geting data in both directions is supported and optimized.
 */
public class SessionPhoneMap {

    private HashMap<String, String> sessionToPhone = new HashMap<>();
    private HashMap<String, ArrayList<String>> phoneToSession = new HashMap<>();

    /**
     * Create pair or append new session to phone number.
     * @param phone
     * @param session
     * @return true if previous value of session is overwritten
     */
    public boolean addPair(String phone, String session) {
        sessionToPhone.put(session, phone);
        ArrayList<String> sessions = phoneToSession.get(phone);
        if (sessions == null){
            phoneToSession.put(phone, new ArrayList<String>(Arrays.asList(session)));
            return false;
        } else {
            sessions.add(session);
            return true;
        }
    }

    /**
     * Get phone number associated with given session
     * @param session
     * @return
     */
    public String getPhone(String session) {
        return sessionToPhone.get(session);
    }

    /**
     * Get sessions associated with given phone number.
     * @param phone
     * @return
     */
    public ArrayList<String> getSessions(String phone) {
        return phoneToSession.get(phone);
    }

    /**
     * Delete phone number from map. Delete all sessions associated with phone.
     * @param phone
     */
    public void deletePhone(String phone) {
        ArrayList<String> sessions = phoneToSession.remove(phone);
        sessions.forEach(s -> sessionToPhone.remove(s));
        // sessionToPhone.remove(sessions);
    }

    /**
     * Delete session from map. If it was the last session listening for a phone, remove phone.
     * @param session session id to be deleted
     * @return number of remaining sessions listening for phone associated with given session.
     */
    public int deleteSession(String session) {
        String phone = sessionToPhone.remove(session);
        ArrayList sessions = phoneToSession.get(phone);
        sessions.removeIf(s -> s.equals(session));
        if(sessions.size() == 0) {
            phoneToSession.remove(phone);
            return 0;
        } else
            return sessions.size();

    }

    /**
     * Get number of listeners for given phone number.
     * @param phone
     * @return
     */
    public int getSessionsCount(String phone) {
        ArrayList sessions = phoneToSession.get(phone);
        if(sessions != null)
            return phoneToSession.get(phone).size();
        else
            return 0;
    }
}
