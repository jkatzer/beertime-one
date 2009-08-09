package com.appspot.bartimebot;

import java.util.Date;
import java.util.logging.Logger;

import com.appspot.bartimebot.temperature.Thermometer;
import com.google.wave.api.*;

@SuppressWarnings("serial")
public class BartimebotServlet extends AbstractRobotServlet
{
	private static final int TEMP_INITIAL = 0;
	private static final int TEMP_BASIC_INCREMENT = 10;
	
	private static final int TEMP_THRESHOLD_WARN = 80;
	private static final int TEMP_THRESHOLD_ACTION = 100;

	private static final String WARN_TEXT = "WARNING: The heat is on.";
	private static final String GADGET_ACTION = "http://betagunit.com/joe/bartime_gadget.xml";
	
	private static final int INTERVAL_TEMP_REDUCE = 60; // seconds
	private static final int TEMP_REDUCE_AMOUNT = 10;

	private static final String GADGET_GAUGE = "http://betagunit.com/temperature.xml?temperature=%d&maxTemperature=%d";
	
	final static Logger LOG = Logger.getLogger(BartimebotServlet.class.getName());

	@Override
	public void processEvents(RobotMessageBundle bundle)
	{
		Wavelet wavelet = bundle.getWavelet();

		if (bundle.wasSelfAdded())
		{
			wavelet.setDataDocument("temperature", String.valueOf(TEMP_INITIAL));
			wavelet.setDataDocument("lastReductionTime", String.valueOf((new Date()).getTime()));
		}

		for (Event event : bundle.getBlipSubmittedEvents())
		{
			// parse blip text and increase temperature if applicable
			int temperature = Integer.parseInt(wavelet.getDataDocument("temperature"));
			final int initialTemperature = temperature;
			Blip blip = event.getBlip();
			temperature += Thermometer.getTemperatureDifference(blip.getDocument().getText(), TEMP_BASIC_INCREMENT);
			wavelet.setDataDocument("temperature", "" + temperature);
			LOG.warning("Conversation temperature: " + temperature);

			// periodic temperature decrease
			long lastReductionTimestamp = Long.parseLong(wavelet.getDataDocument("lastReductionTime"));
			long currentTimestamp = (new Date()).getTime();	// milliseconds
			long timeDifference = (currentTimestamp - lastReductionTimestamp) / 1000;	// seconds
			while (timeDifference >= INTERVAL_TEMP_REDUCE)
			{
				timeDifference -= INTERVAL_TEMP_REDUCE;
				temperature -= TEMP_REDUCE_AMOUNT;
				LOG.warning("Temperature reduced by " + TEMP_REDUCE_AMOUNT);
			}

			// ensuring upper bound
			temperature = Math.min(temperature, initialTemperature + (TEMP_THRESHOLD_ACTION / 4));
			
			// ensuring lower bound
			temperature = Math.max(temperature, 0);

			// if temperature has changed, reload the widget
			if (temperature != initialTemperature)
			{
				String gaugeGadgetUrl = String.format(GADGET_GAUGE, temperature, TEMP_THRESHOLD_ACTION);
				Gadget gaugeGadget = new Gadget(gaugeGadgetUrl);
				gaugeGadget.setProperty("temperature", String.valueOf(temperature));
				gaugeGadget.setProperty("maxTemperature", String.valueOf(TEMP_THRESHOLD_ACTION));
				Blip gaugeBlip = blip.createChild();
				gaugeBlip.getDocument().getGadgetView().append(gaugeGadget);
			}

			// set the temperature finally
			wavelet.setDataDocument("temperature", "" + temperature);
			
			// Action!
			if(temperature >= TEMP_THRESHOLD_WARN && !wavelet.hasDataDocument("warned"))
			{
				Blip warnBlip = wavelet.appendBlip();
				warnBlip.getDocument().append(WARN_TEXT);
				wavelet.setDataDocument("warned", "1");
			}
			else if(temperature >= TEMP_THRESHOLD_ACTION && !wavelet.hasDataDocument("beertime"))
			{
				Blip warnBlip = wavelet.appendBlip();
				Gadget actionGadget = new Gadget(GADGET_ACTION);
				warnBlip.getDocument().getGadgetView().append(actionGadget);
				wavelet.setDataDocument("beertime", "1");
			}
		}
	}
}