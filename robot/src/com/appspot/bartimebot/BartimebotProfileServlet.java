// Copyright 2009 Google Inc. All Rights Reserved.
package com.appspot.bartimebot;

import com.google.wave.api.ProfileServlet;

/**
* A servlet that is used to fetch the profile information for Cartoony.
* 
*/
public class BartimebotProfileServlet extends ProfileServlet {

@Override
public String getRobotAvatarUrl() {
	return "http://bartimebot.appspot.com/favicon.ico";
}

@Override
public String getRobotName() {
	return "Bartime";
}

@Override
public String getRobotProfilePageUrl() {
	return "http://bartimebot.appspot.com";
}
}