package com.example.filip.cunnyedgetest;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

/**
 * To make a Tool:
 *  1) Subclass Tool implementing 'onStart()', 'onMove()', 'onEnd()', draw with canvas.setBitmap(bitmap)
 *  2) Give the tool a unique ID in the call to the super constructor
 *  3) If the tool has user configurable attributes, implement a subclass of ToolAttributes and
 *     create a corresponding XML layout. Subclass ToolOptionsView and linkup the UI.
 *  4) In the constructor of the tool, set the 'toolAttributes' variable to an instance of ToolAttributes
 *  5) In ToolboxFragment, create an instance of your tool, options, attributes and ImageButton.
 *  6) In initialiseViews() of ToolboxFragment, call tool.setToolAttributes(attributes). Done!
 */
public abstract class Tool implements Command {

    // User interface
    protected final String name;
    protected final Drawable icon;

    // Used to retrieve the tool on config change
    protected final int toolId;

    // Drawing
    protected Canvas canvas = new Canvas();
    protected ToolAttributes toolAttributes;
    protected ToolReport toolReport;
    protected boolean cancelled = false;

    public Tool(String name, Drawable icon, int toolId) {
        this.name = name;
        this.icon = icon;
        this.toolId = toolId;

        toolReport = new ToolReport();
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public int getToolId() {
        return toolId;
    }

    @Override
    public final ToolReport start(Bitmap bitmap, PointF event) {
        cancelled = false;
        toolReport.getPath().reset();
        toolReport.getPath().moveTo(event.x, event.y);
        onStart(bitmap, event);

        return toolReport;
    }

    @Override
    public final ToolReport move(Bitmap bitmap, PointF event) {
        if (!cancelled) {
            toolReport.getPath().lineTo(event.x, event.y);
            onMove(bitmap, event);
            return toolReport;
        }
        return toolReport;
    }

    @Override
    public final ToolReport end(Bitmap bitmap, PointF event) {
        if (!cancelled) {
            toolReport.getPath().lineTo(event.x, event.y);
            onEnd(bitmap, event);
            return toolReport;
        }
        return toolReport;
    }

    @Override
    public final void cancel() {
        cancelled = true;
        toolReport.reset();
    }

    protected abstract void onStart(Bitmap bitmap, PointF event);

    protected abstract void onMove(Bitmap bitmap, PointF event);

    protected abstract  void onEnd(Bitmap bitmap, PointF event);

    protected static boolean isInBounds(Bitmap bitmap, PointF point) {
        if (point.x >= 0 && point.x < bitmap.getWidth()) {
            if (point.y >= 0 && point.y < bitmap.getHeight()) {
                return true;
            }
        }
        return false;
    }

    public ToolAttributes getToolAttributes() {
        return toolAttributes;
    }
}