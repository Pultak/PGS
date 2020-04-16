package tasks;

import IO.FileOutput;
import segments.Book;
import segments.Volume;
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
        for(Book book : (List<Book>)parentSegment.children){
            //is book free?
            if(book.setAssigned()){
                book.initSubSegments();
                FileOutput.createOutputFiles(book);
                for(int i = 0; i < Const.COUNT_OF_MASTER_THREADS; i++){
                    Thread thread = new Thread(new MasterTask(book, localSemaphore));
                    thread.start();
                }
                try {
                    localSemaphore.acquire(Const.COUNT_OF_MASTER_THREADS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Functions.sumUpEveryWord(book.wordMap, book.children);
                writeWordStatisticsToFile(book);
                writeToAllStateFiles(book);
            }
        }
        //System.out.println("UNDER BOSS THREAD DONE! ("+parentSegment+")");
        parentSemaphore.release();
    }
}