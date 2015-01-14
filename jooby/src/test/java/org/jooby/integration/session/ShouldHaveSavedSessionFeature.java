package org.jooby.integration.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.jooby.Session;
import org.jooby.integration.FilterFeature.HttpResponseValidator;
import org.jooby.test.ServerFeature;
import org.junit.Test;

public class ShouldHaveSavedSessionFeature extends ServerFeature {

  private static final CountDownLatch latch = new CountDownLatch(1);

  {
    use(new Session.MemoryStore() {

      @Override
      public void create(final Session session) {
        super.create(session);
        latch.countDown();
      }
    });

    get("/shouldHaveSavedSession", req -> {
      req.session().set("k1", "v1");
      return req.session().get("k1").get();
    });
  }

  @Test
  public void shouldHaveSavedSession() throws Exception {
    assertEquals("v1", execute(GET(uri("shouldHaveSavedSession")), r0 -> {
      assertEquals(200, r0.getStatusLine().getStatusCode());
      assertNotNull(r0.getFirstHeader("Set-Cookie"));
    }));

    latch.await();
  }

  private static Request GET(final URIBuilder uri) throws Exception {
    return Request.Get(uri.build());
  }

  private static String execute(final Request request, final HttpResponseValidator validator)
      throws Exception {
    HttpResponse resp = Executor.newInstance().execute(request).returnResponse();
    validator.validate(resp);
    return EntityUtils.toString(resp.getEntity());
  }

}