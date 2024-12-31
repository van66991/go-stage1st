package live.nanami.gos1.common;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Globally shared Jackson Tools
 *
 * @author Administrator
 * @since 1.0
 */
public final class JacksonFactory {

    private static volatile ObjectMapper defaultObjectMapper = null;

    private static volatile ObjectMapper polymorphicObjectMapper = null;

    /**
     * 不支持多态性的ObjectMapper。
     *
     * default to using DefaultTyping.OBJECT_AND_NON_CONCRETE.
     *
     * @return ObjectMapper
     */
    public static ObjectMapper getDefaultObjectMapper() {
        if (defaultObjectMapper == null) {
            defaultObjectMapper = new ObjectMapper();
        }
        return defaultObjectMapper;
    }

    /**
     * 支持多态性的ObjectMapper
     *
     * using DefaultTyping.NON_FINAL
     *
     * @return ObjectMapper
     */
    public static ObjectMapper getPolymorphicObjectMapper() {
        if (polymorphicObjectMapper == null) {
            polymorphicObjectMapper = new ObjectMapper()
                    .enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
            polymorphicObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        return polymorphicObjectMapper;
    }

}
