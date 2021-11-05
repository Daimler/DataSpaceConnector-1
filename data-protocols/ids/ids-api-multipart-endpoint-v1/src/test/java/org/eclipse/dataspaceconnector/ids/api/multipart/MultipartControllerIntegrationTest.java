package org.eclipse.dataspaceconnector.ids.api.multipart;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import org.eclipse.dataspaceconnector.junit.launcher.EdcExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(EdcExtension.class)
public class MultipartControllerIntegrationTest extends AbstractMultipartControllerIntegrationTest {

    @Override
    protected Map<String, String> getSystemProperties() {
        return new HashMap<>() {
            {
                put("web.http.port", String.valueOf(getPort()));
            }
        };
    }

    @Test
    void test() {
        RestAssured.given()
                .header(new Header("content-type", "multipart/form-data"))
                //.multiPart("file", new File("./src/main/resources/test.txt"))
                //.formParam("description", "This is my doc")
                .when()
                .post(getUrl())
                .then()
                .log();
//                    .assertThat()
//                    .body(matchesJsonSchemaInClasspath("schemas/members/member-document.json"));
    }
}
