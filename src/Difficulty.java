//Abstract class for three levels of difficulty
public abstract class Difficulty {
    //The count of mice, milk and timer delay for each depends on the difficulty level.
    
    //amount that pops up
    protected int miceCount;
    protected int milkCount;

    //speed
    protected int miceInterval;
    protected int milkInterval;

    //Accessor methods.
    public abstract String getDifficultyLevel();
    public int getMiceCount()    { return miceCount; }
    public int getMilkCount()    { return milkCount; }
    public int getMiceInterval() { return miceInterval; }
    public int getMilkInterval() { return milkInterval; }
}
