package com.example.hp.sih2020.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.hp.sih2020.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class UnfamiliarVehicleFragment extends Fragment {
    ImageView img;
    Button detect;
    TextView txt;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;
    private GalleryViewModel galleryViewModel;
    Vibrator v;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_unfamiliar_vehicle, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        img=(ImageView)view.findViewById(R.id.imageView6);
        detect=(Button)view.findViewById(R.id.update_account_settings_btn);
        txt=(TextView)view.findViewById(R.id.textView2);
        v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                txt.setText("");
            }
        });

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
                    FirebaseVisionTextDetector firebaseVisionTextRecognizer = FirebaseVision.getInstance().getVisionTextDetector();
                    firebaseVisionTextRecognizer.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
                            display_text(firebaseVisionText);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                catch (Exception e)
                {
                    Toast.makeText(getActivity(), "No image selected! Take picture of the vehicle to continue...", Toast.LENGTH_LONG).show();
                    vibrate_new();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            img.setImageBitmap(imageBitmap);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void vibrate_new()
    {
        vibrate(this);
    }
    private void display_text(FirebaseVisionText firebaseVisionText)
    {
        List<FirebaseVisionText.Block> blockList=firebaseVisionText.getBlocks();
        if(blockList.size()==0)
        {
            Toast.makeText(getActivity(), "Cannot recognise No. Plate! Please try again...", Toast.LENGTH_SHORT).show();
            vibrate(this);
        }
        else
        {
            for(FirebaseVisionText.Block block: firebaseVisionText.getBlocks())
            {
                String text=block.getText();
                txt.setText(text);
            }
        }
    }

    public static void vibrate(UnfamiliarVehicleFragment fragment) {
        Vibrator vibrator = (Vibrator) fragment.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(300);
    }
}