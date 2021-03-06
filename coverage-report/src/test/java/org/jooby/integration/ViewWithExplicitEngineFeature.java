package org.jooby.integration;

import org.jooby.Body.Writer;
import org.jooby.View;
import org.jooby.test.ServerFeature;
import org.junit.Test;

public class ViewWithExplicitEngineFeature extends ServerFeature {

  {

    use(new View.Engine() {

      @Override
      public String name() {
        return "hbs";
      }

      @Override
      public void render(final View viewable, final Writer writer) throws Exception {
        writer.text(w -> w.write(name()));
      }
    });

    use(new View.Engine() {

      @Override
      public String name() {
        return "freemarker";
      }

      @Override
      public void render(final View viewable, final Writer writer) throws Exception {
        writer.text(w -> w.write(name()));
      }
    });

    get("/:engine", (req, rsp) -> {
      String engine = req.param("engine").value();
      rsp.send(View.of("view", new Object()).engine(engine));
    });

  }

  @Test
  public void hbs() throws Exception {
    request()
        .get("/hbs")
        .expect("hbs");
  }

  @Test
  public void freemarker() throws Exception {
    request()
        .get("/freemarker")
        .expect("freemarker");
  }

}
