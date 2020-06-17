package com.example.lit.smartap_20180111;

import android.content.Context;
import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyRecyclerAdpter extends RecyclerView.Adapter<MyRecyclerAdpter.MyViewHolder> {
    private final String TAG="MyRecyclerAdpter";
    private List<String> data;
    private List<Boolean> signlist;
    private int selcetPosition;
    private Context context;

    public MyRecyclerAdpter(Context context, List<String>  data){
        super();
        this.data=data;
        initSignList();
        selcetPosition=-1;
        this.context=context;
    }

    void initSignList(){
        signlist=new ArrayList<Boolean>();
        for (int i=0;i<data.size();i++) {
            signlist.add(false);
        }
    }

    List<Map<String,String>> initlistdata(){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        Map<String,String> map=new HashMap<String, String>();
        map.put("head","1");
        map.put("name","liutao");
        map.put("info","mensuo");
        mapList.add(map);
        return mapList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.layout_list_itemfordevicelist, parent,
                false));
    }

    @Override
    public int getItemCount(){
        return data.size();
    }


    interface OnItemClickListener
    {
        public void onItemClick(View view, int postion);
        public void onItemLongClick(View view,int postion);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener (OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position){
        Log.i(TAG, "onBindViewHolder: start :"+position);
        if (signlist.get(position)){
            holder.constraintLayout.setVisibility(View.VISIBLE);
        }else {
            holder.constraintLayout.setVisibility(View.GONE);
        }
        if (onItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=holder.getAdapterPosition();
                    onItemClickListener.onItemClick(holder.itemView,pos);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos=holder.getAdapterPosition();
                    onItemClickListener.onItemLongClick(holder.itemView,pos);
                    return false;
                }
            });
        }

        holder.tv1.setText(data.get(position));
        holder.tv2.setText(" 3 ");
        holder.tv3.setText(" 1 ");
        holder.hidetextview.setText("hide part");
        holder.listView.setAdapter(new SimpleAdapter(this.context,
                initlistdata(),
                R.layout.layout_list_itemforhidelist,
                new String[]{"head","name","info"},
                new int[]{R.id.textView4,R.id.textView8,R.id.textView5}
                ));

        //单选展示隐藏列表内容
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if(signlist.get(pos)){
                    signlist.set(pos,false);
                }else {
                    signlist.set(pos,true);
                }
                if (selcetPosition!=pos && selcetPosition!=-1){
                    signlist.set(selcetPosition,false);
                }
                selcetPosition=pos;
                notifyDataSetChanged();
            }
        });
        Log.i(TAG, "onBindViewHolder: finish:"+position);
            /*
            可复选展示隐藏列表内容
            */

            /*
            holder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (signlist.get(position)) {
                        signlist.set(position,false);
                        holder.hidetextview.setVisibility(View.GONE);
                        //notifyDataSetChanged();
                    }else {
                        signlist.set(position,true);
                        holder.hidetextview.setVisibility(View.VISIBLE);
                        //notifyDataSetChanged();
                    }
                }
            });
            */

    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv1,tv2,tv3,hidetextview;
        ImageView imageView;
        ImageView imageButton;
        ListView listView;
        ConstraintLayout constraintLayout;

        public MyViewHolder(View view)
        {
            super(view);
            tv1 =view.findViewById(R.id.devicename);
            tv2 = view.findViewById(R.id.power);
            tv3 = view.findViewById(R.id.singnal);
            imageView = view.findViewById(R.id.imageView);
            imageButton = view.findViewById(R.id.imageButton);
            hidetextview = view.findViewById(R.id.hide_testview);
            listView=view.findViewById(R.id.hide_listview);
            constraintLayout = view.findViewById(R.id.constraintLayout_hide);
            Log.i(TAG, "MyViewHolder: finishi");
        }
    }

    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int normal;
        private final int margin;

        @Override
        public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int childAdapterPosition = parent.getChildAdapterPosition(view);

            if (childAdapterPosition == 0 ) {
                outRect.top = normal;
            }else {
                outRect.top = 0;
            }
            outRect.bottom = normal;
            outRect.right = margin;
            outRect.left = margin;
            /*
            if (childAdapterPosition % 3 == 0) {
                outRect.right = normal;
                outRect.left = margin;
            } else if (childAdapterPosition % 3 == 1) {
                outRect.right = margin;
                outRect.left = margin;
            } else if (childAdapterPosition % 3 == 2) {
                outRect.right = normal;
                outRect.left = margin;
            }
             */

        }

        public SpaceItemDecoration(int normal, int margin) {
            this.normal = normal;
            this.margin = margin;
        }
    }
}
