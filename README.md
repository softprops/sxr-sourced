# Sxr Sourced

Serving Scala for the _good_ of mankind

A Simple server for Sxr Docs

(UNDER DEVELOPMENT)

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
    prepare-webapp
    
### Starting and Stopping

Within sbt

start with

    dev-appserver-start

stop with

    dev-appserver-stop

### TODO 

* Generate app engine template file via sbt action
* Stores
* Deploy Steps

### See Also
* [sxr-publish](http://github.com/n8han/sxr-publish)
* [browse](http://github.com/harrah/browse)
* [sbt-appengine](http://github.com/Yasushi/sbt-appengine-plugin)

Doug Tangren 2010 (softprops)