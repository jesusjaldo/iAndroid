package util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

import model.Product;

/**
 * Created by anotauntanto on 27/3/16.
 */
public class Util {

    public static String bitmapToBase64(Bitmap bitmapImage) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;

    }

    public static Bitmap base64ToBitmap (String myImageData) {

        byte[] imageAsBytes = Base64.decode(myImageData.getBytes(), Base64.DEFAULT);
        Bitmap bitmapImage = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        return bitmapImage;

    }

    public static Intent sendMail(Product product, String message){

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{product.getUsername().getUsername()});
        email.putExtra(Intent.EXTRA_SUBJECT, "\"" + product.getNameproduct() + "\"" + " --> iChopping");
        email.putExtra(Intent.EXTRA_TEXT, message + " " + product.getNameproduct() + "\n" + product.getDescription());

        //need this to prompts email client only
        email.setType("message/rfc822");
        return email;
    }
}
