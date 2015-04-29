package com.mrcornman.otp.models;

/**
 * Created by Anil on 7/17/2014.
 * Sample Product
 * {
        sizes: "7,8,9,10"
        stylename: "Fila Men Grey Unique Casual Shoes"
        search_image: http://myntra.myntassets.com/images/style/style_search_image/Fila-Men-Grey-Unique-Casual-Shoes_4f2ce26116e46668dde9071b0c7c8829_images_180_240.jpg
        discounted_price: 1199
        discount_label: "Flat_Search_Percent"
        dre_discount_label: "(40% OFF)"
        dre_landing_page_url: "Casual-Shoes/FILA/Fila-Men-Grey-Unique-Casual-Shoes/238802/buy"
        visual_tag: ""
        global_attr_base_colour: "Grey"
        discount: 800
        id: "0_style_238802"
        product: "Fila Men Grey Unique Casual Shoes"
        imageEntry_default: "{\"path\":\"http://assets.myntassets.com/v1/images/style/properties/Fila-Men-Grey-Unique-Casual-Shoes_4f2ce26116e46668dde9071b0c7c8829_images.jpg\",\"resolutionFormula\":\"h_($height),q_($qualityPercentage),w_($width)/v1/images/style/properties/Fila-Men-Grey-Unique-Casual-Shoes_4f2ce26116e46668dde9071b0c7c8829_images.jpg\",\"domain\":\"http://assets.myntassets.com/\",\"securedDomain\":\"https://res.cloudinary.com/myntra-com/iu/\",\"relativePath\":\"v1/images/style/properties/Fila-Men-Grey-Unique-Casual-Shoes_4f2ce26116e46668dde9071b0c7c8829_images.jpg\",\"storedUploaderType\":\"CL\",\"servingUploaderType\":\"CL\",\"supportedResolutions\":\"1080X1440,360X480,540X720,48X64,150X200,180X240,81X108,96X128\"}"
        price: 1999
        brands_filter_facet: "FILA"
        styleid: 238802
        global_attr_colour1: "NA"
        style_group: ""
        allSkuForSizes: [
            "7:803148"
            "8:803149"
            "9:803150"
            "10:803151"
        ]
 * }
 */
public class MatchItem {
    //fixme: update properties of Product, add required properties and remove useless ones like mIndex;
    // discount, discounted price, actual price, image url.. etc, unique id
    // remove mId, mIndex;
    private int mId;
    private String styleName;
    private String discountedPrice;
    private String discount;
    private String price;
    private String styleId;
    private String imageUrl;
    private String dreLandingPageUrl;
    private String uniqueProductGroup;
    private String productGroup;
    private int liked;

    public String getUniqueProductGroup() {
        return uniqueProductGroup;
    }

    public void setUniqueProductGroup(String uniqueProductGroup) {
        this.uniqueProductGroup = uniqueProductGroup;
    }

    public String getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStyleId() {
        return styleId;
    }

    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    public String getDreLandingPageUrl() {
        return dreLandingPageUrl;
    }

    public void setDreLandingPageUrl(String dreLandingPageUrl) {
        this.dreLandingPageUrl = dreLandingPageUrl;
    }

    @Override
    public String toString() {
        return styleName;
    }

    public MatchItem(int mId){
        this.mId = mId;
    }

    public MatchItem(String groupLabel){
        this.uniqueProductGroup = groupLabel;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
