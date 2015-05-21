package sink;

/**
 * Created by mahmoud on 5/21/15.
 */
public interface WriterInterface {
    void write(String message);
    void write(Iterable<String> messages);
}
