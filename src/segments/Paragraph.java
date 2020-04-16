package segments;

import IO.FileInput;


public class Paragraph extends AFileSegment {


    public static int count = 0;


    public Paragraph(long paragraphStartLocation, long paragraphEndLocation, int id, Chapter parentChapter) {
        super(paragraphStartLocation, paragraphEndLocation, id, parentChapter);
        count++;
    }

    @Override
    public void initSubSegments() {
        children.addAll(FileInput.readParagraph(this.startLocation, this.endLocation));
    }
}
