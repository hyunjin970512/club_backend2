package kr.co.koreazinc.spring;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomYamlPropertiesInitializer implements EnvironmentPostProcessor {

    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        log.debug("** START CustomYamlPropertiesInitializer **");
        ResourceLoader resourceLoader = Optional
                .ofNullable(application.getResourceLoader())
                .orElseGet(DefaultResourceLoader::new);

        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(resourceLoader);

        Resource[] resources = new Resource[]{};
        try {
            resources = resourcePatternResolver.getResources("classpath*:config/custom-*.yaml");
        } catch(IOException e) {
            log.warn(e.getMessage(), e);
        }

        String[] activeProfiles = environment.getActiveProfiles();
        for (String activeProfile : activeProfiles) {
            Stream.of(resources)
                .filter(resource->{
                    return Optional.ofNullable(resource.getFilename())
                        .map(name->name.endsWith(String.format("-%s.yaml", activeProfile)) || name.endsWith(String.format("-%s.yml", activeProfile)))
                        .orElse(false);
                })
                .sorted(new ResourceComparator())
                .forEach(resource->{
                    loadYaml(resource).forEach(propertySource -> {
                        environment.getPropertySources().addLast(propertySource);
                    });
                });
        }
        log.debug("** END CustomYamlPropertiesInitializer **");
    }

    private static class ResourceComparator implements Comparator<Resource> {

        @Override
        public int compare(Resource ps1, Resource ps2) {
            if (compare(ps1.getFilename(), ps2.getFilename())) {
                return 1;
            }
            if (compare(ps2.getFilename(), ps1.getFilename())) {
                return -1;
            }
            return 0;
        }

        private boolean compare(String name1, String name2) {
            return name1.contains("core") && !name2.contains("core");
        }
    }

    private List<PropertySource<?>> loadYaml(Resource path) {
        if (!path.exists()) {
            throw new IllegalArgumentException("Resource " + path + " does not exist");
        }
        try {
            return loader.load(path.getFilename(), path);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load yaml configuration from " + path, e);
        }
    }
}