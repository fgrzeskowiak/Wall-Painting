package com.example.filip.cunnyedgetest;

import android.graphics.Path;

/**
 * Created by ween on 11/14/14.
 */
public class ToolReport {

    // The path taken over the lifecycle of the this drawing operation
    private Path path = new Path();

    // The inverse of the path
    private Path inversePath = new Path();

    // The colour to be returned
    protected int dropColour = 0;

    public void reset() {
        path.reset();
        inversePath.reset();;
        dropColour = 0;
    }

    public Path getPath() {
        return path;
    }

    public Path getInversePath() {
        return inversePath;
    }

    public int getDropColour() {
        return dropColour;
    }

    public void setDropColour(int dropColour) {
        this.dropColour = dropColour;
    }
}
