import java.util.HashSet;
import java.util.Set;

public final class SimilarityCalculator {

    private SimilarityCalculator() {
    }

    public static double computeSimilarity(SimilarityComparable first,
            SimilarityComparable second) {

        Set<String> dimensions = new HashSet<>();
        dimensions.addAll(first.getDimensions());
        dimensions.retainAll(second.getDimensions());

        double sum = 0.0;
        int count = 0;

        for (String dimension : dimensions) {
            if (!first.hasRating(dimension) || !second.hasRating(dimension)) {
                continue;
            }

            double value1 = first.getNormalizedRating(dimension);
            double value2 = second.getNormalizedRating(dimension);
            double diff = value1 - value2;

            sum += diff * diff;
            count++;
        }

        if (count == 0) {
            return Double.NaN;
        }

        return Math.sqrt(sum);
    }
}
