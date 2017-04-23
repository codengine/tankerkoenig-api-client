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

import static de.codengine.tankerkoenig.models.mapper.GsonMapperUtil.getAsBoolean;
import static de.codengine.tankerkoenig.models.mapper.GsonMapperUtil.getAsFloat;
import static de.codengine.tankerkoenig.models.mapper.GsonMapperUtil.getAsString;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

final class StationDeserializer implements JsonDeserializer<Station>
{
   @Override
   public Station deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext context) throws JsonParseException
   {
      final JsonObject jObject = jsonElement.getAsJsonObject();
      final Station station = new Station();
      station.setName(getAsString(jObject, "name"));
      station.setId(getAsString(jObject, "id"));
      station.setOpen(getAsBoolean(jObject, "isOpen"));
      station.setBrand(getAsString(jObject, "brand"));
      station.setWholeDay(getAsBoolean(jObject, "wholeDay", null));
      station.setPrice(getAsFloat(jObject, "price", null));

      final Location location = mapLocation(context, jObject);
      station.setLocation(location);

      final GasPrices gasPrices = mapGasPrices(context, jObject);
      if (gasPrices.hasPrices())
      {
         station.setGasPrices(gasPrices);
      }

      final List<OpeningTime> openingTimeList = mapOpeningTimes(context, jObject);
      if (openingTimeList != null && !openingTimeList.isEmpty())
      {
         station.setOpeningTimes(Collections.unmodifiableList(openingTimeList));
      }

      final List<String> overridesList = mapOverrides(context, jObject);
      if (overridesList != null && !overridesList.isEmpty())
      {
         station.setOverridingOpeningTimes(Collections.unmodifiableList(overridesList));
      }

      return station;
   }

   private static List<String> mapOverrides(final JsonDeserializationContext context, final JsonObject jObject)
   {
      final String[] overrides = context.deserialize(jObject.get("overrides"), String[].class);
      return overrides == null ? null : GsonMapperUtil.nodeMemberCollection(overrides, ArrayList::new);
   }

   private static List<OpeningTime> mapOpeningTimes(final JsonDeserializationContext context, final JsonObject jObject)
   {
      final OpeningTime[] openingTimes = context.deserialize(jObject.get("openingTimes"), OpeningTime[].class);
      return openingTimes == null ? null : GsonMapperUtil.nodeMemberCollection(openingTimes, ArrayList::new);
   }

   private static GasPrices mapGasPrices(final JsonDeserializationContext context, final JsonObject jObject)
   {
      return context.deserialize(jObject, GasPrices.class);
   }

   private static Location mapLocation(final JsonDeserializationContext context, final JsonObject jObject)
   {
      return context.deserialize(jObject, Location.class);
   }
}
