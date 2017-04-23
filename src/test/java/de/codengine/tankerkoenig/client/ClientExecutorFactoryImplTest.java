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

package de.codengine.tankerkoenig.client;

import static de.codengine.tankerkoenig.utils.CustomAsserts.assertSingleton;
import static de.codengine.tankerkoenig.utils.CustomAsserts.testPrivateConstructor;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.junit.Test;

import de.codengine.tankerkoenig.utils.Tweaks;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class ClientExecutorFactoryImplTest
{
   @Test
   public void factoryIsSingleton() throws IllegalAccessException, InstantiationException, InvocationTargetException
   {
      assertSingleton(ClientExecutorFactoryImpl.class);
      testPrivateConstructor(ClientExecutorFactoryImpl.class);
   }

   @Test
   public void getInstance()
   {
      final ClientExecutorFactoryImpl first = ClientExecutorFactoryImpl.getInstance();
      final ClientExecutorFactoryImpl second = ClientExecutorFactoryImpl.getInstance();
      assertThat(first).isSameAs(second);
   }

   @Test
   public void buildDefaultClientExecutor() throws IOException
   {
      final ClientExecutorFactoryImpl factory = ClientExecutorFactoryImpl.getInstance();
      final ClientExecutor firstExecutor = factory.buildDefaultClientExecutor();
      final ClientExecutor secondExecutor = factory.buildDefaultClientExecutor();
      assertThat(firstExecutor).isNotNull()
            .isInstanceOf(OkHttp3ClientExecutor.class)
            .isNotSameAs(secondExecutor);
      assertThat(secondExecutor).isNotNull()
            .isInstanceOf(OkHttp3ClientExecutor.class);

      verifyExecutor(firstExecutor);
   }

   private void verifyExecutor(final ClientExecutor firstExecutor) throws IOException
   {
      try (MockWebServer mockWebServer = new MockWebServer())
      {
         Tweaks.disableMockWebserverLogging();
         mockWebServer.enqueue(buildMockResponse("Test Get"));
         mockWebServer.enqueue(buildMockResponse("Test Post"));

         final String firstResponse = firstExecutor.get(mockWebServer.url("/getUrl").toString(), new HashMap<>());
         final String secondResponse = firstExecutor.post(mockWebServer.url("/postUrl").toString(), new HashMap<>());

         assertThat(firstResponse).isEqualTo("Test Get");
         assertThat(secondResponse).isEqualTo("Test Post");
      }
   }

   private MockResponse buildMockResponse(final String test)
   {
      return new MockResponse()
            .setBody(test)
            .setResponseCode(200);
   }
}