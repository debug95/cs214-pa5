/**
 * Central class for project, houses main method
 */
public class Cs214Project {
  /**
   * Runs the ProcessorChooser based on input and catches errors
   * 
   * @param args String command line input by the user
   */
  public static void main(String[] args) {
    try {
      ProcessorChooser.processorHandler(args);
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}