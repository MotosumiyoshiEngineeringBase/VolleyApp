package base.engineering.motosumiyoshi.volleymanageapp.helper;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import base.engineering.motosumiyoshi.volleymanageapp.R;
import base.engineering.motosumiyoshi.volleymanageapp.model.ChatCommunity;
import base.engineering.motosumiyoshi.volleymanageapp.model.News;

public class NewsAdapter extends ArrayAdapter<News> {
    public NewsAdapter(Context context, int resource, List<News> objects) {
        super((Context) context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_news, parent, false);
        }

        TextView createDateTextView = (TextView) convertView.findViewById(R.id.communityName);
        //TextView textTextView = (TextView) convertView.findViewById(R.id.communityExplain);

        News news = getItem(position);

        createDateTextView.setText(news.getCreateDate().toString());
        //textTextView.setText(news.getText());

        return convertView;
    }
}
