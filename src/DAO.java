import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DAO {
    public static HashMap<Integer,String> getStudentsByName(HashMap<Integer,String> students,String name){
        return (HashMap<Integer, String>) students.entrySet().stream().filter(el -> el.getValue().equals(name))
                .sorted((o1,o2) -> o1.getValue().compareTo(o2.getValue()))
                .collect(Collectors.toMap(el -> el.getKey(),el -> el.getValue()));
    }
    public static String getStudentById(HashMap<Integer,String> students,int id){
        return students.get(id);
    }
    public static int addStudent(HashMap<Integer,String> students,String name){
        int id = students.keySet().stream().max((o1, o2) -> o1.compareTo(o2)).get()+1;
        students.put(id,name);
        return id;
    }
    public static String deleteStudentById(HashMap<Integer,String> students,int id){
        String name = students.get(id);
        students.remove(id);
        return name;
    }
}
