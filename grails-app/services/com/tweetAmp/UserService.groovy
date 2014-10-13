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

    def oauthService
    def twitter4jService
    def springSecurityService
    def grailsApplication

    //creates a new user and save the same.
    def registerNewUser(OAuthToken oAuthToken,Token googleAccessToken){

        String password = RandomStringUtils.randomAlphabetic(10)
        def googleResource = oauthService.getGoogleResource(googleAccessToken, "https://www.googleapis.com/oauth2/v1/userinfo")
        def googleResponse = JSON.parse(googleResource?.getBody())
        println googleResponse
        User newUser = new User (username: googleResponse.email, password: password, enabled: true,email: googleResponse.email,
                    picture: googleResponse.picture, name:googleResponse.name)

        if(newUser.validate()){
            newUser.save(flush: true)
        } else{
            println newUser.errors
        }

        def userRole = Role.findByAuthority("ROLE_USER")
        UserRole.create(newUser, userRole, true)
        return newUser
    }


    def OAuth2Token getOAuth2Token(){

        OAuth2Token token = null;
        ConfigurationBuilder cb = new ConfigurationBuilder();
        def consumerKey = grailsApplication.config.twitter4j.'default'.OAuthConsumerKey?:''
        def consumerSecret = grailsApplication.config.twitter4j.'default'.OAuthConsumerSecret?:''

        cb.setApplicationOnlyAuthEnabled(true);
        cb.setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret);

        try
        {
            token = new TwitterFactory(cb.build()).getInstance().getOAuth2Token();
        }
        catch (Exception e)
        {
            println "Could not get OAuth2 token"
            e.printStackTrace();
        }

        return token;
    }

    def getUserTweets(TwitterCredentials twitterCredentials) {

        def consumerKey = grailsApplication.config.twitter4j.'default'.OAuthConsumerKey?:''
        def consumerSecret = grailsApplication.config.twitter4j.'default'.OAuthConsumerSecret?:''

        ConfigurationBuilder cb = new ConfigurationBuilder();
        OAuth2Token token = getOAuth2Token();
        AccessToken accessToken = null

        if(!twitterCredentials){
            cb.setApplicationOnlyAuthEnabled(true);
            cb.setOAuth2TokenType(token.getTokenType());
            cb.setOAuth2AccessToken(token.getAccessToken());
        }

        cb.setOAuthConsumerKey(consumerKey);
        cb.setOAuthConsumerSecret(consumerSecret);
        if(twitterCredentials)
             accessToken = new AccessToken(twitterCredentials?.accessToken, twitterCredentials?.accessTokenSecret)

        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        if(twitterCredentials){
            twitter.setOAuthAccessToken(accessToken)
        }

        List<twitter4j.Status> statuses = null;
        try
        {
            if(twitterCredentials){
                Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("search");
           //     RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");
            }
            statuses = twitter.getUserTimeline("IntelliGrape",new Paging (1, 50))
        }
        catch (TwitterException te) {
            if(401 == te.getStatusCode()){
                println "Error with application authorization for TweetAmp"
                te.printStackTrace();
            }
        }

        return statuses;
    }


    def saveTwitterCredentials(AccessToken accessToken){

         User currentUser = springSecurityService.currentUser as User
         TwitterCredentials twitterCredentials = new TwitterCredentials(accessToken: accessToken.token,
                                                 accessTokenSecret: accessToken.tokenSecret,screenName: accessToken.getScreenName(),
                                                 twitterUserId: accessToken.getUserId(),user: currentUser)
         if(twitterCredentials.validate())
             twitterCredentials.save(flush: true)
         else
             println twitterCredentials.getErrors()

    }

    def revokeApp(){

        User currentUser = springSecurityService.currentUser as User
        def twitterCredentials = TwitterCredentials.findByUser(currentUser)
        twitterCredentials.delete(flush: true)
    }


}
