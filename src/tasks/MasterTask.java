package tasks;

import core.Book;
import core.Chapter;
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
        parentSegment.initSubSegments();
        for(Chapter chapter : (List<Chapter>)parentSegment.children){
            if(chapter.setAssigned()){
                for(int i = 0; i < Const.COUNT_OF_FOREMAN_THREADS; i++){
                    Thread thread = new Thread(new ForemanTask(chapter, localSemaphore));
                    thread.start();
                }
                try {
                    localSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }

                Functions.sumUpEveryWord(chapter.wordMap, chapter.children);
                writeWordStatisticsToFile();
                writeToAllStateFiles("Chapter "+chapter.id+" - OK");
            }
        }

        System.out.println("MASTER THREAD DONE!");

        parentSemaphore.release();
    }
}
