package com.homeinspection;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class LivingRoomListAppliancesActivity extends AppCompatActivity {
    FrameLayout back;
    ListView furnitureList;
    TextView txvHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_furniture);
        back = (FrameLayout) findViewById(R.id.flBack);
        furnitureList = (ListView) findViewById(R.id.lvFurnitureList);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txvHeader = (TextView) findViewById(R.id.txvHeader);
        txvHeader.setText("Appliances");

        ArrayList<String> name = new ArrayList<>();
        name.add("Oven");
        name.add("Microwave");
        name.add("Refrigerator");

        FurnitureListAdapter adapter = new FurnitureListAdapter(name);
        furnitureList.setAdapter(adapter);
    }

    public class FurnitureListAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<String> name;
        int m = 0;

        public FurnitureListAdapter(ArrayList<String> name) {
            // TODO Auto-generated constructor stub
            this.name = name;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_furniture_listitem,
                        parent, false);
                holder = new ViewHolder();
                holder.txvFurnitureName = (TextView) convertView.findViewById(R.id.txvFurnitureName);
                holder.imvFurnitureImage = (ImageView) convertView.findViewById(R.id.imvFurnitureImage);
                holder.row = (LinearLayout) convertView.findViewById(R.id.llFurnitureRow);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txvFurnitureName.setText(name.get(position));


            return convertView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return name.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        class ViewHolder {
            ImageView imvFurnitureImage;
            TextView txvFurnitureName;
            LinearLayout row;
        }
    }

}
