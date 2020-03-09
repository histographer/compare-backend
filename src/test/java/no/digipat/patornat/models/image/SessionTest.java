package no.digipat.patornat.models.image;
import static org.junit.Assert.*;

import no.digipat.patornat.models.session.Session;
import org.junit.Test;

public class SessionTest {
    @Test
    public void testSetFields() {
        String ID = "TESTID";
        String HOSPITAL = "TESTHOSPITAL";
        String MONITOR_TYPE = "TESTMONITOR";
        Session session = new Session().setId(ID).setHospital(HOSPITAL).setMonitorType(MONITOR_TYPE);

        assertEquals(session.getId(), ID);
        assertEquals(session.getHospital(), HOSPITAL);
        assertEquals(session.getMonitorType(), MONITOR_TYPE);
        assertNotEquals(session.getId(), HOSPITAL);

    }

}
