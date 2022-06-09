package com.example.vmmr.ui.istruzioni;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.vmmr.databinding.FragmentNotificationsBinding;

public class IstruzioniFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private String instructions_String = "Per iniziare andare nella schermata Foto e Scan e premere il bottone Camera " +
            "per scattare una foto, e successivamente il bottone Scan per eseguire il riconoscimento del modello.\n\n" +
            "Nella schermata Storico Auto è possibile trovare una lista in ordine cronologico della auto analizzate nella sessione corrente " +
            "con la rispettiva accuratezza di riconoscimento. Verrà mostrato per ogni immagine il modello riconsciuto con l'accuratezza più alta";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        IstruzioniViewModel istruzioniViewModel =
                new ViewModelProvider(this).get(IstruzioniViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TextView instructions_textView = binding.instructionTextView;
        instructions_textView.setText(instructions_String);

        final TextView textView = binding.textNotifications;
        istruzioniViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}