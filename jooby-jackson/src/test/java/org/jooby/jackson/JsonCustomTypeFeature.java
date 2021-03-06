package org.jooby.jackson;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.jooby.MediaType;
import org.jooby.test.ServerFeature;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.TypeLiteral;

@SuppressWarnings("unchecked")
public class JsonCustomTypeFeature extends ServerFeature {

  {

    use(new Json().types(MediaType.valueOf("application/vnd.github.v3+json")));

    get("/members",
        req -> Lists.newArrayList(ImmutableMap.<String, Object> of("id", 1, "name", "pablo")));

    post("/members", req -> {
      List<Map<String, Object>> members = req.body(new TypeLiteral<List<Map<String, Object>>>() {
      });
      return members;
    });
  }

  @Test
  public void get() throws URISyntaxException, Exception {
    request()
        .get("/members")
        .expect(200)
        .header("Content-Type", "application/vnd.github.v3+json;charset=UTF-8");
  }

  @Test
  public void post() throws URISyntaxException, Exception {
    request()
        .post("/members")
        .body("[{\"id\":1,\"name\":\"vilma\"}]", "application/vnd.github.v3+json")
        .expect(200)
        .expect("[{\"id\":1,\"name\":\"vilma\"}]")
        .header("Content-Type", "application/vnd.github.v3+json;charset=UTF-8");
  }

  @Test
  public void err415() throws URISyntaxException, Exception {
    request()
        .post("/members")
        .body("[{\"id\":1,\"name\":\"vilma\"}]", "application/json")
        .expect(415);
  }

}
