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

import static de.codengine.tankerkoenig.utils.CustomAsserts.assertParamValues;
import static de.codengine.tankerkoenig.utils.CustomAsserts.assertPostBody;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.codengine.tankerkoenig.exception.ClientExecutorException;
import de.codengine.tankerkoenig.stubs.ToStringObjectMock;
import de.codengine.tankerkoenig.utils.FluentMap;
import de.codengine.tankerkoenig.utils.Tweaks;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class OkHttp3ClientExecutorTest
{
   private MockWebServer server;

   @Before
   public void setUp() throws Exception
   {
      server = new MockWebServer();
      Tweaks.disableMockWebserverLogging();
      server.start();
   }

   @After
   public void tearDown() throws Exception
   {
      server.shutdown();
   }

   @Test
   public void getBuildsCorrectUrlWithFilteringAndCalls() throws InterruptedException
   {
      final HttpUrl url = buildAndEnqueueResponse("/test/get");

      OkHttp3ClientExecutor executor = buildExecutor();

      final Map<String, Object> params = new FluentMap<String, Object>()
            .with("apikey", "foo")
            .with("encoding", "foo bar %$!?")
            .with("nullable", null)
            .with(null, "foo")
            .with("integer", (int) 5)
            .with("boxedInteger", new Integer(5))
            .with("mock", new ToStringObjectMock("foo"))
            .with("", "foo")
            .with("foo", "");

      final String responseBody = executor.get(url.toString(), params);
      assertThat(responseBody).isEqualTo("Ok");

      assertThat(server.getRequestCount())
            .isEqualTo(1);

      final RecordedRequest recordedRequest = server.takeRequest();
      assertThat(recordedRequest.getMethod())
            .isEqualTo("GET");

      final Map<String, String> expectedParams = new FluentMap<String, String>()
            .with("apikey", "foo")
            .with("encoding", "foo bar %$!?")
            .with("integer", "5")
            .with("boxedInteger", "5")
            .with("mock", "foomocked!");

      assertParamValues(recordedRequest, expectedParams);
   }

   private OkHttp3ClientExecutor buildExecutor()
   {
      return new OkHttp3ClientExecutor(new OkHttpClient());
   }

   private HttpUrl buildAndEnqueueResponse(final String url)
   {
      return buildAndEnqueueResponse(url, 200, "Ok");
   }

   private HttpUrl buildAndEnqueueResponse(final String url, int code, String body)
   {
      MockResponse response = new MockResponse()
            .setResponseCode(code)
            .setBody(body);

      server.enqueue(response);

      return server.url(url);
   }

   @Test
   public void postBuildsCorrectUrlWithFilteringAndCalls() throws InterruptedException
   {
      final HttpUrl url = buildAndEnqueueResponse("/test/post");

      OkHttp3ClientExecutor executor = buildExecutor();

      final Map<String, Object> params = new FluentMap<String, Object>()
            .with("apikey", "foo")
            .with("encoding", "foo bar %$!?")
            .with("nullable", null)
            .with(null, "foo")
            .with("integer", (int) 5)
            .with("boxedInteger", new Integer(5))
            .with("mock", new ToStringObjectMock("foo"))
            .with("", "foo")
            .with("foo", "");

      final String responseBody = executor.post(url.toString(), params);
      assertThat(responseBody).isEqualTo("Ok");

      assertThat(server.getRequestCount())
            .isEqualTo(1);

      final RecordedRequest recordedRequest = server.takeRequest();
      assertThat(recordedRequest.getMethod())
            .isEqualTo("POST");

      final HttpUrl requestUrl = recordedRequest.getRequestUrl();
      assertThat(requestUrl.queryParameterNames())
            .hasSize(0);

      final Map<String, String> expectedParams = new FluentMap<String, String>()
            .with("apikey", "foo")
            .with("encoding", "foo bar %$!?")
            .with("integer", "5")
            .with("boxedInteger", "5")
            .with("mock", "foomocked!");

      assertPostBody(recordedRequest, expectedParams);
   }

   @Test
   public void getThrowsExceptionOnHttpError()
   {
      final HttpUrl url = buildAndEnqueueResponse("/test/post", 500, "Not Ok");

      OkHttp3ClientExecutor executor = buildExecutor();

      assertThatThrownBy(() -> executor.get(url.toString(), null))
            .isExactlyInstanceOf(ClientExecutorException.class)
            .hasNoCause()
            .hasMessageContaining("500")
            .hasFieldOrPropertyWithValue("url", url.toString());
   }

   @Test
   public void postThrowsExceptionOnHttpError()
   {
      final HttpUrl url = buildAndEnqueueResponse("/test/post", 500, "Not Ok");

      OkHttp3ClientExecutor executor = buildExecutor();

      final Map<String, Object> params = new FluentMap<String, Object>()
            .with("apikey", "foo");

      assertThatThrownBy(() -> executor.post(url.toString(), params))
            .isExactlyInstanceOf(ClientExecutorException.class)
            .hasNoCause()
            .hasMessageContaining("500")
            .hasFieldOrPropertyWithValue("url", url.toString());
   }

   @Test
   public void getAcceptsEmptyParams() throws InterruptedException
   {
      final HttpUrl url = buildAndEnqueueResponse("/test/get", 200, "Ok");
      final HttpUrl url2 = buildAndEnqueueResponse("/test/get", 200, "Ok");

      OkHttp3ClientExecutor executor = buildExecutor();

      executor.get(url.toString(), new HashMap<>());
      executor.get(url2.toString(), new HashMap<>());

      assertThat(server.getRequestCount())
            .isEqualTo(2);

      final RecordedRequest recordedRequest = server.takeRequest();
      final RecordedRequest recordedRequest2 = server.takeRequest();

      assertThat(recordedRequest.getRequestUrl().toString())
            .endsWith("/test/get");

      assertThat(recordedRequest2.getRequestUrl().toString())
            .endsWith("/test/get");

      assertParamValues(recordedRequest, new HashMap<>());
      assertParamValues(recordedRequest2, new HashMap<>());
   }

   @Test
   public void postAcceptsEmptyParams() throws InterruptedException
   {
      final HttpUrl url = buildAndEnqueueResponse("/test/post", 200, "Ok");
      final HttpUrl url2 = buildAndEnqueueResponse("/test/post", 200, "Ok");

      OkHttp3ClientExecutor executor = buildExecutor();

      executor.post(url.toString(), new HashMap<>());
      executor.post(url2.toString(), new HashMap<>());

      assertThat(server.getRequestCount())
            .isEqualTo(2);

      final RecordedRequest recordedRequest = server.takeRequest();
      final RecordedRequest recordedRequest2 = server.takeRequest();

      assertThat(recordedRequest.getRequestUrl().toString())
            .endsWith("/test/post");

      assertThat(recordedRequest2.getRequestUrl().toString())
            .endsWith("/test/post");

      assertParamValues(recordedRequest, new HashMap<>());
      assertParamValues(recordedRequest2, new HashMap<>());
   }
}