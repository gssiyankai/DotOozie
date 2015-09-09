package org.dotoozie.launch;

import org.dotoozie.DotOozie;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import static org.dotoozie.DotOozieFactory.newDotOozie;

public final class Launcher {

    private Launcher() {
    }

    public static void main(String[] args) throws Exception {
        LauncherOptions options = new LauncherOptions();

        CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("USAGE:");
            parser.printUsage(System.err);
        }

        DotOozie dotOozie = newDotOozie(options.recursive);
        dotOozie.givenDot(options.dot)
                .fromWorkflow(options.workflow)
                .exportTo(options.format);
    }

}
