package com.example.p2p.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p2p.CurrentUserManager;
import com.example.p2p.Model.Message;
import com.example.p2p.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.VH> {
    private final List<Message> messages = new ArrayList<>();

    public void setMessages(List<Message> newList) {
        messages.clear();
        messages.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Message msg = messages.get(pos);
        boolean fromMe = (msg.sender.getTarget().id == CurrentUserManager.getUser().id);

        h.tvPeer.setVisibility(fromMe ? View.GONE : View.VISIBLE);
        h.tvMine.setVisibility(fromMe ? View.VISIBLE : View.GONE);

        String text = msg.content;

        if (fromMe) h.tvMine.setText(text);
        else h.tvPeer.setText(text);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView tvPeer, tvMine;

        VH(View itemView) {
            super(itemView);
            tvPeer = itemView.findViewById(R.id.tvPeer);
            tvMine = itemView.findViewById(R.id.tvMine);
        }
    }
}
