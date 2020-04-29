package no.digipat.compare.models.project;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ProjectTest {

    @Test
    public void testSetFields() {
        Long id = 20L;
        String name = "Project Name";
        Boolean active = true;

        Project project = new Project().setId(id).setName(name).setActive(active);

        assertEquals(project.getId(), id);
        assertEquals(project.getName(), name);
        assertEquals(project.getActive(), active);
        assertNotEquals(project.getId(), name);
    }
}
