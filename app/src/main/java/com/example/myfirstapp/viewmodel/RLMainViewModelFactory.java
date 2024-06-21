package com.example.myfirstapp.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class RLMainViewModelFactory implements ViewModelProvider.Factory {
    private final RLMainRepository mainRepository;

    public RLMainViewModelFactory(RLMainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RLMainViewModel.class)) {
            return (T) new RLMainViewModel(mainRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
