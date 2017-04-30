package com.kaanburaksener.octoUML.src.model;

/**
 * Created by kaanburaksener on 11/04/17.
 */
public class Region {
    private double xStart, yStart, width, height;
    private String name = "";

    public Region(double xStart, double yStart, double width, double height, String name) {
            this.xStart = xStart;
            this.yStart = yStart;
            this.width = width;
            this.height = height;
            this.name = name;
    }

    public double getXStart() {
        return xStart;
    }

    public double getYStart() {
        return yStart;
    }

    public double getXEnd() {
        return xStart + width;
    }

    public double getYEnd() {
        return yStart + height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public String getName() { return name;}
}