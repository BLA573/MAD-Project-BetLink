package com.example.betlink.common;
import com.example.betlink.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.betlink.databinding.ActivityMessageDetailBinding;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;

public class MessageDetailActivity extends AppCompatActivity {

    private ActivityMessageDetailBinding binding;
    private ChatAdapter adapter;
    private List<ChatMessage> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String sender = getIntent().getStringExtra("sender");
        String bookingRef = getIntent().getStringExtra("bookingRef");
        String bookingStatus = getIntent().getStringExtra("bookingStatus");
        ArrayList<String> conversation = getIntent().getStringArrayListExtra("conversation");

        binding.textChatPartnerName.setText(sender);
        binding.textBookingContextTitle.setText(getString(R.string.booking_ref_label, bookingRef));
        binding.textBookingContextStatus.setText(bookingStatus);

        adapter = new ChatAdapter();
        binding.recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewChat.setAdapter(adapter);

        if (conversation != null) {
            for (String msg : conversation) {
                messageList.add(new ChatMessage(msg, "10:45 AM", false));
            }
            adapter.submitList(messageList);
            if (!messageList.isEmpty()) {
                binding.recyclerViewChat.scrollToPosition(messageList.size() - 1);
            }
        }

        binding.toolbarMessageDetail.setNavigationOnClickListener(v -> finish());

        binding.buttonSendMessage.setOnClickListener(v -> sendMessage());
        
        setupQuickReplies();

        binding.buttonAttachment.setOnClickListener(v -> {
            Toast.makeText(this, "Attachment support coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupQuickReplies() {
        View.OnClickListener listener = v -> {
            if (v instanceof Chip) {
                String text = ((Chip) v).getText().toString();
                binding.editMessageInput.setText(text);
                binding.editMessageInput.setSelection(text.length());
            }
        };

        binding.chipReplyAvailable.setOnClickListener(listener);
        binding.chipReplyAddress.setOnClickListener(listener);
        binding.chipReplyWifi.setOnClickListener(listener);
        binding.chipReplyChecking.setOnClickListener(listener);
    }
    
    private void sendMessage() {
        String text = binding.editMessageInput.getText().toString().trim();
        if (!text.isEmpty()) {
            messageList.add(new ChatMessage(text, "Just now", true));
            adapter.submitList(new ArrayList<>(messageList));
            binding.editMessageInput.setText("");
            binding.recyclerViewChat.smoothScrollToPosition(messageList.size() - 1);
        }
    }

    private static class ChatMessage {
        String content;
        String time;
        boolean isMe;

        ChatMessage(String content, String time, boolean isMe) {
            this.content = content;
            this.time = time;
            this.isMe = isMe;
        }
    }

    private static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
        private List<ChatMessage> items = new ArrayList<>();

        public void submitList(List<ChatMessage> list) {
            this.items = list;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
            return new ChatViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            ChatMessage msg = items.get(position);
            holder.content.setText(msg.content);
            holder.time.setText(msg.time);
            
            android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) holder.bubble.getLayoutParams();
            if (msg.isMe) {
                holder.bubble.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.brand_primary));
                holder.content.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
                holder.container.setGravity(android.view.Gravity.END);
                params.gravity = android.view.Gravity.END;
            } else {
                holder.bubble.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.surface_alt));
                holder.content.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_primary));
                holder.container.setGravity(android.view.Gravity.START);
                params.gravity = android.view.Gravity.START;
            }
            holder.bubble.setLayoutParams(params);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ChatViewHolder extends RecyclerView.ViewHolder {
            TextView content, time;
            com.google.android.material.card.MaterialCardView bubble;
            android.widget.LinearLayout container;
            ChatViewHolder(View v) {
                super(v);
                content = v.findViewById(R.id.textMessageContent);
                time = v.findViewById(R.id.textTimestamp);
                bubble = v.findViewById(R.id.cardMessageBubble);
                container = (android.widget.LinearLayout) v;
            }
        }
    }
}
