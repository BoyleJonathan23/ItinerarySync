package com.example.itinerarysync.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itinerarysync.Entities.Vacation;
import com.example.itinerarysync.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {

    public interface OnVacationClickListener {
        void onVacationClick(Vacation vacation);
    }

    private final List<Vacation> displayedVacations = new ArrayList<>();
    private final List<Vacation> originalVacations = new ArrayList<>();
    private final LayoutInflater inflater;
    private OnVacationClickListener listener;

    public VacationAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnVacationClickListener(OnVacationClickListener listener) {
        this.listener = listener;
    }

    public void setVacations(List<Vacation> vacations) {
        this.originalVacations.clear();
        if (vacations != null) {
            this.originalVacations.addAll(vacations);
        }
        this.displayedVacations.clear();
        this.displayedVacations.addAll(this.originalVacations);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        displayedVacations.clear();
        if (query == null || query.trim().isEmpty()) {
            displayedVacations.addAll(originalVacations);
        } else {
            String lowerQuery = query.trim().toLowerCase(Locale.US);
            for (Vacation vacation : originalVacations) {
                boolean matchesTitle = vacation.getTitle() != null &&
                        vacation.getTitle().toLowerCase(Locale.US).contains(lowerQuery);
                boolean matchesHotel = vacation.getHotel() != null &&
                        vacation.getHotel().toLowerCase(Locale.US).contains(lowerQuery);
                if (matchesTitle || matchesHotel) {
                    displayedVacations.add(vacation);
                }
            }
        }
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.vacation_list_item, parent, false);
        return new VacationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        Vacation current = displayedVacations.get(position);
        holder.tvTitle.setText(current.getTitle());
        holder.tvHotel.setText(current.getHotel());

        String startDate = current.getStartDate() != null ? current.getStartDate() : "";
        String endDate = current.getEndDate() != null ? current.getEndDate() : "";
        String dateRange = startDate + " – " + endDate;
        holder.tvDates.setText(dateRange);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVacationClick(current);
            }
        });
    }

    @Override
    public int getItemCount() {
        return displayedVacations.size();
    }

    public static class VacationViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvHotel;
        private final TextView tvDates;

        VacationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvVacationTitle);
            tvHotel = itemView.findViewById(R.id.tvVacationHotel);
            tvDates = itemView.findViewById(R.id.tvVacationDates);
        }
    }
}
