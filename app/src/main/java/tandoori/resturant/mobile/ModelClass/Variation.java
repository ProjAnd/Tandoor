package tandoori.resturant.mobile.ModelClass;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by HP_Owner_Android on 5/21/2018.
 */

public class Variation implements Serializable {
    private String productVariationId;
    private String name;
    private String slug;
    private String shortDescription;
    private String descriptions;
    private String attributes;
    private Double price;
    private ArrayList<String> image = new ArrayList<>();
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

    public String getProductVariationId() {
        return productVariationId;
    }

    public void setProductVariationId(String productVariationId) {
        this.productVariationId = productVariationId;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
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