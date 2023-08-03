package com.springbook.user.service;

import com.mysql.cj.Session;
import com.springbook.mail.MailSender;
import com.springbook.user.dao.UserDao;
import com.springbook.user.domain.Level;
import com.springbook.user.domain.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;



public class UserService {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int  MIN_RECCOMEND_FOR_GOLD = 30;

    static UserDao userDao;
    private PlatformTransactionManager transactionManager;
    private MailSender mailSender;

    public void setMialSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }



    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }


    public void upgradeLevels()  throws Exception {
        TransactionStatus status =
                this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            List<User> users = userDao.getAll();
            for (User user : users) {
                if (canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
            this.transactionManager.commit(status);
        } catch (RuntimeException e) {
            this.transactionManager.rollback(status);
            throw e;
        }
    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case BASIC : return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
            case GOLD:  return false;
            default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        }
    }

    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEMail(user);
    }

    private void sendUpgradeEMail(User user) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("mail.server.com");

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmim@ksug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());

        this.mailSender.send(mailMessage);
    }

    public static void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}