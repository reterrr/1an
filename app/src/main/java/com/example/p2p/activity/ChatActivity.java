package com.example.p2p.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.p2p.Model.Chat;
import com.example.p2p.CurrentUserManager;
import com.example.p2p.Model.Message;

import com.example.p2p.Model.Message_;
import com.example.p2p.Model.User;
import com.example.p2p.ObjectBox;
import com.example.p2p.activity.adapter.ChatAdapter;
import com.example.p2p.databinding.ActivityChatBinding;

import java.util.Collections;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.query.Query;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private ChatAdapter adapter;
    private Box<Chat> chatBox;
    private Box<Message> messageBox;
    private Chat chat;
    private User otherUser;
    private long currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 0) Determine current user ID however you store it
        //    (could be in SharedPreferences or a singleton)
        currentUserId = CurrentUserManager.getUser().id;/* e.g. Prefs.get().getLong("currentUserId", -1) */
        ;

        // 1) Load Chat by ID
        long chatId = getIntent().getLongExtra("chat_id", -1L);
        if (chatId < 0) {
            Toast.makeText(this, "Chat not specified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        chatBox = ObjectBox.get().boxFor(Chat.class);
        messageBox = ObjectBox.get().boxFor(Message.class);

        chat = chatBox.get(chatId);
        if (chat == null) {
            Toast.makeText(this, "Chat not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2) Determine the “other” user from the participants list
        for (User u : chat.participants) {
            if (u.id != currentUserId) {
                otherUser = u;
                break;
            }
        }

        if (otherUser == null) {
            Toast.makeText(this, "Chat participants invalid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 3) Set up Toolbar
        Toolbar toolbar = binding.toolbarInclude.chatToolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Populate toolbar views
        binding.toolbarInclude.ivAvatar
                .setContentDescription(otherUser.username + " avatar");

        binding.toolbarInclude.tvChatUsername
                .setText(otherUser.username);

        String ipAndPort = otherUser.networkInfo.getTarget().ip
                + ":" + otherUser.networkInfo.getTarget().port;
        binding.toolbarInclude.tvChatIp
                .setText(ipAndPort);

        // 4) RecyclerView + Adapter
        adapter = new ChatAdapter();
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMessages.setAdapter(adapter);

        // 5) Load & display messages
        loadMessages();
    }

    private void loadMessages() {
        // Query messages in this chat, sort by timestamp descending
        Query<Message> q = messageBox.query()
                .equal(Message_.chatId, chat.id)
                .orderDesc(Message_.createdTimestamp)
                .build();

        List<Message> list = q.find();
        Collections.reverse(list);   // oldest at top
        adapter.setMessages(list);

        if (!list.isEmpty()) {
            binding.rvMessages.scrollToPosition(list.size() - 1);
        }
    }
}
