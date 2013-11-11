package fi.vm.sade.service.valintaperusteet.model;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
public class ArvovaliSortTest {

    @Test
    public void testaaArvovalinSorttaus() {
        SortedSet<Arvovalikonvertteriparametri> s = new TreeSet<Arvovalikonvertteriparametri>();
        List<Arvovalikonvertteriparametri> l = new ArrayList<Arvovalikonvertteriparametri>();

        {
            Arvovalikonvertteriparametri a0 = new Arvovalikonvertteriparametri();
            a0.setMinValue(new BigDecimal("-10000.0"));
            a0.setMaxValue(new BigDecimal("3.0"));
            s.add(a0);
            l.add(a0);
        }
        {
            Arvovalikonvertteriparametri a0 = new Arvovalikonvertteriparametri();
            a0.setMinValue(new BigDecimal("3.0"));
            a0.setMaxValue(new BigDecimal("6.0"));
            s.add(a0);
            l.add(a0);
        }
        int i = 0;
        for (Arvovalikonvertteriparametri p : s) {
            Assert.assertEquals(l.get(i), p);
            ++i;
        }
    }
}
