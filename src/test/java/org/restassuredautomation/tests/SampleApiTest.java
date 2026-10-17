package org.restassuredautomation.tests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.restassuredautomation.BaseTest;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class SampleApiTest extends BaseTest {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Test
    public void testGetProducts() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/api/products")
                .then()
                .spec(responseSpec)
                .extract()
                .response();

        assertThat(response.statusCode()).isEqualTo(200);

        // Capture array of items from response
        List<Map<String, Object>> products = response.jsonPath().getList("");

        assertThat(products).isNotEmpty();

        // Use JsonPath assertions on first product
        assertThat(products.get(0).get("product_id")).isEqualTo(1);
        assertThat(products.get(0).get("name")).isEqualTo("Smartphone");
        assertThat(products.get(0).get("description")).isEqualTo("High-end smartphone with advanced features.");
        assertThat(products.get(0).get("price")).isEqualTo(599.99f);
        assertThat(products.get(0).get("unit")).isEqualTo("Piece");
        assertThat(products.get(0).get("image")).isEqualTo("https://example.com/images/smartphone.jpg");
        assertThat(products.get(0).get("discount")).isEqualTo(10);
        assertThat(products.get(0).get("availability")).isEqualTo(true);
        assertThat(products.get(0).get("brand")).isEqualTo("BrandX");
        assertThat(products.get(0).get("category")).isEqualTo("Electronics");
        assertThat(products.get(0).get("rating")).isEqualTo(4.5f);

        List<Map<String, Object>> reviews = (List<Map<String, Object>>) products.get(0).get("reviews");
        assertThat(reviews).hasSize(2);

        Map<String, Object> review1 = reviews.get(0);
        assertThat(review1.get("user_id")).isEqualTo(1);
        assertThat(review1.get("rating")).isEqualTo(5);
        assertThat(review1.get("comment")).isEqualTo("Great phone with a superb camera!");

        Map<String, Object> review2 = reviews.get(1);
        assertThat(review2.get("user_id")).isEqualTo(2);
        assertThat(review2.get("rating")).isEqualTo(4);
        assertThat(review2.get("comment")).isEqualTo("Good performance, but the battery life could be better.");
    }

    @Test
    public void testGetUsers(){
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/api/users")
                .then()
                .spec(responseSpec)
                .extract()
                .response();

        assertThat(response.statusCode()).isEqualTo(200);

        List<Map<String, Object>> users = response.jsonPath().getList("");

        assertThat(users).isNotEmpty();

        for(Map<String, Object> user : users) {
            assertThat(user.get("user_id")).isInstanceOf(Number.class);
            assertThat(((Number) user.get("user_id")).intValue()).isPositive();

            assertThat(user.get("username")).isInstanceOf(String.class);
            assertThat(((String) user.get("username"))).isNotEmpty();

            assertThat(user.get("email")).isInstanceOf(String.class);
            assertThat(((String) user.get("email"))).matches(EMAIL_PATTERN.pattern());

            assertThat(user.get("password")).isInstanceOf(String.class);
            assertThat(((String) user.get("password"))).isNotEmpty();
        }

        // Assert first user fields
/*        assertThat(users.get(0).get("user_id")).isEqualTo(1);
        assertThat(users.get(0).get("username")).isEqualTo("john_doe");
        assertThat(users.get(0).get("email")).isEqualTo("john@example.com");
        assertThat(users.get(0).get("password")).isEqualTo("hashed_password");*/
    }

    @Test
    public void testCarts(){

        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/api/carts")
                .then()
                .spec(responseSpec)
                .extract()
                .response();

        System.out.println("Response : "+ response.getBody().asString());
        JsonPath jsonPath = new JsonPath(response.asString());
        System.out.println("cartId : "+ jsonPath.getInt("[0].cart_id"));
        System.out.println("userId : "+ jsonPath.getInt("[0].user_id"));
        List<Map<String,Object>> items1 = jsonPath.getList("[0].items");
        System.out.println("items : "+ items1);
        for(Map<String,Object> item : items1){
            System.out.println("productId : "+ item.get("product_id"));
            System.out.println("quantity : "+ item.get("quantity"));
        }

        List<Map<String,Object>> carts = response.getBody().jsonPath().getList("");

        for(Map<String,Object> cart : carts){
            assertThat(cart.get("cart_id")).isInstanceOf(Number.class);
            assertThat(((Number) cart.get("cart_id")).intValue()).isPositive();

            assertThat(cart.get("user_id")).isInstanceOf(Number.class);
            assertThat(((Number) cart.get("user_id")).intValue()).isPositive();

            List<Map<String,Object>> items = (List<Map<String,Object>>) cart.get("items");
            for(Map<String,Object> item : items){
                assertThat(item.get("product_id")).isInstanceOf(Number.class);
                assertThat(((Number) item.get("product_id")).intValue()).isPositive();

                assertThat(item.get("quantity")).isInstanceOf(Number.class);
                assertThat(((Number) item.get("quantity")).intValue()).isPositive();
            }
        }
    }
}
