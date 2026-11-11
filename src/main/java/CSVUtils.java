import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
/**
 * CSV reader and parser class
 */
public final class CSVUtils {
    private CSVUtils() {}
    /**
     * CSV reader/parser class
     * 
     * @param inputFile name of CSV input file
     * @param consumer operation to preform on each CSV record
     * @throws IOException if the file cannot be read or parsed
     */
    public static void forEachRecord(String inputFile, Consumer<CSVRecord> consumer) throws IOException {
        try(Reader reader = new FileReader(inputFile);
            CSVParser parser = CSVFormat.DEFAULT.parse(reader)){
                for(CSVRecord record : parser) {
                    consumer.accept(record);
                }
            }
    }
}
