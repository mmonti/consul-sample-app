package com.mmonti.consulsample.config;

import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomConfigFileApplicationListener extends ConfigFileApplicationListener {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Resource path = new ClassPathResource("bootstrap.yml");
        PropertySource<?> propertySource = loadYaml(path);
        environment.getPropertySources().addLast(propertySource);

        environment.addActiveProfile("test");

        super.postProcessEnvironment(environment, application);
//        addPropertySources(environment, application.getResourceLoader());
//        configureIgnoreBeanInfo(environment);
//        bindToSpringApplication(environment, application);
    }

    private void configureIgnoreBeanInfo(ConfigurableEnvironment environment) {
        if (System.getProperty(CachedIntrospectionResults.IGNORE_BEANINFO_PROPERTY_NAME) == null) {
            RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(environment, "spring.beaninfo.");
            Boolean ignore = resolver.getProperty("ignore", Boolean.class, Boolean.TRUE);
            System.setProperty(CachedIntrospectionResults.IGNORE_BEANINFO_PROPERTY_NAME, ignore.toString());
        }
    }


    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    private PropertySource<?> loadYaml(Resource path) {
        if (!path.exists()) {
            throw new IllegalArgumentException("Resource " + path + " does not exist");
        }
        try {
            loader.load("test", new CustomURLResource("http://localhost:8500/v1/kv/config/md-processor/data"), null);
            return this.loader.load("custom-resource", path, null);
        }
        catch (IOException ex) {
            throw new IllegalStateException(
                    "Failed to load yaml configuration from " + path, ex);
        }
    }

    class CustomURLResource extends UrlResource {

        public CustomURLResource(URI uri) throws MalformedURLException {
            super(uri);
        }

        public CustomURLResource(URL url) {
            super(url);
        }

        public CustomURLResource(String path) throws MalformedURLException {
            super(path);
        }

        public CustomURLResource(String protocol, String location) throws MalformedURLException {
            super(protocol, location);
        }

        @Override
        public InputStream getInputStream() throws IOException {
            RestTemplate restTemplate = new RestTemplate();
            try {
                Object m = restTemplate.getForObject(new URI("http://localhost:8500/v1/kv/config/de-md-processor/data"), Object.class);
                System.out.println(m);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return new StringBufferInputStream("");
        }

        public CustomURLResource(String protocol, String location, String fragment) throws MalformedURLException {
            super(protocol, location, fragment);
        }
    }
}
