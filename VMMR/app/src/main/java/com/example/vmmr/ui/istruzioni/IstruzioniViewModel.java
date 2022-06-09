package com.example.vmmr.ui.istruzioni;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IstruzioniViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public IstruzioniViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Realizzato da Carlo Gemelli e Robert Shehu");
    }

    public LiveData<String> getText() {
        return mText;
    }
}