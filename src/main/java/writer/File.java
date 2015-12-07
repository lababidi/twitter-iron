package writer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

/**
 *
 * Created by mahmoud on 4/24/15.
 */
public class File extends Writer {
    int counter = 0;
    private String directoryName;
    private String extension;

    public File(BlockingQueue<String> queue, String directoryName){
        super(queue);
        this.directoryName = directoryName;
        this.runBatch = true;
        extension = ".json";
    }

    @Override
    public void write(String message) {
        writeFile(String.valueOf(++counter) + extension, message);
    }

    @Override
    public void write(Iterable<String> messages){
        writeFile(String.valueOf(++counter) + extension, String.join("\n", messages));
    }

    public void writeFile(String fileName, String content){
        try {
            java.io.File file = new java.io.File(this.directoryName + "/" + fileName);
            if(!file.getParentFile().exists() && !file.getParentFile().mkdirs())
                throw new FileNotFoundException("Unable to create Directory: " + this.directoryName);

            PrintWriter writer = new PrintWriter(file);
            writer.write(content);
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }






}
