package FTPUtil;

public class FTPStatusCodes {
    //Connection status codes
    public static final int SUCCESS_AUTHORIZATION = 0;
    public static final int CANNOT_CONNECT_TO_FTP_SERVER = -1;
    public static final int AUTHORIZATION_ERROR = -2;
    //Loading file status codes
    public static final int SUCCESS_LOADING = 0;
    public static final int LOADING_ERROR = -1;
    public static final int JSON_FILES_NOT_FOUND = -2;
    public static final int CANNOT_DEFINE_REQUIRED_FILE = -3;
    public static final int FILE_ALREADY_EXISTS = -4;
    //Disconnection status codes
    public static final int SUCCESS_DISCONNECTION = 0;
    public static final int DISCONNECTION_ERROR = -1;
}
