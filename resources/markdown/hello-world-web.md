A simple website that displays "Hello World!".

This introduces the ```modular.ring/WebRequestHandler``` protocol which provides an integration surface between a Ring-compatible web server (e.g. Jetty, http-kit) and a Ring handler.

In most applications, the choice of web server is made ahead of time and some code is written to provide the web server with the handler to call. In this example, the relationship between the web server and Ring handler is made via a dependency declaration between the web server (in this case, [http-kit](http://www.http-kit.org/)) and the handler.

In the ```system.clj``` we see two components: ```:http-listener-listener``` and ```:hello-world-website-handler```. The first component declares a dependency on a request handler, with the ```using``` clause. The ```new-dependency-map``` function returns a map that satisfies the web server comonent's ```:request-handler``` dependency with our application component ```hello-world-web.hello-world-website/HelloWorldHandler```. This component must satisfy the ```modular.ring/WebRequestHandler``` protocol. This pattern of using protocols to provide the integration surface between components is used throughout [modular](https://github.com/juxt/modular).
