package com.example.user.news;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String urlTopStories = "https://news.google.com.tw/news",
            urlWorld = "https://news.google.com.tw/news/section?cf=all&pz=1&topic=w",
            urlTaiwan = "https://news.google.com.tw/news/section?cf=all&pz=1&topic=n",
            urlBusiness = "https://news.google.com.tw/news/section?cf=all&pz=1&topic=b",
            urlTechnology = "https://news.google.com.tw/news/section?cf=all&pz=1&topic=t",
            urlSports = "https://news.google.com.tw/news/section?cf=all&pz=1&topic=s",
            urlEntertainment = "https://news.google.com.tw/news/section?cf=all&pz=1&ned=tw&topic=e",
            urlTaiwan_China = "https://news.google.com.tw/news/section?cf=all&pz=1&topic=c",
            urlCommunity = "https://news.google.com.tw/news/section?cf=all&pz=1&topic=y",
            urlHealthy = "https://news.google.com.tw/news/section?cf=all&pz=1&topic=m";

    private String sUrl = urlTopStories;
    private boolean isSlide = false;
    private View view;
    private ListView listView;
    private Adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog = null;
    private TextView txtHeading;
    private MotionEvent down, stop, move;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        view = findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.listView);
//        listView.setOnTouchListener(listViewTouchListener);

        listView.setFastScrollEnabled(true);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        txtHeading = (TextView) findViewById(R.id.txtHeading);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light, android.R.color.holo_blue_light, android.R.color.holo_purple);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (listView == null || listView.getChildCount() == 0) ?
                                0 : listView.getChildAt(0).getTop();
                //在最上面才可以swipe更新資料
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("onRefresh", "Refreshing");
                swipeRefreshLayout.setRefreshing(true);
                swipeRefreshLayout.setEnabled(false);
                new Thread(spider).start();
            }
        });

//        浮動按鈕
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSlide) {
                    isSlide = true;
                    new Thread(slide).start();
                    isSlide = false;
                }
            }
        });

//        側邊欄
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        new Thread(spider).start();
    }


    //抓新聞資料的執行續
    private Thread spider = new Thread(new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    setProgressBarIndeterminateVisibility(true);
                    progressDialog = ProgressDialog.show(MainActivity.this, "請稍等...", "獲取資料中...", true);
                }
            });
            Looper.prepare();
            Log.d("thread", "running");

            Connection conn = Jsoup.connect(sUrl);
            conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");

            Document doc = null;
            try {
                doc = conn.get();
            } catch (Exception e) {
                Log.e("Connection", e.toString());
            }
            Elements elements = doc.select("table.esc-layout-table");
            List<News> news_list = new ArrayList<News>();//new 新的List<News>裝新資料

            for (int i = 0; i < elements.size(); i++) {
                String imageUrl = "", Title = "", Time = "", Address = "", Source = "";
                Bitmap Image = null;

                //Google新聞的圖片Html格式一種用src一種用imgsrc
                imageUrl = elements.get(i).select("div.esc-thumbnail-image-wrapper img").attr("src");
                if (imageUrl == "") {
                    imageUrl = elements.get(i).select("div.esc-thumbnail-image-wrapper img").attr("imgsrc");
                }

                if (!imageUrl.equals("")) {//在此過濾空的圖片
                    if (imageUrl.substring(0, 3).equals("//t")) {//google有些圖放在//t開頭的網址裡
                        Image = getBitmapfromURL("http:" + imageUrl);
                    } else {  //google有的圖片是用Base64編碼存放，所以先把此字串前面data:image/jpeg;base64,的宣告刪掉再去解碼
                        try {
                            imageUrl = imageUrl.substring(imageUrl.indexOf(",") + 1);
                            byte[] byteUrl = Base64.decode(imageUrl, Base64.DEFAULT);
                            Image = BitmapFactory.decodeByteArray(byteUrl, 0, byteUrl.length);
                        } catch (Exception e) {
                            Log.e("Base64", e.toString());
                            Log.e("Base64", imageUrl);
                        }
                    }
                }

                Title = elements.get(i).select("h2.esc-lead-article-title span.titletext").text();
                Address = elements.get(i).select("h2.esc-lead-article-title a").attr("url");
                Time = elements.get(i).select("div.esc-lead-article-source-wrapper span.al-attribution-timestamp").text();
                Source = elements.get(i).select("div.esc-lead-article-source-wrapper span.al-attribution-source").text();
                news_list.add(new News(Image, Title, Address, Time, Source));
            }

            adapter = new Adapter(MainActivity.this, news_list);
            runOnUiThread(new Runnable() {
                public void run() {
                    listView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setEnabled(true);
                    progressDialog.dismiss();
                }
            });
        }
    });

    //touchListener
//    private View.OnTouchListener listViewTouchListener = new View.OnTouchListener() {
//        private float x, y;
//        private int mx, my;
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    x = event.getX();
//                    y = event.getY();
//                    Log.d("Action_Down", " X:" + x + " Y:" + y);
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    mx = (int) (event.getX() - x);
//                    my = (int) (event.getY() - y);
//                    Log.d("Action_Move", "mX:" + mx + " mY:" + my);
//                    break;
//            }
////            Log.d("Action_Time", "dt:" + event.getDownTime() + " et:" + event.getEventTime());
//            return true;
//        }
//    };

    //滑頁的執行續
    private Thread slide = new Thread(new Runnable() {
        @Override
        public void run() {
            int x = getResources().getDisplayMetrics().widthPixels;
            int y = getResources().getDisplayMetrics().heightPixels;
            Log.d("Point", x + "+" + y);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    down = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 500, 900, 0);
                    view.dispatchTouchEvent(down);
                    new CountDownTimer(1000,100){
                        int distance = 1;
                        @Override
                        public void onTick(long millisUntilFinished) {
                            move = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, 500, 900 - 80*distance, 0);
                            view.dispatchTouchEvent(move);
                            distance++;
                        }
                        @Override
                        public void onFinish() {
                            stop = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 500, 100, 0);
                            view.dispatchTouchEvent(stop);
                            move.recycle();
                            down.recycle();
                            stop.recycle();
                        }
                    }.start();
                }
            });
        }
    });

    //傳入圖片網址回傳Bitmap圖
    public static Bitmap getBitmapfromURL(String src) {
        try {
            java.net.URL url = new URL(src);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();//建立連線

            InputStream input = conn.getInputStream();//把網址內資料丟進Stream
            Bitmap mBitmap = BitmapFactory.decodeStream(input);//把資料轉為Bitmap格式
            return mBitmap;
        } catch (Exception e) {
            Log.e("URL", src);
            Log.e("getBitmap", e.toString());
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        progressDialog.dismiss();
        progressDialog = null;
        int id = item.getItemId();
        item.setChecked(true);
        txtHeading.setText(item.getTitle());
        if (id == R.id.iTopStories) {
            sUrl = urlTopStories;
        } else if (id == R.id.iWorld) {
            sUrl = urlWorld;
        } else if (id == R.id.iTaiwan) {
            sUrl = urlTaiwan;
        } else if (id == R.id.iBusiness) {
            sUrl = urlBusiness;
        } else if (id == R.id.iTechnology) {
            sUrl = urlTechnology;
        } else if (id == R.id.iSports) {
            sUrl = urlSports;
        } else if (id == R.id.iEntertainment) {
            sUrl = urlEntertainment;
        } else if (id == R.id.iTaiwan_China) {
            sUrl = urlTaiwan_China;
        } else if (id == R.id.iCommunity) {
            sUrl = urlCommunity;
        } else if (id == R.id.iHealthy) {
            sUrl = urlHealthy;
        }

        new Thread(spider).start();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
