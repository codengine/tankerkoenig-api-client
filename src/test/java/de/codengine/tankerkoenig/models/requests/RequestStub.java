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

import java.util.HashMap;
import java.util.Map;

class RequestStub extends BaseRequest<ResultStub>
{
   private Method method = Method.GET;

   RequestStub(final String apiKey, final String baseUrl, final Requester requester)
   {
      super(apiKey, baseUrl, requester);
   }

   @Override
   String getEndpoint()
   {
      return "stub.php";
   }

   @Override
   Map<String, Object> getRequestParameters()
   {
      return new HashMap<>();
   }

   @Override
   void validate()
   {
   }

   RequestStub setMethod(final Method method)
   {
      this.method = method;
      return this;
   }

   @Override
   Method getMethod()
   {
      return method;
   }

   @Override
   Class<ResultStub> getResultClass()
   {
      return ResultStub.class;
   }
}
