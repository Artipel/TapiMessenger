package messenger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Connects to database and invokes query. Works with Oracle Apex application.
 */
@Service(value="apex")
public class ApexDBController implements DBController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * Retrieve a phone number of a user that is logged in to application and has given session number.
     * @param session
     * @return
     */
    public String getNumberFromSession(String session) {
        String result = jdbcTemplate.query("SELECT telefon " +
                "FROM " +
                "adm$uzytkownicy WHERE konto = ( SELECT user_name FROM APEX_WORKSPACE_SESSIONS WHERE apex_session_id = ? )", new Object[]{ session },
                (rs, rownum) -> new String(rs.getString(1))).get(0);
        return result;
    }

}
