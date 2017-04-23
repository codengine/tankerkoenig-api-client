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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import de.codengine.tankerkoenig.exception.RequestParamException;
import de.codengine.tankerkoenig.stubs.ToStringObjectMock;

public class RequestParamValidatorTest
{
   @Test
   public void utilIsSingleton() throws IllegalAccessException, InstantiationException, InvocationTargetException
   {
      assertSingleton(RequestParamValidator.class);
      assertThatThrownBy(() -> testPrivateConstructor(RequestParamValidator.class))
            .isExactlyInstanceOf(InvocationTargetException.class)
            .hasCauseExactlyInstanceOf(UnsupportedOperationException.class);
   }

   @Test
   public void minMax()
   {
      RequestParamValidator.minMax(7, 5, 10, "test");
      RequestParamValidator.minMax(5, 5, 10, "test");
      RequestParamValidator.minMax(10, 5, 10, "test");
      RequestParamValidator.minMax(0, -5, 10, "test");
      RequestParamValidator.minMax(-5, -5, 10, "test");
      RequestParamValidator.minMax(-1, -5, -1, "test");
      RequestParamValidator.minMax(5, 5, 5, "test");
      RequestParamValidator.minMax(0, 0, 0, "test");
      RequestParamValidator.minMax(-5, -5, -5, "test");
      assertThatThrownBy(() -> RequestParamValidator.minMax(-6, -5, -1, "test"))
            .isExactlyInstanceOf(RequestParamException.class);
      assertThatThrownBy(() -> RequestParamValidator.minMax(0, -5, -1, "test"))
            .isExactlyInstanceOf(RequestParamException.class);
      assertThatThrownBy(() -> RequestParamValidator.minMax(0, 1, 5, "test"))
            .isExactlyInstanceOf(RequestParamException.class);
      assertThatThrownBy(() -> RequestParamValidator.minMax(6, 1, 5, "test"))
            .isExactlyInstanceOf(RequestParamException.class);
   }

   @Test
   public void minMaxDouble()
   {
      RequestParamValidator.minMax(7.0, 5, 10, "test");
      RequestParamValidator.minMax(5.0, 5, 10, "test");
      RequestParamValidator.minMax(10.0, 5, 10, "test");
      RequestParamValidator.minMax(0.0, -5, 10, "test");
      RequestParamValidator.minMax(-5.0, -5, 10, "test");
      RequestParamValidator.minMax(-1.0, -5, -1, "test");
      RequestParamValidator.minMax(5.0, 5, 5, "test");
      RequestParamValidator.minMax(0.0, 0, 0, "test");
      RequestParamValidator.minMax(-5.0, -5, -5, "test");
      assertThatThrownBy(() -> RequestParamValidator.minMax(-5.000000000000001, -5, -1, "test"))
            .isExactlyInstanceOf(RequestParamException.class);
      assertThatThrownBy(() -> RequestParamValidator.minMax(-0.999999999999999, -5, -1, "test"))
            .isExactlyInstanceOf(RequestParamException.class);
      assertThatThrownBy(() -> RequestParamValidator.minMax(0.9999999999999999, 1, 5, "test"))
            .isExactlyInstanceOf(RequestParamException.class);
      assertThatThrownBy(() -> RequestParamValidator.minMax(6.9999999999999999, 1, 5, "test"))
            .isExactlyInstanceOf(RequestParamException.class);
   }

   @Test
   public void notNull()
   {
      final ToStringObjectMock mock = new ToStringObjectMock("foo");
      RequestParamValidator.notNull(mock, "test");
      RequestParamValidator.notNull("", "foo");
      assertThatThrownBy(() -> RequestParamValidator.notNull(null, "foo"))
            .isExactlyInstanceOf(RequestParamException.class);
   }

   @Test
   public void notEmpty()
   {
      RequestParamValidator.notEmpty(" ", "test");
      RequestParamValidator.notEmpty("foo", "test");
      assertThatThrownBy(() -> RequestParamValidator.notEmpty((String) null, "test"))
            .isExactlyInstanceOf(RequestParamException.class);
      assertThatThrownBy(() -> RequestParamValidator.notEmpty("", "test"))
            .isExactlyInstanceOf(RequestParamException.class);
   }

   @Test
   public void notEmptyWithCollection()
   {
      RequestParamValidator.notEmpty(Lists.newArrayList(""), "test");
      RequestParamValidator.notEmpty(Lists.newArrayList((String) null), "test");
      assertThatThrownBy(() -> RequestParamValidator.notEmpty((List<String>) null, "test"))
            .isExactlyInstanceOf(RequestParamException.class);
      assertThatThrownBy(() -> RequestParamValidator.notEmpty(Lists.emptyList(), "test"))
            .isExactlyInstanceOf(RequestParamException.class);
   }

   @Test
   public void maxCount()
   {
      assertThatThrownBy(() -> RequestParamValidator.maxCount(null, -1, "test"))
            .isInstanceOf(RuntimeException.class);
      RequestParamValidator.maxCount(Lists.emptyList(), 5, "test");
      RequestParamValidator.maxCount(Sets.newSet("1"), 5, "test");
      RequestParamValidator.maxCount(Sets.newSet("1", "2", "3", "4", "5"), 5, "test");
      RequestParamValidator.maxCount(Lists.newArrayList("1", "2", "3", "4", "5"), 5, "test");
      RequestParamValidator.maxCount(Lists.emptyList(), 0, "test");
      assertThatThrownBy(() -> RequestParamValidator.maxCount(Sets.newSet("1"), -1, "test"))
            .isInstanceOf(RuntimeException.class);
      assertThatThrownBy(() -> RequestParamValidator.maxCount(Sets.newSet("1", "2", "3", "4", "5", "6"), 5, "test"))
            .isInstanceOf(RuntimeException.class);
   }

   @Test
   public void isFloat()
   {
      final List<String> validValues = Lists.newArrayList("0.12", "-0.12");
      final List<String> invalidValues = Lists.newArrayList("foo", "0", "1", "+0.12", " 1.23 ", null);

      for (final String value : validValues)
      {
         RequestParamValidator.isFloat(value, "test");
      }

      for (final String value : invalidValues)
      {
         assertThatThrownBy(() -> RequestParamValidator.isFloat(value, "test"))
               .isExactlyInstanceOf(RequestParamException.class);
      }
   }

   @Test
   public void isPostCode()
   {
      final List<String> validValues = Lists.newArrayList("12345");
      final List<String> invalidValues = Lists.newArrayList("foo", "0", "1", "+0.12", " 1.23 ", null);

      for (final String value : validValues)
      {
         RequestParamValidator.isPostCode(value);
      }

      for (final String value : invalidValues)
      {
         assertThatThrownBy(() -> RequestParamValidator.isPostCode(value))
               .isExactlyInstanceOf(RequestParamException.class);
      }
   }

   @Test
   public void isLocationData()
   {
      final List<String> validValues = Lists.newArrayList("55.2,33.2", "55.2, 33.2", "-55.2, 33.2", "55.2, -33.2", "-55.2, -33.2");
      final List<String> invalidValues = Lists
            .newArrayList("foo", "55,3,55,4", "55", "55,33", "55 ,33", " 55.2, 33.3", "55.2, 33.3 ", "55.3 33.4", "55 33", "55.2a,33.2", "55.2,33.2a", "a55.2,33.2", "55.2,a33.2", null);

      for (final String value : validValues)
      {
         RequestParamValidator.isLocationData(value);
      }

      for (final String value : invalidValues)
      {
         assertThatThrownBy(() -> RequestParamValidator.isLocationData(value))
               .isExactlyInstanceOf(RequestParamException.class);
      }
   }
}