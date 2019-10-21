package net.hungryboys.letsyeat.registration;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import net.hungryboys.letsyeat.MyApplication;
import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.browse.TagGridAdapter;
import net.hungryboys.letsyeat.MyApplication;
import net.hungryboys.letsyeat.APICalls.RESTcalls.user;

///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link RegistrationTagFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link RegistrationTagFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class RegistrationTagFragment extends Fragment {

    private static final String ARG_TAGS = "tags";

    private OnTagSelectedListener mListener;
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
     * @return A new instance of fragment RegistrationTagFragment.
     */
    public static RegistrationTagFragment newInstance(String[] tags) {
        RegistrationTagFragment f = new RegistrationTagFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray(ARG_TAGS, tags);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tags = getArguments().getStringArray(ARG_TAGS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (root != null) {
            return root;
        }

        root = inflater.inflate(R.layout.fragment_registration_tag, container, false);

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
        tagGrid = root.findViewById(R.id.registration_tags_grid);
        tagGrid.setHasFixedSize(true);
        tagGrid.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        tagGrid.setAdapter(tagGridAdapter);

        return root;
    }

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnTagSelectedListener {
        void onTagSelected(String tag, boolean isChecked);
    }
}
