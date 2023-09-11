import UIUtil.ConsoleUI;

public class Main {
    private static final int FIVE_SECONDS = 5000;
    private static final int ERROR_STATUS = -1;
    public static void main(String[] args){
        while (ControlUnit.loadFile() != ControlUnit.getSuccessCode()) {
            System.out.println(ConsoleUI.CONNECTION_ERROR_HINT);
            ControlUnit.sleep(FIVE_SECONDS);
        }
        ControlUnit.parseFile();
        ControlUnit.sleep(FIVE_SECONDS);
        ControlUnit.menuControl();
    }
}