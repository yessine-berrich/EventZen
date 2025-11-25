package com.example.eventzen;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final Context context;
    private Cursor cursor;
    private final UserClickListener listener;

    public interface UserClickListener {
        void onUserClick(int userId, String currentRole, String userName);
    }

    public UserAdapter(Context context, Cursor cursor, UserClickListener listener) {
        this.context = context;
        this.cursor = cursor;
        this.listener = listener;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvEmail, tvRole;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_user_name);
            tvEmail = itemView.findViewById(R.id.tv_user_email);
            tvRole = itemView.findViewById(R.id.tv_user_role);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        try {
            // Récupération des données
            final int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID));
            String nom = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NOM));
            String prenom = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRENOM));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_EMAIL));
            String role = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ROLE));

            // Affichage
            String fullName = prenom + " " + nom;
            holder.tvName.setText(fullName);
            holder.tvEmail.setText(email);
            holder.tvRole.setText(role.toUpperCase()); // Afficher le rôle en majuscules

            // Gestion du clic pour modifier le rôle
            holder.itemView.setOnClickListener(v -> listener.onUserClick(userId, role, fullName));

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}