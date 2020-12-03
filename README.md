
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
| ```/tracking/:id/:idType``` | GET | Retrieve the tracking data for the 'id' and 'idType'. The Id is the tax identifier and the 'idType' is the type of tax identifier. [More...](docs/tracking.md)|


# Sandbox
All the above endpoints are accessible on sandbox with `/sandbox` prefix on each endpoint,e.g.
```
    GET /sandbox/tracking/:id/:idType
```

To trigger the sandbox endpoints locally, use the "X-MOBILE-USER-ID" header with one of the following values:
208606423740 or 167927702220

To test different scenarios, add a header "SANDBOX-CONTROL" with one of the following values:

| *Value* | *Description* |
|--------|----|
| "NO-FORMS" | Happy path with no form data |
| "ERROR-401" | Unhappy path, trigger a 401 Unauthorized response |
| "ERROR-403" | Unhappy path, trigger a 403 Forbidden response |
| "ERROR-500" | Unhappy path, trigger a 500 Internal Server Error response |

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

