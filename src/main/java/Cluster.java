import java.util.ArrayList;
import java.util.List;

public class Cluster {
    private double[] centroid;
    private final List<Song> songs;
    
    public Cluster(double[] centroid){
        this.centroid = centroid;
        this.songs = new ArrayList<>();
    }

    public double[] getCentroid() {
        return centroid;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void clearSongs() {
        songs.clear();
    }
    
    public void addSong(Song song) {
        songs.add(song);
    }

    public void recomputeCentroid(List<String> users) {
        if (songs.isEmpty()) {
            return;
        }

        double[] newCentroid = new double[users.size()];

        for (Song song : songs) {
            for(int i = 0; i < users.size(); i++) {
                newCentroid[i] += song.getNormalizedRating(users.get(i));
            }
        }

        centroid = newCentroid;
        
    }
}
