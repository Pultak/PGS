package tasks;

import core.Paragraph;
import utils.MutableInteger;

import java.util.List;
import java.util.concurrent.Semaphore;

public class SlaveTask extends ATask {

    public SlaveTask(Paragraph paragraph, Semaphore parentSemaphore){
        super(paragraph, 0, parentSemaphore);
    }

    @Override
    public void run() {
        for(String line : (List<String>)parentSegment.children){
            String[] words = line.toLowerCase().trim().split("[^a-z]+");
            for(String word : words){
                //if(word.length() > 0){
                    MutableInteger count = parentSegment.wordMap.get(word);
                    if(count == null){
                        parentSegment.wordMap.put(word, new MutableInteger());
                    }else{
                        count.increment();
                    }
                //}
            }
        }
        System.out.println("Slave done!");
        parentSemaphore.release();
    }
}
