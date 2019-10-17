package net.hungryboys.letsyeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import net.hungryboys.letsyeat.browse.BrowseFragment;
import net.hungryboys.letsyeat.navigation.NavigationFragment;
import net.hungryboys.letsyeat.ui.login.LoggedInUserView;
import net.hungryboys.letsyeat.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_USER_DATA = "user_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            LoggedInUserView loggedInUserView = extras.getParcelable(EXTRA_USER_DATA);
            Log.d(EXTRA_USER_DATA, loggedInUserView.toString());
        }


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
    }


}
