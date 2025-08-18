package com.example.logicandsolutions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public EventAdapter(List<Event> eventList, OnEventClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText, dateText, locationText, priceText, categoryText, attendeesText;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.eventTitle);
            dateText = itemView.findViewById(R.id.eventDate);
            locationText = itemView.findViewById(R.id.eventLocation);
            priceText = itemView.findViewById(R.id.eventPrice);
            categoryText = itemView.findViewById(R.id.eventCategory);
            attendeesText = itemView.findViewById(R.id.eventAttendees);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEventClick(eventList.get(position));
                }
            });
        }

        public void bind(Event event) {
            titleText.setText(event.getTitle());
            dateText.setText(event.getDate() + " at " + event.getTime());
            locationText.setText(event.getLocation());
            priceText.setText(event.getPrice() == 0 ? "Free" : "₹" + event.getPrice());
            categoryText.setText(event.getCategory());
            attendeesText.setText(event.getCurrentAttendees() + "/" + event.getMaxAttendees() + " attendees");
        }
    }
}