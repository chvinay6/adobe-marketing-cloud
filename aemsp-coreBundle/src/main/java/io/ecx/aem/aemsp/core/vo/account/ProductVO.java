package io.ecx.aem.aemsp.core.vo.account;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

public class ProductVO
{
    int id;
    String productId;
    String name;
    String description;
    Date creationTime;
    Date lastChangeTime;
    boolean active;
    List<ProductAttributeVO> productAttributes;
    String price;
    String platform;
    String[] catalogs;
    List<CatalogGroupVO> catalogGroups;

    public ProductVO(JSONObject productObject) throws JSONException
    {
        readValues(productObject);

        final JSONArray productAtts = productObject.getJSONArray("productAttributes");
        productAttributes = new ArrayList<>();
        for (int i = 0; i < productAtts.length(); i++)
        {
            productAttributes.add(new ProductAttributeVO(productAtts.getJSONObject(i)));
        }

        final JSONArray catalogGrps = productObject.getJSONArray("catalogGroups");
        catalogGroups = new ArrayList<>();
        for (int i = 0; i < catalogGrps.length(); i++)
        {
            catalogGroups.add(new CatalogGroupVO(catalogGrps.getJSONObject(i)));
        }

    }

    private void readValues(JSONObject productObject) throws JSONException
    {
        id = productObject.getInt("id");
        productId = productObject.getString("productId");
        name = productObject.getString("name");
        description = productObject.getString("description");
        creationTime = new Date(productObject.getLong("creationTime"));
        lastChangeTime = new Date(productObject.getLong("lastChangeTime"));
        active = productObject.getBoolean("active");
        price = productObject.getString("price");
        platform = productObject.getString("platform");
        //catalogs = (String[]) productObject.get("catalogs");
    }

    public int getId()
    {
        return id;
    }

    public String getProductId()
    {
        return productId;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public Date getCreationTime()
    {
        return creationTime;
    }

    public Date getLastChangeTime()
    {
        return lastChangeTime;
    }

    public boolean isActive()
    {
        return active;
    }

    public List<ProductAttributeVO> getProductAttributes()
    {
        return productAttributes;
    }

    public String getPrice()
    {
        return price;
    }

    public String getPlatform()
    {
        return platform;
    }

    public String[] getCatalogs()
    {
        return catalogs;
    }

    public List<CatalogGroupVO> getCatalogGroups()
    {
        return catalogGroups;
    }

}
