/*
 * MIT License
 *
 * Copyright (c) 2017 Stefan Hueg (Codengine)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.codengine.tankerkoenig.models.mapper;

import static de.codengine.tankerkoenig.utils.CustomAsserts.assertDefaultFailedResponseAsserts;
import static de.codengine.tankerkoenig.utils.CustomAsserts.assertOpeningTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Sets;
import org.junit.Test;

import de.codengine.tankerkoenig.exception.ResponseParsingException;
import de.codengine.tankerkoenig.models.requests.Result;
import de.codengine.tankerkoenig.models.requests.StationDetailResult;
import de.codengine.tankerkoenig.utils.ResourceLoader;

public class DetailJsonMappingTest extends MapperTest
{
   @Test
   public void isFinal()
   {
      assertThat(StationDetailResult.class).isFinal();
   }

   @Test
   public void detail() throws IOException
   {
      final String detailContent = ResourceLoader.readString("/detail.json");
      final StationDetailResult result = getMapper().fromJson(detailContent, StationDetailResult.class);
      assertThat(result.getStatus()).isPresent().hasValue(Result.ResponseStatus.OK);
      final Station station = result.getStation();
      assertThat(station.getId()).isEqualTo("51d4b660-a095-1aa0-e100-80009459e03a");
      assertThat(station.getName()).isPresent().hasValue("JET BERLIN HERZBERGSTR. 27");
      assertThat(station.getBrand()).isPresent().hasValue("JET");
      assertThat(station.isWholeDay()).isPresent().hasValue(true);
      assertThat(station.isOpen()).isTrue();

      final Optional<GasPrices> optGasPrices = station.getGasPrices();
      assertThat(optGasPrices).isPresent();
      final GasPrices gasPrices = optGasPrices.get();
      assertThat(gasPrices.getStatus())
            .isNull();

      for (final GasPrices.GasType gasType : GasPrices.GasType.values())
      {
         assertThat(gasPrices.hasPrice(gasType)).isTrue();
         assertThat(gasPrices.getPrice(gasType))
               .isPresent()
               .hasValue(1.009f);
      }

      final Location location = station.getLocation();
      assertThat(location).isNotNull();
      assertThat(location.getStreetName()).isEqualTo("HERZBERGSTR. 27");
      assertThat(location.getCity()).isEqualTo("BERLIN");
      assertThat(location.getZipCode()).isEqualTo(10365);
      assertThat(location.getHouseNumber()).isNotPresent();
      assertThat(location.getDistance()).isNotPresent();
      assertThat(location.getLat()).isEqualTo(52.5262);
      assertThat(location.getLng()).isEqualTo(13.4886);
      assertThat(location.getState()).isPresent().hasValue(State.deHE);

      final Optional<List<OpeningTime>> optOpeningTimes = station.getOpeningTimes();
      assertThat(optOpeningTimes).isPresent();
      final List<OpeningTime> openingTimes = optOpeningTimes.get();
      assertThat(openingTimes).hasSize(4);

      assertThatThrownBy(() -> openingTimes.add(new OpeningTime(Sets.newHashSet(), "1", "2")))
            .isInstanceOf(UnsupportedOperationException.class);

      final OpeningTime firstOpeningTime = openingTimes.get(0);
      assertOpeningTime(firstOpeningTime, "05:00:00", "23:00:00", DayOfWeek.MONDAY, DayOfWeek.TUESDAY);

      final OpeningTime secondOpeningTime = openingTimes.get(1);
      assertOpeningTime(secondOpeningTime, "05:00:00", "23:00:00", DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY);

      final OpeningTime thirdOpeningTime = openingTimes.get(2);
      assertOpeningTime(thirdOpeningTime, "05:00:00", "23:00:00", DayOfWeek.FRIDAY);

      final OpeningTime fourthOpeningTime = openingTimes.get(3);
      assertOpeningTime(fourthOpeningTime, "06:00:00", "23:00:00", DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

      final Optional<List<String>> optOverrides = station.getOverridingOpeningTimes();
      assertThat(optOverrides).isPresent();
      final List<String> overrides = optOverrides.get();
      assertThat(overrides).hasSize(2);

      assertThatThrownBy(() -> overrides.add("foo"))
            .isInstanceOf(UnsupportedOperationException.class);

      assertThat(overrides.get(0)).isEqualTo("13.01.2016, 08:00:00 - 13.01.2016, 19:00:00: geöffnet");
      assertThat(overrides.get(1)).isEqualTo("13.02.2016, 08:00:00 - 13.02.2016, 19:00:00: geöffnet");
   }

   @Test
   public void detailWithoutTimes() throws IOException
   {
      final String detailContent = ResourceLoader.readString("/detail_without_times.json");
      final StationDetailResult result = getMapper().fromJson(detailContent, StationDetailResult.class);
      assertThat(result.getStatus()).isPresent().hasValue(Result.ResponseStatus.OK);
      final Station station = result.getStation();
      assertThat(station.getId()).isEqualTo("51d4b660-a095-1aa0-e100-80009459e03a");

      assertThat(station.getOpeningTimes()).isNotPresent();
      assertThat(station.getOverridingOpeningTimes()).isNotPresent();
   }

   @Test
   public void detailWithFaultyDay() throws IOException
   {
      final String detailContent = ResourceLoader.readString("/detail_faulty_day.json");
      assertThatThrownBy(() -> getMapper().fromJson(detailContent, StationDetailResult.class))
            .isExactlyInstanceOf(ResponseParsingException.class);
   }

   @Test
   public void detailWithFaultyDayRange() throws IOException
   {
      final String detailContent = ResourceLoader.readString("/detail_faulty_day_range.json");
      assertThatThrownBy(() -> getMapper().fromJson(detailContent, StationDetailResult.class))
            .isExactlyInstanceOf(ResponseParsingException.class);
   }

   @Test
   public void withError() throws IOException
   {
      final String complaintContent = ResourceLoader.readString("/fail_response.json");
      final StationDetailResult result = getMapper().fromJson(complaintContent, StationDetailResult.class);
      assertDefaultFailedResponseAsserts(result);
      assertThat(result.getStation()).isNull();
   }
}
