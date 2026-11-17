import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
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
    }

    private static void validateSelections(String[] selections, Set<String> allSongs) {
        
        for (String song : selections) {
            if (!allSongs.contains(song)) {
                throw new IllegalArgumentException("Error: selected songs must exist");
            }
        }
    }

    private static void validateNoDuplicates(String[] selections) {
        
        Set<String> unique = new HashSet<>();

        for (String song : selections) {
            if (!unique.add(song)) {
                throw new IllegalArgumentException("Error: dublicate song selections");
            }
        }
    }
}
