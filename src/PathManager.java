import java.nio.file.Path;

interface PathManager {
    Path getOutputPath();
    void setOutputPath(String path);
    void createDirectoryIfNeeded();
}