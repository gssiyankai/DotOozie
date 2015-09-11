package oozieviz.workflow.job;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Optional;

import static oozieviz.Constants.JOB_PROPERTIES;
import static oozieviz.utils.ResourceHelper.resourceFile;
import static oozieviz.workflow.job.JobProperties.newJobProperties;
import static org.fest.assertions.Assertions.assertThat;

public class PathResolverTest {

    private PathResolver resolver;

    @Before
    public void setup() throws Exception {
        resolver  = new PathResolver(
                            newJobProperties(
                                    Optional.of(resourceFile(JOB_PROPERTIES))));
    }

    @Test
    public void it_resolves_paths() {
        assertThat(resolver.resolvePath("/a/b/${sub_workflow_folder}/c"))
                .isEqualTo("/a/b/sub-wf/c");
    }

    @Test
    public void it_relativize_paths() throws Exception {
        assertThat(resolver.relativize(
                                resourceFile("action_workflow.xml").getAbsolutePath(),
                                resourceFile("decision_workflow.xml").getAbsolutePath()))
                .isEqualTo(".." + File.separator + "decision_workflow.xml");
    }

}
