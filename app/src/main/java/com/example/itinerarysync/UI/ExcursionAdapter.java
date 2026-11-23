package com.example.itinerarysync.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itinerarysync.Entities.Excursion;
import com.example.itinerarysync.R;

import java.util.ArrayList;
import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {

    public interface OnExcursionClickListener {
        void onExcursionClick(Excursion excursion);
    }

    private final List<Excursion> excursionList = new ArrayList<>();
    private final LayoutInflater inflater;
    private OnExcursionClickListener listener;

    public ExcursionAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnExcursionClickListener(OnExcursionClickListener listener) {
        this.listener = listener;
    }

    public void setExcursions(List<Excursion> excursions) {
        this.excursionList.clear();
        if (excursions != null) {
            this.excursionList.addAll(excursions);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.excursion_list_item, parent, false);
        return new ExcursionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        Excursion current = excursionList.get(position);
        holder.tvTitle.setText(current.getTitle());
        holder.tvDate.setText(current.getStartDate());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExcursionClick(current);
            }
        });
    }

    @Override
    public int getItemCount() {
        return excursionList.size();
    }

    public static class ExcursionViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvDate;

        public ExcursionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvExcursionTitle);
            tvDate = itemView.findViewById(R.id.tvExcursionDate);
        }
    }
}
