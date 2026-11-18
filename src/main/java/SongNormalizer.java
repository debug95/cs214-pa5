import java.util.Map;

public final class SongNormalizer {
    private SongNormalizer() {
    }

    public static void normalizeSongs(Map<String, Song> songs) {

        for (Song song : songs.values()) {
            normalizeASong(song);
        }
    }

    private static void normalizeASong(Song song) {

        double mean = calculateSongMean(song);

        double stdDev = calculateSongStdDev(song, mean);

        if (stdDev == 0.0) {
            setAllRatingsToZero(song);
            return;
        }

        applyNormalization(song, mean, stdDev);
    }

    public static double calculateSongMean(Song song) {
        double sum = 0.0;
        int count = 0;

        for (double rating : song.getRatings().values()) {
            sum += rating;
            count++;
        }

        double mean = sum / count;
        return mean;
    }

    private static double calculateSongStdDev(Song song, double mean) {
        double variance = 0.0;

        for (double rating : song.getRatings().values()) {
            double diff = rating - mean;
            variance += diff * diff;
        }

        return Math.sqrt(variance / song.getRatings().size());
    }

    private static void setAllRatingsToZero(Song song) {
        for (String user : song.getRatings().keySet()) {
            song.setRating(user, 0.0);
        }
    }

    private static void applyNormalization(Song song, double mean, double stdDev) {
        for (String user : song.getRatings().keySet()) {
            double rating = song.getRatings().get(user);
            double normalized = (rating - mean) / stdDev;

            song.setRating(user, normalized);
        }
    }
}
