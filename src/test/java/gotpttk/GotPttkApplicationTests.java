package gotpttk;

import gotpttk.dao.BadgeDao;
import gotpttk.entities.*;
import gotpttk.service.BadgeService;
import gotpttk.service.BookRouteService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.lang.reflect.Method;
import java.sql.Date;
import java.util.ArrayList;

import static junit.framework.TestCase.*;

@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = WebMvcConfigure.class, loader = AnnotationConfigContextLoader.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class GotPttkApplicationTests {

	@Test
	public void contextLoads() {
		assertTrue(true);
	}

	@TestConfiguration
	static class BadgeServiceImplTestContextConfiguration {

		@Bean
		public BadgeService badgeService() {
			return new BadgeService();
		}
	}
	@Autowired
	private BadgeService badgeService;

	@Autowired
	private BookRouteService bookRouteService;

	@Test
	public void getPeriodOfScoringGivenBadge_userHasZeroBadges(){
		var badge = new Badge();
		var badges = new ArrayList<Badge>();
		var date = badgeService.getPeriodOfScoringGivenBadge(badges, badge);
		assertNull(date);
	}

	@Test
	public void getPeriodOfScoringGivenBadge_oldestBadge(){
		var badgeDate = new Date(new java.util.Date().getTime());
		var badge = new Badge(badgeDate, new Category(), new Tourist());
		var badges = new ArrayList<Badge>();
		badges.add(badge);
		var dates = badgeService.getPeriodOfScoringGivenBadge(badges, badge);
		assertEquals(new java.util.Date(0), dates[0]);
		assertEquals(badgeDate, dates[1]);
	}

	@Test
	public void getPeriodOfScoringGivenBadge_userHasMoreBadge(){
		var firstBadgeDate = new Date(new java.util.Date(1000).getTime());
		var secondBadgeDate = new Date(new java.util.Date().getTime());
		var firstBadge = new Badge(firstBadgeDate, new Category(), new Tourist());
		var secondBadge = new Badge(secondBadgeDate, new Category(), new Tourist());

		var badges = new ArrayList<Badge>();
		badges.add(firstBadge);
		badges.add(secondBadge);

		var dates = badgeService.getPeriodOfScoringGivenBadge(badges, secondBadge);
		assertEquals(firstBadgeDate, dates[0]);
		assertEquals(secondBadgeDate, dates[1]);
	}

}

