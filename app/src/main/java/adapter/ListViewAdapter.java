package adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inftel07.myapplication.MainActivity;
import com.example.inftel07.myapplication.R;
import com.example.inftel07.myapplication.ViewProduct;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import model.Product;
import util.Util;

/**
 * Created by inftel08 on 28/3/16.
 */
public class ListViewAdapter extends BaseAdapter {


    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<Product> productModelList = null;
    private ArrayList<Product> arraylist;

    public ListViewAdapter(Context context, List<Product> productModelList) {
        mContext = context;
        this.productModelList = productModelList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Product>();
        this.arraylist.addAll(productModelList);
    }

    public class ViewHolder {
        TextView userName;
        TextView price;
        TextView nameProduct;
        de.hdodenhof.circleimageview.CircleImageView imageProduct;
    }

    @Override
    public int getCount() {
        return productModelList.size();
    }

    @Override
    public Product getItem(int position) {
        return productModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            //System.out.println("Prifdsaemro"+productModelList.get(0).getNameproduct());
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listfragment, null);
            // Locate the TextViews in listview_item.xml
            holder.nameProduct = (TextView) view.findViewById(R.id.nameProductList);
            holder.price = (TextView) view.findViewById(R.id.priceProductList);
            holder.imageProduct = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.icon);
            //holder.userName = (TextView) view.findViewById(R.id.);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.nameProduct.setText(productModelList.get(position).getNameproduct());
        holder.price.setText(productModelList.get(position).getPrice());
        Bitmap bitmap = Util.base64ToBitmap(productModelList.get(position).getImage());
        holder.imageProduct.setImageBitmap(bitmap);



        //holder.userName.setText(productModelList.get(position).getUserName());

        // Listen for ListView Item Click

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Send single item click data to SingleItemView Class
                Intent intent = new Intent(mContext, ViewProduct.class);
                Gson productGson = new Gson();
                String productString = productGson.toJson(productModelList.get(position));
                intent.putExtra("product", productString);
                mContext.startActivity(intent);
            }
        });

        return view;
    }

    // Filter Class
    /*public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        productModelList.clear();
        if (charText.length() == 0) {
            productModelList.addAll(arraylist);
        }
        else
        {
            for (Product pm : arraylist)
            {
                if (pm.getNameproduct().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    productModelList.add(pm);
                }
            }
        }
        notifyDataSetChanged();
    }*/

}
