import java.io.File;
import java.io.FileNotFoundException;

public final class Validator {
    private Validator(){}

    public static void validateInputFile(String inputFile) throws FileNotFoundException {
        File file = new File(inputFile);

        if(!inputFile.endsWith(".csv")){
            throw new IllegalArgumentException("Error: input file is not a .csv");
        }

        if(!file.exists()){
            throw new FileNotFoundException("Error: file " + inputFile + " does not exist");
        }

        if(file.length()==0){
            throw new IllegalArgumentException("Error: input file is empty");
        }
    }

    public static void validateOutputPath(String outputFile) {
        
        if(!outputFile.endsWith(".csv")) {
            throw new IllegalArgumentException("output file is not a .csv");
        }

        File out = new File(outputFile);
        File parent = out.getParentFile();

        if(parent != null && !parent.exists()) {
            throw new IllegalArgumentException("output directory does not exist");
        }
    }
}
