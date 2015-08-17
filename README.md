gocd-github-tasks
=================

![GitHub Logo](https://travis-ci.org/omigos/gocd-github-tasks.svg)

This is a plugin for Thoughtworks GO to provide easy integration with github.

status
======

WIP.
Currently only release-drafts are supported.
The plugin will provide a "Github Release" task that will take an github oauth token, a repository name and the 
GO environment variable GIT_REVISION to create a new release draft. The prerelease checkbox currently doesn't work and 
makes every release a prerelease, but that doesn't matter that much because every release is a draft anyways.

roadmap
=======

* attach artifacts to a release (probably by filename)
* update commit status (the small icons besides a commit in a ticket, like travis-ci does)
* update releases (change name, make pre-release to release, publish draft, add comment like "deployed to...", etc.)
* maybe some integration with the github deployment status api
* further things to come...

build
=====

```bash
mvn install:install-file -DgroupId=com.thoughtworks.go -DartifactId=go-plugin-api -Dversion=14.1.0 -Dpacking=jar -Dfile=lib/go-plugin-api-14.2.0.jar
mvn install
```
