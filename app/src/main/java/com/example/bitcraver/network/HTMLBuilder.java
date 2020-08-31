package com.example.bitcraver.network;

import android.text.Html;
import android.text.Spanned;

import java.io.InputStream;

public class HTMLBuilder {


    public static Spanned StringtoHTML(String source) {

        return Html.fromHtml(source);

    }

}
