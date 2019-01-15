package net.lim.model.adv;

import java.net.URL;

/**
 * Created by Limmy on 08.05.2018.
 */
public class Advertisement {
    private String header;
    private String text;
    private URL url;

    public Advertisement(String header) {
        this.header = header;
    }

    public Advertisement(String header, String text, URL url) {
        this.header = header;
        this.text = text;
        this.url = url;
    }

    public Advertisement(String header, String text) {
        this.header = header;
        this.text = text;
    }

    public Advertisement(String header, URL url) {
        this.header = header;
        this.url = url;
    }

    public String getHeader() {
        return header;
    }

    public String getText() {
        return text;
    }

    public URL getUrl() {
        return url;
    }
}
