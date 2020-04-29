package no.digipat.compare.models.image;

import static org.junit.Assert.*;

import org.junit.Test;

import no.digipat.compare.models.session.Session;

public class SessionTest {
    @Test
    public void testSetFields() {
        String id = "TESTID";
        String hospital = "TESTHOSPITAL";
        String monitorType = "TESTMONITOR";
        Long projectId = 123L;
        Session session = new Session().setId(id).setHospital(hospital).setMonitorType(monitorType)
                .setProjectId(projectId);

        assertEquals(session.getId(), id);
        assertEquals(session.getHospital(), hospital);
        assertEquals(session.getMonitorType(), monitorType);
        assertEquals(projectId, session.getProjectId());
        assertNotEquals(session.getId(), hospital);

    }

}
