package com.hong823.timezonemap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.hong823.timezonemap.exceptions.ConfigException;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class TimezoneMapperTest {

  private static final String CONFIG_PATH = "src/test/resources/config";

  private static final String TIMEZONE_PROVIDER_FILE = CONFIG_PATH + "/timezone-provider.json";
  private static final String TIMEZONE_MAPPING_FILE = CONFIG_PATH + "/timezone-mapping.json";
  private static final String TIMEZONE_INVALID_FILE = CONFIG_PATH + "/invalid.json";
  private static final String TIMEZONE_MISSING_FILE = CONFIG_PATH + "/invalid file";

  private static final String PROVIDER_1 = "provider1";
  private static final String PROVIDER_2 = "provider2";
  private static final String PROVIDER_NOT_FOUND = "not exist provider";

  private TimezoneMapper timezoneMapper;

  @Before
  public void setup() {
    timezoneMapper = new TimezoneMapper(CONFIG_PATH, PROVIDER_1);
  }

  @Test(expected = ConfigException.class)
  public void ConfigureProviderTimezone_WithoutFile_ShouldThrowException() {
    TimezoneMapper.configureProviderTimezone(TIMEZONE_INVALID_FILE, PROVIDER_1);
  }

  @Test(expected = ConfigException.class)
  public void ConfigureProviderTimezone_WithMissingFile_ShouldThrowException() {
    TimezoneMapper.configureProviderTimezone(TIMEZONE_MISSING_FILE, PROVIDER_1);
  }

  @Test(expected = ConfigException.class)
  public void ConfigureProviderTimezone_WithoutValidConfig_ShouldThrowException() {
    TimezoneMapper.configureProviderTimezone(TIMEZONE_INVALID_FILE, PROVIDER_1);
  }

  @Test
  public void ConfigureProviderTimezone_WithProviderNotFound_ShouldReturnEmpty() {
    List<String> result =
        TimezoneMapper.configureProviderTimezone(TIMEZONE_PROVIDER_FILE, PROVIDER_NOT_FOUND);

    assertEquals(0, result.size());
  }

  @Test
  public void ConfigureProviderTimezone_WithValidConfig_ShouldReturnConfig() {
    List<String> result =
        TimezoneMapper.configureProviderTimezone(TIMEZONE_PROVIDER_FILE, PROVIDER_1);

    assertEquals(3, result.size());
    assertEquals("Asia/Singapore", result.get(0));
    assertEquals("America/Chicago", result.get(1));
    assertEquals("Europe/Brussels", result.get(2));

    result = TimezoneMapper.configureProviderTimezone(TIMEZONE_PROVIDER_FILE, PROVIDER_2);

    assertEquals(4, result.size());
    assertEquals("America/Chicago", result.get(0));
    assertEquals("Europe/London", result.get(1));
    assertEquals("Asia/Tokyo", result.get(2));
    assertEquals("Asia/Kuala_Lumpur", result.get(3));
  }

  @Test(expected = ConfigException.class)
  public void ConfigureTimezoneMaps_WithoutFile_ShouldThrowException() {
    TimezoneMapper.configureTimezoneMaps(TIMEZONE_INVALID_FILE, PROVIDER_1);
  }

  @Test(expected = ConfigException.class)
  public void ConfigureTimezoneMaps_WithInvalidConfig_ShouldThrowException() {
    TimezoneMapper.configureTimezoneMaps(TIMEZONE_INVALID_FILE, PROVIDER_1);
  }

  @Test
  public void ConfigureTimezoneMaps_WithProviderNotFound_ShouldReturnEmpty() {
    Map<String, List<String>> result =
        TimezoneMapper.configureTimezoneMaps(TIMEZONE_MAPPING_FILE, PROVIDER_NOT_FOUND);

    assertEquals(0, result.size());
  }

  @Test
  public void ConfigureTimezoneMaps_WithValidConfig_ShouldReturnTimezoneMaps() {
    Map<String, List<String>> result =
        TimezoneMapper.configureTimezoneMaps(TIMEZONE_MAPPING_FILE, PROVIDER_1);

    assertEquals(3, result.size());

    List<String> tzResult = result.get("Asia/Singapore");
    assertEquals(1, tzResult.size());
    assertEquals("Asia/Kuala_Lumpur", tzResult.get(0));

    tzResult = result.get("Europe/Amsterdam");
    assertEquals(3, tzResult.size());
    assertEquals("Europe/Berlin", tzResult.get(0));
    assertEquals("Europe/Bern", tzResult.get(1));
    assertEquals("Europe/Rome", tzResult.get(2));

    tzResult = result.get("Asia/Shanghai");
    assertEquals(1, tzResult.size());
    assertEquals("Asia/Beijing", tzResult.get(0));

    result = TimezoneMapper.configureTimezoneMaps(TIMEZONE_MAPPING_FILE, PROVIDER_2);

    assertEquals(2, result.size());

    tzResult = result.get("America/Chicago");
    assertEquals(1, tzResult.size());
    assertEquals("America/Dallas", tzResult.get(0));

    tzResult = result.get("Asia/Japan");
    assertEquals(1, tzResult.size());
    assertEquals("Asia/Osaka", tzResult.get(0));
  }

  @Test
  public void WithInvalidProvider_ShouldInitialize() {
    timezoneMapper = new TimezoneMapper(CONFIG_PATH, PROVIDER_NOT_FOUND);

    assertEquals(0, timezoneMapper.getProviderTimezone().size());
    assertEquals(0, timezoneMapper.getTimezoneMaps().size());
  }

  @Test
  public void Map_WithInvalidTimezone_ShouldReturnNull() {
    assertThat(timezoneMapper.map("invalid timezone"), is(nullValue()));
  }

  @Test
  public void Map_WithProviderTimezone_ShouldReturnProviderTimezone() {
    assertEquals("America/Chicago", timezoneMapper.map("America/Chicago"));
  }

  @Test
  public void Map_WithPredefinedTimezoneForMapping_ShouldReturnProviderTimezone() {
    assertEquals("Asia/Singapore", timezoneMapper.map("Asia/Kuala_Lumpur"));
  }

  @Test
  public void
      Map_WithoutPredefinedTimezoneForMappingAndValidTimezone_ShouldReturnProviderTimezone() {
    assertThat(timezoneMapper.map("Europe/Copenhagen"), is(nullValue()));
  }
}
