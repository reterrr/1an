package com.example.p2p.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p2p.CurrentUserManager;
import com.example.p2p.Model.Message;
import com.example.p2p.Model.State;
import com.example.p2p.Model.User;
import com.example.p2p.ObjectBox;
import com.example.p2p.R;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_PEER = 0;
    private static final int VIEW_TYPE_MINE = 1;

    private final List<Message> messages = new ArrayList<>();

    @Override
    public int getItemViewType(int position) {
        Message msg = messages.get(position);
        boolean fromMe = msg.sender.getTarget().id == CurrentUserManager.getUser().user.getTargetId();

        return fromMe ? VIEW_TYPE_MINE : VIEW_TYPE_PEER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_MINE) {
            View v = inf.inflate(R.layout.item_message_my, parent, false);

            return new MineVH(v);
        } else {
            View v = inf.inflate(R.layout.item_message_another, parent, false);

            return new PeerVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        Message message = messages.get(pos);
        if (holder instanceof MineVH) {
            MineVH mineVH = (MineVH) holder;
            mineVH.tvMine.setText(message.content);
            mineVH.tvMine.setVisibility(View.VISIBLE);

            int iconRes;
            switch (message.state) {
                case SENT:
                    iconRes = R.drawable.ic_sent;
                    break;
                case RECEIVED:
                    iconRes = R.drawable.ic_received;
                    break;
                case SEEN:
                    iconRes = R.drawable.ic_seen;
                    break;
                default:
                    iconRes = R.drawable.ic_sending;
            }

            mineVH.ivStatus.setImageResource(iconRes);
            mineVH.ivStatus.setVisibility(View.VISIBLE);

        } else {
            PeerVH peerVH = (PeerVH) holder;
            peerVH.tvPeer.setText(message.content);
            peerVH.tvPeer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<Message> newList) {
        messages.clear();
        messages.addAll(newList);

        notifyDataSetChanged();
    }

    static class PeerVH extends RecyclerView.ViewHolder {
        TextView tvPeer;

        PeerVH(View itemView) {
            super(itemView);
            tvPeer = itemView.findViewById(R.id.tvPeer);
        }
    }

    static class MineVH extends RecyclerView.ViewHolder {
        TextView tvMine;
        ImageView ivStatus;

        MineVH(View itemView) {
            super(itemView);

            tvMine = itemView.findViewById(R.id.tvMine);
            ivStatus = itemView.findViewById(R.id.ivStatus);
        }
    }
}

