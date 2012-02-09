/**
 * Project: Nausicaa
 * 
 * File Created at Feb 9, 2012
 * $Id$
 * 
 * This is some code written by khotyn, and you are free to distribute those code for any use.
 * Welcome to my website: http://khotyn.com
 * Free we will be!
 */
package com.khotyn.nausicaa.scribe;

/**
 * The validator to valid the safety level
 * 
 * @author khotyn
 * 
 */
public class SafetyLevelValidator implements Validator {

    @Override
    public boolean validate(String param) {
        return param.equalsIgnoreCase("safe") || param.equalsIgnoreCase("middle") || param.equalsIgnoreCase("restricted");
    }

}
