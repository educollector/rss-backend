package com.ola;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.ola.model.*;
import lombok.val;

import javax.naming.AuthenticationException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created on 11/29/15.
 */
public class DatabaseManager {

  private final static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
  private final static String JDBC_URL = "jdbc:mysql://localhost:3306/wwsi_prod";
  private final static String DB_USER = "wwsi_prod";
  private final static String DB_PASS = "wwsi2015!";

  private Connection connect = null;
  private Dao<User, Long> userDao;
  private Dao<Sesion, Long> sesionDao;
  private Dao<FeedUser, Long> feedUserDao;
  private Dao<Feed, Long> feedDao;

  public DatabaseManager() throws SQLException {
    // create a connection source to our database
    ConnectionSource connectionSource =  new JdbcConnectionSource(JDBC_URL, DB_USER, DB_PASS);

    // instantiate the DAOs
    userDao = DaoManager.createDao(connectionSource, User.class);
    sesionDao = DaoManager.createDao(connectionSource, Sesion.class);
    feedUserDao = DaoManager.createDao(connectionSource, FeedUser.class);
    feedDao = DaoManager.createDao(connectionSource, Feed.class);
  }

  public String register(User user) throws SQLException, AuthenticationException {
    if(checkUserExistInDbByName(user)){
      throw new AuthenticationException(StringsManager.stringNickNotAvailable());
    }
    userDao.create(user);
    return login(user);
  }

  public String login(User user) throws SQLException, AuthenticationException {
    User authUser = userDao.queryBuilder().where().eq(User.COLUMN_NAME, user.getName()).and().eq(User.COLUMN_PASSWORD, user.getPassword()).queryForFirst();
    if (authUser == null) throw new AuthenticationException(StringsManager.stringInvalidNameOrPassword());
    return makeSession(authUser);
  }

  // HELPER METHODS
  public boolean checkUserExistInDbByName(User user) throws SQLException{
    User dbUser = userDao.queryBuilder().where().eq(User.COLUMN_NAME, user.getName()).queryForFirst();
    if (dbUser == null) {
      return false;
    }else{
      return true;
    }
  }

  private String makeSession(User userFromDb) throws SQLException{
    //delete all expired sessions
    deleteExpiredSesions();
    long expPeriod = 3600000; //hour

    Sesion sesionUpdate = sesionDao.queryBuilder().where().eq(Sesion.COLUMN_ID_USER, userFromDb.getId()).queryForFirst();
    if(sesionUpdate == null){
      //no sesion for this user
      String sessionToken = makeNewSessionFroUser(userFromDb, expPeriod);
      return  sessionToken;
    }else {
      sesionUpdate.setExpDate(System.currentTimeMillis() + expPeriod);
      sesionDao.update(sesionUpdate);
      return  sesionUpdate.getToken();
    }
  }

  private String makeNewSessionFroUser (User userFromDb, long expPeriod) throws SQLException{
    UUID token = UUID.randomUUID();
    long expDateToSave = (System.currentTimeMillis()+expPeriod);
    Sesion sesionToSave = new Sesion();
    sesionToSave.setToken(token.toString());
    sesionToSave.setExpDate(expDateToSave);
    sesionToSave.setIdUser(userFromDb.getId());
    //save session to database
    int result = sesionDao.create(sesionToSave);
    if(result == 1){
      return  sesionToSave.getToken();
    }else{
      return null;
    }
  }

  private void deleteExpiredSesions() throws SQLException {
    val deleteBuilder = sesionDao.deleteBuilder();
    deleteBuilder.where().le(Sesion.COLUMN_EXP_DATE, System.currentTimeMillis());
    deleteBuilder.delete();
  }

  public FeedRequest saveFeeds(FeedRequest feedRequest, User authenticatedUser) throws SQLException{
    long userId = authenticatedUser.getId(); //getUserIdWithToken(feedRequest.getToken());
    /************************************************************/
    long now = System.currentTimeMillis();//feedRequest.getTimestamp()+1;
    /************************************************************/
    // (1) save CREATED AND UPDATED with user id
    for(String url : feedRequest.getCreatedUpdated()){
      //FEED
      long feedId;
      Feed feedFromDb = feedDao.queryBuilder().where().eq(Feed.COLUMN_URL, url).queryForFirst();
      if(feedFromDb != null){
        feedFromDb.setSyncTimestamp(now);
        feedDao.update(feedFromDb);
        feedId = feedFromDb.getId();
      }else{
        Feed feedToAdd = new Feed();
        feedToAdd.setUrl(url);
        feedToAdd.setSyncTimestamp(now);
        feedDao.create(feedToAdd);
        feedId = feedToAdd.getId();
      }
      //FEED_USER
      FeedUser feedUser = feedUserDao.queryBuilder().where()
              .eq(FeedUser.COLUMN_ID_FEED, feedId).and()
              .eq(FeedUser.COLUMN_ID_USER, userId)
              .queryForFirst();
      if(feedUser != null){
        //update feed_user entitty
        feedUser.setUpdateDate(now);
        feedUser.setDeleted(false);
        feedUserDao.update(feedUser);
      }else{
        //create feed_user entitty for given id_user and id_feed
        FeedUser feedUserToSave = new FeedUser();
        feedUserToSave.setIdFeed(feedId);
        feedUserToSave.setIdUser(userId);
        feedUserToSave.setUpdateDate(now);
        feedUserToSave.setDeleted(false);
        feedUserDao.create(feedUserToSave);
      }
    }

    // (2) save DELETED with user id
    for(String url : feedRequest.getDeleted()){
      //FEED
      long feedId;
      Feed feedFromDb = feedDao.queryBuilder().where().eq(Feed.COLUMN_URL, url).queryForFirst();
      if(feedFromDb != null){
        feedFromDb.setSyncTimestamp(now);
        feedDao.update(feedFromDb);
        feedId = feedFromDb.getId();
      }else{
        Feed feedToAdd = new Feed();
        feedToAdd.setUrl(url);
        feedToAdd.setSyncTimestamp(now);
        feedDao.create(feedToAdd);
        feedId = feedToAdd.getId();
      }
      //FEED_USER
      FeedUser feedUser = feedUserDao.queryBuilder().where()
              .eq(FeedUser.COLUMN_ID_FEED, feedId).and()
              .eq(FeedUser.COLUMN_ID_USER, userId)
              .queryForFirst();
      if(feedUser != null){
        //update feed_user entitty
        feedUser.setUpdateDate(now);
        feedUser.setDeleted(true);
        feedUserDao.update(feedUser);
      }else{
        //create feed_user entitty for given id_user and id_feed
        FeedUser feedUserToSave = new FeedUser();
        feedUserToSave.setIdFeed(feedId);
        feedUserToSave.setIdUser(userId);
        feedUserToSave.setUpdateDate(now);
        feedUserToSave.setDeleted(true);
        feedUserDao.create(feedUserToSave);
      }
    }

    //RETURNING
    /************************************************************/
    long currentSyncTime = System.currentTimeMillis();
    FeedRequest feedRequestToRetur = new FeedRequest();
    ArrayList<String>creatUpdat = new ArrayList();
    ArrayList<String>deleted = new ArrayList();
    /************************************************************/

    // (3) make an array of created/updated feeds from the last sync
    List<FeedUser> feedsForUserIds = feedUserDao.queryBuilder()
        .where()
        .eq(FeedUser.COLUMN_ID_USER, userId)
        .and()
        .gt(FeedUser.COLUMN_UPDATE_DATE, feedRequest.getTimestamp())
        .query();

    for(FeedUser feedUser : feedsForUserIds){
      Feed feed = feedDao.queryBuilder().where().eq(Feed.COLUMN_ID, feedUser.getIdFeed()).queryForFirst();
      if(feed != null){
        if(feedUser.isDeleted()){
          deleted.add(feed.getUrl());
        }else {
          creatUpdat.add(feed.getUrl());
        }
      }
    }
    feedRequestToRetur.setTimestamp(now);
    feedRequestToRetur.setCreatedUpdated(creatUpdat);
    feedRequestToRetur.setDeleted(deleted);
    return feedRequestToRetur;
  }

  public User authSession(String token) throws SQLException, AuthenticationException {
    Sesion sesion = sesionDao.queryBuilder().where().eq(Sesion.COLUMN_TOKEN, token).queryForFirst();
    if (sesion == null) throw new AuthenticationException("Session expired");
    User user = userDao.queryBuilder().where().eq(User.COLUMN_ID, sesion.getIdUser()).queryForFirst();
    if (user == null) throw new AuthenticationException("Authentication problem");
    return user;
  }

  public void create(Feed feed, User authUser) throws SQLException {
    Feed dbFeed = new Feed();
    dbFeed.setUrl(feed.getUrl());
    feedDao.create(dbFeed);

    FeedUser dbFeedUser = new FeedUser();
    dbFeedUser.setIdFeed(dbFeed.getId());
    dbFeedUser.setIdUser(authUser.getId());
    dbFeedUser.setUpdateDate(System.currentTimeMillis());
    dbFeedUser.setDeleted(false);
    feedUserDao.create(dbFeedUser);
  }

  public List<Feed> getFeeds(User authUser) throws SQLException {
    List<FeedUser> feedUserList = feedUserDao.queryBuilder()
        .where()
        .eq(FeedUser.COLUMN_ID_USER, authUser.getId())
        .query();

    List<Feed> feedList = new ArrayList<>();
    for (FeedUser feedUser : feedUserList) {
      Feed feed = feedDao.queryBuilder()
          .where()
          .eq(Feed.COLUMN_ID, feedUser.getIdFeed())
          .queryForFirst();
      feedList.add(feed);
    }
    return feedList;
  }

  public void deleteFeedById(String feedId, User authUser) throws SQLException {
    DeleteBuilder<FeedUser, Long> deleteBuilder = feedUserDao.deleteBuilder();
    deleteBuilder.where()
        .eq(FeedUser.COLUMN_ID_FEED, feedId)
        .and()
        .eq(FeedUser.COLUMN_ID_USER, authUser.getId());
    deleteBuilder.delete();
  }

  public Feed getFeedById(String feedId, User authUser) throws SQLException {
    return feedDao.queryBuilder()
        .where()
        .eq(Feed.COLUMN_ID, feedId)
        .queryForFirst();
  }

  public void updateFeedById(String feedId, Feed feed, User authUser) throws SQLException {
    feed.setId(Long.parseLong(feedId));
    feedDao.createOrUpdate(feed);
  }
}

