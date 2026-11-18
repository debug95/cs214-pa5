import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PA5Processor {
    public static void run(String inputFile, String outputFile, String[] selections) {
        try {
            Validator.validateInputFile(inputFile);
            Validator.validateOutputPath(outputFile);
            process(inputFile, outputFile, selections);
        } catch (FileNotFoundException e) {
            ErrorPrinter.printFileNotFound(inputFile);
        } catch (IllegalArgumentException e) {
            ErrorPrinter.print(e.getMessage());
        } catch (IOException e) {
            ErrorPrinter.print(e.getMessage());
        }
    }

    private static void process(String inputFile, String outputFile, String[] selections) throws IOException {

        UserDatabase database = new UserDatabase();

        PA3Processor.loadCsvIntoDatabase(inputFile, database);

        Map<String, User> users = database.getUsers();

        PA3Processor.removeUncooperativeUsers(users);

        if (users.size() < 2) {
            throw new IllegalArgumentException("at least two cooperative users are required");
        }

        Set<String> allSongs = PA3Processor.collectAllSongs(users);

        validateSelections(selections, allSongs);
        validateNoDuplicates(selections);

        RatingStats userStats = buildUserStats(users);
        RatingStats songStats = buildSongStats(inputFile);

        PA3Processor.normalizeAllUsers(users);

        PA3Processor.fillMissingRatings(users, allSongs);

        fillPredictedRatings(users, allSongs);
    }

    private static void validateSelections(String[] selections, Set<String> allSongs) {

        for (String song : selections) {
            if (!allSongs.contains(song)) {
                throw new IllegalArgumentException("selected songs must exist");
            }
        }
    }

    private static void validateNoDuplicates(String[] selections) {

        Set<String> unique = new HashSet<>();

        for (String song : selections) {
            if (!unique.add(song)) {
                throw new IllegalArgumentException("duplicate song selections");
            }
        }
    }

    private static void fillPredictedRatings(Map<String, User> users, Set<String> allSongs) {

        for (User user : users.values()) {
            for (String song : allSongs) {

                if (!Double.isNaN(user.getRating(song))) {
                    continue;
                }

                List<User> ranked = PA4Processor.rankUsers(user, users);

                Double prediction = PredictionEngine.predictRating(user, song, ranked);

                if (!Double.isNaN(prediction)) {
                    user.setRating(song, prediction);
                }
            }
        }
    }

    private static RatingStats buildUserStats(Map<String, User> users) {

        Map<String, Double> means = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();

        for (User user : users.values()) {
            means.put(user.getUsername(), user.getMean());
            counts.put(user.getUsername(), user.getRatings().size());
        }

        return new RatingStats(means, counts);
    }

    private static RatingStats buildSongStats(String inputFile) throws IOException {

        SongDatabase database = new SongDatabase();

        CSVUtils.forEachRecord(inputFile, record -> {

            String song = record.get(0).trim();
            int rating = Integer.parseInt(record.get(2).trim());

            database.add(song, rating);
        });

        Map<String, Double> means = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();

        for (var entry : database.getSongs().entrySet()) {
            means.put(entry.getKey(), entry.getValue().getMean());
            counts.put(entry.getKey(), entry.getValue().getCount());
        }

        return new RatingStats(means, counts);
    }
}
