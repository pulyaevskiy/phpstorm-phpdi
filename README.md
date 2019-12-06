# PHP-DI plugin for PhpStorm
[![Version](http://phpstorm.espend.de/badge/7694/version)](https://plugins.jetbrains.com/plugin/7694)
[![Downloads](http://phpstorm.espend.de/badge/7694/downloads)](https://plugins.jetbrains.com/plugin/7694)
[![Downloads last month](http://phpstorm.espend.de/badge/7694/last-month)](https://plugins.jetbrains.com/plugin/7694)

Plugin url : https://plugins.jetbrains.com/plugin/7694

**Suggestion for users who seeking solution for this plugin working on latest PhpStorm.**

As PhpStorm now support this feature in a native way, the plugin is not needed any more. Indeed the plugin won't work on PhpStorm 2019.3 and later. The solution is from [http://php-di.org/doc/ide-integration.html#phpstorm-integration](http://php-di.org/doc/ide-integration.html#phpstorm-integration).

Take my case for example, my container is `Comm_Factory` and there are two static functions `get()` and `make()` in it, so the meta file should look like this

```php
// .phpstorm.meta.php
/**
 * PhpStorm code completion
 *
 * Add code completion for PSR-11 Container Interface and more...
 */

namespace PHPSTORM_META {
    // Old Interop\Container\ContainerInterface
    override(\Comm_Factory::get(0),
        map([
            '' => '@',
        ])
    );

    // PSR-11 Container Interface
    override(\Comm_Factory::make(0),
        map([
            '' => '@',
        ])
    );
}
```

**Below is the original readme.**



This is very simple plugin which only implements type provider for services returned from PHP-DI container.

Strictly speaking current implementation is not dependent on PHP-DI in any way. It just supports certain way of receiving services from container which is widely used in PHP-DI:

```php
<?php
$postRepository = $container->get(PostRepository::class);
```

So the only thing this plugin does is:

1. It looks for all `get` (or `make`, since `v1.2.0`) method calls.
2. It filters out all except those where first argument contains `::class` substring.
3. It extracts class FQN from the first argument and provides it as a return type for that specific method call.

Result:

![Example screenshot](example.png?raw=true "Example screenshot")

#### Contributors:

* [Logan Attwood](https://github.com/lattwood)

License: MIT
