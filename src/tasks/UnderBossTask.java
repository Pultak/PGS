package tasks;

import IO.FileOutput;
import core.Book;
import core.Volume;
import utils.Const;
import utils.Functions;

import java.util.List;
import java.util.concurrent.Semaphore;

public class UnderBossTask extends ATask {

    public UnderBossTask(Volume volume, Semaphore parentSemaphore){
        super(volume, Const.COUNT_OF_MASTER_THREADS, parentSemaphore);
    }

    @Override
    public void run() {
        parentSegment.initSubSegments();
        for(Book book : (List<Book>)parentSegment.children){
            if(book.setAssigned()){
                for(int i = 0; i < Const.COUNT_OF_MASTER_THREADS; i++){
                    Thread thread = new Thread(new MasterTask(book, localSemaphore));
                    thread.start();
                }
                try {
                    localSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Functions.sumUpEveryWord(book.wordMap, book.children);
                writeWordStatisticsToFile();
                writeToAllStateFiles("Book "+book.id+" - OK");
            }
        }
        System.out.println("UNDER BOSS THREAD DONE");
        parentSemaphore.release();


    }
}