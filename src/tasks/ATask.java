package tasks;


public abstract class ATask<PARENT> implements Runnable {

    protected final PARENT parentSegment;

    public ATask(PARENT parentSegment){
        this.parentSegment = parentSegment;
    }

}
