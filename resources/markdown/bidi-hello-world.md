Another simple website that displays "Hello World!".

This introduces a router component which dispatches incoming HTTP
requests to one of its dependant components providing routes.

This demonstrates one of the principles of modularity. We avoid a single data structure comprising all the HTTP routes in a system. Rather, we allow individual modules to make contributions to this route structure.

[bidi](https://github.com/juxt/bidi) is used in this example, but the
principle would be the same using Compojure routes, which supports
similar composeable mechanisms.
