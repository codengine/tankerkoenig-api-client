Tankerkoenig API
================

[![Build Status](https://travis-ci.org/codengine/tankerkoenig-api.svg?branch=master)](https://travis-ci.org/codengine/tankerkoenig-api)

An API client for calling the [Tankerkoenig API][1] (Official Website: http://www.tankerkoenig.de).

For more information see [the wiki][wiki].

Download
========

Via Maven:

```
<dependency>
    <groupId>de.codengine</groupId>
    <artifactId>tankerkoenig-api-client</artifactId>
    <version>1.0</version>
</dependency>
```

Snapshots of the development version are available in [Sonatype's ``snapshots`` repository][sonasnaps].

Obtaining an API Key
--------------------

In order to use the API client, you first have to obtain a personal API key.

Enter the [API Registration Page][register], fill out the form and confirm the email which is sent to you afterwards.

Be careful not to make your private API key publicly available! 

Terms of Usage
--------------

The [Terms of Usage][terms] of the API provider must be read and adhered to as defined on their website. The client does not provide any throtteling or control, so be careful about request limits which will result in a 503 Internal Server Error!

License
-------
    MIT License
    
    Copyright (c) 2017 Stefan Hueg (Codengine)
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.

[1]: https://creativecommons.tankerkoenig.de
[register]: https://creativecommons.tankerkoenig.de/#register
[wiki]: https://github.com/codengine/tankerkoenig-api-client/wiki
[terms]: https://creativecommons.tankerkoenig.de/#usage
[sonasnaps]: https://oss.sonatype.org/content/repositories/snapshots/de/codengine/tankerkoenig-api-client/