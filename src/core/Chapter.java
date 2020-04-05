package core;

import IO.FileInput;
import java.util.List;

public class Chapter extends AFileSegment<Paragraph> {

    public final Book parentBook;


    public Chapter(long chapterStartLocation, long chapterEndLocation, Book parentBook) {
        super(chapterStartLocation, chapterEndLocation);
        this.parentBook = parentBook;
    }

    @Override
    public void initSubSegments() {

        List<SegmentScope> segments = FileInput.findParagraphSegments(this.startLocation, this.endLocation);

        for(SegmentScope segment : segments){
            children.add(new Paragraph(segment.startLocation, segment.endLocation, this));
        }

    }
}
