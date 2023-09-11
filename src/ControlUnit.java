import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

import FTPUtil.*;
import JSONUtil.JSONStatusCodes;
import JSONUtil.StudentsParser;
import UIUtil.ConsoleUI;
import UIUtil.MainMenuKeyCodes;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

//to do
//replace string hints to constants
//add check to integer overflow when adds student
//ad id scanner method
//decompose control unit
public class ControlUnit {
    private static final int TWO_SECONDS = 2000;
    private static final int FIVE_SECONDS = 5000;
    private static final int SUCCESS = 0;
    private static final int ERROR = -1;
    private static final String TEMP_FILE_PATH = "temp.txt";
    private static HashMap<Integer,String> students;
    private static int connect(){
        ConsoleUI.flush();
        ConsoleUI.displayTitle();
        String ip,user,password;
        Scanner scanner = new Scanner(System.in);
        ip = ConsoleUI.getData(ConsoleUI.IP_HINT,scanner);
        user = ConsoleUI.getData(ConsoleUI.USER_NAME_HINT,scanner);
        password = ConsoleUI.getData(ConsoleUI.PASSWORD_HINT,scanner);
        switch (FTPConnector.connect(ip,user,password)) {
            case FTPStatusCodes.CANNOT_CONNECT_TO_FTP_SERVER:
                System.out.println(ConsoleUI.CONNECTION_ERROR);
                break;
            case FTPStatusCodes.AUTHORIZATION_ERROR:
                System.out.println(ConsoleUI.AUTHORIZATION_ERROR);
                break;
            case FTPStatusCodes.SUCCESS_AUTHORIZATION:
                System.out.println(ConsoleUI.SUCCESS_AUTHORIZATION);
                return SUCCESS;
        }
        return ERROR;
    }
    private static int loadFile(OutputStream loadingStream){
        switch (FTPConnector.loadFile(loadingStream)) {
            case FTPStatusCodes.JSON_FILES_NOT_FOUND: {
                System.out.println(ConsoleUI.NO_JSON_FILES_IN_SUCH_DIRECTORY_ERROR);
                Scanner scanner = new Scanner(System.in);
                String path = ConsoleUI.getData(ConsoleUI.PATH_HINT, scanner);
                String fileName = ConsoleUI.getData(ConsoleUI.FILE_NAME_HINT, scanner);
                int status = FTPConnector.loadFile(path, fileName, loadingStream);
                if (status == FTPStatusCodes.SUCCESS_LOADING) {
                    System.out.println(ConsoleUI.SUCCESS_LOADING_FILE);
                    return SUCCESS;
                } else System.out.println(ConsoleUI.LOADING_FILE_ERROR);
                break;
            }
            case FTPStatusCodes.CANNOT_DEFINE_REQUIRED_FILE: {
                System.out.println(ConsoleUI.MANY_JSON_FILES_IN_SUCH_DIRECTORY_ERROR);
                Scanner scanner = new Scanner(System.in);
                String fileName = ConsoleUI.getData(ConsoleUI.FILE_NAME_HINT, scanner);
                int status = FTPConnector.loadFile(fileName, loadingStream);
                if (status == FTPStatusCodes.SUCCESS_LOADING) {
                    System.out.println(ConsoleUI.SUCCESS_LOADING_FILE);
                    return SUCCESS;
                } else System.out.println(ConsoleUI.LOADING_FILE_ERROR);
                break;
            }
            case FTPStatusCodes.SUCCESS_LOADING: {
                System.out.println(ConsoleUI.SUCCESS_LOADING_FILE);
                return SUCCESS;
            }
        }
        return ERROR;
    }
    private static int loadFileWithParams(OutputStream loadingStream) {
        Scanner scanner = new Scanner(System.in);
        String path = ConsoleUI.getData(ConsoleUI.PATH_HINT, scanner);
        String fileName = ConsoleUI.getData(ConsoleUI.FILE_NAME_HINT, scanner);
        int status = FTPConnector.loadFile(path, fileName, loadingStream);
        if (status == FTPStatusCodes.SUCCESS_LOADING) {
            System.out.println(ConsoleUI.SUCCESS_LOADING_FILE);
            return SUCCESS;
        } else System.out.println(ConsoleUI.LOADING_FILE_ERROR);
        return ERROR;
    }
    private static int parseFile(InputStream loadedFileStream){
        int status = StudentsParser.parseToHashMap(loadedFileStream);
        if(status == JSONStatusCodes.SUCCESS_PARSING){
            System.out.println(ConsoleUI.SUCCESS_PARSING);
            students = StudentsParser.getStudentsMap();
            return SUCCESS;
        }
        else{
            System.out.println(ConsoleUI.PARSING_ERROR);
            return ERROR;
        }
    }
    private static int disconnect(){
        if(FTPConnector.disconnect() == FTPStatusCodes.SUCCESS_DISCONNECTION) {
            System.out.println(ConsoleUI.SUCCESS_DISCONNECTION);
            return SUCCESS;
        }
        else{
            System.out.println(ConsoleUI.DISCONNECTION_ERROR);
            return ERROR;
        }
    }
    public static int loadFile(){
        try {
            OutputStream loadingStream = Files.newOutputStream(Paths.get(TEMP_FILE_PATH));
            if ((connect() == SUCCESS) && (loadFile(loadingStream) == SUCCESS)){
                loadingStream.close();
                return SUCCESS;
            }
            return ERROR;
        }
        catch (IOException e){
            System.out.println(ConsoleUI.UNKNOWN_ERROR);
            e.printStackTrace();
            return ERROR;
        }
    }
    public static int parseFile(){
        try {
            InputStream loadedFileStream = Files.newInputStream(Paths.get(TEMP_FILE_PATH));
            if (parseFile(loadedFileStream) == SUCCESS) {
                loadedFileStream.close();
                return SUCCESS;
            }
            else{
                do {
                    System.out.println(ConsoleUI.PARSING_ERROR_HINT);
                    loadedFileStream.close();
                    OutputStream loadingStream = Files.newOutputStream(Paths.get(TEMP_FILE_PATH));
                    if (loadFileWithParams(loadingStream) == SUCCESS) {
                        loadingStream.close();
                        loadedFileStream = Files.newInputStream(Paths.get(TEMP_FILE_PATH));
                    }
                } while (parseFile(loadedFileStream)!=SUCCESS);
            }
            return ERROR;
        }
        catch (IOException e){
            System.out.println(ConsoleUI.UNKNOWN_ERROR);
            e.printStackTrace();
            return ERROR;
        }
    }
    public static void menuControl(){
        while(true){
            ConsoleUI.flush();
            ConsoleUI.displayTitle();
            ConsoleUI.displayMenu();
            String currentFTPMode = FTPConnector.isEnablePassiveMode()?
                    ConsoleUI.PASSIVE_MOD_HINT:ConsoleUI.ACTIVE_MOD_HINT;
            System.out.println(ConsoleUI.FTP_MODE_HINT+" "+currentFTPMode);
            Scanner scanner = new Scanner(System.in);
            int inputKey = 0;
            try {
                inputKey = Integer.parseInt(ConsoleUI.getData(ConsoleUI.MENU_INPUT_KEY_HINT, scanner));
            }
            catch (NumberFormatException e){
                System.out.println(ConsoleUI.MENU_INPUT_ERROR);
                sleep(TWO_SECONDS);
                continue;
            }
            switch (inputKey){
                case MainMenuKeyCodes.GET_STUDENTS_BY_NAME: {
                    String name = ConsoleUI.getData("Введите имя студента: ", scanner);
                    DAO.getStudentsByName(students, name).entrySet()
                            .stream().forEach(el -> System.out.println("Id: " + el.getKey() + " Name: " + el.getValue()));
                    System.out.println("Конец списка");
                    ConsoleUI.getData("Введите любой символ + enter для продолжения ", scanner);
                    break;
                }
                case MainMenuKeyCodes.GET_STUDENT_BY_ID: {
                    int id;
                    try {
                        id = Integer.parseInt(ConsoleUI.getData("Введите id студента: ", scanner));
                    }catch (NumberFormatException e){
                        System.out.println("Id должен быть целым числом");
                        sleep(TWO_SECONDS);
                        continue;
                    }
                    String name = DAO.getStudentById(students, id);
                    if(name != null)
                        System.out.println("Имя студента с заданным id " + name);
                    else System.out.println("Студента с введённым id не существует");
                    ConsoleUI.getData("Введите любой символ + enter для продолжения ", scanner);
                    break;
                }
                case MainMenuKeyCodes.ADD_STUDENT: {
                    String name = ConsoleUI.getData("Введите имя студента: ", scanner);
                    int id = DAO.addStudent(students,name);
                    System.out.println("Добавлен студент "+name+" с id "+id);
                    ConsoleUI.getData("Введите любой символ + enter для продолжения ", scanner);
                    break;
                }
                case MainMenuKeyCodes.DELETE_STUDENT_BY_ID: {
                    int id;
                    try {
                        id = Integer.parseInt(ConsoleUI.getData("Введите id студента: ", scanner));
                    }catch (NumberFormatException e){
                        System.out.println("Id должен быть целым числом");
                        sleep(TWO_SECONDS);
                        continue;
                    }
                    String name = DAO.deleteStudentById(students,id);
                    System.out.println("Удалён студент "+name+" с id "+id);
                    ConsoleUI.getData("Введите любой символ + enter для продолжения ", scanner);
                    break;
                }
                case MainMenuKeyCodes.EXIT: {
                    String answer = ConsoleUI.getData("Сохранить локальные изменения на сервере y/n: ", scanner);
                    if(answer.matches("[yY]")){
                        System.out.println("Сохраняем файл");
                        String path = ConsoleUI.getData("Введите директорию сохранения на сервере: ", scanner);
                        String name = ConsoleUI.getData("Введите имя файла с расширением под которым сохранится файл: ", scanner);
                        try {
                            FileWriter saver = new FileWriter(new File(TEMP_FILE_PATH));
                            saver.flush();
                            saver.write("{\n  \"students\": [\n");
                            String part1 = "    {\n        \"id\": ";
                            String part2 = "\n        \"name\": \"";
                            String part3 = "\"\n    },\n";
                            int status;
                            students.entrySet().stream()
                                    .forEach(el -> {
                                        try {
                                            saver.write(part1 + el.getKey() + part2 + el.getValue() + part3);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                            saver.close();
                            status = FTPConnector.pushFile(path, name, Files.newInputStream(Paths.get(TEMP_FILE_PATH)));
                            if(status == FTPStatusCodes.SUCCESS_LOADING)
                                System.out.println("Файл успешно выгружен на сервер");
                            else {
                                System.out.println("Ошибка выгрузки на сервер");
                                return;
                            }
                            ConsoleUI.getData("Введите любой символ + enter для продолжения ", scanner);
                        }
                        catch (IOException e){
                            e.printStackTrace();
                            System.out.println(ConsoleUI.UNKNOWN_ERROR);
                            sleep(TWO_SECONDS);
                            return;
                        }
                    }
                    return;
                }
                case MainMenuKeyCodes.CHANGE_FTP_MOD: {
                    if(FTPConnector.changeConnectionMode())
                        System.out.println("Режим работы изменён");
                    else System.out.println("Невозможно изменить режим работы");
                    sleep(TWO_SECONDS);
                    break;
                }
                case MainMenuKeyCodes.CHANGE_FTP_SERVER: {
                    while (ControlUnit.loadFile() != ControlUnit.getSuccessCode()) {
                        System.out.println(ConsoleUI.CONNECTION_ERROR_HINT);
                        ControlUnit.sleep(FIVE_SECONDS);
                    }
                    ControlUnit.parseFile();
                    ControlUnit.sleep(FIVE_SECONDS);
                    break;
                }
                default: System.out.println(ConsoleUI.MENU_INPUT_ERROR);
                sleep(TWO_SECONDS);break;
            }
        }
    }
    public static void sleep(int milliseconds){
        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e){
            System.out.println(ConsoleUI.UNKNOWN_ERROR);
            System.exit(ERROR);
        }
    }
    public static int getSuccessCode() {
        return SUCCESS;
    }
    public static int getErrorCode() {
        return ERROR;
    }
}
