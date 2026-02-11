import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

class FileSavingService {
    private final PathManager pathManager;
    private final FileNameGenerator nameGenerator;

    public FileSavingService(PathManager pathManager, FileNameGenerator nameGenerator) {
        this.pathManager = pathManager;
        this.nameGenerator = nameGenerator;
    }

    public Path getFullFilePath(String suffix, String extension) {
        String fileName = nameGenerator.generateFileName(suffix, extension);
        return pathManager.getOutputPath().resolve(fileName);
    }

    public void saveToFile(String suffix, String extension, List<?> data, boolean append) throws IOException {
        if (data == null || data.isEmpty()) {
            return; // Не создаем пустые файлы
        }

        Path filePath = getFullFilePath(suffix, extension);
        pathManager.createDirectoryIfNeeded();

        try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                append ? StandardOpenOption.APPEND : StandardOpenOption.CREATE,
                StandardOpenOption.WRITE)) {
            for (Object item : data) {
                writer.write(item.toString());
                writer.newLine();
            }
        }
    }

    public PathManager getPathManager() {
        return pathManager;
    }

    public FileNameGenerator getNameGenerator() {
        return nameGenerator;
    }
}