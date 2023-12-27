package ui;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.journal.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.Journal;

public class JournalRecylerAdapter extends RecyclerView.Adapter<JournalRecylerAdapter.ViewHolder> {
    private Context context;
    private List<Journal> journalList;

    public JournalRecylerAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public JournalRecylerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.journal_row,parent,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalRecylerAdapter.ViewHolder viewHolder, int position) {
        Journal journal = journalList.get(position);

        viewHolder.title.setText(journal.getTitle());
        viewHolder.thoughts.setText(journal.getThought());
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(journal.getTimeAdded().getSeconds()*1000);
        // To set image use: Picasso
        String imageUrl = journal.getImageUrl();
        viewHolder.dateAdded.setText(timeAgo);
        viewHolder.name.setText(journal.getUserName());
        Picasso.get()
                .load(imageUrl)
                .fit()
                .placeholder(R.drawable.tree)
                .into(viewHolder.image);


    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title,thoughts,dateAdded,name;
        public ImageView image;
        public ImageButton shareBtn;
        String userId,username;
        public ViewHolder(@NonNull View itemView, Context ctxt) {
            super(itemView);
            context = ctxt;
            title = itemView.findViewById(R.id.journal_title_list);
            thoughts = itemView.findViewById(R.id.journal_thought_list);
            dateAdded = itemView.findViewById(R.id.journal_timestamp_list);
            image = itemView.findViewById(R.id.journal_image_list);
            name = itemView.findViewById(R.id.journal_row_user_name);
            shareBtn = itemView.findViewById(R.id.journal_row_share_button);

//            shareBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_SEND);
//                    intent.setType("image/*");
//                    intent.putExtra(Intent.EXTRA_STREAM,journal);
//                }
//            });
        }
    }
}
