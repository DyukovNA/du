import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class SizeOfFilesTest {
    String test(String input) {
        String[] args = input.split(" ");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        System.setOut(printStream);

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
        assertEquals("2", test("f1.txt")); //default in KB
        //multiple fies
        assertEquals("2 0", test("f1.txt f2.txt"));
        //counting size of directory
        int sizeOfDir1 = 68796518;
        assertEquals(Integer.toString(sizeOfDir1 / 1024), test("dir1"));
        //for human read
        assertEquals(sizeOfDir1 + " B", test("-h B dir1"));
        assertEquals(sizeOfDir1 / 1024 + " KB", test("-h KB dir1"));
        assertEquals((int) (sizeOfDir1 / Math.pow(2, 1024)) + " MB", test("-h MB dir1"));
        assertEquals((int) (sizeOfDir1 / Math.pow(3, 1024)) + " GB", test("-h GB dir1"));
        //no unit given
        assertEquals("Unsupported unit of measure", test("-h f1.txt"));
        //not existing unit
        assertEquals("Unsupported unit of measure", test("-h HEHE f1.txt"));
        //unit > GB
        assertEquals("Unsupported unit of measure", test("-h TB f1.txt"));
        //total sum
        int sum12 = 2414 + 609;
        assertEquals(Integer.toString(sum12 / 1024), test("-c f1.txt f2.txt"));
        //different base
        assertNotEquals(test("--si f3.pdf"), test("f3.pdf"));
        //arguments compatibility
        int totalSum = sum12 + 14120412 + sizeOfDir1;
        assertEquals(totalSum + " B", test("-h B -c f1.txt f2.txt f3.pdf dir1"));
        assertNotEquals(test("-h KB f1.txt f2.txt f3.pdf dir1"),
                test("-h KB --si f1.txt f2.txt f3.pdf dir1"));
        assertNotEquals(test("-h KB -c f1.txt f2.txt f3.pdf dir1"),
                test("-h KB -c --si f1.txt f2.txt f3.pdf dir1"));
    }
}