import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public  class Song implements SimilarityComparable {

    private final String name;
    private final Map<String, Double> ratings;
    
    public Song(String name) {
        this.name = name;
        this. ratings = new TreeMap<>();
    }

    public String getName(){
        return name;
    }

    public void addRating(String user, double rating) {
        ratings.put(user, rating);
    }

    @Override 
    public boolean hasRating(String user) {
        return ratings.containsKey(user)
                && !Double.isNaN(ratings.get(user));
    }

    @Override
    public double getNormalizedRating(String user){
        return ratings.getOrDefault(user, Double.NaN);
    }

    @Override 
    public Set<String> getDimensions() {
        return ratings.keySet();
    }
}
