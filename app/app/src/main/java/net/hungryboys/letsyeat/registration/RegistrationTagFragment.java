package net.hungryboys.letsyeat.registration;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.browse.TagGridAdapter;

/**
 * Fragment that handles tag selection for the RegistrationActivity. Activities that use this
 * fragment must implement the {@link RegistrationTagFragment.OnTagSelectedListener} interface
 */
public class RegistrationTagFragment extends Fragment {

    private static final String ARG_TAGS = "tags";

    private OnTagSelectedListener mListener;    // The parent activity represented as this interface
    private String[] tags;

    private View root;
    private TagGridAdapter tagGridAdapter;
    private RecyclerView tagGrid;


    public RegistrationTagFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tags the tags that need to be displayed by this fragment
     * @return A new instance of fragment RegistrationTagFragment.
     */
    public static RegistrationTagFragment newInstance(String[] tags) {
        RegistrationTagFragment f = new RegistrationTagFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray(ARG_TAGS, tags);
        f.setArguments(bundle);
        return f;
    }

    /**
     * Called when an activity attaches itself to its child fragment, this is when we save the
     * activity as a listener with the callback method in {@link RegistrationTagFragment.OnTagSelectedListener}
     * @param context the parent activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTagSelectedListener) {
            mListener = (OnTagSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Called when activity creates fragment, retrieves arguments passed to it from the
     * factory method {@link RegistrationTagFragment#newInstance(String[])}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tags = getArguments().getStringArray(ARG_TAGS);
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (root != null) {
            return root;
        }

        root = inflater.inflate(R.layout.fragment_registration_tag, container, false);
        tagGrid = root.findViewById(R.id.registration_tags_grid);

        return root;
    }

    /**
     * Called after layout has been inflated, and activity has been created, this is when we need to
     * initialise this fragment's state (the view model) and the listeners
     * @param savedInstanceState If non null, previous state of fragment being reconstructed
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String tag = buttonView.getText().toString();
                if (mListener != null) {
                    mListener.onTagSelected(tag, isChecked);
                }
            }
        };

        tagGridAdapter = new TagGridAdapter(tags);
        tagGridAdapter.setListener(listener);
        tagGrid.setHasFixedSize(true);
        tagGrid.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        tagGrid.setAdapter(tagGridAdapter);
    }

    /**
     * When an activity detaches itself from the fragment, removes the listener
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Interface defining which methods activities need to implement to communicate with fragment
     * Will be called when event occurs
     */
    public interface OnTagSelectedListener {
        void onTagSelected(String tag, boolean isChecked);
    }
}
