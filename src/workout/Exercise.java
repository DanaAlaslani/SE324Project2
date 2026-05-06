package workout;

public class Exercise {

    private String exName;
    private String muscle;
    private String description;
    private String tutorialLink;
    private int targetSets;
    private int targetReps;
    private int targetWeight;

    public Exercise(String exName, String muscle, String description, String tutorialLink, int targetSets, int targetReps, int targetWeight) {
        this.exName = exName;
        this.muscle = muscle;
        this.description = description;
        this.tutorialLink = tutorialLink;
        this.targetSets = targetSets;
        this.targetReps = targetReps;
        this.targetWeight = targetWeight;
    }

    public String getExName() { 
        return exName; }
    public String getMuscle() { 
        return muscle; }
    public String getDescription() {
         return description; }
    public String getTutorialLink() { 
        return tutorialLink; }
    public int getTargetSets() { 
        return targetSets; }
    public int getTargetReps() { 
        return targetReps; }
    public int getTargetWeight() {
         return targetWeight; }

    public void setExName(String exName) {
         this.exName = exName; }
    public void setMuscle(String muscle) {
         this.muscle = muscle; }
    public void setDescription(String description) {
         this.description = description; }
    public void setTutorialLink(String tutorialLink) {
         this.tutorialLink = tutorialLink; }
    public void setTargetSets(int targetSets) {
         this.targetSets = targetSets; }
    public void setTargetReps(int targetReps) {
         this.targetReps = targetReps; }
    public void setTargetWeight(int targetWeight) {
         this.targetWeight = targetWeight; }

    public String getTutorial() {
        return tutorialLink;
    }

    @Override
    public String toString() {
        return exName + " | " + muscle + " | Sets: " + targetSets + " | Reps: " + targetReps;
    }
}