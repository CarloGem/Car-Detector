package com.example.vmmr.ui.storicoAuto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.vmmr.MySharedViewModel;
import com.example.vmmr.R;
import com.example.vmmr.databinding.FragmentDashboardBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class StoricoAutoFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private MySharedViewModel mySharedViewModel;
    ListView history;
    View root;
    List<String> list = new ArrayList<String>();
    ArrayAdapter<String> adapter;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StoricoAutoViewModel storicoAutoViewModel =
                new ViewModelProvider(this).get(StoricoAutoViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        root = binding.getRoot();


        final TextView textView = binding.textDashboard;
        storicoAutoViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //estraggo lo storico auto da MySharedViewModel
        mySharedViewModel = new ViewModelProvider(getActivity()).get(MySharedViewModel.class);
        String s = mySharedViewModel.getCarsHistory();
        list = new ArrayList<>(Arrays.asList(s.split("\\|"))); //divido la stringa in elementi di una lista
        if (list.isEmpty())
            list.add(String.format(Locale.ITALIAN,"%s : %s\n", "Automobile", "Accuratezza"));

        history = (ListView) root.findViewById(R.id.cars_history);
        //aggiorno la ListView a schermo
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        history.setAdapter(adapter);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}