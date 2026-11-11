import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class PA2Processor {

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
        UserDatabase database = loadUsers(inputFile);
        Map<String, User> users = removeUncooperative(database.getUsers());
        Set<String> allSongs = collectSongs(users);
        writeOutput(outputFile, users, allSongs);
    }

    private static UserDatabase loadUsers(String inputFile) throws IOException {
        UserDatabase database = new UserDatabase();

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

        return database;
    }

    private static Map<String, User> removeUncooperative(Map<String, User> users) {
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
        return users;
    }

    private static Set<String> collectSongs(Map<String, User> users) {
        Set<String> allSongs = new TreeSet<>();
        for (var entry : users.entrySet()) {
            allSongs.addAll(entry.getValue().getRatings().keySet());
        }
        return allSongs;
    }

    private static void writeOutput(String outputFile, Map<String, User> users,
            Set<String> allSongs) throws IOException {
        CSVFormat outputFormat = CSVFormat.DEFAULT.builder()
                .setHeader("username", "song", "rating")
                .build();

        try (Writer writer = new java.io.FileWriter(outputFile);
                CSVPrinter printer = new CSVPrinter(writer, outputFormat)) {

            for (var entry : users.entrySet()) {
                String user = entry.getKey();
                Map<String, Double> ratings = entry.getValue().getRatings();

                for (String song : allSongs) {
                    Double rating = ratings.getOrDefault(song, Double.NaN);
                    printer.printRecord(user, song, formatRating(rating));
                }

            }
        }
    }

    private static String formatRating(Double rating) {
        if (rating == null || rating.isNaN()) {
            return "NaN";
        }
        double v = rating.doubleValue();
        if (v == Math.floor(v)) {
            return Integer.toString((int) v);
        }
        return Double.toString(v);
    }
}
