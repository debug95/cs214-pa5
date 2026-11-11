import java.util.Map;
import java.util.TreeMap;

public class SongDatabase {
    private Map<String, SongStats> songs = new TreeMap<>();

    public void add(String song, int rating){
        songs.putIfAbsent(song, new SongStats());
        songs.get(song).addRating(rating);
    }

    public Map<String, SongStats> getSongs(){
        return songs;
    }
}