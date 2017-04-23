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
      final Set<DayOfWeek> days = text != null && !text.isEmpty() ? Collections.unmodifiableSet(parseDays(text)) : null;

      return new OpeningTime(days, start, end);
   }

   private static Set<DayOfWeek> parseDays(final String text)
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

         return new TreeSet<>(Collections.singletonList(DayOfWeek.of(dayAsInt)));
      }
   }

   private static Set<DayOfWeek> parseDaysSeparated(final String text, final String delimiter)
   {
      final String[] days = text.split(",");
      return Arrays.stream(days).map(String::trim)
            .map(OpeningTimeDeserializer::dayToInt)
            .map(DayOfWeek::of)
            .collect(Collectors.toCollection(TreeSet::new));
   }

   private static Set<DayOfWeek> parseDayRange(final String text)
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

   private static Set<DayOfWeek> getDaysOfRange(final int fromInt, final int toInt)
   {
      return IntStream.range(fromInt, toInt + 1)
            .mapToObj(DayOfWeek::of)
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
         default:
            return -1;
      }
   }
}
