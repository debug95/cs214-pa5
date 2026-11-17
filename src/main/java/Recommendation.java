public class Recommendation implements Comparable<Recommendation>{
    
    private final String userChoice;
    private final String recommendation;
    
    public Recommendation (String userChoice, String recommendation) {
        
        this.userChoice = userChoice;
        this.recommendation = recommendation;
    }

    public String getUserChoice() {
        return userChoice;
    }

    public String getRecommendation() {
        return recommendation;
    }

    @Override 
    public int compareTo(Recommendation other) {

        int first = userChoice.compareTo(other.userChoice);

        if (first != 0) {
            return first;
        }

        return recommendation.compareTo(other.recommendation);
    }
}
