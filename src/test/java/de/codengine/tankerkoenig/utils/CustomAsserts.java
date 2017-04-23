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

package de.codengine.tankerkoenig.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.time.DayOfWeek;
import java.util.Map;
import java.util.stream.Collectors;

import de.codengine.tankerkoenig.models.mapper.OpeningTime;
import de.codengine.tankerkoenig.models.requests.Result;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.RecordedRequest;

public class CustomAsserts
{
   public static void assertParamValues(RecordedRequest recordedRequest, Map<String, ?> expectedParams)
   {
      final HttpUrl requestUrl = recordedRequest.getRequestUrl();
      assertThat(requestUrl.queryParameterNames())
            .hasSize(expectedParams.size())
            .containsExactlyElementsOf(expectedParams.keySet());

      expectedParams.forEach(
            (key, value) -> assertThat(requestUrl.queryParameter(key)).isEqualTo(value)
      );
   }

   public static void assertParamValues(Map<String, Object> actualParams, Map<String, Object> expectedParams)
   {
      assertThat(actualParams)
            .hasSize(expectedParams.size())
            .containsAllEntriesOf(expectedParams);
   }

   private static String encodeParam(String param)
   {
      try
      {
         return URLEncoder.encode(param, "UTF-8")
               .replace("+", "%20");
      }
      catch (UnsupportedEncodingException e)
      {
         throw new RuntimeException("Something went wrong while encoding the param" + param, e);
      }
   }

   public static void assertPostBody(final RecordedRequest recordedRequest, final Map<String, String> expectedParams)
   {
      String expectedBody = expectedParams.entrySet().stream()
            .map(entry -> encodeParam(entry.getKey()) + "=" + encodeParam(entry.getValue()))
            .collect(Collectors.joining("&"));

      final String body = recordedRequest.getBody()
            .readUtf8();

      assertThat(body)
            .isEqualTo(expectedBody);
   }

   public static void assertSingleton(Class<?> clazz)
   {
      assertThat(clazz).isFinal();

      final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
      for (Constructor<?> constructor : constructors)
      {
         assertThat(Modifier.isPrivate(constructor.getModifiers()))
               .isTrue();
      }
   }

   public static void testPrivateConstructor(Class<?> clazz) throws IllegalAccessException, InvocationTargetException, InstantiationException
   {
      final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
      final Constructor<?> constructor = constructors[0];

      assertThat(Modifier.isPrivate(constructor.getModifiers()))
            .isTrue();

      constructor.setAccessible(true);
      constructor.newInstance();
   }

   public static void assertDefaultSuccessResponseAsserts(Result result)
   {
      assertThat(result.getMessage()).isNotPresent();
      assertThat(result.getLicense()).isPresent()
            .hasValue("CC BY 4.0 -  https://creativecommons.tankerkoenig.de");
      assertThat(result.getData()).isPresent()
            .hasValue("MTS-K");
      assertThat(result.isOk()).isTrue();
   }

   public static void assertOpeningTime(final OpeningTime openingTime, final String start, final String end, final DayOfWeek... weekDays)
   {
      assertThat(openingTime.getStart()).isEqualTo(start);
      assertThat(openingTime.getEnd()).isEqualTo(end);
      assertThat(openingTime.getDays()).containsExactly(weekDays);
   }

   public static void assertDefaultFailedResponseAsserts(Result result)
   {
      assertThat(result.getStatus()).isPresent().hasValue(Result.ResponseStatus.ERROR);
      assertThat(result.getMessage()).isPresent().hasValue("Error Message");
      assertThat(result.getLicense()).isNotPresent();
      assertThat(result.getData()).isNotPresent();
      assertThat(result.isOk()).isFalse();
   }
}
