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
package com.khotyn.nausicaa;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.khotyn.nausicaa.scribe.FlickrAPI;

/**
 * Setup for flickr or yupoo
 * 
 * @author khotyn
 * 
 */
public class Setup {
    /**
     * Setup for flickr or yupoo
     * 
     * @param site flickr or yupoo
     */
    public static void setup(String site) {
        if (site.equalsIgnoreCase("flickr")) {
            setupFlickr();
        } else if (site.equalsIgnoreCase("yupoo")) {
            setupYupoo();
        } else {
            System.out.println("Invalid site name, only flickr and yupoo is valid.");
        }
    }

    /**
     * Setup flickr
     */
    private static void setupFlickr() {
        // Get request token.
        OAuthService service = new ServiceBuilder().provider(FlickrAPI.class).apiKey(Constant.CONSUMER_KEY).apiSecret(Constant.OAUTH_SIGNATURE)
                .build();
        Token requestToken = service.getRequestToken();

        // Get authentication URL.
        String authUrl = service.getAuthorizationUrl(requestToken);

        // Get the verify code from the user.
        System.out.println("Access the follow URL by your favorite web brower:" + authUrl);
        System.out.println("And then input the verify code you get from the above url:");
        Scanner in = new Scanner(System.in);

        // Get the access token.
        Verifier verifier = new Verifier(in.nextLine());
        Token accessToken = service.getAccessToken(requestToken, verifier);

        try {
            FileUtils.writeStringToFile(new File(Constant.ACCESS_TOKEN_FILE), accessToken.getToken() + "," + accessToken.getSecret());
            FileUtils.writeStringToFile(new File(Constant.SETTINGS_FILE), "");
        } catch (IOException e) {
            System.out.println("Error writing data to file!");
            return;
        }

        System.out.println("Set up complete!");
    }

    /**
     * Setup yupoo
     */
    private static void setupYupoo() {
    }
}
