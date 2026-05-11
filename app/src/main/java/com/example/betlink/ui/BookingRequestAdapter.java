package com.example.betlink.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betlink.R;
import com.example.betlink.data.BookingRequest;

import java.util.ArrayList;
import java.util.List;

public class BookingRequestAdapter extends RecyclerView.Adapter<BookingRequestAdapter.BookingViewHolder> {

    public interface OnBookingActionListener {
        void onApprove(BookingRequest request);
        void onReject(BookingRequest request);
        void onItemClick(BookingRequest request);
    }

    private final List<BookingRequest> items = new ArrayList<>();
    private final OnBookingActionListener listener;
    private final boolean showActions;

    public BookingRequestAdapter(OnBookingActionListener listener) {
        this(listener, listener != null);
    }

    public BookingRequestAdapter(OnBookingActionListener listener, boolean showActions) {
        this.listener = listener;
        this.showActions = showActions;
    }

    public void submitList(List<BookingRequest> requests) {
        items.clear();
        items.addAll(requests);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_request, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingRequest request = items.get(position);
        holder.title.setText(holder.itemView.getContext().getString(
                R.string.booking_title,
                request.getTravelerId(),
                request.getListingId()
        ));
        holder.dates.setText(holder.itemView.getContext().getString(
                R.string.booking_dates,
                request.getCheckInDate(),
                request.getCheckOutDate()
        ));
        holder.status.setText(holder.itemView.getContext().getString(
                R.string.booking_status_value,
                request.getStatus()
        ));

        boolean pending = BookingRequest.STATUS_PENDING.equals(request.getStatus());
        
        if (listener != null && showActions) {
            holder.approve.setVisibility(View.VISIBLE);
            holder.reject.setVisibility(View.VISIBLE);
            holder.approve.setEnabled(pending);
            holder.reject.setEnabled(pending);
            holder.approve.setOnClickListener(v -> listener.onApprove(request));
            holder.reject.setOnClickListener(v -> listener.onReject(request));
            holder.itemView.setOnClickListener(v -> listener.onItemClick(request));
        } else {
            holder.approve.setVisibility(View.GONE);
            holder.reject.setVisibility(View.GONE);
            if (listener != null) {
                holder.itemView.setOnClickListener(v -> listener.onItemClick(request));
            } else {
                holder.itemView.setOnClickListener(null);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView dates;
        final TextView status;
        final Button approve;
        final Button reject;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.requestTitle);
            dates = itemView.findViewById(R.id.requestDates);
            status = itemView.findViewById(R.id.requestStatus);
            approve = itemView.findViewById(R.id.buttonApprove);
            reject = itemView.findViewById(R.id.buttonReject);
        }
    }
}
