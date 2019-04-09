package messenger.controller;

import java.util.HashMap;

public class SessionPhoneMap {

    private HashMap<String, String> sessionToPhone = new HashMap<>();
    private HashMap<String, String> phoneToSession = new HashMap<>();

    /**
     *
     * @param phone
     * @param session
     * @return true if previous value of session is overwritten
     */
    public boolean addPair(String phone, String session) {
        sessionToPhone.put(session, phone);
        return phoneToSession.put(phone, session) != null;
    }

    public String getPhone(String session) {
        return sessionToPhone.get(session);
    }

    public String getSession(String phone) {
        return phoneToSession.get(phone);
    }

    public void deletePairByPhone(String phone) {
        String session = phoneToSession.remove(phone);
        sessionToPhone.remove(session);
    }

    public void deletePairBySession(String session) {
        String phone = phoneToSession.remove(session);
        sessionToPhone.remove(phone);
    }
}
