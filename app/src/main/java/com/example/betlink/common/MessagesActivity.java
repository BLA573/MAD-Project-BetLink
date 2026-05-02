package com.example.betlink.common;
import com.example.betlink.R;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.betlink.data.MockRepository;
import com.example.betlink.databinding.ActivityMessagesBinding;
import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    private ActivityMessagesBinding binding;
    private MessagesAdapter adapter;
    private List<MockRepository.MessageThread> allThreads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessagesAdapter(thread -> {
            Intent intent = new Intent(this, MessageDetailActivity.class);
            intent.putExtra("sender", thread.sender);
            intent.putExtra("bookingRef", thread.bookingRef);
            intent.putExtra("bookingStatus", thread.bookingStatus);
            intent.putStringArrayListExtra("conversation", new ArrayList<>(thread.conversation));
            startActivity(intent);
        });
        binding.recyclerViewMessages.setAdapter(adapter);
        
        allThreads = MockRepository.getInstance().getMessages();
        adapter.submitList(allThreads);

        binding.editSearchMessages.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMessages(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterMessages(String query) {
        if (query.isEmpty()) {
            adapter.submitList(allThreads);
            return;
        }
        List<MockRepository.MessageThread> filtered = new ArrayList<>();
        for (MockRepository.MessageThread thread : allThreads) {
            if (thread.sender.toLowerCase().contains(query.toLowerCase()) || 
                thread.lastMessage.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(thread);
            }
        }
        adapter.submitList(filtered);
    }

    private static class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
        
        interface OnThreadClickListener {
            void onThreadClick(MockRepository.MessageThread thread);
        }

        private List<MockRepository.MessageThread> items;
        private final OnThreadClickListener listener;

        public MessagesAdapter(OnThreadClickListener listener) {
            this.listener = listener;
        }

        public void submitList(List<MockRepository.MessageThread> list) {
            this.items = list;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_thread, parent, false);
            return new MessageViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
            MockRepository.MessageThread thread = items.get(position);
            holder.sender.setText(thread.sender);
            holder.lastMessage.setText(thread.lastMessage);
            holder.time.setText(thread.time);
            
            if (thread.unreadCount > 0) {
                holder.unreadIndicator.setVisibility(View.VISIBLE);
                holder.unreadIndicator.setText(String.valueOf(thread.unreadCount));
            } else {
                holder.unreadIndicator.setVisibility(View.GONE);
            }

            holder.bookingRef.setText(thread.bookingRef);
            holder.bookingStatus.setText(thread.bookingStatus);

            holder.itemView.setOnClickListener(v -> listener.onThreadClick(thread));
        }

        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }

        static class MessageViewHolder extends RecyclerView.ViewHolder {
            TextView sender, lastMessage, time, unreadIndicator, bookingRef, bookingStatus;
            MessageViewHolder(View v) {
                super(v);
                sender = v.findViewById(R.id.textSenderName);
                lastMessage = v.findViewById(R.id.textLastMessage);
                time = v.findViewById(R.id.textMessageTime);
                unreadIndicator = v.findViewById(R.id.textUnreadIndicator);
                bookingRef = v.findViewById(R.id.textBookingRef);
                bookingStatus = v.findViewById(R.id.textBookingStatus);
            }
        }
    }
}
