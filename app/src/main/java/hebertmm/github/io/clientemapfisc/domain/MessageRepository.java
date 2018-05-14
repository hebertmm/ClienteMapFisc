package hebertmm.github.io.clientemapfisc.domain;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class MessageRepository {
    private MessageDAO messageDAO;
    private LiveData<List<Message>> allMessages;

    public MessageRepository(Application application) {
        MessageRoomDatabase db = MessageRoomDatabase.getDatabase(application);
        messageDAO = db.messageDAO();
        allMessages = messageDAO.findAll();
    }
    public LiveData<List<Message>> findAll(){
        return allMessages;
    }
    public void insert(Message message){
        new insertAsyncTask(messageDAO).execute(message);
    }

    private static class insertAsyncTask extends AsyncTask<Message, Void, Void> {

        private MessageDAO mAsyncTaskDao;

        insertAsyncTask(MessageDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Message... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
