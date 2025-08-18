package com.example.logicandsolutions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText, emailText, phoneText;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.userName);
            emailText = itemView.findViewById(R.id.userEmail);
            phoneText = itemView.findViewById(R.id.userPhone);
        }

        public void bind(User user) {
            nameText.setText(user.getName());
            emailText.setText(user.getEmail());
            phoneText.setText(user.getPhone());
        }
    }
}