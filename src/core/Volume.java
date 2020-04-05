package core;

import IO.FileInput;
import utils.Const;

import java.util.List;

public class Volume extends AFileSegment<Book> {

    public Volume(long volumeStartLocation, long volumeEndLocation){
        super(volumeStartLocation, volumeEndLocation);
    }

    @Override
    public void initSubSegments() {
        List<SegmentScope> segments = FileInput.findFileSegments(Const.BOOK_BEGINNING, this.startLocation, this.endLocation);

        for(SegmentScope segment : segments){
            children.add(new Book(segment.startLocation, segment.endLocation, this));
        }

    }


}
