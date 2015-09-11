package oozieviz.utils;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

public class ExecutableTest {

    private Executable exe;

    @Before
    public void setup() throws Exception {
        exe = new Executable(
                    Paths.get(System.getProperty("java.home"), "bin", "java").toFile());
    }

    @Test
    public void it_executes_java() throws Exception {
        exe.withArguments("-version")
           .run();
    }

    @Test(expected = ExecutionException.class)
    public void it_throws_an_exception_with_invalid_arguments() throws Exception {
        exe.withArguments("-invalid_arg")
                .run();
    }

}
