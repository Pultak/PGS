package core;

import IO.FileInput;
import utils.Const;

import java.util.List;

public class Book extends AFileSegment<Chapter> {


    public Book(long bookStartLocation, long bookEndLocation, int id, Volume parentVolume) {
        super(bookStartLocation, bookEndLocation, id, parentVolume);

    }


    @Override
    public void initSubSegments() {
        List<SegmentScope> segments = FileInput.findFileSegments(Const.BOOK_BEGINNING, this.startLocation, this.endLocation);

        int id = 1;
        for(SegmentScope segment : segments){
            children.add(new Chapter(segment.startLocation, segment.endLocation, id, this));
            ++id;
        }

    }
}
