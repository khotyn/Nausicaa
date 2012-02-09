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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.khotyn.nausicaa.scribe.ContentTypeValidator;
import com.khotyn.nausicaa.scribe.DescriptionValidator;
import com.khotyn.nausicaa.scribe.FlickrAPI;
import com.khotyn.nausicaa.scribe.SafetyLevelValidator;
import com.khotyn.nausicaa.scribe.TagsValidator;
import com.khotyn.nausicaa.scribe.TitleValidator;
import com.khotyn.nausicaa.scribe.Validator;

/**
 * The entry of the program
 * 
 * @author khotyn
 * 
 */
public class Nausicaa {
    private static Map<String, Validator> paramValidators = new HashMap<String, Validator>();
    private static Map<String, String>    errorMsgMap     = new HashMap<String, String>();
    private static DocumentBuilder        builder         = null;
    private static OAuthService           service         = new ServiceBuilder().provider(FlickrAPI.class).apiKey(Constant.CONSUMER_KEY)
                                                                  .apiSecret(Constant.OAUTH_SIGNATURE).build();
    private static Token                  token           = null;
    static {
        paramValidators.put("title", new TitleValidator());
        paramValidators.put("description", new DescriptionValidator());
        paramValidators.put("tags", new TagsValidator());
        paramValidators.put("safetyLevel", new SafetyLevelValidator());
        paramValidators.put("contentType", new ContentTypeValidator());

        errorMsgMap.put("2", "No photo specified");
        errorMsgMap.put("3", "General upload failure");
        errorMsgMap.put("4", "Filesize was zero");
        errorMsgMap.put("5", "Filetype was not recognised");
        errorMsgMap.put("6", "User exceeded upload limit");
        errorMsgMap.put("96", "Invalid signature");
        errorMsgMap.put("97", "Missing signature");
        errorMsgMap.put("98", "Login failed / Invalid auth token");
        errorMsgMap.put("99", "User not logged in / Insufficient permissions");
        errorMsgMap.put("100", "Invalid API Key");
        errorMsgMap.put("105", "Service currently unavailable");
        errorMsgMap.put("106", "Bad URL found");

        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            System.out.println("Oops, fatal error!");
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        token = getToken();

        if (token == null) {
            System.out.print("It seems that Nausicaa is not setted up, would you like to set up(yes to setup, other to exit): ");
            Scanner in = new Scanner(System.in);

            if (in.nextLine().equalsIgnoreCase("yes")) {
                Setup.setup("flickr");
            } else {
                System.exit(0);
            }
        } else {
            OAuthRequest request = new OAuthRequest(Verb.POST, Constant.FLICKR_UPLOAD_API);
            Map<String, String> bodyParams = new HashMap<String, String>();
            checkParam(args, bodyParams);

            for (String key : bodyParams.keySet()) {
                request.addBodyParameter(key, bodyParams.get(key));
            }

            service.signRequest(token, request);
            try {
                String content = "";

                byte[] file = FileUtils.readFileToByteArray(new File(args[0]));

                // Add all OAuth parameters.
                Map<String, String> oauthParams = request.getOauthParameters();

                for (String key : oauthParams.keySet()) {
                    content += String.format(Constant.NORMAL_PARAM_FORMAT, key, oauthParams.get(key));
                }

                // Add all Body parameters
                for (String key : bodyParams.keySet()) {
                    content += String.format(Constant.NORMAL_PARAM_FORMAT, key, bodyParams.get(key));
                }

                content += String.format(Constant.PHOTO_PARAM_FORMAT, args[0].substring(args[0].lastIndexOf('/') + 1));
                byte[] contentByte = content.getBytes();
                byte[] payload = Utils.addAll(Utils.addAll(contentByte, file), Constant.BOTTOM);
                request.addPayload(payload);
                request.addHeader("Content-Type", "multipart/form-data; boundary=" + Constant.BOUNDARY);
                request.addHeader("Content-Length", String.valueOf(payload.length));
                System.out.println("Uploading......");
                Response response = request.send();
                parseUplodResult(response.getBody());
            } catch (IOException e) {
                System.out.println("Error occured while reading the photo");
            } catch (SAXException e) {
                System.out.println("Error occured while reading the photo");
            }

        }
    }

    private static void parseUplodResult(String response) throws SAXException, IOException {
        Document document = builder.parse(new ByteArrayInputStream(response.getBytes()));
        Element rsp = document.getDocumentElement();

        if (rsp.getAttribute("stat").equals("ok")) {
            System.out.println("Upload Complete.");
            NodeList nodeList = rsp.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeName().equals("photoid")) {
                    String photoId = node.getTextContent();
                    sendSizeRequest(photoId);
                }
            }
        } else {
            error(rsp.getChildNodes());
        }

    }

    private static void sendSizeRequest(String photoId) throws SAXException, IOException {
        OAuthRequest resquest = new OAuthRequest(Verb.GET, "http://api.flickr.com/services/rest/");
        resquest.addQuerystringParameter("method", "flickr.photos.getSizes");
        resquest.addQuerystringParameter("photo_id", photoId);
        service.signRequest(token, resquest);
        Response sizeResponse = resquest.send();
        parseSizeResult(sizeResponse.getBody());

    }

    private static void parseSizeResult(String response) throws SAXException, IOException {
        Document sizeDocument = builder.parse(new ByteArrayInputStream(response.getBytes()));
        Element sizeRsp = sizeDocument.getDocumentElement();

        if (sizeRsp.getAttribute("stat").equals("ok")) {
            System.out.println("The static links of the photo:");
            NodeList children = sizeRsp.getChildNodes();

            for (int k = 0; k < children.getLength(); k++) {
                if (children.item(k).getNodeName().equals("sizes")) {
                    NodeList sizeNodeList = children.item(k).getChildNodes();
                    for (int j = 0; j < sizeNodeList.getLength(); j++) {
                        Node sizeNode = sizeNodeList.item(j);
                        if (sizeNode.getNodeName().equals("size")) {
                            Element sizeElement = (Element) sizeNode;
                            System.out.println(String.format("%s(%sx%s):%s", sizeElement.getAttribute("label"), sizeElement.getAttribute("width"),
                                    sizeElement.getAttribute("height"), sizeElement.getAttribute("source")));
                        }
                    }
                }
            }
        } else {
            error(sizeRsp.getChildNodes());
        }
    }

    private static void error(NodeList nodeList) {

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equals("err")) {
                Element element = (Element) node;
                String errorCode = element.getAttribute("code");
                System.out.println("Oops, we got some error when uploading your photo:" + errorMsgMap.get(errorCode));
            }
        }
    }

    private static void checkParam(String[] args, Map<String, String> bodyParams) {
        if (args.length == 0) {
            System.out.println("Please specify the file you want to upload.");
            System.exit(-1);
        }

        if (args.length >= 2) {
            for (int i = 1; i < args.length; i++) {
                String[] paramValue = args[i].split("=");

                if (paramValue.length != 2) {
                    System.out.println("Invalid parameter....., go to https://github.com/khotyn/Nausicaa to check out how to use it.");
                    System.exit(-1);
                }

                Validator validator = paramValidators.get(paramValue[0]);

                if (validator == null) {
                    System.out.println("Invalid parameter....., go to https://github.com/khotyn/Nausicaa to check out how to use it.");
                    System.exit(-1);
                }

                if (!validator.validate(paramValue[1])) {
                    System.out.println("Invalid parameter....., go to https://github.com/khotyn/Nausicaa to check out how to use it.");
                    System.exit(-1);
                }

                if (paramValue[0].equalsIgnoreCase("safetyLevel")) {
                    if (paramValue[1].equalsIgnoreCase("safe")) {
                        bodyParams.put(paramValue[0], String.valueOf(0));
                    } else if (paramValue[1].equalsIgnoreCase("middle")) {
                        bodyParams.put(paramValue[0], String.valueOf(1));
                    } else if (paramValue[1].equalsIgnoreCase("restricted")) {
                        bodyParams.put(paramValue[0], String.valueOf(2));
                    }
                } else if (paramValue[0].equalsIgnoreCase("contentType")) {
                    if (paramValue[1].equalsIgnoreCase("photo")) {
                        bodyParams.put(paramValue[0], String.valueOf(0));
                    } else if (paramValue[1].equalsIgnoreCase("screenshot")) {
                        bodyParams.put(paramValue[0], String.valueOf(1));
                    } else if (paramValue[1].equalsIgnoreCase("other")) {
                        bodyParams.put(paramValue[0], String.valueOf(2));
                    }
                } else {
                    bodyParams.put(paramValue[0], paramValue[1]);
                }
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
