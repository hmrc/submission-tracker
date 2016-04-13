
# submission-tracker

[![Build Status](https://travis-ci.org/hmrc/submission-tracker.svg?branch=master)](https://travis-ci.org/hmrc/submission-tracker) [ ![Download](https://api.bintray.com/packages/hmrc/releases/submission-tracker/images/download.svg) ](https://bintray.com/hmrc/releases/submission-tracker/_latestVersion)

Retrieve tracking data based on the supplied tax identifier.

Requirements
------------

The following services are exposed from the micro-service.

API
---

| *Task* | *Supported Methods* | *Description* |
|--------|----|----|
| ```/tracking``` | GET | Ping check - Returns 200 if the service is running. |
| ```/tracking/:id/:idType``` | GET | Retrieve the tracking data for the 'id' and 'idType'. The Id is the tax identifier and the 'idType' is the type of tax identifier. [More...](docs/tracking.md)|


# Sandbox
All the above endpoints are accessible on sandbox with `/sandbox` prefix on each endpoint,e.g.
```
    GET /sandbox/tracking/:id/:idType
```

# Definition
API definition for the service will be available under `/api/definition` endpoint.
See definition in `/conf/api-definition.json` for the format.

# Version
Version of API need to be provided in `Accept` request header
```
Accept: application/vnd.hmrc.v1.0+json
```
### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
    