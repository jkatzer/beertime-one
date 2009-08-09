package com.appspot.bartimebot.model;

//import com.google.wave.extensions.twitter.tweety.util.Util;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Model object that represents a Twitter Wave. It holds metadata such as the
 * wave id, wavelet id, Twitter username that is logged in for this wave, and
 * whether this wave is in Twitter Search mode or normal Timeline mode.
 *
 * @author mprasetya@google.com (Marcel Prasetya)
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class BartimeWave {

  /**
   * A prefix that we prepend to the primary key, the wave id, since Google
   * App Engine doesn't support primary key that starts with number.
   */
  private static final String BARTIME_WAVE_KEY_PREFIX = "BARTIME_";

  /**
   * The wave id that serves as the primary key.
   */
  @PrimaryKey
  @Persistent
  private String waveId;

  /**
   * The wavelet id where the robot resides as a participant.
   */
  @Persistent
  private String waveletId;
  
  /**
   * The 'temperature' of the conversation
   */
  @Persistent
  private Float conversationTemperature = 3.0f;

  public Float getConversationTemperature() {
	return conversationTemperature;
}

public void setConversationTemperature(Float conversationTemperature) {
	this.conversationTemperature = conversationTemperature;
}

/**
   * Helper method to fetch a {@link TwitterWave} object from the data store
   * for the given Wave.
   *
   * @param waveId The wave id.
   * @param waveletId The wavelet id.
   * @return The {@link TwitterWave} object associated with the given Wave.
   */
  @SuppressWarnings("unchecked")
  public static BartimeWave getBartimeWave(
      PersistenceManager manager,
      String waveId,
      String waveletId) {
    Query query = manager.newQuery(BartimeWave.class);
    query.setFilter("waveId == waveIdParam");
    query.declareParameters("String waveIdParam");
    String waveIdParam = BartimeWave.BARTIME_WAVE_KEY_PREFIX + waveId;
    List<BartimeWave> bartimeWaves = (List<BartimeWave>) query.execute(waveIdParam);

    if (!bartimeWaves.isEmpty() && bartimeWaves.get(0) != null) {
      return bartimeWaves.get(0);
    }

    return new BartimeWave(waveId, waveletId);
  }

  /**
   * Construct a TwitterWave object.
   *
   * @param waveId The wave id.
   * @param waveletId The wavelet id.
   * @param latestTweetId The id of the latest tweet that was submitted from the
   *     given wave.
   */
  public BartimeWave(String waveId, String waveletId) {
    setWaveId(waveId);
    this.waveletId = waveletId;
  }

  /**
   * Returns wave id of this Twitter Wave.
   *
   * @return The wave id.
   */
  public String getWaveId() {
    return waveId.replace(BARTIME_WAVE_KEY_PREFIX, "");
  }

  /**
   * Sets the wave id that this Twitter Wave is associated with.
   *
   * @param waveId The wave id.
   */
  public void setWaveId(String waveId) {
    this.waveId = BARTIME_WAVE_KEY_PREFIX + waveId;
  }

  /**
   * Returns the id of the wavelet where Tweety is a participant of.
   *
   * @return The wavelet id.
   */
  public String getWaveletId() {
    return waveletId;
  }

  /**
   * Sets the id of the wavelet where Tweety is a participant of.
   *
   * @param waveletId The wavelet id.
   */
  public void setWaveletId(String waveletId) {
    this.waveletId = waveletId;
  }

public void increaseTemperature(Float temperature) {
	this.conversationTemperature += temperature;
}
}
