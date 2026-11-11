import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class User implements SimilarityComparable {

    private final String username;
    private final Map<String, Double> ratings;
    private final UserStats stats;

    public User(String username) {
        this.username = username;
        this.ratings = new TreeMap<>();
        this.stats = new UserStats();
    }

    public String getUsername() {
        return username;
    }

    public void addRating(String song, double rating) {
        ratings.put(song, rating);
        stats.addRating(rating);
    }

    public void addMissingRating(String song){
        ratings.putIfAbsent(song, Double.NaN);
    }

    public Map<String, Double> getRatings() {
        return ratings;
    }

    public Double getRating(String song){
        return ratings.get(song);
    }

    public boolean hasSong(String song){
        return ratings.containsKey(song);
    }

    public void setRating(String song, double rating) {
        ratings.put(song, rating);
    }

    public double getMean() {
        return stats.getMean();
    }

    public double getStdDev() {
        return stats.getStdDev();
    }

    public void normalizeRatings() {
        double mean = getMean();
        double std = getStdDev();

        if (std == 0.0) {
            for (String song : ratings.keySet()) {
                ratings.put(song, 0.0);
            }
            return;
        }

        for (String song : ratings.keySet()) {
            double rating = ratings.get(song);
            ratings.put(song, (rating - mean) / std);
        }
    }

    @Override
    public boolean hasRating(String song) {
        return ratings.containsKey(song)
                && !Double.isNaN(ratings.get(song));
    }

    @Override
    public double getNormalizedRating(String song) {
        return ratings.getOrDefault(song, Double.NaN);
    }

    @Override
    public Set<String> getDimensions() {
        return ratings.keySet();
    }

    @Override 
    public String toString() {
        return username;
    }
}
