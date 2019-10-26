package net.hungryboys.letsyeat.registration;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.data.Recipe;

import java.util.Calendar;
import java.util.Locale;

/**
 * Fragment that handles time and difficulty selection
 *
 * Activities that contain this fragment must implement the
 * {@link RegistrationValuesFragment.OnTimeChangedListener} interface
 * and the {@link RegistrationValuesFragment.OnDifficultyChangedListener} interface
 * to handle interaction events.
 * Use the {@link RegistrationValuesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationValuesFragment extends Fragment {
    private OnDifficultyChangedListener diffListener;
    private OnTimeChangedListener timeListener;

    private View rootView;

    private TextView timeText;
    private Button timeChangeButton;
    private NumberPicker difficultyPicker;

    public RegistrationValuesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RegistrationValuesFragment.
     */
    public static RegistrationValuesFragment newInstance() {
        return new RegistrationValuesFragment();
    }

    /**
     * Called when an activity attaches itself to its child fragment, this is when we save the
     * activity as a listener with the callback method in {@link RegistrationValuesFragment.OnTimeChangedListener}
     * and the callback method in {@link RegistrationValuesFragment.OnDifficultyChangedListener}
     * @param context the parent activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnDifficultyChangedListener
                && context instanceof OnTimeChangedListener) {
            diffListener = (OnDifficultyChangedListener) context;
            timeListener = (OnTimeChangedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTimeChangedListener and OnDifficultyChangedListener");
        }
    }

    /**
     * Called when fragment needs to draw its user interface, only inflate layout in this method
     * @param inflater Inflater user to draw this fragment's layout
     * @param container ViewGroup that contains this fragment
     * @param savedInstanceState If non null, previous state of fragment being reconstructed
     * @return An inflated View for this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }

        rootView = inflater.inflate(R.layout.fragment_registration_values, container, false);

        timeText = rootView.findViewById(R.id.registration_time_text);
        timeChangeButton = rootView.findViewById(R.id.registration_change_time_button);
        difficultyPicker = rootView.findViewById(R.id.registration_difficulty_picker);

        return rootView;
    }

    /**
     * Called after layout has been inflated, and activity has been created, this is when we need to
     * initialise this fragment's state (the view model) and the listeners
     * @param savedInstanceState If non null, previous state of fragment being reconstructed
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        createTimePicker();
        createDifficultyPicker();
    }

    /**
     * When an activity detaches itself from the fragment, removes the listener
     */
    @Override
    public void onDetach() {
        super.onDetach();

        diffListener = null;
        timeListener = null;
    }


    private void createDifficultyPicker() {
        difficultyPicker.setMaxValue((int) Recipe.MAX_DIFF);
        difficultyPicker.setMinValue((int) Recipe.MIN_DIFF);
        difficultyPicker.setValue((int) ((Recipe.MIN_DIFF + Recipe.MAX_DIFF) / 2));
        difficultyPicker.setWrapSelectorWheel(false);

        difficultyPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                diffListener.onDifficultyChanged((double) newVal);
            }
        });
    }

    private void createTimePicker() {
        // Create time picker with current time as starting value
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        // Set TextView of time to selected time
                        timeText.setText( String.format(Locale.getDefault(),
                                "%2d : %2d", selectedHour, selectedMinute));

                        if (timeListener != null) {
                            Calendar now = Calendar.getInstance();
                            now.set(Calendar.HOUR_OF_DAY, selectedHour);
                            now.set(Calendar.MINUTE, selectedMinute);

                            // Call activity's listener method
                            timeListener.onTimeChanged(now);
                        }
                    }
                }, hour, minute, true);

                mTimePicker.setTitle(getString(R.string.select_time));
                mTimePicker.show();
            }
        };

        timeChangeButton.setOnClickListener(listener);
        timeText.setOnClickListener(listener);
    }

    /**
     * Interface defining which methods activities need to implement to communicate with fragment
     * Will be called when difficulty change occurs
     */
    public interface OnDifficultyChangedListener {
        void onDifficultyChanged(double difficulty);
    }

    /**
     * Interface defining which methods activities need to implement to communicate with fragment
     * Will be called when selected time changes
     */
    public interface OnTimeChangedListener {
        void onTimeChanged(Calendar time);
    }
}
