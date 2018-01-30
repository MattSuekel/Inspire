package com.dashtechnologies.rapidchat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class BlogActivity extends AppCompatActivity {


    private static final int REQUEST_PERMISSION_WRITE = 1001;
    private RecyclerView mBlogList;

    private DatabaseReference mDataBase;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;

    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;

    private FrameLayout frame;

    private Bundle bundle;
    private boolean permissionGranted;

    private boolean isHome = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainblog);


        frame = (FrameLayout) findViewById(R.id.frame);



        mDataBase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");


        mDataBase.keepSynced(true);
        mDatabaseUsers.keepSynced(true);


        mBlogList = (RecyclerView) findViewById(R.id.blog_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(layoutManager);

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Rapid Chat");
        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);


    }



    @Override
    protected void onStart() {
        super.onStart();



        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(

                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mDataBase
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setUsername(model.getUsername());



                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Toast.makeText(BlogActivity.this, post_key, Toast.LENGTH_LONG).show();
                    }
                });


            }
        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;

        FirebaseAuth mAuth;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;



            mAuth = FirebaseAuth.getInstance();

        }



        public void setTitle(String title) {

            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setDesc(String desc) {

            TextView post_desc = (TextView) mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }

        public void setImage(Context ctx, String image) {

            ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);
        }

        public void setUsername(String username) {

            TextView post_desc = (TextView) mView.findViewById(R.id.post_username);
            post_desc.setText(username);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //if (Objects.equals(mAuth.getCurrentUser().getUid(), new String("fEItgrqvFwW3uP90uZbl5MXNBXE2"))) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_blogmenu, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Code for users:    || item.getItemId() == R.id.action_add && Objects.equals(mAuth.getCurrentUser().getUid(), new String("UID here"))
        if (item.getItemId() == R.id.action_add ){

            startActivity(new Intent(BlogActivity.this, PostActivity.class));

        } else if (item.getItemId() ==R.id.action_add) {

            Toast.makeText(BlogActivity.this, "Not Authorized", Toast.LENGTH_SHORT).show();

        }



        return super.onOptionsItemSelected(item);
    }


}
