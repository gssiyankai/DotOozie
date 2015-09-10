package oozieviz.launch;

import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

import static oozieviz.Constants.*;

public final class Options {

    public File workflow;
    @Option(name = "-workflow", aliases = {"-w"}, usage = "To set the worflow.xml location", required = true)
    public void setWorkflow(File f) throws FileNotFoundException {
        this.workflow = file(f, WORKFLOW_XML);
    }

    public File dot;
    @Option(name = "-dot", aliases = {"-d"}, usage = "To set the location of the dot executable", required = true)
    public void setDot(File f) throws FileNotFoundException {
        this.dot = file(f, DOT_EXE);
    }

    public Optional<File> jobProperties = Optional.empty();
    @Option(name = "-job-properties", aliases = {"-j"}, usage = "To set the location of the job properties")
    public void setJobProperties(File f) throws FileNotFoundException {
        this.jobProperties = Optional.of(file(f, JOB_PROPERTIES));
    }

    private File file(File file, String fileName) throws FileNotFoundException {
        File f = new File(file, fileName);
        if (!f.exists()) {
            f = file;
        }
        if (!f.exists()) {
            throw new FileNotFoundException(String.format("%s not found at %s", fileName, f.getAbsolutePath()));
        }
        return f;
    }

}
