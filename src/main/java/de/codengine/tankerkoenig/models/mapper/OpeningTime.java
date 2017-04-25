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

import java.time.DayOfWeek;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the opening times of a {@link Station}
 * <p>
 * Each OpeningTime represents a day range for that a specific time is valid.
 * A station can have zero to practically unlimited opening times.
 * <p>
 * If the station is opened 24h, start and end might be something like "00:00:00 - 23:59:00"
 */
public final class OpeningTime
{
   private final String text;
   private final Set<DayOfWeek> days;
   private final String start;
   private final String end;
   private final boolean includesHolidays;

   OpeningTime(final String text, final Set<DayOfWeek> days, final String start, final String end, final boolean includesHolidays)
   {
      this.text = text;
      this.days = days;
      this.start = start;
      this.end = end;
      this.includesHolidays = includesHolidays;
   }

   /**
    * Returns the original string of the opening time, which is useful in case there was an error
    * parsing the opening time the regular way
    */
   public String getText()
   {
      return text;
   }

   /**
    * Returns the unmodifyable set of days of the week for that the times are valid
    */
   public Optional<Set<DayOfWeek>> getDays()
   {
      if (days == null || days.isEmpty())
      {
         return Optional.empty();
      }

      return Optional.of(days);
   }

   /**
    * Returns the start time, which (unreliably) is in the format HH:MM:ss
    */
   public String getStart()
   {
      return start;
   }

   /**
    * Returns the end time, which (unreliably) is in the format HH:MM:ss
    */
   public String getEnd()
   {
      return end;
   }

   /**
    * Defines if the opening time includes holidays
    */
   public boolean includesHolidays()
   {
      return includesHolidays;
   }

   @Override
   public boolean equals(final Object o)
   {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;

      final OpeningTime that = (OpeningTime) o;

      if (getText() != null ? !getText().equals(that.getText()) : that.getText() != null)
         return false;
      if (!getDays().equals(that.getDays()))
         return false;
      if (!Objects.equals(includesHolidays(), that.includesHolidays()))
         return false;
      if (getStart() != null ? !getStart().equals(that.getStart()) : that.getStart() != null)
         return false;
      return getEnd() != null ? getEnd().equals(that.getEnd()) : that.getEnd() == null;
   }

   @Override
   public int hashCode()
   {
      int result = getText() != null ? getText().hashCode() : 0;
      result = 31 * result + getDays().hashCode();
      result = 31 * result + Boolean.hashCode(includesHolidays());
      result = 31 * result + (getStart() != null ? getStart().hashCode() : 0);
      result = 31 * result + (getEnd() != null ? getEnd().hashCode() : 0);
      return result;
   }
}
