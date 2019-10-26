package net.hungryboys.letsyeat.browse;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.navigation.NavigationFragment;

/**
 * Container activity that manages the {@link BrowseFragment} list of recipes and
 * also contains a {@link NavigationFragment} to change to other activities
 */
public class BrowseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Add Browse List
        FragmentTransaction browseTransaction = fragmentManager.beginTransaction();
        BrowseFragment browseFragment = BrowseFragment.newInstance();
        browseTransaction.replace(R.id.main_browse_container, browseFragment);
        browseTransaction.commit();

        // Add Navigation bar
        FragmentTransaction navigationTransaction = fragmentManager.beginTransaction();
        NavigationFragment navigationFragment = NavigationFragment.newInstance(R.id.navigation_browse);
        navigationTransaction.add(R.id.main_navigation_container, navigationFragment);
        navigationTransaction.commit();
    }


}
