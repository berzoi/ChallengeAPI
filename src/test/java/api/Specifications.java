package api;

import static io.restassured.RestAssured.*;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class Specifications {

  public static RequestSpecification requestSpecification(String uri) {
    return new RequestSpecBuilder()
        .addHeader("x-api-key", "reqres-free-v1")
        .setBaseUri(uri)
        .setContentType(ContentType.JSON)
        .build();
  }

  public static ResponseSpecification responseSpecification (int statusCode) {
    return new ResponseSpecBuilder()
        .expectStatusCode(statusCode)
        .build();
  }

  public static void installSpecification(RequestSpecification requestSpec, ResponseSpecification responseSpec) {
    requestSpecification = requestSpec;
    responseSpecification = responseSpec;
  }
}
