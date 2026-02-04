package ck.test.demo.pojo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProductResponse {
    Long id;
    String name;
    String description;
    BigDecimal price;
    Integer stock;
    String category;
    LocalDateTime createdAt;




}
