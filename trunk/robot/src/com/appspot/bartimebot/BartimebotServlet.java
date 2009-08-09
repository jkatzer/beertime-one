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
	private static final int TEMP_REDUCE_AMOUNT = 1;

	private static final String GADGET_GAUGE = "http://betagunit.com/temperature.xml";
	
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
			Blip blip = event.getBlip();
			String blipText = blip.getDocument().getText();
			
			if(blipText.startsWith("/temp"))
			{
				blip.getDocument().replace(new Range(0, 5), "Temperature: " + wavelet.getDataDocument("temperature") + "\n");
			}
			
			final int initialTemperature = Integer.parseInt(wavelet.getDataDocument("temperature"));
			int temperature = initialTemperature;
			
			// periodic temperature decrease
			long lastReductionTimestamp = Long.parseLong(wavelet.getDataDocument("lastReductionTime"));
			long currentTimestamp = (new Date()).getTime();	// milliseconds
			long timeDifference = (currentTimestamp - lastReductionTimestamp) / 1000;	// seconds
			while (timeDifference >= INTERVAL_TEMP_REDUCE)
			{
				temperature -= TEMP_REDUCE_AMOUNT;
				LOG.warning("Temperature reduced by " + TEMP_REDUCE_AMOUNT);
				LOG.info("Calculated time difference (seconds): " + timeDifference);
				timeDifference -= INTERVAL_TEMP_REDUCE;
			}
			
			// temperature increase
			temperature += Thermometer.getTemperatureDifference(blipText, TEMP_BASIC_INCREMENT);
			wavelet.setDataDocument("temperature", "" + temperature);

			// ensuring upper bound
			temperature = Math.min(temperature, initialTemperature + (TEMP_THRESHOLD_ACTION / 4));
			
			// ensuring lower bound
			temperature = Math.max(temperature, 0);
			
			LOG.warning("Conversation temperature: " + temperature);

			// reload the widget IF temperature has changed 
			if (temperature != initialTemperature)
			{
				Gadget gaugeGadget = new Gadget(GADGET_GAUGE);
				gaugeGadget.setProperty("temperature", String.valueOf(temperature));
				gaugeGadget.setProperty("maxTemperature", String.valueOf(TEMP_THRESHOLD_ACTION));
				
				TextView rootDocument = wavelet.getRootBlip().getDocument();
				GadgetView gadgetView = rootDocument.getGadgetView();
				
				if(gadgetView == null)
					rootDocument.appendElement(gaugeGadget);
				else if(gadgetView.getGadget(GADGET_GAUGE) == null)
					gadgetView.append(gaugeGadget);
				else
					gadgetView.replace(gaugeGadget);
			}

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
		}
	}
}