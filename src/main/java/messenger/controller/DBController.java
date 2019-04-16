package messenger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DBController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public String getNumberFromSession(String session) {
        System.out.println(jdbcTemplate);
        String result = jdbcTemplate.query("SELECT telefon " +
                "FROM " +
                "adm$uzytkownicy WHERE konto = ( SELECT user_name FROM APEX_WORKSPACE_SESSIONS WHERE apex_session_id = ? )", new Object[]{ session },
                (rs, rownum) -> new String(rs.getString(1))).get(0);
        return result;
    }

}
