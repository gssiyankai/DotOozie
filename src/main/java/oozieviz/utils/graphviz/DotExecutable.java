package oozieviz.utils.graphviz;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class DotExecutable {

    private final File dot;
    private String[] args;

    public DotExecutable(File dot) {
        this.dot = dot;
    }

    public DotExecutable withArguments(String... args) {
        this.args = args;
        return this;
    }

    public DotExecutable run() throws Exception {
        List<String> commands = new LinkedList<>();
        commands.add(dot.getAbsolutePath());
        commands.addAll(Arrays.asList(args));
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        int exitValue = p.waitFor();
        if (exitValue != 0) {
            throw new ExecutionException(String.format("Failed executing command: %s", String.join(" ", pb.command())));
        }
        return this;
    }

}
