package io.cereebro.autoconfigure;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.cereebro.autoconfigure.annotation.ConsumerHintAnnotationRelationshipDetector;
import io.cereebro.autoconfigure.annotation.DependencyHintAnnotationRelationshipDetector;
import io.cereebro.autoconfigure.annotation.RelationshipHintsAnnotationRelationshipDetector;
import io.cereebro.autoconfigure.cassandra.CereebroCassandraRelationshipDetectorAutoConfiguration;
import io.cereebro.autoconfigure.datasource.DataSourceRelationshipDetector;
import io.cereebro.core.Component;
import io.cereebro.core.CompositeRelationshipDetector;
import io.cereebro.core.RelationshipDetector;
import io.cereebro.snitch.spring.boot.actuate.endpoint.CereebroEndpoint;

/**
 * Autoconfiguration class used to instantiate {@link RelationshipDetector}s and
 * the {@link CereebroEndpoint}
 * 
 * @author lwarrot
 *
 */
@Configuration
@EnableConfigurationProperties({ CereebroProperties.class })
@Import({ CereebroCassandraRelationshipDetectorAutoConfiguration.class })
public class CereebroAutoConfiguration {

    @Autowired
    private CereebroProperties cereebroProperties;

    public RelationshipDetector compositeRelationshipDetector(Set<RelationshipDetector> detectors) {
        return new CompositeRelationshipDetector(detectors);
    }

    @Bean
    public CereebroEndpoint cereebroEndpoint(Set<RelationshipDetector> detectors) {
        Component applicationComponent = cereebroProperties.getApplication().getComponent().toComponent();
        return new CereebroEndpoint(applicationComponent, compositeRelationshipDetector(detectors));
    }

    @Bean
    public ConfigurationPropertiesRelationshipDetector configurationPropertiesRelationshipDetector() {
        return new ConfigurationPropertiesRelationshipDetector(cereebroProperties);
    }

    @Bean
    public DataSourceRelationshipDetector dataSourceRelationshipDetector() {
        return new DataSourceRelationshipDetector();
    }

    @Bean
    public DependencyHintAnnotationRelationshipDetector dependencyHintAnnotationRelationshipDetector() {
        return new DependencyHintAnnotationRelationshipDetector();
    }

    @Bean
    public ConsumerHintAnnotationRelationshipDetector consumerHintAnnotationRelationshipDetector() {
        return new ConsumerHintAnnotationRelationshipDetector();
    }

    @Bean
    public RelationshipHintsAnnotationRelationshipDetector relationshipHintsDetector() {
        return new RelationshipHintsAnnotationRelationshipDetector(dependencyHintAnnotationRelationshipDetector(),
                consumerHintAnnotationRelationshipDetector());
    }

}
