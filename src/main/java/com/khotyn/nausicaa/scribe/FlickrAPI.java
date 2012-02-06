/**
 * Project: Nausicaa
 * 
 * File Created at Feb 7, 2012
 * $Id$
 * 
 * This is some code written by khotyn, and you are free to distribute those code for any use.
 * Welcome to my website: http://khotyn.com
 * Free we will be!
 */
package com.khotyn.nausicaa.scribe;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

/**
 * The flickr API
 * 
 * @author khotyn
 * 
 */
public class FlickrAPI extends DefaultApi10a {

    @Override
    public String getRequestTokenEndpoint() {
        return "http://www.flickr.com/services/oauth/request_token";
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "http://www.flickr.com/services/oauth/access_token";
    }

    @Override
    public String getAuthorizationUrl(Token requestToken) {
        return "http://www.flickr.com/services/oauth/authorize?oauth_token=" + requestToken.getToken();
    }

}
