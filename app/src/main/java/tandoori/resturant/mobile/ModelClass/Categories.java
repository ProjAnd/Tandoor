package tandoori.resturant.mobile.ModelClass;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by HP_Owner_Android on 4/16/2018.
 */

public class Categories implements Serializable {
    public String dataId;
    public String bundleId;
    public String slug;
    public String categoryName;
    public String longDescription;

    ArrayList<Products> productsArrayList = new ArrayList<>();

    public ArrayList<Products> getProductsArrayList() {
        return productsArrayList;
    }

    public void setProductsArrayList(ArrayList<Products> productsArrayList) {
        this.productsArrayList = productsArrayList;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

}