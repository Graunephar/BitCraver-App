package com.graunephar.bitcraver.network;

import android.text.Html;

public class HTMLBuilder {


    public static String removeUnWantedTags(String source) {

        source = source.replace("loading=\"lazy\"", "");

        source = source.replace("http://", "https://"); //http can be dissallowed on newer phones

        return source;
    }


    public static String StringtoHTML(String content) {

        content = removeUnWantedTags(content);

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
