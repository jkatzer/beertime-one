package com.appspot.bartimebot;

import java.util.logging.Logger;
import com.appspot.bartimebot.temperature.Thermometer;
import com.google.wave.api.*;

@SuppressWarnings("serial")
public class BartimebotServlet extends AbstractRobotServlet
{
	private static final int TEMP_INITIAL = 0;
	private static final int TEMP_THRESHOLD_WARN = 8;
	private static final int TEMP_THRESHOLD_ACTION = 10;

	private static final String WARN_TEXT = "WARNING: The heat is on. It's almost the time to grab a beer!!";
	private static final String ACTION_GADGET_URL = "http://betagunit.com/bartime_gadget.xml";
	
	final static Logger LOG = Logger.getLogger(BartimebotServlet.class.getName());

	@Override
	public void processEvents(RobotMessageBundle bundle)
	{
		Wavelet wavelet = bundle.getWavelet();

		if (bundle.wasSelfAdded())
		{
			LOG.warning("I was added!!!");
			wavelet.setDataDocument("temperature", TEMP_INITIAL + "");
		}

		for (Event event : bundle.getBlipSubmittedEvents())
		{
			int temperature = Integer.parseInt(wavelet.getDataDocument("temperature"));
			Blip blip = event.getBlip();
			temperature += Thermometer.getTemperatureDifference(blip.getDocument().getText());
			wavelet.setDataDocument("temperature", "" + temperature);
			LOG.warning("Conversation temperature: " + temperature);
			
			if(temperature >= TEMP_THRESHOLD_WARN && !wavelet.hasDataDocument("warned"))
			{
				Blip warnBlip = wavelet.appendBlip();
				warnBlip.getDocument().append(WARN_TEXT);
				wavelet.setDataDocument("warned", "1");
			}
			
			if(temperature >= TEMP_THRESHOLD_ACTION && !wavelet.hasDataDocument("beertime"))
			{
				Blip warnBlip = wavelet.appendBlip();
				Gadget actionGadget = new Gadget(ACTION_GADGET_URL);
				warnBlip.getDocument().getGadgetView().append(actionGadget);
				wavelet.setDataDocument("beertime", "1");
			}
		}
	}
}