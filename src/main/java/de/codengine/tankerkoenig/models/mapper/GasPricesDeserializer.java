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

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

final class GasPricesDeserializer implements JsonDeserializer<GasPrices>
{
   @Override
   public GasPrices deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext context) throws JsonParseException
   {
      final JsonObject jObject = jsonElement.getAsJsonObject();
      final Map<GasPrices.GasType, Float> gasPriceMap = Collections.unmodifiableMap(mapGasTypes(jObject));
      final GasPrices.Status status = mapStatus(context, jObject);
      return new GasPrices(gasPriceMap, status);
   }

   private static GasPrices.Status mapStatus(final JsonDeserializationContext context, final JsonObject jObject)
   {
      return context.deserialize(jObject.get("status"), GasPrices.Status.class);
   }

   private static Map<GasPrices.GasType, Float> mapGasTypes(final JsonObject jObject)
   {
      final Map<GasPrices.GasType, Float> gasPriceMap = new HashMap<>();
      for (final GasPrices.GasType gasType : GasPrices.GasType.values())
      {
         final Float price = GsonMapperUtil.getAsFloat(jObject, gasType.name().toLowerCase(), null);
         if (price != null)
         {
            gasPriceMap.put(gasType, price);
         }
      }
      return gasPriceMap;
   }
}
