package daseyffert.zappos;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 9/7/2016.
 */
public class MainFragment extends Fragment
{
    private final String SEARCH_TERM_KEY = "Search Term Key";
    private String searchTerm;

    private RecyclerView mProductRecyclerView;
    private MenuItem mSearchItem;
    private SearchView mSearchView;
    private Product mZapposProductSelected = null;
    private List<Product> mZapposProducts = new ArrayList<>();
    private List<Product> m6pmProducts = new ArrayList<>();

    public static MainFragment newInstance()
    {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //Wire up all the widgets and Views
        instantiateViews(view);

        setupAdapter();

        //Handle screen rotation
//        if (savedInstanceState!= null)
//        {
//            handleConfigurationChanges(savedInstanceState);
//        }

        return view;
    }

    private void instantiateViews(View view)
    {
        mProductRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_main_recyclerview);
        mProductRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

    }

//    private void handleConfigurationChanges(Bundle savedInstanceState)
//    {
//        //Handle configuration Changes restoring all values of model
//        searchTerm = savedInstanceState.getString(SEARCH_TERM_KEY);
//
//        updateUI(true);
//    }

    private void setupAdapter() {
        if (isAdded()) {
            mProductRecyclerView.setAdapter(new ProductAdapter(mZapposProducts));
        }
    }

    private void updateUI(boolean submit)
    {
        mSearchView.setQuery(searchTerm, submit);
    }

    private void updateItems(String query)
    {
        new FetchProducts(query).execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_search, menu);

        mSearchItem = menu.findItem(R.id.menu_item_search);
        mSearchView = (SearchView) mSearchItem.getActionView();

        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s)
            {
                searchTerm = s;
                updateItems(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                return false;
            }
        });

        mSearchView.setOnSearchClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                updateUI(false);
            }
        });

    }

    private void notifyUser6pmProducts()
    {
        final ArrayList<Product> cheaperProducts = new ArrayList<>();

        for (Product product: m6pmProducts)
        {
            if (product.getProductPrice() < mZapposProductSelected.getProductPrice())
            {
                cheaperProducts.add(product);
            }
        }

        if (cheaperProducts.size() > 0 )
        {
            alertCustomer(cheaperProducts.get(0));
        }
    }

    private void alertCustomer(final Product product)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("We found the same product even cheaper at 6pm.com, would you like to ");

        alertDialogBuilder.setPositiveButton("Yes Please!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Uri uri = Uri.parse(product.getProductURL());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        alertDialogBuilder.setNegativeButton("No Thanks!",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState)
//    {
//        super.onSaveInstanceState(outState);
//
//        outState.putString(SEARCH_TERM_KEY, searchTerm);
//    }



    /**
     * ViewHolder for RecyclerView
     */
    private class ProductHolder extends RecyclerView.ViewHolder
    {
        private View mItemView;
        private ImageView mProductImage;
        private TextView mBrandName;
        private TextView mProductName;
        private TextView mPrice;
        private TextView mOldPrice;
        private TextView mPercentOff;

        public ProductHolder(View itemView)
        {
            super(itemView);

            mItemView = itemView;

            mProductImage = (ImageView) itemView.findViewById(R.id.product_view_image);
            mBrandName = (TextView) itemView.findViewById(R.id.product_view_brand_name);
            mProductName = (TextView) itemView.findViewById(R.id.product_view_product_name);
            mPrice = (TextView) itemView.findViewById(R.id.product_view_price);
            mOldPrice = (TextView) itemView.findViewById(R.id.product_view_original_price);
            mPercentOff = (TextView) itemView.findViewById(R.id.product_view_percent_off);

        }

        public void bindDrawable(final Product product)
        {
            Picasso.with(mProductImage.getContext()).load(product.getProductThumbnail()).into(mProductImage);
            mBrandName.setText(product.getBrandName());
            mProductName.setText(product.getProductName());
            mPrice.setText(product.getPrice());
            if (!product.isProductDiscounted())
            {
                mOldPrice.setVisibility(View.GONE);
                mPercentOff.setVisibility(View.GONE);
            }
            else
            {
                mOldPrice.setVisibility(View.VISIBLE);
                mPercentOff.setVisibility(View.VISIBLE);

                mOldPrice.setText(product.getOriginalPrice());
                mPercentOff.setText(product.getPercentOff());
            }

            mItemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mZapposProductSelected = product;
                    new CompareProducts(product.getProductID()).execute();
                }
            });
        }
    }

    /**
     * Adapter for RecyclerView
     */
    private class ProductAdapter extends RecyclerView.Adapter<ProductHolder>
    {
        private List<Product> mProductList;

        public ProductAdapter(List<Product> productList)
        {
            mProductList = productList;
        }

        @Override
        public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            //assign layout to View
            View view = inflater.inflate(R.layout.product_view, parent, false);
            //make viewHolder with create View
            return new ProductHolder(view);
        }

        @Override
        public void onBindViewHolder(ProductHolder holder, int position)
        {
            Product product = mProductList.get(position);

            holder.bindDrawable(product);
        }

        @Override
        public int getItemCount()
        {
            return mProductList.size();
        }
    }



    /**
     * ASYNC THREAD to fetch the products from the website
     */
    private class FetchProducts extends AsyncTask<Void, Void, List<Product>>
    {
        private String mQuery;

        public FetchProducts(String query)
        {
            mQuery = query;
        }

        @Override
        protected List<Product> doInBackground(Void... voids)
        {
            return new JSONParser().searchProducts(mQuery);
        }

        @Override
        protected void onPostExecute(List<Product> products)
        {
            mZapposProducts = products;
            setupAdapter();
        }
    }

    /**
     * ASYNC THREAD to fetch the products from the website
     */
    private class CompareProducts extends AsyncTask<Void, Void, List<Product>>
    {
        private String mQueryID;

        public CompareProducts(String query)
        {
            mQueryID = query;
        }

        @Override
        protected List<Product> doInBackground(Void... voids)
        {
            return new JSONParser().compareProducts(mQueryID);
        }

        @Override
        protected void onPostExecute(List<Product> products)
        {
            m6pmProducts = products;

            notifyUser6pmProducts();
        }
    }
}
