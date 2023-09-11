package UIUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class ConsoleUI {
    public static final String IP_HINT = "Введите IP адрес сервера";
    public static final String USER_NAME_HINT = "Введите имя пользователя";
    public static final String PASSWORD_HINT = "Введите пароль";
    public static final String PATH_HINT = "Введите путь к файлу";
    public static final String FILE_NAME_HINT = "Введите имя файла с расширением";
    public static final String NO_JSON_FILES_IN_SUCH_DIRECTORY_ERROR =
            "В текущей директории нет json файлов, выберете другой путь";
    public static final String MANY_JSON_FILES_IN_SUCH_DIRECTORY_ERROR =
            "Найдено более одного json файла в текущей директории (выберите один из списка ниже):";
    public static final String FTP_MODE_HINT = "Текущий режим работы с FTP сервером:";
    public static final String MENU_INPUT_KEY_HINT = "Введите номер пункта меню для выбора";
    public static final String PASSIVE_MOD_HINT = "пассивный";
    public static final String ACTIVE_MOD_HINT = "активный";
    public static final String CONNECTION_ERROR_HINT = "Ошибка подключения. Через несколько секунд повторим попытку";
    public static final String PARSING_ERROR_HINT = "Попробуем выбрать файл ещё раз";
    public static final String CONNECTION_ERROR = "Ошибка подключения к серверу";
    public static final String AUTHORIZATION_ERROR = "Ошибка авторизации";
    public static final String SUCCESS_AUTHORIZATION = "Успешная авторизация на сервере";
    public static final String LOADING_FILE_ERROR = "Ошибка загрузки файла";
    public static final String SUCCESS_LOADING_FILE = "Файл успешно загружен для локальной работы";
    public static final String SUCCESS_DISCONNECTION = "Соединение с сервером разорвано";
    public static final String DISCONNECTION_ERROR = "Ошибка разрыва соединения";
    public static final String SUCCESS_PARSING = "Список студентов успешно получен";
    public static final String PARSING_ERROR =
            "Ошибка получения списка студентов. Проверьте корректность структуры файла";
    public static final String MENU_INPUT_ERROR =
            "Неверный ввод номера пукта меню. Убедитесь, что вы вводите допустимое число";
    public static final String UNKNOWN_ERROR = "Неизвестная ошибка. Завершаем работу";

    private static final String TITLE_FILE_PATH = "/Title.txt";
    private static final String MENU_FILE_PATH = "/MainMenu.txt";
    private static int display(String path){
        try(InputStream fileInputString = ConsoleUI.class.getResourceAsStream(path)){
            Scanner scanner = new Scanner(fileInputString);
            while (scanner.hasNextLine())
                System.out.println(scanner.nextLine());
        }
        catch (Exception e){
            return UIStatusCodes.UI_OPERATION_ERROR;
        }
        return UIStatusCodes.SUCCESS_OPERATION;
    }
    public static int displayTitle(){
        return display(TITLE_FILE_PATH);
    }
    public static int displayMenu(){
        return display(MENU_FILE_PATH);
    }
    public static int flush(){
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        }
        catch (IOException | InterruptedException ex) {
            return UIStatusCodes.UI_OPERATION_ERROR;
        }
        return UIStatusCodes.SUCCESS_OPERATION;
    }
    public static String getData(String userHint, Scanner dataScanner){
        System.out.print(userHint+" ");
        return dataScanner.next();
    }
}
