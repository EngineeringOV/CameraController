package ventures.of.util;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
    public static void copyFile(BufferedReader reader, String destinationFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        public static void writeToFile(String path, String content) {
            Path filePath = Paths.get(path);
            try {
                Files.write(filePath, content.getBytes());
                System.out.println("File written successfully!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public static BufferedReader readerFileFromJar(String path) {
        return new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(path)));
    }

    public static BufferedReader readerFromFile(String path) throws FileNotFoundException, URISyntaxException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(path)));
    }

}
