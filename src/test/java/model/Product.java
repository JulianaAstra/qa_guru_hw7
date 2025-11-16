package model;

public class Product {   
    private int productId;
    private String productName;
    private String productColor;
    private int productAmount;
    private double productPrice;

    public int getProductId() { return productId; }

    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }

    public void setProductName(String productName) { this.productName = productName; }

    public String getProductColor() { return productColor; }

    public void setProductColor(String productColor) { this.productColor = productColor; }

    public int getProductAmount() { return productAmount; }

    public void setProductAmount(int productAmount) { this.productAmount = productAmount; }

    public double getProductPrice() { return productPrice; }

    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }
}
