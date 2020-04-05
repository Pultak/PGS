package tasks;

import core.Book;
import core.Volume;
import utils.Const;

import java.util.ArrayList;
import java.util.List;

public class UnderBossTask extends ATask<Volume> {

    public UnderBossTask(Volume volume){
        super(volume);
    }

    @Override
    public void run() {
        parentSegment.initSubSegments();
        for(Book book : parentSegment.children){
            List<Thread> masterThreads = new ArrayList<>();
            for(int i = 0; i < Const.COUNT_OF_UNDER_BOSS_THREADS; i++){
                Thread thread = new Thread(new MasterTask(book));
                masterThreads.add(thread);
                thread.start();
            }
            for(Thread thread : masterThreads){
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            System.out.println("UNDER BOSS THREAD DONE");
            //todo file writing
        }

    }
}