/*
 * Copyright 2015 C. A. Fitzgerald
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.riotopsys.factotum.sample;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by afitzgerald on 2/14/15.
 */
public class AnimeAdapter extends BaseAdapter {

    private List<LibraryEntry> list = new ArrayList<>();

    private static class Holder{

        private final RelativeLayout root;
        private final TextView title;
        private final ImageView background;

        public Holder( View view ){
            root = (RelativeLayout)view;
            title = (TextView)view.findViewById(R.id.title);
            background = (ImageView)view.findViewById(R.id.background);
        }

        public void apply( LibraryEntry entry ){
            title.setText(entry.anime.title);
            Picasso.with(root.getContext())
                    .load(entry.anime.cover_image)
                    .into(background);
        }

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if ( convertView == null ) {
            convertView = View.inflate(parent.getContext(), R.layout.anime_item, null);
            convertView.setTag(new Holder(convertView));
        }

        Holder holder = (Holder) convertView.getTag();
        holder.apply(list.get(position));

        return convertView;
    }

    public void setList(List<LibraryEntry> list) {
        this.list.clear();;
        this.list.addAll(list);
        notifyDataSetChanged();
    }

}
