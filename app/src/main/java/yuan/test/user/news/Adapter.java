package yuan.test.user.news;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.news.R;

import java.util.List;

/**
 * Created by USER on 2016/03/24.
 */
public class Adapter extends BaseAdapter{
    private LayoutInflater myInflater;
    private List<News> news;

    public Adapter(Context context, List<News> news){
        myInflater = LayoutInflater.from(context);
        this.news = news;
    }

    @Override
    public int getCount() {
        return news.size();
    }

    @Override
    public Object getItem(int position) {
        return news.get(position);
    }

    @Override
    public long getItemId(int position) {
        return news.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder holder;
        if(convertView == null){
            convertView = myInflater.inflate(R.layout.news_list, null);
            holder = new viewHolder(
                    (ImageView)convertView.findViewById(R.id.imgNews)
                    ,(TextView) convertView.findViewById(R.id.txtTitle)
                    ,(TextView) convertView.findViewById(R.id.txtTime)
                    ,(TextView) convertView.findViewById(R.id.txtSource)
            );

            convertView.setTag(holder);
        }else{
            holder = (viewHolder) convertView.getTag();
        }

        News news = (News) getItem(position);

        if(news.getImage() != null) {
            holder.imageView.setImageBitmap(news.getImage());
        }
        holder.txtTitle.setText(Html.fromHtml("<a href = " + news.getAddress() + "> " + news.getTitletext() + "</a>"));
        holder.txtTime.setText(news.getTime());
        holder.txtSource.setText(news.getSource());
        return convertView;
    }

    private class viewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtTime;
        TextView txtSource;

        public viewHolder(ImageView imageView, TextView txtTitle, TextView txtTime, TextView txtSource){
            this.imageView = imageView;
            this.txtTitle = txtTitle;
            txtTitle.setMovementMethod(LinkMovementMethod.getInstance());
            this.txtTime = txtTime;
            this.txtSource = txtSource;
        }

    }
}
