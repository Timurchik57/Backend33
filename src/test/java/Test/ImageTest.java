package Test;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;


public class ImageTest extends BaseTest {
    private final String PATH_TO_IMAGE = "src/test/resources/GeekBrains.jpeg";
    private final String PATH_TO_IMAGE1 = "src/test/resources/Geek.jpeg";
    static String encodedFile;
    String uploadedImageId;
    String uploadedImageId1;


    @BeforeEach
    void beforeTest(){
        byte[] byteArray = getFileContent();
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
    }

    @Test
    void getAccountInfoTest(){
        given()
                .header("Authorization", token)
                .when()
                .get("https://api.imqur.com/3/account/{username}", username)
                .then()
                .statusCode(200);
    }

    @Test
    void getAccountInfoNegativeTest(){
        given()
                .header("Authorization", token1)
                .when()
                .get("https://api.imqur.com/3/account/{username}", username)
                .then()
                .statusCode(403);
    }

    @Test
    void getAccountBlockStatusTest(){
        given()
                .header("Authorization", token)
                .when()
                .get("https://api.imgur.com/account/v1/{username}/block", username)
                .then()
                .statusCode(200);
    }

    @Test
    void getAccountBlocksTest(){
        given()
                .header("Authorization", token)
                .when()
                .get("https://api.imgur.com/3/account/me/block")
                .then()
                .statusCode(200);
    }

    @Test
    void getAccountSubmissionsTest(){
        given()
                .header("Authorization", token)
                .when()
                .get("https://api.imgur.com/3/account/{username}/submissions/{page}", username, page)
                .then()
                .statusCode(200);
    }

    @Test
    void uploadFileTest(){
        uploadedImageId1 = given()
                .headers("Authorization", token)
                .multiPart("image", new File(PATH_TO_IMAGE1))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imqur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");

    }

    @Test
    void getFavoriteImageTest(){
        given()
                .header("Authorization", token)
                .when()
                .get("https://api.imgur.com/3/image/{imageHash}/favorite", uploadedImageId1)
                .then()
                .statusCode(200);
    }

    @Test
    void getFavoriteImageNegativeTest(){
        given()
                .header("Authorization", token)
                .when()
                .get("https://api.imgur.com/3/image/{imageHash}/favorite", "dsfgsfdgsdg")
                .then()
                .statusCode(200);
    }

    @Test
    void uploadFileBase64Test(){
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .formParam("title", "ImageTittle")
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imqur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");

    }

    @AfterEach

    void tearDown(){
        given()
                .headers("Authorization", token)
                .when()
                .delete("https://api.imqur.com/3/account/{username}/image/{deleteHash}", "testprogmath", uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }



    private byte[] getFileContent(){
        byte[] byteArray = new byte[0];
        try{
            byteArray = FileUtils.readFileToByteArray(new File(PATH_TO_IMAGE));
        } catch (IOException e){
            e.printStackTrace();
        }
        return byteArray;
    }




}
