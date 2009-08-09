package com.appspot.bartimebot;

import com.google.wave.api.ProfileServlet;

/**
 * A servlet that is used to fetch the profile information for Cartoony.
 * 
 */
@SuppressWarnings("serial")
public class BartimebotProfileServlet extends ProfileServlet
{
	@Override
	public String getRobotAvatarUrl()
	{
		return "http://bartimebot.appspot.com/avatar.jpg";
	}

	@Override
	public String getRobotName()
	{
		return "BarTime!";
	}

	@Override
	public String getRobotProfilePageUrl()
	{
		return "http://bartimebot.appspot.com";
	}
}