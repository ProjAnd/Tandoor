package tandoori.resturant.mobile.ModelClass;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by HP_Owner_Android on 4/16/2018.
 */

public class Products implements Serializable {
    private String productId;
    private String name;
    private String slug;
    private String shortDescription;
    private String descriptions;
    private ArrayList<String> image = new ArrayList<>();
    private String attributes;
    private double price;
    private ArrayList<Variation> variationArrayList = new ArrayList<>();
    private ArrayList<String> varianceAttribute = new ArrayList<>();

    public ArrayList<String> getVarianceAttribute() {
        return varianceAttribute;
    }

    public void setVarianceAttribute(ArrayList<String> varianceAttribute) {
        this.varianceAttribute = varianceAttribute;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public ArrayList<Variation> getVariationArrayList() {
        return variationArrayList;
    }

    public void setVariationArrayList(ArrayList<Variation> variationArrayList) {
        this.variationArrayList = variationArrayList;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

}