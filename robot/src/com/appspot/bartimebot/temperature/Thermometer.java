package com.appspot.bartimebot.temperature;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Thermometer
{
	final static Logger LOG = Logger.getLogger(Thermometer.class.getName());

	public static int getTemperatureDifference(String blipText)
	{
		return getTemperatureDifference(blipText, 1);
	}
	
	public static int getTemperatureDifference(String blipText, int defaultWeight)
	{
		int result = 0;
		int weight = defaultWeight;
		
		String wordFiltered;
		
		for (String word : Arrays.asList(blipText.split("\\s")))
		{
			// reset weight
			weight = defaultWeight;
			
			// if there's a ! in the word, double the weight
			wordFiltered = word.replaceAll("!", "");
			if(!word.equals(wordFiltered))
				weight *= 2;
			
			if(word.toUpperCase().equals(word))
				result += weight / 2;
			
			// look up the word in the list
			if (WORDS.contains(word.toLowerCase()))
			{
				result += weight;
				LOG.warning("Found: " + word);
			}

			// remove all the symbols from the word
			wordFiltered = word.replaceAll("[^a-zA-Z0-9]", "");
			
			// if the word has changed, look it up again
			if (!word.equals(wordFiltered) && WORDS.contains(wordFiltered.toLowerCase()))
			{
				result += weight;
				LOG.warning("Found: " + wordFiltered);
			}
		}
		
		return result;
	}
	
	public static final List<String> WORDS = Arrays.asList(new String[] {
			"cunt",
			"motherfucker",
			"fuck",
			"wanker",
			"nigger",
			"bastard",
			"prick",
			"bollocks",
			"arsehole",
			"paki",
			"shag",
			"whore",
			"twat",
			"piss",
			"spastic",
			"slag",
			"shit",
			"dickhead",
			"pissedoff",
			"arse",
			"bugger",
			"balls",
			"jew",
			"sodding",
			"jesus",
			"christ",
			"crap",
			"bloody",
			"god",
			"@$$",
			"a$$",
			"as$",
			"a$s",
			"@$s",
			"@s$",
			"amcik",
			"andskota",
			"arschloch",
			"arse",
			"ass",
			"assho",
			"assram",
			"ayir",
			"bi+ch",
			"b!+ch",
			"b!tch",
			"b!7ch",
			"bi7ch",
			"b17ch",
			"b1+ch",
			"b1tch",
			"bitch",
			"bastard",
			"boiolas",
			"bollock",
			"breasts",
			"buceta",
			"butt-pirate",
			"cock",
			"c0ck",
			"cabron",
			"cawk",
			"cazzo",
			"chink",
			"chraa",
			"chuj",
			"cipa",
			"clits",
			"cum",
			"cunt",
			"damn",
			"d4mn",
			"dago",
			"daygo",
			"dego",
			"dick",
			"dike",
			"dildo",
			"dyke",
			"dirsa",
			"dupa",
			"dziwka",
			"ejac",
			"ekrem",
			"ekto",
			"enculer",
			"faen",
			"fag",
			"fanculo",
			"fanny",
			"fatass",
			"fat@$$",
			"fata$$",
			"fatas$",
			"fata$s",
			"fat@$s",
			"fat@s$",
			"fatarse",
			"fcuk",
			"feces",
			"feg",
			"felcher",
			"ficken",
			"fitt",
			"flikker",
			"foreskin",
			"fotze",
			"fu(",
			"fuck",
			"fuk",
			"futkretzn",
			"fux0r",
			"frig",
			"frigin",
			"friggin",
			"gay",
			"gaydar",
			"gook",
			"guiena",
			"h0r",
			"hax0r",
			"h4xor",
			"h4x0r",
			"hell",
			"helvete",
			"hoer",
			"honkey",
			"hore",
			"huevon",
			"hui",
			"injun",
			"jackass",
			"jism",
			"jizz",
			"kanker",
			"kawk",
			"kike",
			"klootzak",
			"knulle",
			"kuk",
			"kuksuger",
			"kurac",
			"kurwa",
			"kusi",
			"kyrpa",
			"l3i+ch",
			"l3itch",
			"l3i7ch",
			"l3!tch",
			"l3!+ch",
			"lesbian",
			"lesbo",
			"mamhoon",
			"masturbat",
			"merd",
			"mibun",
			"monkleigh",
			"motherfuck",
			"mofo",
			"mouliewop",
			"muie",
			"mulkku",
			"muschi",
			"nazi",
			"nepesaurio",
			"nigga",
			"nigger",
			"nutsack",
			"orospu",
			"paska",
			"penis",
			"perse",
			"phuck",
			"picka",
			"pierdol",
			"pillu",
			"pimmel",
			"pimpis",
			"piss",
			"pizda",
			"poontsee",
			"poop",
			"porn",
			"p0rn",
			"pr0n",
			"preteen",
			"prick",
			"pula",
			"pule",
			"pusse",
			"pussy",
			"puta",
			"puto",
			"qahbeh",
			"queef",
			"queer",
			"qweef",
			"rautenberg",
			"schaffer",
			"scheiss",
			"schlampe",
			"schmuck",
			"screw",
			"scrotum",
			"shit",
			"sh!t",
			"sharmuta",
			"sharmute",
			"shemale",
			"shipal",
			"shiz",
			"skribz",
			"skurwysyn",
			"slut",
			"smut",
			"sphencter",
			"spic",
			"spierdalaj",
			"splooge",
			"suka",
			"teets",
			"b00b",
			"teez",
			"testicle",
			"titt",
			"tits",
			"twat",
			"vagina",
			"viag",
			"v1ag",
			"v14g",
			"vi4g",
			"vittu",
			"w00se",
			"wank",
			"wetback",
			"whoar",
			"whore",
			"wichser",
			"wop",
			"wtf",
			"yed",
			"zabourah",
			"rubbish",
			"lazy",
			"trash",
			"bored",
			"annoyed",
			"broken",
			"shithead",
			"beer",
			"beers",
			"drink",
			"stupid",
			"dumb",
			"dumbass",
			"jerk",
			"retard",
			"noob",
			"n00b",
			"stfu",
			"segfault",
			"npe",
			"null",
			"pointer",
	});
	
	public static void main(String[] args)
	{
		for(String sentence : SENTENCES)
		{
			System.out.println(getTemperatureDifference(sentence) + "\t" + sentence);
		}	
	}
	
	public static final String[] SENTENCES = new String[]{
		"Just letting you all here know i have decided to leave here. Other commitments in life have changed my thoughts on things. Actually i go further and say i am slowly leaving all forums and the whole internet bullshit.",
		"Waste of time and effort. Summer is coming and i want to enjoy life not sit in front of a computer. So thanks all for the fun. Thanks heaps to bummy for all the fun. Thanks to omniscient.... He knows... Thanks to Yuki. Its been fun dood. (never hated you, actually admire your strength as to not retaliate) And thanks to the rest....",
		"Fuck it. Might as well have  thread only to chat and flame on as this joint has turned to utter shit.",
		"So yea fuck it. Any asswhole want to have a bit of a dip then i will give it a go. Some fucker has to revive this shit whole.",
		"WHOA, i heard it was rosalicious birthday",
		"why not open up ms paint. make a caed",
		"good plan, Mongy",
		"let's have a few beers while we're at it",
		"i'll open your ASS",
		"and put my NOB IN",
		"You sublte little scamp",
		"I heard shes got her webcam up and is showing bumhole for all those that gaz her requesting it.",
		"I've broken a nail",
		"what do I do"
	};
}