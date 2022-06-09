package com.example.vmmr;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.vmmr.databinding.ActivityMainBinding;
import com.example.vmmr.ml.CarsModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "tag";
    private static final int PERMISSION_STATE = 0;
    private static final int CAMERA_REQUEST = 1;
    private ActivityMainBinding binding;
    private int imageSize = 224;

    private Button imgCamera;
    private ImageView imgResult;
    private Button btnPredict;
    private TextView txtPrediction, txtAccuracies;
    private Bitmap bitmap;
    private String hist = String.format(Locale.ITALIAN,"%s : %s%%\n", "Automobile", "Accuratezza ");

    private MySharedViewModel mySharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mySharedViewModel = new ViewModelProvider(this).get(MySharedViewModel.class);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        imgCamera = (Button) findViewById(R.id.camera_button);
        btnPredict = (Button) findViewById(R.id.scan_button);

        imgResult = (ImageView) findViewById(R.id.imageView);
        txtPrediction = (TextView) findViewById(R.id.result_text);
        txtAccuracies = (TextView) findViewById(R.id.accuracies_text);

        btnPredict.setOnClickListener(this::onClick);
        imgResult.setOnClickListener(this::onClick);

    }


    @Override
    protected void onResume() { Log.d(TAG,"onResume()");
        super.onResume();
        btnPredict.setOnClickListener(this::onClick);
        imgCamera.setOnClickListener(this::onClick);
        checkPermissions();
    }

    @Override
    protected void onPause() { Log.d(TAG,"onPause()");
        super.onPause();
        btnPredict.setOnClickListener(null);
        imgCamera.setOnClickListener(null);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.scan_button:
                predict();
                break;
            case R.id.camera_button:
                launchCamera();
                break;
            default:
                break;
        }
    }

    private void launchCamera() {
        Log.d(TAG,"launchCamera()");
        startActivityForResult(new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST);

    }

    private void predict() {
        Log.d(TAG,"predict()");
        if (bitmap == null){
            Toast.makeText( MainActivity.this, "Scattare prima una foto con il tasto Camera", Toast.LENGTH_LONG).show();
        }
        else {
            bitmap = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true);
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            try {
                CarsModel model = CarsModel.newInstance(getApplicationContext());

                // Creates inputs for reference.
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, imageSize, imageSize, 3}, DataType.FLOAT32);

                TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                tensorImage.load(bitmap);
                ByteBuffer byteBuffer = tensorImage.getBuffer();

                //pixels
                int[] intValues = new int[imageSize * imageSize];
                bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                int pixel = 0;
                for (int i = 0; i < imageSize; i++) {
                    for (int j = 0; i < imageSize; i++) {
                        int val = intValues[pixel++]; //RGB extraction
                        byteBuffer.putFloat(((val >> 16) & 0xFF) / 255.f); // / 1.f ?
                        byteBuffer.putFloat(((val >> 8) & 0xFF) / 255.f);
                        byteBuffer.putFloat((val & 0xFF) / 255.f);
                    }
                }


                inputFeature0.loadBuffer(byteBuffer);

                // Runs model inference and gets result.
                CarsModel.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                //write the results
                getMax(outputFeature0.getFloatArray());
                Log.d("Result", Arrays.toString(outputFeature0.getFloatArray()));


                // Releases model resources if no longer used.
                model.close();
            } catch (IOException e) {
                // TODO Handle the exception
            }
        }
    }

    private void getMax(@NonNull float[] confidences) {
        if ( confidences.length != 0){
            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0; i < confidences.length; i++){ //register the most accurate
                if (confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Audi_A3", "Audi_A4", "Audi_A5", "Audi_TT", "BMW_3-Series",
                    "BMW_5-Series", "BMW_X3", "BMW_X6", "FIAT_500", "Ford_Fiesta", "Honda_Civic",
                    "Jeep_Grand Cherokee", "Lexus_IS", "MINI_Cooper", "Mazda_MAZDA3",
                    "Mazda_MX-5 Miata", "Mercedes-Benz_C Class", "Porsche_Cayenne", "Toyota_Corolla",
                    "Toyota_Yaris", "Volkswagen_Beetle", "Volkswagen_Golf", "Volvo_XC90"};

            txtPrediction.setText(classes[maxPos]);
            String s = "";
            for (int i = 0; i < confidences.length; i++){ // write label : accuracy
                s += String.format(Locale.ITALIAN,"%s : %.1f%%\n", classes[i], confidences[i] * 100);
            }
            txtAccuracies.setText(s);
            //add the most accurate result in the public cars history
            String mostAccurate = String.format(Locale.ITALIAN,"%s : %.1f%%\n", classes[maxPos], confidences[maxPos] * 100);
            hist = hist +"|"+mostAccurate;

            mySharedViewModel.setCarsHistory(hist);

        } else return;

    }

    private void checkPermissions() {
        String[] manifestPermissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            manifestPermissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        } else {
            manifestPermissions = new String[] {
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }
        for (String permission : manifestPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG,"Permission Granted " + permission);
            }
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                Log.d(TAG,"Permission Denied " + permission);
                requestPermissions();
            }
        }
    }

    private void requestPermissions() { Log.d(TAG, "requestPermissions()");
        String[] manifestPermissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            manifestPermissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        } else {
            manifestPermissions = new String[] {
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }

        ActivityCompat.requestPermissions(
                this,
                manifestPermissions,
                PERMISSION_STATE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "PermissionsResult requestCode " + requestCode);
        Log.d(TAG, "PermissionsResult permissions " + Arrays.toString(permissions));
        Log.d(TAG, "PermissionsResult grantResults " + Arrays.toString(grantResults));
        if (requestCode == PERMISSION_STATE) {
            for (int grantResult : grantResults) {
                switch (grantResult) {
                    case PackageManager.PERMISSION_GRANTED:
                        Log.d(TAG, "PermissionsResult grantResult Allowed " + grantResult);
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        Log.d(TAG, "PermissionsResult grantResult Denied " + grantResult);
                        break;
                    default:
                        break;
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult requestCode " + requestCode + " resultCode" + resultCode + "data " + data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) { //photo taken
            bitmap = (Bitmap) data.getExtras().get("data");
            imgResult.setImageBitmap(bitmap);
        }
    }
}