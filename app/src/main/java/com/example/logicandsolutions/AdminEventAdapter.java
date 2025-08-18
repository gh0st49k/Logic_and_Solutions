package com.example.logicandsolutions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.AdminEventViewHolder> {

    private List<Event> eventList;
    private OnEventActionListener listener;

    public interface OnEventActionListener {
        void onEventClick(Event event);
        void onDeleteEvent(Event event);
    }

    public AdminEventAdapter(List<Event> eventList, OnEventActionListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_event, parent, false);
        return new AdminEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminEventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class AdminEventViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText, dateText, organizerText, attendeesText;
        private Button deleteButton;

        public AdminEventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.eventTitle);
            dateText = itemView.findViewById(R.id.eventDate);
            organizerText = itemView.findViewById(R.id.eventOrganizer);
            attendeesText = itemView.findViewById(R.id.eventAttendees);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEventClick(eventList.get(position));
                }
            });

            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteEvent(eventList.get(position));
                }
            });
        }

        public void bind(Event event) {
            titleText.setText(event.getTitle());
            dateText.setText(event.getDate() + " at " + event.getTime());
            organizerText.setText("By: " + event.getOrganizerName());
            attendeesText.setText(event.getCurrentAttendees() + "/" + event.getMaxAttendees() + " attendees");
        }
    }
}