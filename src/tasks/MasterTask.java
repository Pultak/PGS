package tasks;

import IO.FileOutput;
import segments.Book;
import segments.Chapter;
import utils.Const;
import utils.Functions;

import java.util.List;
import java.util.concurrent.Semaphore;

public class MasterTask extends ATask{

    public MasterTask(Book book, Semaphore parentSemaphore){
       super(book, Const.COUNT_OF_FOREMAN_THREADS, parentSemaphore);
    }

    @Override
    public void run() {
        for(Chapter chapter : (List<Chapter>)parentSegment.children){
            //is chapter free?
            if(chapter.setAssigned()){

                chapter.initSubSegments();
                FileOutput.createOutputFiles(chapter);
                for(int i = 0; i < Const.COUNT_OF_FOREMAN_THREADS; ++i){
                    Thread thread = new Thread(new ForemanTask(chapter, localSemaphore));
                    thread.start();
                }
                try {
                    localSemaphore.acquire(Const.COUNT_OF_FOREMAN_THREADS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }

                Functions.sumUpEveryWord(chapter.wordMap, chapter.children);
                writeWordStatisticsToFile(chapter);
                writeToAllStateFiles(chapter);
            }
        }

        //System.out.println("MASTER THREAD DONE! ("+parentSegment+")");
        parentSemaphore.release();
    }
}
