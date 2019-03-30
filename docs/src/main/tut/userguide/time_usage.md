---
layout: docs
title: "Working with Time"
section: "userguide"
---

There are two different things that we can do with it: Using it as a matcher against _DateTime_ objects
and obtaining the next or previous _DateTime_ to a given one according to the parsed expression.

There are also many flavours of _DateTime_ representations in either the JVM or in JS land. **cron4s**
supports a few out of the box and it also makes it easy to add your own types. Every single
supported library out of box, or _adapted_ to work with **cron4s** will support the same
operations with the same semantics.

**cron4s** uses implicit classes to enrich the base expression types with the methods that operate
on specific _DateTime_ representations. There are specific packages containing the enrichments for
each supported _DateTime_ type, so be sure that you have always the correct imports for your _DateTime_
library in your sources.

Now let's take a look at what are the main operations:

-   [Matching Date & Time](time_usage/matching.html)
-   [Forwards & Backwards In Time](time_usage/forward_backwards.html)