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

    private static volatile ObjectMapper DEFAULT_OBJ_MAPPER = null;

    private static volatile ObjectMapper POLYMORPHIC_OBJ_MAPPER = null;

    /**
     * 不支持多态性的ObjectMapper。
     *
     * default to using DefaultTyping.OBJECT_AND_NON_CONCRETE.
     *
     * @return ObjectMapper
     */
    public static ObjectMapper getDefaultObjMapper() {
        if (DEFAULT_OBJ_MAPPER == null) {
            DEFAULT_OBJ_MAPPER = new ObjectMapper();
        }
        return DEFAULT_OBJ_MAPPER;
    }

    /**
     * 支持多态性的ObjectMapper
     *
     * using DefaultTyping.NON_FINAL
     *
     * @return ObjectMapper
     */
    public static ObjectMapper getPolymorphicObjMapper() {
        if (POLYMORPHIC_OBJ_MAPPER == null) {
            POLYMORPHIC_OBJ_MAPPER = new ObjectMapper()
                    .enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
            POLYMORPHIC_OBJ_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        return POLYMORPHIC_OBJ_MAPPER;
    }

}
