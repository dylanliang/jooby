package org.jooby.integration.ws;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;

import org.jooby.test.ServerFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.websocket.WebSocket;
import com.ning.http.client.websocket.WebSocketListener;
import com.ning.http.client.websocket.WebSocketUpgradeHandler;

public class OnCloseFeature extends ServerFeature {

  private static volatile CountDownLatch closeLatch;

  {
    ws("/onClose", ws -> {

      ws.onClose(status -> {
        assertNotNull(status);
        // undertow vs jetty
        assertTrue(status.code() == 1000 || status.code() == 1005);
        assertNull(status.reason());
        closeLatch.countDown();
      });
    });

  }

 private AsyncHttpClient client;

  @Before
  public void before() {
    client = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build());
  }

  @After
  public void after() {
    client.close();
  }

  @Test
  public void sendClose() throws Exception {
    closeLatch = new CountDownLatch(1);

    client.prepareGet(ws("onClose").toString())
        .execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(
            new WebSocketListener() {

              @Override
              public void onOpen(final WebSocket websocket) {
                websocket.close();
              }

              @Override
              public void onClose(final WebSocket websocket) {
              }

              @Override
              public void onError(final Throwable t) {
              }
            }).build()).get();

    closeLatch.await();
  }

}
