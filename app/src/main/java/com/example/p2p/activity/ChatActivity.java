package com.example.p2p.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.p2p.AuthPeerRepository;
import com.example.p2p.Client;

import com.example.p2p.MessageDto;
import com.example.p2p.MessageService;
import com.example.p2p.CurrentUserManager;
import com.example.p2p.Model.Message;
import com.example.p2p.Model.Message_;
import com.example.p2p.Model.NetworkInfo;
import com.example.p2p.Model.State;
import com.example.p2p.Model.User;
import com.example.p2p.ObjectBox;
import com.example.p2p.Request.Request;
import com.example.p2p.Request.SeenRequest;
import com.example.p2p.Request.Sender;
import com.example.p2p.activity.adapter.ChatAdapter;
import com.example.p2p.databinding.ActivityChatBinding;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
    private final Executor executor = Executors.newSingleThreadExecutor();

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

        binding.toolbarInclude.tvChatUsername.setText(Sender.post2username(otherUser.username));
        binding.toolbarInclude.tvChatIp.setText(otherUser.networkInfo.getTarget().ip);

        // Mic button click listener

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

        AuthPeerRepository
                .getInstance()
                .getUsers()
                .observe(this, users -> {
                    boolean stillThere = false;
                    for (User u : users) {
                        if (u.id == otherUser.id) {
                            stillThere = true;
                            break;
                        }
                    }

                    if (!stillThere) {
                        // this peer was removed → close the chat
                        finish();
                    }
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
                .order(Message_.createdTimestamp)
                .build();

        subscription = query.subscribe()
                .on(AndroidScheduler.mainThread())
                .observer(this::onMessagesChanged);
    }

    private void handleUnseenMessages(List<Message> unseen) {
        try {
            NetworkInfo info = otherUser.networkInfo.getTarget();
            SeenRequest request = new SeenRequest(Sender.fromUser(me));
            Client client = Client.getInstance(InetAddress.getByName(info.ip), info.port);
            client.send(Request.create("/messages/seen", request));

            client.close();

            unseen.forEach(m -> m.state = State.SEEN);
        } catch (UnknownHostException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void onMessagesChanged(List<Message> data) {
        // 1) off-load the mark-seen work first:
        List<Message> toMark = data.stream()
                .filter(m -> m.receiver.getTargetId() == me.id && m.state != State.SEEN)
                .collect(Collectors.toList());
        if (!toMark.isEmpty()) {
            executor.execute(() -> {
                handleUnseenMessages(toMark);
                // persist in bulk:
                toMark.forEach(m -> m.state = State.SEEN);
                messageBox.put(toMark);
            });
        }

        // 2) update UI (this is on mainThread)
        adapter.setMessages(data);
        if (!data.isEmpty()) {
            // wait until next layout pass to scroll, avoiding jank:
            binding.rvMessages.post(() ->
                    binding.rvMessages.scrollToPosition(data.size() - 1)
            );
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (subscription != null && !subscription.isCanceled()) {
            subscription.cancel();
        }
    }
}