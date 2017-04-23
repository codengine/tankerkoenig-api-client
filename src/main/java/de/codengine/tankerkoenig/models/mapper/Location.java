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

package de.codengine.tankerkoenig.models.mapper;

import java.util.Optional;

import com.google.gson.annotations.SerializedName;

import de.codengine.tankerkoenig.models.requests.StationDetailResult;
import de.codengine.tankerkoenig.models.requests.StationListRequest;

/**
 * Represents the location of a {@link Station}. The location data is used for multiple
 * results and therefore does not always contain each information / field available.
 * <p>
 * Location information are not 100% reliable as they are directly provided by the gas station
 * owners.
 * <p>
 * Fields that are not available all time will be wrapped in {@link Optional}.
 */
public final class Location
{
   private double lat;
   private double lng;
   @SerializedName("dist")
   private Double distance;
   @SerializedName("street")
   private String streetName;
   private String houseNumber;
   @SerializedName("postCode")
   private Integer zipCode;
   @SerializedName("place")
   private String city;
   private State state;

   Location()
   {
   }

   /**
    * Returns the latitude of the {@link Station}
    */
   public double getLat()
   {
      return lat;
   }

   void setLat(final double lat)
   {
      this.lat = lat;
   }

   /**
    * Returns the longitude of the {@link Station}
    */
   public double getLng()
   {
      return lng;
   }

   void setLng(final double lng)
   {
      this.lng = lng;
   }

   /**
    * Returns the distance to the position which is passed to the {@link StationListRequest}.
    * <p>
    * Unavailable for {@link StationDetailResult}
    */
   public Optional<Double> getDistance()
   {
      return Optional.ofNullable(distance);
   }

   void setDistance(final Double distance)
   {
      this.distance = distance;
   }

   /**
    * Returns the street name of the {@link Station}
    */
   public String getStreetName()
   {
      return streetName;
   }

   void setStreetName(final String streetName)
   {
      this.streetName = streetName;
   }

   /**
    * Returns the house number of the {@link Station}.
    * <p>
    * Sometimes the station owners will put the house number inside the street name, so this information
    * is not always available.
    */
   public Optional<String> getHouseNumber()
   {
      if (houseNumber == null || houseNumber.isEmpty())
      {
         return Optional.empty();
      }
      return Optional.of(houseNumber);
   }

   void setHouseNumber(final String houseNumber)
   {
      this.houseNumber = houseNumber;
   }

   /**
    * Returns the zip code of the {@link Station}
    */
   public Integer getZipCode()
   {
      return zipCode;
   }

   void setZipCode(final Integer zipCode)
   {
      this.zipCode = zipCode;
   }

   /**
    * Returns the city of the {@link Station}
    */
   public String getCity()
   {
      return city;
   }

   void setCity(final String city)
   {
      this.city = city;
   }

   /**
    * Returns the state of the {@link Station}.
    * <p>
    * This information is not available most of the time, therefore it is mark as optional.
    * <p>
    * For mapping purpose, see {@link State}
    */
   public Optional<State> getState()
   {
      return Optional.ofNullable(state);
   }

   void setState(final State state)
   {
      this.state = state;
   }
}
