# filloca-sf

generated using Luminus version "2.9.12.78"
lein new luminus filloca-sf +jetty +re-frame +swagger +sassc +kibit

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Run figwheel and compile cljs
From command line:

    lein figwheel app
 
Or from IDE, after starting a local REPL:

    (start-fw)

## Sass
To compile and watch for changes
    
    lein auto sassc once


## Running
To start a web server for the application from the command line, run:

    lein run 

To start a web server for the application from your IDE, start a local REPL then run:

    (start)

## License

Copyright © 2018 FIXME
