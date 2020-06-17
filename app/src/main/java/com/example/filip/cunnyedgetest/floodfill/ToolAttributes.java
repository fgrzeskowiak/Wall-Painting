package com.example.filip.cunnyedgetest.floodfill;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by ween on 11/2/14.
 */
public class ToolAttributes {

    public static final int MIN_THICKNESS = 1;

    // Paint with which this tool will draw
    protected Paint paint;

    // This tool modifies the drawing (for example selection tool will not mutate the drawing)
    protected boolean mutator = true;

    // This tool returns a selectable region
    protected boolean selector = false;

    // This tool returns a colour
    protected boolean dropper = false;

    public ToolAttributes() {
        paint = new Paint();
        paint.setAntiAlias(false);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
    }

    public final Paint getPaint() {
        return paint;
    }

    public boolean isMutator() {
        return mutator;
    }

    public void setMutator(boolean mutator) {
        this.mutator = mutator;
    }

    public boolean isSelector() {
        return selector;
    }

    public void setSelector(boolean selectable) {
        this.selector = selectable;
    }

    public boolean isDropper() {
        return dropper;
    }

    public void setDropper(boolean dropper) {
        this.dropper = dropper;
    }

}
