import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.channels.FileLock;

public class FileLockTest {

    public static void main(String[] args) throws IOException {
        File file = new File("lock");
        FileOutputStream fos = new FileOutputStream(file);
        FileLock fileLock = fos.getChannel().lock();
        fileLock.release();
    }
}
