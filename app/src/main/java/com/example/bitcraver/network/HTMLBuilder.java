package com.example.bitcraver.network;

import android.text.Html;

public class HTMLBuilder {


    public static String StringtoHTML(String content) {

        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "<meta charset=\"utf-8\"/>" +
                "</head>"+
                "<body>" +
                "<div class='content-wrapper'>" +
                    content +
                "</div>" +
                "</body>" +
                "</html>";
    }

}
