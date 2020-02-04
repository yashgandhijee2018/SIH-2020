package com.example.hp.sih2020.ui.gallery;

import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Capture image of the unfamiliar\nvehicle in your parking space");
    }

    public LiveData<String> getText() {
        return mText;
    }


}