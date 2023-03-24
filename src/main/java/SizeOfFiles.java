import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.kohsuke.args4j.*;
import static java.lang.System.out;

public class SizeOfFiles {
    private boolean isForHumanRead = false;
    @Option(name = "-h")
    private String unitOfMeasure;
    @Option(name = "-c")
    private boolean isTotalSizeRequired = false;
    @Option(name = "--si")
    private boolean isDifferentBaseRequired = false;
    private int base = 1024;
    @Argument()
    private List<String> fileNames = new ArrayList<>();

    public static void main(String[] args) {
        new SizeOfFiles().returnSizeOfFiles(args);
    }

    private void returnSizeOfFiles(String[] args) {
        parse(args);
        try {
            if (isTotalSizeRequired) {
                totalSize();
            } else {
                eachSize();
            }
        } catch (IllegalArgumentException e) {
            out.println("Can not find one of the files");
        } catch (NullPointerException e) {
            out.println("No filenames given");
        }
    }

    private void totalSize() throws NullPointerException {
        if (fileNames.isEmpty()) throw new NullPointerException();
        long total = 0;
        for (String name : fileNames) total += fileSize(name);
        try {
            String inFormatTotal = toFormat(total);
            out.println(inFormatTotal);
        } catch (Exception e) {
            out.println("Unsupported unit of measure");
        }
    }

    private void eachSize() throws NullPointerException {
        if (fileNames.isEmpty()) {
            throw new NullPointerException();
        }
        List<String> sizes = new ArrayList<>();
        for (String name : fileNames) {
            long size = fileSize(name);
            try {
                String inFormatSize = toFormat(size);
                sizes.add(inFormatSize);
            } catch (Exception e) {
                out.println("Unsupported unit of measure");
                return;
            }
        }
        StringBuilder output = new StringBuilder();
        for (String str : sizes) {
            output.append(str).append(" ");
        }
        out.println(output.toString().strip());
    }

    private long fileSize(String fileName) throws IllegalArgumentException {
        Path path = Paths.get(fileName);
        File file = path.toFile();
        if (file.exists()) {
            if (file.isDirectory()) {
                return getDirectorySize(file);
            }
            return file.length();
        } else {
            throw new IllegalArgumentException();
        }
    }

    private long getDirectorySize(File dir) {
        long size = 0;
        if (dir.isFile()) {
            size = dir.length();
        } else {
            File[] subFiles = dir.listFiles();
            for (File file : subFiles) {
                if (file.isFile()) {
                    size += file.length();
                } else {
                    size += getDirectorySize(file);
                }
            }
        }
        return size;
    }

    private String toFormat(long bytes) throws Exception {
        if (Objects.equals(unitOfMeasure, "B")) {
            return (bytes + " B");
        } else if (Objects.equals(unitOfMeasure, "KB")) {
            long kiloBytes = (bytes / base);
            return (kiloBytes + " KB");
        } else if (Objects.equals(unitOfMeasure, "MB")) {
            long megaBytes = (long) (bytes / Math.pow(2, base));
            return (megaBytes + " MB");
        } else if (Objects.equals(unitOfMeasure, "GB")) {
            long gigaBytes = (long) (bytes / Math.pow(3, base));
            return (gigaBytes + " GB");
        } else if (unitOfMeasure == null) {
            long defKiloBytes = (bytes / base);
            return (defKiloBytes + "");
        } else throw new Exception("Unsupported unit of measure");
    }

    private void parse(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException clEx) {
            out.println("Unable to parse command-line options");
            return;
        }
        if (isDifferentBaseRequired) base = 1000;
        if (unitOfMeasure != null) isForHumanRead = true;

        /*System.out.println("isForHumanRead = " + isForHumanRead);
        System.out.println("isTotalSizeRequired = " + isTotalSizeRequired);
        System.out.println("unitOfMeasure = " + unitOfMeasure);
        System.out.println("base = " + base);
        System.out.println("fileNames = " + fileNames);*/
    }
}
