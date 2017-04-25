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

import static de.codengine.tankerkoenig.models.mapper.GsonMapperUtil.getAsString;

import java.lang.reflect.Type;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.codengine.tankerkoenig.exception.ResponseParsingException;

final class OpeningTimeDeserializer implements JsonDeserializer<OpeningTime>
{
   @Override
   public OpeningTime deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext context) throws JsonParseException
   {
      final JsonObject jObject = jsonElement.getAsJsonObject();

      final String start = getAsString(jObject, "start");
      final String end = getAsString(jObject, "end");
      final String text = getAsString(jObject, "text");

      Set<DayOfWeekWithHoliday> days = null;
      boolean includesHolidays = false;

      if (text != null && !text.isEmpty())
      {
         switch (text)
         {
            case "täglich ausser Feiertag":
               days = allDays();
               break;
            case "täglich":
               days = allDays();
               includesHolidays = true;
               break;
            case "täglich ausser Sonn- und Feiertagen":
               days = allDays();
               days.remove(DayOfWeekWithHoliday.SUNDAY);
               days.remove(DayOfWeekWithHoliday.HOLIDAY);
               break;
            default:
               try
               {
                  days = parseDays(text);
                  if (days != null && days.contains(DayOfWeekWithHoliday.HOLIDAY))
                  {
                     includesHolidays = true;
                  }
               }
               catch (Exception e)
               {
                  days = null;
               }
               break;
         }
      }

      final Set<DayOfWeek> daysOfWeek = convertDaysOfWeek(days);

      return new OpeningTime(text, daysOfWeek, start, end, includesHolidays);
   }

   private static Set<DayOfWeek> convertDaysOfWeek(final Set<DayOfWeekWithHoliday> days)
   {
      final Set<DayOfWeek> converted = days == null ? null : days.stream()
            .filter(dayOfWeekWithHoliday -> dayOfWeekWithHoliday != DayOfWeekWithHoliday.HOLIDAY)
            .map(DayOfWeekWithHoliday::getDayOfWeek)
            .collect(Collectors.toCollection(TreeSet::new));

      return converted == null ? null : Collections.unmodifiableSet(converted);
   }

   private static Set<DayOfWeekWithHoliday> parseDays(final String text)
   {
      if (text.contains("-"))
      {
         return parseDayRange(text);
      }
      else if (text.contains(","))
      {
         return parseDaysSeparated(text, ",");
      }
      else
      {
         final int dayAsInt = dayToInt(text);
         if (dayAsInt == -1)
         {
            throw new ResponseParsingException(text, "Opening times");
         }

         return new TreeSet<>(Collections.singletonList(DayOfWeekWithHoliday.of(dayAsInt)));
      }
   }

   private static Set<DayOfWeekWithHoliday> allDays()
   {
      return Arrays.stream(DayOfWeekWithHoliday.values())
            .collect(Collectors.toCollection(TreeSet::new));
   }

   private static Set<DayOfWeekWithHoliday> parseDaysSeparated(final String text, final String delimiter)
   {
      final String[] days = text.split(delimiter);
      return Arrays.stream(days).map(String::trim)
            .map(OpeningTimeDeserializer::dayToInt)
            .map(DayOfWeekWithHoliday::of)
            .collect(Collectors.toCollection(TreeSet::new));
   }

   private static Set<DayOfWeekWithHoliday> parseDayRange(final String text)
   {
      final String from = text.substring(0, text.indexOf("-"));
      final int fromInt = dayToInt(from);

      final String to = text.substring(text.indexOf("-") + 1);
      final int toInt = dayToInt(to);

      if (fromInt == -1 || toInt == -1)
      {
         throw new ResponseParsingException(text, "Opening times");
      }

      return getDaysOfRange(fromInt, toInt);
   }

   private static Set<DayOfWeekWithHoliday> getDaysOfRange(final int fromInt, final int toInt)
   {
      return IntStream.range(fromInt, toInt + 1)
            .mapToObj(DayOfWeekWithHoliday::of)
            .collect(Collectors.toCollection(TreeSet::new));
   }

   private static int dayToInt(final String day)
   {
      switch (day)
      {
         case "Montag":
         case "Mo":
            return 1;
         case "Dienstag":
         case "Di":
            return 2;
         case "Mittwoch":
         case "Mi":
            return 3;
         case "Donnerstag":
         case "Do":
            return 4;
         case "Freitag":
         case "Fr":
            return 5;
         case "Samstag":
         case "Sa":
            return 6;
         case "Sonntag":
         case "So":
            return 7;
         case "Feiertag":
            return 8;
         default:
            return -1;
      }
   }

   public enum DayOfWeekWithHoliday
   {
      MONDAY(DayOfWeek.MONDAY),
      TUESDAY(DayOfWeek.TUESDAY),
      WEDNESDAY(DayOfWeek.WEDNESDAY),
      THURSDAY(DayOfWeek.THURSDAY),
      FRIDAY(DayOfWeek.FRIDAY),
      SATURDAY(DayOfWeek.SATURDAY),
      SUNDAY(DayOfWeek.SUNDAY),
      HOLIDAY(null);

      private static final DayOfWeekWithHoliday[] ENUMS = DayOfWeekWithHoliday.values();
      private final DayOfWeek dayOfWeek;

      DayOfWeekWithHoliday(final DayOfWeek sunday)
      {

         dayOfWeek = sunday;
      }

      public DayOfWeek getDayOfWeek()
      {
         return dayOfWeek;
      }

      public static DayOfWeekWithHoliday of(int dayOfWeek)
      {
         if (dayOfWeek < 1 || dayOfWeek > 8)
         {
            throw new DateTimeException("Invalid value for DayOfWeek: " + dayOfWeek);
         }
         return ENUMS[dayOfWeek - 1];
      }
   }
}
