package model;
import java.util.List;

public class ProductsList {
    private String shopName;
    private List<Product> products;
    
    public String getShopName() { return shopName; }

    public void setShopName(String shopName) { this.shopName = shopName; }
    
    public List<Product> getProducts() { return products; }
    
    public void setProducts(List<Product> products) { this.products = products; }
}