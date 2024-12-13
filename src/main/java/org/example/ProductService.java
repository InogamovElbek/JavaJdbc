package org.example;
import java.sql.SQLException;
import java.util.List;
public class ProductService {
    private final ProductDAO productDAO;
    public ProductService() {
        this.productDAO = new ProductDAO();
    }
    public List<Product> getAllProducts() throws SQLException {
        return productDAO.getAllProducts();
    }
    public void addProduct(Product product) throws SQLException {
        productDAO.addProduct(product);
    }
    public void deleteProduct(int productId) throws SQLException {
        productDAO.deleteProduct(productId);
    }
    public void updateProduct(Product product) throws SQLException {
        productDAO.updateProduct(product);
    }
}


