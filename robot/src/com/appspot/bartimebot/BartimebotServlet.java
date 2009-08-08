package com.appspot.bartimebot;

import com.google.wave.api.AbstractRobotServlet;
import com.google.wave.api.Blip;
import com.google.wave.api.Event;
import com.google.wave.api.Image;
import com.google.wave.api.RobotMessageBundle;
import com.google.wave.api.TextView;
import com.google.wave.api.Wavelet;

@SuppressWarnings("serial")
public class BartimebotServlet extends AbstractRobotServlet {


	@Override
	public void processEvents(RobotMessageBundle bundle) {
		for (Event blipSubmittedEvent : bundle.getBlipSubmittedEvents()) {
	    	makeCartoonBlip(blipSubmittedEvent.getBlip());
	    }
		for(Event e : bundle.getEvents()) {
			e.getWavelet().appendBlip().getDocument().append( "there was an " + String.valueOf(e.getType()) + " event" );
		}
	}
	
	public void makeDebugBlip(Wavelet wavelet, String text) {
		TextView textView = wavelet.appendBlip().getDocument();
		textView.append(text + " in a cartoon!");
	}
	
	public void makeCartoonBlip(Blip blip) {
		String creator = blip.getCreator();
		String text = blip.getDocument().getText();
		Image image = new Image();
		image.setUrl(makeCartoonUrl(text, creator));
		
		blip.getDocument().delete();
		blip.getDocument().appendElement(image);
	}
	
	public String makeCartoonUrl(String text, String creator) {
		String base = "http://www.google.com/chart?chst=d_bubble_texts_big&chld=bb|" + makeColor(creator) + "|FFFFFF";
		base += "|" + text.replace(" ", "+");
		
		return base;
	}
	
	public String makeColor(String name) {
		String hex[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
		String color = "";
		for (int i = 0; i < Math.min(name.length(), 6); i++) {
			char code = name.charAt(i);
			int scaled = code % 16;
			color += hex[scaled];
		}
		return color;
	}
	
}
