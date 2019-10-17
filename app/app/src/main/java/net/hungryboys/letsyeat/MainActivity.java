package net.hungryboys.letsyeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.quickstart.fcm.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.hungryboys.letsyeat.browse.BrowseFragment;
import net.hungryboys.letsyeat.navigation.NavigationFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FragmentManager fragmentManager = getSupportFragmentManager();

        // Add Browse List
        FragmentTransaction browseTransaction = fragmentManager.beginTransaction();
        BrowseFragment browseFragment = new BrowseFragment();
        browseTransaction.add(R.id.main_browse_fragment_container, browseFragment);
        browseTransaction.commit();

        // Add Navigation bar
        FragmentTransaction navigationTransaction = fragmentManager.beginTransaction();
        NavigationFragment navigationFragment = NavigationFragment.newInstance(R.id.navigation_browse);
        navigationTransaction.add(R.id.main_nav_fragment_container, navigationFragment);
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
