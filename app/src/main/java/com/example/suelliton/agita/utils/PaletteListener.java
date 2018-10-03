package com.example.suelliton.agita.utils;

import android.support.v7.graphics.Palette;

public class PaletteListener implements Palette.PaletteAsyncListener{

    @Override
    public void onGenerated(Palette palette){

        // Pega duas cores predominantes, uma vibrante e uma "muda"(cor com tom cinza) e
        //separa elas em três categorias: normal, leve e escuro.
        int vibrant = palette.getVibrantColor(0x000000);
        int vibrantLight = palette.getLightVibrantColor(0x000000);
        int vibrantDark = palette.getDarkVibrantColor(0x000000);
        int muted = palette.getMutedColor(0x000000);
        int mutedLight = palette.getLightMutedColor(0x000000);
        int mutedDark = palette.getDarkMutedColor(0x000000);
    }
}