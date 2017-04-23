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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import de.codengine.tankerkoenig.utils.FluentMap;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class StationTest
{
   private Station station;

   @Before
   public void setUp() throws Exception
   {
      station = new Station();
   }

   @After
   public void tearDown() throws Exception
   {
      station = null;
   }

   @Test
   public void isFinal()
   {
      assertThat(Station.class).isFinal();
   }

   @Test
   public void getName()
   {
      assertThat(station.getName()).isNotPresent();
      station.setName("Name");
      assertThat(station.getName()).isPresent().hasValue("Name");
   }

   @Test
   public void getId()
   {
      assertThat(station.getId()).isNull();
      station.setId("12345");
      assertThat(station.getId()).isEqualTo("12345");
   }

   @Test
   public void getLocation()
   {
      assertThat(station.getLocation()).isNull();

      final Location location = new Location();
      location.setLng(12.12);
      location.setLat(22.33);

      station.setLocation(location);
      assertThat(station.getLocation()).isSameAs(location);
   }

   @Test
   public void getBrand()
   {
      assertThat(station.getBrand()).isNotPresent();
      station.setBrand("Brand");
      assertThat(station.getBrand()).isPresent().hasValue("Brand");
   }

   @Test
   public void isOpen()
   {
      assertThat(station.isOpen()).isFalse();
      station.setOpen(true);
      assertThat(station.isOpen()).isTrue();
   }

   @Test
   public void getGasPrices()
   {
      assertThat(station.getGasPrices()).isNotPresent();
      final FluentMap<GasPrices.GasType, Float> gasPriceMap = new FluentMap<GasPrices.GasType, Float>()
            .with(GasPrices.GasType.DIESEL, 1.23f);
      GasPrices gasPrices = new GasPrices(gasPriceMap, GasPrices.Status.OPEN);
      station.setGasPrices(gasPrices);
      assertThat(station.getGasPrices()).isPresent().hasValue(gasPrices);
   }

   @Test
   public void getOpeningTimes()
   {
      assertThat(station.getOpeningTimes()).isNotPresent();
      final OpeningTime openingTime = new OpeningTime(Sets.newSet(DayOfWeek.MONDAY, DayOfWeek.TUESDAY), "12:00", "18:00");
      List<OpeningTime> openingTimes = Lists.newArrayList(openingTime);
      station.setOpeningTimes(openingTimes);
      assertThat(station.getOpeningTimes()).isPresent().hasValue(openingTimes);
   }

   @Test
   public void getOverridingOpeningTimes()
   {
      assertThat(station.getOverridingOpeningTimes()).isNotPresent();
      List<String> overridingOpeningTimes = Lists.newArrayList("foo", "bar");
      station.setOverridingOpeningTimes(overridingOpeningTimes);
      assertThat(station.getOverridingOpeningTimes()).isPresent().hasValue(overridingOpeningTimes);
   }

   @Test
   public void getWholeDay()
   {
      assertThat(station.isWholeDay()).isNotPresent();
      station.setWholeDay(true);
      assertThat(station.isWholeDay()).isPresent().hasValue(true);
   }

   @Test
   public void getPrice()
   {
      assertThat(station.getPrice()).isNotPresent();
      station.setPrice(1.23f);
      assertThat(station.getPrice()).isPresent().hasValue(1.23f);
   }

   @Test
   public void testEquals()
   {
      EqualsVerifier.forClass(Station.class)
            .withOnlyTheseFields("id")
            .suppress(Warning.NONFINAL_FIELDS)
            .verify();
   }
}