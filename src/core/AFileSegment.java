package core;

import IO.FileOutput;
import utils.MutableInteger;

import java.util.*;

public abstract class AFileSegment {


    public final long startLocation;
    public final long endLocation;

    public final int id;

    public HashMap<String, MutableInteger> wordMap = new HashMap<>();

    public final List children = new ArrayList<>();
    public final AFileSegment parentSegment;

    boolean isAssigned = false;

    public AFileSegment(long startLocation, long endLocation, int id, AFileSegment parentSegment){
        this.endLocation = endLocation;
        this.startLocation = startLocation;
        this.id = id;
        this.parentSegment = parentSegment;
    }

    public abstract void initSubSegments();


    public synchronized boolean setAssigned(){
        boolean alreadyAssigned = isAssigned;
        isAssigned = true;
        return !alreadyAssigned;
    }

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
}
