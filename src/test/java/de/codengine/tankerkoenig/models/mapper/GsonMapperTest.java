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

import static de.codengine.tankerkoenig.utils.CustomAsserts.assertSingleton;
import static de.codengine.tankerkoenig.utils.CustomAsserts.testPrivateConstructor;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import de.codengine.tankerkoenig.models.requests.StationDetailResult;
import de.codengine.tankerkoenig.utils.ResourceLoader;

public class GsonMapperTest
{
   @Test
   public void isSingleton() throws IllegalAccessException, InstantiationException, InvocationTargetException
   {
      assertSingleton(GsonMapper.class);
      testPrivateConstructor(GsonMapper.class);
   }

   @Test
   public void getInstance()
   {
      final GsonMapper first = GsonMapper.getInstance();
      final GsonMapper second = GsonMapper.getInstance();
      assertThat(first).isSameAs(second);
   }

   @Test
   public void fromJson() throws IOException
   {
      final String detailContent = ResourceLoader.readString("/detail.json");
      final JsonMapper mapper = GsonMapper.getInstance();
      final StationDetailResult result = mapper.fromJson(detailContent, StationDetailResult.class);
      assertThat(result).isNotNull();
      assertThat(result.getStation().getId()).isEqualTo("51d4b660-a095-1aa0-e100-80009459e03a");
   }
}