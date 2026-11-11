public final class ErrorPrinter {
    private ErrorPrinter() {}

    public static void print(String message){
        System.err.printf("Error: %s%n", message);
    }
    
    public static void printFileNotFound(String file){
        System.err.printf("Error: file %s does not exist%n", file);
    }
}
