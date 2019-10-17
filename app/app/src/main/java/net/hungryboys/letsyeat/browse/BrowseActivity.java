package net.hungryboys.letsyeat.browse;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.navigation.NavigationFragment;
import net.hungryboys.letsyeat.ui.login.LoggedInUserView;

//import com.google.firebase.quickstart.fcm.R;

public class BrowseActivity extends AppCompatActivity {

    public static final String EXTRA_USER_DATA = "user_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            LoggedInUserView loggedInUserView = extras.getParcelable(EXTRA_USER_DATA);
            Log.d(EXTRA_USER_DATA, loggedInUserView.toString());
        }


        FragmentManager fragmentManager = getSupportFragmentManager();

        // Add Browse List
        FragmentTransaction browseTransaction = fragmentManager.beginTransaction();
        BrowseFragment browseFragment = new BrowseFragment();
        browseTransaction.replace(R.id.main_browse_container, browseFragment);
        browseTransaction.commit();

        // Add Navigation bar
        FragmentTransaction navigationTransaction = fragmentManager.beginTransaction();
        NavigationFragment navigationFragment = NavigationFragment.newInstance(R.id.navigation_browse);
        navigationTransaction.add(R.id.main_navigation_container, navigationFragment);
        navigationTransaction.commit();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                    }
                });
    }


}
