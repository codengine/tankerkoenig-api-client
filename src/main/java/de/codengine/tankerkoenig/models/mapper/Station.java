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

import java.util.List;
import java.util.Optional;

import de.codengine.tankerkoenig.models.requests.GasRequestType;
import de.codengine.tankerkoenig.models.requests.StationDetailRequest;
import de.codengine.tankerkoenig.models.requests.StationDetailResult;
import de.codengine.tankerkoenig.models.requests.StationListRequest;

/**
 * Represents the station. The station data is used for multiple
 * results and therefore does not always contain each information / field available.
 * <p>
 * If the station is closed (isOpen = false), no gas prices will be available.
 * <p>
 * Price information are pretty reliable, but most of the String fields are not as they
 * are provided uncontrolled by the gas stations.
 * <p>
 * Therefore, the name and brand data might not be consistent. E.g., "JET" can also be
 * returned as "Jet" or "jet".
 * <p>
 * Fields that are not available all time will be wrapped in {@link Optional}.
 */
public final class Station
{
   private String id;
   private String name;
   private Location location;
   private String brand;
   private boolean isOpen;
   private Float price;
   private GasPrices gasPrices;

   private List<OpeningTime> openingTimes;
   private List<String> overridingOpeningTimes;
   private Boolean wholeDay;

   Station()
   {
   }

   /**
    * Returns the stations name
    */
   public Optional<String> getName()
   {
      if (name == null || name.isEmpty())
      {
         return Optional.empty();
      }

      return Optional.of(name);
   }

   void setName(final String name)
   {
      this.name = name;
   }

   /**
    * Returns the stations unique ID
    */
   public String getId()
   {
      return id;
   }

   void setId(final String id)
   {
      this.id = id;
   }

   /**
    * Returns the stations location data
    */
   public Location getLocation()
   {
      return location;
   }

   void setLocation(final Location location)
   {
      this.location = location;
   }

   /**
    * Returns the stations brand.
    * <p>
    * Sometimes this information is not available, which is mostly the case for gas stations
    * in private ownership.
    */
   public Optional<String> getBrand()
   {
      return Optional.ofNullable(brand);
   }

   void setBrand(final String brand)
   {
      this.brand = brand;
   }

   /**
    * Determines whether the station is open
    */
   public boolean isOpen()
   {
      return isOpen;
   }

   void setOpen(final boolean open)
   {
      isOpen = open;
   }

   /**
    * Returns the stations gas prices.
    * <p>
    * Will only be available with {@link StationListRequest} if {@link GasRequestType} is set to ALL,
    * or {@link StationDetailRequest}
    */
   public Optional<GasPrices> getGasPrices()
   {
      return Optional.ofNullable(gasPrices);
   }

   void setGasPrices(final GasPrices gasPrices)
   {
      this.gasPrices = gasPrices;
   }

   /**
    * Returns an unmodifyable list of opening times for the station.
    * Will contain zero to an unlimited count of entries
    * <p>
    * Will only be available with {@link StationDetailResult}
    */
   public Optional<List<OpeningTime>> getOpeningTimes()
   {
      if (openingTimes == null || openingTimes.isEmpty())
      {
         return Optional.empty();
      }
      return Optional.of(openingTimes);
   }

   void setOpeningTimes(final List<OpeningTime> openingTimes)
   {
      this.openingTimes = openingTimes;
   }

   /**
    * Returns an unmodifyable list of overriding opening times for the station.
    * Those are arbitrary strings without any specific structure which define
    * temporary changes of opening times
    * <p>
    * Will only be available with {@link StationDetailResult}
    */
   public Optional<List<String>> getOverridingOpeningTimes()
   {
      if (overridingOpeningTimes == null || overridingOpeningTimes.isEmpty())
      {
         return Optional.empty();
      }
      return Optional.of(overridingOpeningTimes);
   }

   void setOverridingOpeningTimes(final List<String> overridingOpeningTimes)
   {
      this.overridingOpeningTimes = overridingOpeningTimes;
   }

   /**
    * Defines if the station is opened the whole day.
    * Not Present is equivalent to "false"
    */
   public Optional<Boolean> isWholeDay()
   {
      return Optional.ofNullable(wholeDay);
   }

   void setWholeDay(final Boolean wholeDay)
   {
      this.wholeDay = wholeDay;
   }

   /**
    * Returns the gas price for the requested {@link GasRequestType}.
    * <p>
    * Will only be available with {@link StationListRequest} if {@link GasRequestType} is set to E5, E10 or DIESEL
    */
   public Optional<Float> getPrice()
   {
      return Optional.ofNullable(price);
   }

   void setPrice(final Float price)
   {
      this.price = price;
   }

   @Override
   public boolean equals(final Object o)
   {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;

      final Station station = (Station) o;

      return getId() != null ? getId().equals(station.getId()) : station.getId() == null;
   }

   @Override
   public int hashCode()
   {
      return getId() != null ? getId().hashCode() : 0;
   }
}
