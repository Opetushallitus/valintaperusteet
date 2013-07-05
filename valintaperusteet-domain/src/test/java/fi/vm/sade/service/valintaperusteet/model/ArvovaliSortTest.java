package fi.vm.sade.service.valintaperusteet.model;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

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
            a0.setMinValue(-10000D);
            a0.setMaxValue(3D);
            s.add(a0);
            l.add(a0);
        }
        {
            Arvovalikonvertteriparametri a0 = new Arvovalikonvertteriparametri();
            a0.setMinValue(3D);
            a0.setMaxValue(6D);
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
