package com.example.compassandgpscamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA = 123;
    private String mCurrentPhotoPath;
    private ImageView imageCapturedView;
    private TextView locationTextView, AddressText;
    private double currentLatitude;
    private double currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button captureImageButton = findViewById(R.id.btn1);
        Button showLocationButton = findViewById(R.id.btn2);
        imageCapturedView = findViewById(R.id.imgview);
        locationTextView = findViewById(R.id.tv1);
        AddressText = findViewById(R.id.tv2);

        // Request permission for accessing location and camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }

        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,100);
                dispatchTakePictureIntent();
            }
        });

        showLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLocation();
            }
        });

    }

    private void getLocationAndCaptureImage() {
        getLocation();
        dispatchTakePictureIntent();
    }

    // Start the camera intent to capture an image
    public void dispatchTakePictureIntent() {

        Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        if (captureImageIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.compassandgpscamera.provider",
                        photoFile);
                captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(captureImageIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                startActivityForResult(captureImageIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // Create a temporary image file
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Handle the captured image
    // Handle the captured image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK && data != null) {
                Bitmap originalBitmap = (Bitmap) data.getExtras().get("data");

                // Create a mutable copy of the original bitmap
                Bitmap mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                // Draw latitude and longitude lines on the mutable bitmap
                mutableBitmap = drawLatLongLines(mutableBitmap, currentLatitude, currentLongitude);
                // Display the modified bitmap with latitude and longitude lines
                imageCapturedView.setImageBitmap(mutableBitmap);
            }
        }
    }


    /*
        private void setPic(String imageFilePath) {
            // Decode the image file into a bitmap
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);

            // Draw latitude and longitude lines on the bitmap
            Bitmap mutableBitmap = drawLatLongLines(bitmap);

            // If the bitmap is not null, set it to the image view
            if (mutableBitmap != null) {
                imageCapturedView.setImageBitmap(mutableBitmap);
            } else {
                // Handle the case when the bitmap is null
                Toast.makeText(getApplicationContext(), "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }*/
// Method to draw latitude and longitude lines on the bitmap
// Method to draw latitude and longitude lines on the bitmap
    // Method to draw latitude and longitude lines on the bitmap
    // Method to draw latitude and longitude lines on the bitmap
    // Method to draw latitude and longitude lines on the bitmap
    private Bitmap drawLatLongLines(Bitmap bitmap, double latitude, double longitude) {
        if (bitmap == null) return null;

        // Define paint properties for drawing lines and text
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(18); // Set text size

        // Create a canvas from the bitmap to draw on it
        Canvas canvas = new Canvas(bitmap);

        // Draw latitude and longitude values
        String latitudeText = "Latitude: " + String.format(Locale.getDefault(), "%.4f", latitude);
        String longitudeText = "Longitude: " + String.format(Locale.getDefault(), "%.4f", longitude);
        canvas.drawText(latitudeText, 10, 30, paint);
        canvas.drawText(longitudeText, 10, 60, paint);

        return bitmap;
    }





    // Get the current location
    // Get the current location
    private void getLocation() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(MainActivity.this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                currentLatitude = location.getLatitude();
                                currentLongitude = location.getLongitude();
                                String currentLocation = "Latitude: " + currentLatitude + ", Longitude: " + currentLongitude;
                                locationTextView.setText(currentLocation);

                                // Logging the retrieved latitude and longitude
                                Log.d("Location", "Latitude: " + currentLatitude + ", Longitude: " + currentLongitude);

                                // Get address from latitude and longitude
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
                                    if (addresses != null && addresses.size() > 0) {
                                        Address address = addresses.get(0);
                                        String addressString = "Address: " + address.getAddressLine(0);
                                        // Display address on map (you may implement this part according to your map implementation)
                                        AddressText.setText(addressString);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                locationTextView.setText("Location not available");
                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CAMERA);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndCaptureImage();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }



}