package core;

import IO.FileInput;


public class Paragraph extends AFileSegment<String> {

    public final Chapter parentChapter;


    public Paragraph(long paragraphStartLocation, long paragraphEndLocation, Chapter parentChapter) {
        super(paragraphStartLocation, paragraphEndLocation);
        this.parentChapter = parentChapter;
    }

    @Override
    public void initSubSegments() {
        children.addAll(FileInput.readParagraph(this.startLocation, this.endLocation));
    }
}
