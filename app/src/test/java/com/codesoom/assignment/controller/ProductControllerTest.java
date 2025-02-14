package com.codesoom.assignment.controller;

import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.domain.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {
    private static final Long PRODUCT_ID = 1L;
    private static final String PRODUCT_NAME = "고양이 낚시대";
    private static final String UPDATE_PREFIX = "NEW ";
    private static final String PRODUCT_MAKER = "애옹이네 장난감";
    private static final int PRODUCT_PRICE = 5000;
    private static final String PRODUCT_IMAGE_URL = "http://image.kyobobook.co.kr/newimages/giftshop_new/goods/400/1095/hot1602809707085.jpg";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(PRODUCT_ID)
                .name(PRODUCT_NAME)
                .maker(PRODUCT_MAKER)
                .price(PRODUCT_PRICE)
                .imageUrl(PRODUCT_IMAGE_URL)
                .build();
    }

    @Nested
    @DisplayName("GET /products 요청 시")
    class Describe_get_products {
        private List<Product> products;

        @BeforeEach
        void setUp() {
            products = new ArrayList<>();
        }

        @Nested
        @DisplayName("만약 1개의 product가 저장된 경우")
        class Context_if_one_product_stored {
            @BeforeEach
            void setUp() {
                products.add(product);

                given(productService.getProducts()).willReturn(products);
            }

            @Nested
            @DisplayName("1개의 Product가 저장되어있는 리스트를 반환한다")
            class It_returns_list_contains_one_product {
                ResultActions subject() throws Exception {
                    return mockMvc.perform(get("/products"));
                }

                @Test
                void test() throws Exception {
                    subject().andExpect(status().isOk())
                            .andExpect(jsonPath("$[0].id").value(PRODUCT_ID))
                            .andExpect(jsonPath("$[0].name").value(PRODUCT_NAME))
                            .andExpect(jsonPath("$[0].maker").value(PRODUCT_MAKER))
                            .andExpect(jsonPath("$[0].price").value(PRODUCT_PRICE));
                }
            }
        }

        @Nested
        @DisplayName("만약 product가 저장되지 않은 경우")
        class Context_if_no_product_stored {
            @BeforeEach
            void setUp() {
                given(productService.getProducts()).willReturn(products);
            }

            @Nested
            @DisplayName("비어있는 리스트를 반환한다")
            class It_returns_empty_list {
                ResultActions subject() throws Exception {
                    return mockMvc.perform(get("/products"));
                }

                @Test
                void test() throws Exception {
                    subject().andExpect(status().isOk());
                }
            }
        }
    }

    @Nested
    @DisplayName("GET /products/{id} 요청 시")
    class Describe_get_products_by_id {
        @Nested
        @DisplayName("만약 유효한 id가 주어진 경우")
        class Context_if_valid_id_given {
            @BeforeEach
            void setUp() {
                given(productService.getProduct(PRODUCT_ID)).willReturn(product);
            }

            @Nested
            @DisplayName("product를 반환한다")
            class It_returns_product {
                ResultActions subject() throws Exception {
                    return mockMvc.perform(get("/products/{id}", PRODUCT_ID));
                }

                @Test
                void test() throws Exception {
                    subject().andExpect(status().isOk())
                            .andExpect(jsonPath("$.id").value(PRODUCT_ID))
                            .andExpect(jsonPath("$.name").value(PRODUCT_NAME))
                            .andExpect(jsonPath("$.maker").value(PRODUCT_MAKER))
                            .andExpect(jsonPath("$.price").value(PRODUCT_PRICE));
                }
            }
        }
    }

    @Nested
    @DisplayName("POST /products 요청 시")
    class Describe_post_products {
        @Nested
        @DisplayName("유효한 product가 주어졌을 경우")
        class Context_if_valid_product_given {
            @BeforeEach
            void setUp() {
                given(productService.addProduct(any())).willReturn(product);
            }

            @Nested
            @DisplayName("product를 반환한다")
            class It_returns_product {
                ResultActions subject() throws Exception {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonRequest = objectMapper.writeValueAsString(product);

                    return mockMvc.perform(post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest));
                }

                @Test
                void test() throws Exception {
                    subject().andExpect(status().isCreated());
                }
            }
        }
    }

    @Nested
    @DisplayName("PUT /products/{id} 요청 시")
    class Describe_put_products_by_id {
        @Nested
        @DisplayName("만약 유효한 id와 product가 주어졌을 경우")
        class Context_if_valid_id_and_product_given {
            @BeforeEach
            void setUp() {
                product = Product.builder()
                        .name(UPDATE_PREFIX + PRODUCT_NAME)
                        .build();

                given(productService.updateProduct(eq(PRODUCT_ID), any())).willReturn(product);
            }

            @Nested
            @DisplayName("수정된 product를 반환한다")
            class It_returns_updated_product {
                ResultActions subject() throws Exception {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonRequest = objectMapper.writeValueAsString(product);

                    return mockMvc.perform(put("/products/{id}", PRODUCT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest));
                }

                @Test
                void test() throws Exception {
                    subject().andExpect(status().isOk())
                            .andExpect(jsonPath("$.name").value(UPDATE_PREFIX + PRODUCT_NAME));
                }
            }
        }
    }

    @Nested
    @DisplayName("PATCH /products/{id} 요청 시")
    class Describe_patch_products_by_id {
        @Nested
        @DisplayName("만약 유효한 id와 product가 주어졌을 경우")
        class Context_if_valid_id_and_product_given {
            @BeforeEach
            void setUp() {
                product = Product.builder()
                        .name(UPDATE_PREFIX + PRODUCT_NAME)
                        .build();

                given(productService.updateProduct(eq(PRODUCT_ID), any())).willReturn(product);
            }

            @Nested
            @DisplayName("수정된 product를 반환한다")
            class It_returns_updated_product {
                ResultActions subject() throws Exception {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonRequest = objectMapper.writeValueAsString(product);

                    return mockMvc.perform(patch("/products/{id}", PRODUCT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest));
                }

                @Test
                void test() throws Exception {
                    subject().andExpect(status().isOk())
                            .andExpect(jsonPath("$.name").value(UPDATE_PREFIX + PRODUCT_NAME));
                }
            }
        }
    }
}
