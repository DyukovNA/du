import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SizeOfFilesTest {
    @Test
    void returnSizeOfFiles() {
        //List<String> args1 = "a b c".split(" ");
        assertEquals("Can not find one of the files", returnSizeOfFiles());
    }
}