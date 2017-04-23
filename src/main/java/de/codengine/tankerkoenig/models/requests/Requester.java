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

import de.codengine.tankerkoenig.client.ClientExecutor;
import de.codengine.tankerkoenig.exception.ClientExecutorException;
import de.codengine.tankerkoenig.exception.RequestParamException;
import de.codengine.tankerkoenig.exception.RequesterException;
import de.codengine.tankerkoenig.models.mapper.JsonMapper;

/**
 * The requester is responsible for the execution of the request
 * and mapping the result to the specified result class
 * <p>
 * Recoverable failures will be wrapped by an {@link RequesterException}
 */
public class Requester
{
   private final JsonMapper jsonMapper;
   private final ClientExecutor clientExecutor;

   public Requester(final ClientExecutor clientExecutor, final JsonMapper jsonMapper)
   {
      this.clientExecutor = clientExecutor;
      this.jsonMapper = jsonMapper;
   }

   <RESULT extends Result> RESULT execute(final BaseRequest<RESULT> request, final Class<RESULT> resultClass) throws RequesterException
   {
      try
      {
         request.validate();
      }
      catch (RequestParamException e)
      {
         throw new RequesterException("An exception was thrown during request validation", e);
      }

      final Map<String, Object> requestParameters = request.getRequestParameters();
      requestParameters.put("apikey", request.getApiKey());
      if (!requestParameters.containsKey("ts"))
      {
         requestParameters.put("ts", System.currentTimeMillis() / 1000L);
      }

      try
      {
         final String result;

         final String requestUrl = request.getBaseUrl() + request.getEndpoint();
         switch (request.getMethod())
         {
            case POST:
               result = clientExecutor.post(requestUrl, requestParameters);
               break;
            case GET:
               result = clientExecutor.get(requestUrl, requestParameters);
               break;
            default:
               throw new UnsupportedOperationException("The request method " + request.getMethod() + " is not supported");
         }

         return jsonMapper.fromJson(result, resultClass);
      }
      catch (ClientExecutorException e)
      {
         throw new RequesterException("An exception was thrown while request execution", e);
      }
      catch (Exception e)
      {
         throw new RequesterException("An unhandled exception was thrown", e);
      }
   }
}
