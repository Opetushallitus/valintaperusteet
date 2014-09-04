package fi.vm.sade.service.valintaperusteet.annotation;

import java.lang.annotation.*;

/**
 * Created by kjsaila on 04/09/14.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface DataSetLocation {
    String value();
}
