package core;

import IO.FileInput;
import utils.Const;

import java.util.List;

public class Book extends AFileSegment<Chapter> {

    public final Volume parentVolume;

    public Book(long bookStartLocation, long bookEndLocation, Volume parentVolume) {
        super(bookStartLocation, bookEndLocation);
        this.parentVolume = parentVolume;

    }


    @Override
    public void initSubSegments() {
        List<SegmentScope> segments = FileInput.findFileSegments(Const.BOOK_BEGINNING, this.startLocation, this.endLocation);

        for(SegmentScope segment : segments){
            children.add(new Chapter(segment.startLocation, segment.endLocation, this));
        }

    }
}
