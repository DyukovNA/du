import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class SizeOfFiles {
    private boolean isForHumanRead = false;
    private String unitOfMeasure;
    private boolean isTotalSizeRequired = false;
    private boolean isDifferentBaseRequired = false;
    private int base = 1024;

    private final List<String> fileNames = new ArrayList<>();

    public static void main(String[] args) {
        new SizeOfFiles().returnSizeOfFiles(args);
    }

    public void returnSizeOfFiles(String[] args) {
        try {
            parse(args);
        } catch (IllegalArgumentException e) {
            System.out.println("Unsupported unit of measure");
            return;
        }
        try {
            if (isTotalSizeRequired) {
                totalSize();
            } else {
                eachSize();
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Can not find one of the files");
        } catch (NullPointerException e) {
            System.out.println("No filenames given");
        }
    }

    private void totalSize() throws NullPointerException {
        if (fileNames.isEmpty()) throw new NullPointerException();
        long total = 0;
        for (String name : fileNames) total += fileSize(name);
        String inFormatTotal = toFormat(total);
        System.out.println(inFormatTotal);
    }

    private void eachSize() {
        if (fileNames.isEmpty()) throw new NullPointerException();
        List<String> sizes = new ArrayList<>();
        for (String name : fileNames) {
            long size = fileSize(name);
            String inFormatSize = toFormat(size);
            sizes.add(inFormatSize);
        }
        StringBuilder output = new StringBuilder();
        for (String str: sizes) {
            output.append(str).append(" ");
        }
        System.out.println(output.toString().strip());
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

    private String toFormat(long bytes) {

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
        } else {
            long defKiloBytes = (bytes / base);
            return (defKiloBytes + "");
        }

    }

    private void parse(String[] args) throws IllegalArgumentException {
        setFlags(args);
        setFileNames(args);
        /*System.out.println("isForHumanRead = " + isForHumanRead);
        System.out.println("isTotalSizeRequired = " + isTotalSizeRequired);
        System.out.println("unitOfMeasure = " + unitOfMeasure);
        System.out.println("base = " + base);
        System.out.println("fileNames = " + fileNames);*/
    }

    private void setFileNames(String[] args) {
        Pattern patternIsFileName = Pattern.compile("\\w+.\\w+");
        for (String arg : args) {
            if (patternIsFileName.matcher(arg).matches()) fileNames.add(arg);
        }
    }

    private void setFlags(String[] args) throws IllegalArgumentException {
        for (String arg : args) {
            setIsForHumanRead(arg);
            if (isForHumanRead && unitOfMeasure == null) {
                setUnitOfMeasure(arg);
            }
            setIsTotalSizeRequired(arg);
            setIsDifferentBaseRequired(arg);
        }
        if (isForHumanRead && unitOfMeasure == null) throw new IllegalArgumentException();
    }

    private void setIsForHumanRead(String input) {
        if (!isForHumanRead) {
            Pattern patternForHumanRead = Pattern.compile("(-h)");
            isForHumanRead = patternForHumanRead.matcher(input).find();
        }
    }

    private void setUnitOfMeasure(String arg) throws IllegalArgumentException {
        Pattern patternForUnit = Pattern.compile("(\\w{1,2})");
        if (patternForUnit.matcher(arg).matches()) {
            if (arg.equals("B") || arg.equals("KB") || arg.equals("MB") || arg.equals("GB"))
                unitOfMeasure = arg;
            else throw new IllegalArgumentException();
        }
    }

    private void setIsTotalSizeRequired(String input) {
        if (!isTotalSizeRequired) {
            Pattern patternForTotalSize = Pattern.compile("(-c)");
            isTotalSizeRequired = patternForTotalSize.matcher(input).find();
        }
    }

    private void setIsDifferentBaseRequired(String input) {
        if (!isDifferentBaseRequired) {
            Pattern patternForDifferentBase = Pattern.compile("(--si)");
            isDifferentBaseRequired = patternForDifferentBase.matcher(input).find();
            if (isDifferentBaseRequired) base = 1000;
        }
    }
}
