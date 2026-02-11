public class FilePrefixManager implements FileNameGenerator {
    private String filePrefix;
    private static final String DEFAULT_PREFIX = "";

    public FilePrefixManager() {
        this.filePrefix = DEFAULT_PREFIX;
    }

    @Override
    public void setPrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new IllegalArgumentException("Префикс не может быть пустым!");
        }
        this.filePrefix = sanitizePrefix(prefix);
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    @Override
    public String generateFileName(String suffix, String extension) {
        if (extension == null || extension.trim().isEmpty()) {
            extension = ".txt";
        }
        if (!extension.startsWith(".")) {
            extension = "." + extension;
        }
        return filePrefix + suffix + extension;
    }

    private String sanitizePrefix(String prefix) {
        return prefix.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}