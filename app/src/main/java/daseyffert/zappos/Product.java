package daseyffert.zappos;

/**
 * Created by Daniel on 9/8/2016.
 */
public class Product {

    private String mProductID;
    private String mBrandName;
    private String mProductName;
    private String mOriginalPrice;
    private String mPrice;
    private String mPercentOff;
    private String mProductThumbnail;
    private String mProductURL;

    public double getProductPrice()
    {
        String stringPrice = mPrice.replace("$", "");
        return Double.parseDouble(stringPrice);
    }

    public boolean isProductDiscounted()
    {
        if (mOriginalPrice.equals(mPrice))
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    /**
     * GETTER and SETTERS
     */
    public String getProductID() {
        return mProductID;
    }

    public void setProductID(String productID) {
        mProductID = productID;
    }

    public String getBrandName() {
        return mBrandName;
    }

    public void setBrandName(String brandName) {
        mBrandName = brandName;
    }

    public String getProductName() {
        return mProductName;
    }

    public void setProductName(String productName) {
        mProductName = productName;
    }

    public String getOriginalPrice() {
        return mOriginalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        mOriginalPrice = originalPrice;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public String getPercentOff() {
        return mPercentOff;
    }

    public void setPercentOff(String percentOff) {
        mPercentOff = percentOff + " OFF!";
    }

    public String getProductThumbnail() {
        return mProductThumbnail;
    }

    public void setProductThumbnail(String productThumbnail) {
        mProductThumbnail = productThumbnail;
    }

    public String getProductURL() {
        return mProductURL;
    }

    public void setProductURL(String productURL) {
        mProductURL = productURL;
    }

    public String printProductInfo()
    {
        return mProductID + mBrandName + mProductName + mOriginalPrice + mPrice + mPercentOff + mProductThumbnail + mProductURL;
    }
}
