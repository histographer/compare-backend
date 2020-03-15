package no.digipat.compare.models.image;
import static org.junit.Assert.*;

import org.junit.Test;

import no.digipat.compare.models.session.Session;

public class SessionTest {
    @Test
    public void testSetFields() {
        String ID = "TESTID";
        String HOSPITAL = "TESTHOSPITAL";
        String MONITOR_TYPE = "TESTMONITOR";
        Long PROJECT_ID = 123L;
        Session session = new Session().setId(ID).setHospital(HOSPITAL).setMonitorType(MONITOR_TYPE)
                .setProjectId(PROJECT_ID);

        assertEquals(session.getId(), ID);
        assertEquals(session.getHospital(), HOSPITAL);
        assertEquals(session.getMonitorType(), MONITOR_TYPE);
        assertEquals(PROJECT_ID, session.getProjectId());
        assertNotEquals(session.getId(), HOSPITAL);

    }

}
