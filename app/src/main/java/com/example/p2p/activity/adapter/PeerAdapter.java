package com.example.p2p.activity.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p2p.Model.User;
import com.example.p2p.databinding.ItemPeerBinding;

import java.util.List;

public class PeerAdapter extends RecyclerView.Adapter<PeerAdapter.PeerViewHolder> {

    private final List<User> peerList;
    private final OnPeerClickListener onPeerClickListener;

    public interface OnPeerClickListener {
        void onClick(User user);
    }

    public PeerAdapter(List<User> initialList, OnPeerClickListener listener) {
        this.peerList = initialList;
        this.onPeerClickListener = listener;
    }

    public void updateList(List<User> newList) {
        peerList.clear();
        peerList.addAll(newList);
        notifyDataSetChanged(); // For simplicity, consider DiffUtil later
    }

    @NonNull
    @Override
    public PeerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemPeerBinding binding = ItemPeerBinding.inflate(inflater, parent, false);

        return new PeerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PeerViewHolder holder, int position) {
        User user = peerList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return peerList.size();
    }

    public class PeerViewHolder extends RecyclerView.ViewHolder {
        private final ItemPeerBinding binding;

        PeerViewHolder(ItemPeerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(User user) {
            binding.tvUsername.setText(user.username.replaceAll("(@[A-z-0-9.]+)+", ""));
            binding.tvIp.setText(user.networkInfo.getTarget().ip);
            binding.tvPort.setText(String.valueOf(user.networkInfo.getTarget().port));
            binding.getRoot().setOnClickListener(v -> onPeerClickListener.onClick(user));
        }
    }
}
