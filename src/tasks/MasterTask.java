package tasks;

import core.Book;
import core.Chapter;

import java.util.List;

public class MasterTask extends ATask<Book>{

    public MasterTask(Book book){
       super(book);
    }

    @Override
    public void run() {

    }
}
