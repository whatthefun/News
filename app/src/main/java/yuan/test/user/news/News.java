package yuan.test.user.news;

import android.app.ListActivity;
import android.graphics.Bitmap;

/**
 * Created by USER on 2016/03/23.
 */
public class News extends ListActivity{
    private String Title;
    private String Address;
    private String Time;
    private Bitmap Image;
    private String Source;

    public News(Bitmap image, String title, String address, String time, String source){
        Title = title;
        Address = address;
        Time = time;
        Image = image;
        Source = source;
    }

    public String getTitletext(){
        return Title;
    }

    public String getAddress(){
        return Address;
    }

    public String getTime(){
        return Time;
    }

    public Bitmap getImage(){
        return Image;
    }

    public String getSource(){
        return Source;
    }
}
