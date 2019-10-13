package net.hungryboys.letsyeat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import net.hungryboys.letsyeat.browse.BrowseFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add Browse List
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BrowseFragment browseFragment = new BrowseFragment();

        fragmentTransaction.add(R.id.main_browse_fragment_container, browseFragment);
        fragmentTransaction.commit();
    }


}
