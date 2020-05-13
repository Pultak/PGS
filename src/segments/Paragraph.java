package segments;

import IO.FileInput;


public class Paragraph extends AFileSegment {


    public static int count = 0;
    public boolean[] usedLines;


    public Paragraph(long paragraphStartLocation, long paragraphEndLocation, int id, Chapter parentChapter) {
        super(paragraphStartLocation, paragraphEndLocation, id, parentChapter);
        count++;
    }

    @Override
    public void initSubSegments() {
        children.addAll(FileInput.readParagraph(this.startLocation, this.endLocation));
        usedLines = new boolean[this.children.size()];
    }


    public synchronized boolean assignLineToThread(int lineId){
        boolean result = usedLines[lineId];
        usedLines[lineId] = true;
        if(!result)
            System.out.println("assigning "+lineId+" to "+Thread.currentThread().getId());
        return !result;
    }
}
