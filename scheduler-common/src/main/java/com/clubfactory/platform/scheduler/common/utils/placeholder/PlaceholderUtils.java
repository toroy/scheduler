package com.clubfactory.platform.scheduler.common.utils.placeholder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * placeholder utils
 */
public class PlaceholderUtils {

    private static final Logger logger = LoggerFactory.getLogger(PlaceholderUtils.class);

    /**
     * Prefix of the position to be replaced
     */
    public static final String placeholderPrefix = "${";

    /**
     * The suffix of the position to be replaced
     */
    public static final String placeholderSuffix = "}";


    /**
     * Replaces all placeholders of format {@code ${name}} with the value returned
     * from the supplied {@link PropertyPlaceholderHelper.PlaceholderResolver}.
     *
     * @param value                 the value containing the placeholders to be replaced
     * @param paramsMap             placeholder data dictionary
     * @return the supplied value with placeholders replaced inline
     */
    public static String replacePlaceholders(String value, Map<String, String> paramsMap, boolean ignoreUnresolvablePlaceholders) {
        //replacement tool， parameter key will be replaced by value,if can't match , will throw an exception
        PropertyPlaceholderHelper strictHelper = getPropertyPlaceholderHelper(false);

        //Non-strict replacement tool implementation, when the position to be replaced does not get the corresponding value, the current position is ignored, and the next position is replaced.
        PropertyPlaceholderHelper nonStrictHelper = getPropertyPlaceholderHelper(true);

        PropertyPlaceholderHelper helper = (ignoreUnresolvablePlaceholders ? nonStrictHelper : strictHelper);

        //the PlaceholderResolver to use for replacement
        return helper.replacePlaceholders(value, new PropertyPlaceholderResolver(value, paramsMap));
    }

    /**
     * Creates a new {@code PropertyPlaceholderHelper} that uses the supplied prefix and suffix.
     * @param ignoreUnresolvablePlaceholders indicates whether unresolvable placeholders should
     * be ignored ({@code true}) or cause an exception ({@code false})
     */
    public static PropertyPlaceholderHelper getPropertyPlaceholderHelper(boolean ignoreUnresolvablePlaceholders) {

        return new PropertyPlaceholderHelper(placeholderPrefix, placeholderSuffix, null, ignoreUnresolvablePlaceholders);
    }

    /**
     * Placeholder replacement resolver
     */
    private static class PropertyPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

        private final String value;

        private final Map<String, String> paramsMap;

        public PropertyPlaceholderResolver(String value, Map<String, String> paramsMap) {
            this.value = value;
            this.paramsMap = paramsMap;
        }

        @Override
        public String resolvePlaceholder(String placeholderName) {
            try {
                return paramsMap.get(placeholderName);
            } catch (Exception ex) {
                logger.error(String.format("resolve placeholder '%s' in [ %s ]" , placeholderName, value), ex);
                return null;
            }
        }
    }


}
