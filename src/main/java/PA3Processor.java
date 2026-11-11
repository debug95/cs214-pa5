import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.io.Writer;

public class PA3Processor {

    public static void run(String inputFile, String outputFile) {
        try {
            Validator.validateInputFile(inputFile);
            Validator.validateOutputPath(outputFile);
            process(inputFile, outputFile);
        } catch (FileNotFoundException e) {
            ErrorPrinter.printFileNotFound(inputFile);
        } catch (IllegalArgumentException e) {
            ErrorPrinter.print(e.getMessage());
        } catch (IOException e) {
            ErrorPrinter.print(e.getMessage());
        }
    }

    private static void process(String inputFile, String outputFile) throws IOException {
        UserDatabase database = new UserDatabase();

        loadCsvIntoDatabase(inputFile, database);

        Map<String, User> users = database.getUsers();

        removeUncooperativeUsers(users);

        if (users.size() < 2) {
            throw new IllegalArgumentException("at least two cooperative users are required for song similarity");
        }

        Set<String> allSongs = collectAllSongs(users);

        normalizeAllUsers(users);

        fillMissingRatings(users, allSongs);

        writeSimilarityCsv(outputFile, allSongs, users);
    }

    static void loadCsvIntoDatabase(String inputFile, UserDatabase database) throws IOException {
        CSVUtils.forEachRecord(inputFile, record -> {
            if (record.size() != 3) {
                throw new IllegalArgumentException("malformed CSV entry");
            }

            String song = record.get(0).trim();
            String user = record.get(1).trim();
            String ratingStr = record.get(2).trim();

            int rating;
            try {
                rating = Integer.parseInt(ratingStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("rating is not an integer");
            }

            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("rating is not between 1 and 5");
            }

            database.add(user, song, (double) rating);
        });
    }

    static void removeUncooperativeUsers(Map<String, User> users) {
        Set<String> toRemove = new HashSet<>();
        for (var entry : users.entrySet()) {
            Set<Double> distinct = new HashSet<>(entry.getValue().getRatings().values());
            if (distinct.size() <= 1) {
                toRemove.add(entry.getKey());
            }
        }
        for (String u : toRemove) {
            users.remove(u);
        }
    }

    static Set<String> collectAllSongs(Map<String, User> users) {
        Set<String> songs = new TreeSet<>();
        for (var entry : users.entrySet()) {
            songs.addAll(entry.getValue().getRatings().keySet());
        }
        return songs;
    }

    static void normalizeAllUsers(Map<String, User> users) {
        for (var entry : users.entrySet()) {
            User ratings = entry.getValue();

            UserStats stats = new UserStats();
            for (double rating : ratings.getRatings().values()) {
                stats.addRating(rating);
            }

            double mean = stats.getMean();
            double std = stats.getStdDev();

            for (String song : ratings.getRatings().keySet()) {
                double rating = ratings.getRatings().get(song);
                ratings.getRatings().put(song, (rating - mean) / std);
            }
        }
    }

    static void fillMissingRatings(Map<String, User> users, Set<String> allSongs) {
        for (var entry : users.entrySet()) {
            User ratings = entry.getValue();
            for (String song : allSongs) {
                ratings.getRatings().putIfAbsent(song, Double.NaN);
            }
        }
    }

    static void writeSimilarityCsv(String outputFile, Set<String> allSongs,
            Map<String, User> users) throws IOException {
        try (Writer writer = new java.io.FileWriter(outputFile)) {

            writer.write("name1,name2,similarity");

            String[] songs = allSongs.toArray(new String[0]);

            for (int i = 0; i < songs.length; i++) {
                for (int j = i + 1; j < songs.length; j++) {

                    String song1 = songs[i];
                    String song2 = songs[j];

                    double similarity = computeSimilarity(song1, song2, users);

                    writer.write("\n");
                    writer.write(song1 + "," + song2 + "," +
                            (Double.isNaN(similarity) ? "NaN" : Double.toString(similarity)));
                }
            }
        }
    }

    static double computeSimilarity(String song1, String song2,
            Map<String, User> users) {
        double sum = 0.0;
        int count = 0;

        for (var entry : users.entrySet()) {
            User ratings = entry.getValue();
            double rating1 = ratings.getRatings().get(song1);
            double rating2 = ratings.getRatings().get(song2);

            if (Double.isNaN(rating1) || Double.isNaN(rating2)) {
                continue;
            }

            double diff = rating1 - rating2;
            sum += diff * diff;
            count++;
        }

        if (count == 0) {
            return Double.NaN;
        }
        return Math.sqrt(sum);
    }
}