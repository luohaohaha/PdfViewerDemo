package com.artifex.mupdfdemo;

/**
 * Project: PdfViewerDemo<br/>
 * Package: com.artifex.mupdfdemo<br/>
 * ClassName: HitItem<br/>
 * Description: TODO<br/>
 * Date: 2023-09-14 10:11 <br/>
 * <p>
 * Author luohao<br/>
 * Version 1.0<br/>
 * since JDK 1.6<br/>
 * <p>
 */
public class HitItem {
    private Hit hit;
    private Annotation annotation;

    private int page;

    private int  index;

    public HitItem(Hit hit, Annotation annotation, int page, int index) {
        this.hit = hit;
        this.annotation = annotation;
        this.page = page;
        this.index = index;
    }

    public Hit getHit() {
        return hit;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public int getPage() {
        return page;
    }

    public int getIndex() {
        return index;
    }
}
