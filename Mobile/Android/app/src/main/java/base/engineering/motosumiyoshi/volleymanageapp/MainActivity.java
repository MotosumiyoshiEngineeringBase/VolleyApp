package base.engineering.motosumiyoshi.volleymanageapp;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import base.engineering.motosumiyoshi.volleymanageapp.model.FriendlyMessage;
import base.engineering.motosumiyoshi.volleymanageapp.model.News;

import static base.engineering.motosumiyoshi.volleymanageapp.R.id.to_games_main;
import static base.engineering.motosumiyoshi.volleymanageapp.R.id.to_community;
import static base.engineering.motosumiyoshi.volleymanageapp.R.id.to_mypage;
import static base.engineering.motosumiyoshi.volleymanageapp.R.id.to_album;

public class MainActivity extends BaseActivity  implements GoogleApiClient.OnConnectionFailedListener{

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView createDateTextView;
        TextView newsTextView;

        public NewsViewHolder(View v) {
            super(v);
            createDateTextView = (TextView) itemView.findViewById(R.id.newsCreateDate);
            newsTextView = (TextView) itemView.findViewById(R.id.newsText);
        }
    }

    private static final String TAG = "MainActivity";
    private RecyclerView mNewsRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<News, NewsViewHolder> mFirebaseAdapter;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mNewsDatabaseReference;

    private GoogleApiClient mGoogleApiClient;

    Random rnd;
    ImageView topPageImage;
    Timer timer;
    MainTimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        topPageImage = (ImageView)findViewById(R.id.top_image);
        rnd = new Random();
        timer = new Timer();
        timerTask = new MainTimerTask();
        timer.schedule(timerTask, 1000, 5000);

        // ニュース欄の更新
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mNewsDatabaseReference = mFirebaseDatabase.getReference().child("NEWS"); //Get News From DB
        DatabaseReference newsRef = mNewsDatabaseReference.getRef();
        SnapshotParser<News> parser = new SnapshotParser<News>() {
            @Override
            public News parseSnapshot(DataSnapshot dataSnapshot) {
                News news = dataSnapshot.getValue(News.class);
                if (news != null) {
                    news.setId(dataSnapshot.getKey());
                }
                return news;
            }
        };
        FirebaseRecyclerOptions<News> options =
                new FirebaseRecyclerOptions.Builder<News>()
                        .setQuery(newsRef, parser)
                        .build();

        mNewsRecyclerView = (RecyclerView) findViewById(R.id.newsRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(false);
        mNewsRecyclerView.setLayoutManager(mLinearLayoutManager);
        mFirebaseAdapter = new FirebaseRecyclerAdapter<News, NewsViewHolder>(options) {
            @Override
            public NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new NewsViewHolder(inflater.inflate(R.layout.item_news, viewGroup, false));
            }
            @Override
            protected void onBindViewHolder(final NewsViewHolder viewHolder, int position, News news) {
                if (news.getText() != null) {
                    viewHolder.createDateTextView.setText(news.getCreateDate().toString());
                    viewHolder.newsTextView.setText(news.getText());
                }
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mNewsRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mNewsRecyclerView.setAdapter(mFirebaseAdapter);

        // 枠線を表示する
        RecyclerView.ItemDecoration dividerItemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        ((DividerItemDecoration) dividerItemDecoration).setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        mNewsRecyclerView.addItemDecoration(dividerItemDecoration);

        // bottomnavigationのclickイベント
        BottomNavigationView bottomnavigation = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomnavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case to_games_main:
                        startActivity(
                                new Intent(getApplicationContext(), GameActivity.class));
                        return true;
                    case to_album:
                        startActivity(
                                new Intent(getApplicationContext(), GameAlbumActivity.class));
                        return true;
                    case to_community:
                        startActivity(
                                new Intent(getApplicationContext(), GroupCommunityActivity.class));
                        return true;
                    case to_mypage:
                        startActivity(
                                new Intent(getApplicationContext(), ManageMyAccountActivity.class));
                        return true;
                }
                return false;
            }
        });
    }

    // TOPページのヘッダー画面切り替え処理用タスク
    public class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //TODO もっとまじめにTOPページの画面切り替え
                    int ran = rnd.nextInt(8);
                    if (ran == 0 ) topPageImage.setImageResource(R.drawable.t1);
                    if (ran == 1 ) topPageImage.setImageResource(R.drawable.t2);
                    if (ran == 2 ) topPageImage.setImageResource(R.drawable.t3);
                    if (ran == 3 ) topPageImage.setImageResource(R.drawable.t4);
                    if (ran == 4 ) topPageImage.setImageResource(R.drawable.t5);
                    if (ran == 5 ) topPageImage.setImageResource(R.drawable.t6);
                    if (ran == 6 ) topPageImage.setImageResource(R.drawable.t7);
                    if (ran == 7 ) topPageImage.setImageResource(R.drawable.t8);
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAdapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
