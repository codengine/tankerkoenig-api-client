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

import java.util.HashMap;

import org.junit.Test;

import de.codengine.tankerkoenig.utils.FluentMap;

public class GasPricesTest
{
   @Test
   public void isFinal()
   {
      assertThat(GasPrices.class)
            .isFinal();
   }

   @Test
   public void getPrice()
   {
      FluentMap<GasPrices.GasType, Float> gasPrices = new FluentMap<GasPrices.GasType, Float>()
            .with(GasPrices.GasType.DIESEL, 1.23f)
            .with(GasPrices.GasType.E5, null);

      GasPrices prices = new GasPrices(gasPrices, GasPrices.Status.OPEN);
      assertThat(prices.getPrice(GasPrices.GasType.E5)).isNotPresent();
      assertThat(prices.getPrice(GasPrices.GasType.E10)).isNotPresent();
      assertThat(prices.getPrice(GasPrices.GasType.DIESEL)).isPresent().hasValue(1.23f);
   }

   @Test
   public void getPriceWithEmptyMap()
   {
      GasPrices prices = new GasPrices(new HashMap<>(), GasPrices.Status.OPEN);
      assertThat(prices.getPrice(GasPrices.GasType.DIESEL)).isNotPresent();
      assertThat(prices.getPrice(null)).isNotPresent();
   }

   @Test
   public void hasPrice()
   {
      FluentMap<GasPrices.GasType, Float> gasPrices = new FluentMap<GasPrices.GasType, Float>()
            .with(GasPrices.GasType.DIESEL, 1.23f)
            .with(GasPrices.GasType.E5, null);

      GasPrices prices = new GasPrices(gasPrices, GasPrices.Status.OPEN);
      assertThat(prices.hasPrice(GasPrices.GasType.DIESEL)).isTrue();
      assertThat(prices.hasPrice(GasPrices.GasType.E5)).isFalse();
      assertThat(prices.hasPrice(GasPrices.GasType.E10)).isFalse();
   }

   @Test
   public void hasPrices()
   {
      FluentMap<GasPrices.GasType, Float> emptyMap = new FluentMap<>();
      FluentMap<GasPrices.GasType, Float> mapWithPrice = new FluentMap<GasPrices.GasType, Float>()
            .with(GasPrices.GasType.DIESEL, 1.23f);
      FluentMap<GasPrices.GasType, Float> mapWithNull = new FluentMap<GasPrices.GasType, Float>()
            .with(GasPrices.GasType.DIESEL, null);
      FluentMap<GasPrices.GasType, Float> mapWithFloatAndNull = new FluentMap<GasPrices.GasType, Float>()
            .with(GasPrices.GasType.DIESEL, 1.23f)
            .with(GasPrices.GasType.E5, null);

      GasPrices pricesWithEmptyMap = new GasPrices(emptyMap, GasPrices.Status.OPEN);
      assertThat(pricesWithEmptyMap.hasPrices()).isFalse();

      GasPrices pricesWithOnePrice = new GasPrices(mapWithPrice, GasPrices.Status.OPEN);
      assertThat(pricesWithOnePrice.hasPrices()).isTrue();

      GasPrices pricesWithNull = new GasPrices(mapWithNull, GasPrices.Status.OPEN);
      assertThat(pricesWithNull.hasPrices()).isFalse();

      GasPrices pricesWithFloatAndNull = new GasPrices(mapWithFloatAndNull, GasPrices.Status.OPEN);
      assertThat(pricesWithFloatAndNull.hasPrices()).isTrue();
   }

   @Test
   public void getStatus()
   {
      GasPrices prices = new GasPrices(new HashMap<>(), GasPrices.Status.OPEN);
      assertThat(prices.getStatus())
            .isEqualTo(GasPrices.Status.OPEN);
   }

}