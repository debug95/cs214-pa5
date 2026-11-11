import java.util.Map;
import java.util.TreeMap;

public class UserDatabase {
    private Map<String, User> users = new TreeMap<>();

    public void add (String username, String song, double rating){
        users.putIfAbsent(username, new User(username));
        users.get(username).addRating(song, rating);
    }
    public Map<String, User> getUsers(){
        return users;
    }
}
