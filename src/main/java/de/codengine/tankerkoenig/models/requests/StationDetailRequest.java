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

/**
 * Request to obtain detail information for a station, specified by the stations unique ID
 * which is obtainable using the {@link StationListRequest}
 */
public final class StationDetailRequest extends BaseRequest<StationDetailResult>
{
   private final static String ENDPOINT = "detail.php";
   private final String stationId;

   public StationDetailRequest(final String apiKey, final String baseUrl, final Requester requester, final String stationId)
   {
      super(apiKey, baseUrl, requester);
      this.stationId = stationId;
   }

   @Override
   String getEndpoint()
   {
      return ENDPOINT;
   }

   @Override
   void validate()
   {
      RequestParamValidator.notEmpty(stationId, "ID");
   }

   @Override
   Method getMethod()
   {
      return Method.GET;
   }

   @Override
   Class<StationDetailResult> getResultClass()
   {
      return StationDetailResult.class;
   }

   @Override
   Map<String, Object> getRequestParameters()
   {
      return RequestParamBuilder.create()
            .addValue("id", stationId)
            .build();
   }
}
