import java.util.Set;

public interface SimilarityComparable {

    boolean hasRating(String dimension);

    double getNormalizedRating(String dimension);

    Set<String> getDimensions();
}