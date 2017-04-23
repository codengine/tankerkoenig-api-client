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

import static de.codengine.tankerkoenig.utils.CustomAsserts.assertDefaultFailedResponseAsserts;
import static de.codengine.tankerkoenig.utils.CustomAsserts.assertDefaultSuccessResponseAsserts;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import de.codengine.tankerkoenig.models.requests.PricesResult;
import de.codengine.tankerkoenig.utils.ResourceLoader;

public class PricesJsonMappingTest extends MapperTest
{
   @Test
   public void isFinal()
   {
      assertThat(PricesResult.class).isFinal();
   }

   @Test
   public void prices() throws IOException
   {
      final String pricesContent = ResourceLoader.readString("prices.json");
      final PricesResult result = getMapper().fromJson(pricesContent, PricesResult.class);
      final Map<String, GasPrices> gasPrices = result.getGasPrices();

      final Optional<GasPrices> firstGasPriceById = result.getGasPrice("1723edea-8e01-4de3-8c5e-ca227a49e2c3");
      assertThat(firstGasPriceById).isPresent();

      assertThat(gasPrices).isNotNull().isNotEmpty();
      assertDefaultSuccessResponseAsserts(result);
      assertThat(result.getStatus()).isNotPresent();

      assertThatThrownBy(() -> gasPrices.put("foo", null))
            .isInstanceOf(UnsupportedOperationException.class);

      assertThat(gasPrices.get("not_existing"))
            .isNull();

      final GasPrices firstPrices = gasPrices.get("1723edea-8e01-4de3-8c5e-ca227a49e2c3");
      assertThat(firstPrices).isSameAs(firstGasPriceById.get());

      final GasPrices secondPrices = gasPrices.get("51d4b660-a095-1aa0-e100-80009459e03a");
      final GasPrices thirdPrices = gasPrices.get("c9dc3f9b-e10a-47b4-a3fe-451b2cb1daad");

      assertThat(firstPrices.getStatus()).isEqualTo(GasPrices.Status.OPEN);
      assertThat(firstPrices.getPrice(GasPrices.GasType.E5)).isPresent().hasValue(1.234f);
      assertThat(firstPrices.getPrice(GasPrices.GasType.E10)).isPresent().hasValue(1.234f);
      assertThat(firstPrices.getPrice(GasPrices.GasType.DIESEL)).isPresent().hasValue(1.234f);

      assertThat(secondPrices.getStatus()).isEqualTo(GasPrices.Status.CLOSED);
      assertThat(secondPrices.getPrice(GasPrices.GasType.E5)).isPresent().hasValue(1.234f);
      assertThat(secondPrices.getPrice(GasPrices.GasType.E10)).isPresent().hasValue(1.234f);
      assertThat(secondPrices.getPrice(GasPrices.GasType.DIESEL)).isNotPresent();

      assertThat(thirdPrices.getStatus()).isEqualTo(GasPrices.Status.NOT_FOUND);
      assertThat(thirdPrices.hasPrices()).isFalse();
   }

   @Test
   public void pricesEmpty() throws IOException
   {
      final String pricesContent = ResourceLoader.readString("prices_empty.json");
      final PricesResult result = getMapper().fromJson(pricesContent, PricesResult.class);
      final Map<String, GasPrices> gasPrices = result.getGasPrices();
      assertThat(gasPrices).isNotNull().isEmpty();
      assertDefaultSuccessResponseAsserts(result);
      assertThat(result.getStatus()).isNotPresent();

      assertThatThrownBy(() -> gasPrices.put("foo", null))
            .isInstanceOf(UnsupportedOperationException.class);

      assertThat(gasPrices.get("not_existing"))
            .isNull();
   }

   @Test
   public void withError() throws IOException
   {
      final String complaintContent = ResourceLoader.readString("fail_response.json");
      final PricesResult result = getMapper().fromJson(complaintContent, PricesResult.class);
      assertDefaultFailedResponseAsserts(result);
   }
}
