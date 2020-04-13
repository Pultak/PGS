package tasks;

import core.Chapter;
import core.Paragraph;
import utils.Const;
import utils.Functions;

import java.util.List;
import java.util.concurrent.Semaphore;
public class ForemanTask extends ATask{

    public ForemanTask(Chapter chapter, Semaphore parentSemaphore){
        super(chapter, Const.COUNT_OF_SLAVE_THREADS, parentSemaphore);
    }

    @Override
    public void run() {
        parentSegment.initSubSegments();
        for(Paragraph paragraph : (List<Paragraph>)parentSegment.children){
            if(paragraph.setAssigned()){
                paragraph.initSubSegments();
                for(int i = 0; i < Const.COUNT_OF_SLAVE_THREADS; i++){
                    Thread thread = new Thread(new SlaveTask(paragraph, localSemaphore));
                    thread.start();
                }
                try {
                    localSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
        Functions.sumUpEveryWord(parentSegment.wordMap, parentSegment.children);
        System.out.println("FOREMAN THREAD DONE");
        parentSemaphore.release();
    }
}
