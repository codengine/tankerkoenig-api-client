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

import java.util.Optional;

/**
 * Almost all results fill most of the fields, except for {@link CorrectionRequest},
 * whereas only isOk will be set.
 * <p>
 * In case of an error, {@link #isOk()} will return false, {@link ResponseStatus} will be set to ERROR and message will contain
 * more information about what error occured.
 */
abstract class BaseResult implements Result
{
   private ResponseStatus status;
   private String message;
   private String license;
   private String data;
   private Boolean ok;

   /**
    * Returns whether the request was successful
    * <p>
    * Won't be filled for requests by {@link CorrectionRequest}
    */
   @Override
   public Optional<ResponseStatus> getStatus()
   {
      return Optional.ofNullable(status);
   }

   /**
    * In case of an error, will return the error message
    */
   @Override
   public Optional<String> getMessage()
   {
      return Optional.ofNullable(message);
   }

   /**
    * Will return the APIs license information.
    * <p>
    * Won't be filled for requests by {@link CorrectionRequest}
    */
   @Override
   public Optional<String> getLicense()
   {
      return Optional.ofNullable(license);
   }

   /**
    * Returns the original data supplier for gas prices
    * <p>
    * Won't be filled for requests by {@link CorrectionRequest}
    */
   @Override
   public Optional<String> getData()
   {
      return Optional.ofNullable(data);
   }

   /**
    * Returns whether the request was successful
    */
   @Override
   public Boolean isOk()
   {
      return ok;
   }
}
