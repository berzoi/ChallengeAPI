package tests;

import static api.Specifications.installSpecification;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import api.Specifications;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import pojo.CreateUpdateUser;
import pojo.CreateUpdateUserResponse;
import pojo.Register;
import pojo.SuccessReg;
import pojo.UnSuccessReg;
import pojo.UserData;

public class ReqresTest {

  private final static String URL = "https://reqres.in";

  @Test
  public void successRegTest() {
    installSpecification(Specifications.requestSpecification(URL), Specifications.responseSpecification(200));

    Integer id = 4;
    String token = "QpwL5tke4Pnpja7X4";

    Register user = new Register("eve.holt@reqres.in", "pistol");

    SuccessReg successReg = given()
        .body(user)
        .when()
        .post("/api/register")
        .then().log().all()
        .extract().as(SuccessReg.class);

    assertNotNull(successReg.getId());
    assertNotNull(successReg.getToken());
    assertEquals(id, successReg.getId());
    assertEquals(token, successReg.getToken());
  }

  @Test
  public void unSuccessRegTest() {

    installSpecification(Specifications.requestSpecification(URL), Specifications.responseSpecification(400));

    Register user = new Register("sydney@fife", "");

    UnSuccessReg unSuccessReg = given()
        .body(user)
        .when()
        .post("/api/register")
        .then().log().all()
        .extract().as(UnSuccessReg.class);

    assertEquals("Missing password", unSuccessReg.getError());
  }

  @Test
  public void checkAvatarAndIdTest() {

    installSpecification(Specifications.requestSpecification(URL), Specifications.responseSpecification(200));

    List<UserData> users = given()
        .when()
        .get("/api/users?page=2")
        .then().log().all()
        .extract().body().jsonPath().getList("data", UserData.class);

    assertFalse(users.isEmpty());

    users.forEach(x -> assertTrue(x.getAvatar().contains(x.getId().toString())));
    assertTrue(users.stream().allMatch(x -> x.getEmail().endsWith("@reqres.in")));

    List<String> avatars = users.stream()
        .map(UserData::getAvatar)
        .toList();

    List<String> ids = users.stream()
        .map(x -> x.getId().toString())
        .toList();

    for (int i = 0; i < avatars.size(); i++) {
      assertTrue(avatars.get(i).contains(ids.get(i)));
    }
  }

  @Test
  public void createUserTest() {

    installSpecification(Specifications.requestSpecification(URL), Specifications.responseSpecification(201));

    CreateUpdateUser newUser = new CreateUpdateUser("morpheus", "leader");

    CreateUpdateUserResponse createUserResponse = given()
        .body(newUser)
        .when()
        .post("/api/users")
        .then().log().all()
        .body("id", notNullValue())
        .extract()
        .as(CreateUpdateUserResponse.class);

    assertEquals(newUser.getName(), createUserResponse.getName());
    assertEquals(newUser.getJob(), createUserResponse.getJob());
  }

  @Test
  public void testUpdateUser() {
    installSpecification(Specifications.requestSpecification(URL), Specifications.responseSpecification(200));

    CreateUpdateUser updatedUser = new CreateUpdateUser("morpheus", "zion resident");

    CreateUpdateUserResponse createUserResponse = given()
        .body(updatedUser)
        .when()
        .put("/api/users/2")
        .then().log().all()
        .extract()
        .as(CreateUpdateUserResponse.class);

    ZonedDateTime currentTimeTruncated = ZonedDateTime.parse(Clock.systemUTC().instant().toString()).truncatedTo(ChronoUnit.MINUTES);
    ZonedDateTime expectedTimeTruncated = ZonedDateTime.parse(createUserResponse.getUpdatedAt()).truncatedTo(ChronoUnit.MINUTES);

    assertEquals(updatedUser.getName(), createUserResponse.getName());
    assertEquals(updatedUser.getJob(), createUserResponse.getJob());
    assertEquals(currentTimeTruncated, expectedTimeTruncated);

  }

  @Test
  public void deleteUserTest() {

    installSpecification(Specifications.requestSpecification(URL), Specifications.responseSpecification(204));

    given()
        .when()
        .delete("/api/users/2")
        .then().log().all();
  }
}
