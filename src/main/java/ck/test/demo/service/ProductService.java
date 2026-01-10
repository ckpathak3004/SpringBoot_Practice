package ck.test.demo.service;

import ck.test.demo.pojo.Product;
import ck.test.demo.pojo.ProductResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(@Valid Product request);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
}