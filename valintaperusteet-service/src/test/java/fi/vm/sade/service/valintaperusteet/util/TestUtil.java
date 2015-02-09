package fi.vm.sade.service.valintaperusteet.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.service.valintaperusteet.ObjectMapperProvider;


/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 24.1.2013
 * Time: 12.42
 * To change this template use File | Settings | File Templates.
 */
public class TestUtil {

    private ObjectMapper objectMapper;

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public TestUtil(Class<?> type) {

        objectMapper = new ObjectMapperProvider().getContext(type);
    }

    public void lazyCheck(Class<?> view, Object object) throws Exception{
        lazyCheck(view, object, false);
    }


    public void lazyCheck(Class<?> view, Object object, boolean print) throws Exception {
        String s = objectMapper.writerWithView(view).writeValueAsString(object);
        if(print) {
            System.out.println(s);
        }
    }
}
