import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class SongPreparation {
    private SongPreparation() {
    }

    public static Map<String, Song> prepareSongs(Map<String, User> users, Set<String> allSongs, String[] selections) {
        Map<String, Song> songs = buildSongs(users, allSongs);

        removeFlatSongs(songs);

        if (songs.size() < selections.length + 1) {
            throw new IllegalArgumentException(
                    "no songs to recommend. Songs may have been removed. Try with a larger file or fewer selections.");
        }

        SongNormalizer.normalizeSongs(songs);
        return songs;
    }

    public static Map<String, Song> buildSongs(Map<String, User> users, Set<String> allSongs) {

        Map<String, Song> songs = new HashMap<>();

        for (String songName : allSongs) {
            Song song = new Song(songName);

            for (User user : users.values()) {
                song.setRating(user.getUsername(), user.getRating(songName));
            }

            songs.put(songName, song);
        }

        return songs;
    }

    public static void removeFlatSongs(Map<String, Song> songs) {

        Set<String> toRemove = new HashSet<>();

        for (Song song : songs.values()) {
            Set<Double> distinct = new HashSet<>(song.getRatings().values());

            if (distinct.size() <= 1) {
                toRemove.add(song.getName());
            }
        }

        for (String songName : toRemove) {
            songs.remove(songName);
        }
    }
}
