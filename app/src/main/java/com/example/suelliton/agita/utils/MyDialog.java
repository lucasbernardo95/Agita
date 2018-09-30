package com.example.suelliton.agita.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.suelliton.agita.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MyDialog extends Dialog {

    private Context context;

    public MyDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public void criaDialogo(String key) {

        LayoutInflater factory = LayoutInflater.from(context);
        final View v = factory.inflate(R.layout.layout_imagem_admin, null);

        final ImageView imgZoom = (ImageView) v.findViewById(R.id.imgEventoZoom);

//        imgZoom.setImageResource(R.drawable.ic_relogio);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("eventos");
        StorageReference islandRef = storageReference.child(key);
        islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imgZoom);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Fechar", null);

        final AlertDialog dialog = builder.create();
        dialog.setView(v);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                Bitmap icon = ((BitmapDrawable)imgZoom.getDrawable()).getBitmap();
                float imageWidthInPX = (float)imgZoom.getWidth();

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
                        Math.round(imageWidthInPX * (float)icon.getHeight() / (float)icon.getWidth()));
                imgZoom.setLayoutParams(layoutParams);


            }
        });
    }
}
