package com.example.vmmr.ui.storicoAuto;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StoricoAutoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public StoricoAutoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Lista delle Auto già riconosciute in questa sessione");
    }

    public LiveData<String> getText() {
        return mText;
    }
}