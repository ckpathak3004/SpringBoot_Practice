package ck.test.demo.service;

import ck.test.demo.Product;
import ck.test.demo.ProductResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(@Valid Product request);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
}