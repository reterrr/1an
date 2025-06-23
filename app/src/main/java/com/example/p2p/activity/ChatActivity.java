package com.example.p2p.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.p2p.MessageDto;
import com.example.p2p.MessageService;
import com.example.p2p.CurrentUserManager;
import com.example.p2p.Model.Message;

import com.example.p2p.Model.Message_;
import com.example.p2p.Model.User;
import com.example.p2p.ObjectBox;
import com.example.p2p.activity.adapter.ChatAdapter;
import com.example.p2p.databinding.ActivityChatBinding;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.query.Query;
import io.objectbox.reactive.DataSubscription;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private Box<Message> messageBox;
    private ChatAdapter adapter;
    private User otherUser;
    private User me;
    private MessageService service;
    private DataSubscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messageBox = ObjectBox.get().boxFor(Message.class);
        me = CurrentUserManager.getUser().user.getTarget();

        service = new MessageService();
        adapter = new ChatAdapter();

        binding.rvMessages.setAdapter(adapter);
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent() != null && getIntent().hasExtra("otherUserId")) {
            long otherId = getIntent().getLongExtra("otherUserId", -1);
            otherUser = ObjectBox.get().boxFor(User.class).get(otherId);
        }

        if (otherUser == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.toolbarInclude.chatToolbar.setTitle(otherUser.username);

        binding.btnSend.setEnabled(false);
        binding.etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.btnSend.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.btnSend.setOnClickListener(v -> {
            service.send(me, otherUser, new MessageDto(binding.etMessage.getText().toString()));
            binding.etMessage.setText("");
        });

        loadMessages();
    }

    private void loadMessages() {
        Query<Message> query = messageBox.query()
                .apply(
                        Message_.receiverId.equal(me.id)
                                .and(Message_.senderId.equal(otherUser.id))
                                .or(Message_.senderId.equal(me.id)
                                        .and(Message_.receiverId.equal(otherUser.id))
                                )
                )

                .orderDesc(Message_.createdTimestamp)
                .build();

        subscription = query.subscribe()
                .on(AndroidScheduler.mainThread())
                .observer(data -> {

                    adapter.setMessages(data);
                    if (!data.isEmpty()) {
                        binding.rvMessages.scrollToPosition(data.size() - 1);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (subscription != null && !subscription.isCanceled()) {
            subscription.cancel();
        }
    }
}
