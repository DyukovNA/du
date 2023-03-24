import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class SizeOfFilesTest {
    String test(String input) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        System.setOut(printStream);

        String[] args;
        if (!Objects.equals(input, "")) {
            args = input.split(" ");
        } else {
            args = new String[]{};
        }
        SizeOfFiles.main(args);

        String[] lines = baos.toString().split(System.lineSeparator());
        String actual = lines[lines.length - 1];

        return actual;
    }

    @Test
    void main() {
        //no files
        assertEquals("No filenames given", test(""));
        //one file
        assertEquals("2", test("input/f1.txt")); //default in KB
        //multiple fies
        assertEquals("2 0", test("input/f1.txt input/f2.txt"));
        //counting size of directory
        int sizeOfDir1 = 68796416;
        assertEquals(Integer.toString(sizeOfDir1 / 1024), test("input/dir1"));
        //for human read
        int sizeOfF3 = 14120412;
        assertEquals(sizeOfF3 + " B", test("-h B input/f3.pdf"));
        assertEquals(sizeOfF3 / 1024 + " KB", test("-h KB input/f3.pdf"));
        assertEquals((int) (sizeOfF3 / Math.pow(2, 1024)) + " MB", test("-h MB input/f3.pdf"));
        assertEquals((int) (sizeOfF3 / Math.pow(3, 1024)) + " GB", test("-h GB input/f3.pdf"));
        //not existing unit
        assertEquals("Unsupported unit of measure", test("-h HEHE input/f1.txt"));
        //unit > GB
        assertEquals("Unsupported unit of measure", test("-h TB input/f1.txt"));
        //total sum
        int sum12 = 2414 + 609;
        assertEquals(Integer.toString(sum12 / 1024), test("-c input/f1.txt input/f2.txt"));
        //different base
        assertNotEquals(test("--si f3.pdf"), test("input/f3.pdf"));
        //arguments compatibility
        int totalSum = sum12 + sizeOfF3;
        assertEquals(totalSum + " B", test("-h B -c input/f1.txt input/f2.txt input/f3.pdf"));
        assertNotEquals(test("-h KB input/f1.txt input/f2.txt input/f3.pdf input/dir1"),
                test("-h KB --si input/f1.txt input/f2.txt input/f3.pdf input/dir1"));
        assertNotEquals(test("-h KB -c input/f1.txt input/f2.txt input/f3.pdf input/dir1"),
                test("-h KB -c --si input/f1.txt input/f2.txt input/f3.pdf input/dir1"));
    }
}