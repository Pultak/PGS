package tasks;

import utils.Const;
import utils.MutableInteger;
import utils.MyEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

public class TaskManager {

    /**
     * singleton
     */
    public static final TaskManager TASK_MANAGER = new TaskManager();

    /**
     * Flag that indicates if threads are still needed
     */
    public static boolean threadsNeeded = true;

    /**
     * hasmap of tasks and their semaphore
     */
    public HashMap<Task, MyEntry<Semaphore, List<ATask>>> allTasks;

    private TaskManager(){
        allTasks = new HashMap<>();

        Task[] values = Task.values();
        MutableInteger actualTaskIndex = new MutableInteger(0);
        initLists().forEach(list -> {
            allTasks.put(values[actualTaskIndex.get()], list);
            actualTaskIndex.increment();
        });

        allTasks.forEach(((taskName, pair) -> {
            pair.getValue().forEach((task) -> {
                Thread newThread = new Thread(task);
                newThread.start();
            });
        }));
    }

    private List<MyEntry<Semaphore, List<ATask>>> initLists(){
        List<ATask> bossList = new ArrayList<>();
        for(int i = 0; i < Const.COUNT_OF_BOSS_THREADS; i++){
            bossList.add(new BossTask(i));
        }
        List<ATask> underBossList = new ArrayList<>();
        for(int i = 0; i < Const.COUNT_OF_UNDER_BOSS_THREADS; i++){
            underBossList.add(new UnderBossTask(i));
        }
        List<ATask> masterList = new ArrayList<>();
        for(int i = 0; i < Const.COUNT_OF_MASTER_THREADS; i++){
            masterList.add(new MasterTask(i));
        }
        List<ATask> foremanList = new ArrayList<>();
        for(int i = 0; i < Const.COUNT_OF_FOREMAN_THREADS; i++){
            foremanList.add(new ForemanTask(i));
        }
        List<ATask> slaveList = new ArrayList<>();
        for(int i = 0; i < Const.COUNT_OF_SLAVE_THREADS; i++){
            slaveList.add(new SlaveTask(i));
        }
        return new ArrayList<MyEntry<Semaphore, List<ATask>>>(){{
            add(new MyEntry<>(new Semaphore(Const.COUNT_OF_BOSS_THREADS), bossList));
            add(new MyEntry<>(new Semaphore(Const.COUNT_OF_UNDER_BOSS_THREADS), underBossList));
            add(new MyEntry<>(new Semaphore(Const.COUNT_OF_MASTER_THREADS), masterList));
            add(new MyEntry<>(new Semaphore(Const.COUNT_OF_FOREMAN_THREADS), foremanList));
            add(new MyEntry<>(new Semaphore(Const.COUNT_OF_SLAVE_THREADS), slaveList));}};
    }


    /**
     * Method that extracts free thread of desired type and returns it
     * @param desiredType Type of desired task thread
     * @return free thread / if there is no free thread then return null
     */
    public ATask getThread(Task desiredType){
        MyEntry<Semaphore, List<ATask>> taskPair = null;
        switch (desiredType){
            case BossTask:
                taskPair = allTasks.get(Task.BossTask);
                break;
            case UnderBossTask:
                taskPair = allTasks.get(Task.UnderBossTask);
                break;
            case MasterTask:
                taskPair = allTasks.get(Task.MasterTask);
                break;
            case ForemanTask:
                taskPair = allTasks.get(Task.ForemanTask);
                break;
            case SlaveTask:
                taskPair = allTasks.get(Task.SlaveTask);
                break;
        }
        try {
            taskPair.getKey().acquire();

            for(int i = 0; i < taskPair.getValue().size(); i++){
                ATask task = taskPair.getValue().get(i);
                if(checkAvailability(task)){
                    return task;
                }
            }
            taskPair.getKey().release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    private synchronized boolean checkAvailability(ATask task){
        boolean result = task.isTaskFree;
        if(result)
            task.isTaskFree = false;
        return result;
    }


    /**
     * Method called at the end of the application process
     */
    public void killAllThreads(){
        threadsNeeded = false;
        for(Task taskName : Task.values()){
            allTasks.get(taskName).getValue().forEach(task -> task.localSemaphore.release());
        }
    }

}
