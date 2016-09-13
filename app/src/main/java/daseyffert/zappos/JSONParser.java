package daseyffert.zappos;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 9/8/2016.
 */
public class JSONParser
{
    final String SEARCH_KEY_ZAPPOS = "b743e26728e16b81da139182bb2094357c31d331";
    final String SEARCH_KEY_6PM = "524f01b7e2906210f7bb61dcbe1bfea26eb722eb";

    public List<Product> searchProducts(String searchTerm)
    {
        String url = buildUrl(searchTerm, 1);
        return downloadProducts(url);
    }

    public List<Product> compareProducts(String searchID)
    {
        String url = buildUrl(searchID, 2);
        return downloadProducts(url);
    }

    private String buildUrl(String searchTerm, int type)
    {
        String url = null;
        switch (type)
        {
            case 1:
                Uri.Builder builderZappos = new Uri.Builder();
                builderZappos.scheme("https")
                        .authority("api.zappos.com")
                        .appendPath("Search")
                        .appendQueryParameter("term", searchTerm)
                        .appendQueryParameter("key", SEARCH_KEY_ZAPPOS);

                url = builderZappos.build().toString();
                break;
            case 2:
                Uri.Builder builder6pm = new Uri.Builder();
                builder6pm.scheme("https")
                        .authority("api.6pm.com")
                        .appendPath("Search")
                        .appendQueryParameter("term", searchTerm)
                        .appendQueryParameter("key", SEARCH_KEY_6PM);

                url = builder6pm.build().toString();
                break;
        }


        return url;
    }

    private List<Product> downloadProducts(String url)
    {
        List<Product> products = new ArrayList<>();

        try {
            String jsonString = getUrlString(url);
            Log.i("DOWNLOADING PRODUCTS", "Received JSON: " + jsonString);

            //3.1 Make JSON Object to a url
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItem(products, jsonBody);

        } catch (IOException ioe) {
            Log.e("DOWNLOADING PRODUCTS", "Failed to fetch items", ioe);
        } catch (JSONException je) {
            Log.e("DOWNLOADING PRODUCTS", "Failed to Parse JSON", je);
        }
        return products;
    }

    private String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    private byte[] getUrlBytes (String urlSpec) throws IOException
    {
        //1.1 Create URL object then connection-objected pointed to URL
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            //1.2 Setup Input and Output streams
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            //1.2.1 Check connection isn't bad
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }
            //1.3 Read until connection runs out of data
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0)
                out.write(buffer, 0, bytesRead);
            //1.4 Close the Stream then give ByteArrayOutputStream
            out.close();
            return out.toByteArray();

        } finally {
            connection.disconnect();
        }
    }



    private void parseItem(List<Product> productList, JSONObject jsonBody) throws IOException, JSONException {

        JSONArray jsonResults = jsonBody.getJSONArray("results");
        //4.1 Traverse JSON Array extracting needed data
        for (int i = 0; i < jsonResults.length(); i++) {
            JSONObject productJsonObj = jsonResults.getJSONObject(i);
            //4.2 Assign data to items
            Product product = new Product();

            product.setProductID(productJsonObj.getString("productId"));
            product.setBrandName(productJsonObj.getString("brandName"));
            product.setProductName(productJsonObj.getString("productName"));
            product.setOriginalPrice(productJsonObj.getString("originalPrice"));
            product.setPrice(productJsonObj.getString("price"));
            product.setPercentOff(productJsonObj.getString("percentOff"));
            product.setProductThumbnail(productJsonObj.getString("thumbnailImageUrl"));
            product.setProductURL(productJsonObj.getString("productUrl"));

            //4.3 Add item to list
            productList.add(product);
        }
    }
}
