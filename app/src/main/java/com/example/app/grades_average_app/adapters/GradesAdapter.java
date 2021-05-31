package com.example.app.grades_average_app.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;


public class GradesAdapter extends RecyclerView.Adapter<GradesAdapter.ViewHolder> {
    private final int[] gradesArray;
    private final Activity mActivity;

    public GradesAdapter(Activity mActivity, int[] gradesArray) {
        this.mActivity = mActivity;
        this.gradesArray = gradesArray;
    }

    // stores information about a single item view
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView gradeLabel;
        private final RadioGroup gradesGroup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gradeLabel = itemView.findViewById(R.id.gradeLabel);
            gradesGroup = itemView.findViewById(R.id.gradesGroup);
        }
    }

    // triggers every time new row is created
    // initializes and inflates the view for gradesArray using grades_group layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_grades_group, parent, false);
        return new ViewHolder(view);
    }

    // triggered when new row is displayed
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // setting label of grade
        holder.gradeLabel.setText(String.format(mActivity.getString(R.string.label_grades_order), position + 1));
        if (gradesArray[position] != 0) {
            holder.gradesGroup.check((holder.gradesGroup.getChildAt(gradesArray[position] - 2)).getId());
        }
        // listener that saves to gradesArray value of checked grade (position = row of checked grade)
        holder.gradesGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // first RadioButton in RadioGroup has indexOfChild = 0 and the corresponding value of grade = 2
            gradesArray[position] = holder.gradesGroup.indexOfChild(holder.gradesGroup.findViewById(checkedId)) + 2;
            // alternative way; parsing value of checked RadioButton text to int
            //gradesArray[position] = Integer.parseInt(((RadioButton) group.findViewById(checkedId)).getText().toString());
        });
    }

    // idk why or how but it works
    // disabling items changing on scroll
    @Override
    public long getItemId(int position) {
        return position;
    }

    // disabling items changing on scroll
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // size of display list equal to length of gradesArray
    @Override
    public int getItemCount() {
        return gradesArray.length;
    }
}