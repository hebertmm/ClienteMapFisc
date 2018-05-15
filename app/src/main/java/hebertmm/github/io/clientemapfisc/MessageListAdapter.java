package hebertmm.github.io.clientemapfisc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hebertmm.github.io.clientemapfisc.domain.Message;

public class MessageListAdapter extends RecyclerView.Adapter {


    class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageItemView;

        private MessageViewHolder(View itemView) {
            super(itemView);
            messageItemView = itemView.findViewById(R.id.textView);
        }
    }

    private final LayoutInflater mInflater;
    private List<Message> mMessages; // Cached copy of Messages

    public MessageListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new MessageViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
        if(mMessages != null){
            Message current = mMessages.get(position);
            Log.i("type", String.valueOf(current.getType()));
            if(current.getType() == 0) {
                messageViewHolder.messageItemView.setBackgroundResource(R.color.sentMessage);
                Log.i("type", "Enviada");
            }
            else {
                messageViewHolder.messageItemView.setBackgroundResource(R.color.receivedMessage);
                Log.i("type", "recebida");
            }
            messageViewHolder.messageItemView.setText(current.getText());

        }
        else
            messageViewHolder.messageItemView.setText("Sem mensagens");
    }

    @Override
    public int getItemCount() {
        if(mMessages != null)
            return mMessages.size();
        else return 0;
    }

    public void setmMessages(List<Message> mMessages) {
        this.mMessages = mMessages;
        notifyDataSetChanged();
    }
}
