package hebertmm.github.io.clientemapfisc.domain;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MessageDAO {
    @Insert
    void insert(Message message);
    @Query("DELETE from message_table")
    void deleteAll();
    @Query("SELECT * from message_table ORDER BY timestamp ASC")
    LiveData<List<Message>> findAll();
}
