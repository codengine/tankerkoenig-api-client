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

import static de.codengine.tankerkoenig.utils.CustomAsserts.assertSingleton;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.codengine.tankerkoenig.utils.CustomAsserts;
import de.codengine.tankerkoenig.utils.FluentMap;

public class RequestParamBuilderTest
{
   @Test
   public void builderIsSingleton()
   {
      assertSingleton(RequestParamBuilder.class);
   }

   @Test
   public void buildEmptyMap()
   {
      final Map<String, Object> map = RequestParamBuilder.create().build();
      assertThat(map).isInstanceOf(HashMap.class).isNotNull().isEmpty();
   }

   @Test
   public void buildWithValues()
   {
      final Map<String, Object> variables = RequestParamBuilder.create()
            .addValue("test", "foo")
            .addValue("test", "bar")
            .addValue("complaint", CorrectionRequest.Type.WRONG_PETROL_STATION_BRAND)
            .build();

      final Map<String, Object> expectedParams = new FluentMap<String, Object>()
            .with("test", "bar")
            .with("complaint", "wrongPetrolStationBrand");
      CustomAsserts.assertParamValues(variables, expectedParams);
   }
}