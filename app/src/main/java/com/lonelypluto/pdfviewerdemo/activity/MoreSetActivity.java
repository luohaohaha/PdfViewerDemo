package com.lonelypluto.pdfviewerdemo.activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdfdemo.Annotation;
import com.artifex.mupdfdemo.HitItem;
import com.artifex.mupdfdemo.MuPDFAlert;
import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.MuPDFPageAdapter;
import com.artifex.mupdfdemo.MuPDFReaderView;
import com.artifex.mupdfdemo.MuPDFReaderViewListener;
import com.artifex.mupdfdemo.MuPDFView;
import com.artifex.mupdfdemo.OutlineActivityData;
import com.artifex.mupdfdemo.PageView;
import com.artifex.mupdfdemo.ReaderView;
import com.artifex.mupdfdemo.SearchTaskResult;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lonelypluto.pdflibrary.constants.CommConsts;
import com.lonelypluto.pdflibrary.utils.CommTools;
import com.lonelypluto.pdflibrary.utils.FileHelper;
import com.lonelypluto.pdflibrary.utils.SharedPreferencesUtil;
import com.lonelypluto.pdfviewerdemo.R;
import com.sa90.materialarcmenu.ArcMenu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @Description: 一些主要方法的设置，在已有功能的基础上增加了一些动态设置参数的方法
 * @author: ZhangYW
 * @time: 2019/3/11 15:56
 */
public class MoreSetActivity extends AppCompatActivity {
    private static final String TAG = MoreSetActivity.class.getSimpleName();
    private final int OUTLINE_REQUEST = 0;// 目录回调
    private String filePath = Environment.getExternalStorageDirectory() + "/android-programming-for-beginners.pdf"; // 文件路径

    private AlertDialog.Builder mAlertBuilder;// 弹出框

    private MuPDFCore muPDFCore;// 加载mupdf.so文件
    private MuPDFReaderView muPDFReaderView;// 显示pdf的view

    private boolean mAlertsActive = false;
    private AsyncTask<Void, Void, MuPDFAlert> mAlertTask;
    private AlertDialog mAlertDialog;// 初始加载pdf等待弹出框

    // tools
    /*private ViewAnimator mTopBarSwitcher;// 工具栏动画
    private ImageButton mLinkButton;// 超链接
    private ImageButton mOutlineButton;// 目录
    private ImageButton mSearchButton;// 搜索
    private ImageButton mAnnotButton;// 注释
    // tools 搜索框
    private EditText et_searchText;// 搜索内容输入框
    private ImageButton mSearchBack;// 搜索内容上一个
    private ImageButton mSearchFwd;// 搜索内容下一个
    // tools 注释类型
    private TextView mAnnotTypeText;// 注释类型
    // tools 底部布局

    private int mPageSliderRes;// 拖动条的个数
    private boolean mButtonsVisible;// 是否显示工具栏
    private TopBarMode mTopBarMode = TopBarMode.Main;// 工具栏类型
    private AcceptMode mAcceptMode;// 工具栏注释类型

    private SearchTask mSearchTask;// 搜索线程
    private boolean mLinkHighlight = false;// 是否高亮显示*/

  /*  private Button btn_change_hv;// 切换横竖显示
    private boolean ischangeHV = false;// 横竖切换
    private Button btn_linkhighlightcolor;// 设置超链接颜色
    private Button btn_searchtextcolor;// 设置搜索文字颜色
    private Button btn_paintcolor;// 设置画笔颜色
    private Button btn_paintstrokewidth;// 设置画笔粗细*/

    private AcceptMode mAcceptMode;// 工具栏注释类型

    private ArcMenu mExpandMenuContainer;
    private FloatingActionButton mFabColorPalette, mPainDraw, mFabHighlight, mFabUnderLine, mFabStrikeOut;

    private int mSelectColorPosition = 0;
    private String mSelectColor = "";

    private Toolbar mToolbar;

    private int maxScreenshot = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_set);
        Uri data = getIntent().getData();
        if (null != data) {
            filePath = data.getPath();
        }
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        SharedPreferencesUtil.init(getApplication());

        muPDFReaderView = (MuPDFReaderView) findViewById(R.id.mupdfreaderview);

//        initToolsView();
        createPDF();
//        setPDFVoid();
        initFabMenus();
    }

    private void initFabMenus() {
        mExpandMenuContainer = findViewById(R.id.expand_menu_container);
        mFabColorPalette = findViewById(R.id.menu_color_palette);
        mPainDraw = findViewById(R.id.menu_draw);
        mFabHighlight = findViewById(R.id.menu_highlight);
        mFabUnderLine = findViewById(R.id.menu_under_line);
        mFabStrikeOut = findViewById(R.id.menu_strike_out);
        mToolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().hide();
        mFabColorPalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPaletteDialog();
            }
        });
        mPainDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnInkButtonClick(v);
            }
        });
        mFabHighlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnHighlightButtonClick(v);
            }
        });
        mFabUnderLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnUnderlineButtonClick(v);
            }
        });
        mFabStrikeOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnStrikeOutButtonClick(v);
            }
        });

        muPDFReaderView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction() || MotionEvent.ACTION_CANCEL == event.getAction()) {
                    MuPDFReaderView.Mode mode = muPDFReaderView.getMode();
                    OnAcceptButtonClick(v, false);
//                    return mode == MuPDFReaderView.Mode.Selecting || mode == MuPDFReaderView.Mode.Drawing;
                }
                return false;
            }
        });
        findViewById(R.id.menu_screen_shot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenShot();
            }
        });
        mSelectColor = CommConsts.COLOR_PALETTE_LIST.get(mSelectColorPosition);
        updateColor();
    }

    /*private void setPDFVoid(){
        //切换横竖显示
        btn_change_hv = (Button)findViewById(R.id.btn_change_hv);
        btn_change_hv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ischangeHV) {
                    muPDFReaderView.setHorizontalScrolling(ischangeHV);
                    btn_change_hv.setText("横");
                    ischangeHV = false;
                } else {
                    muPDFReaderView.setHorizontalScrolling(ischangeHV);
                    btn_change_hv.setText("竖");
                    ischangeHV = true;
                }
            }
        });

        // 改变超链接颜色
        btn_linkhighlightcolor = (Button)findViewById(R.id.btn_linkhighlightcolor);
        btn_linkhighlightcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLinkHighlightColor(ContextCompat.getColor(MoreSetActivity.this, com.lonelypluto.pdfviewerdemo.R.color.link_bg));
            }
        });

        // 改变搜索文字颜色
        btn_searchtextcolor = (Button)findViewById(R.id.btn_searchtextcolor);
        btn_searchtextcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchTextColor(ContextCompat.getColor(MoreSetActivity.this, com.lonelypluto.pdfviewerdemo.R.color.search_bg));
            }
        });

        // 设置画笔颜色
        btn_paintcolor = (Button)findViewById(R.id.btn_set_paint_color);
        btn_paintcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c = ContextCompat.getColor(MoreSetActivity.this, com.lonelypluto.pdfviewerdemo.R.color.rv_item_line_bg);
                setInkColor(0xFF0000FF);
            }
        });

        // 设置画笔粗细
        btn_paintstrokewidth = (Button)findViewById(R.id.btn_set_paint_strokewidth);
        btn_paintstrokewidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPaintStrockWidth(20.0f);
            }
        });
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tool_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_undo:
                MuPDFView pageView = (MuPDFView) muPDFReaderView.getDisplayedView();
                if (null != pageView) {
                    pageView.undo();
                }
                return true;
            case R.id.edit_redo:
                pageView = (MuPDFView) muPDFReaderView.getDisplayedView();
                if (null != pageView) {
                    pageView.redo();
                }
                return true;
            case R.id.edit_complete:
                if (getSupportActionBar().isShowing()) {
                    getSupportActionBar().hide();
                }
                pageView = (MuPDFView) muPDFReaderView.getDisplayedView();
                if (null != pageView) {
                    pageView.complete();
                }
                OnAcceptButtonClick(item.getActionView(), true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化工具栏
     */
   /* private void initToolsView() {

        mTopBarSwitcher = (ViewAnimator) findViewById(R.id.switcher);
        mLinkButton = (ImageButton) findViewById(R.id.linkButton);
        mAnnotButton = (ImageButton) findViewById(R.id.reflowButton);
        mOutlineButton = (ImageButton) findViewById(R.id.outlineButton);
        mSearchButton = (ImageButton) findViewById(R.id.searchButton);

        et_searchText = (EditText) findViewById(R.id.searchText);
        mSearchBack = (ImageButton) findViewById(R.id.searchBack);
        mSearchFwd = (ImageButton) findViewById(R.id.searchForward);

        mAnnotTypeText = (TextView) findViewById(R.id.annotType);


        mTopBarSwitcher.setVisibility(View.INVISIBLE);
    }*/
    private void createPDF() {
        mAlertBuilder = new AlertDialog.Builder(this);

        // 通过MuPDFCore打开pdf文件
        muPDFCore = openFile(getIntent().getData());
        // 搜索设为空
        SearchTaskResult.set(null);
        // 判断如果core为空，提示不能打开文件
        if (muPDFCore == null) {
            AlertDialog alert = mAlertBuilder.create();
            alert.setTitle(com.lonelypluto.pdfviewerdemo.R.string.cannot_open_document);
            alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(com.lonelypluto.pdfviewerdemo.R.string.dismiss),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
            alert.show();
            return;
        }
        // 显示
        muPDFReaderView.setAdapter(new MuPDFPageAdapter(this, muPDFCore));
        // Set up the page slider
        int smax = Math.max(muPDFCore.countPages() - 1, 1);
//        mPageSliderRes = ((10 + smax - 1) / smax) * 2;

        // 创建搜索任务
        /*mSearchTask = new SearchTask(this, muPDFCore) {
            @Override
            protected void onTextFound(SearchTaskResult result) {
                SearchTaskResult.set(result);
                // Ask the ReaderView to move to the resulting page
                muPDFReaderView.setDisplayedViewIndex(result.pageNumber);
                // Make the ReaderView act on the change to SearchTaskResult
                // via overridden onChildSetup method.
                muPDFReaderView.resetupChildren();
            }
        };

        // Search invoking buttons are disabled while there is no text specified
        mSearchBack.setEnabled(false);
        mSearchFwd.setEnabled(false);
        mSearchBack.setColorFilter(Color.argb(0xFF, 250, 250, 250));
        mSearchFwd.setColorFilter(Color.argb(0xFF, 250, 250, 250));

        // 判断如果pdf文件有目录
        if (muPDFCore.hasOutline()) {
            // 点击目录按钮跳转到目录页
            mOutlineButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    OutlineItem outline[] = muPDFCore.getOutline();
                    if (outline != null) {
                        OutlineActivityData.get().items = outline;
                        Intent intent = new Intent(MoreSetActivity.this, OutlineActivity.class);
                        startActivityForResult(intent, OUTLINE_REQUEST);
                    }
                }
            });
        } else {
            mOutlineButton.setVisibility(View.GONE);
        }*/

        // 设置监听事件
        setListener();
    }

    private void screenShot() {
        Executors.newCachedThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                View view = muPDFReaderView.getDisplayedView();
                Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight() * maxScreenshot, Bitmap.Config.RGB_565);
                for (int i = 0; i < maxScreenshot; i++) {
                    view = muPDFReaderView.getDisplayedView();
                    Canvas canvas = new Canvas(bitmap);
                    view.setDrawingCacheEnabled(true);
                    canvas.drawBitmap(view.getDrawingCache(), 0, i * view.getHeight(), null);
                    view.setDrawingCacheEnabled(false);
                    muPDFReaderView.moveToNext();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    String title = System.currentTimeMillis() + "-screenshot.png";
                    String path = getExternalCacheDir().getAbsoluteFile() + "/" + title;
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(path));
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    CommTools.saveToGallery(MoreSetActivity.this, path, title, title);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 打开文件
     *
     * @param path 文件路径
     * @return
     */
    private MuPDFCore openFile(Uri path) {

        Log.e(TAG, "Trying to open " + path);
        try {
            Intent intent = getIntent();
            if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                Uri uri = intent.getData();
                String mimetype = getIntent().getType();

                if (uri == null) {
                    return null;
                }

                String mDocKey = uri.toString();

                Log.i(TAG, "OPEN URI " + uri.toString());
                Log.i(TAG, "  MAGIC (Intent) " + mimetype);

                String mDocTitle = null;
                long size = -1;
                Cursor cursor = null;

                try {
                    cursor = getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int idx;

                        idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (idx >= 0 && cursor.getType(idx) == Cursor.FIELD_TYPE_STRING)
                            mDocTitle = cursor.getString(idx);

                        idx = cursor.getColumnIndex(OpenableColumns.SIZE);
                        if (idx >= 0 && cursor.getType(idx) == Cursor.FIELD_TYPE_INTEGER)
                            size = cursor.getLong(idx);

                        if (size == 0)
                            size = -1;
                    }
                } catch (Exception x) {
                    // Ignore any exception and depend on default values for title
                    // and size (unless one was decoded
                } finally {
                    if (cursor != null)
                        cursor.close();
                }

                Log.i(TAG, "  NAME " + mDocTitle);
                Log.i(TAG, "  SIZE " + size);

                if (mimetype == null || mimetype.equals("application/octet-stream")) {
                    mimetype = getContentResolver().getType(uri);
                    Log.i(TAG, "  MAGIC (Resolved) " + mimetype);
                }
                if (mimetype == null || mimetype.equals("application/octet-stream")) {
                    mimetype = mDocTitle;
                    Log.i(TAG, "  MAGIC (Filename) " + mimetype);
                }

                try {
                    muPDFCore = new MuPDFCore(this, FileHelper.getRealPathFromURI(this,path));
                    SearchTaskResult.set(null);
                } catch (Exception x) {
                    return null;
                }
            }
//            muPDFCore = new MuPDFCore(this, path);
            // 新建：删除旧的目录数据
            OutlineActivityData.set(null);
        } catch (Exception e) {
            Log.e(TAG, "openFile catch:" + e.toString());
            return null;
        } catch (OutOfMemoryError e) {
            //  out of memory is not an Exception, so we catch it separately.
            Log.e(TAG, "openFile catch: OutOfMemoryError " + e.toString());
            return null;
        }
        return muPDFCore;
    }

    private MuPDFCore openCore(Uri uri, long size, String mimetype) throws IOException {
        ContentResolver cr = getContentResolver();

        Log.i(TAG, "Opening document " + uri);

        InputStream is = cr.openInputStream(uri);
        byte[] buf = null;
        int used = -1;
        try {
            final int limit = 8 * 1024 * 1024;
            if (size < 0) { // size is unknown
                buf = new byte[limit];
                used = is.read(buf);
                boolean atEOF = is.read() == -1;
                if (used < 0 || (used == limit && !atEOF)) // no or partial data
                    buf = null;
            } else if (size <= limit) { // size is known and below limit
                buf = new byte[(int) size];
                used = is.read(buf);
                if (used < 0 || used < size) // no or partial data
                    buf = null;
            }
            if (buf != null && buf.length != used) {
                byte[] newbuf = new byte[used];
                System.arraycopy(buf, 0, newbuf, 0, used);
                buf = newbuf;
            }
        } catch (OutOfMemoryError e) {
            buf = null;
        } finally {
            is.close();
        }

        if (buf != null) {
            Log.i(TAG, "  Opening document from memory buffer of size " + buf.length);
            return openBuffer(buf, mimetype);
        } else {
            Log.i(TAG, "  Opening document from stream");
            return null;
        }
    }

    private MuPDFCore openBuffer(byte buffer[], String magic) {
        try {
            muPDFCore = new MuPDFCore(this, buffer, magic);
        } catch (Exception e) {
            Log.e(TAG, "Error opening document buffer: " + e);
            return null;
        }
        return muPDFCore;
    }

    /**
     * 设置监听事件
     */
    private void setListener() {
        // 设置MuPDFReaderView的监听事件
        setMuPDFReaderViewListener();
        // 设置页面拖动条监听事件
//        mPageSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                muPDFReaderView.setDisplayedViewIndex((seekBar.getProgress() + mPageSliderRes / 2) / mPageSliderRes);
//            }
//
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            public void onProgressChanged(SeekBar seekBar, int progress,
//                                          boolean fromUser) {
//                updatePageNumView((progress + mPageSliderRes / 2) / mPageSliderRes);
//            }
//        });
        // 搜索按钮
     /*   mSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                searchModeOn();
            }
        });
        // 注释按钮
        if (muPDFCore.fileFormat().startsWith("PDF") && muPDFCore.isUnencryptedPDF() && !muPDFCore.wasOpenedFromBuffer()) {
            mAnnotButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mTopBarMode = TopBarMode.Annot;
                    mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
                }
            });
        } else {
            mAnnotButton.setVisibility(View.GONE);
        }
        // 搜索的输入框监听事件
        et_searchText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                boolean haveText = s.toString().length() > 0;
                setButtonEnabled(mSearchBack, haveText);
                setButtonEnabled(mSearchFwd, haveText);

                // Remove any previous search results
                if (SearchTaskResult.get() != null && !et_searchText.getText().toString().equals(SearchTaskResult.get().txt)) {
                    SearchTaskResult.set(null);
                    muPDFReaderView.resetupChildren();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });

        //React to Done button on keyboard
        et_searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    search(1);
                return false;
            }
        });

        et_searchText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
                    search(1);
                return false;
            }
        });

        // Activate search invoking buttons
        mSearchBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search(-1);
            }
        });
        mSearchFwd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search(1);
            }
        });

        mLinkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setLinkHighlight(!mLinkHighlight);
            }
        });*/
    }

    /**
     * 设置MuPDFReaderView的监听事件
     */
    private void setMuPDFReaderViewListener() {
        muPDFReaderView.setListener(new MuPDFReaderViewListener() {
            @Override
            public void onMoveToChild(int i) {
                if (muPDFCore == null) {
                    return;
                }
//                mPageNumberView.setText(String.format("%d / %d", i + 1,
//                        muPDFCore.countPages()));
//                mPageSlider.setMax((muPDFCore.countPages() - 1) * mPageSliderRes);
//                mPageSlider.setProgress(i * mPageSliderRes);
                updateColor();
            }

            @Override
            public void onTapMainDocArea() {
              /*  if (!mButtonsVisible) {
                    showButtons();
                } else {
                    if (mTopBarMode == TopBarMode.Main)
                        hideButtons();
                }*/
            }

            @Override
            public void onDocMotion() {
//                hideButtons();
            }

            @Override
            public void onHit(HitItem item) {
                /*switch (mTopBarMode) {
                    case Annot:
                        if (item == Hit.Annotation) {
                            showButtons();
                            mTopBarMode = TopBarMode.Delete;
                            mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
                        }
                        break;
                    case Delete:
                        mTopBarMode = TopBarMode.Annot;
                        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
                        // fall through
                    default:
                        // Not in annotation editing mode, but the pageview will
                        // still select and highlight hit annotations, so
                        // deselect just in case.
                        MuPDFView pageView = (MuPDFView) muPDFReaderView.getDisplayedView();
                        if (pageView != null) {
                            pageView.deselectAnnotation();
                        }
                        break;
                }*/
                showDeleteAnnotationPopup(item.getAnnotation());
            }
        });
    }

    private void showDeleteAnnotationPopup(Annotation annotation) {
        if (null == annotation) {
            Log.d(TAG, " annotation is null ");
            return;
        }
        View mDelete = View.inflate(this, R.layout.view_delete, null);
        PopupWindow mPopupWindow = new PopupWindow(mDelete, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mDelete.setOnClickListener(v -> {
            OnDeleteButtonClick(v);
            if (null != mPopupWindow && mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
        });
        View pageView = muPDFReaderView.getCurrentView();
        float scale = muPDFReaderView.getCurrentScale();
        int left = (int) (pageView.getLeft() + annotation.left * scale + (annotation.right * scale - annotation.left * scale) / 2 - 100);
        int top = (int) (pageView.getTop() + annotation.top * scale);
        mPopupWindow.showAtLocation(pageView, Gravity.LEFT | Gravity.TOP, left, top);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 目录回调
            case OUTLINE_REQUEST:
                if (resultCode >= 0)
                    muPDFReaderView.setDisplayedViewIndex(resultCode);
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置超链接颜色
     *
     * @param color 颜色值
     */
    private void setLinkHighlightColor(int color) {
        muPDFReaderView.setLinkHighlightColor(color);
    }

    /**
     * 设置搜索文字颜色
     *
     * @param color 颜色值
     */
    private void setSearchTextColor(int color) {
        muPDFReaderView.setSearchTextColor(color);
    }

    /**
     * 设置画笔颜色
     *
     * @param color 颜色值
     */
    private void setInkColor(int color) {
        muPDFReaderView.setInkColor(color);
    }

    /**
     * 设置画笔粗细
     *
     * @param inkThickness 粗细值
     */
    private void setPaintStrockWidth(float inkThickness) {
        muPDFReaderView.setPaintStrockWidth(inkThickness);
    }

    /**
     * 显示工具栏
     */
  /*  private void showButtons() {
        if (muPDFCore == null)
            return;
        if (!mButtonsVisible) {
            mButtonsVisible = true;
            // Update page number text and slider
            int index = muPDFReaderView.getDisplayedViewIndex();
            updatePageNumView(index);
//            mPageSlider.setMax((muPDFCore.countPages() - 1) * mPageSliderRes);
//            mPageSlider.setProgress(index * mPageSliderRes);
            if (mTopBarMode == TopBarMode.Search) {
                et_searchText.requestFocus();
                showKeyboard();
            }

            Animation anim = new TranslateAnimation(0, 0, -mTopBarSwitcher.getHeight(), 0);
            anim.setDuration(200);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    mTopBarSwitcher.setVisibility(View.VISIBLE);
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                }
            });
            mTopBarSwitcher.startAnimation(anim);

           *//* anim = new TranslateAnimation(0, 0, mPageSlider.getHeight(), 0);
            anim.setDuration(200);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    mPageSlider.setVisibility(View.VISIBLE);
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    mPageNumberView.setVisibility(View.VISIBLE);
                }
            });
            mPageSlider.startAnimation(anim);*//*
        }
    }

    *//**
     * 隐藏工具栏
     *//*
    private void hideButtons() {
        if (mButtonsVisible) {
            mButtonsVisible = false;
            hideKeyboard();

            Animation anim = new TranslateAnimation(0, 0, 0, -mTopBarSwitcher.getHeight());
            anim.setDuration(200);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    mTopBarSwitcher.setVisibility(View.INVISIBLE);
                }
            });
            mTopBarSwitcher.startAnimation(anim);

            *//*anim = new TranslateAnimation(0, 0, 0, mPageSlider.getHeight());
            anim.setDuration(200);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    mPageNumberView.setVisibility(View.INVISIBLE);
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    mPageSlider.setVisibility(View.INVISIBLE);
                }
            });
            mPageSlider.startAnimation(anim);*//*
        }
    }*/

    /**
     * 更新当前是第多少页
     *
     * @param index
     */
    private void updatePageNumView(int index) {
        if (muPDFCore == null)
            return;
//        mPageNumberView.setText(String.format("%d / %d", index + 1, muPDFCore.countPages()));
    }

    /**
     * 显示键盘
     */
  /*  private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.showSoftInput(et_searchText, 0);
    }*/

    /**
     * 隐藏键盘
     */
   /* private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(et_searchText.getWindowToken(), 0);
    }*/

    /**
     * 工具栏 - 注释点击事件
     *
     * @param v
     */
    public void OnEditAnnotButtonClick(View v) {
       /* mTopBarMode = TopBarMode.Main;
        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());*/
    }

    /**
     * 工具栏 - 复制点击事件
     *
     * @param v
     */
    public void OnCopyTextButtonClick(View v) {
        mAcceptMode = AcceptMode.CopyText;
     /*   mTopBarMode = TopBarMode.Accept;
        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
        muPDFReaderView.setMode(MuPDFReaderView.Mode.Selecting);
        mAnnotTypeText.setText(getString(com.lonelypluto.pdfviewerdemo.R.string.copy_text));
        showInfo(getString(com.lonelypluto.pdfviewerdemo.R.string.select_text));*/
    }

    /**
     * 工具栏 - 搜索框取消点击事件
     *
     * @param v
     */
    public void OnCancelSearchButtonClick(View v) {
        searchModeOff();
    }

    /**
     * 工具栏 - 注释取消点击事件
     *
     * @param v
     */
    public void OnCancelMoreButtonClick(View v) {
      /*  mTopBarMode = TopBarMode.Main;
        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());*/
    }

    /**
     * 开始搜索
     */
    private void searchModeOn() {
       /* if (mTopBarMode != TopBarMode.Search) {
            mTopBarMode = TopBarMode.Search;
            //Focus on EditTextWidget
            et_searchText.requestFocus();
            showKeyboard();
            mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
        }*/
    }

    /**
     * 取消搜索
     */
    private void searchModeOff() {
      /*  if (mTopBarMode == TopBarMode.Search) {
            mTopBarMode = TopBarMode.Main;
            hideKeyboard();
            mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
            SearchTaskResult.set(null);
            // Make the ReaderView act on the change to mSearchTaskResult
            // via overridden onChildSetup method.
            muPDFReaderView.resetupChildren();
        }*/
    }

    /**
     * 工具栏 - 注释 - 高亮点击事件
     *
     * @param v
     */
    public void OnHighlightButtonClick(View v) {
        mAcceptMode = AcceptMode.Highlight;
//        mTopBarMode = TopBarMode.Accept;
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
        muPDFReaderView.setMode(MuPDFReaderView.Mode.Selecting);
//        mAnnotTypeText.setText(com.lonelypluto.pdfviewerdemo.R.string.pdf_tools_highlight);
        showInfo(getString(com.lonelypluto.pdfviewerdemo.R.string.select_text));
    }

    /**
     * 工具栏 - 注释 - 底部画线点击事件
     *
     * @param v
     */
    public void OnUnderlineButtonClick(View v) {
//        mTopBarMode = TopBarMode.Accept;
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
        mAcceptMode = AcceptMode.Underline;
        muPDFReaderView.setMode(MuPDFReaderView.Mode.Selecting);
//        mAnnotTypeText.setText(com.lonelypluto.pdfviewerdemo.R.string.pdf_tools_underline);
        updateColor();
        showInfo(getString(com.lonelypluto.pdfviewerdemo.R.string.select_text));
    }

    private void updateColor() {
        int color = Color.parseColor(mSelectColor);
        setLinkHighlightColor(color);
        setInkColor(color);
    }

    /**
     * 工具栏 - 注释 - 废弃线点击事件
     *
     * @param v
     */
    public void OnStrikeOutButtonClick(View v) {
//        mTopBarMode = TopBarMode.Accept;
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
        mAcceptMode = AcceptMode.StrikeOut;
        muPDFReaderView.setMode(MuPDFReaderView.Mode.Selecting);
//        mAnnotTypeText.setText(com.lonelypluto.pdfviewerdemo.R.string.pdf_tools_strike_out);
        showInfo(getString(com.lonelypluto.pdfviewerdemo.R.string.select_text));
    }

    /**
     * 工具栏 - 注释 - 签字点击事件
     *
     * @param v
     */
    public void OnInkButtonClick(View v) {
//        mTopBarMode = TopBarMode.Accept;
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
        mAcceptMode = AcceptMode.Ink;
        muPDFReaderView.setMode(MuPDFReaderView.Mode.Drawing);
//        mAnnotTypeText.setText(com.lonelypluto.pdfviewerdemo.R.string.pdf_tools_ink);
        showInfo(getString(com.lonelypluto.pdfviewerdemo.R.string.pdf_tools_draw_annotation));
    }

    /**
     * 工具栏 - 注释 - 删除注释点击事件
     *
     * @param v
     */
    public void OnDeleteButtonClick(View v) {
        MuPDFView pageView = (MuPDFView) muPDFReaderView.getDisplayedView();
        if (pageView != null)
            pageView.deleteSelectedAnnotation();
//        mTopBarMode = TopBarMode.Annot;
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
    }

    /**
     * 工具栏 - 注释 - 取消删除注释点击事件
     *
     * @param v
     */
    public void OnCancelDeleteButtonClick(View v) {
        MuPDFView pageView = (MuPDFView) muPDFReaderView.getDisplayedView();
        if (pageView != null)
            pageView.deselectAnnotation();
//        mTopBarMode = TopBarMode.Annot;
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
    }

    /**
     * 工具栏 - 注释 - 取消点击事件
     *
     * @param v
     */
    public void OnCancelAcceptButtonClick(View v) {
        MuPDFView pageView = (MuPDFView) muPDFReaderView.getDisplayedView();
        if (pageView != null) {
            pageView.deselectText();
            pageView.cancelDraw();
        }
        muPDFReaderView.setMode(MuPDFReaderView.Mode.Viewing);
//        switch (mAcceptMode) {
//            case CopyText:
//                mTopBarMode = TopBarMode.Main;
//                break;
//            default:
//                mTopBarMode = TopBarMode.Annot;
//                break;
//        }
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
    }

    /**
     * 工具栏 - 注释 - 确定点击事件
     *
     * @param v
     */
    public void OnAcceptButtonClick(View v, boolean complete) {
        MuPDFView pageView = (MuPDFView) muPDFReaderView.getDisplayedView();
        boolean success = false;
        if (null == mAcceptMode)
            return;
        switch (mAcceptMode) {
            case CopyText:
                if (pageView != null)
                    success = pageView.copySelection();
//                mTopBarMode = TopBarMode.Main;
//                showInfo(success ? getString(com.lonelypluto.pdfviewerdemo.R.string.copied_to_clipboard) : getString(com.lonelypluto.pdfviewerdemo.R.string.no_text_selected));
                break;
            case Highlight:
                // 高亮
                if (pageView != null) {
                    success = pageView.markupSelection(Annotation.Type.HIGHLIGHT);
                }
//                mTopBarMode = TopBarMode.Annot;
//                if (!success) {
//                    showInfo(getString(com.lonelypluto.pdfviewerdemo.R.string.no_text_selected));
//                }
                break;
            case Underline:
                if (pageView != null)
                    success = pageView.markupSelection(Annotation.Type.UNDERLINE);
//                mTopBarMode = TopBarMode.Annot;
//                if (!success)
//                    showInfo(getString(com.lonelypluto.pdfviewerdemo.R.string.no_text_selected));
                break;

            case StrikeOut:
                if (pageView != null)
                    success = pageView.markupSelection(Annotation.Type.STRIKEOUT);
//                mTopBarMode = TopBarMode.Annot;
//                if (!success)
//                    showInfo(getString(com.lonelypluto.pdfviewerdemo.R.string.no_text_selected));
                break;

            case Ink:
                if (pageView != null)
                    success = pageView.saveDraw();
//                mTopBarMode = TopBarMode.Annot;
//                if (!success)
//                    showInfo(getString(com.lonelypluto.pdfviewerdemo.R.string.nothing_to_save));
                break;
        }
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
        if (complete) {
            muPDFReaderView.setMode(MuPDFReaderView.Mode.Viewing);
            mAcceptMode = null;
        }
    }

    /**
     * 设置按钮是否可点击
     *
     * @param button
     * @param enabled
     */
    private void setButtonEnabled(ImageButton button, boolean enabled) {
        button.setEnabled(enabled);
        button.setColorFilter(enabled ? Color.argb(0xFF, 250, 250, 250) : Color.argb(0xFF, 250, 250, 250));
    }

    /**
     * 开始搜索
     *
     * @param direction 搜索内容
     */
    private void search(int direction) {
//        hideKeyboard();
        int displayPage = muPDFReaderView.getDisplayedViewIndex();
        SearchTaskResult r = SearchTaskResult.get();
        int searchPage = r != null ? r.pageNumber : -1;
//        mSearchTask.go(et_searchText.getText().toString(), direction, displayPage, searchPage);
    }

    /**
     * 设置超链接高亮显示
     *
     * @param highlight
     */
    private void setLinkHighlight(boolean highlight) {
//        mLinkHighlight = highlight;
        // LINK_COLOR tint
//        mLinkButton.setColorFilter(highlight ? Color.argb(0xFF, 255, 160, 0) : Color.argb(0xFF, 255, 255, 255));
        // Inform pages of the change.
        muPDFReaderView.setLinksEnabled(highlight);
    }

    /**
     * 工具栏弹出提示信息
     *
     * @param message 提示内容
     */
    private void showInfo(String message) {

      /*  LayoutInflater inflater = getLayoutInflater();
        View toastLayout = inflater.inflate(com.lonelypluto.pdfviewerdemo.R.layout.toast,
                (ViewGroup) findViewById(com.lonelypluto.pdfviewerdemo.R.id.toast_root_view));

        TextView header = (TextView) toastLayout.findViewById(com.lonelypluto.pdfviewerdemo.R.id.toast_message);
        header.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastLayout);
        toast.show();*/
        if (!getSupportActionBar().isShowing()) {
            getSupportActionBar().show();
        }
    }

    /**
     * 创建提示等待
     */
    public void createAlertWaiter() {
        mAlertsActive = true;
        // All mupdf library calls are performed on asynchronous tasks to avoid stalling
        // the UI. Some calls can lead to javascript-invoked requests to display an
        // alert dialog and collect a reply from the user. The task has to be blocked
        // until the user's reply is received. This method creates an asynchronous task,
        // the purpose of which is to wait of these requests and produce the dialog
        // in response, while leaving the core blocked. When the dialog receives the
        // user's response, it is sent to the core via replyToAlert, unblocking it.
        // Another alert-waiting task is then created to pick up the next alert.
        if (mAlertTask != null) {
            mAlertTask.cancel(true);
            mAlertTask = null;
        }
        if (mAlertDialog != null) {
            mAlertDialog.cancel();
            mAlertDialog = null;
        }
        mAlertTask = new AsyncTask<Void, Void, MuPDFAlert>() {

            @Override
            protected MuPDFAlert doInBackground(Void... arg0) {
                if (!mAlertsActive)
                    return null;

                return muPDFCore.waitForAlert();
            }

            @Override
            protected void onPostExecute(final MuPDFAlert result) {
                // core.waitForAlert may return null when shutting down
                if (result == null)
                    return;
                final MuPDFAlert.ButtonPressed pressed[] = new MuPDFAlert.ButtonPressed[3];
                for (int i = 0; i < 3; i++)
                    pressed[i] = MuPDFAlert.ButtonPressed.None;
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialog = null;
                        if (mAlertsActive) {
                            int index = 0;
                            switch (which) {
                                case AlertDialog.BUTTON1:
                                    index = 0;
                                    break;
                                case AlertDialog.BUTTON2:
                                    index = 1;
                                    break;
                                case AlertDialog.BUTTON3:
                                    index = 2;
                                    break;
                            }
                            result.buttonPressed = pressed[index];
                            // Send the user's response to the core, so that it can
                            // continue processing.
                            muPDFCore.replyToAlert(result);
                            // Create another alert-waiter to pick up the next alert.
                            createAlertWaiter();
                        }
                    }
                };
                mAlertDialog = mAlertBuilder.create();
                mAlertDialog.setTitle(result.title);
                mAlertDialog.setMessage(result.message);
                switch (result.iconType) {
                    case Error:
                        break;
                    case Warning:
                        break;
                    case Question:
                        break;
                    case Status:
                        break;
                }
                switch (result.buttonGroupType) {
                    case OkCancel:
                        mAlertDialog.setButton(AlertDialog.BUTTON2, getString(com.lonelypluto.pdfviewerdemo.R.string.cancel), listener);
                        pressed[1] = MuPDFAlert.ButtonPressed.Cancel;
                    case Ok:
                        mAlertDialog.setButton(AlertDialog.BUTTON1, getString(com.lonelypluto.pdfviewerdemo.R.string.okay), listener);
                        pressed[0] = MuPDFAlert.ButtonPressed.Ok;
                        break;
                    case YesNoCancel:
                        mAlertDialog.setButton(AlertDialog.BUTTON3, getString(com.lonelypluto.pdfviewerdemo.R.string.cancel), listener);
                        pressed[2] = MuPDFAlert.ButtonPressed.Cancel;
                    case YesNo:
                        mAlertDialog.setButton(AlertDialog.BUTTON1, getString(com.lonelypluto.pdfviewerdemo.R.string.yes), listener);
                        pressed[0] = MuPDFAlert.ButtonPressed.Yes;
                        mAlertDialog.setButton(AlertDialog.BUTTON2, getString(com.lonelypluto.pdfviewerdemo.R.string.no), listener);
                        pressed[1] = MuPDFAlert.ButtonPressed.No;
                        break;
                }
                mAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        mAlertDialog = null;
                        if (mAlertsActive) {
                            result.buttonPressed = MuPDFAlert.ButtonPressed.None;
                            muPDFCore.replyToAlert(result);
                            createAlertWaiter();
                        }
                    }
                });

                mAlertDialog.show();
            }
        };

        mAlertTask.executeOnExecutor(new ThreadPerTaskExecutor());
    }

    /**
     * 销毁提示等待
     */
    public void destroyAlertWaiter() {
        mAlertsActive = false;
        if (mAlertDialog != null) {
            mAlertDialog.cancel();
            mAlertDialog = null;
        }
        if (mAlertTask != null) {
            mAlertTask.cancel(true);
            mAlertTask = null;
        }
    }

    @Override
    protected void onStart() {
        if (muPDFCore != null) {
            muPDFCore.startAlerts();
            createAlertWaiter();
        }
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mSearchTask != null) {
//            mSearchTask.stop();
//        }
    }

    @Override
    protected void onStop() {
        if (muPDFCore != null) {
            destroyAlertWaiter();
            muPDFCore.stopAlerts();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (muPDFReaderView != null) {
            muPDFReaderView.applyToChildren(new ReaderView.ViewMapper() {
                public void applyToView(View view) {
                    ((MuPDFView) view).releaseBitmaps();
                }
            });
        }
        if (muPDFCore != null)
            muPDFCore.onDestroy();
        if (mAlertTask != null) {
            mAlertTask.cancel(true);
            mAlertTask = null;
        }
        muPDFCore = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (muPDFCore != null && muPDFCore.hasChanges()) {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == AlertDialog.BUTTON_POSITIVE) {
                        muPDFCore.save();
                    }
                    finish();
                }
            };
            AlertDialog alert = mAlertBuilder.create();
            alert.setTitle(com.lonelypluto.pdfviewerdemo.R.string.dialog_title);
            alert.setMessage(getString(com.lonelypluto.pdfviewerdemo.R.string.document_has_changes_save_them));
            alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(com.lonelypluto.pdfviewerdemo.R.string.yes), listener);
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(com.lonelypluto.pdfviewerdemo.R.string.no), listener);
            alert.show();
        } else {
            finish();
        }
    }

    /**
     * 多线程类
     */
    class ThreadPerTaskExecutor implements Executor {
        public void execute(Runnable r) {
            new Thread(r).start();
        }
    }

    /**
     * 工具栏类型 Search:搜索 Annot:注释
     */
    enum TopBarMode {
        Main, Search, Annot, Delete, Accept
    }

    /**
     * 工具栏注释类型 Highlight:高亮显示 ,Underline:底部画线 ,StrikeOut:废弃线 ,Ink:签字 ,CopyText:复制文字
     */
    enum AcceptMode {
        Highlight, Underline, StrikeOut, Ink, CopyText
    }

    private void showColorPaletteDialog() {
        View dialogView = View.inflate(this, R.layout.layout_color_palette, null);
        GridView colorPaletteGridView = dialogView.findViewById(R.id.color_palette);
        colorPaletteGridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return CommConsts.COLOR_PALETTE_LIST.size();
            }

            @Override
            public Object getItem(int position) {
                return CommConsts.COLOR_PALETTE_LIST.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View itemView = View.inflate(MoreSetActivity.this, R.layout.item_color, null);
                ImageView colorItem = itemView.findViewById(R.id.color_item);
                CheckBox selectColor = itemView.findViewById(R.id.select_color);
                if (mSelectColorPosition == position) {
                    selectColor.setChecked(true);
                } else {
                    selectColor.setChecked(false);
                }
                int color = Color.parseColor(CommConsts.COLOR_PALETTE_LIST.get(position));
                ColorStateList tint = ColorStateList.valueOf(color);
                setTint(colorItem, tint);
                return itemView;
            }
        });
        colorPaletteGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectColorPosition = position;
                mSelectColor = CommConsts.COLOR_PALETTE_LIST.get(position);
                ((BaseAdapter) colorPaletteGridView.getAdapter()).notifyDataSetChanged();
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int color = Color.parseColor(mSelectColor);
                muPDFReaderView.setLinkHighlightColor(color);
                muPDFReaderView.setInkColor(color);
            }
        }).setTitle("请选择颜色").setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create();
        dialog.show();
    }

    private static void setTint(ImageView colorItem, ColorStateList tint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorItem.setImageTintList(tint);
        } else {
            DrawableCompat.setTintList(colorItem.getDrawable(), tint);
        }
    }
}
