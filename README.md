# Sxr Sourced

Serving Scala for the _good_ of mankind

A simple server for [Sxr](http://github.com/harrah/browse) Docs

### Install

This project is using the sbt appengine plugin which appears to be unpublished, until it does, do the following

    git clone git://github.com/Yasushi/sbt-appengine-plugin.git
    cd sbt-appengine-plugin
    sbt
    update
    publish-local
    
Clone this project

    git clone git://github.com/softprops/sxr-sourced.git
    cd sxr-sourced
    sbt
    update
    
### Starting and Stopping

Within sbt

start server with

    dev-appserver-start

stop server with

    dev-appserver-stop

### See Also
* [sxr-publish](http://github.com/n8han/sxr-publish)
* [browse](http://github.com/harrah/browse)
* [sbt-appengine](http://github.com/Yasushi/sbt-appengine-plugin)

Doug Tangren 2010 (softprops)