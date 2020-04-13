package core;

import IO.FileInput;


public class Paragraph extends AFileSegment {



    public Paragraph(long paragraphStartLocation, long paragraphEndLocation, int id, Chapter parentChapter) {
        super(paragraphStartLocation, paragraphEndLocation, id, parentChapter);
    }

    @Override
    public void initSubSegments() {
        children.addAll(FileInput.readParagraph(this.startLocation, this.endLocation));
    }
}
