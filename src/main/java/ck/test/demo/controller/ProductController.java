package ck.test.demo.controller;

import ck.test.demo.pojo.Product;
import ck.test.demo.pojo.ProductResponse;
import ck.test.demo.service.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequestMapping("/api/products")
    public class ProductController {

        private final ProductService productService;

        public ProductController(ProductService productService) {
            this.productService = productService;
        }

        // POST - Create a new product by accepting JSON
        @PostMapping(value = "/create")
        public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody Product request) {
            ProductResponse product = productService.createProduct(request);
            return new ResponseEntity<>(product, HttpStatus.CREATED);
        }

        // Optional: GET all products
        @PreAuthorize("hasAnyRole('ROLE_USER'}")
        @GetMapping(value = "getProductList")
        public ResponseEntity<List<ProductResponse>> getAllProducts() {
            return ResponseEntity.ok(productService.getAllProducts());
        }

        // Optional: GET by ID

        @GetMapping("/read/{id}")
        @PreAuthorize("hasAnyRole('ROLE_USER'}")
        public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
            return ResponseEntity.ok(productService.getProductById(id));
        }
}

