package com.example.vmmr;


import androidx.lifecycle.ViewModel;

public class MySharedViewModel extends ViewModel {

    private String carsHistory = "";

    public String getCarsHistory() {
        return carsHistory;
    }

    public void setCarsHistory(String carsHistory) {
            this.carsHistory = carsHistory;
    }


}
