package segments;

import IO.FileOutput;
import utils.MutableInteger;

import java.util.*;
import java.util.concurrent.Semaphore;

public abstract class AFileSegment {


    public final AFileSegment parentSegment;
    public final long startLocation;
    public final long endLocation;
    public final int id;
    public final List children = new ArrayList<>();

    boolean isAssigned = false;
    public HashMap<String, MutableInteger> wordMap = new HashMap<>();


    public AFileSegment(long startLocation, long endLocation, int id, AFileSegment parentSegment){
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.id = id;
        this.parentSegment = parentSegment;
    }

    public abstract void initSubSegments();

    public synchronized boolean setAssigned(){
        boolean alreadyAssigned = isAssigned;
        isAssigned = true;
        return !alreadyAssigned;
    }

    /**
     * Method to determinate segments file system directory and return it as string
     * @return path to segment directory
     */
    public String getActualDirectory(){
        Stack<AFileSegment> stack = new Stack<>();

        stack.push(this);
        AFileSegment parentSegment = this.parentSegment;

        while(parentSegment != null){
            stack.push(parentSegment);
            parentSegment = parentSegment.parentSegment;
        }

        StringBuilder sb = new StringBuilder();

        sb.append(FileOutput.outputRootDirectory);
        while(!stack.isEmpty()){
            AFileSegment actualSegment = stack.pop();
            sb.append(actualSegment.getClass().getSimpleName().toLowerCase());
            sb.append(actualSegment.id);
            sb.append("/");
        }

        return sb.toString();
    }

    @Override
    public String toString(){
        return this.getClass().getSimpleName() + " " +id;
    }

}
