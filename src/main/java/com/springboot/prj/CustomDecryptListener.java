package com.springboot.prj;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;


/**
 * @author Pritam
 *
 */
public class CustomDecryptListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {
	
	private String prefix = "{encrypt}";
	private String keySecret = "keySecretPassword";
	@Override
	public int getOrder() {
		 return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
		ConfigurableEnvironment env = event.getEnvironment();
		final MutablePropertySources propertySources = env.getPropertySources();
        Set<String> encryptedProps = convertSteamFromIterator(propertySources.iterator())
                .filter(EnumerablePropertySource.class::isInstance)
                .map(EnumerablePropertySource.class::cast)
                .flatMap(source -> asList(source.getPropertyNames()).stream())
                //.filter(this::isNotEncryptionConfigProperty)
                .filter(key -> isPropsEncrypted(env.getProperty(key)))
                .collect(toSet());
        includeDecyptedValues(encryptedProps,env,propertySources);
		
	}

	private boolean isPropsEncrypted(String prop) {
		 return prop != null && prop instanceof String && ((String)prop).startsWith(prefix);
	}

	private Stream<PropertySource<?>> convertSteamFromIterator(Iterator<PropertySource<?>> iterator) {
		Iterable<PropertySource<?>> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
	}
	private void includeDecyptedValues(Set<String> encryptedKeys,Environment env, MutablePropertySources propertySources) {
        Map<String, Object> decryptedProperties = encryptedKeys.stream()
                .collect(toMap(
                        key -> key,
                        key -> decryptedValue(env.getProperty(key))));
        propertySources.addFirst(new MapPropertySource("decryptedValues", decryptedProperties));
    }

	private String decryptedValue(String property) {
		String cypherValue =property.substring(prefix.length());
        return AESUtil.decrypt(cypherValue,keySecret);
	}

}
