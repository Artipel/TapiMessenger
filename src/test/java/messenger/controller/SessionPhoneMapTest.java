package messenger.controller;

import org.junit.Test;

import static org.junit.Assert.*;

public class SessionPhoneMapTest {

    @Test
    public void addPair() {
        SessionPhoneMap map = new SessionPhoneMap();
        map.addPair("phone1", "session1");
        map.addPair("phone2", "session2");
        map.addPair("phone1", "session3");
        map.addPair("phone3", "session4");
        assertEquals("phone1", map.getPhone("session1"));
        assertEquals("phone2", map.getPhone("session2"));
        assertEquals("phone1", map.getPhone("session3"));
        assertEquals("session1", map.getSessions("phone1").get(0));
        assertEquals("session3", map.getSessions("phone1").get(1));
        assertEquals("session4", map.getSessions("phone3").get(0));
    }

    @Test
    public void deletePhone() {
        SessionPhoneMap map = new SessionPhoneMap();
        map.addPair("phone1", "session1");
        map.addPair("phone2", "session2");
        map.addPair("phone1", "session3");
        map.addPair("phone3", "session4");
        map.deletePhone("phone1");
        assertNull(map.getPhone("session1"));
        assertNull(map.getPhone("session3"));
        assertEquals("phone2", map.getPhone("session2"));
        assertEquals("phone3", map.getPhone("session4"));
        assertNull(map.getSessions("phone1"));
    }

    @Test
    public void deleteSession() {
        SessionPhoneMap map = new SessionPhoneMap();
        map.addPair("phone1", "session1");
        map.addPair("phone2", "session2");
        map.addPair("phone1", "session3");
        map.addPair("phone3", "session4");
        assertEquals("session1", map.getSessions("phone1").get(0));
        assertEquals(1, map.deleteSession("session1"));
        assertEquals("session3", map.getSessions("phone1").get(0));
        assertEquals(0, map.deleteSession("session3"));
        assertNull(map.getSessions("phone1"));
        assertNull(map.getPhone("session1"));
        assertNull(map.getPhone("session3"));
    }
}