package com.glennio.videostoryboardsample;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.glennio.videostoryboardsample.examples.ActivityExampleSimple;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static class Option {
        private String displayLabel;
        private Class targetActivityClass;

        public Option(@NonNull String displayLabel, @NonNull Class targetActivityClass) {
            this.displayLabel = displayLabel;
            this.targetActivityClass = targetActivityClass;
        }

        public String getDisplayLabel() {
            return displayLabel;
        }

        public Class getTargetActivityClass() {
            return targetActivityClass;
        }

        @Override
        public String toString() {
            return displayLabel;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final List<Object> options = new ArrayList<>();
        options.add(new Option(getString(R.string.simple_example), ActivityExampleSimple.class));

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new Adapter(options, new OptionViewHolder.Callback() {
            @Override
            public void onItemClicked(int position) {
                try {
                    startActivity(new Intent(MainActivity.this, ((Option) options.get(position)).getTargetActivityClass()));
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }));

    }

    private static class Adapter extends RecyclerView.Adapter<OptionViewHolder> {

        public List<Object> data;
        private OptionViewHolder.Callback optionCallback;

        public Adapter(List<Object> data, OptionViewHolder.Callback optionCallback) {
            this.data = data;
            this.optionCallback = optionCallback;
        }


        @Override
        public OptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new OptionViewHolder(LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.item_option, parent, false), optionCallback);
        }

        @Override
        public void onBindViewHolder(OptionViewHolder holder, int position) {
            holder.bind(data.get(position).toString());
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }
    }

    private static class OptionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public interface Callback {
            void onItemClicked(int position);
        }

        private Callback callback;
        private TextView textView;

        public OptionViewHolder(View itemView, Callback callback) {
            super(itemView);
            this.callback = callback;
            this.textView = itemView.findViewById(android.R.id.text1);
            this.itemView.setOnClickListener(this);
        }

        public void bind(String text) {
            this.textView.setText(text);
        }

        @Override
        public void onClick(View v) {
            if (v.equals(itemView)) {
                if (getAdapterPosition() >= 0 && callback != null)
                    callback.onItemClicked(getAdapterPosition());
            }
        }
    }
}
