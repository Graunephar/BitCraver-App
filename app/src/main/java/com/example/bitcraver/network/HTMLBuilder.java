package com.example.bitcraver.network;

import android.text.Html;
import android.text.Spanned;

import java.io.InputStream;

public class HTMLBuilder {


    public static Spanned StringtoHTML(String source) {

        return Html.fromHtml(source);

    }


    // Inject CSS method: read style.css from assets folder
// Append stylesheet to document head
    public void injectCSS() {
        try {
            InputStream inputStream = contexrgetAssets().open("style.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            webView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
