package com.springbook.user.service;

import com.springbook.user.dao.UserDao;
import com.springbook.user.domain.Level;
import com.springbook.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.TestTimedOutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.util.List;
import java.util.Arrays;

import static com.springbook.user.service.UserService.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    UserDao userDao;
    List<User> users;	// test fixture
    User user;

    static class TestUserService extends UserService {
        private String id;

        private TestUserService(String id) {
            this.id = id;
        }

        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException {

    }
    @Before
    public void setUp() {
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0),
                new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
                new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1),
                new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
                new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    @Test()
    public void upgradeLevel() {
     userDao.deleteAll();
     for(User user : users) userDao.add(user);

     userService.upgradeLevels();

     checkLevelUpgraded(users.get(0), false);
     checkLevelUpgraded(users.get(1), true);
     checkLevelUpgraded(users.get(2), false);
     checkLevelUpgraded(users.get(3), true);
     checkLevelUpgraded(users.get(4), false);
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate  = userDao.get(user.getId());
        if (upgraded) {
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        }
        else {
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void cannotUpgradeLevel() {
        Level[] levels = Level.values();
        for(Level level : levels) {
            if (level.nextLevel() != null) continue;
            user.setLevel(level);
            user.upgradeLevel();
        }
    }

    @Test
    public void upgradeAllOrNothing() {
        UserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        try {
            testUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        }
        catch (TestUserServiceException e) {
        }

        checkLevelUpgraded(users.get(1), false);
    }

}