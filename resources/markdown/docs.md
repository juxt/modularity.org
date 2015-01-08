Modular is free software, licensed until the MIT License, with full sources available on [GitHub](https://github.com/juxt/modular).

## Templates

Templates generate working modular applications that give you a complete
Clojure-based project on which to build upon. Unlike many templating
systems which generate code that is hard to change, these templates
leave you with a simple open architecture which supports change and
growth.

All templates incorporate the ideas and techniques described on this
website. Which template you should use depends on what you want to
do. Browse the documentation for the templates below and decide the best
one for you to try first.

Each project generated from a modular template contains the following features _as standard_.

* A repl-driven 'reloaded' development work-flow, [popularised by Stuart Sierra](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded).

* A ```system.clj``` namespace where components are constructed and
their dependency relationships declared.

* A simple test suite

* A ```main.clj``` namespace, allowing the project to be deployed and run (via [Leiningen](http://leiningen.org)).

* Instructions for how to develop and run the project, generated into the project's ```README.md```.

Information on how to use each template is contained in its
documentation.

### Developer information

Templates are driven from data held in a
[manifest file](https://raw.githubusercontent.com/juxt/modular/master/lein-template/resources/manifest.edn)
within modular's lein-template resources directory. If you'd like to add
your own template, feel free to send a [pull request](https://github.com/juxt/modular/pulls).

### Available templates
