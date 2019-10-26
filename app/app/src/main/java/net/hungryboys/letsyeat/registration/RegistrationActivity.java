package net.hungryboys.letsyeat.registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.browse.BrowseActivity;
import net.hungryboys.letsyeat.data.Recipe;
import net.hungryboys.letsyeat.data.User;
import net.hungryboys.letsyeat.login.LoginActivity;
import net.hungryboys.letsyeat.login.LoginResult;

import java.util.Calendar;

/**
 * Activity that handles registration choices for new users. Has a fragment for each step of registration,
 * which get swapped out as user progresses though. RegistrationActivity gets passed a User from login
 * which will get re-sent to server once registration is complete
 *
 * This activity implement several interface for each of its child fragments, and has the corresponding
 * methods. This allows the fragment to call this activity's method as listener when a given action occurs
 * Thanks to this, the ViewModel can be held by the activity and allows for a continuous data flow even when
 * swapping fragments
 */
public class RegistrationActivity extends AppCompatActivity implements
        RegistrationTagFragment.OnTagSelectedListener,
        RegistrationValuesFragment.OnDifficultyChangedListener,
        RegistrationValuesFragment.OnTimeChangedListener {

    public static final String EXTRA_USER_DATA = "user_data";
    private RegistrationViewModel viewModel;
    private Button nextButton;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        User user = (User) getIntent().getSerializableExtra(EXTRA_USER_DATA);

        if (savedInstanceState == null) {

            // View model handlers
            viewModel = ViewModelProviders.of(this).get(RegistrationViewModel.class);
            viewModel.setUser(user);
            viewModel.getRegistrationResult().observe(this, new Observer<LoginResult>() {
                @Override
                public void onChanged(LoginResult loginResult) {
                    if (loginResult.isSuccess() && !loginResult.needsRegistration()) {
                        registrationFinished();
                    } else {
                        returnToLogin();
                    }
                }
            });

            // Tag choice fragment
            String[] tags = getResources().getStringArray(R.array.tag_strings);

            fragmentManager = getSupportFragmentManager();
            FragmentTransaction tagTransaction = fragmentManager.beginTransaction();
            RegistrationTagFragment registrationTagFragment = RegistrationTagFragment.newInstance(tags);
            tagTransaction.replace(R.id.registration_selection_container, registrationTagFragment);
            tagTransaction.commit();

            // Next button handler
            nextButton = findViewById(R.id.registration_next_button);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nextClicked();
                }
            });
        }
    }

    private void nextClicked() {
        // Change to new fragment for rest of selection and change next button text

        FragmentTransaction valuesTransaction = fragmentManager.beginTransaction();
        RegistrationValuesFragment valuesFragment = RegistrationValuesFragment.newInstance();
        valuesTransaction.setCustomAnimations(R.anim.enter_slide_from_right, R.anim.exit_slide_to_left);
        valuesTransaction.replace(R.id.registration_selection_container, valuesFragment);
        valuesTransaction.commit();

        nextButton.setText(R.string.finish);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.finish();
            }
        });
    }



    private void registrationFinished() {
        // Registration succeeded
        Intent intent = new Intent(this, BrowseActivity.class);
        startActivity(intent);
        finish();
    }

    private void returnToLogin() {
        // If registration failed for example
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * Method called by {@link RegistrationTagFragment} when a single tag's status has changed
     * @param tag tag whose status changed
     * @param isChecked new status of tag
     */
    @Override
    public void onTagSelected(String tag, boolean isChecked) {
        if (viewModel != null) {
            viewModel.tagChanged(tag, isChecked);
        }
    }

    /**
     * Method called by {@link RegistrationValuesFragment} when the user difficulty changes
     * @param difficulty the new difficulty
     */
    @Override
    public void onDifficultyChanged(double difficulty) {
        if (viewModel != null ) {
            if (Recipe.MIN_DIFF <= difficulty && difficulty <= Recipe.MAX_DIFF) {
                viewModel.difficultyChanged(difficulty);
            } else {
                Toast.makeText(this, R.string.incorrect_difficulty, Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * Method called by {@link RegistrationValuesFragment} when the user selects another preferred
     * eating time
     * @param time new eating time
     */
    @Override
    public void onTimeChanged(Calendar time) {
        if (viewModel != null) {
            viewModel.timeChanged(time);
        }
    }
}
