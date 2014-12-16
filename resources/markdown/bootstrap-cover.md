Bootstrap cover is adapted from [Twitter Bootstrap's 'cover' example](http://getbootstrap.com/examples/cover/). It introduces the Mustache template renderer, provided by [Clostache](https://github.com/fhd/clostache).

It also shows how to provide static resources from [JQuery](https://jquery.com/) and [Bootstrap](https://getbootstrap.com/) by contributing routes to the router, rather than requiring code modifications.

The template also introduces the concept of _co-dependencies_. This is
can be seen in the arguments to the ```Website``` record in
```website.clj```, which include a reference to the router. The
component uses this router to construct the URLs to other
handlers. While these handlers are also defined within the ```Website```
component, routes can be constructed in this way to any known handler in
the system, using its keyword and any required arguments. See
[bidi](https://github.com/juxt/bidi) for more details.
