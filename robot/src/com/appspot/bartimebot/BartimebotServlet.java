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
	
	private static final int INTERVAL_TEMP_REDUCE = 30; // seconds
	private static final int TEMP_REDUCE_AMOUNT = 2;

	final static Logger LOG = Logger.getLogger(BartimebotServlet.class.getName());

	@Override
	public void processEvents(RobotMessageBundle bundle)
	{
		Wavelet wavelet = bundle.getWavelet();
		
		if (bundle.wasSelfAdded())
		{
			// put state in wavelet
			wavelet.setDataDocument("temperature", String.valueOf(TEMP_INITIAL));
			wavelet.setDataDocument("lastReductionTime", String.valueOf((new Date()).getTime()));
		}
		
		for (Event event : bundle.getBlipSubmittedEvents())
		{
			Blip blip = event.getBlip();
			String blipText = blip.getDocument().getText();
			
			final int initialTemperature = Integer.parseInt(wavelet.getDataDocument("temperature"));
			int temperature = initialTemperature;
			
			// periodic temperature decrease
			long lastReductionTimestamp = Long.parseLong(wavelet.getDataDocument("lastReductionTime"));
			long currentTimestamp = (new Date()).getTime(); // milliseconds
			int timeDifference = (int) ((currentTimestamp - lastReductionTimestamp) / 1000); // seconds
			while (timeDifference >= INTERVAL_TEMP_REDUCE)
			{
				temperature -= TEMP_REDUCE_AMOUNT;
				timeDifference -= INTERVAL_TEMP_REDUCE;
				lastReductionTimestamp += (INTERVAL_TEMP_REDUCE * 1000);
				wavelet.setDataDocument("lastReductionTime", String.valueOf(lastReductionTimestamp));
				
				LOG.info("Temperature reduced by " + TEMP_REDUCE_AMOUNT);
				// LOG.info("Current Timestamp (ms): " + currentTimestamp);
				// LOG.info("Last reduction Timestamp (ms): " + lastReductionTimestamp);
				LOG.info("Calculated time difference (seconds): " + timeDifference);
				// LOG.info("Expected time difference (ms): " + (currentTimestamp - lastReductionTimestamp));
			}
			
			// ensuring lower bound
			temperature = Math.max(temperature, 0);
			
			// temperature increase
			temperature += Thermometer.getTemperatureDifference(blipText, TEMP_BASIC_INCREMENT);
			wavelet.setDataDocument("temperature", "" + temperature);

			// ensuring upper bound
			temperature = Math.min(temperature, initialTemperature + (TEMP_THRESHOLD_ACTION / 4));
			
			LOG.warning("Conversation temperature: " + temperature);

			// store the final temperature
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
			
			// process special switches
			if(blipText.startsWith("/temp"))
			{
				blip.getDocument().replace(new Range(0, 5), "Temperature: " + wavelet.getDataDocument("temperature") + "\n");
			}
			
			if(blipText.startsWith("/reset"))
			{
				wavelet.setDataDocument("temperature", "0");
				blip.getDocument().replace(new Range(0, 6), "Temperature: " + wavelet.getDataDocument("temperature") + "\n");
				LOG.warning("Conversation temperature has been reset.");
			}
		}
	}
}