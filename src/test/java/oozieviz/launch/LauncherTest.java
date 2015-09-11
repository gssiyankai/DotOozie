package oozieviz.launch;

import org.junit.Test;

import java.io.File;

import static oozieviz.Constants.WORKFLOW_SVG;
import static oozieviz.ResourceHelper.resourceFile;
import static org.fest.assertions.Assertions.assertThat;

public class LauncherTest {

    @Test
    public void it_fails_when_path_to_dot_executable_is_not_provided() throws Exception {
        File workflow = resourceFile("simple_workflow.xml");
        Launcher.main(new String[] { "-w", workflow.getAbsolutePath() });
        assertThat(new File(workflow.getParent(), WORKFLOW_SVG))
                .doesNotExist();
    }

}
