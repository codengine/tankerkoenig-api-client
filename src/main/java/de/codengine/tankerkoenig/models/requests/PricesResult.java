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

package de.codengine.tankerkoenig.models.requests;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.google.gson.annotations.SerializedName;

import de.codengine.tankerkoenig.models.mapper.GasPrices;

/**
 * Result of {@link PricesRequest}. In case of success, {@link #isOk()} will return true.
 * <p>
 * Else, the error information are supplied as described in {@link BaseResult}
 */
public final class PricesResult extends BaseResult
{
   @SerializedName("prices")
   private Map<String, GasPrices> gasPrices;

   /**
    * Will return an unmodifyable map of gas prices, which uses the
    * Station ID as key
    */
   public Map<String, GasPrices> getGasPrices()
   {
      return Collections.unmodifiableMap(gasPrices);
   }

   /**
    * Will return the gas prices for a station, defined by the Station ID.
    */
   public Optional<GasPrices> getGasPrice(final String id)
   {
      return Optional.ofNullable(gasPrices.get(id));
   }
}
