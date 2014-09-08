package fi.vm.sade.service.valintaperusteet.service;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.List;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintakoeDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 16.1.2013 Time: 14.16 To
 * change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {
		ValinnatJTACleanInsertTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class ValintaryhmaServiceTest {

	@Autowired
	private ValintaryhmaService valintaryhmaService;

	@Autowired
	private ValintaryhmaDAO valintaryhmaDAO;

	@Autowired
	private ValinnanVaiheDAO valinnanVaiheDAO;

	@Autowired
	private ValintatapajonoDAO valintatapajonoDAO;

	@Autowired
	private ValintakoeDAO valintakoeDAO;

	@Test
	public void testInsertChild() {
		final String parentOid = "oid6";
		final int valinnanVaiheetLkm = 5;

		ValintaryhmaCreateDTO uusiValintaryhma = new ValintaryhmaCreateDTO();
		uusiValintaryhma.setNimi("uusi valintaryhma");

		Valintaryhma lisatty = valintaryhmaService.insert(uusiValintaryhma,
				parentOid);
		List<ValinnanVaihe> valinnanVaiheet = LinkitettavaJaKopioitavaUtil
				.jarjesta(valinnanVaiheDAO.findByValintaryhma(lisatty.getOid()));
		assertEquals(valinnanVaiheetLkm, valinnanVaiheet.size());

		assertEquals(10L, valinnanVaiheet.get(0).getMasterValinnanVaihe()
				.getId());
		assertEquals(11L, valinnanVaiheet.get(1).getMasterValinnanVaihe()
				.getId());
		assertEquals(12L, valinnanVaiheet.get(2).getMasterValinnanVaihe()
				.getId());
		assertEquals(13L, valinnanVaiheet.get(3).getMasterValinnanVaihe()
				.getId());
		assertEquals(14L, valinnanVaiheet.get(4).getMasterValinnanVaihe()
				.getId());
	}

	@Test
	public void testInsert() {
		final String parentOid = "oid33";
		{
			assertNotNull(valintaryhmaService.readByOid(parentOid));
			List<ValinnanVaihe> vr33Lvaiheet = LinkitettavaJaKopioitavaUtil
					.jarjesta(valinnanVaiheDAO.findByValintaryhma(parentOid));

			assertEquals(2, vr33Lvaiheet.size());
			ValinnanVaihe vaihe80L = vr33Lvaiheet.get(0);
			ValinnanVaihe vaihe81L = vr33Lvaiheet.get(1);

			List<Valintatapajono> vaihe80Ljonot = LinkitettavaJaKopioitavaUtil
					.jarjesta(valintatapajonoDAO.findByValinnanVaihe(vaihe80L
							.getOid()));
			List<Valintatapajono> vaihe81Ljonot = LinkitettavaJaKopioitavaUtil
					.jarjesta(valintatapajonoDAO.findByValinnanVaihe(vaihe81L
							.getOid()));

			assertEquals(2, vaihe80Ljonot.size());
			assertEquals(1, vaihe81Ljonot.size());
		}

		ValintaryhmaCreateDTO uusiValintaryhma = new ValintaryhmaCreateDTO();

		uusiValintaryhma.setNimi("uusi nimi");

		Valintaryhma lisatty = valintaryhmaService.insert(uusiValintaryhma,
				parentOid);
		assertTrue(StringUtils.isNotBlank(lisatty.getOid()));

		{
			assertNotNull(valintaryhmaService.readByOid(parentOid));
			List<ValinnanVaihe> vr33Lvaiheet = LinkitettavaJaKopioitavaUtil
					.jarjesta(valinnanVaiheDAO.findByValintaryhma(parentOid));

			assertEquals(2, vr33Lvaiheet.size());
			ValinnanVaihe vaihe80L = vr33Lvaiheet.get(0);
			ValinnanVaihe vaihe81L = vr33Lvaiheet.get(1);

			List<Valintatapajono> vaihe80Ljonot = LinkitettavaJaKopioitavaUtil
					.jarjesta(valintatapajonoDAO.findByValinnanVaihe(vaihe80L
							.getOid()));
			List<Valintatapajono> vaihe81Ljonot = LinkitettavaJaKopioitavaUtil
					.jarjesta(valintatapajonoDAO.findByValinnanVaihe(vaihe81L
							.getOid()));

			assertEquals(2, vaihe80Ljonot.size());
			assertEquals(1, vaihe81Ljonot.size());
		}
		{
			assertNotNull(valintaryhmaService.readByOid(lisatty.getOid()));
			List<ValinnanVaihe> uusiVaiheet = LinkitettavaJaKopioitavaUtil
					.jarjesta(valinnanVaiheDAO.findByValintaryhma(lisatty
							.getOid()));

			assertEquals(2, uusiVaiheet.size());
			ValinnanVaihe uusiVaihe1 = uusiVaiheet.get(0);
			ValinnanVaihe uusiVaihe2 = uusiVaiheet.get(1);

			List<Valintatapajono> uusiVaihe1jonot = LinkitettavaJaKopioitavaUtil
					.jarjesta(valintatapajonoDAO.findByValinnanVaihe(uusiVaihe1
							.getOid()));
			List<Valintatapajono> uusiVaihe2jonot = LinkitettavaJaKopioitavaUtil
					.jarjesta(valintatapajonoDAO.findByValinnanVaihe(uusiVaihe2
							.getOid()));

			assertEquals(2, uusiVaihe1jonot.size());
			assertEquals(1, uusiVaihe2jonot.size());
		}
	}

	@Test
	public void testKopioiValintakokeetUudenAlavalintaryhmanValinnanVaiheelle() {
		final String parentValintaryhmaOid = "oid56";
		final String valinnanVaiheOid = "107";
		final String valintakoeOid = "oid14";

		{
			Valintaryhma valintaryhma = valintaryhmaService
					.readByOid(parentValintaryhmaOid);
			List<ValinnanVaihe> vaiheet = valinnanVaiheDAO
					.findByValintaryhma(parentValintaryhmaOid);
			assertEquals(1, vaiheet.size());

			ValinnanVaihe vaihe = vaiheet.get(0);
			assertEquals(ValinnanVaiheTyyppi.VALINTAKOE,
					vaihe.getValinnanVaiheTyyppi());
			assertEquals(valinnanVaiheOid, vaihe.getOid());

			List<Valintakoe> kokeet = valintakoeDAO.findByValinnanVaihe(vaihe
					.getOid());
			assertEquals(1, kokeet.size());
			assertEquals(valintakoeOid, kokeet.get(0).getOid());
		}

		ValintaryhmaCreateDTO childCreate = new ValintaryhmaCreateDTO();

		childCreate.setNimi("uusi alavalintaryhma");
		Valintaryhma child = valintaryhmaService.insert(childCreate,
				parentValintaryhmaOid);

		{
			List<ValinnanVaihe> vaiheet = valinnanVaiheDAO
					.findByValintaryhma(child.getOid());
			assertEquals(1, vaiheet.size());

			ValinnanVaihe vaihe = vaiheet.get(0);
			assertEquals(ValinnanVaiheTyyppi.VALINTAKOE,
					vaihe.getValinnanVaiheTyyppi());
			assertEquals(valinnanVaiheOid, vaihe.getMasterValinnanVaihe()
					.getOid());

			List<Valintakoe> kokeet = valintakoeDAO.findByValinnanVaihe(vaihe
					.getOid());
			assertEquals(1, kokeet.size());
			assertEquals(valintakoeOid, kokeet.get(0).getMasterValintakoe()
					.getOid());
		}
	}
}
