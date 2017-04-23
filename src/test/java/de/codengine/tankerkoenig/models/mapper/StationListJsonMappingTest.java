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
import static de.codengine.tankerkoenig.utils.CustomAsserts.assertDefaultSuccessResponseAsserts;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import de.codengine.tankerkoenig.models.requests.Result;
import de.codengine.tankerkoenig.models.requests.StationListResult;
import de.codengine.tankerkoenig.utils.ResourceLoader;

public class StationListJsonMappingTest extends MapperTest
{
   @Test
   public void isFinal()
   {
      assertThat(StationListResult.class).isFinal();
   }

   @Test
   public void listEmpty() throws IOException
   {
      final String listContent = ResourceLoader.readString("list_empty.json");
      final StationListResult emptyStationList = getMapper().fromJson(listContent, StationListResult.class);
      assertThat(emptyStationList.getStations()).isNotNull().isEmpty();
      assertThatThrownBy(() -> emptyStationList.getStations().add(new Station()))
            .isInstanceOf(UnsupportedOperationException.class);
      assertDefaultSuccessResponseAsserts(emptyStationList);
      assertThat(emptyStationList.getStatus()).isPresent().hasValue(Result.ResponseStatus.OK);
   }

   @Test
   public void listOnePrice() throws IOException
   {
      final String listContent = ResourceLoader.readString("list_one_price.json");
      final StationListResult stationList = getMapper().fromJson(listContent, StationListResult.class);
      final List<Station> stations = stationList.getStations();
      assertThat(stations).isNotNull().hasSize(2);
      assertThatThrownBy(() -> stations.add(new Station()))
            .isInstanceOf(UnsupportedOperationException.class);
      assertDefaultSuccessResponseAsserts(stationList);
      assertThat(stationList.getStatus()).isPresent().hasValue(Result.ResponseStatus.OK);

      // First Station
      final Station firstStation = stations.get(0);
      assertThat(firstStation.getId()).isEqualTo("51d4b660-a095-1aa0-e100-80009459e03a");
      assertThat(firstStation.getName()).isPresent().hasValue("JET BERLIN HERZBERGSTR. 27");
      assertThat(firstStation.getBrand())
            .isPresent()
            .hasValue("JET");
      assertThat(firstStation.getPrice())
            .isPresent()
            .hasValue(1.009f);
      assertThat(firstStation.isOpen()).isTrue();
      assertThat(firstStation.getOpeningTimes())
            .isNotPresent();
      assertThat(firstStation.getOverridingOpeningTimes())
            .isNotPresent();
      assertThat(firstStation.isWholeDay())
            .isNotPresent();

      final Location firstLocation = firstStation.getLocation();
      assertThat(firstLocation).isNotNull();
      assertThat(firstLocation.getStreetName()).isEqualTo("HERZBERGSTR. 27");
      assertThat(firstLocation.getCity()).isEqualTo("BERLIN");
      assertThat(firstLocation.getZipCode()).isEqualTo(10365);
      assertThat(firstLocation.getHouseNumber()).isNotPresent();
      assertThat(firstLocation.getDistance()).isPresent().hasValue(3.5);
      assertThat(firstLocation.getLat()).isEqualTo(52.5262);
      assertThat(firstLocation.getLng()).isEqualTo(13.4886);
      assertThat(firstLocation.getState()).isNotPresent();

      // Second Station
      final Station secondStation = stations.get(1);
      assertThat(secondStation.getId()).isEqualTo("1c4f126b-1f3c-4b38-9692-05c400ea8e61");
      assertThat(secondStation.getName()).isPresent().hasValue("Sprint Berlin Kniprodestr.");
      assertThat(secondStation.getBrand())
            .isNotPresent();
      assertThat(secondStation.getPrice())
            .isPresent()
            .hasValue(1.009f);
      assertThat(secondStation.isOpen()).isTrue();
      assertThat(secondStation.getOpeningTimes())
            .isNotPresent();
      assertThat(secondStation.getOverridingOpeningTimes())
            .isNotPresent();
      assertThat(secondStation.isWholeDay())
            .isNotPresent();

      final Location secondLocation = secondStation.getLocation();
      assertThat(secondLocation).isNotNull();
      assertThat(secondLocation.getStreetName()).isEqualTo("Kniprodestr.");
      assertThat(secondLocation.getCity()).isEqualTo("Berlin");
      assertThat(secondLocation.getZipCode()).isEqualTo(10407);
      assertThat(secondLocation.getHouseNumber()).isPresent().hasValue("25");
      assertThat(secondLocation.getDistance()).isPresent().hasValue(1.5);
      assertThat(secondLocation.getLat()).isEqualTo(52.533901);
      assertThat(secondLocation.getLng()).isEqualTo(13.4451);
      assertThat(secondLocation.getState()).isNotPresent();
   }

   @Test
   public void listAllPrices() throws IOException
   {
      final String listContent = ResourceLoader.readString("list_all_prices.json");
      final StationListResult stationList = getMapper().fromJson(listContent, StationListResult.class);
      final List<Station> stations = stationList.getStations();
      assertThat(stations).isNotNull().hasSize(2);
      assertThatThrownBy(() -> stations.add(new Station()))
            .isInstanceOf(UnsupportedOperationException.class);
      assertDefaultSuccessResponseAsserts(stationList);
      assertThat(stationList.getStatus()).isPresent().hasValue(Result.ResponseStatus.OK);

      // First Station
      final Station firstStation = stations.get(0);
      assertThat(firstStation.getId()).isEqualTo("51d4b660-a095-1aa0-e100-80009459e03a");
      assertThat(firstStation.getPrice())
            .isNotPresent();

      final Optional<GasPrices> optFirstGasPrices = firstStation.getGasPrices();
      assertThat(optFirstGasPrices).isPresent();
      final GasPrices firstGasPrices = optFirstGasPrices.get();
      assertThat(firstGasPrices.getStatus())
            .isNull();
      assertThat(firstGasPrices.hasPrices()).isTrue();
      assertThat(firstGasPrices.hasPrice(GasPrices.GasType.DIESEL)).isFalse();
      assertThat(firstGasPrices.getPrice(GasPrices.GasType.DIESEL))
            .isNotPresent();
      assertThat(firstGasPrices.hasPrice(GasPrices.GasType.E5)).isTrue();
      assertThat(firstGasPrices.getPrice(GasPrices.GasType.E5))
            .isPresent()
            .hasValue(1.289f);
      assertThat(firstGasPrices.hasPrice(GasPrices.GasType.E10)).isTrue();
      assertThat(firstGasPrices.getPrice(GasPrices.GasType.E10))
            .isPresent()
            .hasValue(1.269f);

      // Second Station
      final Station secondStation = stations.get(1);
      assertThat(secondStation.getId()).isEqualTo("1c4f126b-1f3c-4b38-9692-05c400ea8e61");
      assertThat(secondStation.getPrice())
            .isNotPresent();

      final Optional<GasPrices> optSecondGasPrices = secondStation.getGasPrices();
      assertThat(optSecondGasPrices).isPresent();
      final GasPrices secondGasPrices = optSecondGasPrices.get();
      assertThat(secondGasPrices.getStatus())
            .isNull();
      assertThat(secondGasPrices.hasPrices()).isTrue();
      assertThat(secondGasPrices.hasPrice(GasPrices.GasType.DIESEL)).isTrue();
      assertThat(secondGasPrices.getPrice(GasPrices.GasType.DIESEL))
            .isPresent()
            .hasValue(1.029f);
      assertThat(secondGasPrices.hasPrice(GasPrices.GasType.E5)).isTrue();
      assertThat(secondGasPrices.getPrice(GasPrices.GasType.E5))
            .isPresent()
            .hasValue(1.289f);
      assertThat(secondGasPrices.hasPrice(GasPrices.GasType.E10)).isFalse();
      assertThat(secondGasPrices.getPrice(GasPrices.GasType.E10))
            .isNotPresent();
   }

   @Test
   public void withError() throws IOException
   {
      final String complaintContent = ResourceLoader.readString("fail_response.json");
      final StationListResult result = getMapper().fromJson(complaintContent, StationListResult.class);
      assertDefaultFailedResponseAsserts(result);
   }
}