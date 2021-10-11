package com.deepsekharghosh.whiteboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {
//    VIEWS
    SignatureView signatureView;
    SeekBar seekBar;
    ImageButton saver, pickcolor , eraser;
    int default_color;

//    FILE PATH
    private static String filename;
    File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/WhiteBoard");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        DRAWING DEFAULT COLOR
        default_color = ContextCompat.getColor(MainActivity.this,R.color.black);

//        GETTING THEM
        signatureView = findViewById(R.id.signature_view);
        seekBar = findViewById(R.id.seekBar);
        pickcolor = findViewById(R.id.imageButton);
        eraser = findViewById(R.id.imageButton2);
        saver = findViewById(R.id.imageButton3);

//        DEXTER PERMISSION
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }

        }).check();

//        BUTTONS
        pickcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(MainActivity.this, default_color, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {

                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        default_color = color;
                        signatureView.setPenColor(default_color);
                    }
                });
                ambilWarnaDialog.show();
            }
        });

//        PEN SIZE
        seekBar.setMax(50);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                signatureView.setPenSize(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//        ERASER
        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signatureView.clearCanvas();
            }
        });

//        SAVER
        saver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!signatureView.isBitmapEmpty())
                {
                    SimpleDateFormat format = new SimpleDateFormat("yyMMdd_HHmmss", Locale.getDefault());
                    String date = format.format(new Date());
                    filename = path + "/" + date + ".png";

                    if(!path.exists())
                    {
                        path.mkdirs();
                    }
                    File file = new File(filename);

                    Bitmap bitmap = signatureView.getSignatureBitmap();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG,0,bos);
                    byte[] bitmapData = bos.toByteArray();
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                    } catch (FileNotFoundException e) { e.printStackTrace(); }
                    try {fos.write(bitmapData); } catch (IOException e) { e.printStackTrace(); }
                    try { fos.flush(); } catch (IOException e) { e.printStackTrace(); }
                    try { fos.close(); } catch (IOException e) { e.printStackTrace(); }
                    Toast.makeText(MainActivity.this, "saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}