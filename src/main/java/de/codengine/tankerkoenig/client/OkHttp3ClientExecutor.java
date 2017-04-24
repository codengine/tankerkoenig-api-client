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

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

import de.codengine.tankerkoenig.exception.ClientExecutorException;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Client Executor which wraps around {@link OkHttpClient}
 */
public final class OkHttp3ClientExecutor implements ClientExecutor
{
   private final OkHttpClient client;

   public OkHttp3ClientExecutor(final OkHttpClient client)
   {
      this.client = client;
   }

   private static Request buildGetRequest(final String url, final Map<String, Object> queryParameters)
   {
      final HttpUrl requestUrl = buildUrl(url, queryParameters);

      return new Request.Builder()
            .url(requestUrl)
            .build();
   }

   private static HttpUrl buildUrl(final String url, final Map<String, Object> queryParameters)
   {
      final HttpUrl.Builder urlbuilder = HttpUrl.parse(url)
            .newBuilder();

      processRequestParameters(queryParameters, entry -> urlbuilder.addQueryParameter(entry.getKey(), entry.getValue().toString()));

      return urlbuilder.build();
   }

   private static boolean nullOrEmpty(final Object value)
   {
      return value == null || (value instanceof String && ((String) value).isEmpty());
   }

   private static void processRequestParameters(final Map<String, Object> requestParameters, final Consumer<Map.Entry<String, Object>> processor)
   {
      if (requestParameters != null && !requestParameters.isEmpty())
      {
         requestParameters.entrySet().stream()
               .filter(entry -> !nullOrEmpty(entry.getKey()) && !nullOrEmpty(entry.getValue()))
               .forEach(processor);
      }
   }

   private static Request buildPostRequest(final String url, final Map<String, Object> formParams)
   {
      final FormBody.Builder requestBodyBuilder = new FormBody.Builder();

      processRequestParameters(formParams, entry -> requestBodyBuilder.add(entry.getKey(), entry.getValue().toString()));

      return new Request.Builder()
            .url(url)
            .post(requestBodyBuilder.build())
            .build();
   }

   /**
    * Builds the final URL using the supplied url, appends query parameters and
    * executes the request
    *
    * @param url               The request URL
    * @param requestParameters The request parameters. Can be empty or null
    * @return The response body
    * @throws ClientExecutorException Thrown if the API call unsuccessful or any other exception of the underlying client occured
    */
   @Override
   public String get(final String url, final Map<String, Object> requestParameters) throws ClientExecutorException
   {
      final Request request = buildGetRequest(url, requestParameters);
      return executeRequest(request);
   }

   /**
    * Builds the form body and executes the request
    *
    * @param url        The request URL
    * @param formParams The form body parameters. Can be empty or null
    * @return The response body
    * @throws ClientExecutorException Thrown if the API call unsuccessful or any other exception of the underlying client occured
    */
   @Override
   public String post(final String url, final Map<String, Object> formParams) throws ClientExecutorException
   {
      final Request request = buildPostRequest(url, formParams);
      return executeRequest(request);
   }

   /**
    * Executes the {@link Request}
    *
    * @param request The request
    * @return The response body
    * @throws ClientExecutorException Thrown if the API call unsuccessful or any other exception of the underlying client occured
    */
   private String executeRequest(final Request request) throws ClientExecutorException
   {
      try (final Response response = client.newCall(request).execute())
      {
         if (!response.isSuccessful())
         {
            throw new ClientExecutorException(request.url().toString(), String.format("The API call was unsuccessful (Code: %s - %s)", response.code(), response.message()));
         }

         return response.body().string();
      }
      catch (IOException e)
      {
         throw new ClientExecutorException(request.url().toString(), "An exception was thrown while request execution", e);
      }
   }
}
