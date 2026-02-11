import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePathManager implements PathManager {
    private Path outputPath;

    public FilePathManager() {
        this.outputPath = Paths.get(".");
    }

    @Override
    public Path getOutputPath() {
        return outputPath;
    }

    @Override
    public void setOutputPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Путь не может быть пустым!");
        }
        this.outputPath = Paths.get(path);
    }


    @Override
    public void createDirectoryIfNeeded() {
        File dir = outputPath.toFile();
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new RuntimeException("Не удалось создать директорию: " + outputPath);
            }
        }
    }
}