package heusc.Utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import org.tinylog.Logger;

/**
 * Manages text files in the data folder of this program
 */
public class TextFile {
    private static final String dataPathPrefix = "heusc\\data\\";
    private static final String txtExtension = ".txt";

    /**
     * creates a txt file in heusc data folder
     * @param fileName name of file, treat as a key
     * @param persistant if false, will delete the file on program termination
     * @return true if the named file does not exist and was successfully created; false if the named file already exists or ioexception
     */
    public static boolean create(String fileName, boolean persistant) {
        try {
            File file = new File(dataPathPrefix + fileName + txtExtension);
            if (!persistant) {
                file.deleteOnExit();
            }
            return file.createNewFile();
        } catch (IOException e) {
            Logger.error(e);
            return false;
        }
    }
    
    public static boolean setContent(String fileName, String content) {
        File file = new File(dataPathPrefix + fileName + txtExtension);
        return writeToFile(file, content, false);
    }

    public static boolean appendContent(String fileName, String content) {
        File file = new File(dataPathPrefix + fileName + txtExtension);
        return writeToFile(file, content, true);
    }

    public static Optional<String> readContent(String fileName) {
        File file = new File(dataPathPrefix + fileName + txtExtension);
        if (!fileExistsAndIsVisible(file)) {
            return Optional.empty();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }

            if (content.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(content.toString());
            }
        } catch (IOException e) {
            Logger.error(e);
            return Optional.empty();
        }
    }

    public static boolean deleteFile(String fileName) {
        File file = new File(dataPathPrefix + fileName + txtExtension);
        if (!fileExistsAndIsVisible(file)) {
            return false;
        }

        return file.delete();
    }

    public static Optional<File> getFile(String fileName) {
        File file = new File(dataPathPrefix + fileName + txtExtension);
        if (fileExistsAndIsVisible(file)) {
            return Optional.of(file);
        } else {
            return Optional.empty();
        }
    }

    private static boolean writeToFile(File file, String content, boolean append) {
        if (!fileExistsAndIsVisible(file)) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, append))) {
            writer.write(content);
            writer.close();
            return true;
        } catch (IOException e) {
            Logger.error(e);
            return false;
        }
    }

    private static boolean fileExistsAndIsVisible(File file) {
        if (!file.exists()) {
            System.err.println("File " + file.getAbsolutePath() + " does not exist.");
            return false;
        }

        if (!file.canWrite()) {
            System.err.println("Cannot write to file " + file.getAbsolutePath());
            return false;
        }

        if (!file.canRead()) {
            System.err.println("Cannot read file " + file.getAbsolutePath());
            return false;
        }

        return true;
    }
}
