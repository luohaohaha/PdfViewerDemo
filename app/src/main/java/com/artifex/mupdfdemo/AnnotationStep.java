package com.artifex.mupdfdemo;

import android.graphics.PointF;

/**
 * Project: PdfViewerDemo<br/>
 * Package: com.artifex.mupdfdemo<br/>
 * ClassName: AnnotationStep<br/>
 * Description: TODO<br/>
 * Date: 2023-09-19 15:35 <br/>
 * <p>
 * Author luohao<br/>
 * Version 1.0<br/>
 * since JDK 1.6<br/>
 * <p>
 */
public class AnnotationStep {
    private int page;
    private PointF[] quadPoints ;

    private  PointF[][] arcs;
    private Annotation.Type type;

    private int color;

    private float inkThickness;

    public AnnotationStep(int page, PointF[] quadPoints, PointF[][] arcs, Annotation.Type type, int color, float inkThickness) {
        this.page = page;
        this.quadPoints = quadPoints;
        this.arcs = arcs;
        this.type = type;
        this.color = color;
        this.inkThickness = inkThickness;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public PointF[] getQuadPoints() {
        return quadPoints;
    }

    public void setQuadPoints(PointF[] quadPoints) {
        this.quadPoints = quadPoints;
    }

    public PointF[][] getArcs() {
        return arcs;
    }

    public void setArcs(PointF[][] arcs) {
        this.arcs = arcs;
    }

    public Annotation.Type getType() {
        return type;
    }

    public void setType(Annotation.Type type) {
        this.type = type;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getInkThickness() {
        return inkThickness;
    }

    public void setInkThickness(float inkThickness) {
        this.inkThickness = inkThickness;
    }
}
