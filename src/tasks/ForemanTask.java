package tasks;

import segments.Chapter;
import segments.Paragraph;
import utils.Const;

import java.util.List;
import java.util.concurrent.Semaphore;
public class ForemanTask extends ATask{

    public ForemanTask(Chapter chapter, Semaphore parentSemaphore){
        super(chapter, Const.COUNT_OF_SLAVE_THREADS, parentSemaphore);
    }

    @Override
    public void run() {
        List<Paragraph> list = (List<Paragraph>)parentSegment.children;
        for(int i = 0; i < list.size(); i++){
            Paragraph paragraph = list.get(i);
            //is paragraph free?
            if(paragraph.setAssigned()){
                paragraph.initSubSegments();
                for(int j = 0; j < Const.COUNT_OF_SLAVE_THREADS; j++){
                    Thread thread = new Thread(new SlaveTask(paragraph, localSemaphore));
                    thread.start();
                }
                try {
                    localSemaphore.acquire(Const.COUNT_OF_SLAVE_THREADS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
        //System.out.println("FOREMAN THREAD DONE ("+parentSegment+")");
        parentSemaphore.release();
    }
}
