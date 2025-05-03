package sv.sinai.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.sinai.server.entities.Product;
import sv.sinai.server.repositories.IProductRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final IProductRepository productRepository;

    @Autowired
    public ProductService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get product by id
    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    // Get product by name
    public Optional<Product> getProductByName(String name) {
        return productRepository.findByName(name);
    }

    // Create product
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // Update product
    public Optional<Product> updateProduct(Integer id, Product productDetails) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(productDetails.getName());
                    product.setUpdatedAt(Instant.now());
                    return productRepository.save(product);
                });
    }

    // Delete product
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }
}
