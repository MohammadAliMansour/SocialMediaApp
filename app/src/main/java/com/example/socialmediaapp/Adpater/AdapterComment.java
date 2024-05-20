package com.example.socialmediaapp.adpater;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediaapp.databinding.RowCommentsBinding;
import com.example.socialmediaapp.model.ModelComment;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.MyHolder> {

    Context context;
    List<ModelComment> list;

    public AdapterComment(Context context, List<ModelComment> list, String myuid, String postid) {
        this.context = context;
        this.list = list;
        this.myuid = myuid;
        this.postid = postid;
    }

    String myuid;
    String postid;


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.row_comments, parent, false);
//        return new MyHolder(view);
        RowCommentsBinding binding = RowCommentsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyHolder(binding);
    }

//    @Override
//    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
////        final String uid = list.get(position).getUid();
//        String name = list.get(position).getUname();
////        String email = list.get(position).getUemail();
//        String image = list.get(position).getUdp();
////        final String cid = list.get(position).getcId();
//        String comment = list.get(position).getComment();
//        String timestamp = list.get(position).getPtime();
//        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
//        calendar.setTimeInMillis(Long.parseLong(timestamp));
//        String timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
//
//        holder.name.setText(name);
//        holder.time.setText(timedate);
//        holder.comment.setText(comment);
//        try {
//            Glide.with(context).load(image).into(holder.imagea);
//        } catch (Exception e) {
//
//        }
//    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position){
        ModelComment comment = list.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private final RowCommentsBinding binding;
//        ImageView imagea;
//        TextView name, comment, time;

//        public MyHolder(@NonNull View itemView) {
//            super(itemView);
//            imagea = itemView.findViewById(R.id.loadcomment);
//            name = itemView.findViewById(R.id.commentname);
//            comment = itemView.findViewById(R.id.commenttext);
//            time = itemView.findViewById(R.id.commenttime);
//        }
        public MyHolder(@NonNull RowCommentsBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ModelComment comment){
            binding.commentname.setText(comment.getUname());
            binding.commenttext.setText(comment.getComment());

            long timestamp = Long.parseLong(comment.getPtime());
            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            calendar.setTimeInMillis(timestamp);
            String timeDate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
            binding.commenttime.setText(timeDate);

            try {
                Glide.with(binding.getRoot()).load(comment.getUdp()).into(binding.loadcomment);
            } catch (Exception ignored){

            }

        }
    }
}
