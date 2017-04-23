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

import java.util.Map;
import java.util.Optional;

import com.google.gson.annotations.SerializedName;

/**
 * A list of Gas Prices which depends on the requested gas price types.
 * E.g. if E5 and E10 are requested, Diesel will not be available in this container
 * <p>
 * If the station is closed, no gas prices will be available.
 */
public final class GasPrices
{
   private final Map<GasType, Float> prices;
   private final Status status;

   GasPrices(final Map<GasType, Float> prices, final Status status)
   {
      this.prices = prices;
      this.status = status;
   }

   /**
    * Returns the gas price, if available
    */
   public Optional<Float> getPrice(final GasType gasType)
   {
      return Optional.ofNullable(prices.get(gasType));
   }

   /**
    * Determines if a gas price of a certain type is available
    */
   public boolean hasPrice(final GasType gasType)
   {
      return prices.containsKey(gasType) && prices.get(gasType) != null;
   }

   /**
    * Determines if any price is available
    */
   public boolean hasPrices()
   {
      return prices.size() > 0 && prices.entrySet().stream()
            .anyMatch(entry -> entry.getValue() != null);
   }

   /**
    * Returns the stations status / availability.
    * If it returns NOT_FOUND, the supplied ID at the request was wrong or the station
    * is temporarily unavailable
    */
   public Status getStatus()
   {
      return status;
   }

   /**
    * A set of requestable gas types
    */
   public enum GasType
   {
      DIESEL, E5, E10
   }

   /**
    * A set of possible status flags of the station
    */
   public enum Status
   {
      /**
       * The gas station could not be found
       */
      @SerializedName("not found")
      NOT_FOUND,
      /**
       * The gas station is closed
       */
      @SerializedName("closed")
      CLOSED,
      /**
       * The gas station is opened
       */
      @SerializedName("open")
      OPEN
   }
}
