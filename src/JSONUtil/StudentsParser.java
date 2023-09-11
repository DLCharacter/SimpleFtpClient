package JSONUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

//JSON Parser class
//Checking file for compliance with students array structure
//Fills HashMap with students data
public class StudentsParser {
    private static HashMap<Integer,String> students;
    private static final String STUDENTS_ARRAY_PATTERN = "\"students\"[\u0020\u0085\u0009]*:.*";
    private static final String STUDENT_ID_PATTERN = ".*\"id\"[\u0020\u0085\u0009]*:.*";
    private static final String STUDENT_NAME_PATTERN = ".*\"name\"[\u0020\u0085\u0009]*:.*";
    private static final String NOT_DIGIT_PATTERN = "[^0123456789]";
    private static final String JSON_STRING_WITH_DATA_PATTERN = ".*[^\\[\\]{},\u0020\u0085\u0009].*";
    public static int parseToHashMap(InputStream inputStream) {
        students = new HashMap<>();
        Scanner scanner = new Scanner(inputStream);
        //Checking file structure
        //Should find students array or return error code
        while (!scanner.hasNext(STUDENTS_ARRAY_PATTERN)) {
            if (!scanner.hasNext()|| scanner.hasNext(JSON_STRING_WITH_DATA_PATTERN))
                return JSONStatusCodes.FILE_STRUCTURE_ERROR;
            scanner.nextLine();
        }
        //students array line
        scanner.nextLine();
        //When find students array, checking students data
        while (scanner.hasNext()) {
            Integer id;
            String temp = scanner.nextLine();
            if(!temp.matches(STUDENT_ID_PATTERN)){
                //If there are no students data in array return error code
                if(temp.matches(JSON_STRING_WITH_DATA_PATTERN))
                    return JSONStatusCodes.FILE_STRUCTURE_ERROR;
                continue;
            }
            //When find id try to find name field
            else if (temp.replaceAll(NOT_DIGIT_PATTERN,"").length()!=0)
                id = Integer.parseInt(temp.replaceAll(NOT_DIGIT_PATTERN,""));
            else return JSONStatusCodes.FILE_STRUCTURE_ERROR;
            while(!scanner.hasNext(STUDENT_NAME_PATTERN)){
                if (!scanner.hasNext())
                    return JSONStatusCodes.FILE_STRUCTURE_ERROR;
                scanner.nextLine();
            }
            String name = scanner.nextLine().replaceAll("\"","");
            name = name.split(":")[1].trim();
            //If there are many students with same id in file
            //return error
            if(students.containsKey(id))
                return JSONStatusCodes.FILE_STRUCTURE_ERROR;
            students.put(id,name);
        }
        return JSONStatusCodes.SUCCESS_PARSING;
    }
    public static HashMap<Integer, String> getStudentsMap() {
        return students;
    }
}
