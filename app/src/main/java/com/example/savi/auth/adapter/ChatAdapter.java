package com.example.savi.auth.adapter;

import android.content.Context;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.savi.auth.R;
import com.example.savi.auth.model.MessageItem;
import com.example.savi.auth.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MessageItem> messageItemList;
    private Context mContext ;

    public ChatAdapter(Context mContext) {
        this.mContext = mContext ;
        messageItemList = new ArrayList<>();
    }

    public void addMessage(MessageItem messageItem){
        if(messageItemList.contains(messageItem))return;
        messageItemList.add(messageItem);
        notifyItemInserted(messageItemList.size()-1);
    }

    public void addMessage(MessageItem messageItem,int position){
        if(messageItemList.contains(messageItem))return;
        messageItemList.add(position,messageItem);
        notifyItemInserted(position);
    }

    public void updateMessage(MessageItem messageItem){
        MessageItem tempItem = new MessageItem();
        for(MessageItem Item : messageItemList){
            if(Item.getSenderBranchKey().equals(messageItem.getSenderBranchKey())){
                tempItem = Item ; break;
            }
        }

        int position = messageItemList.indexOf(tempItem);
        messageItemList.set(position,messageItem);
        notifyItemChanged(position);


    }

    public void addMessageList(List<MessageItem> messageItems){
        messageItemList.clear();
        messageItemList.addAll(messageItems);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =null ;
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(viewType== MessageItem.SELF_MESSAGE){
             view =  inflater.inflate(R.layout.item_chat_right,parent,false);
            return new ChatSelfViewHolder(view);
        }else {
             view = inflater.inflate(R.layout.item_chat_left,parent,false);
            return new ChatResponseViewHolder(view);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if(messageItemList.get(position).getSelf())
            return MessageItem.SELF_MESSAGE ;
        else
            return MessageItem.RESPONSE_MESSAGE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType()== MessageItem.SELF_MESSAGE){
            ChatSelfViewHolder selfViewHolder = (ChatSelfViewHolder)holder;
            selfViewHolder.textViewSelfMessage.setText(messageItemList.get(position).getMessage() + "  -"+ messageItemList.get(position).getStatus());
            if(messageItemList.get(position).getStatus()==MessageItem.SENT){
                selfViewHolder.imageViewStatus.setImageResource(R.drawable.ic_check_grey600_18dp);
            }else if(messageItemList.get(position).getStatus()==MessageItem.DELIVERED){
                selfViewHolder.imageViewStatus.setImageResource(R.drawable.ic_check_all_grey600_18dp);
            }else{
                selfViewHolder.imageViewStatus.setImageResource(0);
            }
            long time = Long.parseLong(messageItemList.get(position).getTimeStamp()) ;
            String times = DateUtils.getFormattedString(time*1000, DateUtils.DATE_TIME_FORMAT_TYPE_HH_mm);
            String date = DateUtils.getFormattedString(time*1000, DateUtils.DATE_TIME_FORMAT_TYPE_dd_MM_yyyy);

            selfViewHolder.textViewTime.setText(times + "," + date);
        }else {
            ChatResponseViewHolder responseViewHolder  = (ChatResponseViewHolder)holder ;
            responseViewHolder.textViewResponseMessage.setText(messageItemList.get(position).getMessage());
            long time = Long.parseLong(messageItemList.get(position).getTimeStamp()) ;
            String times = DateUtils.getFormattedString(time*1000, DateUtils.DATE_TIME_FORMAT_TYPE_HH_mm);
            String date = DateUtils.getFormattedString(time*1000, DateUtils.DATE_TIME_FORMAT_TYPE_dd_MM_yyyy);

            responseViewHolder.textViewTime.setText(times + "," + date);
        }
    }

    @Override
    public int getItemCount() {
        return messageItemList.size();
    }

    public class ChatSelfViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewSelfMessage ;
        private TextView textViewTime ;
        private ImageView imageViewStatus ;
        public ChatSelfViewHolder(View itemView) {
            super(itemView);
            textViewSelfMessage = (TextView)itemView.findViewById(R.id.textview_msg);
            textViewTime = (TextView)itemView.findViewById(R.id.textview_msg_time);
            imageViewStatus = (ImageView)itemView.findViewById(R.id.imageview_status);
        }
    }

    public class ChatResponseViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewResponseMessage ;
        public TextView textViewTime ;
        public ChatResponseViewHolder(View itemView) {
            super(itemView);
            textViewResponseMessage = (TextView)itemView.findViewById(R.id.textview_msg);
            textViewTime = (TextView)itemView.findViewById(R.id.textview_msg_time);

        }
    }

    private String getDate1(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm dd-MMM-yyyy", cal).toString();
        return date;
    }

    public String getDate(long time){
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(time);
        return (cal.get(Calendar.YEAR) + " " + (cal.get(Calendar.MONTH) + 1) + " "
                + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":"
                + cal.get(Calendar.MINUTE));

    }
}
