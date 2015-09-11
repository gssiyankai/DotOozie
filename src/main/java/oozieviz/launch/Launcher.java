package oozieviz.launch;

import oozieviz.OozieViz;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public final class Launcher {

    private Launcher() {
    }

    public static void main(String[] args) throws Exception {
        Options options = new Options();

        CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);

            OozieViz oozieViz = new OozieViz();
            oozieViz.givenDot(options.dot)
                    .givenJobProperties(options.jobProperties)
                    .fromWorkflow(options.workflow)
                    .exportToSvg();

        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("USAGE:");
            parser.printUsage(System.err);
        }

    }

}
