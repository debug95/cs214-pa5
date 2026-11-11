import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class PA1Processor {

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
        SongDatabase database = new SongDatabase();

        CSVUtils.forEachRecord(inputFile, record -> {
            if (record.size() != 3) {
                throw new IllegalArgumentException("malformed CSV entry");
            }

            String song = record.get(0).trim();
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

            database.add(song, rating);
        });

        CSVFormat outputFormat = CSVFormat.DEFAULT.builder()
                .setHeader("song", "number of ratings", "mean", "standard deviation")
                .build();

        try (Writer writer = new java.io.FileWriter(outputFile);
                CSVPrinter printer = new CSVPrinter(writer, outputFormat)) {

            for (var entry : database.getSongs().entrySet()) {
                String song = entry.getKey();
                SongStats stats = entry.getValue();

                printer.printRecord(
                        song,
                        stats.getCount(),
                        stats.getMean(),
                        stats.getStdDev());
            }
        }
    }
}