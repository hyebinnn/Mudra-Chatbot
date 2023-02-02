package com.cookandroid.myapplication;

import android.content.Context;
import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends BaseAdapter {

    ArrayList<MessageItem> messageItems;
    LayoutInflater layoutInflater;

    public ChatAdapter(ArrayList<MessageItem> messageItems, LayoutInflater layoutInflater){
        this.messageItems = messageItems;
        this.layoutInflater = layoutInflater;
    }

    public int getCount() {         //전체 데이터 개수
        return messageItems.size();
    }

    @Override
    public Object getItem(int position) {
        return messageItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        MessageItem item = messageItems.get(position);

        View itemView = null;

        // 프로필 이미지 설정
        if (item.getName().equals(G.nickName)) {        // user일때
            itemView = layoutInflater.inflate(R.layout.my_msgbox, viewGroup, false);
            item.setProfileUrl(Uri.parse("android.resource://"+R.class.getPackage().getName()+"/"+R.drawable.img).toString());
        } else {  // 봇일때
            itemView = layoutInflater.inflate(R.layout.other_msgbox, viewGroup, false);
            item.setProfileUrl(Uri.parse("android.resource://"+R.class.getPackage().getName()+"/"+R.drawable.img_1).toString());
        }

        //만들어진 itemView에 값들 설정
        CircleImageView iv = itemView.findViewById(R.id.iv);
        TextView tvName = itemView.findViewById(R.id.tv_name);
        TextView tvMsg = itemView.findViewById(R.id.tv_msg);
        TextView tvTime = itemView.findViewById(R.id.tv_time);


        tvName.setText(item.getName());
        tvMsg.setText(item.getMessage());
        tvTime.setText(item.getTime());

        Glide.with(itemView).load(item.getProfileUrl()).into(iv);        //이미지 뿌려주기
        return itemView;
    }
}


