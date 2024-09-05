package me.ykrank.s1next.viewmodel;

public final class ImageViewModel {

    public final String url;

    public final String thumbUrl;

    public ImageViewModel(String url) {
        this.url = url;
        this.thumbUrl = null;
    }

    public ImageViewModel(String url, String thumbUrl) {
        this.url = url;
        this.thumbUrl = thumbUrl;
    }
}
