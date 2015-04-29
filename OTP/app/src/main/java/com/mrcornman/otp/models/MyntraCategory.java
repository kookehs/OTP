package com.mrcornman.otp.models;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import com.mrcornman.otp.R;

/**
 * Created by Anil on 8/24/2014.
 */
public class MyntraCategory {

    public static class ProductGroup{

        private String groupLabel;
        private String uniqueGroupLabel;
        private String fileName;
        private String startFromKey;
        private String maxProductsKey;
        private String postDataHead;
        private String postDataTail;

        public ProductGroup(String groupLabel, String uniqueGroupLabel, String fileName, String startFromKey, String maxProductsKey, String postDataHead, String postDataTail) {
            this.groupLabel = groupLabel;
            this.uniqueGroupLabel = uniqueGroupLabel;
            this.fileName = fileName;
            this.startFromKey = startFromKey;
            this.maxProductsKey = maxProductsKey;
            this.postDataHead = postDataHead;
            this.postDataTail = postDataTail;
        }

        public String getGroupLabel() {
            return groupLabel;
        }

        public void setGroupLabel(String groupLabel) {
            this.groupLabel = groupLabel;
        }

        public String getUniqueGroupLabel() {
            return uniqueGroupLabel;
        }

        public void setUniqueGroupLabel(String uniqueGroupLabel) {
            this.uniqueGroupLabel = uniqueGroupLabel;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getStartFromKey() {
            return startFromKey;
        }

        public void setStartFromKey(String startFromKey) {
            this.startFromKey = startFromKey;
        }

        public String getMaxProductsKey() {
            return maxProductsKey;
        }

        public void setMaxProductsKey(String maxProductsKey) {
            this.maxProductsKey = maxProductsKey;
        }

        public String getPostDataHead() {
            return postDataHead;
        }

        public void setPostDataHead(String postDataHead) {
            this.postDataHead = postDataHead;
        }

        public String getPostDataTail() {
            return postDataTail;
        }

        public void setPostDataTail(String postDataTail) {
            this.postDataTail = postDataTail;
        }
    }
    public static class ProductHeadGroup{

        private String groupName;
        private List<ProductGroup> childGroups;
        private boolean isHeader;

        public ProductHeadGroup(String groupName, List<ProductGroup> childGroups) {
            this.groupName = groupName;
            this.childGroups = childGroups;
        }

        public ProductHeadGroup(String groupName, List<ProductGroup> childGroups, boolean isHeader) {
            this.groupName = groupName;
            this.childGroups = childGroups;
            this.isHeader = isHeader;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public List<ProductGroup> getChildGroups() {
            return childGroups;
        }

        public void setChildGroups(List<ProductGroup> childGroups) {
            this.childGroups = childGroups;
        }

        public boolean isHeader() {
            return isHeader;
        }

        public void setHeader(boolean isHeader) {
            this.isHeader = isHeader;
        }
    }

    public static List<ProductHeadGroup> generateSampleProductHeadGroups(Context context) {
        List<ProductHeadGroup> productHeadGroups = new ArrayList<ProductHeadGroup>();
        List<ProductGroup> productGroups = new ArrayList<ProductGroup>();

        ProductGroup productGroup = new ProductGroup(
                "Casual Shoes",
                context.getString(R.string.men_shoes_group_label),
                context.getString(R.string.men_shoes_filename),
                context.getString(R.string.men_shoes_start_from_key),
                context.getString(R.string.men_shoes_max_products_key),
                context.getString(R.string.men_shoes_post_data_head),
                context.getString(R.string.men_shoes_post_data_tail)
        );

        productGroups.add(productGroup);
        productGroups.add(productGroup);
        productGroups.add(productGroup);
        productHeadGroups.add(new ProductHeadGroup("Men", productGroups));

        return productHeadGroups;
    }

    public static List<ProductHeadGroup> generateMyntraMenProductGroups(Context context){
        List<ProductHeadGroup> productHeadGroups = new ArrayList<ProductHeadGroup>();
        List<ProductGroup> productGroups;
        ProductGroup productGroup;

        //////////////////////////// Accessories /////////////////////////////
        productGroups = new ArrayList<ProductGroup>();
        // Belts, Ties & Cufflinks //
        productGroup = new ProductGroup(
                "Belts, Ties & Cufflinks",
                "men-accessories-belts-ties-and-cufflinks",
                "men-accessories-belts-ties-and-cufflinks-file",
                "men-accessories-belts-ties-and-cufflinks-start-key",
                "men-accessories-belts-ties-and-cufflinks-max-products-key",
                "[{\"query\":\"(global_attr_age_group:(\\\"Adults-Men\\\" OR \\\"Adults-Unisex\\\") AND global_attr_article_type_facet:(\\\" Cufflinks and Pocket Square\\\" OR \\\"Belts\\\" OR \\\"Cufflinks\\\" OR \\\"Suspenders\\\" OR \\\"Tie+Cufflink+Pocket square - Combo Pack\\\" OR \\\"Ties\\\" OR \\\"Ties\\\" OR \\\"Ties and Cufflinks\\\"))\",\"start\":",
                ",\"rows\":96,\"facetField\":[],\"fq\":[\"count_options_availbale:[1 TO *]\"],\"sort\":[{\"sort_field\":\"count_options_availbale\",\"order_by\":\"desc\"},{\"sort_field\":\"style_store21_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"potential_revenue_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"global_attr_catalog_add_date\",\"order_by\":\"desc\"}],\"return_docs\":true,\"colour_grouping\":true,\"facet\":true}]"
        );
        productGroups.add(productGroup);
        // Jewellery //
        productGroup = new ProductGroup(
                "Jewellery",
                "men-accessories-jewellery",
                "men-accessories-jewellery-file",
                "men-accessories-jewellery-start-key",
                "men-accessories-jewellery-max-products-key",
                "[{\"query\":\"(global_attr_age_group:(\\\"Adults-Men\\\" OR \\\"Adults-Unisex\\\") AND global_attr_article_type_facet:(\\\"Anklet\\\" OR \\\"Bangle\\\" OR \\\"Bracelet\\\" OR \\\"Earring & Pendant Set\\\" OR \\\"Earrings\\\" OR \\\"Jewellery\\\" OR \\\"Jewellery Set\\\" OR \\\"Key chain\\\" OR \\\"Necklace\\\" OR \\\"Pendant\\\" OR \\\"Ring\\\"))\",\"start\":",
                ",\"rows\":96,\"facetField\":[],\"fq\":[\"count_options_availbale:[1 TO *]\"],\"sort\":[{\"sort_field\":\"count_options_availbale\",\"order_by\":\"desc\"},{\"sort_field\":\"style_store21_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"potential_revenue_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"global_attr_catalog_add_date\",\"order_by\":\"desc\"}],\"return_docs\":true,\"colour_grouping\":true,\"facet\":true}]"
        );
        productGroups.add(productGroup);
        // Sunglasses //
        productGroup = new ProductGroup(
                "Sunglasses",
                "men-accessories-sunglasses",
                "men-accessories-sunglasses-file",
                "men-accessories-sunglasses-start-key",
                "men-accessories-sunglasses-max-products-key",
                "[{\"query\":\"(global_attr_age_group:(\\\"Adults-Men\\\" OR \\\"Adults-Unisex\\\") AND global_attr_sub_category:(\\\"Eyewear\\\"))\",\"start\":",
                ",\"rows\":96,\"facetField\":[],\"fq\":[\"count_options_availbale:[1 TO *]\"],\"sort\":[{\"sort_field\":\"count_options_availbale\",\"order_by\":\"desc\"},{\"sort_field\":\"style_store21_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"potential_revenue_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"global_attr_catalog_add_date\",\"order_by\":\"desc\"}],\"return_docs\":true,\"colour_grouping\":true,\"facet\":true}]"
        );
        productGroups.add(productGroup);
        productHeadGroups.add(new ProductHeadGroup("Accessories", productGroups, false));

        return productHeadGroups;
    }

    public static List<ProductHeadGroup> generateMyntraCategoriesForSingleELV(Context context){
        List<ProductHeadGroup> productHeadGroups = new ArrayList<ProductHeadGroup>();

        productHeadGroups.add(new ProductHeadGroup("Men", new ArrayList<ProductGroup>(), true));
        productHeadGroups.addAll(generateMyntraMenProductGroups(context));
        return productHeadGroups;
    }
}
