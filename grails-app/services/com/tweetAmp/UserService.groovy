package com.tweetAmp

import grails.converters.JSON
import grails.plugin.springsecurity.oauth.OAuthToken
import grails.transaction.Transactional
import org.apache.commons.lang.RandomStringUtils
import org.scribe.model.Token
import twitter4j.Paging
import twitter4j.Query
import twitter4j.QueryResult
import twitter4j.RateLimitStatus
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import twitter4j.auth.OAuth2Token
import twitter4j.conf.ConfigurationBuilder

@Transactional
class UserService {

    public static final String GOOGLE_USER_PROFILE_URL = "https://www.googleapis.com/oauth2/v1/userinfo"
    public static final String INTELLI_GRAPE = "IntelliGrape"
    def oauthService
    def springSecurityService
    def grailsApplication

    def registerNewUser(OAuthToken oAuthToken, Token googleAccessToken) {

        String password = RandomStringUtils.randomAlphabetic(10)
        def googleResource = oauthService.getGoogleResource(googleAccessToken, GOOGLE_USER_PROFILE_URL)
        def googleResponse = JSON.parse(googleResource?.getBody())
        User newUser = new User(username: googleResponse.email, password: password, enabled: true, email: googleResponse.email,
                picture: googleResponse.picture, name: googleResponse.name)

        if (newUser.save(flush: true)) {
            Role userRole = Role.findByAuthority("ROLE_USER")
            UserRole.create(newUser, userRole, true)
        }

        return newUser
    }


    def OAuth2Token getOAuth2Token() {

        def twitterConfig = grailsApplication.config.twitter4j
        String consumerKey = twitterConfig.'default'.OAuthConsumerKey ?: ''
        String consumerSecret = twitterConfig.'default'.OAuthConsumerSecret ?: ''

        OAuth2Token token
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setApplicationOnlyAuthEnabled(true);
        cb.setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret);

        try {
            token = new TwitterFactory(cb.build()).getInstance().getOAuth2Token();
        }
        catch (Exception e) {
            println "Could not get OAuth2 token ${e.message}"
        }

        return token;
    }

    List<Status> getUserTweets(TwitterCredentials twitterCredentials) {
        def twitterConfig = grailsApplication.config.twitter4j
        String consumerKey = twitterConfig.'default'.OAuthConsumerKey ?: ''
        String consumerSecret = twitterConfig.'default'.OAuthConsumerSecret ?: ''

        ConfigurationBuilder cb = new ConfigurationBuilder();


        if (!twitterCredentials) {
            OAuth2Token token = getOAuth2Token();
            cb.setApplicationOnlyAuthEnabled(true);
            cb.setOAuth2TokenType(token.getTokenType());
            cb.setOAuth2AccessToken(token.getAccessToken());
        }

        cb.setOAuthConsumerKey(consumerKey);
        cb.setOAuthConsumerSecret(consumerSecret);
        Twitter twitter = new TwitterFactory(cb.build()).getInstance();

        if (twitterCredentials) {
            AccessToken accessToken = new AccessToken(twitterCredentials?.accessToken, twitterCredentials?.accessTokenSecret)
            twitter.setOAuthAccessToken(accessToken)
        }

        List<Status> statuses = []
        try {
            statuses = twitter.getUserTimeline(INTELLI_GRAPE, new Paging(1, 50))
        }
        catch (TwitterException te) {
            if (401 == te.getStatusCode()) {
                println "Error with application authorization for TweetAmp ${te.message}"
            }
        }

        return statuses;
    }


    TwitterCredentials saveTwitterCredentials(AccessToken accessToken) {
        User currentUser = springSecurityService.currentUser as User
        TwitterCredentials twitterCredentials = new TwitterCredentials(accessToken: accessToken.token,
                accessTokenSecret: accessToken.tokenSecret, screenName: accessToken.getScreenName(),
                twitterUserId: accessToken.getUserId(), user: currentUser)
        twitterCredentials.save(flush: true, failOnError: true)
        return twitterCredentials
    }

    def revokeApp() {
        User currentUser = springSecurityService.currentUser as User
        def twitterCredentials = TwitterCredentials.findByUser(currentUser)
        twitterCredentials.delete(flush: true)
    }


}
