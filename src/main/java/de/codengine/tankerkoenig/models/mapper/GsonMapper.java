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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Default JSON Mapper which utilizes {@link Gson}
 */
public final class GsonMapper implements JsonMapper
{
   private final static Gson gson;

   static
   {
      gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .registerTypeAdapter(OpeningTime.class, new OpeningTimeDeserializer())
            .registerTypeAdapter(Station.class, new StationDeserializer())
            .registerTypeAdapter(GasPrices.class, new GasPricesDeserializer())
            .create();
   }

   private final static GsonMapper ourInstance = new GsonMapper();

   private GsonMapper()
   {
   }

   public static GsonMapper getInstance()
   {
      return ourInstance;
   }

   /**
    * Converts the supplied JSON string to the result class
    *
    * @param json        The json string
    * @param resultClass The expected result class
    * @param <T>         The type of the return object
    * @return Returns the mapped result object
    */
   @Override
   public <T> T fromJson(final String json, final Class<T> resultClass)
   {
      return gson.fromJson(json, resultClass);
   }
}
