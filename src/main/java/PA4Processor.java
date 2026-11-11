import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class PA4Processor {

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

        PA3Processor.loadCsvIntoDatabase(inputFile, database);

        Map<String, User> users = database.getUsers();

        PA3Processor.removeUncooperativeUsers(users);

        if (users.size() < 2) {
            throw new IllegalArgumentException("at least two cooperative users are required");
        }

        Set<String> allSongs = PA3Processor.collectAllSongs(users);

        validatePredictionsExist(users, allSongs);

        PA3Processor.normalizeAllUsers(users);

        PA3Processor.fillMissingRatings(users, allSongs);

        writePredictionCsv(outputFile, users, allSongs);
    }

    static void validatePredictionsExist(Map<String, User> users, Set<String> allSongs) {
        for (User user : users.values()) {
            for (String song : allSongs) {
                if(!user.hasSong(song)){
                    return;
                }
            }
        }
        throw new IllegalArgumentException("no predictions to be made");
    }

    static void writePredictionCsv(String outputFile, Map<String, User> users,
            Set<String> allSongs) throws IOException {

        try (Writer writer = new java.io.FileWriter(outputFile)) {
            writer.write("song,user,predicted rating");
            
            Set<String> songs = new TreeSet<>(allSongs);

            for (String song : songs) {
                for (User user : users.values()) {
                    if (!Double.isNaN(user.getRating(song))) {
                        continue;
                    }

                    List<User> rankedUsers = rankUsers(user, users);
                    Double prediction = PredictionEngine.predictRating(user, song, rankedUsers);

                    writer.write("\n");
                    writer.write(song + "," + user.getUsername() + "," +
                            (Double.isNaN(prediction) ? "NaN" : Integer.toString(prediction.intValue())));

                }
            }
        }
    }

    static List<User> rankUsers(User target, Map<String, User> users) {
        List<User> ranked = new ArrayList<>();

        for (User other : users.values()) {
            if (other == target) {
                continue;
            }

            double similarity = SimilarityCalculator.computeSimilarity(target, other);

            if (!Double.isNaN(similarity)) {
                ranked.add(other);
            }
        }

        ranked.sort(Comparator.comparingDouble(user -> SimilarityCalculator.computeSimilarity(target, user)));

        return ranked;
    }
}
