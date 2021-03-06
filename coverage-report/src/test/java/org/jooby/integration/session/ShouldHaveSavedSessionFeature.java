package org.jooby.integration.session;

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.CountDownLatch;

import org.jooby.Session;
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
    request()
        .get("/shouldHaveSavedSession")
        .expect("v1")
        .header("Set-Cookie", setCookie -> assertNotNull(setCookie));

    latch.await();
  }

}
