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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.gson.JsonObject;

public class GsonMapperUtilTest
{
   @Test
   public void utilIsSingleton() throws IllegalAccessException, InstantiationException, InvocationTargetException
   {
      assertSingleton(GsonMapperUtil.class);
      assertThatThrownBy(() -> testPrivateConstructor(GsonMapperUtil.class))
            .isExactlyInstanceOf(InvocationTargetException.class)
            .hasCauseExactlyInstanceOf(UnsupportedOperationException.class);
   }

   @Test
   public void getAsString()
   {
      JsonObject jsonElement = new JsonObject();
      jsonElement.addProperty("foo", "bar");
      jsonElement.addProperty("fooNull", (String) null);
      assertThat(GsonMapperUtil.getAsString(jsonElement, "foo"))
            .isEqualTo("bar");
      assertThat(GsonMapperUtil.getAsString(jsonElement, "fooNull"))
            .isNull();
      assertThat(GsonMapperUtil.getAsString(jsonElement, "notExisting"))
            .isNull();
   }

   @Test
   public void getAsBoolean()
   {
      JsonObject jsonElement = new JsonObject();
      jsonElement.addProperty("foo", true);
      jsonElement.addProperty("fooNull", (Boolean) null);
      assertThat(GsonMapperUtil.getAsBoolean(jsonElement, "foo"))
            .isTrue();
      assertThat(GsonMapperUtil.getAsBoolean(jsonElement, "fooNull"))
            .isFalse();
      assertThat(GsonMapperUtil.getAsBoolean(jsonElement, "notExisting"))
            .isFalse();
      assertThat(GsonMapperUtil.getAsBoolean(jsonElement, "notExisting", true))
            .isTrue();
   }

   @Test
   public void getAsFloat()
   {
      JsonObject jsonElement = new JsonObject();
      jsonElement.addProperty("foo", 9.99f);
      jsonElement.addProperty("fooNull", (Float) null);
      assertThat(GsonMapperUtil.getAsFloat(jsonElement, "foo", 0f))
            .isEqualTo(9.99f);
      assertThat(GsonMapperUtil.getAsFloat(jsonElement, "fooNull", 1.23f))
            .isEqualTo(1.23f);
      assertThat(GsonMapperUtil.getAsFloat(jsonElement, "notExisting", 1.23f))
            .isEqualTo(1.23f);
   }

   @Test
   public void nodeMemberCollectionWithNull()
   {
      final List<Object> collection = GsonMapperUtil.nodeMemberCollection(null, ArrayList::new);
      assertThat(collection)
            .isNotNull()
            .isInstanceOf(ArrayList.class)
            .isEmpty();
   }

   @Test
   public void nodeMemberCollectionWithEmptyArray()
   {
      String[] array = {};

      final List<String> collection = GsonMapperUtil.nodeMemberCollection(array, ArrayList::new);
      assertThat(collection)
            .isNotNull()
            .isInstanceOf(ArrayList.class)
            .isEmpty();
   }

   @Test
   public void nodeMemberCollectionWithArray()
   {
      String[] array = { "foo", null };

      final List<String> collection = GsonMapperUtil.nodeMemberCollection(array, ArrayList::new);
      assertThat(collection)
            .isNotNull()
            .isInstanceOf(ArrayList.class)
            .isNotEmpty()
            .containsExactly(array);
   }
}