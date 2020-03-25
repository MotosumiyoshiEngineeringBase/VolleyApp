package base.engineering.motosumiyoshi.volleymanageapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// 各Activityクラスの基底クラスです。
// セッション情報の管理とかに使う想定です。
public class BaseActivity extends AppCompatActivity {
    // Firebase instance variables
    protected FirebaseAuth mFirebaseAuth;
    protected FirebaseUser mFirebaseUser;
    protected FirebaseAuth.AuthStateListener mAuthStateListener;

    protected String mUsername;
    protected String mPhotoUrl;
    public static final String ANONYMOUS = "Anonymous";
    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER =  2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //共通認証処理
        // Firebase認証。未認証の場合はログインアクティビティに遷移させる。
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }
    }
}
