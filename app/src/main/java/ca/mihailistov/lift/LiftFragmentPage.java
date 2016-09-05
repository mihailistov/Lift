package ca.mihailistov.lift;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mihai on 16-09-04.
 */
public class LiftFragmentPage extends Fragment {
    int mNum;

    static LiftFragmentPage newInstance(int num) {
        LiftFragmentPage f = new LiftFragmentPage();

        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lift_fragment_page, container, false);
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        rv.setHasFixedSize(true);
        MyAdapter adapter = new MyAdapter(new String[]{"test one" + mNum, "test two", "test three", "test four", "test five" , "test six" , "test seven"});
        rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        return rootView;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        GridLayout gridLayout = (GridLayout) view.findViewById(R.id.gridLayout1);
//        gridLayout.setVisibility(View.GONE);
//        TextView textView = (TextView) view.findViewById(R.id.textView1);
//        textView.setText("Current frag # " + mNum);
    }
}
