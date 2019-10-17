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
import net.hungryboys.letsyeat.data.model.Recipe;

import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
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
    // TODO: Rename and change types and number of parameters
    public static RegistrationValuesFragment newInstance() {
        return new RegistrationValuesFragment();
    }

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        createTimePicker();
        createDifficultyPicker();
    }

    private void createDifficultyPicker() {
        difficultyPicker.setMaxValue((int) Recipe.MAX_DIFF);
        difficultyPicker.setMinValue((int) Recipe.MIN_DIFF);
        difficultyPicker.setValue((int) ((Recipe.MIN_DIFF + Recipe.MAX_DIFF) / 2));
        difficultyPicker.setWrapSelectorWheel(false);
    }

    private void createTimePicker() {
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
                        timeText.setText( String.format(Locale.getDefault(),
                                "%d : %d", selectedHour, selectedMinute));

                        if (timeListener != null) {
                            Calendar now = Calendar.getInstance();
                            now.set(Calendar.HOUR_OF_DAY, selectedHour);
                            now.set(Calendar.MINUTE, selectedMinute);

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

    @Override
    public void onDetach() {
        super.onDetach();
        diffListener = null;
        timeListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDifficultyChangedListener {
        void onDifficultyChanged(double difficulty);
    }

    public interface OnTimeChangedListener {
        void onTimeChanged(Calendar time);
    }
}
