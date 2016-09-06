package ca.mihailistov.lift;

/**
 * Created by mihai on 16-09-05.
 */
public class ExerciseChild {
    private int setNum;
    private int weight;
    private int reps;

    public ExerciseChild(int num, int weight, int reps) {
        this.setNum = num;
        this.weight = weight;
        this.reps = reps;
    }

    public int getSetNum(){
        return setNum;
    }

    public int getWeight(){
        return weight;
    }

    public int getReps(){
        return reps;
    }

}
