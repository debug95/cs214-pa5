import java.util.Map;

public class RatingStats {
    
    private final Map<String, Double> means;
    private final Map<String, Integer> counts;

    public RatingStats(Map<String,Double> means, Map<String, Integer> counts){
        this.means = means;
        this.counts = counts;
    }

    public Map<String, Double> getMeans() {
        return means;
    }

    public Map<String, Integer> getCounts(){
        return counts;
    }
}
