<!--
  ~ Copyright 2017 Stormpath, Inc.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  -->
# Java Token Authentication Made Easy

Juiser (pronounced "juicer", a [portmanteau](https://en.wikipedia.org/wiki/Portmanteau) of "java", "juice" and "user") is a small Java library that automates 
token authentication during an HTTP request.  After authentication, a simple but clean `User` object will be 
available to your code to support user-specific logic and authorization decisions.

Currently Juiser supports the `X-Forwarded-User` header with either JSON or [JWS](https://tools.ietf.org/html/rfc7515)
values.  Juiser works in both standard Servlet applications as well as Spring/Boot applications.

## Security Frameworks

Juiser has one purpose and one purpose only - to translate an authentication token
into an intuitive `User` object and make that `User` object available during a request.

You can use the `User` object during a request to perform security checks 
(like `user.isAuthenticated()` or `user.hasRole("admin")`) and get information about the user (e.g. `user.getName()`), etc, and
this may be perfectly suitable for many applications.

However if you have any need of more powerful security controls, we recommend that you use Apache Shiro or Spring
Security and configure these frameworks to delegate to Juiser as necessary so you have one cohesive security
programming experience.

### Spring Security

Juiser already has native support for Spring Security so you can still leverage all the security controls
Spring Security provides.  You don't even need to use Juiser's `User` api at all if you don't want to - just use standard
Spring Security classes like `UserDetails` and you'll never know Juiser is 'in the mix' at all.

That said, even with this Spring Security support, we think you'll find that our `User` API is a really nice 
enhancement on top of Spring Security and you'll probably want to use it in addition to everything else Spring Security
provides.

### Apache Shiro

We don't yet have native support for Apache Shiro, but we'll integrate as soon as we can.  Any community help in
this area would be appreciated!  Please open a GitHub issue or Pull Request to initiate discussion and we'll be happy
to help guide you!
