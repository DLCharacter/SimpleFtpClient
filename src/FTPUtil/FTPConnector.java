package FTPUtil;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpProtocolException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Scanner;
//Class used to:
//1.Connecting to FTP server
//2.Loading json files from FTP server
//3.Closing opened connection
public class FTPConnector {
    private static final int FTP_CONNECTION_PORT = 21;
    private final static FtpClient ftpClient = FtpClient.create();
    public static int connect(String ip, String user, String password){
        try{
            ftpClient.connect(new InetSocketAddress(ip,FTP_CONNECTION_PORT));
        }
        catch (FtpProtocolException | IOException e){
            return FTPStatusCodes.CANNOT_CONNECT_TO_FTP_SERVER;
        }
        try {
            ftpClient.login(user, password.toCharArray());
        }
        catch (FtpProtocolException | IOException e){
            return FTPStatusCodes.AUTHORIZATION_ERROR;
        }
        return FTPStatusCodes.SUCCESS_AUTHORIZATION;
    }
    //Automatic loading json file
    //Works if there is alone json file in root directory
    public static int loadFile(OutputStream loadingStream){
        try{
            //getting root directory info
            Scanner scanner = new Scanner(ftpClient.list(""));
            int jsonFilesCount = 0;
            String fileName = "";
            while(scanner.hasNext()){
                String ftpInfoString = scanner.next();
                if(ftpInfoString.matches(".+\u002Ejson")){
                    jsonFilesCount++;
                    fileName = ftpInfoString;
                }
            }
            if(jsonFilesCount == 0)
                return FTPStatusCodes.JSON_FILES_NOT_FOUND;
            else if(jsonFilesCount > 1)
                return FTPStatusCodes.CANNOT_DEFINE_REQUIRED_FILE;
            else ftpClient.getFile(fileName,loadingStream);
        }
        catch (FtpProtocolException | IOException e){
            return FTPStatusCodes.LOADING_ERROR;
        }
        return FTPStatusCodes.SUCCESS_LOADING;
    }
    //Loading file by name
    //Works if there are many json files
    public static int loadFile(String fileName,OutputStream loadingStream){
        try{
            ftpClient.getFile(fileName,loadingStream);
        }
        catch (FtpProtocolException | IOException e){
            return FTPStatusCodes.LOADING_ERROR;
        }
        return FTPStatusCodes.SUCCESS_LOADING;
    }
    //loading file by directory and name
    //works if there are no json files in root directory
    public static int loadFile(String path,String fileName,OutputStream loadingStream){
        try{
            ftpClient.changeDirectory(path);
            ftpClient.getFile(fileName,loadingStream);
        }
        catch (FtpProtocolException | IOException e){
            return FTPStatusCodes.LOADING_ERROR;
        }
        return FTPStatusCodes.SUCCESS_LOADING;
    }
    public static int pushFile(String path, String fileName, InputStream loadingStream){
        try{
            ftpClient.changeDirectory(path);
            //If it cannot find file with input name
            //put file to ftp server
            try{
                ftpClient.getFile(fileName,new ByteArrayOutputStream());
                return FTPStatusCodes.LOADING_ERROR;
            }
            catch (FtpProtocolException | IOException e) {
                ftpClient.putFile(fileName, loadingStream);
                return FTPStatusCodes.SUCCESS_LOADING;
            }
        }
        catch (FtpProtocolException | IOException e){
            return FTPStatusCodes.LOADING_ERROR;
        }
    }
    public static boolean changeConnectionMode() {
        boolean currentMod = ftpClient.isPassiveModeEnabled();
        ftpClient.enablePassiveMode(!currentMod);
        return currentMod != ftpClient.isPassiveModeEnabled();
    }
    public static boolean isEnablePassiveMode(){
        return ftpClient.isPassiveModeEnabled();
    }
    public static int disconnect(){
        try {
            ftpClient.close();
        }
        catch (IOException e){
            return FTPStatusCodes.DISCONNECTION_ERROR;
        }
        return FTPStatusCodes.SUCCESS_DISCONNECTION;
    }
}