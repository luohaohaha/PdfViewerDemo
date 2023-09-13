package com.lonelypluto.pdfviewerdemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Project: PdfViewerDemo<br/>
 * Package: com.lonelypluto.pdfviewerdemo.widget<br/>
 * ClassName: NonScrollableGridView<br/>
 * Description: TODO<br/>
 * Date: 2023-09-13 12:24 <br/>
 * <p>
 * Author luohao<br/>
 * Version 1.0<br/>
 * since JDK 1.6<br/>
 * <p>
 */
public class NonScrollableGridView extends GridView {

    public NonScrollableGridView(Context context) {
        super(context);
    }

    public NonScrollableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonScrollableGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Do not use the highest two bits of Integer.MAX_VALUE because they are
        // reserved for the MeasureSpec mode
        int heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightSpec);
        getLayoutParams().height = getMeasuredHeight();
    }
}
