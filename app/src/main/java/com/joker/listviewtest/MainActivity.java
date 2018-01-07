package com.joker.listviewtest;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by @author jokermonn
 */
public class MainActivity extends AppCompatActivity {
  private List<String> data = new ArrayList<>();

  {
    for (int i = 1; i < 34; i++) {
      data.add(String.valueOf(i));
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    final ListView listView = findViewById(R.id.lv_content);
    final TextView textView = findViewById(R.id.tv_info);
    listView.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override public void onScrollStateChanged(AbsListView view, int scrollState) {

      }

      @SuppressLint("SetTextI18n") @SuppressWarnings("unchecked") @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
        try {
          Class<?> absClass = Class.forName("android.widget.AbsListView");
          Field mRecycler = absClass.getDeclaredField("mRecycler");
          mRecycler.setAccessible(true);
          Object recyclerInstance = mRecycler.get(listView);

          Class<?> recycleBin = Class.forName(mRecycler.getType().getName());
          Constructor<?> recycleBinCon = recycleBin.getDeclaredConstructor(absClass);

          Field mCurrentScrap = recycleBin.getDeclaredField("mCurrentScrap");
          Field mActiveViews = recycleBin.getDeclaredField("mActiveViews");
          Field mScrapViews = recycleBin.getDeclaredField("mScrapViews");
          mCurrentScrap.setAccessible(true);
          mActiveViews.setAccessible(true);
          mScrapViews.setAccessible(true);

          ArrayList<View> mCurrents = (ArrayList<View>) (mCurrentScrap.get(recyclerInstance));
          ArrayList<View>[] mScraps = (ArrayList<View>[]) (mScrapViews.get(recyclerInstance));
          textView.setText(
              "mActiveViews' length is "
                  + ((View[]) (mActiveViews.get(recyclerInstance))).length
                  + "\n"
                  + "mScrapViews' length is "
                  + mScraps.length
                  + "\n"
                  + "mCurrentScrap's size is "
                  + ((ArrayList<View>) (mCurrentScrap.get(recyclerInstance))).size()
                  + "\n"
                  + getLastOneMCurrentScrap(mCurrents)
                  + getMScrapViews(mScraps)
                  + getLastOneMScrapViews(mScraps)

          );
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      private String getMScrapViews(ArrayList<View>[] mScraps) {
        int length = mScraps.length;
        if (length > 0) {
          String s = "";
          for (int i = 0; i < length; i++) {
            s += "mScrapViews[" + i + "] size is " + mScraps[i].size() + "\n";
          }
          s += "\n";
          return s;
        }
        return "";
      }

      private CharSequence getLastOneMScrapViews(ArrayList<View>[] mScraps) {
        if (mScraps.length > 0) {
          String s = "";
          int size = mScraps[mScraps.length - 1].size();
          if (size > 0) {
            return "mScrapViews["
                + (mScraps.length - 1)
                +"]["
                + (size - 1)
                + "] type is "
                + mScraps[mScraps.length - 1].get(size - 1)
                + "\n";
          }
        }
        return "";
      }

      private CharSequence getLastOneMCurrentScrap(ArrayList<View> views) {
        return views.size() > 0 ?
            "the last one of mCurrentScrap's type is "
                + views.get(views.size() - 1)
                + "\n"
            : "\n\n";
      }
    });
    listView.setAdapter(new BaseAdapter() {
      @Override public int getCount() {
        return data.size();
      }

      @Override public Object getItem(int position) {
        return data.get(position);
      }

      @Override public long getItemId(int position) {
        return position;
      }

      @Override public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
          if (isFirstViewType(position)) {
            convertView = new Item1(MainActivity.this);
          } else {
            convertView = new Item2(MainActivity.this);
          }
          holder = new ViewHolder(convertView);
          convertView.setTag(holder);
        } else {
          holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(data.get(position));

        return convertView;
      }

      @Override public int getViewTypeCount() {
        return 2;
      }

      @Override public int getItemViewType(int position) {
        return isFirstViewType(position) ? 0 : 1;
      }

      private boolean isFirstViewType(int position) {
        return position < 17;
      }

      class ViewHolder {
        TextView tv;

        ViewHolder(View view) {
          tv = view.findViewById(R.id.tv_content);
        }
      }
    });
  }
}