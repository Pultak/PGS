package segments;

import IO.FileInput;
import utils.Const;

import java.util.List;

public class Volume extends AFileSegment {

    public static int count = 0;

    public Volume(long volumeStartLocation, long volumeEndLocation, int id){
        super(volumeStartLocation, volumeEndLocation, id, null);
        count++;
    }

    @Override
    public void initSubSegments() {
        List<SegmentScope> segments = FileInput.findFileSegments(Const.BOOK_BEGINNING, this.startLocation, this.endLocation);

        int id = 1;
        for(SegmentScope segment : segments){
            children.add(new Book(segment.startLocation, segment.endLocation, id, this));
            ++id;
        }
    }
}
