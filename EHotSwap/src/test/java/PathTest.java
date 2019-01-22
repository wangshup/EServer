import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class PathTest {
    private static final Logger logger = LoggerFactory.getLogger(PathTest.class);

    @Test
    public void getAllPatches() throws IOException {
        Collection<File> patchClassFiles = FileUtils.listFiles(new File("build"), new String[]{"class"}, true);
        for(File f : patchClassFiles) {
            logger.info("name: {}, path: {}, absolute path: {}, cano path : {}", f.getName(), f.getPath(), f.getAbsolutePath(), f.getCanonicalPath());
        }
    }

    @Test
    public void getFullClassName() {
        String filePath = "patches/com/wangsp/agent/ClassModifier.class";
        int start = filePath.indexOf("/") + 1;
        int end = filePath.lastIndexOf('.');
        String fullClassPath = filePath.substring(start, end);
        String fullClassName = fullClassPath.replace("/", ".");
        logger.info("start {}, end {}, class name: {}", start, end, fullClassName);
    }
}
