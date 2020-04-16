package tasks;

import segments.Paragraph;
import utils.MutableInteger;

import java.util.List;
import java.util.concurrent.Semaphore;

public class SlaveTask extends ATask {

    public boolean[] usedLines;

    public SlaveTask(Paragraph paragraph, Semaphore parentSemaphore){
        super(paragraph, 0, parentSemaphore);
        usedLines = new boolean[paragraph.children.size()];
    }

    @Override
    public void run() {
        int id = 0;
        for(String line : (List<String>)parentSegment.children){
            if(assignLineToThread(id)){
                String[] words = line.toLowerCase().trim().split("[^a-z]+");
                for(String word : words){
                    MutableInteger count = parentSegment.wordMap.get(word);
                    if(count == null){
                        parentSegment.wordMap.put(word, new MutableInteger());
                    }else{
                        count.increment();
                    }
                }
            }
            id++;
        }
        parentSemaphore.release();
    }

    private synchronized boolean assignLineToThread(int lineId){
        boolean result = usedLines[lineId];
        usedLines[lineId] = true;
        return !result;
    }

}
