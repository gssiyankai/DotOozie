package oozieviz;

import java.io.File;
import java.nio.file.Paths;

public class ResourceHelper {

    public static File resourceFile(String name) throws Exception {
        return Paths.get(ResourceHelper.class.getResource("/" + name).toURI()).toFile();
    }

}
