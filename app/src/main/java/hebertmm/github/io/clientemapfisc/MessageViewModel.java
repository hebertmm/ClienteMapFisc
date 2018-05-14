package hebertmm.github.io.clientemapfisc;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import hebertmm.github.io.clientemapfisc.domain.Message;
import hebertmm.github.io.clientemapfisc.domain.MessageRepository;

public class MessageViewModel extends AndroidViewModel {
    private MessageRepository messageRepository;
    private LiveData<List<Message>> allMessages;

    public MessageViewModel(@NonNull Application application) {
        super(application);
        messageRepository = new MessageRepository(application);
        allMessages = messageRepository.findAll();

    }
    public LiveData<List<Message>> getAllMessages() {
        return allMessages;
    }
    public void insert(Message message){
        messageRepository.insert(message);
    }
}
