package com.utravel.app.ui.zxing.camera;

import android.graphics.Bitmap;

import com.google.zxing.LuminanceSource;

/**
 * Created by aaron on 16/7/27.
 * 鑷畾涔夎В鏋怋itmap LuminanceSource
 */
public class BitmapLuminanceSource extends LuminanceSource {

    private byte bitmapPixels[];

    public BitmapLuminanceSource(Bitmap bitmap) {
        super(bitmap.getWidth(), bitmap.getHeight());

        // 棣栧厛锛岃鍙栧緱璇ュ浘鐗囩殑鍍忕礌鏁扮粍鍐呭
        int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
        this.bitmapPixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(data, 0, getWidth(), 0, 0, getWidth(), getHeight());

        // 灏唅nt鏁扮粍杞崲涓篵yte鏁扮粍锛屼篃灏辨槸鍙栧儚绱犲�间腑钃濊壊鍊奸儴鍒嗕綔涓鸿鲸鏋愬唴瀹�
        for (int i = 0; i < data.length; i++) {
            this.bitmapPixels[i] = (byte) data[i];
        }
    }

    @Override
    public byte[] getMatrix() {
        // 杩斿洖鎴戜滑鐢熸垚濂界殑鍍忕礌鏁版嵁
        return bitmapPixels;
    }

    @Override
    public byte[] getRow(int y, byte[] row) {
        // 杩欓噷瑕佸緱鍒版寚瀹氳鐨勫儚绱犳暟鎹�
        System.arraycopy(bitmapPixels, y * getWidth(), row, 0, getWidth());
        return row;
    }
}
