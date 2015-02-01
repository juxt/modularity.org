A blog site based on [Start Bootstrap's clean-blog](http://startbootstrap.com/template-overviews/clean-blog/) design. I run my own blog on this template, [malcolmsparks.com](http://malcolmsparks.com). The nice thing is that it's very minimal and once you're confident with Clojure you can personalise it however you wish.

Projects generated with this template get the following features 'out of the box'.

* Markdown-formatted posts in the `posts` directory (although this location is configurable), formatted using [endophile](https://github.com/theJohnnyBrown/endophile)
* The option to run the website live, or generate the static content - it's up to you! (except the static content generation bit isn't yet finished)
* A built-in [less](http://lesscss.org/) compiler, giving you the scope to greatly personalise the design
* As with all other modular projects, a highly productive  [resettable](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded) workflow
* With a [component](https://github.com/stuartsierra/component)-based architecture, easy to modify and add/replace functionality without the project turning into a [big ball of mud](http://en.wikipedia.org/wiki/Big_ball_of_mud)
* All the benefits of the Clojure language, such as performance, functional programming, pervasive immutable data structures and more

All this in less than 500 lines of code :)

## Getting started

The lein command (see below for the actual incantation) will create a directory with the name of your project. `cd` into this directory and run `lein run` which will run your blogsite and launch a browser so you can view it.

If you prefer to generate the static content instead, use `lein gen`.

## Add posts

Add posts in the `posts` directory. These should be [Markdown](http://daringfireball.net/projects/markdown/syntax) formatted, but can contain additional metadata lines at the top of the file. For example :-

```
Title: Prefer data over functions
Subtitle: Functional programming is great, but always prefer data!
Date: 2015-01-31
Keywords: clojure
Background: img/redirect.jpg
```

Refer to examples from [my blog sources](https://github.com/malcolmsparks/malcolmsparks.blog/tree/master/posts).

## Developing the site

If you want to hack on the Clojure code to add functionality, feel welcome — that's the really whole point! Instead of running `lein run` you should run `lein repl`, then type `(dev)` to compile and `(go)` to start the components, then make code changes, then hit `(reset)` which will reload your modified code and restart all the components.

Components are defined in the `system.clj` file, at the bottom of the `src/` directory.

## Static resources

Add static resources, such as images, to `resources/public`.

## Less CSS

The Less files are under `resources/less` — if you change them they will be recompiled to CSS, under `target/css` on the next `(reset)`.
