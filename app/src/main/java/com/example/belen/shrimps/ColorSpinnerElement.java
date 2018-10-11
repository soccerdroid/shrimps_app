package com.example.belen.shrimps;

public class ColorSpinnerElement {
    String image_color;
    String class_name;

    public ColorSpinnerElement(String image_color, String class_name) {
        this.image_color = image_color;
        this.class_name = class_name;
    }

    public String getImage_color() {
        return image_color;
    }

    public void setImage_color(String image_color) {
        this.image_color = image_color;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }
}
