
package com.akshen.bankapp.home;

public class HomeItemHolder {

    private String mContent;

    private String mHeading;

    private int mImageResourceId = NO_IMAGE_PROVIDED;

    public static final int NO_IMAGE_PROVIDED = -1;

    public HomeItemHolder(String defaultTranslation, String miwokTranslation) {
        this(defaultTranslation,miwokTranslation,NO_IMAGE_PROVIDED);
    }

    public HomeItemHolder(String defaultTranslation, String miwokTranslation, int imageResourceId) {
        mHeading = defaultTranslation;
        mContent = miwokTranslation;
        mImageResourceId = imageResourceId;
    }

    public String getContent() {
        return mContent;
    }

    public String getHeading() {
        return mHeading;
    }

    public int getImageResourceId() {
        return mImageResourceId;
    }

    public boolean hasImage() {
        return mImageResourceId != NO_IMAGE_PROVIDED;
    }
}