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
import static de.codengine.tankerkoenig.utils.CustomAsserts.testPrivateConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

public class RequestUtilsTest
{
   @Test
   public void utilIsSingleton() throws IllegalAccessException, InstantiationException, InvocationTargetException
   {
      assertSingleton(RequestUtils.class);
      assertThatThrownBy(() -> testPrivateConstructor(RequestUtils.class))
            .isExactlyInstanceOf(InvocationTargetException.class)
            .hasCauseExactlyInstanceOf(UnsupportedOperationException.class);
   }

   @Test
   public void join()
   {
      assertThat(RequestUtils.join(null, ",")).isEqualTo("");
      assertThat(RequestUtils.join(Collections.emptySet(), ",")).isEqualTo("");
      assertThat(RequestUtils.join(Sets.newSet("", null), ",")).isEqualTo("");
      assertThat(RequestUtils.join(Sets.newSet("1", "2"), ",")).isEqualTo("1,2");
      assertThat(RequestUtils.join(Lists.newArrayList("1", "2", "2"), ",")).isEqualTo("1,2,2");
   }
}