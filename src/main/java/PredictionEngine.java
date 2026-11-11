import java.util.List;

public final class PredictionEngine {

    private PredictionEngine() {
    }

    public static double denormalize(double normalizedRating, User targetUser) {
        return normalizedRating * targetUser.getStdDev() + targetUser.getMean();
    }

    public static int clampRating(double rating) {

        long rounded = Math.round(rating);

        if (rounded < 1) {
            return 1;
        }

        if (rounded > 5) {
            return 5;
        }

        return (int) rounded;
    }

    public static Double predictRating(User targetUser, String song, List<User> rankedUsers) {

        for (User other : rankedUsers) {

            if (other.hasRating(song)) {
                double normalized = other.getNormalizedRating(song);
                double predicted = denormalize(normalized, targetUser);

                return (double) clampRating(predicted);
            }
        }
        return Double.NaN;
    }
}
