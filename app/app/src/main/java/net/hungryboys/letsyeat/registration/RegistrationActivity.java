package net.hungryboys.letsyeat.registration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.browse.TagGridAdapter;

public class RegistrationActivity extends AppCompatActivity {

    private RegistrationViewModel viewModel;

    private RecyclerView tagGrid;
    private TagGridAdapter tagGridAdapter;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        if (savedInstanceState == null) {
            viewModel = ViewModelProviders.of(this).get(RegistrationViewModel.class);
            String[] tags = getResources().getStringArray(R.array.tag_strings);

            CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String tag = buttonView.getText().toString();
                    viewModel.tagChanged(tag, isChecked);
                }
            };

            tagGridAdapter = new TagGridAdapter(tags, listener);
            tagGrid = findViewById(R.id.registration_tags_grid);
            tagGrid.setHasFixedSize(true);
            tagGrid.setLayoutManager(new GridLayoutManager(this, 3));
            tagGrid.setAdapter(tagGridAdapter);

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

    }



}
