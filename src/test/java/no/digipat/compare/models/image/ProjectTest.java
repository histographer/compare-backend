package no.digipat.compare.models.image;
import no.digipat.compare.models.project.Project;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ProjectTest {

    @Test
    public void testSetFields() {
        Long ID = 20l;
        String NAME = "Project Name";

        Project project = new Project().setId(ID).setName(NAME);

        assertEquals(project.getId(), ID);
        assertEquals(project.getName(), NAME);
        assertNotEquals(project.getId(), NAME);
    }
}
