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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LocationTest
{
   private Location location;

   @Before
   public void setUp() throws Exception
   {
      location = new Location();
   }

   @After
   public void tearDown() throws Exception
   {
      location = null;
   }

   @Test
   public void isFinal()
   {
      assertThat(Location.class).isFinal();
   }

   @Test
   public void getLat()
   {
      assertThat(location.getLat()).isEqualTo(0);
      location.setLat(12.23);
      assertThat(location.getLat()).isEqualTo(12.23);
   }

   @Test
   public void getLng()
   {
      assertThat(location.getLng()).isEqualTo(0);
      location.setLng(12.23);
      assertThat(location.getLng()).isEqualTo(12.23);
   }

   @Test
   public void getDistance()
   {
      assertThat(location.getDistance()).isNotPresent();
      location.setDistance(12.23);
      assertThat(location.getDistance()).isPresent().hasValue(12.23);
   }

   @Test
   public void getStreetName()
   {
      assertThat(location.getStreetName()).isNull();
      location.setStreetName("Musterstr.");
      assertThat(location.getStreetName()).isEqualTo("Musterstr.");
   }

   @Test
   public void getHouseNumber()
   {
      assertThat(location.getHouseNumber()).isNotPresent();
      location.setHouseNumber("45f");
      assertThat(location.getHouseNumber()).isPresent().hasValue("45f");
   }

   @Test
   public void getZipCode()
   {
      assertThat(location.getZipCode()).isNull();
      location.setZipCode(12345);
      assertThat(location.getZipCode()).isEqualTo(12345);
   }

   @Test
   public void getCity()
   {
      assertThat(location.getCity()).isNull();
      location.setCity("Berlin");
      assertThat(location.getCity()).isEqualTo("Berlin");
   }

   @Test
   public void getStateWithNull()
   {
      assertThat(location.getState()).isNotPresent();
   }

   @Test
   public void getStateWithValue()
   {
      location.setState(State.deHE);

      assertThat(location.getState()).isPresent().hasValue(State.deHE);
   }
}