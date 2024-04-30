package com.example.myfirstapp.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MainViewModelFactory implements ViewModelProvider.Factory {
    private final MainRepository mainRepository;

    public MainViewModelFactory(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(mainRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
