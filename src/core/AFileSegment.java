package core;

import java.util.ArrayList;
import java.util.List;

public abstract class AFileSegment<C> {


    public final long startLocation;
    public final long endLocation;

    public final List<C> children = new ArrayList<>();

    boolean isAssigned = false;

    public AFileSegment(long startLocation, long endLocation){
        this.endLocation = endLocation;
        this.startLocation = startLocation;
    }


    public synchronized boolean setAssigned(){
        boolean alreadyAssigned = isAssigned;
        isAssigned = true;
        return !alreadyAssigned;
    }

    public abstract void initSubSegments();

}
