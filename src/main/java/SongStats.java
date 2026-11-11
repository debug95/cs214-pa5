public class SongStats {
    private int count = 0;
    private double mean = 0.0;
    private double m2 = 0.0;

    public void addRating(int rating){
        count++;
        double delta = rating - mean;
        mean += delta/count;
        double delta2 = rating - mean;
        m2 += delta * delta2;
    }

    public int getCount() {
        return count;
    }

    public double getMean() {
        return mean;
    }

    public double getStdDev() {
        if (count > 0) {
            return Math.sqrt(m2/count);
        }else{
            return 0.0;
        }
    }
}