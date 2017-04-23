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
 * Request to obtain a list of stations. The search is based around the coordinates,
 * which are mandatory and must be in the specified boundaries.
 */
public final class StationListRequest extends BaseRequest<StationListResult>
{
   private final static String ENDPOINT = "list.php";
   private Double lat;
   private Double lng;
   private double searchRadius = 5;
   private GasRequestType gasRequestType = GasRequestType.ALL;
   private SortingRequestType sorting = SortingRequestType.DISTANCE;

   public StationListRequest(final String apiKey, final String baseUrl, final Requester requester)
   {
      super(apiKey, baseUrl, requester);
   }

   /**
    * Sets the center of the search
    *
    * @param lat Must be between -90 and 90
    * @param lng Must be between -180 and 180
    */
   public StationListRequest setCoordinates(final double lat, final double lng)
   {
      this.lat = lat;
      this.lng = lng;
      return this;
   }

   /**
    * Sets which gas prices should be requested, which can either be a specific
    * one or ALL
    * <p>
    * Default is: ALL
    */
   public StationListRequest setGasRequestType(final GasRequestType gasRequestType)
   {
      this.gasRequestType = gasRequestType;
      return this;
   }

   /**
    * Sets the search radius in Kilometers.
    * Default is: 5
    *
    * @param radius Must be between 1.0 and 25.0 km
    * @return
    */
   public StationListRequest setSearchRadius(final double radius)
   {
      this.searchRadius = radius;
      return this;
   }

   /**
    * Sets the sorting for the results.
    * Default is: DISTANCE
    */
   public StationListRequest setSorting(final SortingRequestType sorting)
   {
      this.sorting = sorting;
      return this;
   }

   @Override
   Method getMethod()
   {
      return Method.GET;
   }

   @Override
   Class<StationListResult> getResultClass()
   {
      return StationListResult.class;
   }

   @Override
   String getEndpoint()
   {
      return ENDPOINT;
   }

   @Override
   void validate()
   {
      RequestParamValidator.notNull(lat, "Latitude");
      RequestParamValidator.notNull(lng, "Longitude");
      RequestParamValidator.minMax(lat, -90, 90, "Latitude");
      RequestParamValidator.minMax(lng, -180, 180, "Longitude");
      RequestParamValidator.minMax(searchRadius, 1, 25, "Radius");
      RequestParamValidator.notNull(gasRequestType, "Gas Request Type");
      RequestParamValidator.notNull(sorting, "Sorting");
   }

   @Override
   Map<String, Object> getRequestParameters()
   {
      return RequestParamBuilder.create()
            .addValue("lat", lat)
            .addValue("lng", lng)
            .addValue("rad", searchRadius)
            .addValue("type", gasRequestType)
            .addValue("sort", gasRequestType == GasRequestType.ALL ? SortingRequestType.DISTANCE : sorting)
            .build();
   }

   public enum SortingRequestType implements RequestParam
   {
      PRICE, DISTANCE("dist");

      private final String paramKey;

      SortingRequestType(final String paramKey)
      {
         this.paramKey = paramKey;
      }

      SortingRequestType()
      {
         paramKey = name().toLowerCase();
      }

      @Override
      public String toQueryParam()
      {
         return paramKey;
      }
   }

}
