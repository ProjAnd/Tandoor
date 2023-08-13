package tandoori.resturant.mobile.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Collections;

import tandoori.resturant.mobile.Activity.ItemDetailActivity;
import tandoori.resturant.mobile.CustomRecyclerView.CustomRecyclerView;
import tandoori.resturant.mobile.ModelClass.Products;
import tandoori.resturant.mobile.R;
import tandoori.resturant.mobile.volly.AppController;

import static java.lang.Math.max;

public class HomeFragment extends Fragment
{
    LinearLayout linearHome;
    LinearLayoutManager linearLayoutManagerHomeFragment;


    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(root);

        for (int i = 1; i < AppController.getInstance().getCategories().size(); i++) {
            LayoutInflater linearLaoutFlater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View ll = linearLaoutFlater.inflate(R.layout.item_recyclerview, null);
            linearHome.addView(ll);
            TextView textViewHeader = ll.findViewById(R.id.textViewHeader);
            linearLayoutManagerHomeFragment = new LinearLayoutManager(getActivity());
            CustomRecyclerView recyclerViewHome = ll.findViewById(R.id.recyclerViewHome);
            recyclerViewHome.setHasFixedSize(true);
            recyclerViewHome.setLayoutManager(linearLayoutManagerHomeFragment);
            recyclerViewHome.setItemAnimator(new DefaultItemAnimator());
            recyclerViewHome.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            textViewHeader.setText(AppController.getInstance().getCategories().get(i).getCategoryName());
            HomeFragmentAdapter homeFragmentAdapter = new HomeFragmentAdapter
                    (getActivity(), AppController.getInstance().getCategories().get(i).getProductsArrayList());
            recyclerViewHome.setAdapter(homeFragmentAdapter);
        }
        return root;
    }

    private void initViews(View root) {
        linearHome = root.findViewById(R.id.linearHome);
    }

    public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeFragmentAdapter.MyViewHolder> {
        ArrayList<Products> itemList;
        Context mContext;

        public HomeFragmentAdapter(Context mContext, ArrayList<Products> itemList) {
            this.mContext = mContext;
            this.itemList = itemList;
        }

        @Override
        public HomeFragmentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate
                    (R.layout.item_homefragment, parent, false);
            return new HomeFragmentAdapter.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final HomeFragmentAdapter.MyViewHolder holder, final int position) {
            String pic = itemList.get(position).getImage().get(0).replace("\\", "");
            Glide.with(getActivity())
                    .load(pic)
                    .placeholder(getActivity().getResources().getDrawable(R.drawable.placeholder))
                    .error(getActivity().getResources().getDrawable(R.drawable.placeholder))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.itemImage);
            holder.itemName.setText(itemList.get(position).getName());




            if (itemList.get(position).getVariationArrayList().size() == 0) {
                holder.itemDescription.setText("$" + itemList.get(position).getPrice() + "");
            } else {
                Double price=itemList.get(position).getVariationArrayList().get(0).getPrice();
                Double price1=itemList.get(position).getVariationArrayList().get(1).getPrice();
                Double price2=itemList.get(position).getVariationArrayList().get(2).getPrice();
                double get_max = max(price, max(price1, price2));
                double get_min = -max(-price, max(-price1, -price2));
                double get_mid = (price + price1 + price2)
                        - (get_max + get_min);
                holder.itemDescription.setText("$" + get_min + " - $" + get_max);
            }


            holder.cdItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, ItemDetailActivity.class);
                    i.putExtra("position", position);
                    i.putExtra("productArrayList", itemList);
                    mContext.startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView itemImage;
            TextView itemName, itemRating, itemDescription, itemRateInUsd;
            CardView cdItem;

            public MyViewHolder(final View itemView) {
                super(itemView);
                itemImage = itemView.findViewById(R.id.itemImage);
                itemName = itemView.findViewById(R.id.itemName);
                itemRating = itemView.findViewById(R.id.itemRating);
                itemDescription = itemView.findViewById(R.id.itemDescription);
                itemRateInUsd = itemView.findViewById(R.id.itemRateInUsd);
                cdItem = itemView.findViewById(R.id.cdItem);
            }
        }
    }

    private void sorted(Double price, Double price1, Double price2) {

    }
}