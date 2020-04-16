package segments;

public class SegmentScope {

    public final long startLocation;
    public final long endLocation;

    public SegmentScope(long startLocation, long endLocation){
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }
}
