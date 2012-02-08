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

/**
 * Some constants of Nausicaa
 * 
 * @author khotyn
 * 
 */
public class Constant {
    public static String FLICKR              = "flickr";
    public static String YUPOO               = "yupoo";
    public static String CONSUMER_KEY        = "0af00ad7924868076522b75aa05774db";
    public static String OAUTH_SIGNATURE     = "85ff1bfcd77120ce";
    public static String ACCESS_TOKEN_FILE   = System.getProperty("user.home") + "/.nausicaa/access.token.secret";
    public static String SETTINGS_FILE       = System.getProperty("user.home") + "/.nausicaa/setttings.properties";
    public static String BOUNDARY            = "---------------------------7d44e178b0434";
    public static String NORMAL_PARAM_FORMAT = "--" + Constant.BOUNDARY + "\r\n" + "Content-Disposition: form-data; name=\"%s\"\r\n\r\n%s\r\n";
    public static String PHOTO_PARAM_FORMAT  = "--"
                                                     + Constant.BOUNDARY
                                                     + "\r\n"
                                                     + "Content-Disposition: form-data; name=\"photo\"; filename=\"%s\"\r\nContent-Type: image/jpeg\r\n\r\n";
    public static byte[] BOTTOM              = ("\r\n--" + BOUNDARY + "--").getBytes();
    public static String FLICKR_UPLOAD_API   = "http://api.flickr.com/services/upload/";
}
