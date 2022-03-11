package shared.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSONSerializer {

    public String serialize(Object obj) {
        if(obj == null){
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T deserialize(String content, TypeReference<T> ref) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(content, ref);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}