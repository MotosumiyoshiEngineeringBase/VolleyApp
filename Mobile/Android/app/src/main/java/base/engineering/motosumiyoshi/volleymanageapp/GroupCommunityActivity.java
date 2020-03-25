package base.engineering.motosumiyoshi.volleymanageapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import base.engineering.motosumiyoshi.volleymanageapp.common.Constants;
import base.engineering.motosumiyoshi.volleymanageapp.model.FriendlyMessage;
import base.engineering.motosumiyoshi.volleymanageapp.model.Message;

public class GroupCommunityActivity extends BaseActivity {

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView idView;

        public GroupViewHolder(View v) {
            super(v);
            nameView = (TextView) itemView.findViewById(R.id.communityName);
            idView = (TextView) itemView.findViewById(R.id.communityId);

            nameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = nameView.getText().toString();
                    String id = idView.getText().toString();
                    Intent intent = new Intent(GroupCommunityActivity.this, ChatActivity.class);
                    intent.putExtra("groupName", name);
                    intent.putExtra("groupCode", id);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private static final String TAG = "GroupCommunityActivity";
    private RecyclerView mGroupRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<Message, GroupViewHolder> mFirebaseAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupcommunity);

        // DBからチャットグループのリストを取得する
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child(Constants.DB_NODE_NAME_MESSAGES); //Get News From DB
        DatabaseReference messageRef = mMessagesDatabaseReference.getRef();
        SnapshotParser<Message> parser = new SnapshotParser<Message>() {
            @Override
            public Message parseSnapshot(DataSnapshot dataSnapshot) {
                Message message = dataSnapshot.getValue(Message.class);
                if (message != null) {
                    message.setId(dataSnapshot.getKey());
                }
                return message;
            }
        };
        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(messageRef, parser)
                        .build();

        mGroupRecyclerView = (RecyclerView) findViewById(R.id.groupRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(false);
        mGroupRecyclerView.setLayoutManager(mLinearLayoutManager);
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message, GroupViewHolder>(options) {
            @Override
            public GroupViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new GroupViewHolder(inflater.inflate(R.layout.item_community, viewGroup, false));
            }
            @Override
            protected void onBindViewHolder(final GroupViewHolder viewHolder, int position, Message message) {
                if (message.getId() != null) {
                    viewHolder.nameView.setText(message.getName());
                    viewHolder.idView.setText(message.getId());
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
                    mGroupRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mGroupRecyclerView.setAdapter(mFirebaseAdapter);

        Button chatBtn = (Button) findViewById(R.id.createGroupSubmitButton);
        final EditText edit = (EditText)findViewById(R.id.editText);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = edit.getText().toString();
                if(name == null || name.matches("")){
                    Toast.makeText(GroupCommunityActivity.this, "Please enter a Group Name", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(GroupCommunityActivity.this, ChatActivity.class);
                    intent.putExtra("groupName", name);
                    intent.putExtra("groupCode", "");
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
            }
            else if(resultCode == RESULT_CANCELED){
                //Toast.makeText(this, "Sign In Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
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

    private void attachDatabaseReadListener(){
        if(mChildEventListener == null){
            mChildEventListener  = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    //mMessageAdapter.add(friendlyMessage);
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener(){
        if(mChildEventListener!=null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                Intent intent = new Intent(GroupCommunityActivity.this, LogoutActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
