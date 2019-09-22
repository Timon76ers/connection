import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.concurrent.CopyOnWriteArrayList;

public class Test implements Serializable {
    public static void main(String[] args) throws IOException {
        CopyOnWriteArrayList<String> arrayList = new CopyOnWriteArrayList<>();

        for (int i = 0; i < 10; i++) {
            arrayList.add(String.valueOf(i));
        }

        StringWriter stringWriter = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(stringWriter, arrayList);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        }

        System.out.println(stringWriter.toString());
    }
}
