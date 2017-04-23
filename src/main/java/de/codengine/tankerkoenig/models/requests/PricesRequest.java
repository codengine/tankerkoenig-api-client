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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Request for gas prices.
 * Between 1 and 10 station IDs can and must be supplied, or the request will fail.
 */
public final class PricesRequest extends BaseRequest<PricesResult>
{
   private final static String ENDPOINT = "prices.php";
   private final Set<String> stationIds = new LinkedHashSet<>();

   public PricesRequest(final String apiKey, final String baseUrl, final Requester requester)
   {
      super(apiKey, baseUrl, requester);
   }

   /**
    * Adds a station id. Will only be added if not null nor empty.
    * <p>
    * A maximum of 10 IDs is allowed and IDs are getting added uniquely
    */
   public PricesRequest addId(final String stationId)
   {
      if (stationId != null && !stationId.isEmpty())
      {
         stationIds.add(stationId);
      }
      return this;
   }

   /**
    * Adds multiple station ids. Will only be added if not null nor empty.
    * <p>
    * A maximum of 10 IDs is allowed and IDs are getting added uniquely
    */
   public PricesRequest addIds(final String... stationIds)
   {
      return addIds(Arrays.asList(stationIds));
   }

   /**
    * Adds multiple station ids. Will only be added if not null nor empty.
    * <p>
    * A maximum of 10 IDs is allowed and IDs are getting added uniquely
    */
   public PricesRequest addIds(final Collection<String> stationIds)
   {
      final Set<String> filteredIds = stationIds.stream().filter(id -> id != null && !id.isEmpty())
            .collect(Collectors.toCollection(LinkedHashSet::new));

      this.stationIds.addAll(filteredIds);
      return this;
   }

   @Override
   String getEndpoint()
   {
      return ENDPOINT;
   }

   @Override
   Map<String, Object> getRequestParameters()
   {
      return RequestParamBuilder.create()
            .addValue("ids", RequestUtils.join(stationIds, ","))
            .build();
   }

   @Override
   void validate()
   {
      RequestParamValidator.notEmpty(stationIds, "IDs");
      RequestParamValidator.maxCount(stationIds, 10, "IDs");
   }

   @Override
   Method getMethod()
   {
      return Method.GET;
   }

   @Override
   Class<PricesResult> getResultClass()
   {
      return PricesResult.class;
   }
}
