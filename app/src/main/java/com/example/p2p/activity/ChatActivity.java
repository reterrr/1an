package com.example.p2p.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import io.objectbox.query.Query;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private Box<Message> messageBox;
    private ChatAdapter adapter;
    private User otherUser;
    private User me;
    private MessageService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messageBox = ObjectBox.get().boxFor(Message.class);
        me = CurrentUserManager.getUser().user.getTarget();

        service = new MessageService();

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
                .equal(Message_.senderId, me.id)
                .and()
                .equal(Message_.receiverId, otherUser.id)
                .or()
                .equal(Message_.senderId, otherUser.id)
                .and()
                .equal(Message_.receiverId, me.id)
                .orderDesc(Message_.createdTimestamp)
                .build();

        List<Message> messages = query.find();
//        adapter.submitList(messages);
        if (!messages.isEmpty()) {
            binding.rvMessages.scrollToPosition(messages.size() - 1);
        }
    }
}
