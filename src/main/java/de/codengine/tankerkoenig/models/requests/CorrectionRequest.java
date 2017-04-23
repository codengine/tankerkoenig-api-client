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
 * Request for station data corrections.
 * <p>
 * Most {@link Type}s will require correction data except for WRONG_STATUS_OPEN AND WRONG_STATUS_CLOSED.
 * The type of the correction values depend on the {@link Type}
 * <p>
 * The station ID is always required
 */
public final class CorrectionRequest extends BaseRequest<CorrectionResult>
{
   private final static String ENDPOINT = "complaint.php";
   private final Type type;
   private String stationId;
   private String correctionValue;

   public CorrectionRequest(final String apiKey, final String baseUrl, final Requester requester, final Type type)
   {
      super(apiKey, baseUrl, requester);
      this.type = type;
   }

   /**
    * Sets the station ID for the correction request.
    * This information is required
    *
    * @param stationId The unique station ID, which is provided with {@link StationListResult} or {@link StationDetailResult}
    */
   public CorrectionRequest setStationId(final String stationId)
   {
      this.stationId = stationId;
      return this;
   }

   /**
    * Sets the corrected value for the correction request.
    * This information is required
    * <p>
    * The type of the data is determined by the correction type:
    * <p>
    * - Float (#.##): WRONG_PRICE_E5, WRONG_PRICE_E10, WRONG_PRICE_DIESEL
    * - Post Code (e.g. 12345): WRONG_PETROL_STATION_POSTCODE
    * - Location Data (e.g. 53.0, 13.0): WRONG_PETROL_STATION_LOCATION
    * - Any String: Everything else
    *
    * @param correctionValue The correction value
    */
   public CorrectionRequest setCorrectionValue(final String correctionValue)
   {
      this.correctionValue = correctionValue;
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
      final RequestParamBuilder builder = RequestParamBuilder.create()
            .addValue("id", stationId)
            .addValue("type", type);

      if (type.requiresCorrectionValue())
      {
         builder.addValue("correction", correctionValue);
      }

      return builder.build();
   }

   @Override
   void validate()
   {
      RequestParamValidator.notNull(type, "Type");
      RequestParamValidator.notEmpty(stationId, "Station ID");

      if (type.requiresCorrectionValue())
      {
         RequestParamValidator.notEmpty(correctionValue, "Correction Value");
      }

      switch (type)
      {
         case WRONG_PRICE_E5:
         case WRONG_PRICE_E10:
         case WRONG_PRICE_DIESEL:
            RequestParamValidator.isFloat(correctionValue, "Correction Value");
            break;
         case WRONG_PETROL_STATION_POSTCODE:
            RequestParamValidator.isPostCode(correctionValue);
            break;
         case WRONG_PETROL_STATION_LOCATION:
            RequestParamValidator.isLocationData(correctionValue);
            break;
      }
   }

   @Override
   Method getMethod()
   {
      return Method.POST;
   }

   @Override
   Class<CorrectionResult> getResultClass()
   {
      return CorrectionResult.class;
   }

   /**
    * The type of the correction request
    */
   public enum Type implements RequestParam
   {
      /**
       * Correction of the stations name
       * Requires a correction value of String
       */
      WRONG_PETROL_STATION_NAME("wrongPetrolStationName"),
      /**
       * Correction of the current opening status of the gas station if open
       * Requires no correction value
       */
      WRONG_STATUS_OPEN("wrongStatusOpen"),
      /**
       * Correction of the current opening status of the gas station if closed
       * Requires no correction value
       */
      WRONG_STATUS_CLOSED("wrongStatusClosed"),
      /**
       * Correction of the price for E5 gasoline
       * Requires a correction value of Float (#.##)
       */
      WRONG_PRICE_E5("wrongPriceE5"),
      /**
       * Correction of the price for E10 gasoline
       * Requires a correction value of Float (#.##)
       */
      WRONG_PRICE_E10("wrongPriceE10"),
      /**
       * Correction of the price for Diesel
       * Requires a correction value of Float (#.##)
       */
      WRONG_PRICE_DIESEL("wrongPriceDiesel"),
      /**
       * Correction for the stations brand
       * Requires a correction value of String
       */
      WRONG_PETROL_STATION_BRAND("wrongPetrolStationBrand"),
      /**
       * Correction for the stations street
       * Requires a correction value of String
       */
      WRONG_PETROL_STATION_STREET("wrongPetrolStationStreet"),
      /**
       * Correction for the stations house number
       * Requires a correction value of String
       */
      WRONG_PETROL_STATION_HOUSE_NUMBER("wrongPetrolStationHouseNumber"),
      /**
       * Correction for the stations post code
       * Requires a correction value of Numeric (5 digits)
       */
      WRONG_PETROL_STATION_POSTCODE("wrongPetrolStationPostcode"),
      /**
       * Correction of the stations city
       * Requires a correction value of String
       */
      WRONG_PETROL_STATION_PLACE("wrongPetrolStationPlace"),
      /**
       * Correction of the stations location data
       * Requires a correction value of String
       */
      WRONG_PETROL_STATION_LOCATION("wrongPetrolStationLocation");

      private final String queryParam;

      Type(final String queryParam)
      {
         this.queryParam = queryParam;
      }

      @Override
      public String toQueryParam()
      {
         return queryParam;
      }

      /**
       * Determines if the correction type requires a correction value
       */
      public boolean requiresCorrectionValue()
      {
         switch (this)
         {
            case WRONG_STATUS_OPEN:
            case WRONG_STATUS_CLOSED:
               return false;
            default:
               return true;
         }
      }
   }
}
