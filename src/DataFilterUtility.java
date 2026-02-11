import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataFilterUtility {
    private static final Logger logger = Logger.getLogger(DataFilterUtility.class.getName());

    private final FileSavingService fileService;
    private boolean appendMode = false;
    private boolean showShortStats = false;
    private boolean showFullStats = false;
    private String inputFilePath;

    public DataFilterUtility() {
        this.fileService = new FileSavingService(new FilePathManager(), new FilePrefixManager());
    }

    // Метод для отображения меню
    public void showMenu() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     ПРОГРАММА ФИЛЬТРАЦИИ ДАННЫХ      ║");
        System.out.println("╚══════════════════════════════════════╝");

        // Шаг 1: Входной файл
        while (inputFilePath == null || inputFilePath.trim().isEmpty()) {
            System.out.print("\nВведите путь к входному файлу: ");
            inputFilePath = scanner.nextLine().trim();

            if (!Files.exists(Paths.get(inputFilePath))) {
                System.out.println("⚠ Файл не найден! Попробуйте еще раз.");
                inputFilePath = null;
            }
        }

        // Шаг 2: Основное меню опций
        boolean exitMenu = false;
        while (!exitMenu) {
            System.out.println("\n═════════════ МЕНЮ ОПЦИЙ ═════════════");
            System.out.println("Текущие настройки:");
            System.out.println("  • Входной файл: " + inputFilePath);
            System.out.println("  • Путь сохранения: " + fileService.getPathManager().getOutputPath());
            System.out.println("  • Префикс файлов: " + (fileService.getNameGenerator() instanceof FilePrefixManager ?
                    ((FilePrefixManager) fileService.getNameGenerator()).getFilePrefix() : "нет"));


            System.out.println("  • Режим добавления: " + (appendMode ? "ВКЛ" : "ВЫКЛ"));
            System.out.println("  • Статистика: " +
                    (showFullStats ? "ПОЛНАЯ" : showShortStats ? "КРАТКАЯ" : "НЕТ"));

            System.out.println("\nДоступные опции:");
            System.out.println("  1. -o  Указать путь для сохранения результатов");
            System.out.println("  2. -p  Задать префикс имен выходных файлов");
            System.out.println("  3. -a  Включить режим добавления (по умолчанию: перезапись)");
            System.out.println("  4. -s  Показать краткую статистику");
            System.out.println("  5. -f  Показать полную статистику");
            System.out.println("  6.     Запустить обработку файла");
            System.out.println("  7.     Выйти из программы");
            System.out.print("\nВыберите опцию (введите цифру или -опцию): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                case "-o":
                    System.out.print("Введите путь для сохранения результатов: ");
                    String path = scanner.nextLine().trim();
                    try {
                        fileService.getPathManager().setOutputPath(path);
                        System.out.println("✓ Путь сохранения установлен: " + path);
                    } catch (Exception e) {
                        System.out.println("✗ Ошибка: " + e.getMessage());
                    }
                    break;

                case "2":
                case "-p":
                    System.out.print("Введите префикс для имен файлов: ");
                    String prefix = scanner.nextLine().trim();
                    try {
                        fileService.getNameGenerator().setPrefix(prefix);
                        System.out.println("✓ Префикс установлен: " + prefix);
                    } catch (Exception e) {
                        System.out.println("✗ Ошибка: " + e.getMessage());
                    }
                    break;

                case "3":
                case "-a":
                    appendMode = !appendMode;
                    System.out.println("✓ Режим добавления: " + (appendMode ? "ВКЛЮЧЕН" : "ВЫКЛЮЧЕН"));
                    break;

                case "4":
                case "-s":
                    showShortStats = true;
                    showFullStats = false;
                    System.out.println("✓ Бует показана КРАТКАЯ статистика");
                    break;

                case "5":
                case "-f":
                    showFullStats = true;
                    showShortStats = false;
                    System.out.println("✓ Бует показана ПОЛНАЯ статистика");
                    break;

                case "6":
                    processFile(inputFilePath);
                    exitMenu = true;
                    break;

                case "7":
                    System.out.println("Выход из программы...");
                    scanner.close();
                    return;

                default:
                    System.out.println("⚠ Неизвестная опция. Попробуйте еще раз.");
                    break;
            }
        }

        scanner.close();
    }

    // Обработка входного файла
    private void processFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                System.out.println("✗ Файл не найден: " + filePath);
                return;
            }

            System.out.println("\n══════════ ОБРАБОТКА ФАЙЛА ══════════");


            String content = Files.readString(path);
            System.out.println("Файл прочитан: " + filePath);
            System.out.println("Размер файла: " + content.length() + " символов");

            processContent(content);

        } catch (NoSuchFileException e) {
            System.out.println("✗ Файл не найден: " + e.getFile());
        } catch (IOException e) {
            System.out.println("✗ Ошибка чтения файла: " + e.getMessage());
            logger.log(Level.SEVERE, "Ошибка чтения файла", e);
        } catch (Exception e) {
            System.out.println("✗ Неизвестная ошибка: " + e.getMessage());
            logger.log(Level.SEVERE, "Неизвестная ошибка", e);
        }
    }

    // Обработка содержимого
    private void processContent(String content) {
        List<Long> integers = new ArrayList<>();
        List<Double> floats = new ArrayList<>();
        List<String> strings = new ArrayList<>();

        NumberStatistics<Long> intStats = new NumberStatistics<>();
        NumberStatistics<Double> floatStats = new NumberStatistics<>();
        StringStatistics stringStats = new StringStatistics();

        String[] tokens = content.split("[\\s,;:!?\\[\\](){}\"']+");

        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty()) continue;

            try {
                if (token.matches("-?\\d+")) {
                    Long value = Long.parseLong(token);
                    integers.add(value);
                    intStats.add(value);
                } else if (token.matches("-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?")) {
                    Double value = Double.parseDouble(token);
                    floats.add(value);
                    floatStats.add(value);
                } else {
                    strings.add(token);
                    stringStats.add(token);
                }
            } catch (NumberFormatException e) {
                strings.add(token);
                stringStats.add(token);
            }
        }

        // Сортировка
        Collections.sort(integers);
        Collections.sort(floats);

        // Сохранение в файлы (только если есть данные)
        boolean anyFileCreated = false;
        try {
            if (!integers.isEmpty()) {
                Path intPath = fileService.getFullFilePath("integers", ".txt");
                fileService.saveToFile("sample-integers", ".txt", integers, appendMode);
                System.out.println("✓ Целые числа сохранены в: " + intPath);
                anyFileCreated = true;
            }
            if (!floats.isEmpty()) {
                Path floatPath = fileService.getFullFilePath("floats", ".txt");
                fileService.saveToFile("sample-floats", ".txt", floats, appendMode);
                System.out.println("✓ Вещественные числа сохранены в: " + floatPath);
                anyFileCreated = true;
            }
            if (!strings.isEmpty()) {
                Path stringPath = fileService.getFullFilePath("strings", ".txt");
                fileService.saveToFile("sample-strings", ".txt", strings, appendMode);
                System.out.println("✓ Строки сохранены в: " + stringPath);
                anyFileCreated = true;
            }

            if (!anyFileCreated) {
                System.out.println("⚠ В файле не найдено данных для обработки");
            }

        } catch (IOException e) {
            System.out.println("✗ Ошибка записи в файл: " + e.getMessage());
            logger.log(Level.SEVERE, "Ошибка записи в файл", e);
        }

        // Вывод статистики
        printStatistics(intStats, floatStats, stringStats);
    }

    // Вывод статистики
    private void printStatistics(NumberStatistics<Long> intStats,
                                 NumberStatistics<Double> floatStats,
                                 StringStatistics stringStats) {

        System.out.println("\n════════════ СТАТИСТИКА ════════════");

        if (showFullStats) {
            System.out.println("ПОЛНАЯ СТАТИСТИКА:");
            System.out.println("• Целые числа: " + intStats.getFullStats());
            System.out.println("• Вещественные числа: " + floatStats.getFullStats());
            System.out.println("• Строки: " + stringStats.getFullStats());
        } else if (showShortStats) {
            System.out.println("КРАТКАЯ СТАТИСТИКА:");
            System.out.println("• Целые числа: " + intStats.getShortStats());
            System.out.println("• Вещественные числа: " + floatStats.getShortStats());
            System.out.println("• Строки: " + stringStats.getShortStats());
        } else {
            System.out.println("ОБРАБОТАННЫЕ ДАННЫЕ:");
            System.out.println("• Целые числа: " + intStats.getCount() + " элементов");
            System.out.println("• Вещественные числа: " + floatStats.getCount() + " элементов");
            System.out.println("• Строки: " + stringStats.getCount() + " элементов");
        }

        System.out.println("══════════════════════════════════════\n");
    }
}