package core;

import IO.FileInput;
import utils.Const;

import java.util.List;

public class Volume extends AFileSegment {

    public Volume(long volumeStartLocation, long volumeEndLocation, int id){
        super(volumeStartLocation, volumeEndLocation, id, null);
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
