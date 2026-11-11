import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TestCs214Project {

    @TempDir
    Path tempDir;

    @Test
    public void invalidThirdArgument() {
        try {
            ProcessorChooser.processorHandler(new String[]{"in.csv", "out.csv", "-z"});
        } catch (IllegalArgumentException e) {
            assertEquals("Error: invalid third argument", e.getMessage());
        }
    }

    @Test 
    public void similarityCalculatorWorks() {
        User user1 = new User("user1");
        user1.addRating("A", 1);
        user1.addRating("B", 3);

        User user2 = new User("user2");
        user2.addRating("A", 1);
        user2.addRating("B", 5);

        user1.normalizeRatings();
        user2.normalizeRatings();

        double similarity = SimilarityCalculator.computeSimilarity(user1, user2);

        assertFalse(Double.isNaN(similarity));
    }

    @Test 
    public void predictionEnginePredictsRating() {
        User target = new User("target");
        target.addRating("A", 1);
        target.addRating("B", 5);

        User helper = new User("helper");
        helper.addRating("A", 2);
        helper.addRating("B", 4);
        helper.addRating("C", 5);

        target.normalizeRatings();
        helper.normalizeRatings();

        List<User> ranked = new java.util.ArrayList<>();

        ranked.add(helper);

        Double prediction = PredictionEngine.predictRating(target, "C", ranked);

        assertEquals(5.0, prediction);
    }

    @Test
    public void badInputExtension() throws IOException {
        Path bad = tempDir.resolve("invalid_file_type.txt");
        Files.writeString(bad, "song1,user1,3\n");

        try {
            Validator.validateInputFile(bad.toString());
        } catch (IllegalArgumentException e) {
            assertEquals("Error: input file is not a .csv", e.getMessage());
        }
    }

    @Test
    public void missingInputFile() {
        Path missing = tempDir.resolve("missing.csv");
        try {
            Files.deleteIfExists(missing);
        } catch (IOException e) {
            System.err.println("Error: could not delete temporary file");
        }

        try {
            Validator.validateInputFile(missing.toString());
        } catch (FileNotFoundException e) {
            assertEquals("Error: file " + missing.toString() + " does not exist", e.getMessage());
        }

    }

    @Test
    public void emptyInputFile() throws IOException {
        Path empty = tempDir.resolve("empty.csv");
        Files.deleteIfExists(empty);
        Files.createFile(empty);
        try {
            Validator.validateInputFile(empty.toString());
        } catch (IllegalArgumentException e) {
            assertEquals("Error: input file is empty", e.getMessage());
        }
    }

    @Test
    public void tooFewArguments() {
        try{
            ProcessorChooser.processorHandler(new String[]{"in.csv"});
        } catch(IllegalArgumentException e){
            assertEquals("Error: incorrect number of arguments", e.getMessage());
        }
    }

    @Test 
    public void tooManyArguments() {
        try{
            ProcessorChooser.processorHandler(new String[]{"in.csv", "out.csv", "-a", "-u"});
        } catch(IllegalArgumentException e){
            assertEquals("Error: incorrect number of arguments", e.getMessage());
        }
    }

    @Test
    public void pa1MalformedCsvThrows() throws IOException {
        Path input = tempDir.resolve("bad.csv");
        Files.writeString(input, "song,rating\n");

        Path output = tempDir.resolve("out.csv");

        try {
            PA1Processor.run(input.toString(), output.toString());
        } catch (IllegalArgumentException e) {
            assertEquals("malformed CSV entry", e.getMessage());
        }
    }

    @Test
    public void pa1RatingTooHigh() throws IOException {
        Path input = tempDir.resolve("bad.csv");
        Files.writeString(input, "song1, user1, 6\n");

        Path output = tempDir.resolve("out.csv");

        try {
            PA1Processor.run(input.toString(), output.toString());
        } catch (IllegalArgumentException e) {
            assertEquals("rating is not between 1 and 5", e.getMessage());
        }
    }

    @Test
    public void pa1RatingTooLow() throws IOException {
        Path input = tempDir.resolve("bad.csv");
        Files.writeString(input, "song1, user1, 0\n");

        Path output = tempDir.resolve("out.csv");

        try {
            PA1Processor.run(input.toString(), output.toString());
        } catch (IllegalArgumentException e) {
            assertEquals("rating is not between 1 and 5", e.getMessage());
        }
    }

    @Test
    public void pa1ValidStats() throws IOException {
        Path input = tempDir.resolve("songs.csv");
        Files.writeString(input,
                "s1,u1,3\n" +
                        "s1,u1,5\n" +
                        "s2,u1,4\n" +
                        "s2,u2,2\n");

        Path output = tempDir.resolve("out.csv");
        PA1Processor.run(input.toString(), output.toString());

        String csv = Files.readString(output).trim();
        assertTrue(csv.contains("s1"));
        assertTrue(csv.contains("s2"));
    }

    @Test
    public void pa1NonIntegerRating() throws IOException {
        Path input = tempDir.resolve("bad.csv");
        Files.writeString(input, "song1,user1,abs\n");

        Path output = tempDir.resolve("out.csv");

        try {
            PA1Processor.run(input.toString(), output.toString());
        } catch (IllegalArgumentException e) {
            assertEquals("ratings is not an integer", e.getMessage());
        }
    }

    @Test
    public void testRemoveUncooperativeUsers() {
        Map<String, User> users = new HashMap<>();

        User user1 = new User("user1");
        user1.addRating("A", 5.0);
        user1.addRating( "B", 3.0);

        User user2 = new User("user2");
        user2.addRating("A", 4.0);

        users.put("user1", user1);
        users.put("user2", user2);
        
        PA3Processor.removeUncooperativeUsers(users);

        assertTrue(users.containsKey("user1"));
        assertFalse(users.containsKey("user2"));
    }

    @Test
    public void pa2FillsMissingRatings() throws IOException {
        Path input = tempDir.resolve("in.csv");
        Files.writeString(input,
                "s1,u1,5\n" +
                        "s2,u1,4\n" +
                        "s2,u2,4\n" +
                        "s3,u2,3\n");

        Path output = tempDir.resolve("out.csv");
        PA2Processor.run(input.toString(), output.toString());

        String csv = Files.readString(output);
        assertTrue(csv.contains("NaN"));
    }

    @Test
    public void pa2MalformedCsvThrows() throws IOException {
        Path input = tempDir.resolve("bad.csv");
        Files.writeString(input, "song,rating\n");

        Path output = tempDir.resolve("out.csv");

        try {
            PA2Processor.run(input.toString(), output.toString());
        } catch (IllegalArgumentException e) {
            assertEquals("malformed CSV entry", e.getMessage());
        }
    }

    @Test
    public void pa3SimilarityNaNNoCommonUsers() throws IOException {
        Path input = tempDir.resolve("in.csv");
        Files.writeString(input,
                "s1,u1,5\n" +
                        "s2,u2,3\n");

        Path output = tempDir.resolve("out.csv");
        try {
            PA3Processor.run(input.toString(), output.toString());
        } catch (IllegalArgumentException e) {
            assertEquals("at least two cooperative users are required for song similarity", e.getMessage());
        }
    }

    @Test
    public void pa3SimilarityOneCommonUser() throws IOException {
        Path input = tempDir.resolve("in.csv");
        Files.writeString(input,
                "s1,u1,5\n" +
                        "s2,u1,3\n");

        Path output = tempDir.resolve("out.csv");
        try {
            PA3Processor.run(input.toString(), output.toString());
        } catch (IllegalArgumentException e) {
            assertEquals("at least two cooperative users are required for song similarity", e.getMessage());
        }
    }

    @Test
    public void pa3RemovesUncooperativeBeforeSimilarity() throws IOException {
        Path input = tempDir.resolve("in.csv");
        Files.writeString(input,
                "s1,u1,5\n" +
                        "s2,u1,5\n" +
                        "s1,u2,3\n" +
                        "s2,u2,4\n");

        Path output = tempDir.resolve("out.csv");

        try {
            PA3Processor.run(input.toString(), output.toString());
        } catch (IllegalArgumentException e) {
            assertEquals("at least two cooperative users are required for song similarity", e.getMessage());
        }
    }
}