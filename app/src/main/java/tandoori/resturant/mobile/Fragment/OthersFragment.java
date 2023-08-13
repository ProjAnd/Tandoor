package tandoori.resturant.mobile.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import tandoori.resturant.mobile.Activity.ItemDetailActivity;
import tandoori.resturant.mobile.CustomRecyclerView.CustomRecyclerView;
import tandoori.resturant.mobile.ModelClass.Categories;
import tandoori.resturant.mobile.ModelClass.Products;
import tandoori.resturant.mobile.R;
import tandoori.resturant.mobile.volly.AppController;

import static java.lang.Math.max;

public class OthersFragment extends Fragment {
    String imageLink = "";

    public OthersFragment() {
    }

    public static OthersFragment newInstance(String text) {
        OthersFragment f = new OthersFragment();
        Bundle b = new Bundle();
        b.putString("slug", text);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_others, container, false);
        String slug = getArguments().getString("slug");
        initViews(view, slug);
        return view;
    }

    private void initViews(View view, String slug) {
        LinearLayoutManager linearLayoutManagerOthers = new LinearLayoutManager(getActivity());
        CustomRecyclerView recyclerViewOthers = view.findViewById(R.id.recyclerViewOthers);
        recyclerViewOthers.setHasFixedSize(true);
        recyclerViewOthers.setLayoutManager(linearLayoutManagerOthers);
        recyclerViewOthers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewOthers.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        ArrayList<Products> productsArrayList =new ArrayList<>();
        ArrayList<Categories> categoriesArrayList = (ArrayList<Categories>) AppController.getInstance().getCategories().clone();
        for (int products = 0; products < categoriesArrayList.size(); products++) {
            if (categoriesArrayList.get(products).getSlug().equalsIgnoreCase(slug)) {
                productsArrayList.addAll(categoriesArrayList.get(products).getProductsArrayList());
                imageLink = productsArrayList.get(products).getImage().get(0);
            }
        }
        recyclerViewOthers.setAdapter(new OthersFragmentAdapter(productsArrayList));
    }

    public class OthersFragmentAdapter extends RecyclerView.Adapter<OthersFragmentAdapter.MyViewHolder> {
        ArrayList<Products> productsArrayList;

        OthersFragmentAdapter(ArrayList<Products> productsArrayList) {
            this.productsArrayList = productsArrayList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_others_fragment, parent, false);
            return new MyViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            Glide.with(getActivity())
                    .load(productsArrayList.get(position).getImage().get(0))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(getActivity().getResources().getDrawable(R.drawable.placeholder))
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
            holder.itemName.setText(productsArrayList.get(position).getName());
            holder.itemDescription.setText(productsArrayList.get(position).getDescriptions());


//            holder.itemRating.setText("Price : "+productsArrayList.get(position).getPrice());

            if (productsArrayList.get(position).getVariationArrayList().size() == 0) {
                holder.itemRating.setText("$" + productsArrayList.get(position).getPrice() + "");
            } else {
                Double price=productsArrayList.get(position).getVariationArrayList().get(0).getPrice();
                Double price1=productsArrayList.get(position).getVariationArrayList().get(1).getPrice();
                Double price2=productsArrayList.get(position).getVariationArrayList().get(2).getPrice();
                double get_max = max(price, max(price1, price2));
                double get_min = -max(-price, max(-price1, -price2));
                double get_mid = (price + price1 + price2)
                        - (get_max + get_min);
                holder.itemRating.setText("$" + get_min + " - $" + get_max);
            }

            holder.cdItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), ItemDetailActivity.class);
                    i.putExtra("position", position);
                    i.putExtra("productArrayList", productsArrayList);
                    AppController.getInstance().saveString("productId",productsArrayList.get(position).getProductId());
                    getActivity().startActivity(i);
                }
            });
        }
        @Override
        public int getItemCount() {
            return productsArrayList.size();
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
}