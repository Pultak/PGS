package segments;

import IO.FileInput;
import java.util.List;

public class Chapter extends AFileSegment{

    public static int count = 0;

    public Chapter(long chapterStartLocation, long chapterEndLocation, int id, Book parentBook) {
        super(chapterStartLocation, chapterEndLocation, id, parentBook);
        count++;
    }

    @Override
    public void initSubSegments() {

        List<SegmentScope> segments = FileInput.findParagraphSegments(this.startLocation, this.endLocation);
        int id = 1;
        for(SegmentScope segment : segments){
            children.add(new Paragraph(segment.startLocation, segment.endLocation, id, this));
            ++id;
        }

    }
}
