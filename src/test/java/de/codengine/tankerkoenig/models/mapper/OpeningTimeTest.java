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
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import nl.jqno.equalsverifier.EqualsVerifier;

public class OpeningTimeTest
{
   @Test
   public void isFinal()
   {
      assertThat(OpeningTime.class).isFinal();
   }

   @Test
   public void getText()
   {
      OpeningTime openingTime = new OpeningTime("foo", null, null, null, true);
      assertThat(openingTime.getText()).isEqualTo("foo");
   }

   @Test
   public void getDays()
   {
      Set<DayOfWeek> days = Sets.newSet(DayOfWeek.MONDAY, DayOfWeek.TUESDAY);

      OpeningTime openingTime = new OpeningTime(null, days, null, null, true);
      assertThat(openingTime.getDays()).isPresent()
            .hasValueSatisfying(dayOfWeeks -> assertThat(dayOfWeeks).containsExactlyElementsOf(days));
   }

   @Test
   public void getDaysWithEmptyOrNull()
   {
      OpeningTime openingTime = new OpeningTime(null, null, null, null, true);
      assertThat(openingTime.getDays()).isNotPresent();

      OpeningTime openingTime2 = new OpeningTime(null, new HashSet<>(), null, null, true);
      assertThat(openingTime2.getDays()).isNotPresent();
   }

   @Test
   public void getStart()
   {
      OpeningTime openingTime = new OpeningTime(null, null, "12:00", null, true);
      assertThat(openingTime.getStart()).isEqualTo("12:00");
   }

   @Test
   public void getEnd()
   {
      OpeningTime openingTime = new OpeningTime(null, null, null, "18:00", true);
      assertThat(openingTime.getEnd()).isEqualTo("18:00");
   }

   @Test
   public void includesOpeningTimes()
   {
      OpeningTime openingTime = new OpeningTime(null, null, null, null, true);
      assertThat(openingTime.includesHolidays()).isTrue();
   }

   @Test
   public void testEquals()
   {
      EqualsVerifier.forClass(OpeningTime.class).verify();
   }
}