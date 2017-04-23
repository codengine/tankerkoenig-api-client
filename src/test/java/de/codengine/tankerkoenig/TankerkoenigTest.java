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

package de.codengine.tankerkoenig;

import static de.codengine.tankerkoenig.utils.CustomAsserts.assertSingleton;
import static de.codengine.tankerkoenig.utils.CustomAsserts.testPrivateConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Function;

import org.junit.Test;

import de.codengine.tankerkoenig.client.ClientExecutor;
import de.codengine.tankerkoenig.client.ClientExecutorFactory;
import de.codengine.tankerkoenig.client.OkHttp3ClientExecutor;
import de.codengine.tankerkoenig.exception.ClientExecutorException;
import de.codengine.tankerkoenig.exception.RequesterException;
import de.codengine.tankerkoenig.models.mapper.GasPrices;
import de.codengine.tankerkoenig.models.requests.CorrectionRequest;
import de.codengine.tankerkoenig.models.requests.CorrectionResult;
import de.codengine.tankerkoenig.models.requests.PricesRequest;
import de.codengine.tankerkoenig.models.requests.PricesResult;
import de.codengine.tankerkoenig.models.requests.Request;
import de.codengine.tankerkoenig.models.requests.Result;
import de.codengine.tankerkoenig.models.requests.StationDetailRequest;
import de.codengine.tankerkoenig.models.requests.StationDetailResult;
import de.codengine.tankerkoenig.models.requests.StationListRequest;
import de.codengine.tankerkoenig.models.requests.StationListResult;
import de.codengine.tankerkoenig.stubs.ExceptionThrowingInterceptor;
import de.codengine.tankerkoenig.utils.ResourceLoader;
import de.codengine.tankerkoenig.utils.Tweaks;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class TankerkoenigTest
{
   @Test
   public void isSingleton() throws IllegalAccessException, InstantiationException, InvocationTargetException
   {
      assertSingleton(Tankerkoenig.class);
      assertThatThrownBy(() -> testPrivateConstructor(Tankerkoenig.class))
            .isExactlyInstanceOf(InvocationTargetException.class)
            .hasCauseExactlyInstanceOf(UnsupportedOperationException.class);
   }

   @Test
   public void buildsDefaultClientExecutor() throws IOException
   {
      final String detailContent = ResourceLoader.readString("/detail.json");

      final ClientExecutorFactory clientExecutorFactory = mock(ClientExecutorFactory.class);
      final ClientExecutor clientExecutor = mock(ClientExecutor.class);

      when(clientExecutor.get(anyString(), any()))
            .thenReturn(detailContent);

      when(clientExecutorFactory.buildDefaultClientExecutor())
            .thenReturn(clientExecutor);

      final Tankerkoenig.ApiBuilder apiBuilder = new Tankerkoenig.ApiBuilder(clientExecutorFactory);
      final Tankerkoenig.Api api = apiBuilder.withDefaultClientExecutor()
            .withDemoApiKey()
            .build();

      final StationDetailRequest request = api.detail("5");
      final StationDetailResult result = request.execute();

      assertThat(result).isExactlyInstanceOf(StationDetailResult.class);
      assertThat(result.getStation().getId()).isEqualTo("51d4b660-a095-1aa0-e100-80009459e03a");

      verify(clientExecutorFactory, times(1)).buildDefaultClientExecutor();
      verify(clientExecutor, times(1)).get(argThat(argument -> argument.equals("https://creativecommons.tankerkoenig.de/json/detail.php")), argThat(
            params -> Objects.equals(params.get("id"), "5") && Objects.equals(params.get("apikey"), "00000000-0000-0000-0000-000000000002")
      ));
      verifyNoMoreInteractions(clientExecutorFactory, clientExecutor);
   }

   @Test
   public void buildsDefaultClientExecutorWithRealFactory() throws IOException, InterruptedException
   {
      final String detailContent = ResourceLoader.readString("/detail.json");

      try (MockWebServer webServer = new MockWebServer())
      {
         Tweaks.disableMockWebserverLogging();

         MockResponse response = new MockResponse().setBody(detailContent)
               .setResponseCode(200);
         webServer.enqueue(response);
         final HttpUrl url = webServer.url("/detail.json");
         final HttpUrl baseUrl = new HttpUrl.Builder().scheme(url.scheme())
               .host(url.host())
               .port(url.port())
               .build();

         final Tankerkoenig.ApiBuilder apiBuilder = new Tankerkoenig.ApiBuilder(baseUrl.toString());
         final Tankerkoenig.Api api = apiBuilder.withDefaultClientExecutor()
               .withDemoApiKey()
               .build();

         final StationDetailRequest request = api.detail("5");
         final StationDetailResult result = request.execute();

         assertThat(result).isNotNull();
         assertThat(result.getStatus()).isPresent().hasValue(Result.ResponseStatus.OK);
         assertThat(result.getStation().getId()).isEqualTo("51d4b660-a095-1aa0-e100-80009459e03a");

         RecordedRequest recordedRequest = webServer.takeRequest();
         assertThat(recordedRequest.getRequestUrl().queryParameter("id"))
               .isEqualTo("5");
      }
   }

   @Test
   public void buildsWithCustomExecutorThrowingException() throws IOException, InterruptedException
   {
      try (MockWebServer webServer = new MockWebServer())
      {
         Tweaks.disableMockWebserverLogging();

         MockResponse response = new MockResponse().setBody("")
               .setResponseCode(200);
         webServer.enqueue(response);
         final HttpUrl url = webServer.url("/detail.json");
         final HttpUrl baseUrl = new HttpUrl.Builder().scheme(url.scheme())
               .host(url.host())
               .port(url.port())
               .build();

         final Interceptor interceptor = spy(ExceptionThrowingInterceptor.class);

         OkHttpClient client = new OkHttpClient.Builder()
               .addInterceptor(interceptor)
               .build();

         final OkHttp3ClientExecutor executor = new OkHttp3ClientExecutor(client);

         final Tankerkoenig.ApiBuilder apiBuilder = new Tankerkoenig.ApiBuilder(baseUrl.toString());
         final Tankerkoenig.Api api = apiBuilder.withClientExecutor(executor)
               .withDemoApiKey()
               .build();

         final StationDetailRequest request = api.detail("5");
         assertThatThrownBy(request::execute)
               .isExactlyInstanceOf(RequesterException.class)
               .hasCauseExactlyInstanceOf(ClientExecutorException.class)
               .hasRootCauseExactlyInstanceOf(IOException.class);

         verify(interceptor, atLeastOnce()).intercept(any());
      }
   }

   @Test
   public void buildsDefaultClientExecutorIfNotExplicitlySet() throws IOException
   {
      final String detailContent = ResourceLoader.readString("/detail.json");

      final ClientExecutorFactory clientExecutorFactory = mock(ClientExecutorFactory.class);
      final ClientExecutor clientExecutor = mock(ClientExecutor.class);

      when(clientExecutor.get(anyString(), any()))
            .thenReturn(detailContent);

      when(clientExecutorFactory.buildDefaultClientExecutor())
            .thenReturn(clientExecutor);

      final Tankerkoenig.ApiBuilder apiBuilder = new Tankerkoenig.ApiBuilder(clientExecutorFactory);
      final Tankerkoenig.Api api = apiBuilder.withDemoApiKey()
            .build();

      final StationDetailRequest request = api.detail("5");
      final StationDetailResult result = request.execute();

      assertThat(result).isExactlyInstanceOf(StationDetailResult.class);
      assertThat(result.getStation().getId()).isEqualTo("51d4b660-a095-1aa0-e100-80009459e03a");

      verify(clientExecutorFactory, times(1)).buildDefaultClientExecutor();
      verify(clientExecutor, times(1)).get(argThat(argument -> argument.equals("https://creativecommons.tankerkoenig.de/json/detail.php")), argThat(
            params -> Objects.equals(params.get("id"), "5") && Objects.equals(params.get("apikey"), "00000000-0000-0000-0000-000000000002")
      ));
      verifyNoMoreInteractions(clientExecutorFactory, clientExecutor);
   }

   @Test
   public void usesCustomExecutor() throws IOException
   {
      final String detailContent = ResourceLoader.readString("/detail.json");

      ClientExecutor clientExecutor = mock(ClientExecutor.class);
      when(clientExecutor.get(any(), any())).thenReturn(detailContent);

      final Tankerkoenig.ApiBuilder apiBuilder = new Tankerkoenig.ApiBuilder();

      final Tankerkoenig.Api api = apiBuilder.withClientExecutor(clientExecutor)
            .withApiKey("123")
            .build();

      final StationDetailRequest request = api.detail("5");
      final StationDetailResult result = request.execute();

      assertThat(result).isExactlyInstanceOf(StationDetailResult.class);
      assertThat(result.getStation().getId()).isEqualTo("51d4b660-a095-1aa0-e100-80009459e03a");

      verify(clientExecutor, times(1)).get(argThat(argument -> argument.equals("https://creativecommons.tankerkoenig.de/json/detail.php")), argThat(
            params -> Objects.equals(params.get("id"), "5") && Objects.equals(params.get("apikey"), "123")
      ));
      verifyNoMoreInteractions(clientExecutor);
   }

   @Test
   public void emptyApiKeyThrowsException() throws IOException
   {
      ClientExecutor clientExecutor = mock(ClientExecutor.class);

      final Tankerkoenig.ApiBuilder apiBuilder = new Tankerkoenig.ApiBuilder()
            .withClientExecutor(clientExecutor);

      assertThatThrownBy(apiBuilder::build)
            .isExactlyInstanceOf(IllegalStateException.class)
            .hasMessage("The API key has to be neither empty nor null");

      apiBuilder.withApiKey("");

      assertThatThrownBy(apiBuilder::build)
            .isExactlyInstanceOf(IllegalStateException.class)
            .hasMessage("The API key has to be neither empty nor null");
   }

   @Test
   public void verifyDetailCall() throws IOException
   {
      final StationDetailResult result = verifyCall("detail.php", "detail.json", StationDetailResult.class, StationDetailRequest.class, api -> api.detail("5"), Request.Method.GET);
      assertThat(result.getStation().getId()).isEqualTo("51d4b660-a095-1aa0-e100-80009459e03a");
   }

   @Test
   public void verifyComplaintCall() throws IOException
   {
      final CorrectionResult result = verifyCall("complaint.php", "correction.json", CorrectionResult.class, CorrectionRequest.class,
            api -> api.correction("5", CorrectionRequest.Type.WRONG_STATUS_CLOSED), Request.Method.POST);
      assertThat(result.isOk()).isTrue();
   }

   @Test
   public void verifyPricesCall() throws IOException
   {
      final PricesResult result = verifyCall("prices.php", "prices.json", PricesResult.class, PricesRequest.class,
            api -> api.prices().addId("1723edea-8e01-4de3-8c5e-ca227a49e2c3"), Request.Method.GET);
      assertThat(result.getGasPrices().get("1723edea-8e01-4de3-8c5e-ca227a49e2c3").getStatus()).isEqualTo(GasPrices.Status.OPEN);
   }

   @Test
   public void verifyListCall() throws IOException
   {
      final StationListResult result = verifyCall("list.php", "list_all_prices.json", StationListResult.class, StationListRequest.class,
            api -> api.list(53, 13), Request.Method.GET);
      assertThat(result.getStations().get(0).getId()).isEqualTo("51d4b660-a095-1aa0-e100-80009459e03a");
   }

   private <R extends Result, T extends Request<R>> R verifyCall(String endpoint, String jsonResultFile, Class<R> resultClass, Class<T> requestClass, Function<Tankerkoenig.Api, T> caller,
         Request.Method method)
         throws IOException
   {
      final String responseContent = ResourceLoader.readString("/" + jsonResultFile);

      ClientExecutor clientExecutor = mock(ClientExecutor.class);
      if (method == Request.Method.GET)
      {
         when(clientExecutor.get(any(), any())).thenReturn(responseContent);
      }
      else
      {
         when(clientExecutor.post(any(), any())).thenReturn(responseContent);
      }

      final Tankerkoenig.ApiBuilder apiBuilder = new Tankerkoenig.ApiBuilder();

      final Tankerkoenig.Api api = apiBuilder.withClientExecutor(clientExecutor)
            .withApiKey("123")
            .build();

      final T request = caller.apply(api);
      final R result = request.execute();

      assertThat(request).isExactlyInstanceOf(requestClass);
      assertThat(result).isExactlyInstanceOf(resultClass);

      if (method == Request.Method.GET)
      {
         verify(clientExecutor, times(1)).get(argThat(argument -> argument.equals("https://creativecommons.tankerkoenig.de/json/" + endpoint)), argThat(
               params -> Objects.equals(params.get("apikey"), "123")
         ));
      }
      else
      {
         verify(clientExecutor, times(1)).post(argThat(argument -> argument.equals("https://creativecommons.tankerkoenig.de/json/" + endpoint)), argThat(
               params -> Objects.equals(params.get("apikey"), "123")
         ));
      }

      verifyNoMoreInteractions(clientExecutor);

      return result;
   }
}