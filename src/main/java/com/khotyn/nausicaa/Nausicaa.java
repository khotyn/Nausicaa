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
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.khotyn.nausicaa.scribe.FlickrAPI;

/**
 * The entry of the program
 * 
 * @author khotyn
 * 
 */
public class Nausicaa {
    public static void main(String[] args) {
        Token token = getToken();

        if (token == null) {
            System.out.print("It seems that Nausicaa is not setted up, would you like to set up(yes to setup, other to exit): ");
            Scanner in = new Scanner(System.in);

            if (in.nextLine().equalsIgnoreCase("yes")) {
                Setup.setup("flickr");
            } else {
                System.exit(0);
            }
        } else {

            OAuthService service = new ServiceBuilder().provider(FlickrAPI.class).apiKey(Constant.CONSUMER_KEY).apiSecret(Constant.OAUTH_SIGNATURE)
                    .build();
            OAuthRequest request = new OAuthRequest(Verb.POST, Constant.FLICKR_UPLOAD_API);
            service.signRequest(token, request);
            Map<String, String> bodyParams = new HashMap<String, String>();
            String fileName = "The Smile of My Niece";
            try {
                String content = "";
                byte[] file = FileUtils.readFileToByteArray(new File("/Users/apple/Desktop/IMG_0745.JPG"));

                // Add all OAuth parameters.
                Map<String, String> oauthParams = request.getOauthParameters();

                for (String key : oauthParams.keySet()) {
                    content += String.format(Constant.NORMAL_PARAM_FORMAT, key, oauthParams.get(key));
                }

                // Add all Body parameters
                for (String key : bodyParams.keySet()) {
                    content += String.format(Constant.NORMAL_PARAM_FORMAT, key, bodyParams.get(key));
                }

                content += String.format(Constant.PHOTO_PARAM_FORMAT, fileName);
                byte[] contentByte = content.getBytes();
                byte[] payload = ArrayUtils.addAll(ArrayUtils.addAll(contentByte, file), Constant.BOTTOM);
                request.addPayload(payload);
                request.addHeader("Content-Type", "multipart/form-data; boundary=" + Constant.BOUNDARY);
                request.addHeader("Content-Length", String.valueOf(payload.length));
                Response response = request.send();
                System.out.println(response.getBody());
            } catch (IOException e) {
                System.out.println("Error occured while reading the photo");
            }

        }
    }

    public static Token getToken() {
        File accessTokenFile = new File(Constant.ACCESS_TOKEN_FILE);

        if (accessTokenFile.exists()) {
            try {
                String tokenString = FileUtils.readFileToString(accessTokenFile);

                if (tokenString != null && tokenString.length() != 0) {
                    String tokenParts[] = tokenString.split(",");
                    return new Token(tokenParts[0], tokenParts[1]);
                }
            } catch (IOException e) {
                System.out.println("Error while reading the file.");
                System.exit(1);
            }
        }

        return null;
    }
}
