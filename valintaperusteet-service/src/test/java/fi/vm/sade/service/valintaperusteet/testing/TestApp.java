package fi.vm.sade.service.valintaperusteet.testing;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableWebSecurity(debug = true)
public class TestApp {
  public static void startTestApp() {
    startTestApp(null);
  }

  private static void startTestApp(final Integer port) {
    SpringApplication.run(TestApp.class);
    if (port != null) {
      System.setProperty("TestApp.server.port", String.valueOf(port));
      System.setProperty(
          "TestApp.server.rootUrl",
          String.format("http://localhost:%d/valintaperusteet-service/resources", port));
    } else {
      final Integer serverPort =
          (Integer) ApplicationContextGetter.applicationContext.getBean("serverPort");
      System.setProperty("TestApp.server.port", String.valueOf(serverPort));
      System.setProperty(
          "TestApp.server.rootUrl",
          String.format("http://localhost:%d/valintaperusteet-service/resources", serverPort));
    }
  }

  public static void stopTestApp() {
    if (ApplicationContextGetter.applicationContext != null) {
      ((ConfigurableApplicationContext) ApplicationContextGetter.applicationContext).close();
    }
  }

  public static void main(String[] args) {
    startTestApp(9080);
  }

  public static class ApplicationContextGetter implements ApplicationContextAware {
    public static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      ApplicationContextGetter.applicationContext = applicationContext;
    }
  }
}
