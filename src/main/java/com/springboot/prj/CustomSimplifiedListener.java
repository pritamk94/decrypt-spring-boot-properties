package com.springboot.prj;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;


/**
 * @author Pritam
 *
 */
public class CustomSimplifiedListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {
	
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
		Set<String> encryptedProps = new TreeSet<>();
		propertySources.iterator().forEachRemaining(obj->{
			if(obj instanceof EnumerablePropertySource) {
				for(String key : ((EnumerablePropertySource<?>) obj).getPropertyNames()) {
					if(isPropsEncrypted(env.getProperty(key))) {
						encryptedProps.add(key);
					}
				}
			}
		});
        includeDecyptedValues(encryptedProps,env,propertySources);
	}

	private boolean isPropsEncrypted(String prop) {
		 return prop != null && prop instanceof String && ((String)prop).startsWith(prefix);
	}

	private void includeDecyptedValues(Set<String> encryptedKeys,Environment env, MutablePropertySources propertySources) {
        Map<String, Object> decryptedProperties = new HashMap<>();
        encryptedKeys.forEach(key->decryptedProperties.put(key, decryptedValue(env.getProperty(key))));
        propertySources.addFirst(new MapPropertySource("decryptedValues", decryptedProperties));
    }

	private String decryptedValue(String property) {
		String cypherValue =property.substring(prefix.length());
        return AESUtil.decrypt(cypherValue,keySecret);
	}

}
