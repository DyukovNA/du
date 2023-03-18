import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class SizeOfFiles {
    private boolean isForHumanRead = false;
    private String unitOfMeasure;
    private boolean isTotalSizeRequired = false;
    private boolean isDifferentBaseRequired = false;
    private int base = 1024;

    private final List<String> fileNames = new ArrayList<>();

    public static void main(String[] args) {
        String line = "[-h B] filename1.txt filename2.txt";
        System.out.println(Arrays.stream(args).toList());
        String[] ass = line.split(" ");
        Path path = Paths.get("filename1.txt").toAbsolutePath();
        SizeOfFiles abomination = new SizeOfFiles();
        abomination.parse(args);
        abomination.returnSizeOfFiles();
    }

    public void returnSizeOfFiles() {
        try {
            if (isTotalSizeRequired) {
                totalSize();
            } else {
                eachSize();
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Can not find one of the files");
        }
    }

    private void totalSize() {
        long total = 0;
        for (String name : fileNames) total += fileSize(name);
        String inFormatTotal = toFormat(total);
        System.out.println(inFormatTotal);
    }

    private void eachSize() {
        for (String name : fileNames) {
            long size = fileSize(name);
            String inFormatSize = toFormat(size);
            System.out.println(inFormatSize);
        }
    }

    private long fileSize(String fileName) {
        Path path = Paths.get(fileName);
        File file = new File(fileName);
        if (file.exists()) {
            return file.length();
        } else {
            throw new IllegalArgumentException();
        }
    }

    private String toFormat(long bytes) {
        switch (unitOfMeasure) {
            case "B" -> {
                return (bytes + " B");
            }
            case "KB" -> {
                long kiloBytes = (bytes / base);
                return (kiloBytes + " KB");
            }
            case "GB" -> {
                long gigaBytes = (long) (bytes / Math.pow(2, base));
                return (gigaBytes + " GB");
            }
            default -> {
                long defKiloBytes = (bytes / base);
                return (defKiloBytes + "");
            }
        }
    }

    public void parse(String[] args) {
        setFlags(args);
        setFileNames(args);
        System.out.println("isForHumanRead = " + isForHumanRead);
        System.out.println("isTotalSizeRequired = " + isTotalSizeRequired);
        System.out.println("unitOfMeasure = " + unitOfMeasure);
        System.out.println("base = " + base);
        System.out.println("fileNames = " + fileNames);
    }

    private void setFileNames(String[] args) {
        Pattern patternIsFileName = Pattern.compile("\\w+.\\w+");
        for (String arg : args) {
            if (patternIsFileName.matcher(arg).matches()) fileNames.add(arg);
        }
    }

    private void setFlags(String[] args) {
        for (String arg : args) {
            setIsForHumanRead(arg);
            if (isForHumanRead) setUnitOfMeasure(arg);
            setIsTotalSizeRequired(arg);
            setIsDifferentBaseRequired(arg);
        }
    }

    private void setIsForHumanRead(String input) {
        if (!isForHumanRead) {
            Pattern patternForHumanRead = Pattern.compile("(-h)");
            isForHumanRead = patternForHumanRead.matcher(input).find();
        }
    }

    public void setUnitOfMeasure(String arg) {
        Pattern patternForUnit = Pattern.compile("(\\w{1,2}])");
        if (patternForUnit.matcher(arg).matches()) {
            String unit = arg.replace("]", "");
            if (unit.equals("B") || unit.equals("KB") || unit.equals("MB") || unit.equals("GB"))
                unitOfMeasure = unit;
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
