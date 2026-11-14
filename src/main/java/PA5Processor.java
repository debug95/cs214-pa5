import java.io.FileNotFoundException;
import java.io.IOException;

public class PA5Processor {
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

    private static void process(String inputFile, String outputFile) throws IOException  {
        System.out.println("PA5 process method active");
    }
}
