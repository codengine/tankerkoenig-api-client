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

import java.util.Map;

import de.codengine.tankerkoenig.exception.RequesterException;

abstract class BaseRequest<R extends Result> implements Request<R>
{
   private final String apiKey;
   private final String baseUrl;
   private final Requester requester;

   BaseRequest(final String apiKey, final String baseUrl, final Requester requester)
   {
      this.apiKey = apiKey;
      this.baseUrl = baseUrl;
      this.requester = requester;
   }

   /**
    * Executes the request using the underlying {@link Requester},
    * which will return the requested result object
    *
    * @throws RequesterException Checked Exceptions that might be thrown during request execution
    */
   @Override
   public final R execute() throws RequesterException
   {
      return requester.execute(this, getResultClass());
   }

   abstract String getEndpoint();

   abstract Map<String, Object> getRequestParameters();

   abstract void validate();

   abstract Method getMethod();

   abstract Class<R> getResultClass();

   String getApiKey()
   {
      return apiKey;
   }

   String getBaseUrl()
   {
      return baseUrl;
   }
}
