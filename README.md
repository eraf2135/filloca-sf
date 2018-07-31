# filloca-sf

Generated using Luminus version "2.9.12.78" `lein new luminus filloca-sf +jetty +re-frame +swagger +sassc +kibit`

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Run figwheel and compile cljs
From command line:

    lein figwheel app
 
Or from the REPL:

    (start-fw)

## Sass
To compile and watch for changes
    
    lein auto sassc once

## Config
Add a `dev-config.edn` in the project root and include a mapbox api key:

    {:dev true
     :port 3000
     :nrepl-port 7000
     :mapbox-api-key "changeme"}

## Running
To start a web server for the application from the command line, run:

    lein run 

Or from the REPL:

    (start)
    
Now you can access the site at `http://localhost:3000`

## Tests
Clojure:

    lein test
    
Clojurescript:

    lein with-profile test doo phantom

## Swagger

    http://localhost:3000/swagger-ui

## Debugging
Re-frame 10x is installed so you can inspect the app-state and lifecycle events.
From any of the re-frame/react pages press CTRL+H to open the debugger.

## Prod Build

    lein uberjar  
    
Can run with:

    java -jar ./target/uberjar/filloca-sf.jar
    
