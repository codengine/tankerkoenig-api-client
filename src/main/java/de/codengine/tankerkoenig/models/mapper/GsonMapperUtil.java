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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

final class GsonMapperUtil
{
   private GsonMapperUtil()
   {
      throw new UnsupportedOperationException();
   }

   static String getAsString(final JsonObject jObject, final String key)
   {
      return nullSafeGet(JsonElement::getAsString, jObject, key, null);
   }

   static boolean getAsBoolean(final JsonObject jObject, final String key)
   {
      return getAsBoolean(jObject, key, false);
   }

   static Boolean getAsBoolean(final JsonObject jObject, final String key, final Boolean defaultValue)
   {
      return nullSafeGet(JsonElement::getAsBoolean, jObject, key, defaultValue);
   }

   static Float getAsFloat(final JsonObject jObject, final String key, final Float defaultValue)
   {
      return nullSafeGet(JsonElement::getAsFloat, jObject, key, defaultValue);
   }

   private static <T> T nullSafeGet(final Function<JsonElement, T> getter, final JsonObject jObject, final String key, final T defaultValue)
   {
      final JsonElement value = jObject.get(key);
      return value != null && !(value instanceof JsonNull) ? getter.apply(value) : defaultValue;
   }

   static <T, C extends Collection<T>> C nodeMemberCollection(final T[] values, final Function<Collection<T>, C> collectionSupplier)
   {
      final Collection<T> valueCollection = (values == null || values.length == 0) ? Collections.emptyList() : Arrays.asList(values);
      return collectionSupplier.apply(valueCollection);
   }
}
