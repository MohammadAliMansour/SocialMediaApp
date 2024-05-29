package com.example.socialmediaapp.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediaapp.adpater.AdapterPosts;
import com.example.socialmediaapp.EditProfilePage;
import com.example.socialmediaapp.model.ModelPost;
import com.example.socialmediaapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ImageView avatar;
    TextView name, email;
    FloatingActionButton fab;
    ProgressDialog pd;
    RecyclerView recyclerView;
    AdapterPosts adapterPosts;
    List<ModelPost> posts;
    String uid;


    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference("Users");

        avatar = view.findViewById(R.id.avatar);
        name = view.findViewById(R.id.nameTextView);
        email = view.findViewById(R.id.emailTextView);
        fab = view.findViewById(R.id.fab);
        uid = FirebaseAuth.getInstance().getUid();
        recyclerView = view.findViewById(R.id.recyclerposts);
        posts = new ArrayList<>();
        pd = new ProgressDialog(getActivity());
        pd.setCanceledOnTouchOutside(false);
        loadMyPosts();


        Query query = databaseReference.orderByChild("uid").equalTo(firebaseUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String name1 = (String) dataSnapshot1.child("name").getValue();
                    String email1 = (String) dataSnapshot1.child("email").getValue();
                    String image = (String) dataSnapshot1.child("image").getValue();
                    name.setText(name1);
                    email.setText(email1);
                    try {
                        Glide.with(requireActivity()).load(image).into(avatar);
                    } catch (Exception ignored) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfilePage.class)));
        return view;
    }

    private void loadMyPosts() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = databaseReference1.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ModelPost modelPost = dataSnapshot.getValue(ModelPost.class);
                    posts.add(modelPost);
                    adapterPosts = new AdapterPosts(getActivity(), posts);
                    recyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
}
