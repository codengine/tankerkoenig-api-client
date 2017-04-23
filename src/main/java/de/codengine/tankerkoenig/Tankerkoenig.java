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

import de.codengine.tankerkoenig.client.ClientExecutor;
import de.codengine.tankerkoenig.client.ClientExecutorFactory;
import de.codengine.tankerkoenig.client.ClientExecutorFactoryImpl;
import de.codengine.tankerkoenig.models.mapper.GsonMapper;
import de.codengine.tankerkoenig.models.requests.CorrectionRequest;
import de.codengine.tankerkoenig.models.requests.PricesRequest;
import de.codengine.tankerkoenig.models.requests.Requester;
import de.codengine.tankerkoenig.models.requests.StationDetailRequest;
import de.codengine.tankerkoenig.models.requests.StationListRequest;

/**
 * Entry point for creation of the Tankerkoenig API instance.
 * <p>
 * For technical information: @see https://creativecommons.tankerkoenig.de/#techInfo
 * For usage details: @see https://creativecommons.tankerkoenig.de/#usage
 * For information regarding api key registration: @see https://creativecommons.tankerkoenig.de/#register
 */
public final class Tankerkoenig
{
   private final static String BASE_URL = "https://creativecommons.tankerkoenig.de/json/";
   private final static String DEMO_API_KEY = "00000000-0000-0000-0000-000000000002";

   private Tankerkoenig()
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Builder for an API instance
    */
   public static final class ApiBuilder
   {
      private String apiKey;
      private ClientExecutor clientExecutor;
      private ClientExecutorFactory clientExecutorFactory;
      private final String baseUrl;

      /**
       * Creates a new API builder instances
       */
      public ApiBuilder()
      {
         this.baseUrl = Tankerkoenig.BASE_URL;
         clientExecutorFactory = ClientExecutorFactoryImpl.getInstance();
      }

      ApiBuilder(final String baseUrl)
      {
         this.baseUrl = baseUrl;
         clientExecutorFactory = ClientExecutorFactoryImpl.getInstance();
      }

      ApiBuilder(final ClientExecutorFactory clientExecutorFactory)
      {
         this.baseUrl = Tankerkoenig.BASE_URL;
         this.clientExecutorFactory = clientExecutorFactory;
      }

      /**
       * Sets the API Key to the default key as defined on the
       * official website
       */
      public ApiBuilder withDemoApiKey()
      {
         this.apiKey = DEMO_API_KEY;
         return this;
      }

      /**
       * Sets the personal API key
       */
      public ApiBuilder withApiKey(final String apiKey)
      {
         this.apiKey = apiKey;
         return this;
      }

      /**
       * Uses the default client executor
       */
      public ApiBuilder withDefaultClientExecutor()
      {
         this.clientExecutor = clientExecutorFactory.buildDefaultClientExecutor();
         return this;
      }

      /**
       * Uses the specified client executor
       */
      public ApiBuilder withClientExecutor(final ClientExecutor clientExecutor)
      {
         this.clientExecutor = clientExecutor;
         return this;
      }

      /**
       * Builds the final API instance. If apiKey is null or empty, will throw an {@link IllegalStateException}.
       * <p>
       * If no client executor is explicitly specified, will build the default client executor.
       */
      public Api build()
      {
         if (apiKey == null || apiKey.isEmpty())
         {
            throw new IllegalStateException("The API key has to be neither empty nor null");
         }

         if (clientExecutor == null)
         {
            clientExecutor = clientExecutorFactory.buildDefaultClientExecutor();
         }

         final Requester requester = new Requester(clientExecutor, GsonMapper.getInstance());

         return new Api(this, baseUrl, requester);
      }
   }

   /**
    * The Tankerkoenig API, which will build the requests
    */
   public static final class Api
   {
      private final String apiKey;
      private final String baseUrl;
      private final Requester requester;

      private Api(final ApiBuilder apiBuilder, final String baseUrl, final Requester requester)
      {
         this.apiKey = apiBuilder.apiKey;
         this.baseUrl = baseUrl;
         this.requester = requester;
      }

      /**
       * Builds a station list request. The supplied coordinates define
       * the search center
       *
       * @param lat Must be between -90 and 90
       * @param lng Must be between -180 and 180
       */
      public StationListRequest list(final double lat, final double lng)
      {
         return new StationListRequest(apiKey, baseUrl, requester)
               .setCoordinates(lat, lng);
      }

      /**
       * Builds a station detail request.
       *
       * @param stationId The station ID which is obtainable using the {@link #list(double, double)} request
       */
      public StationDetailRequest detail(final String stationId)
      {
         return new StationDetailRequest(apiKey, baseUrl, requester, stationId);
      }

      /**
       * Builds a prices search request
       */
      public PricesRequest prices()
      {
         return new PricesRequest(apiKey, baseUrl, requester);
      }

      /**
       * Builds a station correction request
       *
       * @param stationId The station ID which is obtainable using the {@link #list(double, double)} request
       * @param type      The correction request type
       */
      public CorrectionRequest correction(final String stationId, final CorrectionRequest.Type type)
      {
         return new CorrectionRequest(apiKey, baseUrl, requester, type)
               .setStationId(stationId);
      }
   }
}
