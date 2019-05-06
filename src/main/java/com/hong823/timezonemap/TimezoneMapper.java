package com.hong823.timezonemap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hong823.timezonemap.exceptions.ConfigException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TimezoneMapper {

  private static final String TIMEZONE_PROVIDER_FILE = "timezone-provider.json";
  private static final String TIMEZONE_MAPPING_FILE = "timezone-mapping.json";

  private List<String> providerTimezone;
  private Map<String, List<String>> timezoneMaps;

  public TimezoneMapper(String configPath, String provider) {
    this.providerTimezone = configureProviderTimezone(timezoneProviderPath(configPath), provider);
    this.timezoneMaps = configureTimezoneMaps(timezoneMapsPath(configPath), provider);
  }

  static String timezoneProviderPath(String configPath) {
    return String.format("%s/%s", configPath, TIMEZONE_PROVIDER_FILE);
  }

  static String timezoneMapsPath(String configPath) {
    return String.format("%s/%s", configPath, TIMEZONE_MAPPING_FILE);
  }

  static List<String> configureProviderTimezone(String file, String provider) {
    ObjectMapper mapper = new ObjectMapper();

    Map<String, List<String>> providerTimezoneFromFile;

    try {
      providerTimezoneFromFile =
          mapper.readValue(new File(file), new TypeReference<Map<String, List<String>>>() {});
    } catch (IOException e) {
      throw new ConfigException(e.getMessage());
    }

    return providerTimezoneFromFile.get(provider) != null
        ? providerTimezoneFromFile.get(provider)
        : Collections.<String>emptyList();
  }

  static Map<String, List<String>> configureTimezoneMaps(String file, String provider) {
    ObjectMapper mapper = new ObjectMapper();

    Map<String, Map<String, List<String>>> timezoneMapsFromFile;

    try {
      timezoneMapsFromFile =
          mapper.readValue(
              new File(file), new TypeReference<Map<String, Map<String, List<String>>>>() {});
    } catch (IOException e) {
      throw new ConfigException(e.getMessage());
    }

    return timezoneMapsFromFile.get(provider) != null
        ? timezoneMapsFromFile.get(provider)
        : Collections.<String, List<String>>emptyMap();
  }

  public String map(String timezone) {
    // Matches the provider's timezone
    if (providerTimezone.contains(timezone)) return timezone;

    // Looking for the mapping (if any)
    for (Map.Entry<String, List<String>> entry : timezoneMaps.entrySet())
      if (entry.getValue().contains(timezone)) return entry.getKey();

    return null;
  }

  List<String> getProviderTimezone() {
    return providerTimezone;
  }

  Map<String, List<String>> getTimezoneMaps() {
    return timezoneMaps;
  }
}
