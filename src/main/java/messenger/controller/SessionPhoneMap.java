package messenger.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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

    public String getPhone(String session) {
        return sessionToPhone.get(session);
    }

    public ArrayList<String> getSessions(String phone) {
        return phoneToSession.get(phone);
    }

    public void deletePhone(String phone) {
        ArrayList<String> sessions = phoneToSession.remove(phone);
        sessions.forEach(s -> sessionToPhone.remove(s));
        // sessionToPhone.remove(sessions);
    }

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

    public void addSubscriber(String subscriber, String phone) {

    }

    public void removeSubscriber(String subscriber, String phone) {

    }

    public int getSessionsCount(String phone) {
        ArrayList sessions = phoneToSession.get(phone);
        if(sessions != null)
            return phoneToSession.get(phone).size();
        else
            return 0;
    }
}
