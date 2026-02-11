import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        DataFilterUtility utility = new DataFilterUtility();

        if (args.length == 0) {
            // Интерактивный режим с меню
            utility.showMenu();
        } else {
            // Режим командной строки
            Scanner scanner = new Scanner(System.in);

            System.out.println("Использование: java DataFilterUtility");
            System.out.println("Опции будут доступны в интерактивном меню.");
            System.out.println("\nЕсли хотите использовать аргументы командной строки,");
            System.out.println("запустите с одним из следующих ключей:");
            System.out.println("  -o <путь>    путь для сохранения");
            System.out.println("  -p <префикс> префикс файлов");
            System.out.println("  -a           режим добавления");
            System.out.println("  -s           краткая статистика");
            System.out.println("  -f           полная статистика");

            System.out.print("\nЗапустить интерактивный режим? (да/нет): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("да")||response.equals("yes")||response.equals("y")) {
                utility.showMenu();
            } else {
                System.out.println("Выход из программы.");
            }

            scanner.close();
        }
    }
}