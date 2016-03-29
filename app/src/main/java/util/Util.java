package util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by anotauntanto on 27/3/16.
 */
public class Util {

    public static String bitmapToBase64(Bitmap bitmapImage) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;

    }

    public static Bitmap base64ToBitmap (String myImageData) {

        byte[] imageAsBytes = Base64.decode(myImageData.getBytes(), Base64.DEFAULT);
        Bitmap bitmapImage = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        return bitmapImage;

    }
}
